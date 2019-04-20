package com.znt.push.callback;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhy.http.okhttp.callback.Callback;
import com.znt.lib.bean.DeviceStatusInfor;
import com.znt.push.http.callback.BaseCallBack;

import okhttp3.Response;


public abstract class DevStatusCallBack extends BaseCallBack
{
	protected String RESULT_INFO = "info";
	protected String RESULT_OK = "result";

    @Override
    public String parseNetworkResponse(Response response,int requestId) throws IOException
    {
    	DeviceStatusInfor deviceStatusInfor = null;
    	if(response.isSuccessful())
    	{
    		String string = response.body().string();
    		try
    		{
    			JSONObject jsonObject = new JSONObject(string);
    			int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					/*JSONObject json = new JSONObject(info);
					deviceStatusInfor = new DeviceStatusInfor();
					String vodFlag = getInforFromJason(json, "vodFlag");
					String planId = getInforFromJason(json, "planId");
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
					//String playingPos  = getInforFromJason(json, "playingPos");
					deviceStatusInfor.setLastVersionNum(sysLastVersionNum);
					deviceStatusInfor.setVodFlag(vodFlag);
					deviceStatusInfor.setMusicLastUpdate(lastMusicUpdate);
					deviceStatusInfor.setPlanId(planId);
					deviceStatusInfor.setPlanTime(planTime);
					deviceStatusInfor.setPlayStatus(playStatus);
					deviceStatusInfor.setPushStatus(pushStatus);
					//deviceStatusInfor.setPlayingPos(playingPos);
					deviceStatusInfor.setVolume(volume);
					deviceStatusInfor.setDownloadFlag(downloadFlag);
					deviceStatusInfor.setVideoWhirl(videoWhirl);
					
					deviceStatusInfor.setWifiName(wifiName);
					deviceStatusInfor.setWifiPwd(wifiPassword);*/
					
					return info;
					
					
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
}