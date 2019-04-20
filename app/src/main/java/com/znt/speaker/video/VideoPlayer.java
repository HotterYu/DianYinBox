package com.znt.speaker.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.lib.bean.MediaInfor;
import com.znt.lib.utils.FileUtils;
import com.znt.lib.utils.SystemUtils;
import com.znt.lib.utils.ViewUtils;
import com.znt.push.httpmodel.HttpAPI;
import com.znt.speaker.R;
import com.znt.speaker.timer.CheckDelayTimer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import service.ZNTDownloadServiceManager;
import service.ZNTPushServiceManager;


public class VideoPlayer extends RelativeLayout
{
    private static final String TAG = "VideoPlayer";
    @BindView(R.id.video_view)
    public TextureVideoPlayer videoView;
    @BindView(R.id.tv_device_info)
    public TextView tvDevInfor;
    @BindView(R.id.mediaController)
    public VideoMediaController mediaController;

    private CheckDelayTimer mCheckDelayTimer = null;

    public MediaPlayer mPlayer;
    private Surface mSurface;

    private ZNTPushServiceManager mZNTPushServiceManager;
    private ZNTDownloadServiceManager mZNTDownloadServiceManager;

    private int lastPosition = 0;
    public boolean hasPlay;//是否播放了
    private MediaInfor curPlayingMedia = null;
    private MediaInfor pauseMediaInfor = null;


    private OnMediaPlayListsner mOnMediaPlayListsner = null;
    public interface OnMediaPlayListsner
    {
        public void onMediaPlay(MediaInfor mediaInfor);
    }

