package com.znt.push.entity;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.znt.lib.bean.DeviceInfor;
import com.znt.lib.utils.SystemUtils;

import org.json.JSONObject;

public class LocalDataEntity
{

	private Context context = null;

	private static LocalDataEntity INSTANCE = null;

	private final String DEVICE_ID_OLD = "WS_DEVICE_ID";
	private final String DEVICE_ID = "NEW_DEVICE_ID";
	private final String DEVICE_NAME = "NEW_DEVICE_NAME";
	private final String WIFI_PWD = "NEW_WIFI_PWD";
	private final String WIFI_SSID = "NEW_WIFI_SSID";
	private final String IS_INIT = "NEW_IS_INIT";

	private final String LOCAL_MUSIC_INDEX = "NEW_LOCAL_MUSIC_INDEX";
	private final String SEEK_POS = "NEW_SEEK_POS";
	private final String CUR_LAST_UPDATE_TIME = "NEW_CUR_LAST_UPDATE_TIME";
	private final String DEVICE_ADDR = "NEW_DEVICE_ADDR";
	private final String DEVICE_LAT = "NEW_DEVICE_LAT";
	private final String DEVICE_LON = "NEW_DEVICE_LON";
	private final String DEVICE_CODE_OLD = "DEVICE_CODE";
	private final String DEVICE_CODE = "NEW_DEVICE_CODE";
	private final String ACT_CODE = "NEW_ACT_CODE";
	private final String MUSIC_UPDATE_TIME = "NEW_MUSIC_UPDATE_TIME";
	private final String PLAN_TIME = "NEW_PLAN_TIME";
	private final String PLAN_ID = "NEW_PLAN_ID";
	private final String AD_PLAN_ID = "NEW_AD_PLAN_ID";
	private final String AD_PLAN_TIME = "NEW_AD_PLAN_TIME";
	private final String DB_VERSION = "NEW_DB_VERSION";
	private final String VOLUME = "NEW_VOLUME";
	private final String DOWNLOADFLAG = "NEW_DOWNLOADFLAG";
	private final String VIDEO_WHIRL = "NEW_VIDEO_WHIRL";
	private final String LAST_SERVER_TIME = "NEW_LAST_SERVER_TIME";
	private final String LAST_REBOOT_TIME = "NEW_LAST_REBOOT_TIME";
	private final String REBOOT_COUNT = "NEW_REBOOT_COUNT";

	private final String VOLUME_SET_SWITCH = "VOLUME_SET_SWITCH";
	private final String WIFI_SET_SWITCH = "WIFI_SET_SWITCH";

	private final String RESTART_APP = "RESTART_APP";

	public void setVolumeSet(boolean isInit)
	{
		sharedPre.setData(VOLUME_SET_SWITCH, isInit);
	}
	public boolean isVolumeSetOpen()
	{
		return sharedPre.getData(VOLUME_SET_SWITCH, true);
	}

	public void setWifiSet(boolean isInit)
	{
		sharedPre.setData(WIFI_SET_SWITCH, isInit);
	}
	public boolean isWifiSetOpen()
	{
		return sharedPre.getData(WIFI_SET_SWITCH, true);
	}

	public void setRestartAppFlag(String flag)
	{
		sharedPre.setData(RESTART_APP, flag);
	}
	public String getRestartAppFlag()
	{
		return sharedPre.getData(RESTART_APP, "0");
	}

	private MySharedPreference sharedPre = null;

	public LocalDataEntity(Context context)
	{
		this.context = context;
		sharedPre = MySharedPreference.newInstance(context);
	}
	public static LocalDataEntity newInstance(Context context)
	{
		if(INSTANCE == null)
		{
			synchronized (LocalDataEntity.class)
			{
				if(INSTANCE == null)
					INSTANCE = new LocalDataEntity(context);
			}
		}
		return INSTANCE;
	}

	public void setIsInit(boolean isInit)
	{
		sharedPre.setData(IS_INIT, isInit);
	}
	public boolean isInit()
	{
		return sharedPre.getData(IS_INIT, true);
	}

	public void setDbVersion(int version)
	{
		sharedPre.setData(DB_VERSION, version);
	}
	public int getDbVersion()
	{
		return sharedPre.getData(DB_VERSION, 0);
	}

	/*public void setVolume(String volume)
	{
		sharedPre.setData(VOLUME, volume);
	}
	public int getVolume()
	{
		String volume = sharedPre.getData(VOLUME, "0");
		if(!TextUtils.isEmpty(volume))
			return Integer.parseInt(volume);
		else
			return 0;
	}*/
	public void setDownloadFlag(String downloadFlag)
	{
		sharedPre.setData(DOWNLOADFLAG, downloadFlag);
	}
	public String getDownloadFlag()
	{
		return sharedPre.getData(DOWNLOADFLAG, "0");
	}

