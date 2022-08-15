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

package com.huawei.hms.videoeditor.ui.mediaeditor.texts.repository;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.ui.common.EditorManager;

public class TextAnimationRepository {
    public static HVEEffect appendEnterAnimation(HVEAsset hveAsset, HVEEffect.Options options, long duration) {
        if (!(hveAsset instanceof HVEVisibleAsset)) {
            return null;
        }
        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) hveAsset;
        return visibleAsset.appendEnterAnimationEffect(options, duration);
    }

    public static HVEEffect appendLeaveAnimation(HVEAsset hveAsset, HVEEffect.Options options, long duration) {
        if (!(hveAsset instanceof HVEVisibleAsset)) {
            return null;
        }
        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) hveAsset;
        return visibleAsset.appendLeaveAnimationEffect(options, duration);
    }

    public static HVEEffect appendCycleAnimation(HVEAsset hveAsset, HVEEffect.Options options, long duration) {
        if (!(hveAsset instanceof HVEVisibleAsset)) {
            return null;
        }
        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) hveAsset;
        return visibleAsset.appendCycleAnimationEffect(options, duration);
    }

    public static boolean removeEnterAnimation(HVEAsset hveAsset) {
        if (!(hveAsset instanceof HVEVisibleAsset)) {
            return false;
        }
        HuaweiVideoEditor huaweiVideoEditor = EditorManager.getInstance().getEditor();
        HVETimeLine hveTimeLine = EditorManager.getInstance().getTimeLine();
        if (huaweiVideoEditor == null || hveTimeLine == null) {
            return false;
        }
        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) hveAsset;
        huaweiVideoEditor.seekTimeLine(hveTimeLine.getCurrentTime());
        return visibleAsset.removeEnterAnimationEffect();
    }

    public static boolean removeLeaveAnimation(HVEAsset hveAsset) {
        if (!(hveAsset instanceof HVEVisibleAsset)) {
            return false;
        }
        HuaweiVideoEditor huaweiVideoEditor = EditorManager.getInstance().getEditor();
        HVETimeLine hveTimeLine = EditorManager.getInstance().getTimeLine();
        if (huaweiVideoEditor == null || hveTimeLine == null) {
            return false;
        }
        HVEVisibleAsset hveVisibleAsset = (HVEVisibleAsset) hveAsset;
        huaweiVideoEditor.seekTimeLine(hveTimeLine.getCurrentTime());
        return hveVisibleAsset.removeLeaveAnimationEffect();
    }

    public static boolean removeCycleAnimation(HVEAsset asset) {
        if (!(asset instanceof HVEVisibleAsset)) {
            return false;
        }
        HuaweiVideoEditor huaweiVideoEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (huaweiVideoEditor == null || timeLine == null) {
            return false;
        }
        HVEVisibleAsset hveVisibleAsset = (HVEVisibleAsset) asset;
        huaweiVideoEditor.seekTimeLine(timeLine.getCurrentTime());
        return hveVisibleAsset.removeCycleAnimationEffect();
    }

    public static boolean setEnterAnimationDuration(HVEAsset asset, long duration) {
        if (!(asset instanceof HVEVisibleAsset)) {
            return false;
        }
        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) asset;
        return visibleAsset.setEnterAnimationDuration(duration);
    }

    public static boolean setLeaveAnimationDuration(HVEAsset asset, long duration) {
        if (!(asset instanceof HVEVisibleAsset)) {
            return false;
        }
        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) asset;
        return visibleAsset.setLeaveAnimationDuration(duration);
    }

    public static boolean setCycleAnimationDuration(HVEAsset asset, long duration) {
        if (!(asset instanceof HVEVisibleAsset)) {
            return false;
        }
        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) asset;
        return visibleAsset.setCycleAnimationDuration(duration);
    }

    public static HVEEffect getEnterAnimation(HVEAsset asset) {
        if (!(asset instanceof HVEVisibleAsset)) {
            return null;
        }
        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) asset;
        return visibleAsset.getEnterAnimation();
    }

    public static HVEEffect getLeaveAnimation(HVEAsset asset) {
        if (!(asset instanceof HVEVisibleAsset)) {
            return null;
        }
        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) asset;
        return visibleAsset.getLeaveAnimation();
    }

    public static HVEEffect getCycleAnimation(HVEAsset asset) {
        if (!(asset instanceof HVEVisibleAsset)) {
            return null;
        }
        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) asset;
        return visibleAsset.getCycleAnimation();
    }
}
