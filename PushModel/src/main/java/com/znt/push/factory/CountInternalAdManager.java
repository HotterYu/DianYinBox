package com.znt.push.factory;

import com.znt.lib.bean.MediaInfor;
import com.znt.push.db.DBMediaHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CountInternalAdManager extends BaseAdManager
{
    private String localAdPlanTime = "";
    private String curPlayMediaType = "";
    private int curPlayMode = -1;

    private int max_internal_count = 0;

    private NormalMediaManager mNormalMediaManager = null;
    public void setNormalMediaManager(NormalMediaManager mNormalMediaManager)
    {
        this.mNormalMediaManager = mNormalMediaManager;
    }

    private List<MediaInfor> adList = new ArrayList<>();
    private Vector<MediaInfor> tempList = new Vector<>();

    private int playAdvIndex = 0;

    public void setInternalCount(int internalTime)
    {
        this.max_internal_count = internalTime;
    }
    public int getInternalCount()
    {
        return max_internal_count;
    }

    public void clear()
    {
        clearAd();
        adList.clear();
        DBMediaHelper.getInstance().deleteCurAdPlan();
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
        if(!isTimeToPlayAdvByInternalCount() && (mNormalMediaManager.getCurPlayMediasSize() != 0))
            return null;

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

    private int internalCount = 0;
    private boolean isTimeToPlayAdvByInternalCount()
    {
        /*if(curPlayAdvs.size() == 0)
            return false;*/

        if(internalCount >= max_internal_count)
        {
            internalCount = 0;
            return true;
        }
        else
            internalCount ++;
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
