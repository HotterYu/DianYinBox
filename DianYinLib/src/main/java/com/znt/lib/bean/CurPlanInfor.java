package com.znt.lib.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CurPlanInfor implements Serializable
{
	private static final long serialVersionUID = 1L;


	private String id;
	private String planName;
	private String planType;
	private String merchId;
	private String merchName;
	private String groupName;
	private String groupId;
	private String startDate;
	private String endDate;
	private String addTime;

	private String[] scheIds;
	private String[] cycleTypes;
	private String[] startTimes;
	private String[] endTimes;
	private String[] categoryIds;
	private String[] categoryNames;

	private List<CurPlanSubInfor> curPlanSubInfors = new ArrayList<>();

	public List<CurPlanSubInfor> getCurPlanSubInfors() {
		return curPlanSubInfors;
	}
	public void clearSubPlanInfos()
	{
		curPlanSubInfors.clear();
	}
	public void addCurPlanSubInfors(CurPlanSubInfor tempInfo) {

		this.curPlanSubInfors.add(tempInfo);
	}

	/*public void initSubPlanInfors()
	{
		int count = getScheIds().length;
		clearSubPlanInfos();
		for(int i=0;i<count;i++)
		{
			CurPlanSubInfor tempSubInfo = new CurPlanSubInfor();

			String tempScheId = getScheIds()[i];

			String tempCycleType = getCycleTypes()[i];
			String tempStartTime = getStartTimes()[i];
			String tempEndTime = getEndTimes()[i];

			String tempCategoryIds = getCategoryIds()[i] ;

			if(tempCategoryIds.contains(";"))
				tempCategoryIds = tempCategoryIds.replace(";",",");

			tempSubInfo.setPlanId(getId());
			tempSubInfo.setPlanName(getPlanName());
			tempSubInfo.setStartDate(getStartDate());
			tempSubInfo.setEndDate(getEndDate());
			tempSubInfo.setStartTime(tempStartTime);
			tempSubInfo.setEndTime(tempEndTime);
			tempSubInfo.setWeek(tempCycleType);
			tempSubInfo.setCategoryIds(tempCategoryIds);
			curPlanSubInfors.add(tempSubInfo);
		}
	}*/

	/*public List<MediaInfor> getCurPlayMedias(String planId, String startTime, String endTime,String week)
	{
		List<MediaInfor> tempList = new ArrayList<>();
		int sCount = curPlanSubInfors.size();
		for(int i=0;i<sCount;i++)
		{
			CurPlanSubInfor tempInfo = curPlanSubInfors.get(i);
			String tempStartTime = tempInfo.getStartTime();
			String tempEndTime = tempInfo.getEndTime();
			String tempWeek = tempInfo.getWeek();
			String tempPlanId = tempInfo.getPlanId();

			if(tempStartTime.equals(startTime) && tempEndTime.equals(endTime) && tempWeek.equals(week) && tempPlanId.equals(planId))
			{
				tempList = tempInfo.getSongList();
				break;
			}
		}
		return tempList;
	}*/

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
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

	public String[] getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String[] categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String[] getCategoryNames() {
		return categoryNames;
	}

	public void setCategoryNames(String[] categoryNames) {
		this.categoryNames = categoryNames;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
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
