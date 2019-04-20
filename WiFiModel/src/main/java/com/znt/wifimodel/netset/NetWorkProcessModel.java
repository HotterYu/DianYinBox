package com.znt.wifimodel.netset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.znt.lib.bean.WifiInfor;
import com.znt.wificonnector.WifiConnector;
import com.znt.wificonnector.interfaces.ConnectionResultListener;
import com.znt.wificonnector.interfaces.RemoveWifiListener;
import com.znt.wificonnector.interfaces.ShowWifiListener;
import com.znt.wificonnector.interfaces.WifiConnectorModel;
import com.znt.wificonnector.interfaces.WifiStateListener;
import com.znt.wifimodel.db.DBManager;
import com.znt.wifimodel.entity.WifiLocalDataEntity;
import com.znt.wifimodel.receiver.WifiReceiver;
import com.znt.wifimodel.timer.CheckSsidTimer;
import com.znt.wifimodel.v.INetWorkView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class NetWorkProcessModel implements WifiReceiver.WifiStateListener,WifiConnectorModel
{
	private final String TAG = "NetWorkModel";
	
	private INetWorkView iNetWorkView = null;
	
	private Context mContext = null;
	private WifiAdmin mWifiAdmin = null;

	private WifiReceiver mWifiReceiver = null;

	private WifiConnector wifiConnector;
	
	private volatile String curConnectWifiName = "";
	private volatile String curConnectWifiPwd = "";

	private volatile List<ScanResult> mWifiScanResult = new ArrayList<>();

	private boolean isConnectRunning = false;
	private CheckSsidTimer mCheckWifiStateTimer;
	private final int CHECK_WIFI_STATE = 1;
	private volatile boolean isWifiRegistered = false;
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{

			if(msg.what == CHECK_WIFI_STATE)
			{
				if(mWifiAdmin.isWifiEnabled())
				{
					mCheckWifiStateTimer.stopTimer();
					connectNextWifi();
				}
			}

			super.handleMessage(msg);
		}
	};

	public NetWorkProcessModel(Context context, INetWorkView iNetWorkView)
	{
		this.mContext = context;
		this.iNetWorkView = iNetWorkView;
		
		mWifiReceiver = new WifiReceiver(context);

		mWifiReceiver.setWifiStateListener(this);
		registerWifiReceiver();

		curConnectWifiName = WifiLocalDataEntity.newInstance(mContext).getWifiName();
		curConnectWifiPwd = WifiLocalDataEntity.newInstance(mContext).getWifiPwd();
		
		mCheckWifiStateTimer = new CheckSsidTimer(context);
		mCheckWifiStateTimer.setHandler(mHandler, CHECK_WIFI_STATE);
		mCheckWifiStateTimer.setTimeInterval(1000);
		mCheckWifiStateTimer.setMaxTime(20);

		createWifiConnectorObject();

		scanForWifiNetworks();
		
		if(mWifiAdmin == null)
		{
			mWifiAdmin = new WifiAdmin(context)
			{
				@Override
				public void onNotifyWifiConnected()
				{
					// TODO Auto-generated method stub
					isConnectRunning = false;
					doconenctWifiSuccess();
				}
				
				@Override
				public void onNotifyWifiConnectFailed()
				{
					// TODO Auto-generated method stub
					isConnectRunning = false;
					connectNextWifi();
				}
				
				@Override
				public void myUnregisterReceiver(BroadcastReceiver receiver)
				{
					// TODO Auto-generated method stub
					//mContext.unregisterReceiver(receiver);
				}
				
				@Override
				public Intent myRegisterReceiver(BroadcastReceiver receiver,
						IntentFilter filter)
				{
					// TODO Auto-generated method stub
					return null;
				}
			};
		}
	}

	public boolean isConnectRunning()
	{
		return isConnectRunning;
	}

	private void registerWifiReceiver()
	{
		if(isWifiRegistered)
		{
			isWifiRegistered = false;
			mContext.unregisterReceiver(mWifiReceiver);
		}

		if(!isWifiRegistered)
		{
			IntentFilter filter = new IntentFilter();
	        filter.addAction("android.net.wifi.RSSI_CHANGED");
	        filter.addAction("android.net.wifi.STATE_CHANGE");
	        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
	        mContext.registerReceiver(mWifiReceiver, filter);
	        isWifiRegistered = true;
		}
	}

	public boolean isWifiEnabled()
	{
		return mWifiAdmin.isWifiEnabled();
	}
	public void openWifi()
	{
		if(!isWifiEnabled())
			mWifiAdmin.openWifi();
	}

	public WifiAdmin getWifiAdmin()
	{
		return mWifiAdmin;
	}
	public void startConnectCurrentWifiAndReset()
	{

		isCurWifiConnectedSuccessRepoted = false;

		loopConnectFailCountMax = 0;
		isConnectRunning = false;
		startConnectCurrentWifi();
		showLogStep("startConnectCurrentWifi");
	}

	public void startConnectCurrentWifi()
	{

		if(loopConnectFailCountMax >= 3)
		{
			loopConnectFailCountMax ++;
			if(loopConnectFailCountMax >= 20)
			{
				loopConnectFailCountMax = 0;
				Log.e("", "***************wifi reconnect start-->"+loopConnectFailCountMax);
			}
			else
				Log.e("", "***************wifi loop pasue-->"+loopConnectFailCountMax);
		}
		else
		{
            connectNextWifi();
			/*String name = WifiLocalDataEntity.newInstance(mContext).getWifiName();
			String pwd = WifiLocalDataEntity.newInstance(mContext).getWifiPwd();
			if(TextUtils.isEmpty(name))
				connectNextWifi();
			else
				connectWifi(name, pwd);*/

		}
	}

	public void connectWifi(final String name, String pwd)
	{

		//pwd = pwd.replace(" ", "");
		Log.e(TAG, "***********start connect wifi-->" + name + "   " + pwd);
		this.curConnectWifiName = name;
		this.curConnectWifiPwd = pwd;
		iNetWorkView.connectWifiSatrt(name);

		if(isWifiEnabled())
		{
			doConnectWifi();
		}
		else
		{
			openWifi();
			startCheckWifiState();
		}
	}

	private void doConnectWifi()
	{
		try
		{
			isConnectRunning = true;
			mWifiAdmin.addNetwork(curConnectWifiName, curConnectWifiPwd);
			/*ScanResult scanResult = getScanResultByWifiName(curConnectWifiName);
			if(scanResult == null)
			{
				mWifiAdmin.addNetwork(curConnectWifiName, curConnectWifiPwd);
			}
			else
				connectToWifiAccessPoint(scanResult, curConnectWifiPwd);*/
		}
		catch (Exception e)
		{
			// TODO: handle exception
			Log.e("", e.getMessage());

			connectNextWifi();
		}
	}

	private void startCheckWifiState()
	{
		mCheckWifiStateTimer.startTimer();
	}
	public void stopCheckWifiState()
	{
		if(!mCheckWifiStateTimer.isOver() && !mCheckWifiStateTimer.isStop())
			mCheckWifiStateTimer.stopTimer();
	}

	public void checkIfShouldConnectWifi()
    {
		Log.e(TAG, "***********checkIfShouldConnectWifi");
    	/*if(NetWorkUtils.checkEthernet(mContext))
    	{
    		return;
    	}*/
    	startConnectCurrentWifi();
    }

	private int wifiIndex = 0;
	private int loopConnectCount = 0;
	private int loopConnectFailCountMax = 0;
	private void connectNextWifi()
	{
		if(DBManager.INSTANCE.getWifiCount() == 0)
		{
			doconenctWifiFail();
			return;
		}

		if(wifiIndex < DBManager.INSTANCE.getWifiCount())
		{
			WifiInfor tempInfor = getWifiInforByIndex();
			wifiIndex ++;
			connectWifi(tempInfor.getWifiName(), tempInfor.getWifiPassword());
		}
		else
		{
			loopConnectCount ++;

			if(loopConnectCount >= 2)
				doconenctWifiFail();
			else
			{
				wifiIndex = 0;
				connectNextWifi();
			}
		}
	}

	private WifiInfor getWifiInforByIndex()
	{
		List<WifiInfor> wifiList = DBManager.INSTANCE.getWifiList();
		WifiInfor tempInfor = wifiList.get(wifiIndex);
		return tempInfor;
	}

	private void resetLoopConnextCount()
	{
		//wifiIndex = 0;
		loopConnectCount = 0;
	}
	
	private void doconenctWifiFail()
	{
		loopConnectFailCountMax ++;
		isConnectRunning = false;
		resetLoopConnextCount();

		if(iNetWorkView != null)
			iNetWorkView.connectWifiFailed(curConnectWifiName, curConnectWifiPwd);
	}
	private void doconenctWifiSuccess()
	{
		isConnectRunning = false;
		resetLoopConnextCount();

		WifiLocalDataEntity.newInstance(mContext).updateWifi(curConnectWifiName, curConnectWifiPwd);

		DBManager.INSTANCE.insertWifi(curConnectWifiName, curConnectWifiPwd);
		
		if(iNetWorkView != null)
			iNetWorkView.connectWifiSuccess(curConnectWifiName, curConnectWifiPwd);
	}

	public ScanResult getScanResultByWifiName(String wifiName)
	{
		ScanResult resultScanResult = null;
		int wifiCount = mWifiScanResult.size();
		for(int i=0;i<wifiCount;i++)
		{
			ScanResult tempScanResult = mWifiScanResult.get(i);
			if(!TextUtils.isEmpty(tempScanResult.SSID) && tempScanResult.SSID.equals(wifiName))
			{
				resultScanResult = tempScanResult;
				break;
			}
		}

		return resultScanResult;
	}

	@Override
	public void onRssiChanged() 
	{
		// TODO Auto-generated method stub
		
	}

	private volatile boolean isCurWifiConnectedSuccessRepoted = false;
	private volatile boolean isFailWhenSuccess = false;
	@Override
	public void onNetWorkStateChanged(String wifi, boolean info) 
	{
		if(wifi.contains(curConnectWifiName) && !TextUtils.isEmpty(curConnectWifiName))
		{
			if(info)
			{
				if(!isCurWifiConnectedSuccessRepoted)
				{
					isCurWifiConnectedSuccessRepoted = true;
					isFailWhenSuccess = true;
			        doconenctWifiSuccess(); 
				}
			}
			else
			{
				mHandler.postDelayed(new Runnable()
				{
					@Override
					public void run() 
					{
						// TODO Auto-generated method stub
						if(!isFailWhenSuccess)
							connectNextWifi();
					}
				}, 10000);
				
				
			}
		}
	}
	private void showLogStep(String info)
	{
		Log.e("", "@@@@@@@@@@@@@@@@@@@-->" + info);
	}
	private boolean isCurWifiSame(String wifiSSID)
	{
		return curConnectWifiName.equals(wifiSSID) || ("\""+ curConnectWifiName +"\"").equals(wifiSSID);
	}

	@Override
	public void onWifiStateChanged(int wifistate) 
	{
		// TODO Auto-generated method stub
		/*if(wifistate == WifiManager.WIFI_STATE_DISABLED)
        {  

        }  
        else if(wifistate == WifiManager.WIFI_STATE_ENABLED)
        {  

        }  */
	}

	@Override
	public void createWifiConnectorObject()
	{
		wifiConnector = new WifiConnector(mContext);
		wifiConnector.setLog(true);
		wifiConnector.registerWifiStateListener(new WifiStateListener()
		{
			@Override
			public void onStateChange(int wifiState)
			{

			}

			@Override
			public void onWifiEnabled()
			{
				//MainActivity.this.onWifiEnabled();
			}

			@Override
			public void onWifiEnabling()
			{

			}

			@Override
			public void onWifiDisabling()
			{

			}

			@Override
			public void onWifiDisabled()
			{

			}
		});}

	@Override
	public void connectToWifiAccessPoint(final ScanResult scanResult, String password)
	{
		this.wifiConnector.setScanResult(scanResult, password);
		this.wifiConnector.setLog(true);
		this.wifiConnector.connectToWifi(new ConnectionResultListener()
		{
			@Override
			public void successfulConnect(String SSID)
			{
				if(!TextUtils.isEmpty(SSID)&& !TextUtils.isEmpty(curConnectWifiName))
				{
					if(SSID.contains("\""))
					{
						SSID = SSID.replace("\"", "");
					}
					if(curConnectWifiName.equals(SSID))
					{
						//wifi配置成功，可以上报
						doconenctWifiSuccess();
						Log.e("", "successfulConnect-->"+SSID);
					}
				}
				Toast.makeText(mContext, "You are connected to " + SSID + "!!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void errorConnect(int codeReason)
			{
				connectNextWifi();
				//Toast.makeText(MainActivity.this, "Error on connecting to wifi: " + scanResult.SSID +"\nError code: "+ codeReason,Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStateChange(SupplicantState supplicantState)
			{

			}
		});
	}

	@Override
	public void disconnectFromAccessPoint(ScanResult scanResult)
	{
		this.wifiConnector.removeWifiNetwork(scanResult, new RemoveWifiListener()
		{
			@Override
			public void onWifiNetworkRemoved()
			{
				//Toast.makeText(MainActivity.this, "You have removed this wifi!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onWifiNetworkRemoveError()
			{
				//Toast.makeText(MainActivity.this, "Error on removing this network!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void destroyWifiConnectorListeners()
	{
		wifiConnector.unregisterWifiStateListener();
	}

	@Override
	public void scanForWifiNetworks()
	{
		// TODO Auto-generated method stub
		wifiConnector.showWifiList(new ShowWifiListener()
		{
			@Override
			public void onNetworksFound(WifiManager wifiManager, List<ScanResult> wifiScanResult)
			{
				mWifiScanResult = wifiScanResult;
				//adapter.setScanResultList(wifiScanResult);
			}

			@Override
			public void onNetworksFound(JSONArray wifiList)
			{
				//Log.e("", "wifiList-->"+wifiList);
			}

			@Override
			public void errorSearchingNetworks(int errorCode)
			{
				Log.e("", "errorSearchingNetworks-->"+errorCode);//Toast.makeText(getApplicationContext(), "Error on getting wifi list, error code: " + errorCode, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
