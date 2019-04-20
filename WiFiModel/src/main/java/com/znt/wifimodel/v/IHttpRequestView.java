package com.znt.wifimodel.v;

public interface IHttpRequestView 
{
	public void requestStart(int requestId);
	public void requestError(int requestId, String error);
	public void requestSuccess(Object obj, int requestId);
	public void requestNetWorkError();
}
