
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

package com.huawei.hms.videoeditor.ui.mediaeditor.audio.viewmodel;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialConstant;
import com.huawei.hms.videoeditor.materials.HVEMaterialsManager;
import com.huawei.hms.videoeditor.materials.HVEMaterialsResponseCallback;
import com.huawei.hms.videoeditor.materials.HVETopColumnInfo;
import com.huawei.hms.videoeditor.materials.HVETopColumnRequest;
import com.huawei.hms.videoeditor.materials.HVETopColumnResponse;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class SoundEffectViewModel extends AndroidViewModel {

    private static final String TAG = "SoundEffectViewModel";

    private MutableLiveData<CloudMaterialBean> mSelectData = new MutableLiveData<>();

    private MutableLiveData<List<HVEColumnInfo>> soundEffectColumnsContent = new MutableLiveData<>();

    private final MutableLiveData<String> errorString = new MutableLiveData<>();

    public SoundEffectViewModel(@NonNull Application application) {
        super(application);
    }

    public void initColumns() {
        List<String> fatherColumn = new ArrayList<>();
        fatherColumn.add(HVEMaterialConstant.SOUND_EFFECT_FATHER_COLUMN);
        HVETopColumnRequest request = new HVETopColumnRequest(fatherColumn);

        HVEMaterialsManager.getTopColumnById(request, new HVEMaterialsResponseCallback<HVETopColumnResponse>() {
            @Override
            public void onFinish(HVETopColumnResponse response) {
                initMaterialsCutColumnResp(response);
            }

            @Override
            public void onUpdate(HVETopColumnResponse response) {
                initMaterialsCutColumnResp(response);
            }

            @Override
            public void onError(Exception e) {
                errorString.postValue(getApplication().getString(R.string.result_illegal));
                String b = e.getMessage();
                SmartLog.e(TAG, b);
            }
        });

    }

    private void initMaterialsCutColumnResp(HVETopColumnResponse response) {
        List<HVETopColumnInfo> materialsCutContents = response.getColumnInfos();
        if (materialsCutContents != null && materialsCutContents.size() > 0) {
            for (HVETopColumnInfo materialsCutColumn : materialsCutContents) {
                if (materialsCutColumn.getColumnId().equals(HVEMaterialConstant.SOUND_EFFECT_FATHER_COLUMN)
                    && materialsCutColumn.getChildInfoList().size() > 0) {
                    soundEffectColumnsContent.postValue(materialsCutColumn.getChildInfoList());
                    break;
                }
            }
        }
    }

    public MutableLiveData<List<HVEColumnInfo>> getColumns() {
        return soundEffectColumnsContent;
    }

    public MutableLiveData<CloudMaterialBean> getSelectData() {
        return mSelectData;
    }

    public void setSelectCutContent(CloudMaterialBean mCutContent) {
        mSelectData.postValue(mCutContent);
    }

    public MutableLiveData<String> getErrorString() {
        return errorString;
    }
}
