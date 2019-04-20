package com.znt.speaker.factory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jungle.mediaplayer.base.BaseMediaPlayerListener;
import com.jungle.mediaplayer.base.VideoInfo;
import com.jungle.mediaplayer.widgets.JungleMediaPlayer;
import com.znt.lib.bean.MediaInfor;
import com.znt.lib.utils.CacheDirUtils;
import com.znt.push.entity.Constant;
import com.znt.push.entity.LocalDataEntity;
import com.znt.speaker.R;
import com.znt.speaker.timer.CheckDelayTimer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import service.ZNTDownloadServiceManager;
import service.ZNTPushServiceManager;

/**
 * Created by prize on 2018/12/12.
 */

public class HardWarePlayFactory
{

    private Activity mContext = null;

    private JungleMediaPlayer mVideoPlayer = null;

    private ZNTDownloadServiceManager mZNTDownloadServiceManager = null;
    private ZNTPushServiceManager mZNTPushServiceManager;

    private List<MediaInfor> curPlayList = new ArrayList<>();
    private List<MediaInfor> curPushList = new ArrayList<>();

    private MediaInfor curPauseMedia = null;
    private MediaInfor curPlayMedia = null;

    private CheckDelayTimer mCheckDelayTimer = null;

    private boolean isOnLine = true;

