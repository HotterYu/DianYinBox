package com.znt.wifimodel.entity;

import android.content.Context;
import android.text.TextUtils;

import com.znt.wifimodel.entity.WifiSharedPreference;

public class WifiLocalDataEntity 
{

	private Context context = null;
	
	private static WifiLocalDataEntity INSTANCE = null;
	
	private final String WIFI_PWD = "WS_WIFI_PWD";
	private final String WIFI_SSID = "WS_WIFI_SSID";
	
	private WifiSharedPreference sharedPre = null;
	
	public WifiLocalDataEntity(Context context)
	{
		this.context = context;
		sharedPre = WifiSharedPreference.newInstance(context);
	}
	public static WifiLocalDataEntity newInstance(Context context)
	{
		if(INSTANCE == null)
		{
			synchronized (WifiLocalDataEntity.class) 
			{
				if(INSTANCE == null)
					INSTANCE = new WifiLocalDataEntity(context);
			}
		}
		return INSTANCE;
	}
	
	public void clearWifiRecord()
	{
		sharedPre.setData(WIFI_SSID, "");
		sharedPre.setData(WIFI_PWD, "");
	}
	
	
	public void updateWifi(String wifiName, String wifiPwd)
	{
		if(!TextUtils.isEmpty(wifiName))
			sharedPre.setData(WIFI_SSID, wifiName);
		sharedPre.setData(WIFI_PWD, wifiPwd);
	}
	public String getWifiName()
	{
		return sharedPre.getData(WIFI_SSID, "");
	}
	public String getWifiPwd()
	{
		return sharedPre.getData(WIFI_PWD, "");
	}
	
	
}
