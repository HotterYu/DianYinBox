package entity;

import android.content.Context;
import android.util.Log;

import com.znt.download.IDownloadListener;
import com.znt.lib.bean.MediaInfor;
import com.znt.lib.utils.NetWorkUtils;

import java.util.ArrayList;
import java.util.List;

public class FileDownLoadManager 
{
	private Context context = null;
	
	private DownloadTask downloadTask;
    
    private volatile boolean isDownloadRunning = false;
    private boolean isCancel = false;
    private MediaInfor curDownloadSong = null;
    private List<MediaInfor> mediaList = new ArrayList<>();

    private List<MediaInfor> getDownloadList()
    {
        if(mediaList == null)
            mediaList = new ArrayList<>();
        return mediaList;
    }
    
    private IDownloadListener mIDownloadListener = null;
    
    public FileDownLoadManager(Context context,IDownloadListener mIDownloadListener)
    {
    	this.mIDownloadListener = mIDownloadListener;
    	this.context = context;
    }
    
    private DownloadListener listener = new DownloadListener() 
    {

        @Override
        public void onProgress(int progress) 
        {

        }

        @Override
        public void onSuccess() 
        {
            isDownloadRunning = false;
            startDownload();
            //Toast.makeText(DownloadService.this,"Download Success",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed()
        {
            isDownloadRunning = false;
            if(curDownloadSong != null)
            {
            	curDownloadSong.increaseReloadCount();
            	if(curDownloadSong.getReloadCount() <= 2)
                    getDownloadList().add(0, curDownloadSong);
            }
            	
            startDownload();
            //Toast.makeText(DownloadService.this,"Download Failed",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() 
        {
            isDownloadRunning = false;
            //Toast.makeText(DownloadService.this,"Download Paused",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled()
        {
            isDownloadRunning = false;
            //Toast.makeText(DownloadService.this,"Download Canceled",Toast.LENGTH_SHORT).show();
        }

		@Override
		public void onRemoveLargeSize(String url) {
			// TODO Auto-generated method stub
			isDownloadRunning = false;
			if(mIDownloadListener != null)
				mIDownloadListener.onRemoveLargeSize(url);
			startDownload();
		}
    };
    
    public static FileDownLoadManager INSTANCE = null;
    public static FileDownLoadManager init(Context context,IDownloadListener mIDownloadListener)
	{
		if(INSTANCE == null)
		{
			synchronized (FileDownLoadManager.class) 
			{
				if(INSTANCE == null)
					INSTANCE = new FileDownLoadManager(context,mIDownloadListener);
			}
		}
		
		return INSTANCE;
	}
    
    public void init(Context context)
    {
    	this.context = context.getApplicationContext();
    }
    
    public boolean isDownloadRunning()
	{
		return isDownloadRunning;
	}
	
	public void addDownloadSong(MediaInfor songInfor)
    {
        getDownloadList().add(songInfor);
    	startDownload();
    }
    /*public void addDownloadSongs(List<MediaInfor> songList)
    {
    	if(downloadList.size() > 0)
    		downloadList.clear();
    	downloadList.addAll(songList);
    	startDownload();
    }*/
    
    /*public void initDownloadList()
    {
    	downloadList.clear();
    	downloadList.addAll(DBManager.INSTANCE.getAllPlanMusics(false));
    	startDownload();
    }*/
    
    public void cancelAll()
    {
    	isCancel = true;
    	if(downloadTask != null)
    	{
    		downloadTask.cancelDownload();
        	//downloadTask.cancel(true);
        	downloadTask = null;
    	}
    }

    private int isDownloadRunningCount = 0;
    private void  startDownload()
    {
    	if(isDownloadRunning() || !NetWorkUtils.isNetConnected(context))
    	{
    	    /*if(isDownloadRunningCount >= 2)
            {
                isDownloadRunningCount = 0;
                isDownloadRunning = false;
            }
            else
                isDownloadRunningCount ++;*/
    		Log.e("", "file download is running");
    		return;
    	}
    	
    	if(getDownloadList().size() > 0)
    	{
    		try 
    		{
    			isDownloadRunning = true;
        		curDownloadSong = getDownloadList().remove(0);
        		String url = curDownloadSong.getMediaUrl();
        		
        		if(downloadTask != null)
        		{
        			downloadTask.cancelDownload();
        			downloadTask.cancel(true);
        			downloadTask = null;
        		}
                downloadTask = new DownloadTask(listener, mIDownloadListener);
                downloadTask.execute(url);
			} 
    		catch (Exception e) 
			{
                isDownloadRunning = false;
                if(listener != null)
                    listener.onFailed();
				// TODO: handle exception
    			Log.e("", "file download error-->"+e.getMessage());
			}
    	}
    }


    public void pauseDownload()
    {
        if(downloadTask!=null)
        {
            downloadTask.pauseDownload();
        }
    }

    public void cancelDownload()
    {
        if(downloadTask!=null)
        {
            downloadTask.cancelDownload();
        }
        isCancel = true;
        /*else 
        {
            if(downloadUrl!=null)
            {

                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file=new File(directory+fileName);
                if(file.exists())
                {
                    file.delete();
                }
                stopForeground(true);
                //Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }*/
    }
}