    private final int MSG_PLAY_DELAY_CHECK = 0;
    private int curPlayIndex = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_PLAY_DELAY_CHECK)
            {
                checkDelay();
            }
        }
    };

    public void setOnLineStatus(boolean isOnLine)
    {
        this.isOnLine = isOnLine;
    }
    public boolean isOnline()
    {
        return isOnLine;
    }

    public HardWarePlayFactory(Activity mContext)
    {
        this.mContext = mContext;
    }

    public void setVideoPlayer(JungleMediaPlayer videoPlayer)
    {
        this.mVideoPlayer = videoPlayer;
        mVideoPlayer.addPlayerListener(mBaseMediaPlayerListener);

        mCheckDelayTimer = new CheckDelayTimer(mContext);
        mCheckDelayTimer.setHandler(mHandler,MSG_PLAY_DELAY_CHECK);
        mCheckDelayTimer.setTimeInterval(20*1000);
        mCheckDelayTimer.startTimer();
    }
    public void setZNTDownloadServiceManager(ZNTDownloadServiceManager mZNTDownloadServiceManager)
    {
        this.mZNTDownloadServiceManager = mZNTDownloadServiceManager;
    }
    public void setZNTPushServiceManager(ZNTPushServiceManager mZNTPushServiceManager)
    {
        this.mZNTPushServiceManager = mZNTPushServiceManager;
    }

    private BaseMediaPlayerListener mBaseMediaPlayerListener = new BaseMediaPlayerListener() {
        @Override
        public void onLoading() {
            Log.d("","");
            //updatePushParams(mContext.getResources().getString(R.string.media_loading),true);
        }

        @Override
        public void onLoadFailed() {
            Log.d("","");
            Constant.DEBUG_INFO += "-onLoadFailed";
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playNext();
                }
            },1000);
        }

        @Override
        public void onFinishLoading() {
            Log.d("","");
        }

        @Override
        public void onError(int what, boolean canReload, final String message) {

            if(mVideoPlayer != null && mVideoPlayer.getPlayerBottomControl() != null)
                mVideoPlayer.getPlayerBottomControl().onErrorProcess();
            Constant.DEBUG_INFO += "-onLoadFailed";
            updatePushParams(mContext.getResources().getString(R.string.media_load_fail),true);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "加载文件失败："+message, Toast.LENGTH_SHORT).show();
                    playNext();
                }
            },1000);

        }

        @Override
        public void onStartPlay() {

            if(mVideoPlayer != null && mVideoPlayer.getPlayerBottomControl() != null)
                mVideoPlayer.getPlayerBottomControl().onStartPlayProcess();

            updatePushParams(null,true);

            //播放完插播后，重新从暂停的地方开始播放
            if(curPlayMedia != null && curPlayMedia.isMedia() && curPlayMedia.getCurSeek() > 0)
                mVideoPlayer.seekTo(curPlayMedia.getCurSeek());
        }

        @Override
        public void onPlayComplete() {
            if(mVideoPlayer != null && mVideoPlayer.getPlayerBottomControl() != null)
                mVideoPlayer.getPlayerBottomControl().onPlayCompleteProcess();
            playNext();
        }

        @Override
        public void onStartSeek() {
            Log.d("","");
        }

        @Override
        public void onSeekComplete() {
            Log.d("","");
        }

        @Override
        public void onResumed() {
            Log.d("","");
            if(mVideoPlayer != null && mVideoPlayer.getPlayerBottomControl() != null)
                mVideoPlayer.getPlayerBottomControl().onResumedPrcess();
        }

        @Override
        public void onPaused() {
            Log.d("","");
            if(mVideoPlayer != null && mVideoPlayer.getPlayerBottomControl() != null)
                mVideoPlayer.getPlayerBottomControl().onPausedProcess();
        }

        @Override
        public void onStopped() {
            Log.d("","");
            if(mVideoPlayer != null && mVideoPlayer.getPlayerBottomControl() != null)
                mVideoPlayer.getPlayerBottomControl().onStoppedProcess();
        }
    };

    public void playNext()
    {
        if(isCurMediaCanPlay())
        {
            MediaInfor tempInfor = getPushMedia();//首先从push中的获取
            if(tempInfor == null)
                tempInfor = getLastPauseMedia();//没有的话再从上次暂停的获取
            if(tempInfor == null)
                tempInfor = mZNTPushServiceManager.getCurPlayMedia();//正常播放列表也没有就从Service中获取
            if(tempInfor == null)
            {
                //没有可播放的歌曲
                return;
            }

            //if(!tempInfor.isMedia())//现在要播放的是正常的
            {
                setCurPauseMedia(null);
                play(tempInfor);
            }
        }
    }

    private String realPlayUrl = "";
    private void play(MediaInfor tempInfor)
    {
        curPlayMedia = tempInfor;
        realPlayUrl = getMusicPlayUrL(tempInfor);

        mZNTPushServiceManager.updatePlayRecord(tempInfor);

        mVideoPlayer.playMedia(new VideoInfo(realPlayUrl,curPlayMedia.getMediaName(), curPlayMedia.getMediaType()),curPlayMedia.getCurSeek());
    }

    public void setDevName(String devName)
    {
        if(TextUtils.isEmpty(devName))
            devName = LocalDataEntity.newInstance(mContext).getDeviceName();
        if(mVideoPlayer != null)
            mVideoPlayer.setDevName(devName + "   " + Build.MODEL);
    }

    public void setSystemTime(String systemTime)
    {
        mVideoPlayer.setSystemTime(systemTime);
    }

    public void playPushMedia(MediaInfor mediaInfor)
    {
        //点播歌曲之前先保存当前正在播放的歌曲
        if(mVideoPlayer != null && mVideoPlayer.isPlaying())
        {
            if(curPlayMedia != null && curPlayMedia.isMedia())//当前正在播放的是正常的播放计划
            {
                if(mVideoPlayer.getCurrentPosition() > 0)
                    curPlayMedia.setCurSeek(mVideoPlayer.getCurrentPosition());
                setCurPauseMedia(curPlayMedia);
            }
        }

        if(curPlayMedia != null && curPlayMedia.isPush())//当前在播放push的
        {

        }
        else if(curPlayMedia != null && !curPlayMedia.isPush())//当前播放不是push的
        {
            play(mediaInfor);
        }
        else
            play(mediaInfor);
    }

    private void setCurPauseMedia(MediaInfor pauseMediaInfor)
    {
        this.curPauseMedia = pauseMediaInfor;
    }

    private boolean isCurMediaCanPlay()
    {
        if(mVideoPlayer.isPlaying())
        {
            if(curPlayMedia == null)
                return true;
            if(!curPlayMedia.isMedia())
                return false;//当前播放的是插播或者广告
        }
        return true;
    }

    public void addPushMedias(List<MediaInfor> tempList)
    {

        this.curPushList.clear();
        this.curPushList.addAll(tempList);

        if(this.curPushList.size()> 0 && (curPlayMedia == null || !curPlayMedia.isPush()))
        {
            MediaInfor tempInfo = this.curPushList.remove(0);
            tempInfo.setMediaType(MediaInfor.MEDIA_TYPE_PUSH);
            playPushMedia(tempInfo);
        }
    }

    private MediaInfor getLastPauseMedia()
    {
        return curPauseMedia;
    }

    private MediaInfor getPushMedia()
    {
        if(curPushList.size() == 0 || !isOnline())//没有插播列表或者掉线都不让插播
            return null;
        curPlayMedia = curPushList.remove(0);
        curPlayMedia.setMediaType(MediaInfor.MEDIA_TYPE_PUSH);
        return curPlayMedia;
    }

    private String getMusicPlayUrL(MediaInfor songInfor)
    {
        String url = songInfor.getMediaUrl();

        if(url.startsWith("http://") || url.startsWith("https://"))
        {
            String fileName = "";
            if(url.contains("."))
                fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
            //String filePath2 = CacheDirUtils.getINSTANCE().getDdownLoadDir() + File.separator + fileName;
            File file = new File(CacheDirUtils.getINSTANCE().getDownLoadDir() + File.separator + fileName);
            if(file.exists())
            {
                return file.getAbsolutePath();
            }
            else
            {
                File file1 = new File(CacheDirUtils.getINSTANCE().getDownLoadDirOld() + File.separator + fileName);
                if(file1.exists())
                    return file1.getAbsolutePath();
                else
                    mZNTDownloadServiceManager.addSonginfor(songInfor);
            }
        }

        return songInfor.getMediaUrl();
    }

    private boolean isUpdatePushParamsRunning = false;
    public synchronized void updatePushParams(final String msg,final boolean updateNow)
    {

        if(!isUpdatePushParamsRunning)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    isUpdatePushParamsRunning = true;
                    String message = msg;
                    if(isPlaying() || isLoading())
                    {
                        if(curPlayMedia == null)
                            curPlayMedia = new MediaInfor();
                        if(TextUtils.isEmpty(message))
                        {
                            if(realPlayUrl.startsWith("http://") || realPlayUrl.startsWith("https://"))
                            {
                                message = "O->";//在线播放
                            }
                            else
                                message = "L->";//本地播放
                        }
                        curPlayMedia.setHintMsg(message);
                        mZNTPushServiceManager.putRequestParams(curPlayMedia, updateNow);
                    }
                    else
                        mZNTPushServiceManager.putRequestParams(null, updateNow);
                    isUpdatePushParamsRunning = false;
                }
            }).start();
        }
    }

    public boolean isPlaying()
    {
        if(mVideoPlayer == null)
            return false;
        return mVideoPlayer.isPlaying();
    }

    public boolean isLoading()
    {
        if(mVideoPlayer == null)
            return false;
        return mVideoPlayer.isLoading();
    }

    private boolean isFirstCheckDelay = true;
    public void checkDelay()
    {
        if(isFirstCheckDelay)
        {
            isFirstCheckDelay = false;
            return;
        }
        if(mVideoPlayer == null && !mVideoPlayer.isPlaying())
            return;

        int pos = mVideoPlayer.getCurrentPosition();
        boolean ret = isDelay(pos);
        if (ret)
        {
            playNext();
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

    public boolean isCurPlayerCanStop()
    {
        if(mVideoPlayer != null && mVideoPlayer.isPlaying())
        {
            if(curPlayMedia.isMedia())
            {
                return true;
            }
        }
        return false;
    }

    public void stopPlayByForce(String info)
    {
        stopPlay(info);
    }

    private int stopCount = 0;
    public void stopPlay(String info)
    {
        stopCount ++;
        Constant.DEBUG_INFO += "-stopCount->"+stopCount + " " + info;
        if( mVideoPlayer != null)
        {
            mVideoPlayer.stop();
        }
    }
}