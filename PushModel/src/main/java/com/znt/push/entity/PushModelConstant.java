
package com.znt.push.entity; 

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.List;

/** 
 * @ClassName: Constant 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-21 下午3:32:20  
 */
public class PushModelConstant
{
	public static String WORK_DIR = "/DianYinBox";
	public static String WIFI_HOT_PWD = "";
	public static String UUID_TAG = "_znt_ios_rrdg_sp";
	public static String IOS_TAG = "_znt_ios_rrdg_sp";
	public static String PKG_END = "znt_pkg_end";
	
	public static final String MEDIA_ADD = "android.intent.action.MEDIA_MOUNTED";
	public static final String MEDIA_REMOVE = "android.intent.action.MEDIA_REMOVED";
	
	public static String PlayPermission = "0";
	
	public static final int DB_VERSION = 1;
	public static final int LUCNHER_VERSION_CODE = 27;
	public static final int INSTALL_VERSION_CODE = 3;
	public static String DEVICE_MAC = "";
	
	public static int UPDATE_TYPE = 0;
	
	public volatile static int STATUS_CHECK_TIME_MAX = 8;
	
	public static long minRemainSize = 1024 * 1024 * 300;
	
	public static boolean isBoxVersion = true;
	
	public static List<ScanResult> wifiList = new ArrayList<ScanResult>();
	
	
	
	
	
	
	public static final int CALL_BACK_PUSH_SUCCESS = 1000;
	public static final int CALL_BACK_PUSH_FAIL = 1001;
	public static final int CALL_BACK_PUSH_SPACE_CHECK = 1002;
	public static final int CALL_BACK_PUSH_CHECK = 1003;
	public static final int CALL_BACK_INIT_TERMINAL_FINISH = 1004;
	public static final int CALL_BACK_REGISTER_FINISH = 1005;
	public static final int CALL_BACK_GET_CUR_PLAN = 1006;
	public static final int CALL_BACK_GET_CUR_ADV_PLAN = 1007;
	public static final int CALL_BACK_NOTIFY_TIME_UPDATE = 1008;
	public static final int CALL_BACK_ON_TIMEING_PUSH_NOTYFY = 1009;
	public static final int CALL_BACK_ON_INTERNAL_TIME_PUSH_NOTYFY = 1010;
	public static final int CALL_BACK_ON_PUSH_MEDIA_NOTYFY = 1011;
	public static final int CALL_BACK_ON_WIFI_CONGFIG_NOTYFY = 1012;
	public static final int CALL_BACK_ON_UPDATE_CHECK = 1013;
	public static final int CALL_BACK_ON_PLAY_OPEN = 1014;
	public static final int CALL_BACK_ON_PLAY_STATUS = 1015;
}
 
