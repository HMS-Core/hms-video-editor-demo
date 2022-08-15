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

package com.huawei.hms.videoeditor.ui.mediaeditor.animation.videoanimation.viewmodel;

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
import com.huawei.hms.videoeditor.ui.mediaeditor.animation.videoanimation.repository.AnimationRepository;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.ColumnsListener;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.ColumnsRespository;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.LoadUrlEvent;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.MaterialsListener;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.MaterialsRespository;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.ArrayList;
import java.util.List;

public class AnimationViewModel extends AndroidViewModel {
    private static final String TAG = "AnimationViewModel";

    private ColumnsRespository mColumnsRespository;

    private MaterialsRespository mMaterialsRespository;

    private final MutableLiveData<List<CloudMaterialBean>> pageData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    private MutableLiveData<List<HVEColumnInfo>> columns = new MutableLiveData<>();

    private final MutableLiveData<LoadUrlEvent> loadUrlEvent = new MutableLiveData<>();

    private MutableLiveData<Integer> errorType = new MutableLiveData<>();

    private MutableLiveData<CloudMaterialBean> selectData = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> downloadInfo = new MutableLiveData<>();

    public AnimationViewModel(@NonNull Application application) {
        super(application);
        mColumnsRespository = new ColumnsRespository();
        mMaterialsRespository = new MaterialsRespository(application);
        mColumnsRespository.seatColumnsListener(columnsListener);
        mMaterialsRespository.setMaterialsListener(materialsListener);
    }
    public MutableLiveData<Integer> getErrorType() {
        return errorType;
    }

    public MutableLiveData<List<CloudMaterialBean>> getPageData() {
        return pageData;
    }

    public MutableLiveData<List<HVEColumnInfo>> getColumns() {
        return columns;
    }

    public LiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    public MutableLiveData<CloudMaterialBean> getSelectData() {
        return selectData;
    }
    public MutableLiveData<MaterialsDownloadInfo> getDownloadInfo() {
        return downloadInfo;
    }

    public MutableLiveData<LoadUrlEvent> getLoadUrlEvent() {
        return loadUrlEvent;
    }



    public List<CloudMaterialBean> loadLocalData(String name) {
        CloudMaterialBean animationNothing = new CloudMaterialBean();
        animationNothing.setName(name);
        animationNothing.setLocalDrawableId(R.drawable.icon_no);
        animationNothing.setId("-1");
        List<CloudMaterialBean> list = new ArrayList<>();
        list.add(animationNothing);
        return list;
    }
    public void setSelectCutContent(CloudMaterialBean mCutContent) {
        selectData.postValue(mCutContent);
    }

    public void loadMaterials(HVEColumnInfo cutContent, Integer page) {
        if (mMaterialsRespository == null || cutContent == null) {
            return;
        }
        mMaterialsRespository.loadMaterials(cutContent.getColumnId(), page);
    }
    public void initColumns(String type) {
        mColumnsRespository.initColumns(type);
    }


    public HVEEffect getEnterAnimation(HVEAsset asset) {
        if (asset == null) {
            return null;
        }
        return AnimationRepository.getEnterAnimation(asset);
    }

    public void downloadMaterials(int previousPosition, int position, CloudMaterialBean cutContent) {
        if (mMaterialsRespository == null || cutContent == null) {
            return;
        }
        mMaterialsRespository.downloadMaterials(previousPosition, position, cutContent);
    }

    public HVEEffect getCycleAnimation(HVEAsset asset) {
        if (asset == null) {
            return null;
        }
        return AnimationRepository.getCycleAnimation(asset);
    }
    public HVEEffect getLeaveAnimation(HVEAsset asset) {
        if (asset == null) {
            return null;
        }
        return AnimationRepository.getLeaveAnimation(asset);
    }



