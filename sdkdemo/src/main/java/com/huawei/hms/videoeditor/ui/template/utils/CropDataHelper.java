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

package com.huawei.hms.videoeditor.ui.template.utils;

import java.util.ArrayList;
import java.util.List;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.template.bean.MaterialData;

public class CropDataHelper {
    private static final String TAG = "CropDataHelper";

    public CropDataHelper() {
        SmartLog.e(TAG, "create CropDataHelper instance");
    }

    public static MediaData convertMaterialDataToMediaData(MaterialData materialData) {
        return convertMaterialDataToMediaData(materialData, 0, 0);
    }

    public static MediaData convertMaterialDataToMediaData(MaterialData materialData, float scaleX, float scaleY) {
        if (materialData == null) {
            return new MediaData();
        }

        return materialData2MediaData(materialData, scaleX, scaleY);
    }

    public static List<MediaData> convertMaterialDataToMediaData(List<MaterialData> materialData) {
        if (materialData == null || materialData.isEmpty()) {
            return new ArrayList<>();
        }
        List<MediaData> dataList = new ArrayList<>();
        MediaData mediaData;
        for (int i = 0, size = materialData.size(); i < size; i++) {
            mediaData = materialData2MediaData(materialData.get(i), 0, 0);
            dataList.add(mediaData);
        }

        return dataList;
    }

    public static List<MaterialData> convertMediaDataToMaterialData(List<MediaData> mediaData) {
        if (mediaData == null || mediaData.isEmpty()) {
            return new ArrayList<>();
        }
        List<MaterialData> dataList = new ArrayList<>();
        MaterialData materialData;
        for (int i = 0, size = mediaData.size(); i < size; i++) {
            materialData = mediaData2MaterialData(mediaData.get(i));
            dataList.add(materialData);
        }
        return dataList;
    }

    public static MediaData getMediaDataFromMediaData(int position, List<MediaData> mediaDataList, float scaleX,
        float scaleY) {
        if (mediaDataList == null || mediaDataList.isEmpty() || position < 0 || position >= mediaDataList.size()) {
            SmartLog.e(TAG, "data is null or index out");
            return null;
        }

        MediaData mediaData = new MediaData();
        mediaData.setName(mediaDataList.get(position).getName());
        mediaData.setPath(mediaDataList.get(position).getPath());
        mediaData.setUri(mediaDataList.get(position).getUri());
        mediaData.setSize(mediaDataList.get(position).getSize());
        mediaData.setMimeType(mediaDataList.get(position).getMimeType());
        mediaData.setAddTime(mediaDataList.get(position).getAddTime());
        mediaData.setIndex(mediaDataList.get(position).getIndex());
        mediaData.setDuration(mediaDataList.get(position).getDuration());
        mediaData.setValidDuration(mediaDataList.get(position).getValidDuration());
        mediaData.setWidth(mediaDataList.get(position).getWidth());
        mediaData.setHeight(mediaDataList.get(position).getHeight());
        mediaData.setCutTrimIn(mediaDataList.get(position).getCutTrimIn());
        mediaData.setCutTrimOut(mediaDataList.get(position).getCutTrimOut());

        mediaData.setGlLeftBottomX(mediaDataList.get(position).getGlLeftBottomX());
        mediaData.setGlLeftBottomY(mediaDataList.get(position).getGlLeftBottomY());
        mediaData.setGlRightTopX(mediaDataList.get(position).getGlRightTopX());
        mediaData.setGlRightTopY(mediaDataList.get(position).getGlRightTopY());
        mediaData.setScaleX(mediaDataList.get(position).getScaleX());
        mediaData.setScaleY(mediaDataList.get(position).getScaleY());
        mediaData.setRotation(mediaDataList.get(position).getRotation());
        mediaData.setMirrorStatus(mediaDataList.get(position).isMirrorStatus());
        mediaData.setVerticalMirrorStatus(mediaDataList.get(position).isVerticalMirrorStatus());

        mediaData.setTemplateCenterX(mediaDataList.get(position).getTemplateCenterX());
        mediaData.setTemplateCenterY(mediaDataList.get(position).getTemplateCenterY());
        mediaData.setTemplateScaleWidth(mediaDataList.get(position).getTemplateScaleWidth());
        mediaData.setTemplateScaleHeight(mediaDataList.get(position).getTemplateScaleHeight());

        if (scaleX != 0 && scaleY != 0) {
            mediaData.setHVEWidth(scaleX);
            mediaData.setHVEHeight(scaleY);
        }

        return mediaData;
    }

