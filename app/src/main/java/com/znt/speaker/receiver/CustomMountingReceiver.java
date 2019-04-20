package com.znt.speaker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CustomMountingReceiver extends BroadcastReceiver 
{
	public static final String MEDIA_ADD = "android.intent.action.MEDIA_MOUNTED";
	public static final String MEDIA_REMOVE = "android.intent.action.MEDIA_REMOVED";
	
	private IMediaChangeListener receiver = null;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		String action = arg1.getAction();
		if(action.equals(MEDIA_ADD))//
		{
			String path = arg1.getDataString();
			if(receiver != null)
			{
				receiver.onMediaChange(true, path);
			}

		}
		if(action.equals(MEDIA_REMOVE))
		{
			if(receiver != null)
			{
				receiver.onMediaChange(false, "");
			}

		}
	}
	
	public static int getAndroidOSVersion()  
    {  
         int osVersion;  
         try  
         {  
            osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);  
         }  
         catch (Exception e)  
         {  
            osVersion = 0;  
         }  
           
         return osVersion;  
   }  
	
	public void setReceiverListener(IMediaChangeListener receiver)
	{
		this.receiver = receiver;
	}
	
	public interface IMediaChangeListener
	{
		public void onMediaChange(boolean isAdd, String path);
	}
}
