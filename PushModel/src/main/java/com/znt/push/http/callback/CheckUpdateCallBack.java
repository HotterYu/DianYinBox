package com.znt.push.http.callback;

import com.znt.lib.bean.UpdateInfor;

import java.io.IOException;

import org.json.JSONObject;

import okhttp3.Response;

public abstract class CheckUpdateCallBack extends BaseCallBack
{
    @Override
    public String parseNetworkResponse(Response response,int requestId) throws IOException
    {
    	UpdateInfor updateInfor = null;
    	if(response.isSuccessful())
    	{
    		String string = response.body().string();
    		try
    		{
    			JSONObject jsonObject = new JSONObject(string);
    			int result = jsonObject.getInt(RESULT_OK);
                if(result == 0)
                {
                	
	               	 JSONObject json = jsonObject.getJSONObject(RESULT_INFO);
	               	 /*updateInfor = new UpdateInfor();
	               	 String versionName = getInforFromJason(json, "version");
	               	 String versionNum = getInforFromJason(json, "versionNum");
	               	 String apkUrl = getInforFromJason(json, "url");
	               	 String updateType  = getInforFromJason(json, "updateType");
	               	 updateInfor.setApkUrl(apkUrl);
	               	 updateInfor.setUpdateType(updateType);
	               	 updateInfor.setVersionNum(versionNum);
	               	 updateInfor.setVersionName(versionName);*/
	               	 
	               	 
	               	return json.toString();
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