
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
import java.util.Objects;

import android.graphics.RectF;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class MediaData implements Parcelable {
    public static final int MEDIA_IMAGE = 0;

    public static final int MEDIA_VIDEO = 1;

    private boolean isFromCloud = false;

    private int setSelected;

    private String dirName = "";

    private String name = "";

    private String path = "";

    private Uri uri;

    private String coverUrl;

    private long size;

    private String mimeType;

    private long addTime;

    private int index;

    private long duration = 0;

    private int width;

    private int height;

    private int position;

    private float glLeftBottomX;

    private float glLeftBottomY;

    private float glRightTopX;

    private float glRightTopY;

    private float scaleX;

    private float scaleY;

    private int type;

    private float speed;

    private int downSamplingState = -2;

    private float rotation;

    private boolean mirrorStatus;

    private boolean verticalMirrorStatus;

    private long cutTrimIn = 0;

    private float hVEWidth;

    private float hVEHeight;

    private long cutTrimOut = 0;

    private RectF clipRectBorder;

    private long validDuration = 0;

    protected float templateScaleWidth;

    protected float templateScaleHeight;

    protected float templateCenterX;

    protected float templateCenterY;

    public MediaData setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public boolean isFromCloud() {
        return isFromCloud;
    }

    public void setFromCloud(boolean fromCloud) {
        isFromCloud = fromCloud;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Uri getUri() {
        return uri;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getAddTime() {
        return addTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isVideo() {
        return !TextUtils.isEmpty(getMimeType()) && getMimeType().toLowerCase(Locale.ROOT).startsWith("video");
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getGlLeftBottomX() {
        return glLeftBottomX;
    }

    public void setGlLeftBottomX(float glLeftBottomX) {
        this.glLeftBottomX = glLeftBottomX;
    }

    public float getGlLeftBottomY() {
        return glLeftBottomY;
    }

    public void setGlLeftBottomY(float glLeftBottomY) {
        this.glLeftBottomY = glLeftBottomY;
    }

    public float getGlRightTopX() {
        return glRightTopX;
    }

    public void setGlRightTopX(float glRightTopX) {
        this.glRightTopX = glRightTopX;
    }

    public float getGlRightTopY() {
        return glRightTopY;
    }

    public void setGlRightTopY(float glRightTopY) {
        this.glRightTopY = glRightTopY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDownSamplingState() {
        return downSamplingState;
    }

    public void setDownSamplingState(int downSamplingState) {
        this.downSamplingState = downSamplingState;
    }

    public float getRotation() {
        return rotation;
    }

    public float getHVEWidth() {
        return hVEWidth;
    }

    public float getHVEHeight() {
        return hVEHeight;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public boolean isMirrorStatus() {
        return mirrorStatus;
    }

    public void setMirrorStatus(boolean mirrorStatus) {
        this.mirrorStatus = mirrorStatus;
    }

    public boolean isVerticalMirrorStatus() {
        return verticalMirrorStatus;
    }

    public long getCutTrimIn() {
        return cutTrimIn;
    }

    public void setCutTrimIn(long cutTrimIn) {
        this.cutTrimIn = cutTrimIn;
    }

    public long getCutTrimOut() {
        return cutTrimOut;
    }

    public void setCutTrimOut(long cutTrimOut) {
        this.cutTrimOut = cutTrimOut;
    }

    public void setHVEWidth(float HVEWidth) {
        this.hVEWidth = HVEWidth;
    }

    public void setHVEHeight(float HVEHeight) {
        this.hVEHeight = HVEHeight;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        MediaData mediaData = (MediaData) object;
        return setSelected == mediaData.setSelected && size == mediaData.size && addTime == mediaData.addTime
            && index == mediaData.index && duration == mediaData.duration && validDuration == mediaData.validDuration
            && width == mediaData.width && height == mediaData.height && position == mediaData.position
            && Float.compare(mediaData.glLeftBottomX, glLeftBottomX) == 0
            && Float.compare(mediaData.glLeftBottomY, glLeftBottomY) == 0
            && Float.compare(mediaData.glRightTopX, glRightTopX) == 0
            && Float.compare(mediaData.glRightTopY, glRightTopY) == 0 && Float.compare(mediaData.scaleX, scaleX) == 0
            && Float.compare(mediaData.scaleY, scaleY) == 0 && type == mediaData.type
            && Float.compare(mediaData.speed, speed) == 0 && downSamplingState == mediaData.downSamplingState
            && Float.compare(mediaData.rotation, rotation) == 0 && mirrorStatus == mediaData.mirrorStatus
            && verticalMirrorStatus == mediaData.verticalMirrorStatus && cutTrimIn == mediaData.cutTrimIn
            && cutTrimOut == mediaData.cutTrimOut && Objects.equals(dirName, mediaData.dirName)
            && Objects.equals(name, mediaData.name) && Objects.equals(path, mediaData.path)
            && Objects.equals(uri, mediaData.uri) && Objects.equals(mimeType, mediaData.mimeType)
            && Objects.equals(clipRectBorder, mediaData.clipRectBorder)
            && Float.compare(mediaData.templateCenterX, templateCenterX) == 0
            && Float.compare(mediaData.templateCenterY, templateCenterY) == 0
            && Float.compare(mediaData.templateScaleHeight, templateScaleHeight) == 0
            && Float.compare(mediaData.templateScaleWidth, templateScaleWidth) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(setSelected, dirName, name, path, uri, size, mimeType, addTime, index, duration, width,
            height, position, glLeftBottomX, glLeftBottomY, glRightTopX, glRightTopY, scaleX, scaleY, type, speed,
            downSamplingState, rotation, mirrorStatus, verticalMirrorStatus, cutTrimIn, cutTrimOut, clipRectBorder,
            validDuration, templateCenterX, templateCenterY, templateScaleWidth, templateScaleHeight);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.setSelected);
        dest.writeString(this.dirName);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeParcelable(this.uri, flags);
        dest.writeLong(this.size);
        dest.writeString(this.mimeType);
        dest.writeLong(this.addTime);
        dest.writeInt(this.index);
        dest.writeInt(this.position);
        dest.writeLong(this.duration);
        dest.writeLong(this.validDuration);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeFloat(this.glLeftBottomX);
        dest.writeFloat(this.glLeftBottomY);
        dest.writeFloat(this.glRightTopX);
        dest.writeFloat(this.glRightTopY);
        dest.writeFloat(this.scaleX);
        dest.writeFloat(this.scaleY);
        dest.writeInt(this.type);
        dest.writeFloat(this.speed);
        dest.writeInt(this.downSamplingState);
        dest.writeFloat(this.rotation);
        dest.writeByte((byte) (this.mirrorStatus ? 1 : 0));
        dest.writeByte((byte) (this.verticalMirrorStatus ? 1 : 0));
        dest.writeLong(this.cutTrimIn);
        dest.writeLong(this.cutTrimOut);
        dest.writeFloat(this.hVEWidth);
        dest.writeFloat(this.hVEHeight);
        dest.writeFloat(this.templateCenterX);
        dest.writeFloat(this.templateCenterY);
        dest.writeFloat(this.templateScaleWidth);
        dest.writeFloat(this.templateScaleHeight);
        dest.writeParcelable(this.clipRectBorder, flags);
    }

    public MediaData() {
    }

    protected MediaData(Parcel in) {
        this.setSelected = in.readInt();
        this.dirName = in.readString();
        this.name = in.readString();
        this.path = in.readString();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.size = in.readLong();
        this.mimeType = in.readString();
        this.addTime = in.readLong();
        this.index = in.readInt();
        this.position = in.readInt();
        this.duration = in.readLong();
        this.validDuration = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.glLeftBottomX = in.readFloat();
        this.glLeftBottomY = in.readFloat();
        this.glRightTopX = in.readFloat();
        this.glRightTopY = in.readFloat();
        this.scaleX = in.readFloat();
        this.scaleY = in.readFloat();
        this.type = in.readInt();
        this.speed = in.readFloat();
        this.downSamplingState = in.readInt();
        this.rotation = in.readFloat();
        this.mirrorStatus = in.readByte() != 0;
        this.verticalMirrorStatus = in.readByte() != 0;
        this.cutTrimIn = in.readLong();
        this.cutTrimOut = in.readLong();
        this.hVEWidth = in.readFloat();
        this.hVEHeight = in.readFloat();
        this.templateCenterX = in.readFloat();
        this.templateCenterY = in.readFloat();
        this.templateScaleWidth = in.readFloat();
        this.templateScaleHeight = in.readFloat();
        this.clipRectBorder = in.readParcelable(RectF.class.getClassLoader());
    }

    public MediaData(MediaData src) {
        this.setSelected = src.setSelected;
        this.dirName = src.dirName;
        this.name = src.name;
        this.path = src.path;
        this.uri = src.uri;
        this.size = src.size;
        this.mimeType = src.mimeType;
        this.addTime = src.addTime;
        this.index = src.index;
        this.position = src.position;
        this.duration = src.duration;
        this.validDuration = src.validDuration;
        this.width = src.width;
        this.height = src.height;
        this.glLeftBottomX = src.glLeftBottomX;
        this.glLeftBottomY = src.glLeftBottomY;
        this.glRightTopX = src.glRightTopX;
        this.glRightTopY = src.glRightTopY;
        this.scaleX = src.scaleX;
        this.scaleY = src.scaleY;
        this.type = src.type;
        this.speed = src.speed;
        this.downSamplingState = src.downSamplingState;
        this.rotation = src.rotation;
        this.mirrorStatus = src.mirrorStatus;
        this.verticalMirrorStatus = src.verticalMirrorStatus;
        this.cutTrimIn = src.cutTrimIn;
        this.cutTrimOut = src.cutTrimOut;
        this.hVEWidth = src.hVEWidth;
        this.hVEHeight = src.hVEHeight;
        this.templateCenterX = src.templateCenterX;
        this.templateCenterY = src.templateCenterY;
        this.templateScaleWidth = src.templateScaleWidth;
        this.templateScaleHeight = src.templateScaleHeight;
        this.clipRectBorder = new RectF(src.clipRectBorder);
    }

    public static final Creator<MediaData> CREATOR = new Creator<MediaData>() {
        @Override
        public MediaData createFromParcel(Parcel source) {
            return new MediaData(source);
        }

        @Override
        public MediaData[] newArray(int size) {
            return new MediaData[size];
        }
    };

    public boolean isSetSelected() {
        return setSelected == 1;
    }

    public void setSetSelected(boolean setSelected) {
        this.setSelected = setSelected ? 1 : 0;
    }
}
