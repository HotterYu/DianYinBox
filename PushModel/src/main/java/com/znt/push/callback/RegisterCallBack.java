package com.znt.push.callback;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;

import com.znt.push.http.callback.BaseCallBack;

import okhttp3.Response;

public abstract class RegisterCallBack extends BaseCallBack
{
    @Override
    public String parseNetworkResponse(Response response,int requestId) throws IOException
    {
    	
    	if(response.isSuccessful())
    	{
    		String string = response.body().string();
    		try
    		{
    			Log.e("", "***************RegisterCallBack response-->" + string);
    			JSONObject jsonObject = new JSONObject(string);
    			int result = jsonObject.getInt(RESULT_OK);
    			if(result == 0)
    				return getInforFromJason(jsonObject, RESULT_INFO);
    		}
    		catch (JSONException e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			Log.e("", "***************error-->" + e.getMessage());
    		}
    	}
    		
        return null;
    }
}