package com.znt.lib.bean;

import java.io.Serializable;

/**
 * Created by prize on 2018/10/31.
 */

public class InitTerminalResultBean implements Serializable
{
    private String resultcode;
    private String message;

    private InitTerminalInfo data;


    public boolean isSuccess()
    {
        return resultcode != null && resultcode.equals("1");
    }

    public InitTerminalInfo getData() {
        return data;
    }

    public void setData(InitTerminalInfo data) {
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
