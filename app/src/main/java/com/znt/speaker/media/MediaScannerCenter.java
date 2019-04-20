package com.znt.speaker.media;

import android.content.Context;
import android.os.Environment;


import com.znt.lib.bean.LocalMediaInfor;
import com.znt.lib.utils.FileUtils;
import com.znt.push.db.DBMediaHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MediaScannerCenter
{

	private static  MediaScannerCenter mInstance;
	private Context mContext;
	
	private ScanMediaThread mediaThread;
	private ILocalMusicScanListener iLocalMusicScanListener = null;

    public MediaScannerCenter(Context context)
	{
		mContext = context;
		initData();
	}

	public static synchronized MediaScannerCenter getInstance(Context context)
	{
		if (mInstance == null)
		{
			mInstance  = new MediaScannerCenter(context);
		}
		return mInstance;
	}
	
	public void setOnLocalMusicScanListener(ILocalMusicScanListener iLocalMusicScanListener)
	{
		this.iLocalMusicScanListener = iLocalMusicScanListener;
	}

	private void initData(){
		
	}
	
	public synchronized boolean startScanThread(IMediaScanListener listener)
	{
		if (mediaThread == null || !mediaThread.isAlive())
		{
			mediaThread = new ScanMediaThread(listener);
			mediaThread.start();
		}
		
		return true;
	}
	
	public synchronized void stopScanThread()
	{
		if (mediaThread != null)
		{
			if (mediaThread.isAlive())
			{
				mediaThread.exit();
			}
			mediaThread = null;
		}
	}
	
	public synchronized boolean isThreadOver()
	{
		if (mediaThread != null && mediaThread.isAlive())
		{
			return false;
		}
		
		return true;
	}
	

	private boolean isMusic(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".mp3") || 
			path.toLowerCase().endsWith(".wav") ||
			//path.toLowerCase().endsWith(".pcm") ||
			path.toLowerCase().endsWith(".wma") ||
			path.toLowerCase().endsWith(".flac") ||
			path.toLowerCase().endsWith(".aac") ||
			path.toLowerCase().endsWith(".ape")) 
		{
			return true;
		}
		return false;
	}
	
	private boolean isTempFile(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".temp")) 
		{
			return true;
		}
		return false;
	}
	
	public boolean isVideo(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".mp4") || 
				path.toLowerCase().endsWith(".264") ||
				path.toLowerCase().endsWith(".3gp") ||
				path.toLowerCase().endsWith(".avi") ||
				path.toLowerCase().endsWith(".wmv") ||
				path.toLowerCase().endsWith(".263") ||
				path.toLowerCase().endsWith(".h264") ||
				path.toLowerCase().endsWith(".rmvb") ||
				path.toLowerCase().endsWith(".mts") ||
				path.toLowerCase().endsWith(".mkv") ||
				path.toLowerCase().endsWith(".flv"))
		{
			return true;
		}
		return false;
	}

	private void getMusicFromPath(String path, final IMediaScanListener listener, 
			final ICancelScanMedia cancelObser)
	{
		if(path == null || path.length() == 0)
			return ;
		File dirFile = new File(path);
		File[] files = dirFile.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				// TODO Auto-generated method stub
				if (cancelObser.ifCancel())
				{
     				return true;
     			}
				
				if(iLocalMusicScanListener != null)
					iLocalMusicScanListener.onScanDoing();
				
				if(file.isDirectory() && !file.isHidden() && file.canRead())
				{
					getMusicFromPath(file.getAbsolutePath(), listener, cancelObser);
					return false;
				}
				

				String path = file.getAbsolutePath();
				if(isMusic(path) || isVideo(path) || isTempFile(path) || FileUtils.isPicture(path))
				{
					listener.mediaScan(path, getNameFromPath(path));

					LocalMediaInfor tempInfor = new LocalMediaInfor();
					tempInfor.setMediaName(getNameFromPath(path));
					tempInfor.setMediaUrl(file.getAbsolutePath());
					tempInfor.setMediaSize(file.length());
					tempInfor.setModifyTime(file.lastModified());

					DBMediaHelper.getInstance().addLocalMedia(tempInfor);
				}
				return false;
			}
		});
	}
	private String getNameFromPath(String path)
	{

		String name = "";
		try
		{
			if(path.contains("/"))
				name = path.substring(path.lastIndexOf("/") + 1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return name;
	}
	
	private void getMusicByScan(IMediaScanListener listener, ICancelScanMedia cancelObser)
	{
		StorageList storageList = new StorageList(mContext);
	    String[] strings = storageList.getVolumePaths();
		
		//List<String> dirList = getStorageDirectoriesArrayList();
		int size = strings.length;
		for(int i=0;i<size;i++)
		{
			getMusicFromPath(strings[i], listener, cancelObser);
		}
	}
	
	public class ScanMediaThread extends Thread implements ICancelScanMedia
	{
		
		IMediaScanListener mListener;
		boolean exitFlag = false;
		
		public ScanMediaThread(IMediaScanListener listener)
		{
			mListener = listener;
		}

		public void exit()
		{
			exitFlag = true;
		}
		
		@Override
		public void run() 
		{

			try 
			{
				if(iLocalMusicScanListener != null)
					iLocalMusicScanListener.onScanStart();
				
				getMusicByScan(mListener, this);
				
				if(iLocalMusicScanListener != null)
					iLocalMusicScanListener.onScanFinish();
				
			} 
			catch (Exception e) 
			{
				if(iLocalMusicScanListener != null)
					iLocalMusicScanListener.onScanFail(e.getMessage());
				e.printStackTrace();
			}
			
			super.run();
		}

		@Override
		public boolean ifCancel() 
		{
			return exitFlag;
		}	
	}
	
	public  interface ICancelScanMedia
	{
		public boolean ifCancel();
	}

	private ArrayList<String> getStorageDirectoriesArrayList()
    {
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader bufReader = null;
        try 
        {
            bufReader = new BufferedReader(new FileReader("/proc/mounts"));
            list.add(Environment.getExternalStorageDirectory().getPath());
            String line;
            while((line = bufReader.readLine()) != null) 
            {
                if(line.contains("vfat") || line.contains("exfat") ||
                   line.contains("/mnt") || line.contains("/Removable")) 
                {
                    StringTokenizer tokens = new StringTokenizer(line, " ");
                    String s = tokens.nextToken();
                    s = tokens.nextToken(); // Take the second token, i.e. mount point

                    if (list.contains(s))
                        continue;

                    if (line.contains("/dev/block/vold")) 
                    {
                        if (!line.startsWith("tmpfs") &&
                            !line.startsWith("/dev/mapper") &&
                            !s.startsWith("/mnt/secure") &&
                            !s.startsWith("/mnt/shell") &&
                            !s.startsWith("/mnt/asec") &&
                            !s.startsWith("/mnt/obb")
                            ) 
                        {
                            list.add(s);
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        finally 
        {
            if (bufReader != null) 
            {
                try 
                {
                    bufReader.close();
                }
                catch (IOException e) {}
            }
        }
        return list;
    }
	
	public interface ILocalMusicScanListener
	{
		public void onScanStart();
		public void onScanDoing();
		public void onScanFinish();
		public void onScanFail(String error);
	}
	
}
