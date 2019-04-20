package com.znt.push.factory;

import android.text.TextUtils;
import android.util.Log;

import com.znt.lib.bean.AdPlanInfor;
import com.znt.lib.bean.MediaInfor;
import com.znt.lib.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class BasePlanFactory
{

    public volatile  int max_internal_count = 0;
    public volatile  int max_internal_time = 0;
    public volatile  List<Long> pushTimes = new ArrayList<>();
    public volatile int curPlayMode = -1;//1 间隔歌曲  2 定时  3,间隔时间

    private String localPlanTime = "";
    private String localAdPlanTime = "";
    public void setLocalPlanTime(String localPlanTime)
    {
        this.localPlanTime = localPlanTime;
    }
    public void setLocalAdPlanTime(String localAdPlanTime)
    {
        this.localAdPlanTime = localAdPlanTime;
    }


    /**
     * 根据当前时间，获取广告播放计划
     * @param curTime
     * @return
     */
    public List<MediaInfor> getCurAdMedias(AdPlanInfor mAdPlanInfor , long curTime)
    {

        List<MediaInfor> medias = new ArrayList<>();
        String[] playModelArray = mAdPlanInfor.getPlayModelsArray();
        String[] scheIdsArray = mAdPlanInfor.getScheIdsArray();
        String[] cycleTypesArray = mAdPlanInfor.getCycleTypesArray();
        String[] startTimesArray = mAdPlanInfor.getStartTimesArray();
        String[] endTimesArray = mAdPlanInfor.getEndTimesArray();

        String[] adUrlsArray = mAdPlanInfor.getAdUrlsArray();
        String[] musicNumsArray = mAdPlanInfor.getMusicNumsArray();
        String[] adinfoIdsArray = mAdPlanInfor.getAdinfoIdsArray();
        String[] adinfoNamesArray = mAdPlanInfor.getAdinfoNamesArray();

        pushTimes.clear();

        int count = scheIdsArray.length;
        for(int i=0;i<count;i++)
        {
            String tempCycleType = cycleTypesArray[i];

            String curWeek = DateUtils.getWeekBylong(curTime) + "";
            if(!tempCycleType.equals("0") && !tempCycleType.equals(curWeek))
            {
                return null;
            }

            String tempScheId = scheIdsArray[i];
            String startTime = startTimesArray[i];
            String endTime = endTimesArray[i];

            long sLong = DateUtils.timeToInt(startTime, ":");
            long eLong = DateUtils.timeToInt(endTime, ":");

            String curTimeShort = DateUtils.getEndDateFromLong(curTime);
            long curTimeShortLong = DateUtils.timeToInt(curTimeShort, ":");
            if(sLong == eLong)
            {
                pushTimes.add(sLong);
            }
            else if(isTimeOverlap(sLong, eLong, curTimeShortLong))
            {
                if(!localAdPlanTime.equals(startTime))
                {
                    if(!TextUtils.isEmpty(playModelArray[i]))
                        curPlayMode = Integer.parseInt(playModelArray[i]);
                    //curPlayMode = 3;
                    if(curPlayMode == 1)//间隔歌曲
                    {
                        max_internal_count = Integer.parseInt(musicNumsArray[i]);
                    }
                    else if(curPlayMode == 2)//定时插播
                    {
                        pushTimes.add(sLong);
                    }
                    else if(curPlayMode == 3)//间隔时间插播
                    {
                        max_internal_time = Integer.parseInt(musicNumsArray[i]);
                    }
                }
            }
            else
                return null;

            String[] urls = adUrlsArray[i].split(";");
            String[] names = adinfoNamesArray[i].split(";");
            int adCount = urls.length;
            for(int j=0;j<adCount;j++)
            {
                MediaInfor tempMedia = new MediaInfor();
                tempMedia.setMediaUrl(urls[j]);
                tempMedia.setMediaName(names[j]);
                tempMedia.setMediaType(MediaInfor.MEDIA_TYPE_ADV);
                medias.add(tempMedia);
            }

            setLocalAdPlanTime(startTime);
        }
        return medias;
    }


    /**
     * 检查时间是否在该时段内
     * @param start
     * @param end
     * @param dest
     * @return
     */
    private boolean isTimeOverlap(long start, long end, long dest)
    {
        if(start > end)
        {
            end = end + 24 * 60 * 60;
            if(dest > start)
            {
                if(dest < end)
                {
                    Log.d("", "");
                    return true;
                }
            }
            else
            {
                dest = dest + 24 * 60 * 60;
                if(dest < end)
                {
                    Log.d("", "");
                    return true;
                }
            }
        }
        else
        {
            if(dest > start && dest < end)
                return true;
        }

        return false;
    }


}
