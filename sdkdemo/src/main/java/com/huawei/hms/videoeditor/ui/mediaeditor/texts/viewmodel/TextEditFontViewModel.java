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

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.PAGE_SIZE;

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
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TextEditFontViewModel extends AndroidViewModel {
    private static final String TAG = "TextEditFontViewModel";

    private final MutableLiveData<String> errorString = new MutableLiveData<>();

    private final MutableLiveData<String> emptyString = new MutableLiveData<>();

    private final MutableLiveData<List<CloudMaterialBean>> mFontMaterials = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mDownloadSuccess = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mDownloadFail = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mDownloadProgress = new MutableLiveData<>();

    private final MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    private MutableLiveData<String> fontColumn = new MutableLiveData<>();

    public TextEditFontViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadMaterials(Integer page) {
        HVEMaterialsResponseCallback contentFontListener = new HVEMaterialsResponseCallback<HVEChildColumnResponse>() {
            @Override
            public void onFinish(HVEChildColumnResponse response) {
                initMaterialsCutContentResp(response, R.string.result_empty);
            }

            @Override
            public void onUpdate(HVEChildColumnResponse response) {
                initMaterialsCutContentResp(response, R.string.result_empty);
            }

            @Override
            public void onError(Exception e) {
                errorString.postValue(getApplication().getString(R.string.result_illegal));
                String b = e.getMessage();
                SmartLog.e(TAG, b);
            }
        };

        HVEMaterialsResponseCallback columnFontListener = new HVEMaterialsResponseCallback<HVETopColumnResponse>() {
            @Override
            public void onFinish(HVETopColumnResponse response) {
                initMaterialsCutColumnResp(response, page, contentFontListener);
            }

            @Override
            public void onUpdate(HVETopColumnResponse response) {
                initMaterialsCutColumnResp(response, page, contentFontListener);
            }

            @Override
            public void onError(Exception e) {
                errorString.postValue(getApplication().getString(R.string.result_illegal));
                String b = e.getMessage();
                SmartLog.e(TAG, b);
            }
        };

        List<String> fatherColumnFont = new ArrayList<>();
        fatherColumnFont.add(HVEMaterialConstant.TEXT_FONT_FATHER_COLUMN);
        HVETopColumnRequest request = new HVETopColumnRequest(fatherColumnFont);

        HVEMaterialsManager.getTopColumnById(request, columnFontListener);
    }

    private void initMaterialsCutContentResp(HVEChildColumnResponse response, int p) {
        List<HVEMaterialInfo> bubblesContents = response.getMaterialInfoList();
        if (!bubblesContents.isEmpty()) {
            boundaryPageData.postValue(response.isHasMoreItem());
            queryDownloadStatus(bubblesContents);
        } else {
            errorString.postValue(getApplication().getString(p));
        }
    }

    private void initMaterialsCutColumnResp(HVETopColumnResponse response, Integer page,
        HVEMaterialsResponseCallback<HVEChildColumnResponse> contentListener) {
        List<HVETopColumnInfo> columns = response.getColumnInfos();
        if (columns.isEmpty()) {
            return;
        }

        HVETopColumnInfo topColumnInfo = columns.get(0);
        if (topColumnInfo == null || topColumnInfo.getChildInfoList().isEmpty()) {
            return;
        }

        List<HVEColumnInfo> fontColumns = topColumnInfo.getChildInfoList();

        if (fontColumns.size() > 0) {
            SmartLog.i(TAG, "return text font content category");
            HVEChildColumnRequest request =
                new HVEChildColumnRequest(fontColumns.get(0).getColumnId(), page * PAGE_SIZE, PAGE_SIZE, false);

            fontColumn.postValue(fontColumns.get(0).getColumnName());

            HVEMaterialsManager.getChildColumnById(request, contentListener);
        }
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
        mFontMaterials.postValue(list);
    }

    public void downloadColumn(int previousPosition, int position, int dataPosition, CloudMaterialBean cutContent) {
        MaterialsDownloadInfo downloadFontInfo = new MaterialsDownloadInfo();
        downloadFontInfo.setPreviousPosition(previousPosition);
        downloadFontInfo.setDataPosition(dataPosition);
        downloadFontInfo.setPosition(position);
        downloadFontInfo.setContentId(cutContent.getId());
        downloadFontInfo.setMaterialBean(cutContent);

        HVEDownloadMaterialRequest request = new HVEDownloadMaterialRequest(cutContent.getId());
        HVEMaterialsManager.downloadMaterialById(request, new HVEDownloadMaterialListener() {
            @Override
            public void onSuccess(String file) {
                SmartLog.i(TAG, "onDecompressionSuccess" + file);
                downloadFontInfo.setMaterialLocalPath(file);
                mDownloadSuccess.postValue(downloadFontInfo);
            }

            @Override
            public void onProgress(int progress) {
                SmartLog.i(TAG, "onDownloading" + progress + "---" + cutContent.getId());
                downloadFontInfo.setProgress(progress);
                mDownloadProgress.postValue(downloadFontInfo);
            }

            @Override
            public void onFailed(Exception exception) {
                SmartLog.i(TAG, exception.getMessage());
                downloadFontInfo.setMaterialLocalPath("");
                mDownloadFail.postValue(downloadFontInfo);
            }

            @Override
            public void onAlreadyDownload(String file) {
                downloadFontInfo.setMaterialLocalPath(file);
                mDownloadSuccess.postValue(downloadFontInfo);
                SmartLog.i(TAG, "onDownloadExists");
            }
        });
    }

    public MutableLiveData<String> getErrorString() {
        return errorString;
    }

    public MutableLiveData<String> getEmptyString() {
        return emptyString;
    }

    public MutableLiveData<List<CloudMaterialBean>> getPageData() {
        return mFontMaterials;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadSuccess() {
        return mDownloadSuccess;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadFail() {
        return mDownloadFail;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadProgress() {
        return mDownloadProgress;
    }

    public LiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    public MutableLiveData<String> getFontColumn() {
        return fontColumn;
    }

}
