package com.znt.push.factory;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.znt.lib.bean.CurAdPlanInfor;
import com.znt.lib.bean.MediaInfor;
import com.znt.lib.utils.DateUtils;
import com.znt.push.db.DBMediaHelper;
import com.znt.push.entity.LocalDataEntity;
import com.znt.push.entity.TimeingAdInfor;

import java.util.List;

public class CurPlayMediaManager extends BasePlanFactory
{

    private final String TAG = "CurPlayMediaManager";

    private volatile long curServerTime = 0;

    private CurAdPlanInfor mAdPlanInfor = null;

    private TimeingAdManager mTimeingAdManager = null;
    private TimeInternalAdManager mTimeInternalAdManager = null;
    private CountInternalAdManager mCountInternalAdManager = null;
    private NormalMediaManager mNormalMediaManager = null;

    public CurPlayMediaManager()
    {
        mTimeingAdManager = new TimeingAdManager();
        mTimeInternalAdManager = new TimeInternalAdManager();
        mCountInternalAdManager = new CountInternalAdManager();
        mNormalMediaManager = new NormalMediaManager();

        mCountInternalAdManager.setNormalMediaManager(mNormalMediaManager);
    }

    public void updateCurServerTime(long curServerTime)
    {
        this.curServerTime = curServerTime;
    }

    public void setCurPlayMediaType(String mediaType)
    {
        mTimeInternalAdManager.setCurPlayMediaType(mediaType);
        mCountInternalAdManager.setCurPlayMediaType(mediaType);
    }

    public void resetMediaPlanLocalStatus()
    {
        mNormalMediaManager.setLocalPlanTime("");
    }


    /**
     * 保存广告计划
     * @param mAdPlanInfor
     */
    public void setAdPlanInfor(CurAdPlanInfor mAdPlanInfor)
    {
        this.mAdPlanInfor = mAdPlanInfor;
    }

