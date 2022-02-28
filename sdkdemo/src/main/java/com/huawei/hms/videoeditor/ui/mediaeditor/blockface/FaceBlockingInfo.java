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

package com.huawei.hms.videoeditor.ui.mediaeditor.blockface;

import android.graphics.Bitmap;

import com.huawei.hms.videoeditor.sdk.bean.HVEAIFaceTemplate;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;

public class FaceBlockingInfo {
    private int id;

    private String type;

    private Bitmap bitmap;

    private Long firstTimeStamp;

    private String localSticker;

    private boolean isSelected = false;

    private boolean isGetFocus = false;

    private boolean isMosaic = true;

    private boolean isShowGray = true;

    private CloudMaterialBean materialsCutContent;

    private HVEAIFaceTemplate faceTemplates;

    public FaceBlockingInfo() {
    }

    public FaceBlockingInfo(String type, String localSticker, CloudMaterialBean materialsCutContent,
                            boolean isShowGray) {
        this.type = type;
        this.isShowGray = isShowGray;
        this.localSticker = localSticker;
        this.materialsCutContent = materialsCutContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Long getFirstTimeStamp() {
        return firstTimeStamp;
    }

    public void setFirstTimeStamp(Long firstTimeStamp) {
        this.firstTimeStamp = firstTimeStamp;
    }

    public String getLocalSticker() {
        return localSticker;
    }

    public void setLocalSticker(String localSticker) {
        this.localSticker = localSticker;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isGetFocus() {
        return isGetFocus;
    }

    public void setGetFocus(boolean getFocus) {
        isGetFocus = getFocus;
    }

    public boolean isMosaic() {
        return isMosaic;
    }

    public void setMosaic(boolean mosaic) {
        isMosaic = mosaic;
    }

    public boolean isShowGray() {
        return isShowGray;
    }

    public void setShowGray(boolean showGray) {
        isShowGray = showGray;
    }

    public CloudMaterialBean getMaterialsCutContent() {
        return materialsCutContent;
    }

    public void setMaterialsCutContent(CloudMaterialBean materialsCutContent) {
        this.materialsCutContent = materialsCutContent;
    }

    public HVEAIFaceTemplate getFaceTemplates() {
        return faceTemplates;
    }

    public void setFaceTemplates(HVEAIFaceTemplate faceTemplates) {
        this.faceTemplates = faceTemplates;
    }
}
