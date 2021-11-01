
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

package com.huawei.hms.videoeditor.ui.mediaeditor.effect.viewmodel;

import java.util.List;

import android.app.Application;

import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.LoadUrlEvent;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.MaterialsListener;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.MaterialsRespository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EffectItemViewModel extends AndroidViewModel {

    private final MutableLiveData<List<CloudMaterialBean>> pageData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    private final MutableLiveData<Integer> errorType = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> downloadInfo = new MutableLiveData<>();

    private final MutableLiveData<LoadUrlEvent> loadUrlEvent = new MutableLiveData<>();

    private MaterialsRespository materialsRespository;

    public EffectItemViewModel(@NonNull Application application) {
        super(application);
        materialsRespository = new MaterialsRespository(application);
        materialsRespository.setMaterialsListener(materialsListener);
    }

    public MutableLiveData<List<CloudMaterialBean>> getPageData() {
        return pageData;
    }

    public LiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    public MutableLiveData<Integer> getErrorType() {
        return errorType;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadInfo() {
        return downloadInfo;
    }

    public MutableLiveData<LoadUrlEvent> getLoadUrlEvent() {
        return loadUrlEvent;
    }

    public void loadMaterials(String columnId, Integer page) {
        if (materialsRespository == null || columnId == null) {
            return;
        }
        materialsRespository.loadMaterials(columnId, page);
    }

    public void downloadMaterials(int previousPosition, int position, CloudMaterialBean cutContent) {
        if (materialsRespository == null || cutContent == null) {
            return;
        }
        materialsRespository.downloadMaterials(previousPosition, position, cutContent);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        materialsListener = null;
        materialsRespository = null;
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
}
