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

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.ColumnsListener;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.ColumnsRespository;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.LoadUrlEvent;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.MaterialsListener;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.MaterialsRespository;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.repository.TextAnimationRepository;

import java.util.List;

public class TextAnimationViewModel extends AndroidViewModel {
    private static final String TAG = "TextAnimationViewModel";

    private ColumnsRespository columnsRespository;

    private MaterialsRespository materialsRespository;

    private MutableLiveData<List<HVEColumnInfo>> columns = new MutableLiveData<>();

    private MutableLiveData<Integer> errorType = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> downloadInfo = new MutableLiveData<>();

    private final MutableLiveData<LoadUrlEvent> loadUrlEvent = new MutableLiveData<>();

    private MutableLiveData<CloudMaterialBean> selectData = new MutableLiveData<>();

    private final MutableLiveData<List<CloudMaterialBean>> pageData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    public TextAnimationViewModel(@NonNull Application application) {
        super(application);
        columnsRespository = new ColumnsRespository();
        materialsRespository = new MaterialsRespository(application);
        columnsRespository.seatColumnsListener(columnsListener);
        materialsRespository.setMaterialsListener(materialsListener);
    }

    public LiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadInfo() {
        return downloadInfo;
    }

    public MutableLiveData<LoadUrlEvent> getLoadUrlEvent() {
        return loadUrlEvent;
    }

    public MutableLiveData<List<HVEColumnInfo>> getColumns() {
        return columns;
    }

    public MutableLiveData<Integer> getErrorType() {
        return errorType;
    }

    public MutableLiveData<List<CloudMaterialBean>> getPageData() {
        return pageData;
    }

    public MutableLiveData<CloudMaterialBean> getSelectData() {
        return selectData;
    }

    public void loadMaterials(HVEColumnInfo cutContent, Integer page) {
        if (materialsRespository == null || cutContent == null) {
            return;
        }
        materialsRespository.loadMaterials(cutContent.getColumnId(), page);
    }

    public void setSelectCutContent(CloudMaterialBean mCutContent) {
        selectData.postValue(mCutContent);
    }

    public void initColumns(String type) {
        columnsRespository.initColumns(type);
    }

    public void downloadMaterials(int previousPosition, int position, CloudMaterialBean cutContent) {
        if (materialsRespository == null || cutContent == null) {
            return;
        }
        materialsRespository.downloadMaterials(previousPosition, position, cutContent);
    }

    public HVEEffect getLeaveAnimation(HVEAsset asset) {
        if (asset == null) {
            return null;
        }
        return TextAnimationRepository.getLeaveAnimation(asset);
    }

    public HVEEffect getEnterAnimation(HVEAsset asset) {
        if (asset == null) {
            return null;
        }
        return TextAnimationRepository.getEnterAnimation(asset);
    }

    public HVEEffect getCycleAnimation(HVEAsset asset) {
        if (asset == null) {
            return null;
        }
        return TextAnimationRepository.getCycleAnimation(asset);
    }

    public HVEEffect appendAnimation(HVEAsset hveAsset, CloudMaterialBean cloudMaterialBean, long duration,
        String type) {
        HVEEffect animationEffect = null;
        if (hveAsset == null) {
            return animationEffect;
        }

        if (cloudMaterialBean == null) {
            return animationEffect;
        }
        switch (type) {
            case HVEEffect.ENTER_ANIMATION:
                animationEffect = TextAnimationRepository.appendEnterAnimation(hveAsset,
                    new HVEEffect.Options(cloudMaterialBean.getName(), cloudMaterialBean.getId(),
                        cloudMaterialBean.getLocalPath()),
                    duration);
                break;
            case HVEEffect.LEAVE_ANIMATION:
                animationEffect = TextAnimationRepository.appendLeaveAnimation(hveAsset,
                    new HVEEffect.Options(cloudMaterialBean.getName(), cloudMaterialBean.getId(),
                        cloudMaterialBean.getLocalPath()),
                    duration);
                break;
            case HVEEffect.CYCLE_ANIMATION:
                animationEffect = TextAnimationRepository.appendCycleAnimation(hveAsset,
                    new HVEEffect.Options(cloudMaterialBean.getName(), cloudMaterialBean.getId(),
                        cloudMaterialBean.getLocalPath()),
                    duration);
                break;
            default:
                animationEffect = null;
                break;
        }
        return animationEffect;
    }

