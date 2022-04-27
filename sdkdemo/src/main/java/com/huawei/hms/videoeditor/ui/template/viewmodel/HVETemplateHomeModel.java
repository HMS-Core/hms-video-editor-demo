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

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.template.HVETemplateManager;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.List;

public class HVETemplateHomeModel extends AndroidViewModel {
    private MutableLiveData<List<HVEColumnInfo>> mHVECategoryList = new MutableLiveData<>();

    private final MutableLiveData<String> errorString = new MutableLiveData<>();

    public HVETemplateHomeModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<HVEColumnInfo>> getHVECategoryList() {
        return mHVECategoryList;
    }

    public void initColumns() {
        HVETemplateManager.getInstance().getColumnInfos(new HVETemplateManager.HVETemplateColumnsCallback() {
            @Override
            public void onSuccess(List<HVEColumnInfo> result) {
                mHVECategoryList.postValue(result);
            }

            @Override
            public void onFail(int error) {
                if (error == HVETemplateManager.TEMPLATE_NETWORK_ERROR) {
                    errorString.postValue(getApplication().getString(R.string.result_illegal));
                } else {
                    errorString.postValue(null);
                }
            }
        });
    }

    public MutableLiveData<String> getErrorString() {
        return errorString;
    }
}