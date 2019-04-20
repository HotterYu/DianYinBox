package com.znt.push.httpmodel;

import android.util.Log;

import com.google.gson.Gson;
import com.znt.lib.bean.ResponseBean;
import com.znt.push.db.DBMediaHelper;

import java.io.IOException;
import java.net.URL;

import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * Created by hzwangchenyan on 2017/2/8.
 */
public abstract class CommonCallback<T>  extends BaseCallBack<T>
{
    private Class<T> clazz;
    private Gson gson;

    public CommonCallback(Class<T> clazz)
    {
        this.clazz = clazz;
        gson = new Gson();
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException
    {
        try
        {
            String jsonString = response.body().string();
            saveJsonToLocal(response,jsonString);
            return gson.fromJson(jsonString, clazz);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void saveJsonToLocal(Response response,String jsonString)
    {
        try
        {
            HttpUrl httpUrl = response.request().url();
            URL url = httpUrl.url();
            String key = url.toString();

            if(key.equals(HttpAPI.GET_CUR_ADV_PLAN)
                    ||key.equals(HttpAPI.GET_CUR_PLAN))
            {
                ResponseBean mResponseBean = new ResponseBean();
                mResponseBean.setKey(key);
                mResponseBean.setValue(jsonString);

                DBMediaHelper.getInstance().addResponseInfo(mResponseBean);
                //String res = DBMediaHelper.getInstance().getResposeInfo(key);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}