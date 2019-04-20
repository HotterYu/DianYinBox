package com.znt.push.entity;

import com.znt.lib.bean.MediaInfor;

import java.util.ArrayList;
import java.util.List;

public class TimeInternalAdInfor
{

    private int internalTime = 0;

    private List<MediaInfor> adList = new ArrayList<>();

    private int playAdvIndex = 0;

    public void setInternalTime(int internalTime)
    {
        this.internalTime = internalTime;
    }
    public int getInternalTime()
    {
        return internalTime;
    }

    public void clearAd()
    {
        adList.clear();
    }

    public void addAd(MediaInfor mediaInfor)
    {
        adList.add(mediaInfor);
    }


    public void setAdList(List<MediaInfor> adList)
    {
        this.adList = adList;
    }
    public List<MediaInfor> getAdList()
    {
        return adList;
    }

    private  MediaInfor getCurPlayAdMedia()
    {

        if(adList.size() == 0)
        {
            //setLocalAdPlanTime("");
            return null;
        }

        MediaInfor curPlayAdvInfor = null;
        if(adList.size() > 0)
        {
            if(playAdvIndex >= adList.size())
                playAdvIndex = 0;
            curPlayAdvInfor = adList.get(playAdvIndex);
            curPlayAdvInfor.setMediaType(MediaInfor.MEDIA_TYPE_ADV);
            playAdvIndex ++ ;
        }
        return curPlayAdvInfor;
    }

    /**
     * 间隔一定时间获取插播广告
     * @return
     */
    private long lastServerTime = 0;
    /*public boolean isTimeToPlayAdByInternalTime(long curTime)
    {

        if(internalTime == 0 || lastServerTime == 0 || (!curPlayMediaType.equals(MediaInfor.MEDIA_TYPE_MEDIA)))
        {
            lastServerTime = internalTime;
        }
        else
        {
            long internalTime = curTime - lastServerTime;
            if(internalTime >= internalTime * 60 * 1000)
            {
                lastServerTime = curTime;
                return true;
            }
        }
        return false;
    }*/


}
