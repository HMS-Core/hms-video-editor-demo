
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

package com.huawei.hms.videoeditor.ui.mediaeditor.filter;

import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.HVEEffectType.FILTER;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.PAGE_SIZE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;

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
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.lane.HVEEffectLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.FilterData;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.utils.LaneSizeCheckUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class FilterPanelViewModel extends AndroidViewModel {

    private static final String TAG = "FilterPanelViewModel";

    private Context context;

    private final MutableLiveData<List<HVEColumnInfo>> mFilterColumns = new MutableLiveData<>();

    private final MutableLiveData<String> errorString = new MutableLiveData<>();

    private final MutableLiveData<String> emptyString = new MutableLiveData<>();

    private final MutableLiveData<List<CloudMaterialBean>> mFilterMaterials = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mDownloadSuccess = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mDownloadFail = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mDownloadProgress = new MutableLiveData<>();

    private final MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    public FilterPanelViewModel(@NonNull Application application) {
        super(application);
    }

    public void initColumns() {
        List<String> fatherColumn = new ArrayList<>();
        fatherColumn.add(HVEMaterialConstant.FILTER_FATHER_COLUMN);
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

    private void initMaterialsCutColumnResp(HVETopColumnResponse filterResp) {
        List<HVETopColumnInfo> materialsCutContents = filterResp.getColumnInfos();

        if (materialsCutContents != null && materialsCutContents.size() == 0) {
            SmartLog.i(TAG, "materialsCutContents:" + materialsCutContents.size());
            errorString.postValue(getApplication().getString(R.string.result_empty));
        }

        if (materialsCutContents == null || materialsCutContents.isEmpty()) {
            return;
        }

        for (HVETopColumnInfo filterCutColumn : materialsCutContents) {
            List<HVEColumnInfo> materialsCutContentList = filterCutColumn.getChildInfoList();
            if (materialsCutContentList.size() == 0) {
                errorString.postValue(getApplication().getString(R.string.result_empty));
                break;
            }

            if (filterCutColumn.getColumnId().equals(HVEMaterialConstant.FILTER_FATHER_COLUMN)
                && filterCutColumn.getChildInfoList().size() > 0) {
                mFilterColumns.postValue(filterCutColumn.getChildInfoList());
                break;
            }
        }
    }

    public void loadMaterials(String columnId, Integer page) {
        if (columnId.equals("-1")) {
            return;
        }

        HVEChildColumnRequest request = new HVEChildColumnRequest(columnId, page * PAGE_SIZE, PAGE_SIZE, false);

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
                errorString.postValue(getApplication().getString(R.string.result_illegal));
                String b = e.getMessage();
                SmartLog.e(TAG, b);
            }
        });
    }

    private void initMaterialsCutContentResp(HVEChildColumnResponse filterResp) {
        List<HVEMaterialInfo> materialsCutContents = filterResp.getMaterialInfoList();

        boundaryPageData.postValue(filterResp.isHasMoreItem());
        if (materialsCutContents.size() > 0) {
            SmartLog.i(TAG, "hasDownload:" + materialsCutContents.toString());
            queryDownloadStatus(materialsCutContents);
        } else {
            errorString.postValue(getApplication().getString(R.string.result_empty));
        }
    }

    private void queryDownloadStatus(List<HVEMaterialInfo> materialsCutContents) {
        List<CloudMaterialBean> list = new ArrayList<>();
        for (int i = 0; i < materialsCutContents.size(); i++) {
            CloudMaterialBean materialInfo = new CloudMaterialBean();
            HVEMaterialInfo hveMaterialInfo = materialsCutContents.get(i);
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

        mFilterMaterials.postValue(list);
    }

    public void downloadColumn(int previousPosition, int position, int dataPosition, CloudMaterialBean cutContent) {
        MaterialsDownloadInfo downloadFilterInfo = new MaterialsDownloadInfo();
        downloadFilterInfo.setPreviousPosition(previousPosition);
        downloadFilterInfo.setPosition(position);
        downloadFilterInfo.setDataPosition(dataPosition);
        downloadFilterInfo.setContentId(cutContent.getId());
        downloadFilterInfo.setMaterialBean(cutContent);

        HVEDownloadMaterialRequest request = new HVEDownloadMaterialRequest(cutContent.getId());
        HVEMaterialsManager.downloadMaterialById(request, new HVEDownloadMaterialListener() {
            @Override
            public void onSuccess(String file) {
                SmartLog.i(TAG, "onDecompressionSuccess" + file);
                downloadFilterInfo.setMaterialLocalPath(file);
                mDownloadSuccess.postValue(downloadFilterInfo);
            }

            @Override
            public void onProgress(int progress) {
                downloadFilterInfo.setProgress(progress);
                mDownloadProgress.postValue(downloadFilterInfo);
            }

            @Override
            public void onFailed(Exception exception) {
                SmartLog.i(TAG, exception.getMessage());
                downloadFilterInfo.setMaterialLocalPath("");
                mDownloadFail.postValue(downloadFilterInfo);
            }

            @Override
            public void onAlreadyDownload(String file) {
                downloadFilterInfo.setMaterialLocalPath(file);
                mDownloadSuccess.postValue(downloadFilterInfo);
                SmartLog.i(TAG, "onDownloadExists");
            }
        });
    }

    public MutableLiveData<String> getErrorString() {
        return errorString;
    }

    public MutableLiveData<List<CloudMaterialBean>> getPageData() {
        return mFilterMaterials;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadSuccess() {
        return mDownloadSuccess;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadFail() {
        return mDownloadFail;
    }

    public MutableLiveData<List<HVEColumnInfo>> getColumns() {
        return mFilterColumns;
    }

    public MutableLiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadProgress() {
        return mDownloadProgress;
    }

    public static final int FILTER_CHANGE = 0;

    public static final int FILTER_LAST = 1;

    public void initFilterData(Map<Integer, FilterData> filterDataMap, EditPreviewViewModel mEditPreviewViewModel,
        boolean isAsset, HVEVisibleAsset visibleAsset) {
        if (!isAsset) {
            HVEEffect effect = mEditPreviewViewModel.getSelectedEffect();
            if (effect == null) {
                return;
            }
            FilterData filterData = new FilterData(effect.getOptions().getEffectName(),
                effect.getOptions().getEffectPath(), effect.getOptions().getEffectId(), effect, effect.getStartTime(),
                effect.getEndTime(), effect.getFloatVal(HVEEffect.FILTER_STRENTH_KEY));
            filterDataMap.put(FILTER_CHANGE, filterData);
            filterDataMap.put(FILTER_LAST, filterData);
        } else {
            if (visibleAsset == null) {
                return;
            }
            List<HVEEffect> hveEffects = visibleAsset.getEffectsWithType(FILTER);
            if (hveEffects.isEmpty()) {
                return;
            }
            for (HVEEffect effect : hveEffects) {
                FilterData filterData = new FilterData(effect.getOptions().getEffectName(),
                    effect.getOptions().getEffectPath(), effect.getOptions().getEffectId(), effect,
                    effect.getStartTime(), effect.getEndTime(), effect.getFloatVal(HVEEffect.FILTER_STRENTH_KEY));
                filterDataMap.put(FILTER_CHANGE, filterData);
                filterDataMap.put(FILTER_LAST, filterData);
            }
        }
    }

    public void deleteFilterEffect(EditPreviewViewModel mEditPreviewViewModel, HuaweiVideoEditor mEditor,
        HVEVisibleAsset visibleAsset, HVEEffect effect, boolean isAsset) {
        if (isAsset) {
            deleteEffectInLan(visibleAsset, effect);
        } else {
            deleteEffectOnLan(mEditPreviewViewModel, mEditor, effect);
        }
    }

    private void deleteEffectInLan(HVEVisibleAsset visibleAsset, HVEEffect effect) {
        if (visibleAsset == null || effect == null) {
            return;
        }
        visibleAsset.removeEffect(effect.getIndex());
    }

    private void deleteEffectOnLan(EditPreviewViewModel mEditPreviewViewModel, HuaweiVideoEditor mEditor,
        HVEEffect effect) {
        HVETimeLine mTimeLine = mEditPreviewViewModel.getTimeLine();
        if (mEditor == null || mTimeLine == null || effect == null) {
            return;
        }
        HVEEffectLane lane = mTimeLine.getEffectLane(effect.getLaneIndex());
        if (lane == null) {
            return;
        }
        lane.removeEffect(effect.getIndex());
        mEditor.seekTimeLine(mEditPreviewViewModel.getSeekTime());
    }

    public HVEEffect disPlayFilter(EditPreviewViewModel mEditPreviewViewModel, HuaweiVideoEditor mEditor,
        HVEVisibleAsset visibleAsset, String name, String path, String id, float strenth, long startTime, long endTime,
        boolean isAllplay, boolean isAsset) {

        HVEVideoLane videoLane = mEditPreviewViewModel.getVideoLane();
        if (mEditor == null || videoLane == null) {
            return null;
        }
        HVEEffect effect = null;
        if (isAsset) {
            if (visibleAsset == null) {
                return null;
            }
            effect = visibleAsset.appendEffectUniqueOfType(new HVEEffect.Options(name, id, path), FILTER);
        } else {
            if (!isAllplay) {
                if (startTime == 0 && endTime == 0) {
                    effect = LaneSizeCheckUtils
                        .getFilterFreeLan(mEditor, mEditPreviewViewModel.getSeekTime(),
                            Math.min(mEditPreviewViewModel.getSeekTime() + 3000, videoLane.getEndTime()))
                        .appendEffect(new HVEEffect.Options(name, id, path), mEditPreviewViewModel.getSeekTime(), 3000);

                    effect.setEndTime(Math.min(mEditPreviewViewModel.getSeekTime() + 3000, videoLane.getEndTime()));

                } else {
                    effect = LaneSizeCheckUtils.getFilterFreeLan(mEditor, startTime, endTime)
                        .appendEffect(new HVEEffect.Options(name, id, path), startTime, endTime - startTime);

                }
            } else {
                effect = LaneSizeCheckUtils.getFilterFreeLan(mEditor, videoLane.getStartTime(), videoLane.getEndTime())
                    .appendEffect(new HVEEffect.Options(name, id, path), videoLane.getStartTime(),
                        videoLane.getEndTime() - videoLane.getStartTime());
            }
        }
        if (effect == null) {
            return null;
        }

        effect.setFloatVal(HVEEffect.FILTER_STRENTH_KEY, strenth);
        if (!isAsset) {
            mEditPreviewViewModel.setSelectedUUID(effect.getUuid());
        }

        mEditor.seekTimeLine(mEditPreviewViewModel.getSeekTime());

        return effect;
    }

    public MutableLiveData<String> getEmptyString() {
        return emptyString;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public HVEEffect replaceFilter(EditPreviewViewModel editPreviewViewModel, HVEEffect lastEffect, String name,
        String path, String id, float strength) {
        if (editPreviewViewModel == null) {
            return null;
        }
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (editor == null || timeLine == null) {
            return null;
        }

        int lastEffectIndex = lastEffect.getIndex();
        int lastEffectLaneIndex = lastEffect.getLaneIndex();

        if (lastEffectIndex < 0 || lastEffectLaneIndex < 0) {
            return null;
        }

        HVEEffectLane effectLane = timeLine.getEffectLane(lastEffectLaneIndex);
        if (effectLane == null) {
            return null;
        }

        long lastStartTime = lastEffect.getStartTime();
        long lastEndTime = lastEffect.getEndTime();

        lastEffect = effectLane.replaceEffect(new HVEEffect.Options(name, id, path), lastEffectIndex, lastStartTime,
            lastEndTime - lastStartTime);
        if (lastEffect == null) {
            return null;
        }
        lastEffect.setFloatVal(HVEEffect.FILTER_STRENTH_KEY, strength);

        editPreviewViewModel.setSelectedUUID(lastEffect.getUuid());

        editor.seekTimeLine(timeLine.getCurrentTime());
        return lastEffect;
    }
}
