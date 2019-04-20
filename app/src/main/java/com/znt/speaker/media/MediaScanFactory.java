package com.znt.speaker.media;

import android.content.Context;
import android.util.Log;

import com.znt.push.db.DBMediaHelper;


public class MediaScanFactory implements MediaScannerCenter.ILocalMusicScanListener,IMediaScanListener
{

	private String TAG = "MediaScanFactory";

	public MediaScanFactory(Context context)
	{
		mMediaScannerCenter = new MediaScannerCenter(context);
	}
	
	private MediaScannerCenter mMediaScannerCenter = null;
	public void scanLocalMedias()
	{
		mMediaScannerCenter.setOnLocalMusicScanListener(this);
		mMediaScannerCenter.startScanThread(this);
	}

	@Override
	public void onScanStart()
	{
		DBMediaHelper.getInstance().deleteAllLocalMedias();
	}

	@Override
	public void onScanDoing()
	{

	}

	@Override
	public void onScanFinish()
	{
		DBMediaHelper.getInstance().checkAndReleaseSpace(0);
        Log.e(TAG, "onScanFinish: local media has scan finished!");
	}

    @Override
    public void onScanFail(String error)
    {
        Log.e(TAG, "onScanFail: " + error);
    }

    @Override
	public void mediaScan(String mediaPath, String mediaName)
	{
		Log.i(TAG, "mediaScan: " + mediaName + "  \n " + mediaPath);
	}
}
