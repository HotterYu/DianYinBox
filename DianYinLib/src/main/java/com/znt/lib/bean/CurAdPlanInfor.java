package com.znt.lib.bean;

import android.text.TextUtils;

import com.znt.lib.utils.UrlUtil;

import java.io.Serializable;

/**
 * Created by prize on 2018/10/31.
 */

public class CurAdPlanInfor implements Serializable
{

    String id = "";
    String name = "";
    String planModel = "";
    String startDate = "";
    String endDate = "";
    String[] scheIds;
    String[] cycleTypes;
    String[] playModels;
    String[] startTimes;
    String[] endTimes;
    String[] musicNums;
    String[] adinfoIds;
    String[] adinfoNames;
    String[] adUrls;
    String merchId;
    String merchName;
    String groupName;
    String groupId;
    String addTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanModel() {
        return planModel;
    }

    public void setPlanModel(String planModel) {
        this.planModel = planModel;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String[] getScheIds() {
        return scheIds;
    }

    public void setScheIds(String[] scheIds) {
        this.scheIds = scheIds;
    }

    public String[] getCycleTypes() {
        return cycleTypes;
    }

    public void setCycleTypes(String[] cycleTypes) {
        this.cycleTypes = cycleTypes;
    }

    public String[] getPlayModels() {
        return playModels;
    }

    public void setPlayModels(String[] playModels) {
        this.playModels = playModels;
    }

    public String[] getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(String[] startTimes) {
        this.startTimes = startTimes;
    }

    public String[] getEndTimes() {
        return endTimes;
    }

    public void setEndTimes(String[] endTimes) {
        this.endTimes = endTimes;
    }

    public String[] getMusicNums() {
        return musicNums;
    }

    public void setMusicNums(String[] musicNums) {
        this.musicNums = musicNums;
    }

    public String[] getAdinfoIds() {
        return adinfoIds;
    }

    public void setAdinfoIds(String[] adinfoIds) {
        this.adinfoIds = adinfoIds;
    }

    public String[] getAdinfoNames() {
        return adinfoNames;
    }

    public void setAdinfoNames(String[] adinfoNames) {
        this.adinfoNames = adinfoNames;
    }

    public String[] getAdUrls() {
        return adUrls;
    }

    public void setAdUrls(String[] adUrls)
    {
        this.adUrls = adUrls;
    }

    public void decodeUrls()
    {
        int count = adUrls.length;
        for(int i=0;i<count;i++)
        {
            if(!TextUtils.isEmpty(adUrls[i]))
                adUrls[i] = UrlUtil.decodeUrl(adUrls[i]);
        }
    }

    public String getMerchId() {
        return merchId;
    }

    public void setMerchId(String merchId) {
        this.merchId = merchId;
    }

    public String getMerchName() {
        return merchName;
    }

    public void setMerchName(String merchName) {
        this.merchName = merchName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
}
