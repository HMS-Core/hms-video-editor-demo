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

import java.util.List;

import android.app.Application;

import com.huawei.hms.videoeditor.template.HVETemplateManager;
import com.huawei.hms.videoeditor.template.HVETemplateInfo;
import com.huawei.hms.videoeditor.ui.template.bean.Constant;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class HVETemplateCategoryModel extends AndroidViewModel {
    private final MutableLiveData<String> errorString = new MutableLiveData<>();

    private final MutableLiveData<String> emptyString = new MutableLiveData<>();

    private final MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    private final MutableLiveData<List<HVETemplateInfo>> mHVECloudTemplateList = new MutableLiveData<>();

    public HVETemplateCategoryModel(@NonNull Application application) {
        super(application);
    }

    public void initTemplateListLiveData(int currentPage, String id, boolean isForceNetwork) {
        HVETemplateManager.getInstance()
                .getTemplateInfos(id, Constant.TEMPLATE_PAGE_SIZE, currentPage * Constant.TEMPLATE_PAGE_SIZE,
                        isForceNetwork, new HVETemplateManager.HVETemplateInfosCallback() {
                            @Override
                            public void onSuccess(List<HVETemplateInfo> result, boolean hasMore) {
                                boundaryPageData.postValue(hasMore);
                                mHVECloudTemplateList.postValue(result);
                            }

                            @Override
                            public void onFail(int errorCode) {
                                if (errorCode == HVETemplateManager.TEMPLATE_NETWORK_ERROR) {
                                    errorString.postValue(getApplication().getString(R.string.result_illegal));
                                } else {
                                    emptyString.postValue(getApplication().getString(R.string.result_empty));
                                }
                            }
                        });
    }


    public MutableLiveData<List<HVETemplateInfo>> getHVECloudTemplateList() {
        return mHVECloudTemplateList;
    }

    public MutableLiveData<String> getErrorString() {
        return errorString;
    }

    public MutableLiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    public MutableLiveData<String> getEmptyString() {
        return emptyString;
    }

}
