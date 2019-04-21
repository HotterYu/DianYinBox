package com.znt.wifimodel.netset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.znt.lib.utils.NetWorkUtils;
import com.znt.lib.utils.ViewUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class WifiAdmin
{
	private static final String TAG = "WifiAdmin";

	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	private List<ScanResult> mWifiList;
	private List<WifiConfiguration> mWifiConfiguration;

	private WifiLock mWifiLock;

	private String mPasswd = "";
	private String curConnectWifiSSID = "";
	private final int CHECK_SSID = 1;
	private Context mContext = null;
	
	
	private void showToast(String text)
	{
		ViewUtils.sendMessage(Uihandler, 0, text);
	}

	public WifiAdmin(Context context)
	{
		mContext = context;
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
	}
	

	public void openWifi()
	{
		if (!mWifiManager.isWifiEnabled())
		{
			if(!mWifiManager.setWifiEnabled(true))
				showToast("WIFI OPEN FAILED");
		}
	}

	public void closeWifi()
	{
		if (mWifiManager.isWifiEnabled())
		{
			mWifiManager.setWifiEnabled(false);
		}
	}
	
	public void restartWifi()
	{
		mWifiManager.setWifiEnabled(false);
		mWifiManager.setWifiEnabled(true);
	}

	private String curConnectWifiName = "";
	
	public boolean isWifiEnabled()
	{
		return mWifiManager.isWifiEnabled();
	}

	public abstract Intent myRegisterReceiver(BroadcastReceiver receiver,
			IntentFilter filter);

	public abstract void myUnregisterReceiver(BroadcastReceiver receiver);

	public abstract void onNotifyWifiConnected();

	public abstract void onNotifyWifiConnectFailed();
	public boolean addNetwork(String wifiName, String wifiPwd)
	{
		curConnectWifiName = wifiName;

		stopTimer();
		unRegister();

		register();

		//WifiApAdmin.closeWifiAp(mContext);
		int wcgID = mWifiManager.addNetwork(createWifiInfo(wifiName, wifiPwd, true));
		//showToast("wcgID-->" + wcgID);
		if(wcgID < 0)
		{
			wcgID = mWifiManager.addNetwork(createWifiInfo(wifiName, wifiPwd, true));
			if(wcgID < 0)
			{
				wcgID = mWifiManager.addNetwork(createWifiInfo(wifiName, wifiPwd, true));
				if(wcgID < 0)
				{
					//showToast(mContext.getResources().getString(R.string.wifi_connect_error) + "  name:"+wifiName + "    pwd:"+wifiPwd);
					onNotifyWifiConnectFailed();
					stopTimer();
					unRegister();
					return false;
				}
			}
		}

		boolean b = mWifiManager.enableNetwork(wcgID, true);
		if(b)
		{
			onNotifyWifiConnected();
			Log.e("", " ************* WifiAdmin   wifi connected success!");
		}
		else
		{
			stopTimer();
			unRegister();
			onNotifyWifiConnectFailed();
		}
		return b;
	}
	public static final int TYPE_NO_PASSWD = 0x11;
	public static final int TYPE_WEP = 0x12;
	public static final int TYPE_WPA = 0x13;


	@Override
	protected void finalize()
	{
		try
		{
			super.finalize();
		} catch (Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public WifiConfiguration createWifiInfo(String SSID, String Password, boolean normal)
	{
		int type = TYPE_NO_PASSWD;
		if(TextUtils.isEmpty(Password))
			type = TYPE_NO_PASSWD;
		else
			type = TYPE_WPA;
		
		this.curConnectWifiSSID = SSID;
		
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		if(normal)
			config.SSID = "\"" + SSID + "\"";
		else
			config.SSID=SSID;
		WifiConfiguration tempConfig = this.IsExsits(SSID);
		/*if (tempConfig != null)
		{
			mWifiManager.removeNetwork(tempConfig.networkId);
			mWifiManager.saveConfiguration();
		}*/
		// nopass
		if (type == TYPE_NO_PASSWD)
		{
			// config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			// config.wepTxKeyIndex = 0;
		}
		// wep
		else if (type == TYPE_WEP)
		{
			if (!TextUtils.isEmpty(Password)) {
				if (isHexWepKey(Password)) {
					config.wepKeys[0] = Password;
				} else {
					config.wepKeys[0] = "\"" + Password + "\"";
				}
			}
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		// wpa
		else if (type == TYPE_WPA)
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	public static final int WIFI_CONNECTED = 0x01;
	public static final int WIFI_CONNECT_FAILED = 0x02;
	public static final int WIFI_CONNECTING = 0x03;

	/**
	 * 
	 * 
	 * @param context
	 * @return
	 */
	public int isWifiContected(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		Log.v(TAG,
				"isConnectedOrConnecting = "
						+ wifiNetworkInfo.isConnectedOrConnecting());
		Log.d(TAG,
				"wifiNetworkInfo.getDetailedState() = "
						+ wifiNetworkInfo.getDetailedState());
		if (wifiNetworkInfo.getDetailedState() == DetailedState.OBTAINING_IPADDR
				|| wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTING)
		{
			return WIFI_CONNECTING;
		} else if (wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTED)
		{
			return WIFI_CONNECTED;
		} else
		{
			Log.d(TAG,
					"getDetailedState() == "
							+ wifiNetworkInfo.getDetailedState());
			return WIFI_CONNECT_FAILED;
		}
	}

	private WifiConfiguration IsExsits(String SSID)
	{
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		if(existingConfigs == null)
			return null;
		for (WifiConfiguration existingConfig : existingConfigs)
		{
			if (existingConfig.SSID.equals("\"" + SSID + "\"") /*
																 * &&
																 * existingConfig
																 * .
																 * preSharedKey.
																 * equals("\"" +
																 * password +
																 * "\"")
																 */)
			{
				return existingConfig;
			}
		}
		return null;
	}
	
    public boolean isWifiHasPwd(String ssid) 
    {
    	boolean result = true;
        List<ScanResult> list = mWifiManager.getScanResults();
        if(list == null)
        	return false;
        for (ScanResult scResult : list) 
        {
            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid))
            {
                String capabilities = scResult.capabilities;
                Log.i("hefeng","capabilities=" + capabilities);
                if (!TextUtils.isEmpty(capabilities))
                {
                    if (capabilities.contains("WPA") || capabilities.contains("wpa"))
                    {
                        Log.i("hefeng", "wpa");
                    } 
                    else if (capabilities.contains("WEP") || capabilities.contains("wep")) 
                    {
                        Log.i("hefeng", "wep");
                        result = true;
                    } 
                    else
                    {
                        Log.i("hefeng", "no");
                        result = false;
                    }
                }
                
                break;
            }
        }
        
        return result;
    }

	public void disconnectWifi(int netId)
	{
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}

	public int checkState()
	{
		return mWifiManager.getWifiState();
	}

	public void acquireWifiLock()
	{
		mWifiLock.acquire();
	}

	public void releaseWifiLock()
	{
		if (mWifiLock.isHeld())
		{
			mWifiLock.acquire();
		}
	}

	public void creatWifiLock()
	{
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	public List<WifiConfiguration> getConfiguration()
	{
		return mWifiConfiguration;
	}

	public void connectConfiguration(int index)
	{
		if (index > mWifiConfiguration.size())
		{
			return;
		}
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
	}

	public void startScan()
	{
		mWifiManager.startScan();
		mWifiList = mWifiManager.getScanResults();
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
	}

	public List<ScanResult> getWifiList()
	{
		mWifiManager.startScan();
		mWifiList = mWifiManager.getScanResults();
		return mWifiList;
	}
	
	public boolean ifHasWifi(String ssid)
	{
		boolean result = false;
		if(mWifiList == null || mWifiList.size() == 0)
		{
			startScan();
		}
		if(mWifiList != null && mWifiList.size() > 0)
		{
			int size = mWifiList.size();
			for(int i=0;i<size;i++)
			{
				ScanResult scanResult = mWifiList.get(i);
				if(scanResult.SSID.equals(ssid))
				{
					result = true;
					break;
				}
				
			}
		}
		else
			result = true;
		
		return result;
	}

	public StringBuilder lookUpScan()
	{
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < mWifiList.size(); i++)
		{
			stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
			stringBuilder.append((mWifiList.get(i)).toString());
			stringBuilder.append("/n");
		}
		return stringBuilder;
	}

	public String getMacAddress()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	public String getBSSID()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	public int getIPAddress()
	{
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	public int getNetworkId()
	{
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	public String getWifiInfo()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}
	
	private static boolean isHexWepKey(String wepKey) {
		final int len = wepKey.length();

		// WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
		if (len != 10 && len != 26 && len != 58) {
			return false;
		}

		return isHex(wepKey);
	}

	private static boolean isHex(String key) {
		for (int i = key.length() - 1; i >= 0; i--) {
			final char c = key.charAt(i);
			if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
					&& c <= 'f')) {
				return false;
			}
		}

		return true;
	}
	
	private WifiConfiguration isExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}
	
	
	private Handler Uihandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			String text = (String) msg.obj;
			Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
		};
	};


	private Timer mTimer = null;
	private void startTimer() {
		if (mTimer != null) {
			stopTimer();
		}

		mTimer = new Timer(true);
		if(mTimerTask == null)
		{
			mTimerTask = new TimerTask() {
				@Override
				public void run() {

					String connectedWifi = NetWorkUtils.getWifiName(mContext);
					if(!TextUtils.isEmpty(connectedWifi) && connectedWifi.contains(curConnectWifiName))
					{
						onNotifyWifiConnected();
					}
					else
					{
						onNotifyWifiConnectFailed();
					}

					unRegister();
				}
			};
		}
		mTimer.schedule(mTimerTask, 15 * 1000);
	}

	private TimerTask mTimerTask = null;

	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION))
			{
				Log.d(TAG, "RSSI changed");

				//有可能是正在获取，或者已经获取了
				Log.d(TAG, " intent is " + WifiManager.RSSI_CHANGED_ACTION);

				int connectResult = isWifiContected(mContext);

				if (connectResult == WIFI_CONNECTED)
				{
					stopTimer();
					onNotifyWifiConnected();
					unRegister();
				}
				else if (connectResult == WIFI_CONNECT_FAILED)
				{
					stopTimer();
					//closeWifi();
					unRegister();
					onNotifyWifiConnectFailed();
				}
				else if (connectResult == WIFI_CONNECTING)
				{

				}
			}
		}
	};

	private final int STATE_REGISTRING = 0x01;
	private final int STATE_REGISTERED = 0x02;
	private final int STATE_UNREGISTERING = 0x03;
	private final int STATE_UNREGISTERED = 0x04;

	private int mHaveRegister = STATE_UNREGISTERED;
	private synchronized void register() {
		Log.v(TAG, "register() ##mHaveRegister = " + mHaveRegister);

		if (mHaveRegister == STATE_REGISTRING
				|| mHaveRegister == STATE_REGISTERED) {
			return ;
		}

		mHaveRegister = STATE_REGISTRING;
		myRegisterReceiver(mBroadcastReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
		mHaveRegister = STATE_REGISTERED;

		startTimer();
	}

	private synchronized void unRegister() {
		Log.v(TAG, "unRegister() ##mHaveRegister = " + mHaveRegister);

		if (mHaveRegister == STATE_UNREGISTERED
				|| mHaveRegister == STATE_UNREGISTERING) {
			return ;
		}

		mHaveRegister = STATE_UNREGISTERING;
		myUnregisterReceiver(mBroadcastReceiver);
		mHaveRegister = STATE_UNREGISTERED;
	}
	
}
