package com.znt.push.http.callback;


import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;


public abstract class BaseCallBack extends Callback<String>
{

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
