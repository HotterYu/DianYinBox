/**
 * Android Jungle-MediaPlayer framework project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jungle.mediaplayer.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import com.znt.lib.utils.ViewUtils;
import com.jungle.mediaplayer.R;
import com.jungle.mediaplayer.base.BaseMediaPlayerListener;
import com.jungle.mediaplayer.base.VideoInfo;
import com.jungle.mediaplayer.player.BaseMediaPlayer;
import com.jungle.mediaplayer.player.SystemImplMediaPlayer;
import com.jungle.mediaplayer.player.render.MediaRender;
import com.jungle.mediaplayer.player.render.TextureViewMediaRender;
import com.jungle.mediaplayer.utils.JMFileUtils;
import com.jungle.mediaplayer.widgets.control.PlayerBottomControl;

import java.util.List;
import java.util.logging.Handler;

public class JungleMediaPlayer extends MediaPlayerFrame {

    public static class PlayVideoInfo {
        public VideoInfo mVideoInfo;

        public PlayVideoInfo(VideoInfo info) {
            mVideoInfo = info;
        }
    }

    private final int MSG_ON_PLAY_VIEW_SHOW = 0;
    private android.os.Handler mHandler = new android.os.Handler(new android.os.Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            if(msg.what == MSG_ON_PLAY_VIEW_SHOW)
            {
                String url = (String) msg.obj;
                if(JMFileUtils.isMusic(url))
                {
                    if(mOnCallBackListener != null)
                        mOnCallBackListener.onDevInfoShow(true);

                    if(surfaceView != null)
                        surfaceView.setVisibility(View.GONE);
                    mMusicPlayViewControl.setVisibility(View.VISIBLE);
                    mBottomControl.setVisibility(View.VISIBLE);
                    mTopControl.setVisibility(View.VISIBLE);
                }
                else
                {
                    if(mOnCallBackListener != null)
                        mOnCallBackListener.onDevInfoShow(false);

                    if(surfaceView != null)
                        surfaceView.setVisibility(View.VISIBLE);
                    mMusicPlayViewControl.setVisibility(View.GONE);
                    mBottomControl.setVisibility(View.GONE);
                    mTopControl.setVisibility(View.GONE);
                }
            }

            return false;
        }
    });

    private BaseMediaPlayer mMediaPlayer;
    private PlayVideoInfo mSavedVideoInfo;


    public JungleMediaPlayer(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public JungleMediaPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JungleMediaPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            return;
        }

        View.inflate(context, R.layout.layout_jungle_media_player, getMediaRootLayout());

        initView();
        initMediaPlayer();
        requestFocus();
    }

    private void initView() {
        mTopControl.createDefault();
        mBottomControl.createDefault();

        mMusicPlayViewControl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnCallBackListener != null)
                {
                    mOnCallBackListener.onLogoViewClick();
                }
            }
        });

        findViewById(R.id.refresh_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mBottomControl.getPlayPosition();

            }
        });
    }

    private TextureView surfaceView = null;
    private void initMediaPlayer() {
        surfaceView = (TextureView) findViewById(R.id.player_surface);
        mMediaPlayer = createMediaPlayer(new TextureViewMediaRender(surfaceView));
        //mMediaPlayer.addPlayerListener(mBasePlayerListener);
        mBottomControl.setMediaPlayer(this);

        setTextureView(surfaceView);
    }

    public PlayerBottomControl getPlayerBottomControl()
    {
        return mBottomControl;
    }

    public void showMusicPlayView(String url)
    {
        ViewUtils.sendMessage(mHandler,MSG_ON_PLAY_VIEW_SHOW,url);
    }

    protected BaseMediaPlayer createMediaPlayer(MediaRender render) {
        return new SystemImplMediaPlayer(getContext(), render);
    }

    @Override
    public void destroy() {
        super.destroy();

        mMediaPlayer.destroy();
    }

    public void playMedia(VideoInfo info, int startMillSeconds) {
        info.setCurrentPosition(startMillSeconds);
        mSavedVideoInfo = new PlayVideoInfo(info);

        showMusicPlayView(mSavedVideoInfo.mVideoInfo.getStreamUrl());

        playMediaInternal();
    }

    private void playMediaInternal() {
        mBottomControl.prepareForPlay();
        mGestureController.prepareForPlay();

        if (!VideoInfo.validate(mSavedVideoInfo.mVideoInfo)) {
            showError(true);
            return;
        }

        checkPlayType();

        showError(false);
        mMediaPlayer.play(mSavedVideoInfo.mVideoInfo);
        mBottomControl.setmCurPlayName(mSavedVideoInfo);

    }

    private void checkPlayType()
    {
        if(mSavedVideoInfo != null && mSavedVideoInfo.mVideoInfo != null)
        {
            String mediaUrl = mSavedVideoInfo.mVideoInfo.getStreamUrl();

            if(mediaUrl.startsWith("http://") || mediaUrl.startsWith("https://"))
            {
                mSavedVideoInfo.mVideoInfo.setPlayType("1");//在线播放
            }
            else
                mSavedVideoInfo.mVideoInfo.setPlayType("0");//本地播放
        }
    }

    public void setDevName(String devName)
    {
        mTopControl.setDevName(devName);
    }

    public void setSystemTime(String systemTime)
    {
        mTopControl.setSystemTime(systemTime);
    }

    public boolean isPaused() {
        return mMediaPlayer.isPaused();
    }

    public boolean hasVideoPlay() {
        return mMediaPlayer != null && mMediaPlayer.hasVideoPlay();
    }

    @Override
    public void setVolume(float volume) {
        mMediaPlayer.setVolume(volume);
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public boolean isPlayCompleted() {
        return mMediaPlayer.isPlayCompleted();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getBufferPercent() {
        return mMediaPlayer.getBufferPercent();
    }

    @Override
    public void pause() {
        if (isLoading()) {
            return;
        }

        mMediaPlayer.pause();
    }

    @Override
    public void resume() {
        if (isLoading()) {
            return;
        }

        mMediaPlayer.resume();
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
    }

    @Override
    public boolean isLoading() {
        return mMediaPlayer.isLoading();
    }

    @Override
    public boolean isLoadingFailed() {
        return mMediaPlayer.isLoadingFailed();
    }

    @Override
    public void addPlayerListener(BaseMediaPlayerListener listener) {
        mMediaPlayer.addPlayerListener(listener);
    }

    @Override
    protected void toggleFullScreen(boolean isFullscreen, boolean reverseOrientation) {

    }

    @Override
    protected void updateMediaSize(int width, int height) {
        mMediaPlayer.updateMediaRenderSize(width, height, false);
    }

    @Override
    protected void handleDragProgress(int startDragPos, float distanceX) {
        float percent = distanceX / (float) mRootView.getMeasuredWidth();
        int total = getDuration();
        int currPosition = startDragPos;
        int seekOffset = (int) (total * percent);

        currPosition += seekOffset;
        if (currPosition < 0) {
            currPosition = 0;
        } else if (currPosition > total) {
            currPosition = total;
        }

        mGestureController.showAdjustProgress(seekOffset > 0, currPosition, total);
        mBottomControl.dragProgress(currPosition);
    }

    @Override
    protected void handleEndDragProgress(int dragPosition, float totalDistanceX) {
        seekTo(dragPosition);
        if (!isPlaying()) {
            resume();
        }
    }

    @Override
    public void onBackBtnClicked() {
        if(mOnCallBackListener != null)
            mOnCallBackListener.onCloseClick();
    }

    @Override
    public Bitmap captureMedia() {
        return null;
    }
}
