package com.znt.lib.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by prize on 2018/10/31.
 */

public class TMECommonResultBean implements Serializable
{
    private String code;
    private String msg;

    boolean isSuccess()
    {
        return !TextUtils.isEmpty(code) && code.equals("10000");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
