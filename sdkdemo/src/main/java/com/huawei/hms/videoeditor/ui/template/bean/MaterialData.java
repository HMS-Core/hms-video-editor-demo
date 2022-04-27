/*
 *   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.hms.videoeditor.ui.template.bean;

import java.util.Locale;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;

import androidx.annotation.NonNull;

public class MaterialData implements Parcelable, Cloneable {
    private String name;

    private String path = "";

    private Uri uri;

    private long size;

    private String mimeType;

    private long addTime;

    private int index;

    private long duration;

    private int width;

    private int height;

    private long validDuration;

    private long cutTrimIn;

    private long cutTrimOut;

    private float glLeftBottomX;

    private float glLeftBottomY;

    private float glRightTopX;

    private float glRightTopY;

    private float scaleX;

    private float scaleY;

    private boolean mirrorStatus;

    private boolean verticalMirrorStatus;

    private float rotation;

    protected float templateScaleWidth;

    protected float templateScaleHeight;

    protected float templateCenterX;

    public float getTemplateScaleWidth() {
        return templateScaleWidth;
    }

    public void setTemplateScaleWidth(float templateScaleWidth) {
        this.templateScaleWidth = templateScaleWidth;
    }

    public float getTemplateScaleHeight() {
        return templateScaleHeight;
    }

    public void setTemplateScaleHeight(float templateScaleHeight) {
        this.templateScaleHeight = templateScaleHeight;
    }

    public float getTemplateCenterX() {
        return templateCenterX;
    }

    public void setTemplateCenterX(float templateCenterX) {
        this.templateCenterX = templateCenterX;
    }

    public float getTemplateCenterY() {
        return templateCenterY;
    }

    public void setTemplateCenterY(float templateCenterY) {
        this.templateCenterY = templateCenterY;
    }

    protected float templateCenterY;

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
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

    public boolean isMirrorStatus() {
        return mirrorStatus;
    }

    public void setMirrorStatus(boolean mirrorStatus) {
        this.mirrorStatus = mirrorStatus;
    }

    public boolean isVerticalMirrorStatus() {
        return verticalMirrorStatus;
    }

    public void setVerticalMirrorStatus(boolean verticalMirrorStatus) {
        this.verticalMirrorStatus = verticalMirrorStatus;
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

    public long getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public MaterialData setUri(Uri uri) {
        this.uri = uri;
        return this;
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

    public long getValidDuration() {
        return validDuration;
    }

    public void setValidDuration(long validDuration) {
        this.validDuration = validDuration;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return "MaterialData{" + "name='" + name + '\'' + ", path='" + path + '\'' + ", uri=" + uri + ", size=" + size
            + ", mimeType='" + mimeType + '\'' + ", addTime=" + addTime + ", index=" + index + ", duration=" + duration
            + ", width=" + width + ", height=" + height + ", validDuration=" + validDuration + ", cutTrimIn="
            + cutTrimIn + ", cutTrimOut=" + cutTrimOut + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeParcelable(this.uri, flags);
        dest.writeLong(this.size);
        dest.writeString(this.mimeType);
        dest.writeLong(this.addTime);
        dest.writeInt(this.index);
        dest.writeLong(this.duration);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeLong(this.validDuration);
        dest.writeLong(this.cutTrimIn);
        dest.writeLong(this.cutTrimOut);

        dest.writeFloat(this.glLeftBottomX);
        dest.writeFloat(this.glLeftBottomY);
        dest.writeFloat(this.glRightTopX);
        dest.writeFloat(this.glRightTopY);
        dest.writeFloat(this.scaleX);
        dest.writeFloat(this.scaleY);
        dest.writeByte((byte) (this.mirrorStatus ? 1 : 0));
        dest.writeByte((byte) (this.verticalMirrorStatus ? 1 : 0));
        dest.writeFloat(this.rotation);

        dest.writeFloat(this.templateCenterX);
        dest.writeFloat(this.templateCenterY);
        dest.writeFloat(this.templateScaleWidth);
        dest.writeFloat(this.templateScaleHeight);
    }

    public MaterialData() {
    }

    protected MaterialData(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.size = in.readLong();
        this.mimeType = in.readString();
        this.addTime = in.readLong();
        this.index = in.readInt();
        this.duration = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.validDuration = in.readLong();
        this.cutTrimIn = in.readLong();
        this.cutTrimOut = in.readLong();

        this.glLeftBottomX = in.readFloat();
        this.glLeftBottomY = in.readFloat();
        this.glRightTopX = in.readFloat();
        this.glRightTopY = in.readFloat();
        this.scaleX = in.readFloat();
        this.scaleY = in.readFloat();
        this.mirrorStatus = in.readByte() != 0;
        this.verticalMirrorStatus = in.readByte() != 0;
        this.rotation = in.readFloat();

        this.templateCenterX = in.readFloat();
        this.templateCenterY = in.readFloat();
        this.templateScaleWidth = in.readFloat();
        this.templateScaleHeight = in.readFloat();
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

    @NonNull
    @Override
    public Object clone() {
        try {
            return (MaterialData) super.clone();
        } catch (CloneNotSupportedException e) {
            SmartLog.e("MaterialData", "[clone] error info:" + e.getMessage());
        }
        return new Object();
    }
}
