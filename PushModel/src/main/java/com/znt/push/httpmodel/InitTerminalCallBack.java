package com.znt.push.httpmodel;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by prize on 2018/10/31.
 */

public class InitTerminalCallBack<T>  extends BaseCallBack<T>
{
    private Class<T> clazz;
    private Gson gson;

    public InitTerminalCallBack(Class<T> clazz)
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
            JSONObject json = new JSONObject(jsonString);

            String data = getInfoFromJson(json, "data");
            //if(data == null)

            return gson.fromJson(data, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onError(Call call, Exception e, int id) {

    }

    @Override
    public void onResponse(T response, int id) {

    }
}