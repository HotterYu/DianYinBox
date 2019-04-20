package com.znt.push.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.FileProvider;

import com.znt.lib.utils.ApkTools;
import com.znt.lib.utils.SystemUtils;
import com.znt.lib.utils.ViewUtils;
import com.znt.push.entity.DownloadFileInfo;
import com.znt.push.entity.PushModelConstant;
import com.znt.push.httpmodel.HttpClient;

import java.io.File;
import java.util.List;

public class UpdateManager implements ApkDownloadListener
{
	private Context activity = null;
	
	private File apkFile = null;
	private File apkDir = null;
	private String pkgInstallerName = "com.znt.install";
	private String pkgSpeakerName = "com.znt.speaker";
	private volatile boolean isUpdateRunning = false;
	
	private final int DOWNLOAD_FILE = 3;
	private final int DOWNLOAD_FILE_SUCCESS = 4;
	private final int DOWNLOAD_FILE_FAIL = 5;
	
	private String devId = "";


	@SuppressLint("HandlerLeak")
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if(msg.what == DOWNLOAD_FILE)
			{

			}
			else if(msg.what == DOWNLOAD_FILE_SUCCESS)
			{
				startInstallApk();
			}
			else if(msg.what == DOWNLOAD_FILE_FAIL)
			{
				String error = (String)msg.obj;
			}
		};
	};

	public UpdateManager(Context activity , SpaceCheckListener mSpaceCheckListener)
	{
		this.activity = activity;
		this.mSpaceCheckListener = mSpaceCheckListener;
		initData();
	}
	
	public boolean isUpdateRunning()
	{
		return isUpdateRunning;
	}
	public void setUpdateRunning()
	{
		isUpdateRunning = true;
	}
	public void setUpdateFinished()
	{
		isUpdateRunning = false;
	}
	
	private void onErroLogReport(String error)
	{
		setUpdateFinished();
		HttpClient.errorReport(activity,error);
	}
	
	private boolean isVersionNew(int updateVersionNum)
	{
		try
		{
			int curAppVersionNum = SystemUtils.getVersionCode(activity);
			if(updateVersionNum > curAppVersionNum)
				return true;
			else
				onErroLogReport("isVersionNew is false, cur version-->"+curAppVersionNum);
			
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			onErroLogReport("isVersionNew check excetion-->"+e.getMessage());
		}
		
		return false;
	}
	
	private void initData()
	{
		apkDir = SystemUtils.getAvailableDir(activity, PushModelConstant.WORK_DIR + "/update/");
		if (!apkDir.exists()) 
		{
			apkDir.mkdirs();
		}
		apkFile = SystemUtils.getAvailableDir(activity, PushModelConstant.WORK_DIR + "/update/ZNTSpeaker.apk");
	}
	
	public void doApkInstall(String url)
	{
		try 
		{
			if(apkFile.exists() && isSignatureMatch())
			{
				int apkFileVersion = SystemUtils.getApkFileInfor(activity, apkFile.getAbsolutePath()).versionCode;
				if(isVersionNew(apkFileVersion))
					startInstallApk();
				else
				{
					apkFile.delete();
					downloadApkFile(url);
				}
			}
			else
				downloadApkFile(url);
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			
			onErroLogReport("download file error-->"+e.getMessage());
			
		}
	}
	
	private void downloadApkFile(final String downUrl)
	{
		ApkDownLoadManager.getInstance().startDownload(downUrl, apkDir.getAbsolutePath(), this);
	}
	
	private void startInstallApk()
	{
		setUpdateFinished();
		if(isFileValid())
		{
			if(PushModelConstant.UPDATE_TYPE == 0)
				installByAuto();
			else
				installByClick();
		}
	}
	
	private void installByClick()
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			Uri contentUri = FileProvider.getUriForFile(
					activity
					, "com.znt.speaker.fileprovider"
					, apkFile);
			intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
		} else {

			intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
		}
		activity.startActivity(intent);

	}
	
	private void installByAuto()
	{
		Intent intent = activity.getPackageManager().getLaunchIntentForPackage(pkgInstallerName);
		if(intent != null)
		{
			try 
			{
				intent.putExtra("pkg_name", pkgSpeakerName);
				intent.putExtra("apk_path", apkFile.getAbsolutePath());
				activity.startActivity(intent);
				
				System.exit(0);
				android.os.Process.killProcess(android.os.Process.myPid());
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				onErroLogReport("installByAuto exception-->" + e.getMessage());
			}
		}
		else
			onErroLogReport("installByAuto but intent is null");
	}
	
	private boolean isFileValid()
	{
		if(!apkFile.exists())
		{
			onErroLogReport("apk file not found");
			return false;
		}
		if(apkFile.length() == 0)
		{
			apkFile.delete();
			onErroLogReport("apk file length==0 and delete it");
			return false;
		}
		
		
		return isSignatureMatch();
	}
	
	private boolean isSignatureMatch()
	{
		String curSign = ApkTools.getSignature(activity);
		List<String> signs = ApkTools.getSignaturesFromApk(apkFile);
		
		if(curSign == null || signs.size() == 0 
				|| signs.get(0) == null || !curSign.equals(signs.get(0)))
		{
			apkFile.delete();
			onErroLogReport("apk sign is not match and delete it");
			return false;
		}
		
		return true;
	}
	

	@Override
	public void onDownloadStart(DownloadFileInfo info)
	{
		// TODO Auto-generated method stub
		ViewUtils.sendMessage(handler, DOWNLOAD_FILE);
		isUpdateRunning = true;
	}

	@Override
	public void onFileExist(DownloadFileInfo info)
	{
		// TODO Auto-generated method stub
		setUpdateFinished();
	}

	@Override
	public void onDownloadProgress(long progress, long size)
	{
		// TODO Auto-generated method stub
		isUpdateRunning = true;
	}

	@Override
	public void onDownloadError(DownloadFileInfo info,String error)
	{
		// TODO Auto-generated method stub
		ViewUtils.sendMessage(handler, DOWNLOAD_FILE_FAIL, error);
		onErroLogReport(error);
		setUpdateFinished();
	}

	@Override
	public void onDownloadFinish(File info)
	{
		// TODO Auto-generated method stub
		ViewUtils.sendMessage(handler, DOWNLOAD_FILE_SUCCESS);
		setUpdateFinished();
	}

	@Override
	public void onDownloadExit(DownloadFileInfo info)
	{
		// TODO Auto-generated method stub
		setUpdateFinished();
	}

	@Override
	public void onSpaceCheck(long size)
	{
		// TODO Auto-generated method stub
		if(mSpaceCheckListener != null)
			mSpaceCheckListener.onSpaceCheck(size);
	}

	private SpaceCheckListener mSpaceCheckListener = null;
	public interface SpaceCheckListener
    {
    	public void onSpaceCheck(long size);
    }
	
}