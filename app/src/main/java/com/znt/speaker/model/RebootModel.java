package com.znt.speaker.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.znt.lib.utils.DateUtils;
import com.znt.lib.utils.PluginConstant;
import com.znt.lib.utils.ShellUtils;
import com.znt.push.entity.LocalDataEntity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by prize on 2019/4/15.
 */

public class RebootModel
{

    private Activity activity = null;

    private int checkRebootStatusCount = 0;

    public RebootModel(Activity activity)
    {
        this.activity = activity;
    }

    public void checkRebootDevice(long curServerTime)
    {
        if(checkRebootStatusCount < 100)
        {
            checkRebootStatusCount ++;
            return;
        }
        checkRebootStatusCount = 0;
        try
        {
            if(curServerTime > 0)// && isOver24Hours(curServerTime))//判断上次关机时间是不是超过24小时
            {
                String curHour = DateUtils.getHour(curServerTime);
                //String s3 = DateUtils.getHour(System.currentTimeMillis());
                if(!TextUtils.isEmpty(curHour))
                {
                    int curHourInt = Integer.parseInt(curHour);
                    if((curHourInt == 2))//凌晨
                    {
                        //更新关机时间
                        LocalDataEntity.newInstance(activity).setLastRebootTime(curServerTime);
                        LocalDataEntity.newInstance(activity).increaseRebootCount();
                        rebootBox(360);
                    }
                }
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }

    private int down = 0;
    //private static final String WAKE_PATH = "/sys/devices/meson-vfd.48/led";
    private static final String WAKE_PATH = "/sys/class/rtc-class-8563/demod_reset_pin";
    public void rebootBox(int time)
    {
        try
        {
            String model = Build.MODEL;
            if(model != null && model.equals("MBOX"))//新款盒子
            {
                bootProcess(time);
            }
            else//如果是插件或者之前的老盒子的话就没有开关机功能
            {
                ShellUtils.reboot();
            }
            /*if(PluginConstant.isPlugin || TextUtils.isEmpty(model) || model.toLowerCase().contains("tx"))
            {

            }*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void boot()
    {
        Log.d("MainActivity", "down1="+down);
        down = down*1000*60;
        timer.schedule(task,down);
    }
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {

            Log.d("MainActivity", "down2="+down);
            shutDown();
        }
    };
    private void bootProcess(int boot)
    {
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage("com.znt.rtc");
        if(intent != null)
        {
            try
            {
                intent.putExtra("ZNT_RTC_ACTION", "ZNT_RTC_ACTION_BOOT");
                intent.putExtra("ZNT_RTC_TIME", boot);
                activity.startActivity(intent);
            }
            catch (Exception e)
            {
                ShellUtils.reboot();
            }
        }


        /*if (boot >= 3) {
		        *//*try {
		            BufferedWriter bufWriter = null;
		            bufWriter = new BufferedWriter(new FileWriter(WAKE_PATH));
		            bufWriter.write(boot);  // 写操作
		            bufWriter.close();
		            Log.d("MainActivity","写成功");
		        } catch (IOException e) {
		            e.printStackTrace();
		            Log.e("MainActivity","can't write the" + WAKE_PATH);
		        }
		        *//*
            DtvControl mControl = new DtvControl(WAKE_PATH);
            mControl.setValueForce(String.valueOf(boot));
            Log.d("MainActivity", "写成功=>boot=>" + boot);
            shutDown();
        }*/

    }

    private void shutDown(){
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    private boolean isOver24Hours(long curServerTime)
    {
        long lastRebootTime = LocalDataEntity.newInstance(activity).getLastRebootTime();
        if(lastRebootTime <= 0)
            return false;

        if((curServerTime - lastRebootTime) >= 24 * 60 * 60 * 1000)
            return true;
        return false;
    }
}
