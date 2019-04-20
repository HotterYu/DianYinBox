package com.znt.lib.bean;

import com.znt.lib.utils.DateUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Table(name = "cur_plan_sub_infor")
public class CurPlanSubInfor implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "sche_id", isId = true)
	private String scheId = "";
	@Column(name = "plan_name")
	private String planName = "";
	@Column(name = "plan_id")
	private String planId = "";
	@Column(name = "start_time")
	private String startTime = "";
	@Column(name = "end_time")
	private String endTime = "";
	@Column(name = "start_date")
	private String startDate = "";
	@Column(name = "end_date")
	private String endDate = "";
	@Column(name = "week")
	private String week = "";
	@Column(name = "category_ids")
	private String categoryIds = "";
	@Column(name = "start_time_sort")
	private long start_time_sort = 0;

	public long getStart_time_sort() {
		return start_time_sort;
	}

	public void setStart_time_sort(long start_time_sort) {
		this.start_time_sort = start_time_sort;
	}

	public String getScheId() {
		return scheId;
	}

	public void setScheId(String scheId) {
		this.scheId = scheId;
	}

	public void checkParams()
	{
		if(scheId == null)
			scheId = "";
		if(categoryIds == null)
			categoryIds = "";

		if(startDate == null)
			startDate = "";

		if(endDate == null)
			endDate = "";

		if(week == null)
			week = "";

		if(planName == null)
			planName = "";

		if(planId == null)
			planId = "";

		if(startTime == null)
			startTime = "";

		if(endTime == null)
			endTime = "";

	}

	public String getCategoryIds() {
		if(categoryIds == null)
			categoryIds = "";
		return categoryIds;
	}

	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getStartDate() {
		if(startDate == null)
			startDate = "";
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		if(endDate == null)
			endDate = "";
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getWeek() {
		if(week == null)
			week = "";
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public void setPlanName(String planName)
	{
		this.planName = planName;
	}
	public String getPlanName(String name)
	{
		if(planName == null)
			planName = "";
		return planName;
	}
	
	public void setPlanId(String planId)
	{
		this.planId = planId;
	}
	public String getPlanId()
	{
		if(planId == null)
			planId = "";
		return planId;
	}
	
	public void setStartTime(String startTime)
	{
		this.startTime = startTime;


		try
		{
			startTime = "2018-10-1 " + startTime;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			start_time_sort = formatter.parse(startTime).getTime() + 30 * 1000;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public  String getDateFromLong(long time)
	{
		Date currentTime = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));//如果不指定时区，在有些机器上会出现时间误差�??
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	public String getStartTime()
	{
		if(startTime == null)
			startTime = "";
		return startTime;
	}
	
	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}
	public String getEndTime()
	{
		if(endTime == null)
			endTime = "";
		return endTime;
	}


	/*private List<MediaInfor> songList = new ArrayList<MediaInfor>();
	public void setSongList(List<MediaInfor> songList)
	{
		this.songList = songList;
	}
	public void addSongList(List<MediaInfor> tempList)
	{
		songList.clear();
		songList.addAll(tempList);
	}
	public List<MediaInfor> getSongList()
	{
		return songList;
	}*/
}
