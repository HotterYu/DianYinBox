package com.znt.lib.bean;

import java.io.Serializable;

/**
 * Created by prize on 2018/10/31.
 */

public class TerminalRunstatusInfo  implements Serializable
{

    private String terminalId = "";
    private String downloadFlag = "";
    private String expiredTime = "";
    private String lastBootTime = "";
    private String lastConnTime = "";
    private String lastMusicUpdate = "";
    private String planId = "";
    private String adplanId = "";
    private String adUpdateTime = "";
    private String playSeek = "";
    private String playModel = "";
    private String playingPos = "";
    private String playingSong = "";
    private String playingSongId = "";
    private String playingSongType = "";
    private String softUpdateFlag = "";
    private String videoWhirl = "";
    private String vodFlag = "";
    private String wifiFlag = "";
    private String playCmd = "";
    private String volume = "";
    private String shopcode = "";
    private String shopname = "";
    private String onlineStatus = "";
    private String shutdownTime = "";//格式HH:mm:ss
    private String startupMinute = "";//分钟

    public String getShutdownTime() {
        return shutdownTime;
    }

    public void setShutdownTime(String shutdownTime) {
        this.shutdownTime = shutdownTime;
    }

    public String getStartupMinute() {
        return startupMinute;
    }

    public void setStartupMinute(String startupMinute) {
        this.startupMinute = startupMinute;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getDownloadFlag() {
        return downloadFlag;
    }

    public void setDownloadFlag(String downloadFlag) {
        this.downloadFlag = downloadFlag;
    }

    public String getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(String expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getLastBootTime() {
        return lastBootTime;
    }

    public void setLastBootTime(String lastBootTime) {
        this.lastBootTime = lastBootTime;
    }

    public String getLastConnTime() {
        return lastConnTime;
    }

    public void setLastConnTime(String lastConnTime) {
        this.lastConnTime = lastConnTime;
    }

    public String getLastMusicUpdate() {
        return lastMusicUpdate;
    }

    public void setLastMusicUpdate(String lastMusicUpdate) {
        this.lastMusicUpdate = lastMusicUpdate;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getAdplanId() {
        return adplanId;
    }

    public void setAdplanId(String adplanId) {
        this.adplanId = adplanId;
    }

    public String getAdUpdateTime() {
        return adUpdateTime;
    }

    public void setAdUpdateTime(String adUpdateTime) {
        this.adUpdateTime = adUpdateTime;
    }

    public String getPlaySeek() {
        return playSeek;
    }

    public void setPlaySeek(String playSeek) {
        this.playSeek = playSeek;
    }

    public String getPlayModel() {
        return playModel;
    }

    public void setPlayModel(String playModel) {
        this.playModel = playModel;
    }

    public String getPlayingPos() {
        return playingPos;
    }

    public void setPlayingPos(String playingPos) {
        this.playingPos = playingPos;
    }

    public String getPlayingSong() {
        return playingSong;
    }

    public void setPlayingSong(String playingSong) {
        this.playingSong = playingSong;
    }

    public String getPlayingSongId() {
        return playingSongId;
    }

    public void setPlayingSongId(String playingSongId) {
        this.playingSongId = playingSongId;
    }

    public String getPlayingSongType() {
        return playingSongType;
    }

    public void setPlayingSongType(String playingSongType) {
        this.playingSongType = playingSongType;
    }

    public String getSoftUpdateFlag() {
        return softUpdateFlag;
    }

    public void setSoftUpdateFlag(String softUpdateFlag) {
        this.softUpdateFlag = softUpdateFlag;
    }

    public String getVideoWhirl() {
        return videoWhirl;
    }

    public void setVideoWhirl(String videoWhirl) {
        this.videoWhirl = videoWhirl;
    }

    public String getVodFlag() {
        return vodFlag;
    }

    public void setVodFlag(String vodFlag) {
        this.vodFlag = vodFlag;
    }

    public String getWifiFlag() {
        return wifiFlag;
    }

    public void setWifiFlag(String wifiFlag) {
        this.wifiFlag = wifiFlag;
    }

    public String getPlayCmd() {
        return playCmd;
    }

    public void setPlayCmd(String playCmd) {
        this.playCmd = playCmd;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getShopcode() {
        return shopcode;
    }

    public void setShopcode(String shopcode) {
        this.shopcode = shopcode;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}
