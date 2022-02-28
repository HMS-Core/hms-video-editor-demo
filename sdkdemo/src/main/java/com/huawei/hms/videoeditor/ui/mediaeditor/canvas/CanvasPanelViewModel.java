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

package com.huawei.hms.videoeditor.ui.mediaeditor.canvas;

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
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEBlur;
import com.huawei.hms.videoeditor.sdk.bean.HVECanvas;
import com.huawei.hms.videoeditor.sdk.bean.HVEColor;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class CanvasPanelViewModel extends AndroidViewModel {

    private static final String TAG = "CanvasPanelViewModel";

    private final MutableLiveData<List<HVEColumnInfo>> mCanvasColumns = new MutableLiveData<>();

    private final MutableLiveData<String> cErrorString = new MutableLiveData<>();

    private final MutableLiveData<List<CloudMaterialBean>> mMaterials = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mDownloadSuccess = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mDownloadFail = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mDownloadProgress = new MutableLiveData<>();

    private EditPreviewViewModel mEditPreviewViewModel;

    private HuaweiVideoEditor mEditor;

    private HVETimeLine mTimeLine;

    public CanvasPanelViewModel(@NonNull Application application) {
        super(application);
    }

    public void setEditPreviewViewModel(EditPreviewViewModel mEditPreviewViewModel) {
        this.mEditPreviewViewModel = mEditPreviewViewModel;
        this.mEditor = mEditPreviewViewModel.getEditor();
        if (mEditor != null) {
            this.mTimeLine = mEditor.getTimeLine();
        } else {
            SmartLog.e(TAG, "setEditPreviewViewModel editor null");
        }
    }

    public void initColumns() {
        List<String> fatherColumn = new ArrayList<>();
        fatherColumn.add(HVEMaterialConstant.CANVAS_FATHER_COLUMN);

        HVETopColumnRequest canvasEvent = new HVETopColumnRequest(fatherColumn);
        HVEMaterialsManager.getTopColumnById(canvasEvent, new HVEMaterialsResponseCallback<HVETopColumnResponse>() {
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
                cErrorString.postValue(getApplication().getString(R.string.result_illegal));
                String b = e.getMessage();
                SmartLog.e(TAG, b);
            }
        });
    }

    private void initMaterialsCutColumnResp(HVETopColumnResponse response) {
        List<HVETopColumnInfo> topColumnInfos = response.getColumnInfos();
        if (topColumnInfos == null || topColumnInfos.isEmpty()) {
            SmartLog.i(TAG, "materialsCutContents is empty");
            cErrorString.postValue(getApplication().getString(R.string.result_illegal));
            return;
        }

        HVETopColumnInfo topColumnInfo = topColumnInfos.get(0);
        List<HVEColumnInfo> canvasList = topColumnInfo.getChildInfoList();
        if (canvasList.isEmpty()) {
            cErrorString.postValue(getApplication().getString(R.string.result_illegal));
            return;
        }

        if (topColumnInfo.getColumnId().equals(HVEMaterialConstant.CANVAS_FATHER_COLUMN)) {
            mCanvasColumns.postValue(canvasList);
        }
    }

    public void loadMaterials(HVEColumnInfo columnInfo, Integer page) {
        HVEChildColumnRequest request =
            new HVEChildColumnRequest(columnInfo.getColumnId(), page * Constant.PAGE_SIZE, Constant.PAGE_SIZE, false);

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
                cErrorString.postValue(getApplication().getString(R.string.result_illegal));
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
        mMaterials.postValue(list);
    }

    public void downloadColumn(int previousPosition, int position, int dataPosition, CloudMaterialBean cutContent) {
        MaterialsDownloadInfo downloadCanvasInfo = new MaterialsDownloadInfo();
        downloadCanvasInfo.setPreviousPosition(previousPosition);
        downloadCanvasInfo.setDataPosition(dataPosition);
        downloadCanvasInfo.setPosition(position);
        downloadCanvasInfo.setContentId(cutContent.getId());
        downloadCanvasInfo.setMaterialBean(cutContent);

        HVEDownloadMaterialRequest request = new HVEDownloadMaterialRequest(cutContent.getId());
        HVEMaterialsManager.downloadMaterialById(request, new HVEDownloadMaterialListener() {
            @Override
            public void onSuccess(String file) {
                downloadCanvasInfo.setMaterialLocalPath(file);
                mDownloadSuccess.postValue(downloadCanvasInfo);
            }

            @Override
            public void onProgress(int progress) {
                downloadCanvasInfo.setProgress(progress);
                mDownloadProgress.postValue(downloadCanvasInfo);
            }

            @Override
            public void onFailed(Exception exception) {
                SmartLog.e(TAG, exception.getMessage());
                mDownloadFail.postValue(downloadCanvasInfo);
            }

            @Override
            public void onAlreadyDownload(String file) {
                downloadCanvasInfo.setMaterialLocalPath(file);
                mDownloadSuccess.postValue(downloadCanvasInfo);
                SmartLog.i(TAG, "onDownloadExists");
            }
        });
    }

    public MutableLiveData<List<CloudMaterialBean>> getPageData() {
        return mMaterials;
    }

    public MutableLiveData<String> getcErrorString() {
        return cErrorString;
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

    public MutableLiveData<List<HVEColumnInfo>> getColumns() {
        return mCanvasColumns;
    }

    public void setBackgroundColor(HVEColor color, boolean applyAll) {
        HVEVideoLane videoLane = mEditPreviewViewModel.getVideoLane();
        if (mEditor == null || mTimeLine == null || videoLane == null) {
            return;
        }
        if (color == null) {
            color = new HVEColor(0, 0, 0, 1);
        }
        HVECanvas canvas = new HVECanvas(color);
        if (applyAll) {
            videoLane.setLaneCanvas(canvas);
        } else {
            HVEAsset asset = mEditPreviewViewModel.getMainLaneAsset();
            if (asset != null) {
                videoLane.setAssetCanvas(asset.getIndex(), canvas);
            }
        }
        mEditor.seekTimeLine(mTimeLine.getCurrentTime());
    }

    public String getBitmapString() {
        HVEAsset asset = mEditPreviewViewModel.getMainLaneAsset();
        if (asset == null) {
            return null;
        }
        if (asset instanceof HVEVideoAsset) {
            return ((HVEVideoAsset) asset).getCanvas().getImagePath();
        }
        if (asset instanceof HVEImageAsset) {
            return ((HVEImageAsset) asset).getCanvas().getImagePath();
        }
        return null;
    }

    public boolean setBackgroundBitmap(String filePath, String id, boolean applyAll) {
        HVEVideoLane videoLane = mEditPreviewViewModel.getVideoLane();
        if (mEditor == null || mTimeLine == null || videoLane == null) {
            return false;
        }
        HVECanvas canvas;
        if (TextUtils.isEmpty(filePath)) {
            setBackgroundColor(null, applyAll);
        } else {
            if (TextUtils.isEmpty(id)) {
                canvas = new HVECanvas(filePath);
            } else {
                canvas = new HVECanvas(filePath, id);
            }
            if (applyAll) {
                videoLane.setLaneCanvas(canvas);
            } else {
                HVEAsset asset = mEditPreviewViewModel.getMainLaneAsset();
                if (asset != null) {
                    videoLane.setAssetCanvas(asset.getIndex(), canvas);
                }
            }
        }
        mEditor.seekTimeLine(mTimeLine.getCurrentTime());
        return true;
    }

    public HVEBlur getBackgroundBlur() {
        HVEAsset asset = mEditPreviewViewModel.getMainLaneAsset();
        if (asset == null) {
            return null;
        }
        if (asset instanceof HVEVideoAsset) {
            return ((HVEVideoAsset) asset).getCanvas().getBlur();
        }
        if (asset instanceof HVEImageAsset) {
            return ((HVEImageAsset) asset).getCanvas().getBlur();
        }
        return null;
    }

    public void setBackgroundBlur(HVEBlur blur, boolean applyAll) {
        HVEVideoLane videoLane = mEditPreviewViewModel.getVideoLane();
        if (mEditor == null || mTimeLine == null || videoLane == null) {
            return;
        }
        HVECanvas canvas = new HVECanvas(blur);
        if (applyAll) {
            videoLane.setLaneCanvas(canvas);
        } else {
            HVEAsset asset = mEditPreviewViewModel.getMainLaneAsset();
            if (asset != null) {
                videoLane.setAssetCanvas(asset.getIndex(), canvas);
            }
        }
        mEditor.seekTimeLine(mTimeLine.getCurrentTime());
    }
}
