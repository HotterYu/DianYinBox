package com.znt.push.httpmodel;

import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseCallBack<T> extends Callback<T>
{


    public String getInfoFromJson(JSONObject jsonObject, String key)
    {
        if(jsonObject == null)
            return null;
        if(jsonObject.has(key))
        {
            try {
                return jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
