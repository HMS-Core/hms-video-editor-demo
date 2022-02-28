
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

package com.huawei.hms.videoeditor.ui.mediaeditor.effect.repository;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.lane.HVEEffectLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.utils.LaneSizeCheckUtils;

public class EffectRepository {
    public static HVEEffect addEffect(CloudMaterialBean content, long startTime) {
        if (content == null) {
            return null;
        }
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        if (editor == null || videoLane == null) {
            return null;
        }
        HVEEffectLane effectLane = LaneSizeCheckUtils.getSpecialFreeLan(editor, startTime, startTime + 3000);
        if (effectLane == null) {
            return null;
        }
        HVEEffect effect = effectLane.appendEffect(
            new HVEEffect.Options(content.getName(), content.getId(), content.getLocalPath()), startTime, 3000);
        if (effect == null) {
            return null;
        }
        effect.setEndTime(Math.min(videoLane.getEndTime(), startTime + 3000));
        return effect;
    }

    public static boolean deleteEffect(HVEEffect effect) {
        if (effect == null) {
            return false;
        }
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (timeLine == null || editor == null) {
            return false;
        }
        HVEEffectLane lane = timeLine.getEffectLane(effect.getLaneIndex());
        if (lane == null) {
            return false;
        }
        boolean isDelete = lane.removeEffect(effect.getIndex());
        editor.seekTimeLine(timeLine.getCurrentTime());
        return isDelete;
    }

    public static HVEEffect replaceEffect(HVEEffect lastEffect, CloudMaterialBean cutContent) {
        if (cutContent == null || lastEffect == null) {
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

        lastEffect = effectLane.replaceEffect(
            new HVEEffect.Options(cutContent.getName(), cutContent.getId(), cutContent.getLocalPath()), lastEffectIndex,
            lastStartTime, lastEndTime - lastStartTime);

        return lastEffect;
    }
}
