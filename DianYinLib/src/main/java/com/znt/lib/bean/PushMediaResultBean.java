package com.znt.lib.bean;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prize on 2018/10/31.
 */

public class PushMediaResultBean implements Serializable
{
    private String resultcode;
    private String message;

    private List<PushMediaInfo>  data;


    public boolean isSuccess()
    {
        return resultcode != null && resultcode.equals("1");
    }

    public List<PushMediaInfo> getData() {
        return data;
    }

    public List<MediaInfor> getDataConvert() {

        List<MediaInfor> tempList = new ArrayList<>();

        for(int i=0;i<data.size();i++)
        {
            MediaInfor tempInfor = new MediaInfor();

            PushMediaInfo mPushMediaInfo = data.get(i);

            tempInfor.setMediaUrl(URLDecoder.decode(mPushMediaInfo.getPlayurl()));
            tempInfor.setMediaName(mPushMediaInfo.getPlayname());
            tempInfor.setMediaId(mPushMediaInfo.getDataId());

            tempList.add(tempInfor);
        }

        return tempList;
    }

    public void setData(List<PushMediaInfo>  data) {
        this.data = data;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }


}
