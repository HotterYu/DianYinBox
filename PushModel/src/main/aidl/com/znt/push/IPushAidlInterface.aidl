// IPushAidlInterface.aidl
package com.znt.push;

// Declare any non-default types here with import statements
import com.znt.push.IPushCallback;
import com.znt.lib.bean.MediaInfor;
interface IPushAidlInterface
{
    void setRequestParams(in MediaInfor mediaInfor, String fnetInfo, boolean updateNow);
    void updatePlayRecord(in MediaInfor mediaInfor);
    void updateVolumeSetStatus(boolean result);
    String getDevId();
    MediaInfor getCurPlayMedia();

    /*List<MediaInfor> getCurPlayList();*/
    MediaInfor getCurTimeingAd();
    MediaInfor getCurTimeInternalAd();
    List<MediaInfor> getPushMedias();
    List<MediaInfor> getCurPlayMedias();

    void init(boolean isPlugin);

    void updateProcessByTime(long time);

    long getCurServerTime();

    void registerCallback(IPushCallback cb);
    void unregisterCallback(IPushCallback cb);

}
