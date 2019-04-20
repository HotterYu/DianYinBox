package com.znt.push.http.callback;

import android.text.TextUtils;

import com.zhy.http.okhttp.callback.Callback;
import com.znt.lib.bean.DeviceStatusInfor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public abstract class DevStatusCallBack extends Callback<DeviceStatusInfor>
{
    @Override
    public DeviceStatusInfor parseNetworkResponse(Response response,int requestId) throws IOException
    {
    	DeviceStatusInfor deviceStatusInfor = null;
    	if(response.isSuccessful())
    	{
    		String string = response.body().string();
    		try
    		{
    			JSONObject jsonObject = new JSONObject(string);
    			int result = jsonObject.getInt(RESULT_OK);
				if(result == RESULT_SUCCESS)
				{
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					if(TextUtils.isEmpty(info))
					{
						deviceStatusInfor = null;
					}
					else
					{
						JSONObject json = new JSONObject(info);
						/*deviceStatusInfor = new DeviceStatusInfor();
						String vodFlag = getInforFromJason(json, "vodFlag");
						String planId = getInforFromJason(json, "planId");
						String adplanId = getInforFromJason(json, "adplanId");
						String adUpdateTime = getInforFromJason(json, "adUpdateTime");
						String planTime = getInforFromJason(json, "planTime");
						String playStatus   = getInforFromJason(json, "playStatus  ");
						String lastMusicUpdate  = getInforFromJason(json, "lastMusicUpdate");
						String sysLastVersionNum  = getInforFromJason(json, "sysLastVersionNum");
						String pushStatus  = getInforFromJason(json, "pushStatus");
						String volume  = getInforFromJason(json, "volume");
						String downloadFlag  = getInforFromJason(json, "downloadFlag");
						String videoWhirl  = getInforFromJason(json, "videoWhirl");
						String wifiName  = getInforFromJason(json, "wifiName");
						String wifiPassword  = getInforFromJason(json, "wifiPassword");
						String playingPos  = getInforFromJason(json, "playingPos");
						deviceStatusInfor.setLastVersionNum(sysLastVersionNum);
						deviceStatusInfor.setVodFlag(vodFlag);
						deviceStatusInfor.setMusicLastUpdate(lastMusicUpdate);
						deviceStatusInfor.setPlanId(planId);
						deviceStatusInfor.setAdplanId(adplanId);
						deviceStatusInfor.setPlanTime(planTime);
						deviceStatusInfor.setPlayStatus(playStatus);
						deviceStatusInfor.setPushStatus(pushStatus);
						deviceStatusInfor.setPlayingPos(playingPos);
						deviceStatusInfor.setVolume(volume);
						deviceStatusInfor.setDownloadFlag(downloadFlag);
						deviceStatusInfor.setVideoWhirl(videoWhirl);
						deviceStatusInfor.setAdUpdateTime(adUpdateTime);

						deviceStatusInfor.setWifiName(wifiName);
						deviceStatusInfor.setWifiPwd(wifiPassword);*/
					}

					
					return deviceStatusInfor;
					
					
				}
    		}
    		catch (Exception e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
        return null;
    }

	protected String RESULT_INFO = "data";
	protected String RESULT_OK = "resultcode";
	protected int RESULT_SUCCESS = 1;
	protected int RESULT_FAILE = 0;

	protected String getInforFromJason(JSONObject json, String key)
	{
		if(json == null || key == null)
			return "";
		if(json.has(key))
		{
			try
			{
				String result = json.getString(key);
				if(result.equals("null"))
					result = "";
				return result;
				//return StringUtils.decodeStr(result);
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
}