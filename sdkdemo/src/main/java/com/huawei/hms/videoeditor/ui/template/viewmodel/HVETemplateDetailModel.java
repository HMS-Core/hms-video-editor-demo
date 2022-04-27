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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.template.HVETemplateElement;
import com.huawei.hms.videoeditor.template.HVETemplateInfo;
import com.huawei.hms.videoeditor.template.HVETemplateManager;
import com.huawei.hms.videoeditor.ui.template.bean.TemplateProjectBean;
import com.huawei.hms.videoeditor.ui.template.module.activity.TemplateDetailActivity;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class HVETemplateDetailModel extends AndroidViewModel {
    private static final String TAG = "HVETemplateDetailModel";

    private static final Map<Object, Object> PROGRESS_MAP = new HashMap<>();

    private final MutableLiveData<String> errorString = new MutableLiveData<>();

    private final MutableLiveData<TemplateProjectBean> templateResources = new MutableLiveData<>();

    private final MutableLiveData<Map<Object, Object>> templateResourcesProgress =
        new MutableLiveData<Map<Object, Object>>();

    public HVETemplateDetailModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Map<Object, Object>> getTemplateResourcesProgress() {
        return templateResourcesProgress;
    }

    public MutableLiveData<TemplateProjectBean> getTemplateResources() {
        return templateResources;
    }

    public void initTemplateResourceLiveData(HVETemplateInfo templateInfo) {
        HVETemplateManager.getInstance()
            .getTemplateProject(templateInfo.getId(), new HVETemplateManager.HVETemplateProjectCallback() {
                @Override
                public void onSuccess(List<HVETemplateElement> editableElements) {
                    TemplateProjectBean detail = new TemplateProjectBean();
                    detail.setTemplateId(templateInfo.getId());
                    detail.setEditableElements(editableElements);
                    templateResources.postValue(detail);
                    PROGRESS_MAP.put(templateInfo.getId(), 100);
                    templateResourcesProgress.postValue(PROGRESS_MAP);
                }

                @Override
                public void onProgress(int progress) {
                    SmartLog.i(TAG,
                        "module onProgressUpdate progress value is : " + progress + "; id:" + templateInfo.getId());
                    if (progress >= 90) {
                        progress = 90;
                    } else if (progress < TemplateDetailActivity.MIN_PROGRESS) {
                        progress = TemplateDetailActivity.MIN_PROGRESS;
                    }
                    PROGRESS_MAP.put(templateInfo.getId(), progress);
                    templateResourcesProgress.postValue(PROGRESS_MAP);
                }

                @Override
                public void onFail(int errorCode) {
                    SmartLog.e(TAG, "getTemplateProject error: " + errorCode);
                    errorString.postValue(getApplication().getString(R.string.result_illegal));
                }
            });
    }

    public MutableLiveData<String> getErrorString() {
        return errorString;
    }
}
