
package com.znt.push.httpmodel;


/** 
 * @ClassName: HttpBaseConnect 
 * @Description: TODO
 * @author yan.yu 
 */
public class HttpAPI
{
	//public static final String SERVER_ADDRESS = "http://47.104.209.249:8080/api";
	public static final String SERVER_ADDRESS = "http://api.zhunit.com/api";


	//检查升级
	public static final String CHECK_UPDATE = SERVER_ADDRESS + "/terminal/lastversion";

	//设备初始化
	public static final String INIT_TERMINAL = SERVER_ADDRESS + "/terminal/init";

	//设备注册
	public static final String REGISTER = SERVER_ADDRESS + "/terminal/register";

	//设备登陆
	public static final String LOGIN = SERVER_ADDRESS + "/terminal/login";

	//更新设备信息
	public static final String UPDATE_DEV_INFO = SERVER_ADDRESS + "/terminal/update";

	//添加设备
	public static final String ADD_BOX = SERVER_ADDRESS + "/terminal/addbox";

	//获取状态、轮询
	public static final String GET_DEVICE_STATUS = SERVER_ADDRESS + "/terminal/status";

	//获取当前播放计划
	public static final String GET_CUR_PLAN = SERVER_ADDRESS + "/terminal/plan";

	//获取当前广告计划
	public static final String GET_CUR_ADV_PLAN = SERVER_ADDRESS + "/terminal/adplan";

	//获取推送的歌曲列表
	public static final String GET_PUSH_MEDIAS = SERVER_ADDRESS + "/terminal/pushlist";

	public static final String GET_CUR_PLAY_MUSICS = SERVER_ADDRESS + "/play/now";

	//获取时段的歌曲列表
	public static final String GET_SCHEDULE_MUSICS = SERVER_ADDRESS + "/terminal/planschemusic";

	//播放历史上报
	public static final String PLAY_RECORD_REPORT = SERVER_ADDRESS + "/terminal/play";

	//终端错误上报
	public static final String DEV_ERROR_REPORT = SERVER_ADDRESS + "/terminal/error";

	//获取WIFI信息
	public static final String DEV_GET_WIFI_INFO = SERVER_ADDRESS + "/terminal/wifi";

	//WIFI配置信息上报
	public static final String WIFI_CONFIG_CHECK = SERVER_ADDRESS + "/terminal/wifiConn";

			
}
 
