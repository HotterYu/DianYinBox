// IDownloadAidlInterface.aidl
package com.znt.download;

// Declare any non-default types here with import statements
import com.znt.download.IDownloadCallback;
interface IDownloadAidlInterface {

    void addDownloadMedia(String name, String url, String artist);


    void registerCallback(IDownloadCallback cb);
    void unregisterCallback(IDownloadCallback cb);
}
