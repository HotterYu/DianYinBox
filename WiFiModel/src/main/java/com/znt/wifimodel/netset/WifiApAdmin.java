/**   
 * @Title: WifiApAdmin.java
 * @Package com.example.wifitest
 * @Description: TODO
 * @author ychuang
 * @date 2015-1-4 上午9:52:56
 * @version V1.0   
 */
package com.znt.wifimodel.netset;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public class WifiApAdmin
{
	public static final String TAG = "WifiApAdmin";

	public  void closeWifiAp(Context context)
	{
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		closeWifiAp(wifiManager);
	}
	
	private com.znt.wifimoidel.netset.CreateWifiApLisnter mCreateWifiApLisnter;

	private WifiManager mWifiManager = null;

	private Context mContext = null;

	public WifiApAdmin(Context context)
	{
		mContext = context;

		mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);

		closeWifiAp(mWifiManager);
	}

	private String mSSID = "";
	private String mPasswd = "";
	
	public void setCreateWifiApLisnter(com.znt.wifimoidel.netset.CreateWifiApLisnter lisnter)
	{
		mCreateWifiApLisnter = lisnter;
	}

	public void startWifiAp(String ssid)
	{
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		//if(currentapiVersion >= 24 && isAppAvilible(mContext, "com.example.wifiap7"))
		if(currentapiVersion >= 24)//android 7.0
		{
			mWifiManager.setWifiEnabled(true);
			return;
		}
		mSSID = ssid;
		if (mWifiManager.isWifiEnabled())
		{
			mWifiManager.setWifiEnabled(false);
		}
		stratWifiAp();
		com.znt.wifimoidel.netset.MyTimerCheck timerCheck = new com.znt.wifimoidel.netset.MyTimerCheck()
		{
			@Override
			public void doTimerCheckWork()
			{
				// TODO Auto-generated method stub

				if (isWifiApEnabled())
				{
					if (mCreateWifiApLisnter != null)
						mCreateWifiApLisnter.onCreateWifiApSuccess();
					this.exit();
				} 
			}

			@Override
			public void doTimeOutWork()
			{
				// TODO Auto-generated method stub
				if (mCreateWifiApLisnter != null)
					mCreateWifiApLisnter.onCreateWifiApFail();
				this.exit();
			}
		};
		timerCheck.start(60, 1000);

	}

	public void stratWifiAp()
	{
		Method method1 = null;
		try
		{
			method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, boolean.class);
			WifiConfiguration netConfig = new WifiConfiguration();

			netConfig.SSID = mSSID;
			netConfig.preSharedKey = mPasswd;

			netConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			netConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.NONE);
			netConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			netConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			netConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			netConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);

			method1.invoke(mWifiManager, netConfig, true);

		} catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void closeWifiAp(WifiManager wifiManager)
	{
		if (isWifiApEnabled())
		{
			try
			{
				Method method = wifiManager.getClass().getMethod(
						"getWifiApConfiguration");
				method.setAccessible(true);

				WifiConfiguration config = (WifiConfiguration) method
						.invoke(wifiManager);

				Method method2 = wifiManager.getClass().getMethod(
						"setWifiApEnabled", WifiConfiguration.class,
						boolean.class);
				method2.invoke(wifiManager, config, false);
			} catch (NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean isWifiApEnabled()
	{
		try
		{
			Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
			method.setAccessible(true);
			return (Boolean) method.invoke(mWifiManager);

		} catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}
	

	public boolean isAppAvilible(Context context, String packageName) {  
	    PackageManager packageManager = context.getPackageManager();  
	  

	    List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);  
	    for (int i = 0; i < pinfo.size(); i++) {  
	        if (((PackageInfo) pinfo.get(i)).packageName  
	                .equalsIgnoreCase(packageName))  
	            return true;  
	    }  
	    return false;  
	}  
}
