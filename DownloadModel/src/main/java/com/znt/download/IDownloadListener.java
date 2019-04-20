package com.znt.download;

import com.znt.lib.bean.MediaInfor;

public interface IDownloadListener {
	public void onDownloadSpaceCheck(long size);
	public void onDownloadRecordInsert(MediaInfor songInfor, long modifyTime);
	void onRemoveLargeSize(String url);
}
