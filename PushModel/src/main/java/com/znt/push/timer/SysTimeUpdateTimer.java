package com.znt.push.timer;

import android.content.Context;

public class SysTimeUpdateTimer extends AbstractTimer
{
    public SysTimeUpdateTimer(Context context)
    {
        super(context);
        setTimeInterval(1000);
    }


}
