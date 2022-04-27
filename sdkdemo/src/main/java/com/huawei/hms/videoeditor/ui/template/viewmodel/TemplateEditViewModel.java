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

package com.huawei.hms.videoeditor.ui.template.viewmodel;

import android.app.Application;

import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class TemplateEditViewModel extends AndroidViewModel {
    private static final String TAG = "ModuleEditViewModel";
    private final MutableLiveData<Float> mWidthLiveData = new MutableLiveData<>();

    private final MutableLiveData<Float> mHeightLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> mTemplateVideoPath = new MutableLiveData<>();

    private final MutableLiveData<HVEAsset> mSetHVEAsset = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mIsUpdata = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mBackState = new MutableLiveData<>();

    public TemplateEditViewModel(@NonNull Application application) {
        super(application);
    }

    public void setWidth(Float width) {
        mWidthLiveData.postValue(width);
    }

    public void setHeight(Float height) {
        mHeightLiveData.postValue(height);
    }

    public void setHVEAsset(HVEAsset hveAsset) {
        mSetHVEAsset.postValue(hveAsset);
    }

    public void setUpdata(boolean isUpdataData) {
        mIsUpdata.setValue(isUpdataData);
    }

    public void setBackState(boolean backState) {
        mBackState.setValue(backState);
    }

    public MutableLiveData<Boolean> getBackState() {
        return mBackState;
    }

    public MutableLiveData<Boolean> getUpdata() {
        return mIsUpdata;
    }

    public MutableLiveData<String> getTemplateVideoPath() {
        return mTemplateVideoPath;
    }
}
