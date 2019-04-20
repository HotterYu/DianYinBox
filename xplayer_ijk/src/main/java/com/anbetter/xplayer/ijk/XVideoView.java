package com.anbetter.xplayer.ijk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.anbetter.xplayer.ijk.api.IXMediaPlayer;
import com.anbetter.xplayer.ijk.api.IXRenderView;
import com.anbetter.xplayer.ijk.api.IXVideoView;
import com.anbetter.xplayer.ijk.delegate.XMediaPlayerDelegate;
import com.anbetter.xplayer.ijk.listener.IXVideoViewListener;
import com.anbetter.xplayer.ijk.listener.XMediaPlayerListener;
import com.anbetter.xplayer.ijk.utils.JzFileUtils;

import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class XVideoView extends FrameLayout implements IXVideoView,
        XMediaPlayerListener, IXRenderView.OnSurfaceStatusListener, AudioManager.OnAudioFocusChangeListener {

    public static final String TAG = XVideoView.class.getSimpleName();

    protected View mCoverView;

    protected IXRenderView mRenderView;
    protected IXMediaPlayer mMediaPlayer;
    protected AudioManager mAudioManager;
    private XVideoControls mVideoControls;
    private IXVideoViewListener mVideoViewListener;

    protected String mTile;
    protected String mVideoPath;
    protected boolean mLooping;

    protected boolean isDragging;

    public interface OnUiStatusListener
    {
        void onUiStatusChanged(boolean show);
    }
    private OnUiStatusListener mOnUiStatusListener = null;
    public void setOnUiStatusListener(OnUiStatusListener mOnUiStatusListener)
    {
        this.mOnUiStatusListener = mOnUiStatusListener;
        mVideoControls.setOnUiStatusListener(mOnUiStatusListener);
    }

    public interface OnPlayResultListener
    {
        void onPlayError(int what, int extra);
        void onPrepared();
        void onPlayFinish();
    }
    private OnPlayResultListener mOnPlayResultListener = null;
    public void setOnPlayResultListener(OnPlayResultListener mOnPlayResultListener)
    {
        this.mOnPlayResultListener = mOnPlayResultListener;
    }
    /**
     * 是否显示控制面板，默认为隐藏，true为显示false为隐藏
     */
    private boolean isShowControlPanl = true;
    protected int currentFocus = 0;

    /**
     * 播放缓冲监听
     */
    private int mCurrentBufferPercentage;

    protected int mVideoWidth;
    protected int mVideoHeight;
    protected int mVideoSarNum;
    protected int mVideoSarDen;

    /**
     * 同步进度
     */
    private static final int MESSAGE_SHOW_PROGRESS = 1;
    /**
     * 设置新位置
     */
    private static final int MESSAGE_SEEK_NEW_POSITION = 3;
    /**
     * 隐藏提示的box
     */
    private static final int MESSAGE_HIDE_CENTER_BOX = 4;
    /**
     * 重新播放
     */
    private static final int MESSAGE_RESTART_PLAY = 5;
    /**
     * 加载开始
     */
    private static final int MESSAGE_LOAD_START = 6;
    /**
     * 加载结束
     */
    private static final int MESSAGE_LOAD_FINISH = 7;

    /**
     * 消息处理
     */
    @SuppressWarnings("HandlerLeak")
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**滑动完成，隐藏滑动提示的box*/
                case MESSAGE_HIDE_CENTER_BOX:
                    /*query.id(R.id.app_video_volume_box).gone();
                    query.id(R.id.app_video_brightness_box).gone();
                    query.id(R.id.app_video_fastForward_box).gone();*/
                    break;
                /**滑动完成，设置播放进度*/
                case MESSAGE_SEEK_NEW_POSITION:
                    /*if (!isLive && newPosition >= 0) {
                        videoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }*/
                    break;
                /**滑动中，同步播放进度*/
                case MESSAGE_SHOW_PROGRESS:
                    long pos = syncProgress();
                    if (!isDragging && isShowControlPanl) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        //updatePausePlay();
                    }
                    break;
                /**重新去播放*/
                case MESSAGE_RESTART_PLAY:
                    /*status = PlayStateParams.STATE_ERROR;
                    startPlay();
                    updatePausePlay();*/
                    break;
                case MESSAGE_LOAD_START:
                    mVideoControls.showPlayLoading();
                    showMeidaTypeView();
                    setKeepScreenOn(true);
                    mVideoControls.updatePlayBtnStatus();
                    break;
                case MESSAGE_LOAD_FINISH:
                    mVideoControls.hidePlayLoading();
                    mVideoControls.updatePlayBtnStatus();
                    break;
            }
        }
    };

    public XVideoView(@NonNull Context context) {
        super(context);
        setup(context);
    }

    public XVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public XVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public XVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context);
    }

    protected void setup(Context context) {
        mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        mRenderView = new TextureRenderView(context);
        mRenderView.setOnSurfaceStatusListener(this);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,Gravity.CENTER);
        addView(mRenderView.getView(), lp);

        mVideoControls = new XVideoControls(context);
        mVideoControls.show();

        setControls(mVideoControls);
        mVideoControls.getSeekBar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser)
                {
                    /**不是用户拖动的，自动播放滑动的情况*/
                    return;
                }
                else
                {
                    long duration = getDuration();
                    int position = (int) ((duration * progress * 1.0) / 1000);
                    String timeMax = generateTime(duration);
                    String timeCur = generateTime(position);
                    mVideoControls.setTvPlayTime(timeCur,timeMax);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDragging = true;
                mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                long duration = getDuration();
                mMediaPlayer.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
                mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
                mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mVideoControls.show();
            }
        });
    }

    public void startPlayImages(List<String> mediaList)
    {
        mVideoControls.startPlayImages(mediaList);
    }

    /**
     * 同步进度
     */
    private long syncProgress() {
        if (isDragging) {
            return 0;
        }
        long position = getCurrentPosition();
        long duration = getDuration();

        String timeMax = generateTime(duration);
        String timeCur = generateTime(position);

        mVideoControls.setTvPlayTime(timeCur, timeMax);

        if (mVideoControls.getSeekBar() != null)
        {
            if (duration > 0)
            {
                mVideoControls.getSeekBar().setMax(1000);
                long pos = 1000L * position / duration;
                mVideoControls.getSeekBar().setProgress((int) pos);
            }
            //mVideoControls.getSeekBar().setSecondaryProgress(mCurrentBufferPercentage * 10);
        }
        return position;
    }

    /**
     * 时长格式化显示
     */
    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void setCoverView(@NonNull View view) {
        mCoverView = view;
    }

    @Override
    public void setMediaPlayer(IXMediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    public void setVideoRotation(int degree) {
        if (mRenderView != null) {
            if (mRenderView instanceof TextureRenderView) {
                mRenderView.setVideoRotation(degree);
            }
        }
    }

    @Override
    public void setDisplayAspectRatio(int displayAspectRatio) {
        if (mRenderView != null) {
            mRenderView.setAspectRatio(displayAspectRatio);
        }
    }

    @Override
    public void setVideoPath(@NonNull String path, String title) {
        mVideoPath = path;
        mTile = title;
    }

    @Override
    public void setLooping(boolean looping) {
        mLooping = looping;
    }

    @Override
    public void seekTo(long pos) {
        if (isPlaying()) {
            mMediaPlayer.seekTo(pos);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if (isPlaying()) {
            mMediaPlayer.setSpeed(speed);
        }
    }

    @Override
    public void setVideoViewListener(IXVideoViewListener listener) {
        mVideoViewListener = listener;
    }

    @Override
    public Surface getSurface() {
        if (mRenderView != null) {
            return mRenderView.getSurface();
        }
        return null;
    }

    @Override
    public void setNeedMute(boolean needMute) {
        if (isPlaying()) {
            mMediaPlayer.setNeedMute(needMute);
        }
    }

    @Override
    public int getCurrentState() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentState();
        }
        return 0;
    }

    @Override
    public void play()
    {
        try
        {
            if (mMediaPlayer == null)
            {
                mMediaPlayer = new XMediaPlayer();
            }
            int currentState = mMediaPlayer.getCurrentState();
            if (currentState == mMediaPlayer.STATE_IDLE
                    || currentState == mMediaPlayer.STATE_ERROR
                    || currentState == mMediaPlayer.STATE_COMPLETED)
            {
                boolean focusAudioRequest = requestAudioFocus();
                if (!focusAudioRequest) {
                    mHandler.removeMessages(MESSAGE_LOAD_FINISH);
                    mHandler.sendEmptyMessage(MESSAGE_LOAD_FINISH);
                    return;
                }
                mHandler.removeMessages(MESSAGE_LOAD_START);
                mHandler.sendEmptyMessage(MESSAGE_LOAD_START);

                mMediaPlayer.reset();

                if (mRenderView != null && mRenderView.getSurface() != null)
                {
                    mMediaPlayer.setSurface(mRenderView.getSurface());
                }
                mMediaPlayer.setMediaPlayerListener(this);
                mMediaPlayer.setVideoPath(mVideoPath);
                mMediaPlayer.setLooping(mLooping);
                mMediaPlayer.play();
                mHandler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
            }
            else if (currentState == mMediaPlayer.STATE_PAUSED)
            {
                mMediaPlayer.resume();
            } /*else if (currentState == mMediaPlayer.STATE_COMPLETED) {
            mMediaPlayer.start();
            }*/
            mVideoControls.showMediaName(mTile);
        }
        catch (Exception e)
        {
            mHandler.removeMessages(MESSAGE_LOAD_FINISH);
            mHandler.sendEmptyMessage(MESSAGE_LOAD_FINISH);
            if(mOnPlayResultListener != null)
                mOnPlayResultListener.onPlayError(0, 0);
            e.printStackTrace();
        }
    }

    public void setOnLogoViewClickListener(OnClickListener l)
    {
        mVideoControls.setOnLogoViewClickListener(l);
    }

    public void setActivity(Activity mActivity)
    {
        mVideoControls.setActivity(mActivity);
    }


    @Override
    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
        setKeepScreenOn(false);
        abandonAudioFocus();
    }

    @Override
    public void resume() {
        boolean focusAudioRequest = requestAudioFocus();
        if (!focusAudioRequest) {
            return;
        }
        setKeepScreenOn(true);
        if (mMediaPlayer != null) {
            mMediaPlayer.resume();
        }
    }

    @Override
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }

        setKeepScreenOn(false);
        abandonAudioFocus();
    }

    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
    }

    @Override
    public void release() {
        stop();

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public int getVideoSarNum() {
        return mVideoSarNum;
    }

    private void showMeidaTypeView()
    {
        if(JzFileUtils.isMusic(mVideoPath))
        {
            //展示音乐播放界面
            mVideoControls.hide();
            mVideoControls.showLogoView(true);
            mVideoControls.setIsVideoPlay(false);
            mRenderView.getView().setVisibility(View.GONE);

        }
        else
        {
            //展示视频播放界面
            mVideoControls.show();
            mVideoControls.showLogoView(false);
            mVideoControls.setIsVideoPlay(true);
            mRenderView.getView().setVisibility(View.VISIBLE);

        }
    }

    public int getVideoSarDen() {
        return mVideoSarDen;
    }

    @Override
    public void onSurfaceCreated(Surface surface, int width, int height) {

        if (mMediaPlayer != null) {
            mMediaPlayer.setSurface(surface);
        }
    }

    @Override
    public void onSurfaceSizeChanged(Surface surface, int width, int height) {

    }

    @Override
    public void onSurfaceDestroyed(Surface surface) {
        if (mMediaPlayer != null) {
            mMediaPlayer.releaseSurface();
        }
    }

    @Override
    public void onPrepared(IXMediaPlayer mp) {

        if(mOnPlayResultListener != null)
            mOnPlayResultListener.onPrepared();

        int max = mp.getDuration();
        mVideoControls.setMaxProgress(max);

        mHandler.removeMessages(MESSAGE_LOAD_FINISH);
        mHandler.sendEmptyMessage(MESSAGE_LOAD_FINISH);

    }

    @Override
    public void onBufferingUpdate(int percent) {
        mCurrentBufferPercentage = percent;
    }

    @Override
    public void onSeekComplete() {
        int curPos = mMediaPlayer.getCurrentPosition();
        mVideoControls.setCurProgress(curPos);
    }

    @Override
    public void onError(int what, int extra) {
        if(mOnPlayResultListener != null)
            mOnPlayResultListener.onPlayError(what, extra);
        mHandler.removeMessages(MESSAGE_LOAD_FINISH);
        mHandler.sendEmptyMessage(MESSAGE_LOAD_FINISH);
    }

    @Override
    public void onInfo(int what, int extra) {
        if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            // 视频准备渲染
            showCoverView(false);
        }
    }

    @Override
    public void onVideoSizeChanged(IXMediaPlayer mp, int width, int height, int sarNum, int sarDe) {
//        MLog.i("=====================onVideoSizeChanged======================");

        mVideoWidth = mp.getVideoWidth();
        mVideoHeight = mp.getVideoHeight();
        mVideoSarNum = mp.getVideoSarNum();
        mVideoSarDen = mp.getVideoSarDen();

//        MLog.i("---->mVideoWidth =" + mVideoWidth);
//        MLog.i("---->mVideoHeight =" + mVideoHeight);
//        MLog.i("---->mVideoSarNum =" + mVideoSarNum);
//        MLog.i("---->mVideoSarDen =" + mVideoSarDen);

        if (mVideoWidth != 0 && mVideoHeight != 0) {
            if (mRenderView != null) {
                mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
            }
            requestLayout();
        }
    }

    @Override
    public void onCompletion() {
        if(mOnPlayResultListener != null)
            mOnPlayResultListener.onPlayFinish();
    }

    private void showCoverView(boolean toVisible) {
        if (mCoverView != null) {
            mCoverView.setVisibility(toVisible ? View.VISIBLE : View.GONE);
        }
    }

    protected boolean requestAudioFocus() {
        if (currentFocus == AudioManager.AUDIOFOCUS_GAIN) {
            return true;
        }

        if (mAudioManager == null) {
            return false;
        }

        int focusRequestGranted = mAudioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

//        AudioManager.AUDIOFOCUS_REQUEST_FAILED
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == focusRequestGranted) {
            currentFocus = AudioManager.AUDIOFOCUS_GAIN;
            return true;
        }
        return false;
    }

    protected void abandonAudioFocus() {
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(null);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
//        MLog.i("focusChange = " + focusChange);
        if (currentFocus == focusChange) {
            return;
        }

        currentFocus = focusChange;
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                // 获得了Audio Focus
                if (!isPlaying()) {
                    play();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // 失去了Audio Focus，并将会持续很长的时间。
                // 这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。
                if (isPlaying()) {
                    stop();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // 暂时失去Audio Focus，并会很快再次获得。
                // 必须停止Audio的播放，但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源；
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // 暂时失去AudioFocus，但是可以继续播放，不过要在降低音量。
                if (isPlaying()) {
                    stop();
                }
                break;
        }
    }

    public void setControls(@Nullable XVideoControls controls) {
        if (mVideoControls != null && mVideoControls != controls) {
            removeView(mVideoControls);
        }

        if (controls != null) {
            mVideoControls = controls;
            controls.setVideoView(this);
            addView(controls, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        MLog.i("--->onAttachedToWindow");

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        MLog.i("--->onDetachedFromWindow");

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
//        MLog.i("=================finalize=================");

    }

}
