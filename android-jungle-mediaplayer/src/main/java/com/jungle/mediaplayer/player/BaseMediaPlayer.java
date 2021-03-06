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

package com.jungle.mediaplayer.player;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.jungle.mediaplayer.base.BaseMediaPlayerInterface;
import com.jungle.mediaplayer.base.BaseMediaPlayerListener;
import com.jungle.mediaplayer.base.MediaSize;
import com.jungle.mediaplayer.base.VideoInfo;
import com.jungle.mediaplayer.player.render.MediaRender;
import com.jungle.mediaplayer.player.render.MockMediaRender;
import com.jungle.mediaplayer.player.render.SurfaceViewMediaRender;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMediaPlayer
        implements BaseMediaPlayerInterface, MediaRender.Listener {

    protected static final String TAG = "BaseMediaPlayer";


    protected interface NotifyListenerRunnable {
        void run(BaseMediaPlayerListener listener);
    }


    protected Context mContext;
    protected SurfaceViewMediaRender mMediaRender;
    protected BaseMediaPlayerListener mPlayerListener = null;
    protected Handler mMainHandler = new Handler(Looper.getMainLooper());
    protected VideoInfo mVideoInfo;
    protected int mVideoWidth;
    protected int mVideoHeight;
    protected boolean mIsLoading;
    protected boolean mIsLoadingFailed;
    protected boolean mMediaPlayerIsPrepared;
    protected boolean mVideoSizeInitialized;
    protected int mBufferPercent;
    protected int mVideoContainerZoneWidth;
    protected int mVideoContainerZoneHeight;
    protected boolean mAutoPlayWhenHolderCreated;
    protected boolean mAutoResumeWhenHolderCreated = true;
    protected boolean mIsPaused = false;


    /*public BaseMediaPlayer(Context context) {
        this(context, new MockMediaRender());
    }*/

    public BaseMediaPlayer(Context context, SurfaceViewMediaRender render) {
        mContext = context;
        mMediaRender = render;

    }

    protected void initRender()
    {
        mMediaRender.setListener(this);
        mMediaRender.initRender();


    }

    public void play(VideoInfo videoInfo) {
        Log.e(TAG, "Pre-Play Video.");

        mIsPaused = false;
        mIsLoading = true;
        mIsLoadingFailed = false;
        mVideoSizeInitialized = false;
        mMediaPlayerIsPrepared = false;
        mVideoInfo = videoInfo;

    }

    public void addPlayerListener(BaseMediaPlayerListener listener) {
        this.mPlayerListener = listener;
    }

    @Override
    public void destroy() {

    }

    public void setAutoResume(boolean autoResume) {
        mAutoResumeWhenHolderCreated = autoResume;
    }

    public abstract boolean hasVideoPlay();

    protected abstract void playWithMediaRender();

    protected abstract void surfaceHolderChanged();

    public MediaRender getMediaRender() {
        return mMediaRender;
    }

    @Override
    public boolean isLoading() {
        return mIsLoading;
    }

    @Override
    public boolean isLoadingFailed() {
        return mIsLoadingFailed;
    }

    public boolean isLoadingOrPlaying() {
        return isLoading() || isPlaying();
    }

    @Override
    public int getBufferPercent() {
        return mBufferPercent;
    }

    @Override
    public boolean isPlayCompleted() {
        return false;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onRenderCreated() {
        if (mAutoPlayWhenHolderCreated) {
            mAutoPlayWhenHolderCreated = false;
            playWithMediaRender();
        } else {
            surfaceHolderChanged();

            if (mAutoResumeWhenHolderCreated) {
                resume();
            }
        }
    }

    @Override
    public void onRenderDestroyed() {
        pause();
    }

    protected void updateMediaRenderSize() {
        updateMediaRenderSize(mVideoContainerZoneWidth, mVideoContainerZoneHeight, true);
    }

    public static MediaSize calcVideoSize(
            int containerWidth, int containerHeight,
            int videoWidth, int videoHeight) {

        if (videoWidth == 0
                || videoHeight == 0
                || containerWidth == 0
                || containerHeight == 0) {
            return null;
        }

        int width = 0;
        int height = 0;
        float containerRatio = (float) containerWidth / (float) containerHeight;
        float ratio = (float) videoWidth / (float) videoHeight;
        if (ratio == containerRatio) {
            width = containerWidth;
            height = containerHeight;
        } else if (containerRatio > ratio) {
            height = containerHeight;
            width = (int) (height * ratio);
        } else {
            width = containerWidth;
            height = (int) (width / ratio);
        }

        return new MediaSize(width, height);
    }

    public void updateMediaRenderSize(int containerWidth, int containerHeight, boolean force) {
        if (mVideoContainerZoneWidth == containerWidth
                && mVideoContainerZoneHeight == containerHeight
                && !force) {
            return;
        }

        mVideoContainerZoneWidth = containerWidth;
        mVideoContainerZoneHeight = containerHeight;

        MediaSize size = calcVideoSize(
                mVideoContainerZoneWidth, mVideoContainerZoneHeight,
                mVideoWidth, mVideoHeight);
        if (size == null) {
            return;
        }

        View renderView = mMediaRender.getRenderView();
        if (renderView != null) {
            ViewGroup.LayoutParams params = renderView.getLayoutParams();
            params.width = size.mWidth;
            params.height = size.mHeight;
            renderView.setLayoutParams(params);
        }
    }

    protected void notifyLoading() {
        Log.e(TAG, "MediaPlayer Loading...");
        if(mPlayerListener != null)
            mPlayerListener.onLoading();
    }

    protected void notifyFinishLoading() {
        Log.e(TAG, "MediaPlayer Finish Loading!");
        if(mPlayerListener != null)
            mPlayerListener.onFinishLoading();

    }

    protected void notifyError(int what, String message) {
        notifyError(what, true, message);
    }

    protected void notifyError(final int what, final boolean canReload, final String message) {
        mIsLoading = false;
        mIsLoadingFailed = true;
        Log.e(TAG, String.format("MediaPlayer Error. what = %d, message = %s.", what, message));
        if(mPlayerListener != null)
            mPlayerListener.onError(what, canReload, message);
    }

    protected void notifyStartPlay() {
        Log.e(TAG, "MediaPlayer Will Play!");
        if(mPlayerListener != null)
            mPlayerListener.onStartPlay();
    }

    protected void notifyPlayComplete() {
        Log.e(TAG, "MediaPlayer Play Current Complete!");
        if(mPlayerListener != null)
            mPlayerListener.onPlayComplete();
    }

    protected void notifyStartSeek() {
        Log.e(TAG, "Video Start Seek!");
        if(mPlayerListener != null)
            mPlayerListener.onStartSeek();
    }

    protected void notifySeekComplete() {
        Log.e(TAG, "Video Seek Complete!");
        if(mPlayerListener != null)
            mPlayerListener.onSeekComplete();
    }

    protected void notifyPaused() {
        Log.e(TAG, "MediaPlayer Paused.");
        if(mPlayerListener != null)
            mPlayerListener.onPaused();
    }

    protected void notifyResumed() {
        Log.e(TAG, "MediaPlayer Resumed.");
        if(mPlayerListener != null)
            mPlayerListener.onResumed();
    }

    protected void notifyStopped() {
        Log.e(TAG, "MediaPlayer Stopped!");
        if(mPlayerListener != null)
            mPlayerListener.onStopped();

    }
}
