
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

package com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.viewmodel;

import java.util.List;

import android.app.Application;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.materials.HVELocalMaterialInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialsManager;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.ai.HVEExclusiveFilter;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.lane.HVEEffectLane;
import com.huawei.hms.videoeditor.sdk.store.MaterialsLocalDataManager;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.sdk.materials.network.response.MaterialsCloudBean;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class ExclusiveFilterPanelViewModel extends AndroidViewModel {
    public static final String FILTER_TYPE_SINGLE = "filterSingle";

    public static final String FILTER_TYPE_CLONE = "filterClone";

    public static final int FILTER_CHANGE = 0;

    public static final int FILTER_LAST = 1;

    private MaterialsLocalDataManager mLocalDataManager;

    public ExclusiveFilterPanelViewModel(@NonNull Application application) {
        super(application);
        mLocalDataManager = new MaterialsLocalDataManager();
    }

    public List<HVELocalMaterialInfo> queryAllMaterialsByType(int type) throws ClassCastException {
        List<HVELocalMaterialInfo> cutContents = HVEMaterialsManager.queryLocalMaterialByType(type);
        return cutContents;
    }

    public void updateFilter(MaterialsCloudBean materialsCutContent) throws ClassCastException {
        HVEExclusiveFilter filter = new HVEExclusiveFilter();
        filter.updateEffectName(materialsCutContent.getId(), materialsCutContent.getName());
    }

    public void deleteFilterData(String contentId) throws ClassCastException {
        if (TextUtils.isEmpty(contentId)) {
            return;
        }

        HVEExclusiveFilter filter = new HVEExclusiveFilter();
        filter.deleteExclusiveEffect(contentId);
    }

    public void deleteFilterEffect(HVEEffect effect) {
        HVETimeLine mTimeLine = EditorManager.getInstance().getTimeLine();
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        if (mEditor == null || mTimeLine == null || effect == null) {
            return;
        }
        HVEEffectLane lane = mTimeLine.getEffectLane(effect.getLaneIndex());
        if (lane == null) {
            return;
        }
        lane.removeEffect(effect.getIndex());
        mEditor.seekTimeLine(mTimeLine.getCurrentTime());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
