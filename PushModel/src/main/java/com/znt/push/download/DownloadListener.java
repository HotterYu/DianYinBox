package com.znt.push.download;

/**
 * Created by Administrator on 2017/2/23.
 */
public interface DownloadListener {


    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();

}
