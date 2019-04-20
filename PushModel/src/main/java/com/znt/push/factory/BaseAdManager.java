package com.znt.push.factory;

import android.util.Log;

import com.znt.lib.bean.AdPlanInfor;

public class BaseAdManager
{

    protected long curServerTime = 0;

    /*protected AdPlanInfor mAdPlanInfor = null;

    public void setAdPlanInfor(AdPlanInfor mAdPlanInfor)
    {
        this.mAdPlanInfor = mAdPlanInfor;
    }*/


    public void updateServerTime(long curTime)
    {
        this.curServerTime = curTime;
    }





}
