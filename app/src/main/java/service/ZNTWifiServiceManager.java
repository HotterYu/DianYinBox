package service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.znt.wifimodel.IWifiAidlInterface;
import com.znt.wifimodel.IWifiCallback;
import com.znt.wifimodel.ZNTWifiService;
import com.znt.wifimodel.entity.WifiModelConstant;

public class ZNTWifiServiceManager
{

	private final String TAG = "ZNTWifiServiceManager";

	private Context context = null;
	private IWifiAidlInterface mIWifiAidlInterface = null;

	private com.znt.wifimodel.v.INetWorkView mINetWorkView = null;


	public ZNTWifiServiceManager(Context context, com.znt.wifimodel.v.INetWorkView mINetWorkView)
	{
		this.context = context;
		this.mINetWorkView = mINetWorkView;
		//bindService();
	}

	public boolean isActive()
	{
		return mIWifiAidlInterface != null;
	}

	public boolean isBindSuccess()
	{
		return mIWifiAidlInterface != null;
	}


	private int WIFI_STATE_CHECK_MAX = 3;
	private int checkWifiInternalCount = WIFI_STATE_CHECK_MAX - 1;
	public void devStatusCheck(boolean isOnline)
	{
		if (mIWifiAidlInterface != null)
		{
			try
			{
				if(!isOnline)
				{
					if(checkWifiInternalCount >= WIFI_STATE_CHECK_MAX)
					{
						mIWifiAidlInterface.devStatusCheck(isOnline);
						checkWifiInternalCount = 0;
						if(WIFI_STATE_CHECK_MAX < 20)
							WIFI_STATE_CHECK_MAX ++;
					}
					else
						checkWifiInternalCount ++;
				}
				else
				{
					WIFI_STATE_CHECK_MAX = 3;
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			bindService();
	}

	public void updateDefaultWifi(boolean isAdd)
	{
		try
		{
			if (mIWifiAidlInterface != null)
			{
				mIWifiAidlInterface.updateDefaultWifi(isAdd);
			}
			else
			{
				bindService();
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startConnectCurWifi(String wifiName, String wifiPwd)
	{
		if (mIWifiAidlInterface != null)
		{
			try
			{
				//mIWifiAidlInterface.setWifiInfor(wifiName, wifiPwd);
				mIWifiAidlInterface.startConnectWifi(wifiName, wifiPwd);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			bindService();
	}

	public String getCurConnectWifiName()
	{

		try
		{
			if (mIWifiAidlInterface != null)
			{
				return mIWifiAidlInterface.getCurWifiName();
			}
			else
			{
				bindService();
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;
	}
	public String getCurConnectWifiPwd()
	{

		try
		{
			if (mIWifiAidlInterface != null)
			{
				return mIWifiAidlInterface.getCurWifiPwd();
			}
			else
			{
				bindService();
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;
	}

	private ServiceConnection mConn = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder)
		{
			mIWifiAidlInterface = IWifiAidlInterface.Stub.asInterface(iBinder);
			if (mIWifiAidlInterface != null)
			{
				try
				{
					mIWifiAidlInterface.registerCallback(mCallback);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
				Log.e("", "wifi service bind error!");
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName)
		{
			mIWifiAidlInterface = null;
		}
	};

	private IWifiCallback mCallback = new IWifiCallback.Stub()
	{
		@Override
		public void actionPerformed(int id, String wifiName, String wifipwd) throws RemoteException
		{
			// TODO Auto-generated method stub
			if(id == com.znt.wifimodel.entity.WifiModelConstant.CALL_BACK_WIFI_CONNECT_START)
			{
				mINetWorkView.connectWifiSatrt(wifiName);
				Log.d("", "wifi connect start");
			}
			else if(id == com.znt.wifimodel.entity.WifiModelConstant.CALL_BACK_WIFI_CONNECT_FAIL)
			{
				mINetWorkView.connectWifiFailed(wifiName, wifipwd);
				Log.d("", "wifi connect fail");
			}
			else if(id == com.znt.wifimodel.entity.WifiModelConstant.CALL_BACK_WIFI_CONNECT_SUCCESS)
			{
				mINetWorkView.connectWifiSuccess(wifiName, wifipwd);
				Log.d("", "wifi connect success");
			}
			else if(id == WifiModelConstant.CALL_BACK_OPEN_WIFI_FAIL)
			{
				mINetWorkView.openWifiFail();
				Log.d("", "wifi connect success");
			}
		}
	};

	public void bindService()
	{
		try
		{
			// UnBind
			unBindService();

			Intent intent = new Intent(context, ZNTWifiService.class);
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
		if (mIWifiAidlInterface != null)
		{
			context.unbindService(mConn);
			mIWifiAidlInterface = null;
		}
	}
}
