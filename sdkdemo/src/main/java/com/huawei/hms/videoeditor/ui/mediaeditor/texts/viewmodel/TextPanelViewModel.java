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

package com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel;

import android.app.Application;

import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class TextPanelViewModel extends AndroidViewModel {

    private CloudMaterialBean mFontContent;

    private CloudMaterialBean mBubblesContent;

    private CloudMaterialBean mFlowerContent;

    private CloudMaterialBean mAnimaText;

    private MutableLiveData<String> animColumn = new MutableLiveData<>();

    private MutableLiveData<String> fontColumn = new MutableLiveData<>();

    public TextPanelViewModel(@NonNull Application application) {
        super(application);

    }

    public CloudMaterialBean getFontContent() {
        return mFontContent;
    }

    public void setFontContent(CloudMaterialBean mFontContent) {
        this.mFontContent = mFontContent;
    }

    public CloudMaterialBean getBubblesContent() {
        return mBubblesContent;
    }

    public void setBubblesContent(CloudMaterialBean mBubblesContent) {
        this.mBubblesContent = mBubblesContent;
    }

    public CloudMaterialBean getFlowerContent() {
        return mFlowerContent;
    }

    public void setFlowerContent(CloudMaterialBean mFlowerContent) {
        this.mFlowerContent = mFlowerContent;
    }

    public CloudMaterialBean getAnimaText() {
        return mAnimaText;
    }

    public void setAnimColumn(String animColumnStr) {
        animColumn.postValue(animColumnStr);
    }

    public MutableLiveData<String> getAnimColumn() {
        return animColumn;
    }

    public void setFontColumn(String fontColumnStr) {
        fontColumn.postValue(fontColumnStr);
    }

    public MutableLiveData<String> getFontColumn() {
        return fontColumn;
    }

    public void setAnimaText(CloudMaterialBean animaText) {
        this.mAnimaText = animaText;
    }
}
