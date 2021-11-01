
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

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.mediaeditor.effect.repository.EffectRepository;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.ColumnsListener;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.ColumnsRespository;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EffectPanelViewModel extends ViewModel {

    private ColumnsRespository columnsRespository;

    private MutableLiveData<List<HVEColumnInfo>> columns = new MutableLiveData<>();

    private MutableLiveData<CloudMaterialBean> selectData = new MutableLiveData<>();

    private MutableLiveData<Boolean> removeData = new MutableLiveData<>();

    private MutableLiveData<Integer> errorType = new MutableLiveData<>();

    public MutableLiveData<Boolean> cancelSelected = new MutableLiveData<>();

    public MutableLiveData<HVEEffect> getSelectEffect() {
        return selectEffect == null ? new MediatorLiveData<>() : selectEffect;
    }

    public void setSelectEffect(HVEEffect selectEffect) {
        if (this.selectEffect == null) {
            return;
        }
        this.selectEffect.postValue(selectEffect);
    }

    private MutableLiveData<HVEEffect> selectEffect;

    public EffectPanelViewModel() {
        columnsRespository = new ColumnsRespository();
        columnsRespository.seatColumnsListener(columnsListener);
    }

    public MutableLiveData<List<HVEColumnInfo>> getColumns() {
        return columns;
    }

    public MutableLiveData<CloudMaterialBean> getSelectData() {
        return selectData;
    }

    public MutableLiveData<Boolean> getRemoveData() {
        return removeData;
    }

    public MutableLiveData<Integer> getErrorType() {
        return errorType;
    }

    public MutableLiveData<Boolean> getCancelSelected() {
        return cancelSelected;
    }

    public void setSelectCutContent(CloudMaterialBean mCutContent) {
        selectData.postValue(mCutContent);
    }

    public HVEEffect addEffect(CloudMaterialBean content, long startTime) {
        if (content == null) {
            return null;
        }
        return EffectRepository.addEffect(content, startTime);
    }

    public boolean deleteEffect(HVEEffect effect) {
        if (effect == null) {
            return false;
        }
        return EffectRepository.deleteEffect(effect);
    }

    public HVEEffect replaceEffect(HVEEffect lastEffect, CloudMaterialBean cutContent, long startTime) {
        if (cutContent == null) {
            return null;
        }
        HVEEffect effect;
        if (lastEffect == null) {
            effect = addEffect(cutContent, startTime);
            setSelectEffect(effect);
            return effect;
        }
        effect = EffectRepository.replaceEffect(lastEffect, cutContent);
        setSelectEffect(effect);
        return effect;
    }

    public void initColumns(String type) {
        columnsRespository.initColumns(type);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        columnsRespository = null;
        columnsListener = null;
    }

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
