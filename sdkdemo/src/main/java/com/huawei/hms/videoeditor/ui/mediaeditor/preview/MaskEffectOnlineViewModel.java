
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

package com.huawei.hms.videoeditor.ui.mediaeditor.preview;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

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

public class MaskEffectOnlineViewModel extends AndroidViewModel {

    private static final String TAG = "MaskEffectOnlineViewModel";

    private final MutableLiveData<List<HVEColumnInfo>> maskColumns = new MutableLiveData<>();

    private final MutableLiveData<String> maskErrorString = new MutableLiveData<>();

    private final MutableLiveData<String> maskEmptyString = new MutableLiveData<>();

    private final MutableLiveData<List<CloudMaterialBean>> maskMaterials = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> maskDownloadSuccess = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> maskDownloadFail = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> maskDownloadProgress = new MutableLiveData<>();

    private final MutableLiveData<Boolean> maskPageData = new MutableLiveData<>();

    public MaskEffectOnlineViewModel(@NonNull Application application) {
        super(application);
    }

    public void initColumns() {
        List<String> fatherColumn = new ArrayList<>();
        fatherColumn.add(HVEMaterialConstant.MASK_FATHER_COLUMN);
        HVETopColumnRequest topColumnRequest = new HVETopColumnRequest(fatherColumn);
        HVEMaterialsManager.getTopColumnById(topColumnRequest,
            new HVEMaterialsResponseCallback<HVETopColumnResponse>() {
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
                    maskErrorString.postValue(getApplication().getString(R.string.result_illegal));
                    String b = e.getMessage();
                    SmartLog.e(TAG, b);
                }
            });

    }

    private void initMaterialsCutColumnResp(HVETopColumnResponse response) {
        List<HVETopColumnInfo> topColumnInfos = response.getColumnInfos();

        if (topColumnInfos == null || topColumnInfos.isEmpty()) {
            SmartLog.i(TAG, "materialsCutContents is empty");
            maskEmptyString.postValue(getApplication().getString(R.string.result_illegal));
            return;
        }

        HVETopColumnInfo topColumnInfo = topColumnInfos.get(0);
        List<HVEColumnInfo> maskList = topColumnInfo.getChildInfoList();
        if (maskList.isEmpty()) {
            maskEmptyString.postValue(getApplication().getString(R.string.result_illegal));
            return;
        }

        if (topColumnInfo.getColumnId().equals(HVEMaterialConstant.MASK_FATHER_COLUMN)) {
            maskColumns.postValue(maskList);
        }
    }

    public void loadMaterials(String cloumnId, Integer page) {
        if (cloumnId.equals("-1")) {
            return;
        }

        HVEChildColumnRequest request =
            new HVEChildColumnRequest(cloumnId, page * Constant.PAGE_SIZE, Constant.PAGE_SIZE, false);

        HVEMaterialsManager.getChildColumnById(request, new HVEMaterialsResponseCallback<HVEChildColumnResponse>() {
            @Override
            public void onFinish(HVEChildColumnResponse response) {
                initMaterialsCutContentResp(response);
            }

            @Override
            public void onUpdate(HVEChildColumnResponse response) {
                initMaterialsCutContentResp(response);
            }

            @Override
            public void onError(Exception e) {
                maskErrorString.postValue(getApplication().getString(R.string.result_illegal));
                String b = e.getMessage();
                SmartLog.e(TAG, b);
            }
        });
    }

    private void initMaterialsCutContentResp(HVEChildColumnResponse response) {
        List<HVEMaterialInfo> materialInfos = response.getMaterialInfoList();
        if (materialInfos.isEmpty()) {
            SmartLog.e(TAG, "initMaterialsCutContentResp failed");
            return;
        }

        queryDownloadStatus(materialInfos);
    }

    private void queryDownloadStatus(List<HVEMaterialInfo> materialInfos) {
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

        maskMaterials.postValue(list);
    }

    public void downloadColumn(int previousPosition, int position, int dataPosition, CloudMaterialBean cutContent) {
        MaterialsDownloadInfo info = new MaterialsDownloadInfo();
        info.setPreviousPosition(previousPosition);
        info.setPosition(position);
        info.setDataPosition(dataPosition);
        info.setContentId(cutContent.getId());
        info.setMaterialBean(cutContent);

        HVEDownloadMaterialRequest request = new HVEDownloadMaterialRequest(cutContent.getId());
        HVEMaterialsManager.downloadMaterialById(request, new HVEDownloadMaterialListener() {
            @Override
            public void onSuccess(String file) {
                info.setMaterialLocalPath(file);
                maskDownloadSuccess.postValue(info);
            }

            @Override
            public void onProgress(int progress) {
                info.setProgress(progress);
                maskDownloadProgress.postValue(info);
            }

            @Override
            public void onFailed(Exception exception) {
                SmartLog.i(TAG, exception.getMessage());
                info.setMaterialLocalPath("");
                maskDownloadFail.postValue(info);
            }

            @Override
            public void onAlreadyDownload(String file) {
                info.setMaterialLocalPath(file);
                maskDownloadSuccess.postValue(info);
            }
        });
    }

    public MutableLiveData<String> getMaskErrorString() {
        return maskErrorString;
    }

    public MutableLiveData<List<CloudMaterialBean>> getPageData() {
        return maskMaterials;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadSuccess() {
        return maskDownloadSuccess;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadFail() {
        return maskDownloadFail;
    }

    public MutableLiveData<List<HVEColumnInfo>> getColumns() {
        return maskColumns;
    }

    public MutableLiveData<Boolean> getMaskPageData() {
        return maskPageData;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadProgress() {
        return maskDownloadProgress;
    }

    public MutableLiveData<String> getMaskEmptyString() {
        return maskEmptyString;
    }
}
