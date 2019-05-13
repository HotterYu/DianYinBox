package com.znt.speaker.reboot;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.znt.lib.utils.ShellUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by prize on 2019/4/15.
 */

public class RebootModel
{

    private Context activity = null;

    public RebootModel(Context activity)
    {
        this.activity = activity;
    }

    private int down = 0;
    //private static final String WAKE_PATH = "/sys/devices/meson-vfd.48/led";
    private static final String WAKE_PATH = "/sys/class/rtc-class-8563/demod_reset_pin";
    public void rebootBox(int time)
    {
        bootProcess(time);
         /*String model = Build.MODEL;
            if(model != null && model.equals("MBOX"))//新款盒子
            {

            }
            else//如果是插件或者之前的老盒子的话就没有开关机功能
            {
                ShellUtils.reboot();
            }*/
    }

    /*private void boot()
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
    };*/
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
        else
            ShellUtils.reboot();
    }

    /*private void shutDown(){
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }*/


}
