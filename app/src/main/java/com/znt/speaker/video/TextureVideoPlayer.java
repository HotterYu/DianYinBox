package com.znt.speaker.video;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.TextureView;

public class TextureVideoPlayer extends TextureView{

    private String url;

    public VideoState mState;

    private int mVideoWidth;//视频宽度
    private int mVideoHeight;//视频高度

    public static final int CENTER_CROP_MODE = 1;//中心裁剪模式
    public static final int CENTER_MODE = 2;//一边中心填充模式

    public int mVideoMode = 0;

    //播放状态
    public enum VideoState{
        init,palying,pause
    }
    public TextureVideoPlayer(Context context) {
        super(context);
    }

    public TextureVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextureVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     *
     * @param mode Pass {@link #CENTER_CROP_MODE} or {@link #CENTER_MODE}. Default
     * value is 0.
     */
    public void updateTextureViewSize(int mode){
        /*if (mode==CENTER_MODE){
            updateTextureViewSizeCenter();
        }else if (mode == CENTER_CROP_MODE){
            updateTextureViewSizeCenterCrop();
        }*/
    }

    //重新计算video的显示位置，裁剪后全屏显示
    public void updateTextureViewSizeCenterCrop(int mVideoWidth, int mVideoHeight){

        float sx = (float) getWidth() / (float) mVideoWidth;
        float sy = (float) getHeight() / (float) mVideoHeight;

        Matrix matrix = new Matrix();
        float maxScale = Math.max(sx, sy);

        //第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((getWidth() - mVideoWidth) / 2, (getHeight() - mVideoHeight) / 2);

        //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(mVideoWidth / (float) getWidth(), mVideoHeight / (float) getHeight());

        //第3步,等比例放大或缩小,直到视频区的一边超过View一边, 另一边与View的另一边相等. 因为超过的部分超出了View的范围,所以是不会显示的,相当于裁剪了.
        matrix.postScale(maxScale, maxScale, getWidth() / 2, getHeight() / 2);//后两个参数坐标是以整个View的坐标系以参考的

        setTransform(matrix);
        postInvalidate();
    }

    //重新计算video的显示位置，让其全部显示并据中
    public void updateTextureViewSizeCenter(int mVideoWidth, int mVideoHeight )
    {

        float sx = (float) getWidth() / (float) mVideoWidth;
        float sy = (float) getHeight() / (float) mVideoHeight;

        Matrix matrix = new Matrix();

        //第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((getWidth() - mVideoWidth) / 2, (getHeight() - mVideoHeight) / 2);

        //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(mVideoWidth / (float) getWidth(), mVideoHeight / (float) getHeight());

        //第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
        if (sx >= sy){
            matrix.postScale(sy, sy, getWidth() / 2, getHeight() / 2);
        }else{
            matrix.postScale(sx, sx, getWidth() / 2, getHeight() / 2);
        }

        setTransform(matrix);
        postInvalidate();
    }

}