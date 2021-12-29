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

package com.huawei.hms.videoeditor.ui.mediaeditor.preview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;

import com.huawei.hms.videoeditor.common.network.http.ability.util.array.ArrayUtils;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.sdk.materials.network.response.MaterialsCloudBean;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.MaskShape;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MaskEffectViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> isShow = new MutableLiveData<>(false);

    private MutableLiveData<Class<? extends MaskShape>> shapeClass = new MutableLiveData<>();

    private MutableLiveData<HVEAsset> hveVideoAsset = new MutableLiveData<>();

    private MutableLiveData<MaterialsCloudBean> materialsCutContentMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> isInverse = new MutableLiveData<>(false);

    private Map<HVEAsset, Map<String, HVEEffect>> effectMap = new HashMap<>();

    private HVEEffect firstEffect = null;

    public static final String LINE = "LINE";

    public static final String CYCLE = "CYCLE";

    public static final String MASK = "MASK";

    public static final String PARALLEL = "PARALLEL";

    public static final String ROUNDRECT = "ROUNDRECT";

    public static final String STAR = "STAR";

    public static final String HEART = "HEART";

    public static final String FLOWER = "FLOWER";

    public HVEEffect getFirstEffect() {
        return firstEffect;
    }

    public void setFirstEffect(HVEEffect hveEffect) {
        firstEffect = hveEffect;
    }

    public HVEEffect appendHVEEffect(HVEAsset assetKey, MaterialsCloudBean materialsCutContent) {
        Map<String, HVEEffect> map = effectMap.get(assetKey);
        if (map == null) {
            effectMap.put(assetKey, new HashMap<>());
        }

        if (!(assetKey instanceof HVEVisibleAsset)) {
            return null;
        }

        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) assetKey;
        Map<String, HVEEffect> effectData = effectMap.get(assetKey);
        if (effectData == null) {
            return null;
        }

        HVEEffect hveEffect = effectData.get(materialsCutContent.getId());

        if (hveEffect == null) {
            HVEEffect.Options options = null;
            options = new HVEEffect.Options(materialsCutContent.getName(), materialsCutContent.getId(),
                materialsCutContent.getLocalPath());
            hveEffect = visibleAsset.appendEffectUniqueOfType(options, HVEEffect.HVEEffectType.MASK);
            if (hveEffect != null) {
                effectData.put(materialsCutContent.getName(), hveEffect);
            }
        } else {
            visibleAsset.appendEffectUniqueOfType(hveEffect, HVEEffect.HVEEffectType.MASK);
        }
        return hveEffect;
    }

    public MaskEffectViewModel(@NonNull Application application) {
        super(application);
    }

    public void getMainAsset() {
        if (EditorManager.getInstance().getEditor() == null || EditorManager.getInstance().getTimeLine() == null) {
            return;
        }

        List<HVEAsset> assets = EditorManager.getInstance().getTimeLine().getVideoLane(0).getAssets();
        if (!assets.isEmpty()) {
            for (int i = 0; i < assets.size(); i++) {
                HVEAsset asset = assets.get(i);
                long nowTime = EditorManager.getInstance().getTimeLine().getCurrentTime();
                if (nowTime >= asset.getStartTime() && nowTime < asset.getEndTime()) {
                    hveVideoAsset.setValue(asset);
                    break;
                }
            }
        }
    }

    public MutableLiveData<MaterialsCloudBean> getMaterialsCutContentMutableLiveData() {
        return materialsCutContentMutableLiveData;
    }

    public void setMaterialsCutContentMutableLiveData(MaterialsCloudBean materialsCutContentMutableLiveData) {
        this.materialsCutContentMutableLiveData.postValue(materialsCutContentMutableLiveData);
    }

    public MutableLiveData<Class<? extends MaskShape>> getShapeClass() {
        return shapeClass;
    }

    public void setShapeClass(Class shapeClass) {
        this.shapeClass.postValue(shapeClass);
    }

    public void refresh() {
        if (EditorManager.getInstance().getEditor() != null && EditorManager.getInstance().getTimeLine() != null) {
            EditorManager.getInstance()
                .getEditor()
                .seekTimeLine(EditorManager.getInstance().getTimeLine().getCurrentTime());
        }
    }

    public MutableLiveData<HVEAsset> getHveVideoAsset() {
        return hveVideoAsset;
    }

    public void setHveVideoAsset(HVEAsset hveVideoAsset) {
        this.hveVideoAsset.setValue(hveVideoAsset);
    }

    public void removeCurrentEffect() {
        HVEAsset videoAsset = hveVideoAsset.getValue();
        if (videoAsset == null) {
            return;
        }

        List<HVEEffect> effects = videoAsset.getEffects();
        if (ArrayUtils.isEmpty(effects)) {
            return;
        }
        for (HVEEffect effect : effects) {
            if (effect.getEffectType() == HVEEffect.HVEEffectType.MASK) {
                videoAsset.removeEffect(effect.getIndex());
            }
        }
    }

    public MutableLiveData<Boolean> getIsShow() {
        return isShow;
    }

    public void setIsShow(Boolean isShow) {
        this.isShow.postValue(isShow);
    }

    public MutableLiveData<Boolean> getIsInverse() {
        return isInverse;
    }

    public void setIsInverse(boolean isInverse) {
        this.isInverse.postValue(isInverse);
    }

}
