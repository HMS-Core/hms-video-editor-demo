
/*
 *  Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.hms.videoeditor.ui.mediaeditor.repository;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.huawei.hms.videoeditor.materials.HVEChildColumnRequest;
import com.huawei.hms.videoeditor.materials.HVEChildColumnResponse;
import com.huawei.hms.videoeditor.materials.HVEDownloadMaterialListener;
import com.huawei.hms.videoeditor.materials.HVEDownloadMaterialRequest;
import com.huawei.hms.videoeditor.materials.HVELocalMaterialInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialsManager;
import com.huawei.hms.videoeditor.materials.HVEMaterialsResponseCallback;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;

public class MaterialsRespository {

    private static final String TAG = "MaterialsRespository";

    public static final int RESULT_ILLEGAL = 0;

    public static final int RESULT_EMPTY = 1;

    public static final int DOWNLOAD_SUCCESS = 2;

    public static final int DOWNLOAD_FAIL = 3;

    public static final int DOWNLOAD_LOADING = 4;

    private MaterialsListener materialsListener;

    public MaterialsRespository(Application application) {
    }

    public void loadMaterials(String columnId, Integer page) {
        if (materialsListener == null) {
            return;
        }

        HVEChildColumnRequest request =
            new HVEChildColumnRequest(columnId, page * Constant.PAGE_SIZE, Constant.PAGE_SIZE, false);

        // 查询子栏目下的素材信息
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
                materialsListener.errorType(RESULT_ILLEGAL);
                String b = e.getMessage();
                SmartLog.e(TAG, b);
            }
        });
    }

    private void initMaterialsCutContentResp(HVEChildColumnResponse contentResp) {
        if (materialsListener == null || contentResp == null) {
            return;
        }
        List<HVEMaterialInfo> materialsCutContentList = contentResp.getMaterialInfoList();

        materialsListener.boundaryPageData(contentResp.isHasMoreItem());
        if (materialsCutContentList.size() > 0) {
            queryDownloadStatus(materialsCutContentList);
        } else {
            materialsListener.errorType(RESULT_EMPTY);
        }
    }

    private void queryDownloadStatus(List<HVEMaterialInfo> materialInfos) {
        if (materialsListener == null || materialInfos == null) {
            return;
        }
        if (materialInfos.isEmpty()) {
            return;
        }

        List<CloudMaterialBean> list = new ArrayList<>();
        for (int i = 0; i < materialInfos.size(); i++) {
            CloudMaterialBean materialInfo = new CloudMaterialBean();

            // 构造UI所需要的数据
            HVEMaterialInfo hveMaterialInfo = materialInfos.get(i);

            // 存储本地路径
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
        materialsListener.pageData(list);
    }

    public void downloadMaterials(int previousPosition, int position, CloudMaterialBean cutContent) {
        if (materialsListener == null || cutContent == null) {
            return;
        }
        MaterialsDownloadInfo downloadInfo = new MaterialsDownloadInfo();
        downloadInfo.setPreviousPosition(previousPosition);
        downloadInfo.setPosition(position);
        downloadInfo.setDataPosition(position);
        downloadInfo.setContentId(cutContent.getId());
        downloadInfo.setMaterialBean(cutContent);

        HVEDownloadMaterialRequest request = new HVEDownloadMaterialRequest(cutContent.getId());
        HVEMaterialsManager.downloadMaterialById(request, new HVEDownloadMaterialListener() {
            @Override
            public void onSuccess(String file) {
                downloadInfo.setMaterialLocalPath(file);
                downloadInfo.setState(DOWNLOAD_SUCCESS);
                materialsListener.downloadInfo(downloadInfo);
            }

            @Override
            public void onProgress(int progress) {
                downloadInfo.setProgress(progress);
                downloadInfo.setState(DOWNLOAD_LOADING);
                materialsListener.downloadInfo(downloadInfo);
            }

            @Override
            public void onFailed(Exception exception) {
                SmartLog.e(TAG, exception.getMessage());
                downloadInfo.setMaterialLocalPath("");
                downloadInfo.setState(DOWNLOAD_FAIL);
                materialsListener.downloadInfo(downloadInfo);
            }

            @Override
            public void onAlreadyDownload(String file) {
                downloadInfo.setMaterialLocalPath(file);
                downloadInfo.setState(DOWNLOAD_SUCCESS);
                materialsListener.downloadInfo(downloadInfo);
                SmartLog.i(TAG, "onDownloadExists");
            }
        });
    }

    public void setMaterialsListener(MaterialsListener materialsListener) {
        this.materialsListener = materialsListener;
    }
}
