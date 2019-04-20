package com.znt.lib.bean;

import java.io.Serializable;
import java.security.SecureRandom;

public class BaseResultBean implements Serializable
{
    private String resultcode;
    private String message;

    public boolean isSuccess()
    {
        return resultcode != null && resultcode.equals("1");
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
