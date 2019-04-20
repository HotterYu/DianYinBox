package com.znt.push.factory;

import com.znt.lib.bean.MediaInfor;
import com.znt.lib.utils.DateUtils;
import com.znt.push.entity.TimeingAdInfor;

import java.util.List;
import java.util.Vector;

public class TimeingAdManager extends BaseAdManager
{

    private TimeingAdInfor curTimeingAdInfor = null;
    private Vector<TimeingAdInfor> adList = new Vector<>();
    private Vector<TimeingAdInfor> tempList = new Vector<>();

    public void clear()
    {
        curTimeingAdInfor = null;
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

    public void addAd(TimeingAdInfor mTimeingAdInfor)
    {
        tempList.add(mTimeingAdInfor);
    }

    public List<TimeingAdInfor> getAdList()
    {
        return adList;
    }

    public boolean isTimeToPush(long curTime)
    {

        curServerTime = curTime;

        boolean result = false;
        for(int i=0;i<adList.size();i++)
        {
            if(adList.size() == 0 || i >= adList.size())
            {
                break;
            }
            long curTimeShort = DateUtils.getSecFromTime(curServerTime);
            TimeingAdInfor tempTiInfo = adList.get(i);
            if(curTimeShort == tempTiInfo.getPushTime())
            {
                result = true;
                curTimeingAdInfor = tempTiInfo;
                break;
            }
        }
        return result;
    }

    public MediaInfor getCurPushMedia()
    {
        if(curTimeingAdInfor == null)
            return null;
        return curTimeingAdInfor.getMediaInfor();
        /*MediaInfor mediaInfor = null;
        int count = adList.size();
        for(int i=0;i<count;i++)
        {
            long curTimeShort = DateUtils.getSecFromTime(curServerTime);
            if(curTimeShort == adList.get(i).getPushTime())
            {
                mediaInfor = adList.get(i).getMediaInfor();
                break;
            }
        }
        return mediaInfor;*/
    }


}
