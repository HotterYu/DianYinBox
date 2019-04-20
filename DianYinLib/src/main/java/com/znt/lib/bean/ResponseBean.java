package com.znt.lib.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by prize on 2018/10/31.
 */
@Table(name = "response_bean")
public class ResponseBean implements Serializable
{
    @Column(name = "key", isId = true)
    private String key;
    @Column(name = "value")
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
