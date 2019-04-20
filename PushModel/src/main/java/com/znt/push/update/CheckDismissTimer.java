package com.znt.push.update;

import com.znt.push.timer.AbstractTimer;

import android.content.Context;

public class CheckDismissTimer extends AbstractTimer
{

	private int maxTime = 0;
	public void setMaxTime(int maxTime)
	{
		this.maxTime = maxTime;
	}
	
	public int getCountTime()
	{
		return countTime;
	}
	
	public boolean isOver()
	{
		if(maxTime > 0 && countTime > 0 && countTime >= maxTime)
		{
			reset();
			return true;
		}
		return false;
	}
	
	public CheckDismissTimer(Context context)
	{
		super(context);
	}
	
}
