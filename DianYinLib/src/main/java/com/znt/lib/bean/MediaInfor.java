package com.znt.lib.bean;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.znt.lib.utils.UrlUtil;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "media_infor")
public class MediaInfor implements Parcelable
{
    public static final String MEDIA_TYPE_MEDIA = "0";
    public static final String MEDIA_TYPE_ADV = "1";
    public static final String MEDIA_TYPE_PUSH = "2";
    public boolean isPlayMeida()
    {
        return musicType.equals(MEDIA_TYPE_MEDIA);
    }

    public boolean isPlayAdv()
    {
        return musicType.equals(MEDIA_TYPE_ADV);
    }

    public boolean isPlayPush()
    {
        return musicType.equals(MEDIA_TYPE_PUSH);
    }

    @Column(name = "id", isId = true,autoGen = true)
    private long keyId = 0;

    @Column(name = "media_name")
    private String musicName = "";
    @Column(name = "media_id")
    private String id = "";
    @Column(name = "media_url")
    private String musicUrl = "";
    @Column(name = "media_size")
    private String fileSize = "";
    @Column(name = "media_type")
    private String musicType = "";//0,media   1, adv   2, push
    @Column(name = "media_duration")
    private String musicDuration = "";
    @Column(name = "album_name")
    private String musicAlbum = "";
    @Column(name = "artist_name")
    private String musicSing = "";
    private int reload_count = 0;

    @Column(name = "cur_seek")
    private int cur_seek = 0;

    @Column(name = "cur_position")
    private int cur_position = 0;

    private boolean isPausePushMedia = false;


    @Column(name = "modify_time")
    private long modify_time = 0;

    @Column(name = "start_play_time")
    private String start_play_time = "";
    @Column(name = "end_play_time")
    private String end_play_time = "";
    @Column(name = "start_play_date")
    private String start_play_date = "";
    @Column(name = "end_play_date")
    private String end_play_date = "";
    @Column(name = "play_week")
    private String play_week = "";
    @Column(name = "plan_id")
    private String plan_id = "";
    @Column(name = "sche_id")
    private String sche_id = "";

    public long getId() {
        return keyId;
    }

    public void setId(long id) {
        this.keyId = id;
    }

    public String getSche_id() {
        return sche_id;
    }

    public void setSche_id(String sche_id) {
        this.sche_id = sche_id;
    }

    private String hintMsg = "";

    public String getHintMsg() {
        return hintMsg;
    }

    public void setHintMsg(String hintMsg) {
        this.hintMsg = hintMsg;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public MediaInfor()
    {

    }

    public MediaInfor(Parcel source)
    {
        this.musicName = source.readString();
        this.id = source.readString();
        this.musicUrl = source.readString();
        this.fileSize = source.readString();
        this.musicType = source.readString();
        this.musicDuration = source.readString();
        this.musicAlbum = source.readString();
        this.musicSing = source.readString();
        this.reload_count = source.readInt();
        this.cur_seek = source.readInt();
        this.cur_position = source.readInt();
        this.modify_time = source.readLong();
        this.start_play_time = source.readString();
        this.end_play_time = source.readString();
        this.start_play_date = source.readString();
        this.end_play_date = source.readString();
        this.play_week = source.readString();
        this.plan_id = source.readString();
        this.hintMsg = source.readString();
    }

    public int getCurSeek() {
        return cur_seek;
    }

    public void setCurPosition(int cur_position) {
        this.cur_position = cur_position;
    }

    public int getCurPosition() {
        return cur_position;
    }

    public void setCurSeek(int curSeek) {
        this.cur_seek = curSeek;
    }

    public long getModifyTime() {
        return modify_time;
    }

    public void setModifyTime(long modifyTime) {
        this.modify_time = modifyTime;
    }

    public void setMediaId(String mediaId)
    {
        this.id = mediaId;
    }
    public String getMediaId()
    {
        return id;
    }

    public String getStartPlayTime() {
        return start_play_time;
    }

    public void setStartPlayTime(String startPlayTime) {
        this.start_play_time = startPlayTime;
    }

    public String getEndPlayTime() {
        return end_play_time;
    }

    public void setEndPlayTime(String endPlayTime) {
        this.end_play_time = endPlayTime;
    }

    public String getStartPlayDate() {
        return start_play_date;
    }

    public void setStartPlayDate(String startPlayDate) {
        this.start_play_date = startPlayDate;
    }

    public String getEndPlayDate() {
        return end_play_date;
    }

    public void setEndPlayDate(String endPlayDate) {
        this.end_play_date = endPlayDate;
    }

    public String getPlayWeek() {
        return play_week;
    }

    public void setPlayWeek(String playWeek) {
        this.play_week = playWeek;
    }
    public String getMediaName() {
        return musicName;
    }

    public void setMediaName(String mediaName) {
        this.musicName = mediaName;
    }

    public String getMediaUrl() {
        if(!TextUtils.isEmpty(musicUrl))
            return UrlUtil.decodeUrl(musicUrl);
        return musicUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.musicUrl = mediaUrl;
    }

    public long getMediaSize() {
        if(!TextUtils.isEmpty(fileSize))
            return Long.parseLong(fileSize);
        return 0;
    }

    public void setMediaSize(long mediaSize) {
        this.fileSize = mediaSize + "";
    }

    public String getMediaType() {
        return musicType;
    }

    public void setMediaType(String mediaType) {
        this.musicType = mediaType;
    }

    public boolean isMedia()
    {
        return musicType.equals(MEDIA_TYPE_MEDIA);
    }
    public boolean isAdv()
    {
        return musicType.equals(MEDIA_TYPE_ADV);
    }
    public boolean isPush()
    {
        return musicType.equals(MEDIA_TYPE_PUSH);
    }

    public int getDuration() {
        if(!TextUtils.isEmpty(musicDuration))
            return Integer.parseInt(musicDuration);
        return 0;
    }

    public void setDuration(int duration) {
        this.musicDuration = duration + "";
    }

    public String getAlbumName() {
        return musicAlbum;
    }

    public void setAlbumName(String albumName) {
        this.musicAlbum = albumName;
    }

    public String getArtistName() {
        return musicSing;
    }

    public void setArtistName(String artistName) {
        this.musicSing = artistName;
    }

    public void setReloadCount(int reloadCount)
    {
        this.reload_count = reloadCount;
    }
    public int getReloadCount()
    {
        return reload_count;
    }
    public void increaseReloadCount()
    {
        reload_count ++;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(musicName);
        parcel.writeString(id);
        parcel.writeString(musicUrl);
        parcel.writeString(fileSize);
        parcel.writeString(musicType);
        parcel.writeString(musicDuration);
        parcel.writeString(musicAlbum);
        parcel.writeString(musicSing);
        parcel.writeInt(reload_count);
        parcel.writeLong(modify_time);
        parcel.writeInt(cur_seek);
        parcel.writeInt(cur_position);
        parcel.writeString(start_play_time);
        parcel.writeString(end_play_time);
        parcel.writeString(start_play_date);
        parcel.writeString(end_play_date);
        parcel.writeString(play_week);
        parcel.writeString(plan_id);
        parcel.writeString(hintMsg);

    }

    public static final Creator<MediaInfor> CREATOR = new Creator<MediaInfor>()
    {
        @Override
        public MediaInfor createFromParcel(Parcel source) {
            return new MediaInfor(source);
        }

        @Override
        public MediaInfor[] newArray(int size) {
            return new MediaInfor[0];
        }
    };
}