	public void clearWifiRecord()
	{
		sharedPre.setData(WIFI_SSID, "");
		sharedPre.setData(WIFI_PWD, "");
	}

	public void setDeviceInfor(DeviceInfor infor)
	{
		if(infor == null)
			return;

		/*if(!TextUtils.isEmpty(infor.getId()))
			sharedPre.setData(DEVICE_ID, getDeviceId());*/
		if(!TextUtils.isEmpty(infor.getName()))
			sharedPre.setData(DEVICE_NAME, infor.getName());
		if(!TextUtils.isEmpty(infor.getWifiName()))
			sharedPre.setData(WIFI_SSID, infor.getWifiName());
		//if(!TextUtils.isEmpty(infor.getWifiPwd()))
		sharedPre.setData(WIFI_PWD, infor.getWifiPwd());
		if(!TextUtils.isEmpty(infor.getAddr()))
			sharedPre.setData(DEVICE_ADDR, infor.getAddr());
		if(!TextUtils.isEmpty(infor.getCode()))
			sharedPre.setData(DEVICE_CODE, infor.getCode());
		if(!TextUtils.isEmpty(infor.getActCode()))
			sharedPre.setData(ACT_CODE, infor.getActCode());
	}
	public DeviceInfor getDeviceInfor()
	{
		DeviceInfor infor = new DeviceInfor();
		String id = sharedPre.getData(DEVICE_ID, getDeviceId());
		Build b = new Build();
		String name = "";
		String wifissid = sharedPre.getData(WIFI_SSID, "");
		String wifiPwd = sharedPre.getData(WIFI_PWD, "");
		String code = sharedPre.getData(DEVICE_CODE, "");
		String actCode = sharedPre.getData(ACT_CODE, "");
		name = sharedPre.getData(DEVICE_NAME, "");

		String lat = sharedPre.getData(DEVICE_LAT, "");
		String lon = sharedPre.getData(DEVICE_LON, "");
		String addr = sharedPre.getData(DEVICE_ADDR, "");
		infor.setLon(lon);
		infor.setLat(lat);
		infor.setAddr(addr);
		if(TextUtils.isEmpty(name))
			name = "DianYinBox_" + b.MODEL;
		infor.setId(getDeviceId());
		infor.setName(name);
		infor.setWifiName(wifissid);
		infor.setWifiPwd(wifiPwd);
		infor.setCode(code);
		infor.setActCode(actCode);
		return infor;
	}

	public void setDeviceId(String devId)
	{
		sharedPre.setData(DEVICE_ID, devId);
	}
	public String getDeviceId()
	{
		return sharedPre.getData(DEVICE_ID, "");
		/*if(TextUtils.isEmpty(Constant.DEVICE_MAC))
		{
			String macAddr = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);;
			Constant.DEVICE_MAC = macAddr +  Constant.UUID_TAG;
			return Constant.DEVICE_MAC;//sharedPre.getData(DEVICE_ID, NetWorkUtils.getMacAddress() + Constant.UUID_TAG);
		}
		else
			return Constant.DEVICE_MAC;*/
	}

	public String getOldDeviceId()
	{
		return sharedPre.getData(DEVICE_CODE_OLD, "");
	}
	public void setOldDeviceId(String id)
	{
		sharedPre.setData(DEVICE_CODE_OLD, id);
	}
	public void clearOldDeviceId()
	{
		sharedPre.setData(DEVICE_CODE_OLD, "");
	}


