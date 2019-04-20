package com.znt.push.http;



public class TMEHttpAPI
{
	public static final String SERVER_ADDRESS_OAUTH = "http://212.64.42.91:10020";//授权服务器
	public static final String SERVER_ADDRESS = "http://212.64.42.91:10022";//业务服务器


	//设备注册
	public static final String REGISTER = SERVER_ADDRESS + "/api/terminal/register";

	//获取状态
	public static final String GET_DEVICE_STATUS = SERVER_ADDRESS + "/api/terminal/dianyin/{udid}/updateStatus";

	//获取授权令牌
	public static final String OAUTH = SERVER_ADDRESS_OAUTH + "/oauth/token";

	//歌曲信息上报
	public static final String SongReport = SERVER_ADDRESS + "/api/terminal/dianyin/song/report";

	//终端信息获取
	public static final String DEV_INFO = SERVER_ADDRESS + "/api/terminal/dianyin/{udid}/info";

}
 

