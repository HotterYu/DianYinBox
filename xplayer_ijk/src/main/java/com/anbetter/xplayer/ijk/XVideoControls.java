package com.anbetter.xplayer.ijk;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.anbetter.xplayer.ijk.api.IXVideoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.loader.ImageLoader;

import java.util.List;

import ling.placeholder.CorpDrawableBuilder;

/**
 * <p>
 * Created by android_ls on 2018/5/8.
 *
 * @author android_ls
 * @version 1.0
 */
public class XVideoControls extends FrameLayout {

    private Activity mActivity = null;

    private IXVideoView mVideoView;
    private ImageView iv_start;
    private ProgressBar pbLoading;
    protected View mLogoView;

    protected View mBottomView;

    private ImageView ivRotation;
    private ImageView ivFullScreen;
    private SeekBar mSeekBar;
    private TextView tvCurTimeLeft;
    private TextView tvCurTimeRight;
    private TextView tvMediaName;

    private XVideoView.OnUiStatusListener mOnUiStatusListener = null;
    public void setOnUiStatusListener(XVideoView.OnUiStatusListener mOnUiStatusListener)
    {
        this.mOnUiStatusListener = mOnUiStatusListener;
    }

    private final int MESSAGE_CONTROLLER_HIDE = 0;
    private int degreee = 0;

