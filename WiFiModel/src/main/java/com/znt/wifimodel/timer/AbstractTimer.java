package com.znt.wifimodel.timer;

import java.util.Timer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public abstract class AbstractTimer {

	private final static int TIMER_INTERVAL = 10000;
	
	protected Context mContext;
	private Timer mTimer;	
	//protected MyTimeTask mTimeTask;
	protected int mTimeInterval = TIMER_INTERVAL;
	protected Handler mHandler;
	protected int msgID;
	protected int countTime = 0;
	private boolean isStop = false;
	
	public AbstractTimer(Context context)
	{
		mContext = context;	
		mTimer = new Timer();	
	}
	
	public void setHandler( Handler handler, int msgID){
		mHandler = handler;
		this.msgID = msgID;
	}
	
	public void setTimeInterval(int interval){
		mTimeInterval = interval;
	}
	
	public void startTimer()
	{
		stopTimer();
		
		isStop = false;
		countTime = 0;

		mHandler.postDelayed(runnable, 100);

		/*if (mTimeTask == null)
		{
			mTimeTask = new MyTimeTask();
			mTimer.schedule(mTimeTask, 0, mTimeInterval);
		}*/
	}
	
	public void reset()
	{
		isStop = false;
		countTime = 0;
	}
	
	public boolean isStop()
	{
		return isStop;
	}
	
	public void stopTimer()
	{
		isStop = true;
		mHandler.removeMessages(msgID);
		mHandler.removeCallbacks(runnable);
		/*if (mTimeTask != null)
		{
			mTimeTask.cancel();
			mTimeTask = null;
		}*/
		countTime = 0;
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			/* do what you need to do */
			//foobar();
			/* and here comes the "trick" */

			if (mHandler != null)
			{
				if(!isStop)
				{
					mHandler.postDelayed(this, 100);
					Message msg = mHandler.obtainMessage(msgID);
					msg.sendToTarget();
					countTime ++;
				}
			}
		}
	};
	
	/*class MyTimeTask extends TimerTask
	{
		@Override
		public void run() 
		{
			if (mHandler != null)
			{
				if(!isStop)
				{
					Message msg = mHandler.obtainMessage(msgID);
					msg.sendToTarget();
					
					countTime ++;
				}
			}
		}
		
	}*/
	
}
