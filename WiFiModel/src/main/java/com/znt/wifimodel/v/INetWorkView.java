package com.znt.wifimodel.v;

public interface INetWorkView 
{
	/*public void createApStart();
	public void createApFail();
	public void createApSuccess();*/
	
	public void openWifiFail();
	public void connectWifiSatrt(String wifiName);
	public void connectWifiFailed(String wifiName, String wifipwd);
	public void connectWifiSuccess(String wifiName, String wifipwd);
}
