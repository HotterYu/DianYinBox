package com.znt.wifimodel;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.znt.wifimodel.db.DBManager;
import com.znt.wifimodel.entity.WifiLocalDataEntity;
import com.znt.wifimodel.netset.WifiPresenter;
import com.znt.wifimodel.v.INetWorkView;

public class ZNTWifiService extends Service implements INetWorkView
{
	
	private String TAG = "ZNTWifiService";
	
    private ServiceBinder mBinder;
    
    private Context mContext = null;
    
    private WifiPresenter mNetWorkPresenter = null;

    private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

    public ZNTWifiService() 
    {
    	
    }
    
    private final String DEFAULT_WIFI_NAME = "DianYinGuanJia";
	private final String DEFAULT_WIFI_PWD = "zhunikeji";
    private void setDefaultWifi()
    {
		DBManager.INSTANCE.insertWifi(DEFAULT_WIFI_NAME, DEFAULT_WIFI_PWD);
		//DBManager.INSTANCE.insertWifi("dianyin", "12345678");
    }
    private void showErrorLog(Exception e)
    {
    	if(e != null)
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
    	// TODO Auto-generated method stub
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
		/*String s = null;
		s.substring(0);*/
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
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						DBManager.init(mContext);
						mNetWorkPresenter = new WifiPresenter(mContext, ZNTWifiService.this);

						setDefaultWifi();
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

    final RemoteCallbackList<IWifiCallback> mCallbacks = new RemoteCallbackList <IWifiCallback>();
    void callback(int val, String arg1, String arg2) 
    {   
        final int N = mCallbacks.beginBroadcast();  
        for (int i=0; i<N; i++)
        {   
            try 
            {  
                mCallbacks.getBroadcastItem(i).actionPerformed(val, arg1, arg2);   
            }  
            catch (Exception e)
            {   
                // The RemoteCallbackList will take care of removing   
                // the dead object for us.     
            }  
        }  
        mCallbacks.finishBroadcast();  
    }

    class ServiceBinder extends IWifiAidlInterface.Stub
    {

        public ServiceBinder() 
        {
            
        }

		@Override
		public void startConnectWifi(String wifiName, String wifiPwd) throws RemoteException 
		{
			// TODO Auto-generated method stub
			try 
			{
				DBManager.INSTANCE.insertWifi(wifiName, wifiPwd);
				
				//WifiLocalDataEntity.newInstance(mContext).updateWifi(wifiName, wifiPwd);
				mNetWorkPresenter.connectWifi(wifiName, wifiPwd);
				//Log.e(TAG, "startConnectWifi -- >" + wifiName + "   " + wifiPwd);
			}
			catch (Exception e)
			{
				// TODO: handle exception
				showErrorLog(e);
			}
		}

		@Override
		public void setWifiInfor(String wifiName, String wifiPwd) throws RemoteException
		{
			// TODO Auto-generated method stub
			try
			{
				DBManager.INSTANCE.insertWifi(wifiName, wifiPwd);
				//LocalDataEntity.newInstance(mContext).updateWifi(wifiName, wifiPwd);
			}
			catch (Exception e)
			{
				// TODO: handle exception
				showErrorLog(e);
			}
		}

		@Override
		public String getCurWifiName() throws RemoteException
		{
			// TODO Auto-generated method stub
			try
			{
				//List<WifiInfor> wifiList = DBManager.INSTANCE.getWifiList();
				return WifiLocalDataEntity.newInstance(mContext).getWifiName();
			}
			catch (Exception e)
			{
				showErrorLog(e);
			}
			return null;
		}

		@Override
		public String getCurWifiPwd() throws RemoteException
		{
			// TODO Auto-generated method stub

			try
			{
				return WifiLocalDataEntity.newInstance(mContext).getWifiPwd();
			}
			catch (Exception e)
			{
				showErrorLog(e);
			}
			return null;
		}

		@Override
		public void devStatusCheck(boolean isOnline) throws RemoteException
		{
			// TODO Auto-generated method stub
			try
			{
				mNetWorkPresenter.checkWifiState();
			} 
			catch (Exception e) 
			{
				showErrorLog(e);
			}
		}

		@Override
		public void updateDefaultWifi(boolean isAdd) throws RemoteException {

			try
			{
				if(isAdd)
				{
					DBManager.INSTANCE.insertWifi("cstorebp", "51060018");
					DBManager.INSTANCE.insertWifi("cstoreap", "51060018562");
				}
				else
				{
					DBManager.INSTANCE.deleteWifi("cstorebp");
					DBManager.INSTANCE.deleteWifi("cstoreap");
				}
			}
			catch (Exception e)
			{
				// TODO: handle exception
			}
		}

		@Override
		public void registerCallback(IWifiCallback cb) throws RemoteException
		{
			// TODO Auto-generated method stub
			if (cb != null) 
			{   
                mCallbacks.register(cb);  
            }  
		}

		@Override
		public void unregisterCallback(IWifiCallback cb) throws RemoteException
		{
			if(cb != null) 
			{  
                mCallbacks.unregister(cb);  
            }  
		}
    }

	@Override
	public void openWifiFail() {
		callback(com.znt.wifimodel.entity.WifiModelConstant.CALL_BACK_OPEN_WIFI_FAIL, null, null);
	}

	@Override
	public void connectWifiSatrt(String wifiName) 
	{
		// TODO Auto-generated method stub
		callback(com.znt.wifimodel.entity.WifiModelConstant.CALL_BACK_WIFI_CONNECT_START, wifiName, null);
	}
	@Override
	public void connectWifiFailed(String wifiName, String wifipwd) 
	{
		// TODO Auto-generated method stub
		callback(com.znt.wifimodel.entity.WifiModelConstant.CALL_BACK_WIFI_CONNECT_FAIL, wifiName, wifipwd);
	}
	@Override
	public void connectWifiSuccess(String wifiName, String wifipwd) 
	{
		// TODO Auto-generated method stub
		callback(com.znt.wifimodel.entity.WifiModelConstant.CALL_BACK_WIFI_CONNECT_SUCCESS, wifiName, wifipwd);
	}
}