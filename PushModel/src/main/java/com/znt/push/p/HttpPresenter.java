/*
package com.znt.push.p;

import android.content.Context;

import com.znt.lib.bean.MediaInfor;
import com.znt.push.m.HttpRequestModel;
import com.znt.push.v.IHttpRequestView;

public class HttpPresenter
{
	private Context mContext = null;
	private HttpRequestModel httpRequestModel = null;
	
	public HttpPresenter(Context mContext, IHttpRequestView iHttpRequestView)
	{
		this.mContext = mContext;
		httpRequestModel = new HttpRequestModel(mContext, iHttpRequestView);
	}
	
	public boolean isRunning(int id)
	{
		return httpRequestModel.isRuning(id);
	}

	*/
/**
	 * 检查更新
	 *//*

	public void checkUpdate()
	{
		httpRequestModel.checkUpdate();
	}

	*/
/**
	 * 状态轮询
	 * @param id
	 * @param playSeek
	 * @param playingSong
	 * @param playingSongType
	 * @param netInfo
	 *//*

	public void getDevStatus(String id, int playSeek, String playingSong, String playingSongType, String netInfo)
	{
		httpRequestModel.getDevStatus(playSeek, playingSong, playingSongType, id, netInfo);
	}

	*/
/**
	 * 注册
	 *//*

	public void register()
	{
		httpRequestModel.register();
	}

	*/
/**
	 * 设备初始化
	 *//*

	public void initTerminal()
	{
		httpRequestModel.initTerminal();
	}

	*/
/**
	 * 获取广告计划
	 * @param adPlanId
	 *//*

	public void getAdPlan(String adPlanId)
	{
		httpRequestModel.getCurAdvPlan(adPlanId);
	}

	*/
/**
	 * 获取当前播放计划
	 * @param planId
	 *//*

	public void getCurPlan(final String planId)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				httpRequestModel.getCurPlan(planId);
			}
		}).start();
	}

	*/
/**
	 * 获取推送的歌曲列表
	 * @param devId
	 *//*

	public void getPushMedias(String devId)
	{
		httpRequestModel.getPushMedias(devId);
	}

	*/
/**
	 * 上报播放记录
	 * @param devId
	 * @param mediaInfor
	 *//*

	public void reportPlayRecord(String devId, MediaInfor mediaInfor)
	{
		httpRequestModel.reportPlayRecord(devId, mediaInfor);
	}

	*/
/**
	 * 上报系统错误日志
	 * @param softCode
	 * @param terminalId
	 * @param softVersion
	 * @param errormsg
	 *//*

	public void reportErrorLog(String softCode, String terminalId, String softVersion, String errormsg)
	{
		httpRequestModel.reportErrorLog(softCode, terminalId,softVersion,errormsg);
	}
	
	public void cancle(int requestId)
	{
		httpRequestModel.cancelHttp(requestId);
	}
	
}
*/
