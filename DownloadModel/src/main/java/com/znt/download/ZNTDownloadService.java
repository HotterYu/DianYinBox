package com.znt.download;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.znt.lib.bean.MediaInfor;

import entity.FileDownLoadManager;

public class ZNTDownloadService extends Service
{
	
	private String TAG = "ZNTDownloadService";
	
    private ServiceBinder mBinder;
    
    private Context mContext = null;
    
    public static final int CALL_BACK_DOWNLOAD_CHECK_SPACE = 300;
    public static final int CALL_BACK_DOWNLOAD_INSERT_RECORD = 301;
    public static final int CALL_BACK_DOWNLOAD_REMOVE_LARGE_SIZE = 302;
    
    public ZNTDownloadService() 
    {
    	
    }
    
    private void showErrorLog(Exception e)
    {
    	Log.e(TAG, e.getMessage());
    }
    
    @Override
    public void onCreate() 
    {
    	// TODO Auto-generated method stub
    	super.onCreate();
    }
    
    @Override
    public void onDestroy() 
    {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
		/*String s = null;
		s.substring(0);*/
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }
    
    @Override
    public IBinder onBind(Intent intent) 
    {
        if (mBinder == null)
            mBinder = new ServiceBinder();
        if(mContext == null)
        {
        	try 
        	{
            	mContext = getApplicationContext();
            	//DBManager.init(mContext);
            	FileDownLoadManager.init(new IDownloadListener()
            	{
					@Override
					public void onDownloadSpaceCheck(long size) 
					{
						// TODO Auto-generated method stub
						callback(CALL_BACK_DOWNLOAD_CHECK_SPACE, size+"", null, null);
					}
					
					@Override
					public void onDownloadRecordInsert(MediaInfor songInfor, long modifyTime)
					{
						// TODO Auto-generated method stub
						if(songInfor != null)
							callback(CALL_BACK_DOWNLOAD_INSERT_RECORD, songInfor.getMediaName(), songInfor.getMediaUrl(), modifyTime+"");
					}

					@Override
					public void onRemoveLargeSize(String url) {
						// TODO Auto-generated method stub
						
						callback(CALL_BACK_DOWNLOAD_REMOVE_LARGE_SIZE, url, null, null);
					}
				});
    		}
        	catch (Exception e) 
        	{
    			// TODO: handle exception
        		showErrorLog(e);
    		}
        }
        
        return mBinder;
    }

    final RemoteCallbackList<IDownloadCallback> mCallbacks = new RemoteCallbackList <IDownloadCallback>();
    void callback(int val, String arg1, String arg2, String arg3) 
    {   
        final int N = mCallbacks.beginBroadcast();  
        for (int i=0; i<N; i++)
        {   
            try 
            {  
                mCallbacks.getBroadcastItem(i).actionPerformed(val, arg1, arg2, arg3);   
            }  
            catch (Exception e)
            {   
                // The RemoteCallbackList will take care of removing   
                // the dead object for us.     
            }  
        }  
        mCallbacks.finishBroadcast();  
    }  
    
    class ServiceBinder extends IDownloadAidlInterface.Stub 
    {

        public ServiceBinder() 
        {
            
        }

		@Override
		public void registerCallback(IDownloadCallback cb) throws RemoteException 
		{
			// TODO Auto-generated method stub
			if (cb != null) 
			{   
                mCallbacks.register(cb);  
            }  
		}

		@Override
		public void unregisterCallback(IDownloadCallback cb) throws RemoteException 
		{
			if(cb != null) 
			{  
                mCallbacks.unregister(cb);  
            }  
		}

		@Override
		public void addDownloadMedia(String name, String url, String artist) throws RemoteException
		{
			// TODO Auto-generated method stub
            MediaInfor infor = new MediaInfor();
            infor.setMediaName(name);
            infor.setMediaUrl(url);
            infor.setArtistName(artist);

			FileDownLoadManager.INSTANCE.addDownloadSong(infor);
            //FileDownLoadManager.INSTANCE.addDownloadSongs(infors);
			Log.e(TAG, "addSongInfor");
		}

    }
}