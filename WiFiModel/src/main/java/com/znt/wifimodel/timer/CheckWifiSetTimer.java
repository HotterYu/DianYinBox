
package com.znt.wifimodel.timer;

import android.content.Context;

/** 
 * @ClassName: CheckSsidTimer 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-20 下午3:49:12  
 */
public class CheckWifiSetTimer extends com.znt.wifimodel.timer.AbstractTimer
{

	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public CheckWifiSetTimer(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		setTimeInterval(1000);
	}
	
	/*public void setMaxTime(int maxTime)
	{
		this.maxTime = maxTime;
	}
	
	public boolean isOver()
	{
		if(maxTime > 0 && countTime > 0 && countTime >= maxTime)
		{
			stopTimer();
			return true;
		}
		return false;
	}*/

}
 
