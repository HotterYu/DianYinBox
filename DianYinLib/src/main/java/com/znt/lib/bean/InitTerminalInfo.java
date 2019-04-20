package com.znt.lib.bean;

import java.io.Serializable;

/**
 * Created by prize on 2018/10/31.
 */

public class InitTerminalInfo  implements Serializable
{

    private String serverIp = "";
    private String systemTime = "";
    private String code = "";
    private TerminalRunstatusInfo trs;

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TerminalRunstatusInfo getTrs() {
        return trs;
    }

    public void setTrs(TerminalRunstatusInfo trs) {
        this.trs = trs;
    }
}
