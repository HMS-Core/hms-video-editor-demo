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

package com.huawei.hms.videoeditor.ui.mediaeditor.materialedit;

import java.util.List;

import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEPosition2D;

public class MaterialEditData {

    private HVEVisibleAsset mAsset;

    private MaterialType mMaterialType;

    private List<HVEPosition2D> mFaceBoxList;

    public MaterialEditData(HVEVisibleAsset asset, MaterialType materialType) {
        this.mAsset = asset;
        this.mMaterialType = materialType;
    }

    public MaterialEditData(HVEVisibleAsset asset, MaterialType materialType, List<HVEPosition2D> faceBoxList) {
        mAsset = asset;
        mMaterialType = materialType;
        mFaceBoxList = faceBoxList;
    }

    public HVEVisibleAsset getAsset() {
        return mAsset;
    }

    public void setAsset(HVEVisibleAsset asset) {
        this.mAsset = asset;
    }

    public MaterialType getMaterialType() {
        return mMaterialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.mMaterialType = materialType;
    }

    public List<HVEPosition2D> getFaceBoxList() {
        return mFaceBoxList;
    }

    public void setFaceBoxList(List<HVEPosition2D> faceBoxList) {
        mFaceBoxList = faceBoxList;
    }

    @Override
    public String toString() {
        return "MaterialEditData{" + "mAsset=" + mAsset + ", mMaterialType=" + mMaterialType + ", mFaceBoxList="
            + mFaceBoxList + '}';
    }

    public enum MaterialType {
        MAIN_LANE,
        PIP_LANE,
        STICKER,
        WORD,
        WORD_TAIL,
        FACE,
        PERSON,
    }
}
