package com.znt.lib.bean;

public class WifiInfor 
{
	private String wifiName = "";
	private String wifiPassword = "";
	private String wifi_status = "";
	private int reconnect_count = 0;
	private String shopCode = "";
	private String name = "";

	public String getWifiName() {
		return wifiName;
	}

	public void setWifiName(String wifiName) {
		this.wifiName = wifiName;
	}

	public String getWifiPassword() {
		return wifiPassword;
	}

	public void setWifiPassword(String wifiPassword) {
		this.wifiPassword = wifiPassword;
	}

	public String getWifi_status() {
		return wifi_status;
	}

	public void setWifi_status(String wifi_status) {
		this.wifi_status = wifi_status;
	}

	public int getReconnect_count() {
		return reconnect_count;
	}

	public void setReconnect_count(int reconnect_count) {
		this.reconnect_count = reconnect_count;
	}

	public String getShopCode() {
		return shopCode;
	}

	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
