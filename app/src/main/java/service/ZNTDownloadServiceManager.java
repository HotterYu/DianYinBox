package service;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.znt.download.IDownloadAidlInterface;
import com.znt.download.IDownloadCallback;
import com.znt.download.ZNTDownloadService;
import com.znt.lib.bean.MediaInfor;

import java.util.List;

public class ZNTDownloadServiceManager 
{

	private final String TAG = "ZNTDownloadServiceManager";

	private Context context = null;
	private IDownloadAidlInterface mIDownloadAidlInterface = null;

	private DownlaodCallBack mDownlaodCallBack = null;
	
	public interface DownlaodCallBack
	{
		public void onDownloadSpaceCheck(String size);
		public void onDownloadRecordInsert(String mediaName, String mediaUrl, String modifyTime);
		void onRemoveLargeSize(String url);
	}

	public boolean isActive()
	{
		return mIDownloadAidlInterface != null;
	}
	
	public ZNTDownloadServiceManager(Context context,DownlaodCallBack mDownlaodCallBack)
	{
		this.context = context;
		this.mDownlaodCallBack = mDownlaodCallBack;
	}
	
	public void addSonginfor(MediaInfor songInfor)
	{
		addDownloadMedia(songInfor);
	}
	
	public void addSonginfor(List<MediaInfor> songInfors)
	{
		int size = songInfors.size();
		for(int i=0;i<size;i++)
		{
			addDownloadMedia(songInfors.get(i));
		}
	}

	private void addDownloadMedia(MediaInfor songInfor)
	{
		try
		{
			mIDownloadAidlInterface.addDownloadMedia(songInfor.getMediaName(),songInfor.getMediaUrl(),songInfor.getArtistName());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public boolean isBindSuccess()
	{
		return mIDownloadAidlInterface != null;
	}
	
	private ServiceConnection mConn = new ServiceConnection() 
	{
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) 
        {
        	mIDownloadAidlInterface = IDownloadAidlInterface.Stub.asInterface(iBinder);
            if (mIDownloadAidlInterface != null)
            {
            	try
            	{
            		mIDownloadAidlInterface.registerCallback( (com.znt.download.IDownloadCallback) mCallback);
				}
            	catch (Exception e)
            	{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            else
                Log.e("", "download service bind error!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) 
        {
        	mIDownloadAidlInterface = null;
        }
    };
    
    private IDownloadCallback mCallback = new IDownloadCallback.Stub()
    {

		@Override
		public void actionPerformed(int id, String arg1, String arg2, String arg3) throws RemoteException
		{
			// TODO Auto-generated method stub
			if(id == ZNTDownloadService.CALL_BACK_DOWNLOAD_CHECK_SPACE)
			{
				if(mDownlaodCallBack != null)
					mDownlaodCallBack.onDownloadSpaceCheck(arg1);

			}
			else if(id == ZNTDownloadService.CALL_BACK_DOWNLOAD_INSERT_RECORD)
			{
				if(mDownlaodCallBack != null)
					mDownlaodCallBack.onDownloadRecordInsert(arg1, arg2,arg3);
			}
			else if(id == ZNTDownloadService.CALL_BACK_DOWNLOAD_REMOVE_LARGE_SIZE)
			{
				if(mDownlaodCallBack != null)
					mDownlaodCallBack.onRemoveLargeSize(arg1);
			}
		}

    };

	@SuppressLint("LongLogTag")
	public void startService()
	{
		try
		{
			//启动Service处理任务
			Intent intent2 = new Intent(context, ZNTDownloadService.class);
			context.startService(intent2);
		}
		catch (Exception e)
		{
			Log.d(TAG, "startService: "+e.getMessage());
		};

	}

    public void bindService() 
    {
    	try 
    	{
    		// UnBind
            unBindService();

            Intent intent = new Intent(context, ZNTDownloadService.class);
            context.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
		} 
    	catch (Exception e) 
    	{
			// TODO: handle exception
    		Log.e("", e.getMessage());
		}
    }

    public void unBindService() 
    {
        // Service
        if (mIDownloadAidlInterface != null)
        {
        	context.unbindService(mConn);
        	mIDownloadAidlInterface = null;
        }
    }
}
