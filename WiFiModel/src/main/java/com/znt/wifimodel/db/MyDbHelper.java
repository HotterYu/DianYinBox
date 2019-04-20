
package com.znt.wifimodel.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.znt.lib.utils.FileUtils;
import com.znt.lib.utils.SystemUtils;
import com.znt.wifimodel.R;
import com.znt.wifimodel.entity.WifiModelConstant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;


public class MyDbHelper extends SQLiteOpenHelper
{

	protected SQLiteDatabase db = null;
	private Context context = null;
	private File dbFile = null;
	private final String DB_NAME = "znt_wifi.db";
	private final String ROW_ID = "_id";
	protected final String COIN_ORDER_ASC = "music_coin asc";
	protected final String COIN_DESC = "music_coin desc";
	protected final String TIME_ORDER_ASC = "modify_time asc";
	protected final String TIME_ORDER_DESC = "modify_time desc";
	protected final String TBL_SONG_LIST = "song_list_admin";
	protected final String TBL_SONG_LIST_ADMIN = "song_list_admin";
	protected final String TBL_SONG_RECORD = "song_record_list";
	protected final String TBL_USER_INFOR = "user_infor";
	protected final String TBL_USER_RECORD = "user_record";
	protected final String TBL_ADMIN = "admin_list";
	protected final String TBL_WIFI = "wifi_list";
	protected final String TBL_CUR_PLAN_LIST = "cur_plan_list";
	protected final String TBL_CUR_PLAN_MUSIC = "cur_plan_music";
	private String dbDir = null;
	
	public MyDbHelper(Context context)
	{
		super(context, null, null, 2);
		this.context = context;
		
		dbDir = SystemUtils.getAvailableDir(context, WifiModelConstant.WORK_DIR + "/db_wifi/").getAbsolutePath();
		
		dbFile = new File(dbDir + "/" + DB_NAME);
		
		db = getWritableDatabase();
		//createDb(dbFile);
		openDatabase();
	}
	
