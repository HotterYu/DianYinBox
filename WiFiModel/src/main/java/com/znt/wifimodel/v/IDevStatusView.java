package com.znt.wifimodel.v;

import java.util.List;

import com.znt.lib.bean.MediaInfor;

public interface IDevStatusView 
{
	public void onGetDevStatus();
	public void onCheckDelay();
	public void getLocalPlayListAndPlay(List<MediaInfor> tempSongList);
}
