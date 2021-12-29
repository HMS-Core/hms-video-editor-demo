/*
 *   Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.huawei.hms.videoeditor.ui.common.bean;

import java.util.Locale;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class MaterialData implements Parcelable {
    private String mName;

    private String mPath = "";

    private Uri mUri;

    private long mSize;

    private String mMimeType;

    private long addTime;

    private int index;

    private long mDuration;

    private int mWidth;

    private int mHeight;

    private long mValidDuration;

    private int mTypeAsset;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public Uri getUri() {
        return mUri;
    }

    public MaterialData setUri(Uri uri) {
        this.mUri = uri;
        return this;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        this.mSize = size;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public void setMimeType(String mimeType) {
        this.mMimeType = mimeType;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isVideo() {
        return !TextUtils.isEmpty(getMimeType()) && getMimeType().toLowerCase(Locale.ROOT).startsWith("video");
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    public long getValidDuration() {
        return mValidDuration;
    }

    public void setValidDuration(long validDuration) {
        this.mValidDuration = validDuration;
    }

    public int getTypeAsset() {
        return mTypeAsset;
    }

    public void setTypeAsset(int typeAsset) {
        this.mTypeAsset = typeAsset;
    }

    @Override
    public String toString() {
        return "MaterialData{" + "name='" + mName + '\'' + ", path='" + mPath + '\'' + ", uri=" + mUri + ", size="
            + mSize + ", mimeType='" + mMimeType + '\'' + ", addTime=" + addTime + ", index=" + index + ", duration="
            + mDuration + ", width=" + mWidth + ", height=" + mHeight + ", validDuration=" + mValidDuration
            + ", typeAsset=" + mTypeAsset + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mPath);
        dest.writeParcelable(this.mUri, flags);
        dest.writeLong(this.mSize);
        dest.writeString(this.mMimeType);
        dest.writeLong(this.addTime);
        dest.writeInt(this.index);
        dest.writeLong(this.mDuration);
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mHeight);
        dest.writeLong(this.mValidDuration);
        dest.writeInt(this.mTypeAsset);
    }

    public MaterialData() {
    }

    protected MaterialData(Parcel in) {
        this.mName = in.readString();
        this.mPath = in.readString();
        this.mUri = in.readParcelable(Uri.class.getClassLoader());
        this.mSize = in.readLong();
        this.mMimeType = in.readString();
        this.addTime = in.readLong();
        this.index = in.readInt();
        this.mDuration = in.readLong();
        this.mWidth = in.readInt();
        this.mHeight = in.readInt();
        this.mValidDuration = in.readLong();
        this.mTypeAsset = in.readInt();
    }

    public static final Creator<MaterialData> CREATOR = new Creator<MaterialData>() {
        @Override
        public MaterialData createFromParcel(Parcel source) {
            return new MaterialData(source);
        }

        @Override
        public MaterialData[] newArray(int size) {
            return new MaterialData[size];
        }
    };
}
