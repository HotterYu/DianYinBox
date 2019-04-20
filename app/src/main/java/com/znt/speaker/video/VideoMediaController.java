package com.znt.speaker.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.znt.lib.utils.DateUtils;
import com.znt.lib.utils.ViewUtils;
import com.znt.speaker.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VideoMediaController extends RelativeLayout {

    private static final String TAG = "VideoMediaController";
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.iv_replay)
    ImageView ivReplay;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.rl_play_finish)
    RelativeLayout rlPlayFinish;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.tv_cur_time)
    TextView tvCurTime;
    @BindView(R.id.tv_use_time)
    TextView tvUseTime;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.pb_play_loading)
    ProgressBar mProgressBar;
    @BindView(R.id.iv_fullscreen)
    ImageView ivFullscreen;
    @BindView(R.id.ll_play_control)
    LinearLayout llPlayControl;

    private Context mContext = null;

    private boolean hasPause;//是否暂停

    private static final  int MSG_HIDE_TITLE = 0;
    private static final int MSG_UPDATE_TIME_PROGRESS = 1;
    private static final int  MSG_HIDE_CONTROLLER = 2;
    private static final int  UPDATE_CUR_SERVER_TIME = 3;
    private static final int  UPDATE_CUR_NAME = 4;
    //消息处理器
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_HIDE_TITLE:
                    tvTitle.setVisibility(View.GONE);
                    break;
                case MSG_UPDATE_TIME_PROGRESS:
                    updatePlayTimeAndProgress();
                    break;
                case MSG_HIDE_CONTROLLER:
                    showOrHideVideoController();
                case UPDATE_CUR_SERVER_TIME:
                    long time = (long) msg.obj;
                    tvCurTime.setText(DateUtils.getDateFromLong(time));
                    break;
                case UPDATE_CUR_NAME:
                    String name = (String) msg.obj;
                    tvTitle.setText(name);
                    break;
            }
        }
    };

    public void showPlayLoadingView(boolean show)
    {
        if(show)
            mProgressBar.setVisibility(View.VISIBLE);
        else
            mProgressBar.setVisibility(View.GONE);
    }

    public void delayHideTitle(){
        //移除消息
        /*mHandler.removeMessages(MSG_HIDE_TITLE);
        //发送一个空的延时2秒消息
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_TITLE,2000);*/
    }

    public VideoMediaController(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public VideoMediaController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }

    public VideoMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    //初始化控件
    private void initView() {
        View view = View.inflate(getContext(), R.layout.video_controller, this);
        ButterKnife.bind(this,view);

        initViewDisplay();
        //设置视频播放时的点击界面
        setOnTouchListener(onTouchListener);
        //设置SeekBar的拖动监听
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        //播放完成的界面要销毁触摸事件
        rlPlayFinish.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        //拖动的过程中调用
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        //开始拖动的时候调用
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //暂停视频的播放、停止时间和进度条的更新
            MediaHelper.pause();
            mHandler.removeMessages(MSG_UPDATE_TIME_PROGRESS);
        }

        //停止拖动时调用
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //把视频跳转到对应的位置
            int progress = seekBar.getProgress();
            if(myVideoPlayer != null && myVideoPlayer.mPlayer !=null)
            {
                int duration = myVideoPlayer.mPlayer.getDuration();
                int position = duration * progress / 100;
                myVideoPlayer.mPlayer.seekTo(position);
                //开始播放、开始时间和进度条的更新
                MediaHelper.play();
                updatePlayTimeAndProgress();
            }

        }
    };


    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //按下+已经播放了
            /*if(event.getAction() == MotionEvent.ACTION_DOWN && myVideoPlayer.hasPlay){
                //显示或者隐藏视频控制界面
                showOrHideVideoController();
            }*/
            return true;//去处理事件
        }
    };
    //显示或者隐藏视频控制界面
    public void showOrHideVideoController() {
        /*if(llPlayControl.getVisibility() == View.GONE){
            //显示（标题、播放按钮、视频进度控制）
            tvTitle.setVisibility(View.VISIBLE);
            ivPlay.setVisibility(View.VISIBLE);
            if(MediaHelper.getInstance().isPlaying())
            {
                ivPlay.setImageResource(R.drawable.new_pause_video);
            }
            else
            {
                ivPlay.setImageResource(R.drawable.new_play_video);
            }
            //加载动画
            Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_enter);
            animation.setAnimationListener(new SimpleAnimationListener(){
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    llPlayControl.setVisibility(View.VISIBLE);
                    //过2秒后自动隐藏
                    mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER,2000);
                }
            });
            //执行动画
            llPlayControl.startAnimation(animation);
        }else{
            //隐藏（标题、播放按钮、视频进度控制）
            tvTitle.setVisibility(View.GONE);
            ivPlay.setVisibility(View.GONE);
            //加载动画
            Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_exit);
            animation.setAnimationListener(new SimpleAnimationListener(){
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    llPlayControl.setVisibility(View.GONE);
                }
            });
            //执行动画
            llPlayControl.startAnimation(animation);
        }*/
    }

    //更新进度条的第二进度（缓存）
    public void updateSeekBarSecondProgress(int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    //设置播放视频的总时长
    public void setDuration(int duration) {
        String time = formatDuration(duration);
        tvTime.setText(time);
        tvUseTime.setText("00:00");
    }

    //格式化时间 00：00
    public String formatDuration(int duration){
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(duration));
    }

    //更新播放的时间和进度
    public void updatePlayTimeAndProgress() {
        //获取目前播放的进度
        if(MediaHelper.getInstance().isPlaying())
        {
            int currentPosition = MediaHelper.getInstance().getCurrentPosition();
            //格式化
            String useTime = formatDuration(currentPosition);
            tvUseTime.setText(useTime);
            //更新进度
            int duration = MediaHelper.getInstance().getDuration();
            if(duration == 0)
            {
                return;
            }
            int progress = 100*currentPosition/duration;
            seekBar.setProgress(progress);
        }
        else
            Log.e("","player has reset");

        //发送一个更新的延时消息
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME_PROGRESS,1000);
    }

    //移除所有的消息
    public void removeAllMessage(){
        mHandler.removeCallbacksAndMessages(null);
    }

    //显示视频播放完成的界面
    public void showPlayFinishView() {
        tvTitle.setVisibility(View.VISIBLE);
        rlPlayFinish.setVisibility(View.VISIBLE);
        tvCurTime.setVisibility(View.VISIBLE);
    }

    private int position;
    public void setPosition(int position) {
        this.position = position;
    }

    public void setTitle(String title)
    {
        ViewUtils.sendMessage(mHandler,UPDATE_CUR_NAME,title);
    }

    private long curServerTime = 0;
    public void showCurTime(final long time)
    {
        this.curServerTime = time;
        ViewUtils.sendMessage(mHandler,UPDATE_CUR_SERVER_TIME,time);
    }

    public long getCurServerTime()
    {
        return curServerTime;
    }

    //简单的动画监听器（不需要其他的监听器去实现多余的方法）
    private class SimpleAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    //初始化控件的显示状态
    public void initViewDisplay() {
        tvTitle.setVisibility(View.VISIBLE);
        ivPlay.setImageResource(R.drawable.new_play_video);
        tvCurTime.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        llPlayControl.setVisibility(View.VISIBLE);
        rlPlayFinish.setVisibility(View.GONE);
        ivPlay.setVisibility(View.VISIBLE);
        tvUseTime.setText("00:00");
        seekBar.setProgress(0);
        seekBar.setSecondaryProgress(0);
    }

    public void showPlayStateView(boolean isPlaying)
    {
        if(isPlaying)
        {
            ivPlay.setImageResource(R.drawable.new_pause_video);
        }
        else
            ivPlay.setImageResource(R.drawable.new_play_video);
    }

    @OnClick({R.id.iv_replay, R.id.iv_share, R.id.iv_play, R.id.iv_fullscreen})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_replay:
                //隐藏播放完成界面
                rlPlayFinish.setVisibility(View.GONE);
                //隐藏时间
                //tvCurTime.setVisibility(View.GONE);
                tvUseTime.setText("00:00");
                //进度条
                seekBar.setProgress(0);
                //把媒体播放器的位置移动到开始的位置
                MediaHelper.getInstance().seekTo(0);
                //开始播放
                MediaHelper.play();
                //延时隐藏标题
                delayHideTitle();
                break;
            case R.id.iv_share:
                break;
            case R.id.iv_play:
                if(MediaHelper.getInstance().isPlaying())
                {
                    //暂停
                    MediaHelper.pause();
                    //移除隐藏Controller布局的消息
                    mHandler.removeMessages(MSG_HIDE_CONTROLLER);
                    //移除更新播放时间和进度的消息
                    mHandler.removeMessages(MSG_UPDATE_TIME_PROGRESS);
                    ivPlay.setImageResource(R.drawable.new_play_video);
                    hasPause = true;
                }
                else {
                    if(hasPause)
                    {
                        //继续播放
                        MediaHelper.play();
                        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER,2000);
                        updatePlayTimeAndProgress();
                        hasPause = false;
                    }else {
                        //播放
                        ivPlay.setVisibility(View.GONE);
                        //tvCurTime.setVisibility(View.GONE);
                        pbLoading.setVisibility(View.VISIBLE);
                        //视频播放界面也需要显示
                        myVideoPlayer.setVideoViewVisiable(View.VISIBLE);
                    }
                    ivPlay.setImageResource(R.drawable.new_pause_video);
                }


                break;
            case R.id.iv_fullscreen:

                WindowManager windowManager = (WindowManager)mContext
                        .getSystemService(Context.WINDOW_SERVICE);
                int rotation = windowManager.getDefaultDisplay().getRotation();

                if(rotateCount == 0)
                    myVideoPlayer.getTextureView().setRotation(90.0f);
                if(rotateCount == 1)
                    myVideoPlayer.getTextureView().setRotation(180.0f);
                if(rotateCount == 2)
                    myVideoPlayer.getTextureView().setRotation(270.0f);
                if(rotateCount == 3)
                    myVideoPlayer.getTextureView().setRotation(360.0f);

                rotateCount ++;
                if(rotateCount > 3)
                    rotateCount = 0;

                break;
        }
    }

    private int rotateCount = 0;

    private VideoPlayer myVideoPlayer;
    public void setVideoPlayer(VideoPlayer myVideoPlayer) {
        this.myVideoPlayer = myVideoPlayer;
    }

    //设置视频加载进度条的显示状态
    public void setPbLoadingVisiable(int visiable) {
        pbLoading.setVisibility(visiable);
    }

    public void showPlayControl(int visiable)
    {
        llPlayControl.setVisibility(visiable);
    }

    public void showCurTime(int visiable)
    {
        tvCurTime.setVisibility(visiable);
    }
}
