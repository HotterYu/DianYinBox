package com.znt.lib.bean;

public class DeviceStatusInfor
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
	private String wifiFlag = "";//wifi是否变更（设备用）0-否1-是，变更后调用获取wifi接口—已加
	private String playCmd = "";//0-播放 1-暂停 2-跳过 3-有插播
	private String volume = "";
	private String shopcode = "";
	private String shopname = "";
	private String onlineStatus = "";
	private String sourceType = "";//0-店音 1-腾讯

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
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
