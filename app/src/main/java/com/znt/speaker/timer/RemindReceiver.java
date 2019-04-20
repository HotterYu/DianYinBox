package com.znt.speaker.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RemindReceiver extends BroadcastReceiver
{

    private final String TAG = "";

    public RemindReceiver()
    {

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {

        Bundle b = intent.getExtras();
        //MediaInfor media = (MediaInfor) b.getSerializable("MEDIA");
        String media = b.getString("MEDIA");

        Log.d(TAG, "onReceive: ");
    }

}
