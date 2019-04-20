package com.znt.push.entity;

import com.znt.lib.bean.MediaInfor;

import java.util.List;

public class TimeingAdInfor
{

    private long pushTime = 0;

    private MediaInfor mediaInfor = null;

    public void setPushTime(long pushTime)
    {
        this.pushTime = pushTime;
    }
    public long getPushTime()
    {
        return pushTime;
    }

    public void setMediaInfor(MediaInfor mediaInfor)
    {
        this.mediaInfor = mediaInfor;
    }

    public MediaInfor getMediaInfor()
    {
        return mediaInfor;
    }

}
