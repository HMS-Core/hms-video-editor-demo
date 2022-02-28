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

package com.huawei.hms.videoeditor.ui.mediaeditor.aihair.viewmodel;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.materials.HVEChildColumnRequest;
import com.huawei.hms.videoeditor.materials.HVEChildColumnResponse;
import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.materials.HVEDownloadMaterialListener;
import com.huawei.hms.videoeditor.materials.HVEDownloadMaterialRequest;
import com.huawei.hms.videoeditor.materials.HVELocalMaterialInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialConstant;
import com.huawei.hms.videoeditor.materials.HVEMaterialInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialsManager;
import com.huawei.hms.videoeditor.materials.HVEMaterialsResponseCallback;
import com.huawei.hms.videoeditor.materials.HVETopColumnInfo;
import com.huawei.hms.videoeditor.materials.HVETopColumnRequest;
import com.huawei.hms.videoeditor.materials.HVETopColumnResponse;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class AiHairViewModel extends AndroidViewModel {
    private static final String TAG = "AiHairViewModel";

    private final MutableLiveData<List<HVEColumnInfo>> mAiHairTitleColumns = new MutableLiveData<>();

    private final MutableLiveData<String> errorText = new MutableLiveData<>();

    private final MutableLiveData<String> emptyText = new MutableLiveData<>();

    private final MutableLiveData<List<CloudMaterialBean>> mAiHairMaterials = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mAiHairDownloadSuccess = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mAiHairDownloadFail = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mAiHairDownloadProgress = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mHasNextPageData = new MutableLiveData<>();

    private final MutableLiveData<String> mFatherColumn = new MutableLiveData<>();

    public AiHairViewModel(@NonNull Application application) {
        super(application);
    }

    public void initAiHairTitleColumns() {
        if (TextUtils.isEmpty(mFatherColumn.getValue())) {
            SmartLog.i(TAG, "mFatherColumn is null");
            errorText.postValue(getApplication().getString(R.string.result_illegal));
            return;
        }

        List<String> titleColumn = new ArrayList<>();
        titleColumn.add(mFatherColumn.getValue());
        HVETopColumnRequest request = new HVETopColumnRequest(titleColumn);
        HVEMaterialsManager.getTopColumnById(request, new HVEMaterialsResponseCallback<HVETopColumnResponse>() {
            @Override
            public void onFinish(HVETopColumnResponse response) {
                initMaterialsAiHairTitleColumnResp(response);
            }

            @Override
            public void onUpdate(HVETopColumnResponse response) {
                initMaterialsAiHairTitleColumnResp(response);
            }

            @Override
            public void onError(Exception e) {
                errorText.postValue(getApplication().getString(R.string.result_illegal));
                String b = e.getMessage();
                SmartLog.e(TAG, b);
            }
        });

    }

    private void initMaterialsAiHairTitleColumnResp(HVETopColumnResponse aiHairTitleResp) {
        List<HVETopColumnInfo> materialsAHairTitleContents = aiHairTitleResp.getColumnInfos();

        if (materialsAHairTitleContents == null || materialsAHairTitleContents.isEmpty()) {
            SmartLog.i(TAG, "materialsAiHairTitleCutContents is null");
            emptyText.postValue(null);
        }

        assert materialsAHairTitleContents != null;
        for (HVETopColumnInfo aiHairCutColumn : materialsAHairTitleContents) {
            List<HVEColumnInfo> aiHairList = aiHairCutColumn.getChildInfoList();
            if (aiHairList.isEmpty()) {
                emptyText.postValue(null);
                break;
            }
            if (aiHairCutColumn.getColumnId().equals(HVEMaterialConstant.AI_HAIR_FATHER_COLUMN)
                && aiHairList.size() > 0) {
                mAiHairTitleColumns.postValue(aiHairList);
                break;
            }
        }
    }

    public void loadAiHairMaterials(String columnId, Integer page) {
        if (columnId.equals("-1")) {
            return;
        }

        HVEChildColumnRequest request =
            new HVEChildColumnRequest(columnId, page * Constant.PAGE_SIZE, Constant.PAGE_SIZE, false);

        HVEMaterialsManager.getChildColumnById(request, new HVEMaterialsResponseCallback<HVEChildColumnResponse>() {
            @Override
            public void onFinish(HVEChildColumnResponse response) {
                initAiHairMaterialsContentResp(response);
            }

            @Override
            public void onUpdate(HVEChildColumnResponse response) {
                initAiHairMaterialsContentResp(response);
            }

            @Override
            public void onError(Exception e) {
                errorText.postValue(getApplication().getString(R.string.result_illegal));
                String b = e.getMessage();
                SmartLog.e(TAG, b);
            }
        });
    }

    private void initAiHairMaterialsContentResp(HVEChildColumnResponse response) {
        List<HVEMaterialInfo> aiHairMaterials = response.getMaterialInfoList();

        mHasNextPageData.postValue(response.isHasMoreItem());
        if (aiHairMaterials.size() > 0) {
            SmartLog.i(TAG, "hasDownload:" + aiHairMaterials.toString());
            queryAiHairDownloadStatus(aiHairMaterials);
        } else {
            emptyText.postValue(null);
        }
    }

    private void queryAiHairDownloadStatus(List<HVEMaterialInfo> materialInfos) {
        List<CloudMaterialBean> list = new ArrayList<>();
        for (int i = 0; i < materialInfos.size(); i++) {
            CloudMaterialBean materialInfo = new CloudMaterialBean();

            HVEMaterialInfo hveMaterialInfo = materialInfos.get(i);

            HVELocalMaterialInfo localMaterialInfo =
                HVEMaterialsManager.queryLocalMaterialById(hveMaterialInfo.getMaterialId());
            if (!StringUtil.isEmpty(localMaterialInfo.getMaterialPath())) {
                materialInfo.setLocalPath(localMaterialInfo.getMaterialPath());
            }

            materialInfo.setPreviewUrl(hveMaterialInfo.getPreviewUrl());
            materialInfo.setId(hveMaterialInfo.getMaterialId());
            materialInfo.setName(hveMaterialInfo.getMaterialName());

            list.add(materialInfo);
        }
        mAiHairMaterials.postValue(list);
    }

    public void downloadAiHairMaterial(int previousPosition, int position, int dataPosition,
        CloudMaterialBean aiHairMaterial) {
        MaterialsDownloadInfo downloadLocalAiHairInfo = new MaterialsDownloadInfo();
        downloadLocalAiHairInfo.setPreviousPosition(previousPosition);
        downloadLocalAiHairInfo.setPosition(position);
        downloadLocalAiHairInfo.setDataPosition(dataPosition);
        downloadLocalAiHairInfo.setContentId(aiHairMaterial.getId());
        downloadLocalAiHairInfo.setMaterialBean(aiHairMaterial);

        HVEDownloadMaterialRequest request = new HVEDownloadMaterialRequest(aiHairMaterial.getId());
        HVEMaterialsManager.downloadMaterialById(request, new HVEDownloadMaterialListener() {
            @Override
            public void onSuccess(String file) {
                downloadLocalAiHairInfo.setMaterialLocalPath(file);
                mAiHairDownloadSuccess.postValue(downloadLocalAiHairInfo);
            }

            @Override
            public void onProgress(int progress) {
                downloadLocalAiHairInfo.setProgress(progress);
                mAiHairDownloadSuccess.postValue(downloadLocalAiHairInfo);
            }

            @Override
            public void onFailed(Exception exception) {
                SmartLog.e(TAG, exception.getMessage());
                mAiHairDownloadFail.postValue(downloadLocalAiHairInfo);
            }

            @Override
            public void onAlreadyDownload(String file) {
                downloadLocalAiHairInfo.setMaterialLocalPath(file);
                mAiHairDownloadSuccess.postValue(downloadLocalAiHairInfo);
                SmartLog.i(TAG, "onDownloadExists");
            }
        });
    }

    public MutableLiveData<String> getErrorText() {
        return errorText;
    }

    public MutableLiveData<List<CloudMaterialBean>> getPageData() {
        return mAiHairMaterials;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadSuccess() {
        return mAiHairDownloadSuccess;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadFail() {
        return mAiHairDownloadFail;
    }

    public MutableLiveData<List<HVEColumnInfo>> getAiHairTitleColumns() {
        return mAiHairTitleColumns;
    }

    public MutableLiveData<Boolean> getHasNextPageData() {
        return mHasNextPageData;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadProgress() {
        return mAiHairDownloadProgress;
    }

    public MutableLiveData<String> getEmptyText() {
        return emptyText;
    }

    public MutableLiveData<String> getFatherColumn() {
        return mFatherColumn;
    }

    public void setFatherColumn(String fatherColumn) {
        mFatherColumn.setValue(fatherColumn);
    }
}