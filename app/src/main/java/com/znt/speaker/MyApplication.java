package com.znt.speaker;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;
import com.znt.lib.utils.SystemUtils;
import com.znt.push.entity.Constant;
import com.znt.push.entity.LocalDataEntity;

import org.xutils.x;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        //RePlugin.registerPluginBinder("demo2test", new ZNTPushService.ServiceBinder());

        x.Ext.init(this); //xUtils初始化

        Constant.deviceCode = SystemUtils.getAndroidId(this) + "_BOX";

        /* Bugly SDK初始化
        * 参数1：上下文对象
        * 参数2：APPID，平台注册时得到,注意替换成你的appId
        * 参数3：是否开启调试模式，调试模式下会输出'CrashReport'tag的日志
        */
        CrashReport.initCrashReport(getApplicationContext(), "faed430f12", false);

        String deviId = LocalDataEntity.newInstance(getApplicationContext()).getDeviceId();

        CrashReport.putUserData(getApplicationContext(),"DeviceId",deviId);


    }
}