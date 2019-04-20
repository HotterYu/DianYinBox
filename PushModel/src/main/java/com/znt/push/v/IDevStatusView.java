package com.znt.push.v;


import com.znt.lib.bean.DeviceStatusInfor;

public interface IDevStatusView
{
	public void onPushSuccess(DeviceStatusInfor devStatusInfor);
	public void onPushFail(int count);
	public void onPushCheck(int count);
	public void onInitterminalFinish(String time);
	public void onUpdateServerTime(String time);
	public void onRegisterFinish(String devId, String devName,String shopCode);
	public void onSpaceCheck(long size);
	public void onGetCurPlanFinish();
	public void onGetAdPlanFinish();
	public void onTimeingPushNotify();
	public void onInternalTimePushNotify();
	public void onPushMediaNotify();
	public void onWifiConfig(String wifiName, String wifiPwd);
	public void onUpdateCheck(String vName, String vNum, String url);
	public void onPlayStatus(String status);
}