    public HVEEffect appendAnimation(HVEAsset asset, CloudMaterialBean materialBean, long duration, String type) {
        HVEEffect animationEffect = null;
        if (asset == null) {
            return animationEffect;
        }

        if (materialBean == null) {
            return animationEffect;
        }
        switch (type) {
            case HVEEffect.ENTER_ANIMATION:
                animationEffect = AnimationRepository.appendEnterAnimation(asset,
                    new HVEEffect.Options(materialBean.getName(), materialBean.getId(), materialBean.getLocalPath()), duration);
                break;
            case HVEEffect.LEAVE_ANIMATION:
                animationEffect = AnimationRepository.appendLeaveAnimation(asset,
                    new HVEEffect.Options(materialBean.getName(), materialBean.getId(), materialBean.getLocalPath()), duration);
                break;
            case HVEEffect.CYCLE_ANIMATION:
                animationEffect = AnimationRepository.appendCycleAnimation(asset,
                    new HVEEffect.Options(materialBean.getName(), materialBean.getId(), materialBean.getLocalPath()), duration);
                break;
            default:
                animationEffect = null;
                break;
        }
        return animationEffect;
    }

    public boolean removeAnimation(HVEAsset asset, String type) {
        boolean isRemove = false;
        if (asset == null) {
            return isRemove;
        }
        switch (type) {
            case HVEEffect.ENTER_ANIMATION:
                isRemove = AnimationRepository.removeEnterAnimation(asset);
                break;
            case HVEEffect.LEAVE_ANIMATION:
                isRemove = AnimationRepository.removeLeaveAnimation(asset);
                break;
            case HVEEffect.CYCLE_ANIMATION:
                isRemove = AnimationRepository.removeCycleAnimation(asset);
                break;
            default:
                isRemove = false;
                break;
        }
        return isRemove;
    }

    public boolean setAnimationDuration(HVEAsset asset, long duration, String type) {
        boolean isSetDuration = false;
        if (asset == null) {
            return isSetDuration;
        }
        switch (type) {
            case HVEEffect.ENTER_ANIMATION:
                isSetDuration = AnimationRepository.setEnterAnimationDuration(asset, duration);
                break;
            case HVEEffect.LEAVE_ANIMATION:
                isSetDuration = AnimationRepository.setLeaveAnimationDuration(asset, duration);
                break;
            case HVEEffect.CYCLE_ANIMATION:
                isSetDuration = AnimationRepository.setCycleAnimationDuration(asset, duration);
                break;
            default:
                isSetDuration = false;
                break;
        }
        return isSetDuration;
    }

    public int getSelectedPosition(HVEAsset hveAsset, List<CloudMaterialBean> cloudMaterialBeans, String type) {
        int selectedPosition = 0;
        HVEEffect enterEffect = getEnterAnimation(hveAsset);
        HVEEffect leaveEffect = getLeaveAnimation(hveAsset);
        HVEEffect cycleEffect = getCycleAnimation(hveAsset);

        if (enterEffect != null && type.equals(HVEEffect.ENTER_ANIMATION)) {
            selectedPosition = getPosition(enterEffect, cloudMaterialBeans);
        }

        if (leaveEffect != null && type.equals(HVEEffect.LEAVE_ANIMATION)) {
            selectedPosition = getPosition(leaveEffect, cloudMaterialBeans);
        }

        if (cycleEffect != null && type.equals(HVEEffect.CYCLE_ANIMATION)) {
            selectedPosition = getPosition(cycleEffect, cloudMaterialBeans);
        }
        return selectedPosition;
    }

    private int getPosition(HVEEffect animEffect, List<CloudMaterialBean> cloudMaterialBeans) {
        int selectedPosition = 0;
        for (int i = 0; i < cloudMaterialBeans.size(); i++) {
            if (animEffect.getOptions().getEffectId().equals(cloudMaterialBeans.get(i).getId())) {
                selectedPosition = i;
                break;
            }
        }
        return selectedPosition;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mColumnsRespository = null;
        mMaterialsRespository = null;
        columnsListener = null;
        materialsListener = null;

    }
    private ColumnsListener columnsListener = new ColumnsListener() {
        @Override
        public void columsData(List<HVEColumnInfo> hveColumnInfos) {
            columns.postValue(hveColumnInfos);
        }

        @Override
        public void errorType(int type) {
            errorType.postValue(type);
        }
    };

    private MaterialsListener materialsListener = new MaterialsListener() {
        @Override
        public void pageData(List<CloudMaterialBean> cloudMaterialBeans) {
            pageData.postValue(cloudMaterialBeans);
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
        public void downloadInfo(MaterialsDownloadInfo info) {
            downloadInfo.postValue(info);
        }

        @Override
        public void loadUrlEvent(LoadUrlEvent urlEvent) {
            loadUrlEvent.postValue(urlEvent);
        }
    };
}