    private static MaterialData mediaData2MaterialData(MediaData data) {
        if (data == null) {
            return new MaterialData();
        }
        MaterialData materialData = new MaterialData();
        materialData.setName(data.getName());
        materialData.setPath(data.getPath());
        materialData.setUri(data.getUri());
        materialData.setSize(data.getSize());
        materialData.setMimeType(data.getMimeType());
        materialData.setAddTime(data.getAddTime());
        materialData.setIndex(data.getIndex());
        materialData.setDuration(data.getDuration());
        materialData.setValidDuration(data.getValidDuration());
        materialData.setWidth(data.getWidth());
        materialData.setHeight(data.getHeight());
        materialData.setCutTrimIn(data.getCutTrimIn());
        materialData.setCutTrimOut(data.getCutTrimOut());

        materialData.setGlLeftBottomX(data.getGlLeftBottomX());
        materialData.setGlLeftBottomY(data.getGlLeftBottomY());
        materialData.setGlRightTopX(data.getGlRightTopX());
        materialData.setGlRightTopY(data.getGlRightTopY());
        materialData.setScaleX(data.getScaleX());
        materialData.setScaleY(data.getScaleY());
        materialData.setRotation(data.getRotation());
        materialData.setMirrorStatus(data.isMirrorStatus());
        materialData.setVerticalMirrorStatus(data.isVerticalMirrorStatus());

        materialData.setTemplateCenterX(data.getTemplateCenterX());
        materialData.setTemplateCenterY(data.getTemplateCenterY());
        materialData.setTemplateScaleWidth(data.getTemplateScaleWidth());
        materialData.setTemplateScaleHeight(data.getTemplateScaleHeight());

        return materialData;
    }

    private static MediaData materialData2MediaData(MaterialData data, float scaleX, float scaleY) {
        if (data == null) {
            return new MediaData();
        }
        MediaData mediaData = new MediaData();
        mediaData.setName(data.getName());
        mediaData.setPath(data.getPath());
        mediaData.setUri(data.getUri());
        mediaData.setSize(data.getSize());
        mediaData.setMimeType(data.getMimeType());
        mediaData.setAddTime(data.getAddTime());
        mediaData.setIndex(data.getIndex());
        mediaData.setDuration(data.getDuration());
        mediaData.setValidDuration(data.getValidDuration());
        mediaData.setWidth(data.getWidth());
        mediaData.setHeight(data.getHeight());
        mediaData.setCutTrimIn(data.getCutTrimIn());
        mediaData.setCutTrimOut(data.getCutTrimOut());

        mediaData.setGlLeftBottomX(data.getGlLeftBottomX());
        mediaData.setGlLeftBottomY(data.getGlLeftBottomY());
        mediaData.setGlRightTopX(data.getGlRightTopX());
        mediaData.setGlRightTopY(data.getGlRightTopY());
        mediaData.setScaleX(data.getScaleX());
        mediaData.setScaleY(data.getScaleY());
        mediaData.setRotation(data.getRotation());
        mediaData.setMirrorStatus(data.isMirrorStatus());
        mediaData.setVerticalMirrorStatus(data.isVerticalMirrorStatus());

        mediaData.setTemplateCenterX(data.getTemplateCenterX());
        mediaData.setTemplateCenterY(data.getTemplateCenterY());
        mediaData.setTemplateScaleWidth(data.getTemplateScaleWidth());
        mediaData.setTemplateScaleHeight(data.getTemplateScaleHeight());

        if (scaleX != 0 && scaleY != 0) {
            mediaData.setHVEWidth(scaleX);
            mediaData.setHVEHeight(scaleY);
        }
        return mediaData;
    }
}