    public boolean removeAnimation(HVEAsset hveAsset, String type) {
        boolean isRemove = false;
        if (hveAsset == null) {
            return isRemove;
        }
        switch (type) {
            case HVEEffect.ENTER_ANIMATION:
                isRemove = TextAnimationRepository.removeEnterAnimation(hveAsset);
                break;
            case HVEEffect.LEAVE_ANIMATION:
                isRemove = TextAnimationRepository.removeLeaveAnimation(hveAsset);
                break;
            case HVEEffect.CYCLE_ANIMATION:
                isRemove = TextAnimationRepository.removeCycleAnimation(hveAsset);
                break;
            default:
                isRemove = false;
                break;
        }
        return isRemove;
    }

    public boolean setAnimationDuration(HVEAsset hveAsset, long duration, String type) {
        boolean isSetDuration = false;
        if (hveAsset == null) {
            return isSetDuration;
        }
        switch (type) {
            case HVEEffect.ENTER_ANIMATION:
                isSetDuration = TextAnimationRepository.setEnterAnimationDuration(hveAsset, duration);
                break;
            case HVEEffect.LEAVE_ANIMATION:
                isSetDuration = TextAnimationRepository.setLeaveAnimationDuration(hveAsset, duration);
                break;
            case HVEEffect.CYCLE_ANIMATION:
                isSetDuration = TextAnimationRepository.setCycleAnimationDuration(hveAsset, duration);
                break;
            default:
                isSetDuration = false;
                break;
        }
        return isSetDuration;
    }

    public int getSelectedPosition(HVEAsset asset, List<CloudMaterialBean> animList, String type) {
        int selectedPosition = 0;
        HVEEffect enterEffect = getEnterAnimation(asset);
        HVEEffect leaveEffect = getLeaveAnimation(asset);
        HVEEffect cycleEffect = getCycleAnimation(asset);

        if (enterEffect != null && type.equals(HVEEffect.ENTER_ANIMATION)) {
            selectedPosition = getPosition(enterEffect, animList);
        }

        if (leaveEffect != null && type.equals(HVEEffect.LEAVE_ANIMATION)) {
            selectedPosition = getPosition(leaveEffect, animList);
        }

        if (cycleEffect != null && type.equals(HVEEffect.CYCLE_ANIMATION)) {
            selectedPosition = getPosition(cycleEffect, animList);
        }
        return selectedPosition;
    }

    private int getPosition(HVEEffect effect, List<CloudMaterialBean> animList) {
        int selectedPosition = 0;
        for (int i = 0; i < animList.size(); i++) {
            if (effect.getOptions().getEffectId().equals(animList.get(i).getId())) {
                selectedPosition = i;
                break;
            }
        }
        return selectedPosition;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        columnsRespository = null;
        materialsRespository = null;
        columnsListener = null;
        materialsListener = null;

    }

    private MaterialsListener materialsListener = new MaterialsListener() {
        @Override
        public void pageData(List<CloudMaterialBean> materialsCutContentList) {
            pageData.postValue(materialsCutContentList);
        }

        @Override
        public void errorType(int type) {
            errorType.postValue(type);
        }

        @Override
        public void boundaryPageData(boolean isBoundaryPageData) {
            boundaryPageData.postValue(isBoundaryPageData);
        }

        @Override
        public void downloadInfo(MaterialsDownloadInfo materialsDownloadInfo) {
            downloadInfo.postValue(materialsDownloadInfo);
        }

        @Override
        public void loadUrlEvent(LoadUrlEvent mLoadUrlEvent) {
            loadUrlEvent.postValue(mLoadUrlEvent);
        }
    };

    private ColumnsListener columnsListener = new ColumnsListener() {
        @Override
        public void columsData(List<HVEColumnInfo> materialsCutContentList) {
            columns.postValue(materialsCutContentList);
        }

        @Override
        public void errorType(int type) {
            errorType.postValue(type);
        }
    };
}
