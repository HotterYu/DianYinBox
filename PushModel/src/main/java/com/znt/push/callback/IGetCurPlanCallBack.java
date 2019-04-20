package com.znt.push.callback;


import com.znt.lib.bean.CurPlanInfor;

public interface IGetCurPlanCallBack
{
	public void requestStart(int requestId);
	public void requestFail(int requestId);
	public void requestSuccess(CurPlanInfor curPlanInfor, int requestId);
	
}
