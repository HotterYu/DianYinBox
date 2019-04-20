package com.znt.lib.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

@Table(name = "ad_plan_infor")
public class AdPlanInfor implements Serializable
{
    @Column(name = "id", isId = true, autoGen = true)
    public int id;
    @Column(name = "ad_id")
    private String ad_id = "";
    @Column(name = "name")
    private String name = "";
    @Column(name = "plan_model")
    private String plan_model = "";
    @Column(name = "start_date")
    private String start_date = "";
    @Column(name = "end_date")
    private String end_date = "";
    @Column(name = "sche_ids")
    private String sche_ids = null;
    @Column(name = "cycle_types")
    private String cycle_types = null;
    @Column(name = "play_models")
    private String play_models = null;
    @Column(name = "start_times")
    private String start_times = null;
    @Column(name = "end_times")
    private String end_times = null;
    @Column(name = "music_nums")
    private String music_nums = null;
    @Column(name = "adinfo_ids")
    private String adinfo_ids = null;
    @Column(name = "adinfo_names")
    private String adinfo_names = null;
    @Column(name = "ad_urls")
    private String ad_urls = null;
    @Column(name = "merch_id")
    private String merch_id = "";
    @Column(name = "merch_name")
    private String merch_name = "";
    @Column(name = "group_name")
    private String group_name = "";
    @Column(name = "group_id")
    private String group_id = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdId() {
        return ad_id;
    }

    public void setAdId(String ad_id)
    {
        this.ad_id = ad_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanModel() {
        return plan_model;
    }
    /*public String[] getPlanModelArray()
    {
        return removeTags(planModel).split(",");
    }*/
    public void setPlanModel(String planModel) {
        this.plan_model = planModel;
    }

    public String getStartDate() {
        return start_date;
    }

    public void setStartDate(String startDate) {
        this.start_date = startDate;
    }

    public String getEndDate() {
        return end_date;
    }

    public void setEndDate(String endDate) {
        this.end_date = endDate;
    }

    public String getScheIds() {
        return sche_ids;
    }
    public String[] getScheIdsArray()
    {
        return removeTags(sche_ids).split(",");
    }
    public void setScheIds(String scheIds) {
        this.sche_ids = scheIds;
    }

    public String getCycleTypes() {
        return cycle_types;
    }
    public String[] getCycleTypesArray()
    {
        return removeTags(cycle_types).split(",");
    }

    public void setCycleTypes(String cycleTypes) {
        this.cycle_types = cycleTypes;
    }

    public String getPlayModels() {
        return play_models;
    }
    public String[] getPlayModelsArray()
    {
        return removeTags(play_models).split(",");
    }
    public void setPlayModels(String playModels) {
        this.play_models = playModels;
    }

    public String getStartTimes() {
        return start_times;
    }
    public String[] getStartTimesArray()
    {
        return removeTags(start_times).split(",");
    }

    public void setStartTimes(String startTimes) {
        this.start_times = startTimes;
    }

    public String getEndTimes() {
        return end_times;
    }
    public String[] getEndTimesArray()
    {
        return removeTags(end_times).split(",");
    }

    public void setEndTimes(String endTimes) {
        this.end_times = endTimes;
    }

    public String getMusicNums() {
        return music_nums;
    }
    public String[] getMusicNumsArray()
    {
        return removeTags(music_nums).split(",");
    }
    public void setMusicNums(String musicNums) {
        this.music_nums = musicNums;
    }

    public String getAdinfoIds() {
        return adinfo_ids;
    }
    public String[] getAdinfoIdsArray()
    {
        return removeTags(adinfo_ids).split(",");
    }
    public void setAdinfoIds(String adinfoIds) {
        this.adinfo_ids = adinfoIds;
    }

    public String getAdinfoNames() {
        return adinfo_names;
    }
    public String[] getAdinfoNamesArray()
    {
        return removeTags(adinfo_names).split(",");
    }
    public void setAdinfoNames(String adinfoNames) {
        this.adinfo_names = adinfoNames;
    }

    public String getAdUrls() {
        return ad_urls;
    }
    public String[] getAdUrlsArray()
    {
        return removeTags(ad_urls).split(",");
    }
    public void setAdUrls(String adUrls) {
        this.ad_urls = adUrls;
    }

    public String getMerchId() {
        return merch_id;
    }

    public void setMerchId(String merchId) {
        this.merch_id = merchId;
    }

    public String getMerchName() {
        return merch_name;
    }

    public void setMerchName(String merchName) {
        this.merch_name = merchName;
    }

    public String getGroupName() {
        return group_name;
    }

    public void setGroupName(String groupName) {
        this.group_name = groupName;
    }

    public String getGroupId() {
        return group_id;
    }

    public void setGroupId(String groupId) {
        this.group_id = groupId;
    }


    private String removeTags(String strDest)
    {
        if(strDest == null)
            strDest = "";
        if(strDest.contains("["))
            strDest = strDest.replace("[", "");
        if(strDest.contains("]"))
            strDest = strDest.replace("]", "");
        if(strDest.contains("\""))
            strDest = strDest.replace("\"", "");
        if(strDest.contains("\""))
            strDest = strDest.replace("\"", "");

        return strDest;
    }

}