    @SuppressWarnings("HandlerLeak")
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**滑动完成，隐藏滑动提示的box*/
                case MESSAGE_CONTROLLER_HIDE:
                    msg = obtainMessage(MESSAGE_CONTROLLER_HIDE);
                    sendMessageDelayed(msg, 1000);
                    break;

            }
        }
    };

    public XVideoControls(Context context) {
        super(context);
        setup(context);
    }

    public XVideoControls(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public void setActivity(Activity mActivity)
    {
        this.mActivity = mActivity;
    }


    protected void setup(Context context) {

        LayoutInflater.from(context).inflate(R.layout.video_view_controls_layout, this);
        mBottomView = findViewById(R.id.jz_play_controller);

        mLogoView = findViewById(R.id.view_logo);
        ivFullScreen = (ImageView)findViewById(R.id.app_video_fullscreen);
        ivRotation = (ImageView)findViewById(R.id.ijk_iv_rotation);
        iv_start = (ImageView)findViewById(R.id.app_video_play);
        pbLoading = (ProgressBar) findViewById(R.id.app_video_play_loding);
        mSeekBar = (SeekBar)findViewById(R.id.app_video_seekBar);
        tvCurTimeLeft = (TextView) findViewById(R.id.app_video_currentTime);
        tvCurTimeRight = (TextView) findViewById(R.id.app_video_endTime);
        tvMediaName = (TextView) findViewById(R.id.app_video_media_name);


        iv_start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mVideoView == null)
                {
                    return;
                }
                if(mVideoView.isPlaying())
                {
                    iv_start.setImageResource(R.drawable.jz_click_play_selector);
                    mVideoView.pause();
                }
                else
                {
                    iv_start.setImageResource(R.drawable.jz_click_pause_selector);
                    mVideoView.play();
                }
            }
        });
        ivFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFullScreen();
            }
        });
        ivRotation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                degreee = degreee + 90;
                if(degreee >= 360)
                    degreee = 0;
                mVideoView.setVideoRotation(degreee);
            }
        });
    }



    public void showPlayLoading()
    {
        pbLoading.setVisibility(View.VISIBLE);
    }
    public void hidePlayLoading()
    {
        pbLoading.setVisibility(View.GONE);
    }

    public void showLogoView(boolean show)
    {
        setVisibility(View.VISIBLE);
        if(banner != null && banner.isShown())
        {
            mLogoView.setVisibility(View.GONE);
            setVisibility(View.GONE);
            if(mOnUiStatusListener != null)
                mOnUiStatusListener.onUiStatusChanged(false);
        }
        else
        {
            if(show)
            {
                mLogoView.setVisibility(View.VISIBLE);
                setVisibility(View.VISIBLE);
                if(mOnUiStatusListener != null)
                    mOnUiStatusListener.onUiStatusChanged(true);
            }
            else
            {
                mLogoView.setVisibility(View.GONE);
                setVisibility(View.GONE);
                if(mOnUiStatusListener != null)
                    mOnUiStatusListener.onUiStatusChanged(false);
            }
        }

    }

    public void setTvPlayTime(String time1,String time2)
    {
        tvCurTimeLeft.setText(time1);
        tvCurTimeRight.setText(time2);
    }

    public void setOnLogoViewClickListener(OnClickListener l)
    {
        mLogoView.setOnClickListener(l);
    }

    public void setMaxProgress(int maxProgress)
    {
        mSeekBar.setMax(maxProgress);
    }

    public void setCurProgress(int curProgress)
    {
        mSeekBar.setProgress(curProgress);
    }

    public SeekBar getSeekBar()
    {
        return mSeekBar;
    }

    public void setVideoView(IXVideoView videoView) {
        this.mVideoView = videoView;
    }

    public void showMediaName(String name)
    {
        tvMediaName.setText(name);
    }

    boolean isVideoPlay = false;
    public void setIsVideoPlay(boolean isVideoPlay)
    {
        this.isVideoPlay = isVideoPlay;
    }

    public void show()
    {
        setVisibility(View.VISIBLE);
        if(mOnUiStatusListener != null)
        {
            mOnUiStatusListener.onUiStatusChanged(true);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isVideoPlay)
                {
                    setVisibility(View.GONE);
                    if(mOnUiStatusListener != null)
                    {
                        mOnUiStatusListener.onUiStatusChanged(false);
                    }
                }
            }
        }, 10 *1000);
    }

    public void hide()
    {
        setVisibility(View.GONE);
    }


    public void updatePlayBtnStatus()
    {
        if(mVideoView.isPlaying())
        {
            iv_start.setImageResource(R.drawable.jz_click_pause_selector);
        }
        else
        {
            iv_start.setImageResource(R.drawable.jz_click_play_selector);
        }
    }

    /**
     * 全屏切换
     */
    public void toggleFullScreen()
    {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
        {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        updateFullScreenButton();
    }
    /**
     * 更新全屏和半屏按钮
     */
    private void updateFullScreenButton() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            ivFullScreen.setImageResource(R.drawable.simple_player_icon_fullscreen_shrink);
        } else {
            ivFullScreen.setImageResource(R.drawable.simple_player_icon_fullscreen_stretch);
        }
    }
    /**
     * 获取界面方向
     */
    public int getScreenOrientation() {
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }


    private Banner banner = null;
    private void onImageClickProcess()
    {
        setVisibility(View.VISIBLE);
        if(mOnUiStatusListener != null)
            mOnUiStatusListener.onUiStatusChanged(true);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLogoView(false);
            }
        },10*1000);
    }
    public void startPlayImages(List<String> mediaList)
    {
        if(banner == null)
        {
            banner = (Banner) findViewById(R.id.banner);
            banner.setOnBannerClickListener(new OnBannerClickListener() {
                @Override
                public void OnBannerClick(int position) {
                    onImageClickProcess();
                }
            });
        }
        if(mediaList != null && mediaList.size() > 0)
        {
            banner.setVisibility(View.VISIBLE);

            showLogoView(false);
            //设置图片加载器
            banner.setImageLoader(new GlideImageLoader());
            //设置自动轮播，默认为true
            banner.isAutoPlay(true);
            //设置轮播时间
            banner.setDelayTime((int) 10000);
            //设置指示器位置（当banner模式中有指示器时）
            banner.setBannerStyle(BannerConfig.NOT_INDICATOR);
            //设置banner动画效果
            //banner.setBannerAnimation(Transformer.DepthPage);
            banner.setOffscreenPageLimit(3);

            //设置图片集合
            banner.setImages(mediaList);
            //banner设置方法全部调用完毕时最后调用
            banner.start();
        }
        else
        {
            banner.setVisibility(View.GONE);
            banner.stopAutoPlay();

        }

    }
    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //Glide 加载图片简单用法
            Glide.with(mActivity).load((String) path)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(CorpDrawableBuilder.build(ActivityCompat.getDrawable(mActivity, R.drawable.jz_loading_bg), Color.BLACK))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFirstResource) {
                            // 可替换成进度条
                            //Toast.makeText(activity, "图片加载失败", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache,
                                                       boolean isFirstResource) {
                            // 图片加载完成，取消进度条
                            //Toast.makeText(activity, "图片加载成功", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }).into(imageView);
        }

        //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
        @Override
        public ImageView createImageView(Context context) {
            //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView

            return super.createImageView(context);
        }
    }
}