	public void deleteDbFile()
	{
		if(dbFile != null && dbFile.exists())
		{
			close();
			/*final File to = new File(dbFile.getAbsolutePath() + System.currentTimeMillis());
			dbFile.renameTo(to);
			to.delete();*/
			dbFile.delete();
			openDatabase();
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	*callbacks
	*/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		/*String sql = "drop table " + TBL_NAME;
        db.execSQL(sql);
        onCreate(db);*/
	}


	public int createDb(File file)
	{
		if(file == null)
			return 1;
		if(!file.exists())
		{
			int result = FileUtils.createFile(dbFile.getAbsolutePath());
			if(result != 0)
			{
				db = getWritableDatabase();
				return result;
			}
		}
		
		db = SQLiteDatabase.openOrCreateDatabase(file, null);
		
		if(db == null)
			return 1;
		return 0;
	}
	
	public int openDatabase() 
	{
        try 
        {
            File dir = new File(dbDir);
            if (!dir.exists())
                dir.mkdirs();
            if (!dbFile.exists()) 
            {
                InputStream is = context.getResources().openRawResource(R.raw.znt_wifi);
                FileOutputStream fos = new FileOutputStream(dbFile);
                byte[] buffer = new byte[8192];
                int count = 0;

                while ((count = is.read(buffer)) > 0) 
                {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                is.close();
            }
           

            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
            if(db != null)
            {
            	return 0;
            }
        } 
        catch (Exception e) 
        {
        	
        }
        return 1;
    }
	
	protected void checkDbStatus()
	{
		if(db == null)
			db = getWritableDatabase();
	}
	

	public int cretaeTbl(String tblName)
	{
		if(TextUtils.isEmpty(tblName))
			return 1;
		String CREATE_TBL = " create table "  
	            + tblName + " (" + ROW_ID + " integer primary key autoincrement, name text, url text, size long) "; 
		try
		{
			db.execSQL(CREATE_TBL);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			return 1;
		}
		return 0;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onOpen(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		super.onOpen(db);
	}
	

	public long insert(ContentValues values, String tblName)
	{
		checkDbStatus();
		return db.insert(tblName, null, values);
	}
	

	public Cursor query(String tblName)
	{
		//Cursor cur = db.rawQuery("SELECT * FROM " + tblName, null);
		String order = COIN_DESC;
		
		if(tblName.equals(TBL_SONG_RECORD))
			order = TIME_ORDER_ASC;
		else if(tblName.equals(TBL_ADMIN))
			order = "";
		else if(tblName.equals(TBL_WIFI))
			order = TIME_ORDER_ASC;
		checkDbStatus();
		Cursor cursor = null;
		try
		{
			String[] s = {};
			cursor = db.query(tblName, s, "", s, "", "", order);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			close();
			db = getWritableDatabase();
			cursor = db.query(tblName, null, null, null, null, null, order);
		}
		return cursor;
	}
	public Cursor queryAsc(String tblName)
	{
		//Cursor cur = db.rawQuery("SELECT * FROM " + tblName, null);
		String order = COIN_DESC;
		
		if(!tblName.equals(TBL_SONG_LIST))
			order = TIME_ORDER_ASC;
		checkDbStatus();
		Cursor cursor = null;
		try
		{
			String[] s = {};
			cursor = db.query(tblName, s, "", s, "", "", order);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			close();
			db = getWritableDatabase();
			cursor = db.query(tblName, null, null, null, null, null, order);
		}
		return cursor;
	}
	public Cursor queryNormal(String tblName)
	{
		//Cursor cur = db.rawQuery("SELECT * FROM " + tblName, null);
		String order = TIME_ORDER_DESC;
		
		checkDbStatus();
		Cursor cursor = null;
		try
		{
			String[] s = {};
			cursor = db.query(tblName, s, "", s, "", "", order);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			close();
			db = getWritableDatabase();
			cursor = db.query(tblName, null, null, null, null, null, order);
		}
		return cursor;
	}
	
	public void edit(String tblName, int id, String key, String newValue)
	{
		if(TextUtils.isEmpty(newValue) || TextUtils.isEmpty(key))
			return ;
		ContentValues values = new ContentValues();
		values.put(key, newValue);
		try
		{
			checkDbStatus();
			db.update(tblName, values, ROW_ID + "=" + Integer.toString(id), null);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}
	

	protected int editWithTime(String tblName, String id, ContentValues values)
	{
		values.put("modify_time", System.currentTimeMillis());
		try
		{
			checkDbStatus();
			return db.update(tblName, values, "user_id=?", new String[]{id});
		} 
		catch (Exception e)
		{
			
			// TODO: handle exception
		}
		return -1;
	}
	protected int edit(String tblName, ContentValues values)
	{
		try
		{
			checkDbStatus();
			return db.update(tblName, values, null, null);
		} 
		catch (Exception e)
		{
			
			// TODO: handle exception
		}
		return -1;
	}
	protected int edit(String tblName, String id, String state, ContentValues values)
	{
		try
		{
			checkDbStatus();
			return db.update(tblName, values, "user_id=? AND play_state=?", new String[]{id, state});
		} 
		catch (Exception e)
		{
			
			// TODO: handle exception
		}
		return -1;
	}
	protected int editWifi(String tblName, String wifiName, ContentValues values)
	{
		try
		{
			checkDbStatus();
			return db.update(tblName, values, "wifi_name=?", new String[]{wifiName});
		} 
		catch (Exception e)
		{
			
			// TODO: handle exception
		}
		return -1;
	}
	

	public int delete(int id, String tblName) 
	{  
		checkDbStatus();
        return db.delete(tblName, ROW_ID + "=?", new String[] { String.valueOf(id) });  
    }  
	protected int delete(String key, String value, String tblName) 
	{  
		try {
			checkDbStatus();
			return db.delete(tblName, key + "=?", new String[] { value }); 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return -1;
	}  
	protected int deleteAll(String tblName) 
	{  
		try {
			checkDbStatus();
			return db.delete(tblName, null, null);  
		} catch (Exception e) {
			// TODO: handle exception
		}
		return -1;
	}  
	

	public int deleteTbl(String tblName)
	{
		if(TextUtils.isEmpty(tblName))
			return 1;
		String sql = "drop table " + tblName;
		checkDbStatus();
        db.execSQL(sql);
        
        return 0;
	}
	
    public void close() 
    {  
        if (db != null)  
        {
        	db.close();  
        	db = null;
        }
    }  
}
 