    public CurAdPlanInfor getAdPlanInfor()
    {
        try
        {
            if(mAdPlanInfor == null)
                mAdPlanInfor = DBMediaHelper.getInstance().getCurAdPlanInfor().get(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return mAdPlanInfor;
    }

    public boolean isCurPlanNone()
    {
        return !mNormalMediaManager.isCurTimeHasPlan(curServerTime);
    }

    public void fillPlayList(List<MediaInfor> tempList)
    {
        mNormalMediaManager.fillPlayList(tempList);
    }

    public int getCurPlayMediaSize()
    {
        return mNormalMediaManager.getCurPlayMediasSize();
    }
    public int getCurAdPlayMediaSize()
    {
        return mCountInternalAdManager.getAdList().size();
    }

    public boolean checkTimeingPushAd()
    {
        return mTimeingAdManager.isTimeToPush(curServerTime);
    }
    public boolean checkTimeInternalPushAd()
    {
        if(isTimeInternalEnable)
            return mTimeInternalAdManager.isTimeToPlayAdByInternalTime(curServerTime);
        else
            return false;
    }

    public MediaInfor getCurPlayMedia()
    {
        MediaInfor tempMediaInfor = null;

        if(isCountInternalEnable)
        {
            tempMediaInfor = mCountInternalAdManager.getCurPlayAdMedia();
        }

        if(tempMediaInfor == null && mNormalMediaManager.isCurTimeHasPlan(curServerTime))
        {
            tempMediaInfor = mNormalMediaManager.getPlayMedia();
        }
        return tempMediaInfor;
    }

    public void clearNormalPlan()
    {
        mNormalMediaManager.clear();
    }
    public void clearAdPlan()
    {
        mAdPlanInfor = null;
        mCountInternalAdManager.clear();
        mTimeingAdManager.clear();
        mTimeInternalAdManager.clear();
    }

    public MediaInfor getCurTimeingAd()
    {
        return mTimeingAdManager.getCurPushMedia();
    }

    public MediaInfor getCurTimeInternalAd()
    {
        return mTimeInternalAdManager.getCurPlayAdMedia();
    }

    public void updateCurAllMedias(Context context)
    {
        updatePlayPlanMedias(context);
        updateAdPlanMedias(context);
    }

    public void updatePlayPlanMedias(Context context)
    {
        String planId = LocalDataEntity.newInstance(context).getPlanId();
        mNormalMediaManager.updatePlayPlanMedias(curServerTime, planId);
    }

    private boolean isTimeInternalEnable = false;
    private boolean isCountInternalEnable = false;

    /**
     * 根据当前时间，获取广告播放计划
     * @return
     */
    private boolean isCurAdPlanMediaUpdateRunning = false;
    public void updateAdPlanMedias(Context context)
    {
        if(isCurAdPlanMediaUpdateRunning)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    isCurAdPlanMediaUpdateRunning = true;
                    CurAdPlanInfor tempAdPlanInfor = getAdPlanInfor();
                    if(tempAdPlanInfor == null)
                    {
                        isCurAdPlanMediaUpdateRunning = false;
                        return;
                    }

                    isTimeInternalEnable = false;
                    isCountInternalEnable = false;

                    String[] playModelArray = tempAdPlanInfor.getPlayModels();
                    String[] scheIdsArray = tempAdPlanInfor.getScheIds();
                    String[] cycleTypesArray = tempAdPlanInfor.getCycleTypes();
                    String[] startTimesArray = tempAdPlanInfor.getStartTimes();
                    String[] endTimesArray = tempAdPlanInfor.getEndTimes();

                    String[] adUrlsArray = tempAdPlanInfor.getAdUrls();
                    String[] musicNumsArray = tempAdPlanInfor.getMusicNums();
                    String[] adinfoIdsArray = tempAdPlanInfor.getAdinfoIds();
                    String[] adinfoNamesArray = tempAdPlanInfor.getAdinfoNames();

                    mTimeingAdManager.clearAd();
                    mTimeInternalAdManager.clearAd();
                    mCountInternalAdManager.clearAd();

                    int count = scheIdsArray.length;
                    for(int i=0;i<count;i++)
                    {
                        String tempCycleType = cycleTypesArray[i];

                        String playModel = playModelArray[i];

                        String curWeek = DateUtils.getWeekBylong(curServerTime) + "";
                        if(!tempCycleType.equals("0") && !tempCycleType.equals(curWeek))
                        {
                            //不在时间范围内
                            continue ;
                        }

                        String tempScheId = scheIdsArray[i];
                        String startTime = startTimesArray[i];
                        String endTime = endTimesArray[i];

                        long sLong = DateUtils.timeToInt(startTime, ":");
                        long eLong = DateUtils.timeToInt(endTime, ":");

                        String curTimeShort = DateUtils.getEndDateFromLong(curServerTime);
                        long curTimeShortLong = DateUtils.timeToInt(curTimeShort, ":");
                        if(playModel.equals("2") && (sLong == eLong))
                        {
                            //定时插播的
                            TimeingAdInfor mTimeingAdInfor = new TimeingAdInfor();

                            String[] urls = adUrlsArray[i].split(";");
                            String[] names = adinfoNamesArray[i].split(";");
                            String[] adIds = adinfoIdsArray[i].split(";");
                            int adCount = urls.length;

                            for(int j=0;j<adCount;j++)
                            {
                                MediaInfor tempMedia = new MediaInfor();
                                tempMedia.setMediaUrl(urls[j]);
                                tempMedia.setMediaName(names[j]);
                                tempMedia.setMediaType(MediaInfor.MEDIA_TYPE_ADV);
                                tempMedia.setMediaId(adIds[j]);
                                mTimeingAdInfor.setMediaInfor(tempMedia);
                            }
                            mTimeingAdInfor.setPushTime(sLong);
                            mTimeingAdManager.addAd(mTimeingAdInfor);
                        }
                        else if((playModel.equals("3")) && isTimeOverlap(sLong, eLong,curTimeShortLong))
                        {

                            isTimeInternalEnable = true;

                            String[] urls = adUrlsArray[i].split(";");
                            String[] names = adinfoNamesArray[i].split(";");
                            String[] adIds = adinfoIdsArray[i].split(";");
                            String internal = musicNumsArray[i];
                            int internalInt = 0;
                            if(!TextUtils.isEmpty(internal))
                                internalInt = Integer.parseInt(internal);

                            mTimeInternalAdManager.setInternalTime(internalInt);

                            int adCount = urls.length;
                            for(int j=0;j<adCount;j++)
                            {
                                MediaInfor tempMedia = new MediaInfor();
                                tempMedia.setMediaUrl(urls[j]);
                                tempMedia.setMediaName(names[j]);
                                tempMedia.setMediaType(MediaInfor.MEDIA_TYPE_ADV);
                                tempMedia.setMediaId(adIds[j]);
                                mTimeInternalAdManager.addAd(tempMedia);

                            }
                        }
                        else if((playModel.equals("1")) && isTimeOverlap(sLong, eLong,curTimeShortLong))
                        {

                            isCountInternalEnable = true;

                            String[] urls = adUrlsArray[i].split(";");
                            String[] names = adinfoNamesArray[i].split(";");
                            String[] adIds = adinfoIdsArray[i].split(";");
                            String internal = musicNumsArray[i];
                            int internalInt = 0;
                            if(!TextUtils.isEmpty(internal))
                                internalInt = Integer.parseInt(internal);

                            mCountInternalAdManager.setInternalCount(internalInt);

                            int adCount = urls.length;
                            for(int j=0;j<adCount;j++)
                            {
                                MediaInfor tempMedia = new MediaInfor();
                                tempMedia.setMediaUrl(urls[j]);
                                tempMedia.setMediaName(names[j]);
                                tempMedia.setMediaType(MediaInfor.MEDIA_TYPE_ADV);
                                tempMedia.setMediaId(adIds[j]);
                                mCountInternalAdManager.addAd(tempMedia);
                            }
                        }
                    }
                    mTimeingAdManager.fillAd();
                    mTimeInternalAdManager.fillAd();
                    mCountInternalAdManager.fillAd();

                    isCurAdPlanMediaUpdateRunning = false;
                } catch (Exception e) {
                    isCurAdPlanMediaUpdateRunning = false;
                    e.printStackTrace();
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
    protected boolean isTimeOverlap(long start, long end, long dest)
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

    public List<MediaInfor> getCurPlayMedias()
    {
        return mNormalMediaManager.getCurPlayMedias();
    }

}
