package com.znt.wifimodel.netset;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiState.WifiStateListener;
import com.thanosfisherman.wifiutils.wifiWps.ConnectionWpsListener;
import com.znt.lib.bean.WifiInfor;
import com.znt.lib.utils.ViewUtils;
import com.znt.wifimodel.db.DBManager;
import com.znt.wifimodel.entity.WifiLocalDataEntity;
import com.znt.wifimodel.v.INetWorkView;

import java.util.List;

public class NetWorkPresenter
{
	private Context mContext = null;
	private INetWorkView iNetWorkView = null;

	private String curConnectWifiName = "";
	private String curConnectWifiPwd = "";
	private boolean isConnectRunning = false;
	private int wifiIndex = 0;
	private int loopConnectCount = 0;

	private final int MSG_CONNECT_WIFI_SUCCESS = 0;
	private final int MSG_CONNECT_WIFI_FAIL = 1;
	private final int MSG_OPEN_WIFI_FAIL = 2;
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == MSG_CONNECT_WIFI_SUCCESS)
			{
				doconenctWifiSuccess();
			}
			else if(msg.what == MSG_CONNECT_WIFI_FAIL)
			{
				doconenctWifiFail();
			}
			else if(msg.what == MSG_OPEN_WIFI_FAIL)
			{
				if(iNetWorkView != null)
					iNetWorkView.openWifiFail();
			}
			super.handleMessage(msg);
		}
	};

	public NetWorkPresenter(Context activity, INetWorkView iNetWorkView)
	{
		this.iNetWorkView = iNetWorkView;
		this.mContext = activity;
	}

	public void checkWifiState()
	{
		if(isConnectRunning)
			return;
		isConnectRunning = true;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if(!WifiUtils.withContext(mContext).isWifiEnabled())
				{
					enableWifi();
				}
				else
				{
					resetLoopConnextCount();
					connectNextWifi();
				}
			}
		});
	}

	public void connectCurWifi(final String wifiName, final String wifiPwd)
	{
		if(iNetWorkView != null)
			iNetWorkView.connectWifiSatrt(wifiName);

		resetLoopConnextCount();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if(!WifiUtils.withContext(mContext).isWifiEnabled())
				{
					enableWifi();
				}
				else
				{
					connectWithWpa(wifiName, wifiPwd);
				}
			}
		});
	}

	private void connectNextWifi()
	{
		if(DBManager.INSTANCE.getWifiCount() == 0)
		{
			ViewUtils.sendMessage(mHandler,MSG_CONNECT_WIFI_FAIL);
			return;
		}

		if(wifiIndex < DBManager.INSTANCE.getWifiCount())
		{
			WifiInfor tempInfor = getWifiInforByIndex();
			wifiIndex ++;

			connectWithWpa(tempInfor.getWifiName(), tempInfor.getWifiPassword());
		}
		else
		{
			wifiIndex = 0;
			ViewUtils.sendMessage(mHandler,MSG_CONNECT_WIFI_FAIL);
			/*loopConnectCount ++;

			if(loopConnectCount == 2)
				ViewUtils.sendMessage(mHandler,MSG_CONNECT_WIFI_FAIL);
			else
			{
				wifiIndex = 0;
				connectNextWifi();
			}*/
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
		wifiIndex = 0;
		loopConnectCount = 0;
	}

	private void doconenctWifiFail()
	{
		isConnectRunning = false;
		if(iNetWorkView != null)
			iNetWorkView.connectWifiFailed(curConnectWifiName, curConnectWifiPwd);
	}
	private void doconenctWifiSuccess()
	{
		isConnectRunning = false;
		WifiLocalDataEntity.newInstance(mContext).updateWifi(curConnectWifiName, curConnectWifiPwd);

		DBManager.INSTANCE.insertWifi(curConnectWifiName, curConnectWifiPwd);

		if(iNetWorkView != null)
			iNetWorkView.connectWifiSuccess(curConnectWifiName, curConnectWifiPwd);
	}

	private void connectWithWpa(final String wifiName, final String wifiPwd)
	{
		this.curConnectWifiName = wifiName;
		this.curConnectWifiPwd = wifiPwd;

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				WifiUtils.withContext(mContext)
						.connectWith(wifiName, wifiPwd)
						.setTimeout(30 * 1000)
						.onConnectionResult(new ConnectionSuccessListener()
						{
							@Override
							public void isSuccessful(boolean isSuccess)
							{
								checkResult(RESULT_TYPE_WIFI_CONNECT,isSuccess);
							}
						})
						.start();
			}
		});
	}

	private void enableWifi()
	{
		WifiUtils.withContext(mContext).enableWifi(new WifiStateListener() {
			@Override
			public void isSuccess(boolean isSuccess) {
				checkResult(RESULT_TYPE_SWITCH,isSuccess);
			}
		});
	}


	private final int RESULT_TYPE_SWITCH = 0;
	private final int RESULT_TYPE_WIFI_CONNECT = 1;
	private void checkResult(int resultType, boolean isSuccess) {
		if (isSuccess)
		{
			if(resultType == RESULT_TYPE_SWITCH)//WIFI开关的反馈
			{
				resetLoopConnextCount();
				connectNextWifi();
			}
			else if(resultType == RESULT_TYPE_WIFI_CONNECT)//WIFI连接的反馈
			{
				Toast.makeText(mContext, "CONNECT WIFI SUCCESS-->"+curConnectWifiName + "\n" + curConnectWifiPwd, Toast.LENGTH_SHORT).show();
				ViewUtils.sendMessage(mHandler,MSG_CONNECT_WIFI_SUCCESS);
			}
		}
		else
		{
			if(resultType == RESULT_TYPE_SWITCH)//WIFI开关的反馈,开关打开失败了
			{
				//resetLoopConnextCount();
				ViewUtils.sendMessage(mHandler,MSG_OPEN_WIFI_FAIL);
			}
			else if(resultType == RESULT_TYPE_WIFI_CONNECT)//WIFI连接的反馈
			{
				Toast.makeText(mContext, "CONNECT WIFI FAIL-->"+curConnectWifiName + "\n" + curConnectWifiPwd, Toast.LENGTH_SHORT).show();
				connectNextWifi();
			}

		}
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void connectWithWps() {
		WifiUtils.withContext(mContext).connectWithWps("d8:74:95:e6:f5:f8", "12345678").onConnectionWpsResult(new ConnectionWpsListener() {
			@Override
			public void isSuccessful(boolean isSuccess) {
				checkResult(2,isSuccess);
			}
		}).start();
	}
}
