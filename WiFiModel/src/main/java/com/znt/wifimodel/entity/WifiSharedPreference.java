package com.znt.wifimodel.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class WifiSharedPreference
{

	private SharedPreferences sp = null;
	private Context context = null;
	private Editor edit = null;
	
	public WifiSharedPreference(Context context, String sharedPreferencesName)
	{
		this.context = context;
		if((sharedPreferencesName == null) || (sharedPreferencesName.length() <= 0))
			sharedPreferencesName = "znt_speaker_wifi";
		sp = context.getSharedPreferences(sharedPreferencesName, context.MODE_PRIVATE);
		edit = sp.edit();
	}
	
	public static WifiSharedPreference newInstance(Context context)
	{
		return new WifiSharedPreference(context, null);
	}

	public void setData(String key, Object value)
	{
		if(value instanceof String)
			edit.putString(key, (String)value);
		else if(value instanceof Boolean)
			edit.putBoolean(key, (Boolean)value);
		else if(value instanceof Long)
			edit.putLong(key, (Long)value);
		else if(value instanceof Integer)
			edit.putInt(key, (Integer)value);
		/*else if(value instanceof Float)
			edit.putFloat(key, (Float)value);*/
		edit.commit();
	}


	public String getData(String key, String defValue)
	{
		return sp.getString(key, defValue);
	}
	public boolean getData(String key, boolean defValue)
	{
		return sp.getBoolean(key, defValue);
	}
	public int getData(String key, int defValue)
	{
		return sp.getInt(key, 0);
	}
	public long getDataLong(String key, long defValue)
	{
		return sp.getLong(key, defValue);
	}
	
	/*public void setData(String key, Object value)
	{
		if(value instanceof String)
			Settings.System.putString(context.getContentResolver(),key, (String)value);
		else if(value instanceof Long)
			Settings.System.putLong(context.getContentResolver(),key, (Long)value);
		else if(value instanceof Integer)
			Settings.System.putInt(context.getContentResolver(),key, (Integer)value);
		
		
	}
	
	public String getData(String key, String defValue)
	{
		String reValue = defValue;
		try 
		{
			reValue = Settings.System.getString(context.getContentResolver(),key);
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		}
		if(reValue == null)
			reValue = defValue;
		return reValue;
		//return sp.getString(key, defValue);
	}*/

}
