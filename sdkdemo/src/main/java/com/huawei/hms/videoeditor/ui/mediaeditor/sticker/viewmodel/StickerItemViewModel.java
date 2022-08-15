
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

package com.huawei.hms.videoeditor.ui.mediaeditor.sticker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.LoadUrlEvent;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.MaterialsListener;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.MaterialsRespository;

import java.util.List;

public class StickerItemViewModel extends AndroidViewModel {

    private final MutableLiveData<List<CloudMaterialBean>> pageData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    private final MutableLiveData<Integer> errorType = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> downloadInfo = new MutableLiveData<>();

    private final MutableLiveData<LoadUrlEvent> loadUrlEvent = new MutableLiveData<>();

    private MaterialsRespository materialsRespository;

    public StickerItemViewModel(@NonNull Application application) {
        super(application);
        materialsRespository = new MaterialsRespository(application);
        materialsRespository.setMaterialsListener(listener);
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

    public MutableLiveData<LoadUrlEvent> getLoadUrlEvent() {
        return loadUrlEvent;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadInfo() {
        return downloadInfo;
    }

    public void downloadMaterials(int previousPosition, int position, CloudMaterialBean cutContent) {
        if (materialsRespository == null || cutContent == null) {
            return;
        }
        materialsRespository.downloadMaterials(previousPosition, position, cutContent);
    }

    public void loadMaterials(String cutContent, Integer page) {
        if (materialsRespository == null || cutContent == null) {
            return;
        }
        materialsRespository.loadMaterials(cutContent, page);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        materialsRespository = null;
        listener = null;
    }

    private MaterialsListener listener = new MaterialsListener() {
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
        public void downloadInfo(MaterialsDownloadInfo downloadInfo1) {
            downloadInfo.postValue(downloadInfo1);
        }

        @Override
        public void loadUrlEvent(LoadUrlEvent event) {
            loadUrlEvent.postValue(event);
        }
    };
}
