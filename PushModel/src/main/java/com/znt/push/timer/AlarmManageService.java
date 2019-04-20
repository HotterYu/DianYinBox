/*
package com.znt.push.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import com.znt.push.PushService;

import java.util.Calendar;

public class AlarmManageService
{
    //https://blog.csdn.net/zly921112/article/details/53692957

    public static AlarmManager alarmManager;
    private static String TAG = "AlarmManageService";
    public static void addAlarm(Context context, int requestCode, Bundle bundle, int minute)
    {
        Intent intent = new Intent(context, PushService.class);
        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getService(context,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND,10*minute);
        calendar.add(Calendar.MINUTE,0);
        calendar.add(Calendar.HOUR_OF_DAY,0);
        calendar.add(Calendar.MONTH,0);
        //注册新提醒
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);//一次性闹钟
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),10 * minute,pendingIntent);//重复闹钟
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),30 * minute,pendingIntent);//重复闹钟
        //参数：long startTime 闹钟开始时间，以毫秒为单位
        //参数：PendingIntent pendingIntent：设定闹钟的动作，可以是Activity，BroadcastReceiver 等。
        //参数：long intervalTime：在第二个方法中指，两次闹钟的间隔时间，相等间隔；在第三个方法中也指两次闹钟间隔时间，不相等时间；


    }

    public static void Alarm(Context context)
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(AlarmService.ACTION_ALARM);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.add(Calendar.SECOND,3*minute);

        if(Build.VERSION.SDK_INT < 19)
        {
            //am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),5 ,pendingIntent);//重复闹钟
        }
        else
        {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),5 ,pendingIntent);//重复闹钟
            //am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
        }
    }

}
*/
