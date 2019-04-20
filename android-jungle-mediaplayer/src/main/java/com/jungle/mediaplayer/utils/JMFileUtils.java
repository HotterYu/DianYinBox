
package com.jungle.mediaplayer.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class JMFileUtils
{

	public static boolean isFileValid(String filrUrl)
	{
		if(TextUtils.isEmpty(filrUrl))
			return false;
		File tempFile = new File(filrUrl);
		if(!tempFile.exists())
			return false;
		if(tempFile.length() == 0)
			return false;
		
		return true;
	}

	public static void getLocalFiles(final List<File> fileList, final int type, String path)
	{
		if(path == null || path.length() == 0)
			return ;
		File dirFile = new File(path);
		File[] files = dirFile.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				// TODO Auto-generated method stub
				if(pathname.isDirectory() && !pathname.isHidden() && pathname.canRead())
				{
					getLocalFiles(fileList, type, pathname.getAbsolutePath());
					return false;
				}
				boolean result = false;
				if(type == 0)
					result = isPicture(pathname.getAbsolutePath());
				else if(type == 1)
					result = isMusic(pathname.getAbsolutePath());
				else if(type == 2)
					result = isVideo(pathname.getAbsolutePath());	
				if(result)
				{
					fileList.add(pathname);
				}
				return false;
			}
		});
	}
	

	public static boolean isVideo(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".mp4") || 
				path.toLowerCase().endsWith(".flv") ||
				path.toLowerCase().endsWith(".3gp") ||
				path.toLowerCase().endsWith(".wmv") ||
				path.toLowerCase().endsWith(".avi") ||
				path.toLowerCase().endsWith(".mkv") ||
				path.toLowerCase().endsWith(".rmvb") ||
				path.toLowerCase().endsWith(".mpg") ||
				path.toLowerCase().endsWith(".mpeg") ||
				path.toLowerCase().endsWith(".asf") ||
				path.toLowerCase().endsWith(".iso") ||
				path.toLowerCase().endsWith(".dat") ||
				path.toLowerCase().endsWith(".263") ||
				path.toLowerCase().endsWith(".h264") ||
				path.toLowerCase().endsWith(".mov") ||
				path.toLowerCase().endsWith(".rm") ||
				path.toLowerCase().endsWith(".ts") ||
				path.toLowerCase().endsWith(".vod") ||
				path.toLowerCase().endsWith(".mts"))
		{
			return true;
		}
		return false;
	}
	

	public static boolean isMusic(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".mp3") || 
			path.toLowerCase().endsWith(".flac")||
			path.toLowerCase().endsWith(".wav") ||
			path.toLowerCase().endsWith(".ape") ||
			path.toLowerCase().endsWith(".aac") ||
			path.toLowerCase().endsWith(".wma") ||
			path.toLowerCase().endsWith(".ogg") ||
			path.toLowerCase().endsWith(".ac3") ||
			path.toLowerCase().endsWith(".ddp") ||
			path.toLowerCase().endsWith(".pcm")) 
		{
			return true;
		}
		return false;
	}

	public static boolean isPicture(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".jpg") || 
				path.toLowerCase().endsWith(".png") ||
				path.toLowerCase().endsWith(".bmp") ||
				path.toLowerCase().endsWith(".gif"))
		{
			return true;
		}
		return false;
	}
	

	public static int deleteFile(File file)
	{
		if(file == null || !file.exists())
			return 1;
		if(!file.canWrite())
			return 2;
		file.delete();
		return 0;
	}

	public static int deleteFile(List<File> files)
	{
		int result = 0;
		for(int i=0;i<files.size();i++)
		{
			result = deleteFile(files.get(0));
			if(result != 0)
			{
				break;
			}
		}
		return result;
	}

	public static int deleteFolder(File file)
	{
		if(file == null || !file.exists())
			return 1;
		if(!file.canWrite())
			return 2;
		if(file.isFile())
			file.delete();
		else if(file.isDirectory())
		{
			File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0)
            {
                file.delete();
                return 0;
            }
            for(File f : childFile)
            {
            	deleteFolder(f);
            }
            file.delete();
		}
		return 0;
	}

	public static byte[] getBytes(InputStream is) throws IOException
	{  
		if(is == null)
			return null;
       ByteArrayOutputStream outstream = new ByteArrayOutputStream();
       byte[] buffer = new byte[1024];
       int len = -1;  
       while ((len = is.read(buffer)) != -1) 
       {  
           outstream.write(buffer, 0, len);  
       }  
       if(outstream != null)
    	   outstream.close();  

       return outstream.toByteArray();  
   } 
	
	public static String getStringFromFile(File file)
	{
		if(file == null || !file.exists())
			return "";
		String str = "";
		
		try
		{
			FileInputStream fis = new FileInputStream(file);
			
			byte[] buffer = new byte[1024];
	        int len = -1;  
	        while ((len = fis.read(buffer)) != -1) 
	        {  
	    	     String s = new String(buffer);
	    	     str += s;
	        }  
	        if(fis != null)
	    	   fis.close();  
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return str;
	}

	public static void openApkFile(Activity activity, File file)
	{
        // TODO Auto-generated method stub
		if(file == null || !file.exists())
			return;
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        activity.startActivity(intent);
	}
	

   public static boolean copyFolder(String oldPath, String newPath)
   { 
	   boolean isok = true;
       try 
       { 
           (new File(newPath)).mkdirs();
           File oldFile = new File(oldPath);
           String[] file= oldFile.list();
           File temp=null;
           for (int i = 0; i < file.length; i++) 
           { 
               if(oldPath.endsWith(File.separator))
               { 
                   temp = new File(oldPath + file[i]);
               } 
               else
               { 
                   temp = new File(oldPath+ File.separator + file[i]);
               } 

               if(temp.isFile())
               { 
                   FileInputStream input = new FileInputStream(temp);
                   FileOutputStream fos = new FileOutputStream(newPath + "/" +
                           (temp.getName()).toString()); 
                   BufferedOutputStream bos = new BufferedOutputStream(fos);
                   byte[] b = new byte[1024 * 4 ];
                   int len; 
                   while ( (len = input.read(b)) != -1) 
                   { 
                	   bos.write(b, 0, len); 
                   } 
                   bos.flush(); 
                   bos.close(); 
                   input.close(); 
               } 
               if(temp.isDirectory())
               {

                   copyFolder(oldPath + "/"+ file[i],newPath + "/" + file[i]); 
               } 
           } 
       } 
       catch (Exception e)
       { 
    	    isok = false;
       } 
       return isok;
   }
   

}
 
