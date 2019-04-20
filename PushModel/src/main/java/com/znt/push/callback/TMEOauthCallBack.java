package com.znt.push.callback;

import android.content.Context;
import android.text.TextUtils;

import com.znt.push.entity.LocalDataEntity;
import com.znt.push.http.callback.BaseCallBack;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public abstract class TMEOauthCallBack extends BaseCallBack
{
	private Context activity = null;
	public TMEOauthCallBack(Context activity)
	{
		this.activity = activity;
	}
	
    @Override
    public String parseNetworkResponse(Response response,int requestId) throws IOException
    {
    	String systemTime = "";
    	if(response.isSuccessful())
    	{
    		String string = response.body().string();
    		try
    		{
    			JSONObject jsonObject = new JSONObject(string);
    			int result = jsonObject.getInt(RESULT_OK);
				if(result == 1)
				{
					String infor = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject json1 = new JSONObject(infor);
					
					systemTime = getInforFromJason(json1, "systemTime");
					String serverIp = getInforFromJason(json1, "serverIp");
					String code = getInforFromJason(json1, "code");
					String trs = getInforFromJason(json1, "trs");
					
					if(!TextUtils.isEmpty(code))
						LocalDataEntity.newInstance(activity).setDeviceCode(code);
					
					//String playingPos = getInforFromJason(jsonObj, "playingPos");
				}
    		}
    		catch (Exception e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
        return systemTime;
    }
}