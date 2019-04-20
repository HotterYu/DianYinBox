/*
package com.znt.speaker.factory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.anbetter.xplayer.ijk.XVideoView;
import com.znt.lib.bean.MediaInfor;
import com.znt.lib.utils.CacheDirUtils;
import com.znt.speaker.R;
import com.znt.speaker.timer.CheckDelayTimer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import service.ZNTDownloadServiceManager;
import service.ZNTPushServiceManager;

*/
/**
 * Created by prize on 2018/12/12.
 *//*


public class PlayFactory
{

    private Activity mContext = null;

    private XVideoView mVideoPlayer = null;

    private ZNTDownloadServiceManager mZNTDownloadServiceManager = null;
    private ZNTPushServiceManager mZNTPushServiceManager;

    private List<MediaInfor> curPlayList = new ArrayList<>();
    private List<MediaInfor> curPushList = new ArrayList<>();

    private MediaInfor curPauseMedia = null;
    private MediaInfor curPlayMedia = null;

    private CheckDelayTimer mCheckDelayTimer = null;

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

    public PlayFactory(Activity mContext)
    {
        this.mContext = mContext;
    }

    public void setVideoPlayer(XVideoView videoPlayer)
    {
        this.mVideoPlayer = videoPlayer;
        mVideoPlayer.setOnPlayResultListener(new XVideoView.OnPlayResultListener() {
            @Override
            public void onPlayError(int what, int extra)
            {
                updatePushParams(mContext.getResources().getString(R.string.media_load_fail),true);
                playNext();
            }

            @Override
            public void onPrepared() {
                updatePushParams(null,true);

                //播放完插播后，重新从暂停的地方开始播放
                if(curPlayMedia != null && curPlayMedia.isMedia() && curPlayMedia.getCurSeek() > 0)
                    mVideoPlayer.seekTo(curPlayMedia.getCurSeek());
            }

            @Override
            public void onPlayFinish()
            {
                playNext();
            }
        });

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

    public void playNext()
    {

        MediaInfor tempInfor = null;

        tempInfor = getLastPauseMedia();//首先从上次暂停的获取
        if(tempInfor == null)
            tempInfor = getPushMedia();//没有的话再从push中的获取
        else
            setCurPauseMedia(null);

        if(tempInfor == null)
            tempInfor = mZNTPushServiceManager.getCurPlayMedia();//正常播放列表也没有就从Service中获取
        if(tempInfor == null)
        {
            //没有可播放的歌曲
            return;
        }

        if(isCurMediaCanPlay(tempInfor))
        {
            play(tempInfor);
        }
    }

    private void play(MediaInfor tempInfor)
    {
        curPlayMedia = tempInfor;

        mVideoPlayer.setVideoPath(getMusicPlayUrL(tempInfor),tempInfor.getMediaName());
        mVideoPlayer.reset();
        mVideoPlayer.play();
    }

    public void startPlayImages(List<String> mediaList)
    {

        */
/*String url1 = "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=52867414,535344210&fm=26&gp=0.jpg";

        String url2 = "https://car3.autoimg.cn/cardfs/product/g1/M09/DD/10/1024x0_1_q87_autohomecar__ChsEj1uV-5WAS7mQAAlWgLzIqOw323.jpg";

        String url3 = "https://car2.autoimg.cn/cardfs/product/g3/M06/A8/D8/1024x0_1_q87_autohomecar__ChsEm1wONyeAanfLAAfCnjBQ4-U395.jpg";

        String url4 = "https://car2.autoimg.cn/cardfs/product/g26/M0B/7C/A9/1024x0_1_q87_autohomecar__wKgHHVtqg3SAYeG6AA846nFGHnI473.jpg";*//*
*/
/*

        List<String> mediaList = new ArrayList<>();
        mediaList.add(url1);
        mediaList.add(url2);
        mediaList.add(url3);
        mediaList.add(url4);*//*

        mVideoPlayer.startPlayImages(mediaList);
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
        play(mediaInfor);
    }

    private void setCurPauseMedia(MediaInfor pauseMediaInfor)
    {
        this.curPauseMedia = pauseMediaInfor;
    }

    private boolean isCurMediaCanPlay(MediaInfor mediaInfor)
    {
        if(mediaInfor == null)
            return false;

        if(mVideoPlayer.isPlaying())
        {
            if(!curPlayMedia.isMedia())
                return false;//当前播放的是插播或者广告
            else//当前播放的是正常的
            {
                if(mediaInfor.isMedia())//现在要播放的是正常的
                    return false;
                else
                    return true;//现在要播放的是插播或广告
            }
        }
        else
            return true;
    }

    public void addPushMedias(List<MediaInfor> tempList)
    {

        this.curPushList.clear();
        this.curPushList.addAll(tempList);

        if(this.curPushList.size()> 0)
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
        if(curPushList.size() == 0)
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
            //String filePath2 = CacheDirUtils.getINSTANCE().getDownLoadDir() + File.separator + fileName;
            File file = new File(CacheDirUtils.getINSTANCE().getDownLoadDir() + File.separator + fileName);
            if(file.exists())
            {
                return file.getAbsolutePath();
            }
            else
            {
                File file1 = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
                if(file1.exists())
                    return file1.getAbsolutePath();
                else
                    mZNTDownloadServiceManager.addSonginfor(songInfor);
            }
        }

        return songInfor.getMediaUrl();
    }

    public void updatePushParams(String msg,boolean updateNow)
    {
        if(isPlaying())
        {
            if(curPlayMedia == null)
                curPlayMedia = new MediaInfor();
            if(TextUtils.isEmpty(msg))
                msg = curPlayMedia.getMediaName();
            mZNTPushServiceManager.putRequestParams(curPlayMedia, updateNow);
        }
        else
            mZNTPushServiceManager.putRequestParams(null, updateNow);
    }

    public boolean isPlaying()
    {
        if(mVideoPlayer == null)
            return false;
        return mVideoPlayer.isPlaying();
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
*/
/*		if (pos == 0 || pos != lastPos)
		{
			return false;
		}
*//*

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

    public void stopPlayByForce()
    {

            stopPlay();
    }

    public void stopPlay()
    {
        if( mVideoPlayer != null)
        {
            mVideoPlayer.stop();
        }
    }
}*/
