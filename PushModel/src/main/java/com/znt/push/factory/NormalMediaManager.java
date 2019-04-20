package com.znt.push.factory;

import android.util.Log;

import com.znt.lib.bean.CurPlanSubInfor;
import com.znt.lib.bean.MediaInfor;
import com.znt.lib.utils.DateUtils;
import com.znt.push.db.DBMediaHelper;

import java.util.ArrayList;
import java.util.List;

public class NormalMediaManager
{

    private List<MediaInfor> curPlayMedias = new ArrayList<>();

    private String localPlanTime = "";
    private int curPlayMediaIndex = 0;

    public void setLocalPlanTime(String localPlanTime)
    {
        this.localPlanTime = localPlanTime;
    }

    public void clear()
    {
        curPlayMedias.clear();
        curSubPlanInfos.clear();
        DBMediaHelper.getInstance().deleteCurplan();
    }

    public int getCurPlayMediasSize()
    {
        if(curPlayMedias == null)
            return 0;
        return curPlayMedias.size();
    }

    public MediaInfor getPlayMedia()
    {
        if(curPlayMedias == null || curPlayMedias.size() == 0)
        {
            setLocalPlanTime("");
        }

        MediaInfor curPlayMediaInfor = null;

        if(curPlayMedias.size() > 0)
        {
            if(curPlayMediaIndex >= curPlayMedias.size())
                curPlayMediaIndex = 0;
            curPlayMediaInfor = curPlayMedias.get(curPlayMediaIndex);
            curPlayMediaInfor.setMediaType(MediaInfor.MEDIA_TYPE_MEDIA);
            curPlayMediaInfor.setCurPosition(curPlayMediaIndex);
            curPlayMediaIndex ++ ;
        }

        return curPlayMediaInfor;
    }

    public void fillPlayList(List<MediaInfor> tempList)
    {
        curPlayMedias.clear();
        curPlayMedias.addAll(tempList);
    }

    public List<MediaInfor> getCurPlayMedias()
    {
        return curPlayMedias;
    }


    private List<CurPlanSubInfor> curSubPlanInfos = new ArrayList<>();

    public boolean isCurTimeHasPlan(long curTime)
    {
        if(curPlayMedias == null)
            curPlayMedias = new ArrayList<>();
        if(curTime <= 0)
        {
            curPlayMedias.clear();
            return false;
        }
        if(curSubPlanInfos == null || curSubPlanInfos.size() == 0)
            curSubPlanInfos = DBMediaHelper.getInstance().getCurSubPlanInfors();

        if(curSubPlanInfos == null || curSubPlanInfos.size() <= 0)
        {
            curPlayMedias.clear();
            return false;
        }

        boolean result = false;
        String curWeek = DateUtils.getWeekBylong(curTime) + "";

        int count = curSubPlanInfos.size();
        for(int i=0;i<count;i++)
        {
            CurPlanSubInfor tempInfo = curSubPlanInfos.get(i);

            String startTime = tempInfo.getStartTime();
            String endTime = tempInfo.getEndTime();
            String tempCycleType = tempInfo.getWeek();
            long sLong = DateUtils.timeToInt(startTime, ":");
            long eLong = DateUtils.timeToInt(endTime, ":");

            String curTimeShort = DateUtils.getEndDateFromLong(curTime);
            long curTimeShortLong = DateUtils.timeToInt(curTimeShort, ":");

            if(!tempCycleType.equals("0") && !tempCycleType.equals(curWeek))
            {
                continue;
            }
            if(isTimeOverlap(sLong, eLong, curTimeShortLong))
            {
                result = true;
                break;
            }
        }
        return result;
    }
    /**
     *获取当前时段内的播放列表
     * @param curTime
     * @return
     */
    private boolean isUpdatePlanMediasRunning = false;
    public void updatePlayPlanMedias(final long curTime, final String planId)
    {
        if(curTime == 0)
        {
            curPlayMedias.clear();
            return;
        }

        if(isUpdatePlanMediasRunning)
            return;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    isUpdatePlanMediasRunning = true;
                    if(curSubPlanInfos == null || curSubPlanInfos.size() == 0)
                        curSubPlanInfos = DBMediaHelper.getInstance().getCurSubPlanInfors();
                    if(curSubPlanInfos == null || curSubPlanInfos.size() <= 0)
                    {
                        curPlayMedias.clear();
                        isUpdatePlanMediasRunning = false;
                        return ;
                    }
                    String curWeek = DateUtils.getWeekBylong(curTime) + "";
                    int count = curSubPlanInfos.size();
                    for(int i=0;i<count;i++)
                    {
                        CurPlanSubInfor tempInfo = curSubPlanInfos.get(i);
                        String tempCycleType = tempInfo.getWeek();
                        String curTimeShort = DateUtils.getEndDateFromLong(curTime);
                        long curTimeShortLong = DateUtils.timeToInt(curTimeShort, ":");
                        if(!tempCycleType.equals("0") && !tempCycleType.equals(curWeek))
                        {
                            continue;
                        }

                        String scheId = tempInfo.getScheId();
                        String startTime = tempInfo.getStartTime();
                        String endTime = tempInfo.getEndTime();
                        long sLong = DateUtils.timeToInt(startTime, ":");
                        long eLong = DateUtils.timeToInt(endTime, ":");
                        if(isTimeOverlap(sLong, eLong, curTimeShortLong))
                        {
                            if(!localPlanTime.equals(startTime))
                            {
                                curPlayMedias = DBMediaHelper.getInstance().getCurPlayMedias(planId, scheId, startTime, endTime, tempCycleType);

                                setLocalPlanTime(startTime);
                            }
                        }
                    }
                    isUpdatePlanMediasRunning = false;

                } catch (Exception e) {
                    e.printStackTrace();
                    isUpdatePlanMediasRunning = false;
                }
            }
        }).start();
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
