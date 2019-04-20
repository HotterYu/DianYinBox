package entity;


public interface DownloadListener {



    void onProgress(int progress);


    void onSuccess();


    void onFailed();


    void onPaused();


    void onCanceled();
    
    void onRemoveLargeSize(String url);

}
