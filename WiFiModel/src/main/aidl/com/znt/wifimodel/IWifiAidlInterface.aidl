// IWifiAidlInterface.aidl
package com.znt.wifimodel;

// Declare any non-default types here with import statements

import com.znt.wifimodel.IWifiCallback;
interface IWifiAidlInterface
{

    void startConnectWifi(String wifiName , String wifiPwd);
    void setWifiInfor(String wifiName , String wifiPwd);
    String getCurWifiName();
    String getCurWifiPwd();
    void devStatusCheck(boolean isOnline);

    void updateDefaultWifi(boolean isAdd);

    void registerCallback(IWifiCallback cb);
    void unregisterCallback(IWifiCallback cb);
}