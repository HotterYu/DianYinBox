package com.znt.lib.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

@Table(name = "LocalMediaInfor")
public class LocalMediaInfor implements Serializable
{
    @Column(name = "MEDIA_URL", isId = true)
    private String mediaUrl = "";
    @Column(name = "MODIFY_TIME")
    private long modifyTime = 0;
    @Column(name = "MEDIA_NAME")
    private String mediaName = "";
    @Column(name = "MEDIA_SIZE")
    private long mediaSize = 0;

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public long getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(long mediaSize) {
        this.mediaSize = mediaSize;
    }
}
