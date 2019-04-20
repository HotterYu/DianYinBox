package com.znt.push.callback;

import com.znt.lib.bean.UpdateInfor;
import com.znt.push.http.callback.BaseCallBack;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public abstract class CheckUpdateCallBack extends BaseCallBack
{

	protected String RESULT_INFO = "info";
	protected String RESULT_OK = "result";


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
                if(result == RESULT_SUCCESS)
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