	public String encodeDeviceInfor()
	{

		Build b = new Build();
		String device_name = sharedPre.getData(DEVICE_NAME, "DianYin_" + b.MODEL);
		String device_ssid = sharedPre.getData(WIFI_SSID, "");
		String device_pwd = sharedPre.getData(WIFI_PWD, "");

		JSONObject json = new JSONObject();
		try
		{
			json.put("device_name", device_name);
			json.put("device_id", getDeviceId());
			json.put("device_code", getDeviceCode());
			json.put("device_version", SystemUtils.getVersionName(context));
			json.put("device_ssid", device_ssid);
			json.put("device_pwd", device_pwd);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json.toString();
	}

	public void updateWifi(String wifiName, String wifiPwd)
	{
		if(!TextUtils.isEmpty(wifiName))
			sharedPre.setData(WIFI_SSID, wifiName);
		//if(!TextUtils.isEmpty(wifiPwd))
		sharedPre.setData(WIFI_PWD, wifiPwd);
	}
	public String getWifiName()
	{
		return sharedPre.getData(WIFI_SSID, "");
	}
	public String getWifiPwd()
	{
		return sharedPre.getData(WIFI_PWD, "");
	}

	public String getMusicUpdateTime()
	{
		return sharedPre.getData(MUSIC_UPDATE_TIME, "");
	}
	public void setMusicUpdateTime(String time)
	{
		sharedPre.setData(MUSIC_UPDATE_TIME, time);
	}

	public void setPlanId(String planId)
	{
		sharedPre.setData(PLAN_ID, planId);
	}
	public String getPlanId()
	{
		return sharedPre.getData(PLAN_ID, "");
	}

	public void setAdPlanId(String adPlanId)
	{
		sharedPre.setData(AD_PLAN_ID, adPlanId);
	}
	public String getAdPlanId()
	{
		return sharedPre.getData(AD_PLAN_ID, "");
	}

	public void setAdPlanTime(String adPlanTime)
	{
		sharedPre.setData(AD_PLAN_TIME, adPlanTime);
	}
	public String getAdPlanTime()
	{
		return sharedPre.getData(AD_PLAN_TIME, "");
	}

	public void setPlanTime(String planTime)
	{
		sharedPre.setData(PLAN_TIME, planTime);
	}
	public String getPlayTime()
	{
		return sharedPre.getData(PLAN_TIME, "");
	}

	public void setDeviceCode(String code)
	{
		sharedPre.setData(DEVICE_CODE, code);
	}
	public String getDeviceCode()
	{
		return sharedPre.getData(DEVICE_CODE, "");
	}

	public void setMusicIndex(int index)
	{
		sharedPre.setData(LOCAL_MUSIC_INDEX, index);
	}
	public int getMusicIndex()
	{
		return sharedPre.getData(LOCAL_MUSIC_INDEX, 0);
	}
	public void setSeekPos(int index)
	{
		sharedPre.setData(SEEK_POS, index);
	}
	public int getSeekPos()
	{
		return sharedPre.getData(SEEK_POS, -1);
	}
	public void setCurLastUpdateTime(long index)
	{
		sharedPre.setData(CUR_LAST_UPDATE_TIME, index);
	}
	public long getCurLastUpdateTime()
	{
		return sharedPre.getDataLong(CUR_LAST_UPDATE_TIME, -1);
	}

	public void clearDeviceInfor()
	{
		setDeviceLocation("", "");
		setDeviceName("");
		setDeviceAddr("");
	}
	public void setDeviceLocation(String lon, String lat)
	{
		sharedPre.setData(DEVICE_LAT, lat);
		sharedPre.setData(DEVICE_LON, lon);
	}
	public void setDeviceAddr(String addr)
	{
		sharedPre.setData(DEVICE_ADDR, addr);
	}

	public void setDeviceName(String name)
	{
		sharedPre.setData(DEVICE_NAME, name);
	}
	public String getDeviceName()
	{
		return sharedPre.getData(DEVICE_NAME, android.os.Build.MODEL + "_BOX");
	}

	public String getDeviceAddr()
	{
		return sharedPre.getData(DEVICE_ADDR, "");
	}
	public String getDeviceLat()
	{
		return sharedPre.getData(DEVICE_LAT, "");
	}
	public String getDeviceLon()
	{
		return sharedPre.getData(DEVICE_LON, "");
	}

	public void setVideoWhirl(String videoWhirl)
	{
		sharedPre.setData(VIDEO_WHIRL, videoWhirl);
	}
	public String getVideoWhirl()
	{
		return sharedPre.getData(VIDEO_WHIRL, "");
	}

	public void setLastServerTime(long lastServerTime)
	{
		sharedPre.setData(LAST_SERVER_TIME, lastServerTime);
	}
	public long getLastServerTime()
	{
		return sharedPre.getDataLong(LAST_SERVER_TIME, 0);
	}
	public void setLastRebootTime(long lastServerTime)
	{
		sharedPre.setData(LAST_REBOOT_TIME, lastServerTime);
	}
	public long getLastRebootTime()
	{
		return sharedPre.getDataLong(LAST_REBOOT_TIME, 0);
	}
	public void setRebootCount(int rebootCount)
	{
		sharedPre.setData(REBOOT_COUNT, rebootCount);
	}
	public void increaseRebootCount()
	{
		int localRebootCount = getRebootCount();
		localRebootCount += 1;
		sharedPre.setData(REBOOT_COUNT, localRebootCount);
	}
	public int getRebootCount()
	{
		return sharedPre.getData(REBOOT_COUNT, 0);
	}

}
