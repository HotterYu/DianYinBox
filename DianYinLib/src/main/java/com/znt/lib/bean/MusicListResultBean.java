package com.znt.lib.bean;

import java.io.Serializable;
import java.util.List;

public class MusicListResultBean implements Serializable {

    private String resultcode;
    private String message;

    public boolean isSuccess()
    {
        return resultcode.equals("1");
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

    private List<MediaInfor> data;

    public List<MediaInfor> getData() {

        return data;
    }


}
