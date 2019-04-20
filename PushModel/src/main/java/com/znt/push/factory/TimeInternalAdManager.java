package com.znt.push.factory;

import com.znt.lib.bean.MediaInfor;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TimeInternalAdManager extends BaseAdManager
{
    private String localAdPlanTime = "";
    private String curPlayMediaType = "";
    private int curPlayMode = -1;

    private int internalTime = 0;

    private List<MediaInfor> adList = new ArrayList<>();
    private Vector<MediaInfor> tempList = new Vector<>();

    private int playAdvIndex = 0;

    public void setInternalTime(int internalTime)
    {
        this.internalTime = internalTime;
    }
    public int getInternalTime()
    {
        return internalTime;
    }

    public void clear()
    {
        clearAd();
        adList.clear();
    }

    public void clearAd()
    {
        tempList.clear();
    }

    public void fillAd()
    {
        adList.clear();
        adList.addAll(tempList);
    }

    public void addAd(MediaInfor mediaInfor)
    {
        tempList.add(mediaInfor);
    }


    public void setAdList(List<MediaInfor> adList)
    {
        this.adList = adList;
    }
    public List<MediaInfor> getAdList()
    {
        return adList;
    }

    public  MediaInfor getCurPlayAdMedia()
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
    public boolean isTimeToPlayAdByInternalTime(long curTime)
    {

        if(internalTime == 0 || lastServerTime == 0 || (!curPlayMediaType.equals(MediaInfor.MEDIA_TYPE_MEDIA)))
        {
            lastServerTime = curTime;
        }
        else
        {
            long tempTime = curTime - lastServerTime;
            if(tempTime >= internalTime * 60 * 1000)
            {
                lastServerTime = curTime;
                return true;
            }
        }
        return false;
    }


    public void setCurPlayMediaType(String curPlayMediaType)
    {
        this.curPlayMediaType = curPlayMediaType;
    }

    public void setLocalAdPlanTime(String localAdPlanTime)
    {
        this.localAdPlanTime = localAdPlanTime;
    }

}