    private final int MSG_DEVICE_INFO_UPDATE = 100;
    private final int MSG_PLAY_DELAY_CHECK = 101;
    private final int MSG_ON_MEDIA_PLAY_START = 102;
    private final int MSG_ON_MEDIA_PLAY_FAIL = 103;
    private final int MSG_ON_MEDIA_PLAY_FINISH = 104;
    private final int MSG_ON_MEDIA_PLAY_RUNNING = 105;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_DEVICE_INFO_UPDATE)
            {
                String info = String.valueOf(msg.obj);
                if(HttpAPI.SERVER_ADDRESS.contains("zhunit.com"))
                    info = info + "\n  \n" + SystemUtils.getVersionName(getContext()) + "   " + getResources().getString(R.string.version_release);
                else
                    info = info + "\n  \n" + SystemUtils.getVersionName(getContext()) + "   " + getResources().getString(R.string.version_debug);
                tvDevInfor.setText(info);
            }
            else if(msg.what == MSG_PLAY_DELAY_CHECK)
            {
                checkDelay();
            }
            else if(msg.what == MSG_ON_MEDIA_PLAY_START)
            {
                MediaInfor mediaInfor = (MediaInfor) msg.obj;
                String mediaName = mediaInfor.getMediaName();
                String urlPlay = mediaInfor.getMediaUrl();
                mediaController.showPlayLoadingView(true);
                mediaController.setPbLoadingVisiable(View.VISIBLE);
                mediaController.showPlayStateView(false);
                mediaController.setTitle(mediaName);

                if(FileUtils.isVideo(urlPlay))
                {
                    mediaController.showCurTime(View.GONE);
                    mediaController.showPlayControl(View.VISIBLE);
                    tvDevInfor.setVisibility(View.GONE);
                    getTextureView().setVisibility(View.VISIBLE);
                }
                else
                {
                    mediaController.showCurTime(View.VISIBLE);
                    tvDevInfor.setVisibility(View.VISIBLE);
                    mediaController.showPlayControl(View.VISIBLE);
                    getTextureView().setVisibility(View.GONE);
                }
            }
            else if(msg.what == MSG_ON_MEDIA_PLAY_FAIL)
            {
                mediaController.setPbLoadingVisiable(View.GONE);
                mediaController.showPlayLoadingView(false);
                Toast.makeText(getContext(),getResources().getString(R.string.load_media_fail),Toast.LENGTH_SHORT).show();
                updatePushParams(getResources().getString(R.string.media_load_fail),true);
            }
            else if(msg.what == MSG_ON_MEDIA_PLAY_FINISH)
            {
                mediaController.showPlayLoadingView(false);
                //隐藏视频加载进度条
                mediaController.setPbLoadingVisiable(View.GONE);
                //更新播放按钮状态
                mediaController.showPlayStateView(true);
                //隐藏标题
                mediaController.delayHideTitle();
                //设置视频的总时长
                mediaController.setDuration(mPlayer.getDuration());
                //更新播放的时间和进度
                mediaController.updatePlayTimeAndProgress();
            }
            else if(msg.what == MSG_ON_MEDIA_PLAY_RUNNING)
            {
                int percent = (int) msg.obj;
                mediaController.updateSeekBarSecondProgress(percent);
            }
        }
    };

    public VideoPlayer(Context context) {
        this(context, null);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    //初始化布局
    private void initView()
    {

        View view = View.inflate(getContext(), R.layout.video_play, this);
        ButterKnife.bind(this,view);

        initViewDisplay();
        //把VideoPlayer对象传递给VideoMediaController
        mediaController.setVideoPlayer(this);

        //进行TextureView控件创建的监听
        videoView.setSurfaceTextureListener(surfaceTextureListener);

        if(mPlayer == null)
        {
            mPlayer = MediaHelper.getInstance();
            mPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener()
            {
                @Override
                public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1)
                {
                    videoView.updateTextureViewSizeCenter(i,i1);
                }
            });
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            //设置监听
            mPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mPlayer.setOnCompletionListener(onCompletionListener);
            mPlayer.setOnErrorListener(onErrorListener);
            mPlayer.setOnPreparedListener(onPreparedListener);
            mPlayer.setScreenOnWhilePlaying(true);//在视频播放的时候保持屏幕的高亮
        }

        mCheckDelayTimer = new CheckDelayTimer(getContext());
        mCheckDelayTimer.setHandler(mHandler,MSG_PLAY_DELAY_CHECK);
        mCheckDelayTimer.setTimeInterval(20*1000);
        mCheckDelayTimer.startTimer();
    }

    public void setOnMediaPlayListsner(OnMediaPlayListsner mOnMediaPlayListsner)
    {
        this.mOnMediaPlayListsner = mOnMediaPlayListsner;
    }

    public TextureVideoPlayer getTextureView()
    {
        return videoView;
    }

    public void showCurTime(long time)
    {
        mediaController.showCurTime(time);
    }

    public void showMediaController(boolean show)
    {
        if(show)
            mediaController.setVisibility(View.VISIBLE);
        else
        mediaController.setVisibility(View.GONE);
    }

    public long getCurServerTime()
    {
        return mediaController.getCurServerTime();
    }

    public void showDevInfor(String info)
    {
        ViewUtils.sendMessage(mHandler,MSG_DEVICE_INFO_UPDATE,info);
    }

    public void setPushService(ZNTPushServiceManager mZNTPushServiceManager)
    {
        this.mZNTPushServiceManager = mZNTPushServiceManager;
    }

    public void setDownloadServiceManager(ZNTDownloadServiceManager mZNTDownloadServiceManager)
    {
        this.mZNTDownloadServiceManager = mZNTDownloadServiceManager;
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {

        //创建完成  TextureView才可以进行视频画面的显示
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
           // Log.i(TAG,"onSurfaceTextureAvailable");
            mSurface = new Surface(surface);//连接对象（MediaPlayer和TextureView）
            //让MediaPlayer和TextureView进行视频画面的结合
            mPlayer.setSurface(mSurface);
            //play(mCurPlayMediaManager.getCurPlayMedia().getMediaUrl());
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.i(TAG,"onSurfaceTextureSizeChanged");
            //videoView.updateTextureViewSizeCenter(width,height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.i(TAG,"onSurfaceTextureDestroyed");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            Log.i(TAG,"onSurfaceTextureUpdated");
        }
    };

    public void updatePushParams(String playingSong, boolean updateNow)
    {
        if(curPlayingMedia == null)
            return;
        if(playingSong == null)
            playingSong = curPlayingMedia.getMediaName();
        mZNTPushServiceManager.putRequestParams(curPlayingMedia, updateNow);
    }

    public boolean isPlaying()
    {
        if(mPlayer == null)
            return false;
        return mPlayer.isPlaying();
    }

    private void play(MediaInfor mediaInfor)
    {
        try
        {
            this.curPlayingMedia = mediaInfor;

            updatePushParams(getResources().getString(R.string.media_loading),true);

            String urlPlay = getMusicPlayUrL(mediaInfor);

            //Toast.makeText(getContext(),curPlayingMedia.getMediaName(),Toast.LENGTH_LONG).show();

            mPlayer.reset();

            mPlayer.setDataSource(urlPlay);
            if(mOnMediaPlayListsner != null)
                mOnMediaPlayListsner.onMediaPlay(this.curPlayingMedia);
            //异步准备
            mPlayer.prepareAsync();

            ViewUtils.sendMessage(mHandler,MSG_ON_MEDIA_PLAY_START,mediaInfor);

        }
        catch (Exception e)
        {
            ViewUtils.sendMessage(mHandler,MSG_ON_MEDIA_PLAY_FAIL,null);
            playNormalMedia();
            e.printStackTrace();
        }
    }

    private String getMusicPlayUrL(MediaInfor songInfor)
    {
        String url = songInfor.getMediaUrl();

        if(url.startsWith("http://") || url.startsWith("https://"))
        {
            String fileName = "";
            if(url.contains("."))
                fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
            File file = new File(filePath);
            if(file.exists())
            {
                return filePath;
            }
        }
        return songInfor.getMediaUrl();
    }


    //准备完成监听
    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener()
    {
        @Override
        public void onPrepared(MediaPlayer mp)
        {
            if(curPlayingMedia != null)
                mZNTDownloadServiceManager.addSonginfor(curPlayingMedia);
            if(curPlayingMedia != null)
            {

                updatePushParams(curPlayingMedia.getMediaName(),true);
            }
            //进行视频的播放
            MediaHelper.play();
            hasPlay = true;

            if(curPlayingMedia != null && curPlayingMedia.getCurSeek() > 0)
                MediaHelper.seek(curPlayingMedia.getCurSeek());

            ViewUtils.sendMessage(mHandler,MSG_ON_MEDIA_PLAY_FINISH,null);
        }

    };

    public void stopPlayByForce()
    {

        stopPlay();
    }

    public void stopPlay()
    {
        if( mPlayer != null)
        {
            mPlayer.stop();
            mPlayer.reset();
        }
    }

    //错误监听
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener()
    {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra)
        {
            ViewUtils.sendMessage(mHandler,MSG_ON_MEDIA_PLAY_FAIL,null);
            playNormalMedia();
            return true;
        }
    };

    //完成监听
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener()
    {
        @Override
        public void onCompletion(MediaPlayer mp)
        {
            playNormalMedia();
        }
    };

    //缓冲的监听
    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener()
    {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
//            Log.i(TAG,"percent:"+percent);
            ViewUtils.sendMessage(mHandler,MSG_ON_MEDIA_PLAY_RUNNING,percent);
        }
    };


    //初始化控件的显示状态
    public void initViewDisplay()
    {
        videoView.setVisibility(View.VISIBLE);
        videoView.setLayerType(TextureView.LAYER_TYPE_HARDWARE, null);

        mediaController.initViewDisplay();
    }

    //设置视频播放界面的显示
    public void setVideoViewVisiable(int visible)
    {
        videoView.setVisibility(View.VISIBLE);
    }

    /**
     * 获取当前正在播放的文件
     * @return
     */
    public MediaInfor getCurPlayMedia()
    {
        return curPlayingMedia;
    }

    /*public void startPlayPushMedia()
    {
        addPushMedias(mZNTPushServiceManager.getCurAdList());
        startPlayMedia();
    }*/

    public void playNormalMedia()
    {
        MediaInfor tempMedia = null;

        if(pushMedias.size() == 0)
        {
            //正常播放计划的文件
            if(pauseMediaInfor != null)
            {
                //重新开始播放上一次暂停的播放
                tempMedia = pauseMediaInfor;
                pauseMediaInfor = null;
                Log.e(TAG, "startPlayMedia: start play last media!");
            }
            else
                tempMedia = mZNTPushServiceManager.getCurPlayMedia();
        }
        else//push的文件
            tempMedia = pushMedias.remove(0);

        if(tempMedia == null)
        {
            Log.e(TAG, "startPlayMedia: curPlayMedia == null");
            return;
        }

        if(curPlayingMedia == null || mPlayer == null || !mPlayer.isPlaying())
        {
            play(tempMedia);
        }
    }

    public void playPushMedia(MediaInfor mediaInfor)
    {
        //点播歌曲之前先保存当前正在播放的歌曲
        if(mPlayer != null && mPlayer.isPlaying())
        {
            if(curPlayingMedia != null && curPlayingMedia.isPlayMeida())//当前正在播放的是正常的播放计划
            {
                if(mPlayer.getCurrentPosition() > 0)
                    curPlayingMedia.setCurSeek(mPlayer.getCurrentPosition());
                setCurPauseMedia(curPlayingMedia);
            }
        }
        play(mediaInfor);
    }

    //推送的缓存列表
    private List<MediaInfor> pushMedias = new ArrayList<>();
    public void addPushMedia(MediaInfor mediaInfor)
    {
        this.pushMedias.add(mediaInfor);
    }
    public void addPushMedias(List<MediaInfor> pushMedias)
    {
        this.pushMedias.addAll(pushMedias);
        if(pushMedias.size()> 0)
            playPushMedia(pushMedias.remove(0));
    }
    public void setCurPauseMedia(MediaInfor pauseMediaInfor)
    {
        this.pauseMediaInfor = pauseMediaInfor;
    }

    private boolean isFirstCheckDelay = true;
    public void checkDelay()
    {
        if(isFirstCheckDelay)
        {
            isFirstCheckDelay = false;
            return;
        }
        if(mPlayer == null && !mPlayer.isPlaying())
            return;

        int pos = mPlayer.getCurrentPosition();
        boolean ret = isDelay(pos);
        if (ret)
        {
            playNormalMedia();
        }
        setPos(pos);
    }

    private int lastPos = 0;
    public void setPos(int pos)
    {
        lastPos = pos;
    }

    public boolean isDelay(int pos)
    {
        if (pos != lastPos)
        {
            return false;
        }
/*		if (pos == 0 || pos != lastPos)
		{
			return false;
		}
*/
        return true;
    }

}
