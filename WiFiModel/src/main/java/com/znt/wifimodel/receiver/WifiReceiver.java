package com.znt.wifimodel.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.util.Timer;
import java.util.TimerTask;

public class WifiReceiver extends BroadcastReceiver
{
    private Handler handler = new Handler();
	/*private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			//if(mCheckSsidTimer.isOver())
			{
				mWifiStateListener.onNetWorkStateChanged(curConnectWifi, false);
			}
		};
	};*/
	private Context mConetx = null;
	public WifiReceiver(Context mConetx)
	{
		
	}
	
	public interface WifiStateListener
	{
		public void onRssiChanged();
		public void onNetWorkStateChanged(String wifi, boolean info);
		public void onWifiStateChanged(int wifistate);
		
	}
	
	private String curConnectWifi = "";
	private volatile boolean isCurWifiConnectFailNotify = false;
	private volatile boolean isCurWifiConnectSuccessNotify = false;
	
	public void setConnectWifi(String curConnectWifi)
	{
		isCurWifiConnectFailNotify = false;
		isCurWifiConnectSuccessNotify = false;
		startTimer();
		this.curConnectWifi = curConnectWifi;
	}
	
	public WifiStateListener mWifiStateListener = null;
	public void setWifiStateListener(WifiStateListener mWifiStateListener)
	{
		this.mWifiStateListener = mWifiStateListener; 
	}
	
    @Override  
    public void onReceive(Context context, Intent intent) 
    {  
        // TODO Auto-generated method stub  
        if(intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION))
        {  
            //signal strength changed  
        	if(mWifiStateListener != null)
        		mWifiStateListener.onRssiChanged();
        }  
        else if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
        {

            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO); 
            if(info.getState().equals(NetworkInfo.State.DISCONNECTED))
            {  
        		 WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);  
                 WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                 String ssid = wifiInfo.getSSID();
                 if(!TextUtils.isEmpty(curConnectWifi) && !TextUtils.isEmpty(ssid) && ssid.contains(curConnectWifi))
                 {
                	 if(mWifiStateListener != null && !isCurWifiConnectFailNotify)
                	 {
                		 isCurWifiConnectFailNotify = true;
                		 mWifiStateListener.onNetWorkStateChanged(wifiInfo.getSSID(), false);
                		 stopTimer();
                	 }
                 }

            }  
            else if(info.getState().equals(NetworkInfo.State.CONNECTED))
            {  
                  
                WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);  
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();                  
                String ssid = wifiInfo.getSSID();
                if(!TextUtils.isEmpty(curConnectWifi) && ssid.contains(curConnectWifi))
                {
                	if(mWifiStateListener != null && !isCurWifiConnectSuccessNotify)
                	{
                		isCurWifiConnectSuccessNotify = true;
                		mWifiStateListener.onNetWorkStateChanged(ssid, true);
                		stopTimer();
                	}
                }

            }  
        }  
        else if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION))
        {

            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);  
             if(mWifiStateListener != null)
            	 mWifiStateListener.onWifiStateChanged(wifistate);
             else
             {
            	 if(wifistate == WifiManager.WIFI_STATE_DISABLED)
                 {  

                 }  
                 else if(wifistate == WifiManager.WIFI_STATE_ENABLED)
                 {  

                 }  
             }
        }  
    }  
    
    private volatile boolean isStop = false;
    private void startTimer()
    {
    	if(timer == null)
    	{
    		count = 0;
    		isStop = false;
    		timer = new Timer();
    		timer.schedule(task, 20000, 20000);
    	}
    	
    }
    private void stopTimer()
    {
    	if(timer != null)
    	{
    		timer.cancel();
    		isStop = true;
    	}
    }
    private int count = 0;
    Timer timer = null;
    TimerTask task = new TimerTask() 
    {
        @Override
        public void run() 
        {
            if(!isStop)
            {
            	count ++;
            	if(count > 3)
            	{
            		stopTimer();
            	}
            	else
            	{
            		Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
            	}
            }
        }
    };
}  