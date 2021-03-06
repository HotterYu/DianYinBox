/**
 * Android Jungle-MediaPlayer framework project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jungle.mediaplayer.base;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class VideoInfo implements Parcelable {

    private String mStreamUrl;
    private String mediaName;
    private String mediaType;
    private String playType;//0：本地播放；1：在线播放
    private int mCurrentPosition;

    public String getPlayType() {
        return playType;
    }

    public void setPlayType(String playType) {
        this.playType = playType;
    }

    public VideoInfo() {
    }

    public VideoInfo(String url, String name,String mediaType) {
        mStreamUrl = url;
        mediaName = name;
        this.mediaType = mediaType;
        mCurrentPosition = 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mStreamUrl);
        dest.writeString(mediaName);
        dest.writeInt(mCurrentPosition);
        dest.writeString(mediaType);
        dest.writeString(playType);
    }

    public static boolean validate(VideoInfo videoInfo) {
        if (videoInfo == null) {
            return false;
        }

        return !TextUtils.isEmpty(videoInfo.mStreamUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        public VideoInfo createFromParcel(Parcel source) {
            VideoInfo info = new VideoInfo(source.readString(), source.readString(), source.readString());
            info.mStreamUrl = source.readString();
            info.mediaName = source.readString();
            info.mCurrentPosition = source.readInt();
            info.mediaType = source.readString();
            info.playType = source.readString();
            return info;
        }

        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };

    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }
    public String getMediaType()
    {
        return mediaType;
    }

    public String getStreamUrl() {
        return mStreamUrl;
    }

    public String getMediaName() {
        return mediaName;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
    }
}
