
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

package com.huawei.hms.videoeditor.ui.common.utils;

import java.util.List;

import android.content.Context;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEPosition2D;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.lane.HVEAudioLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEEffectLane;
import com.huawei.hms.videoeditor.sdk.lane.HVELane;
import com.huawei.hms.videoeditor.sdk.lane.HVEStickerLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.KeepOriginal;

@KeepOriginal
public class LaneSizeCheckUtils {

    private static final int HIGH_MEMORY_MAX_PIP_SIZE = 7;

    private static final int LOW_MEMORY_MAX_PIP_SIZE = 4;

    private static final int HIGH_MEMORY_MAX_AUDIO_SIZE = 6;

    private static final int LOW_MEMORY_MAX_AUDIO_SIZE = 3;

    public static HVEAudioLane getAudioFreeLan(HuaweiVideoEditor editor, long start, long end, Context context) {
        if (editor == null || editor.getTimeLine() == null || context == null) {
            return null;
        }

        long endTime = start + 100;

        List<HVEAudioLane> lanes = editor.getTimeLine().getAllAudioLane();
        for (int i = 0; i < lanes.size(); i++) {
            boolean isCan = true;
            HVEAudioLane lane = lanes.get(i);
            List<HVEAsset> assets = lane.getAssets();
            if (assets.isEmpty()) {
                return lane;
            }
            for (HVEAsset asset : assets) {
                if (start <= asset.getStartTime() && endTime >= asset.getEndTime()
                    || (start >= asset.getStartTime() && start < asset.getEndTime())
                    || (endTime > asset.getStartTime() && endTime <= asset.getEndTime())) {
                    isCan = false;
                    break;
                }
            }
            if (isCan) {
                return lane;
            }
        }
        if (!isAudioLaneOutOfSize(editor, context)) {
            return null;
        }
        return editor.getTimeLine().appendAudioLane();
    }

    public static HVEVideoLane getPipFreeLan(HuaweiVideoEditor editor, long start, long end, Context context) {
        if (editor == null || editor.getTimeLine() == null || context == null) {
            return null;
        }
        List<HVEVideoLane> lanes = editor.getTimeLine().getAllVideoLane();
        if (lanes.size() > 1) {
            for (int i = 1; i < lanes.size(); i++) {
                HVEVideoLane lane = lanes.get(i);
                boolean isCan = true;
                List<HVEAsset> assets = lane.getAssets();
                for (HVEAsset asset : assets) {
                    if (start <= asset.getStartTime() && end >= asset.getEndTime()
                        || (start >= asset.getStartTime() && start < asset.getEndTime())
                        || (end > asset.getStartTime() && end <= asset.getEndTime())) {
                        isCan = false;
                        break;
                    }
                }
                if (isCan) {
                    return lane;
                }
            }
        }
        if (!isPipLaneOutOfSize(editor, context)) {
            return null;
        }
        return editor.getTimeLine().appendVideoLane();
    }

    public static HVEStickerLane getStickerFreeLan(HuaweiVideoEditor editor, long start, long end) {
        if (editor == null || editor.getTimeLine() == null) {
            return null;
        }

        List<HVEStickerLane> lanes = editor.getTimeLine().getAllStickerLane();
        for (HVEStickerLane lane : lanes) {
            boolean isCan = true;
            List<HVEAsset> assets = lane.getAssets();
            for (HVEAsset asset : assets) {
                if (start <= asset.getStartTime() && end >= asset.getEndTime()
                    || (start >= asset.getStartTime() && start < asset.getEndTime())
                    || (end > asset.getStartTime() && end <= asset.getEndTime())) {
                    isCan = false;
                    break;
                }
            }
            if (isCan) {
                return lane;
            }
        }
        return editor.getTimeLine().appendStickerLane();
    }

    public static HVEEffectLane getEffectFreeLan(HuaweiVideoEditor editor, long start, long end) {
        if (editor == null || editor.getTimeLine() == null) {
            return null;
        }

        List<HVEEffectLane> lanes = editor.getTimeLine().getAllEffectLane();
        for (HVEEffectLane lane : lanes) {
            boolean isCan = true;
            List<HVEEffect> effects = lane.getEffects();
            for (HVEEffect effect : effects) {
                if (start <= effect.getStartTime() && end >= effect.getEndTime()
                    || (start >= effect.getStartTime() && start < effect.getEndTime())
                    || (end > effect.getStartTime() && end <= effect.getEndTime())) {
                    isCan = false;
                    break;
                }
            }
            if (isCan) {
                return lane;
            }
        }
        return editor.getTimeLine().appendEffectLane();
    }

    public static HVEEffectLane getSpecialFreeLan(HuaweiVideoEditor editor, long start, long end) {
        return getEffectFreeLan(editor, start, end);
    }

    public static HVEEffectLane getFilterFreeLan(HuaweiVideoEditor editor, long start, long end) {
        return getEffectFreeLan(editor, start, end);
    }

    public static boolean isCanAddPip(HuaweiVideoEditor editor, Context context) {
        if (editor == null || editor.getTimeLine() == null) {
            return false;
        }

        List<HVEVideoLane> lanes = editor.getTimeLine().getAllVideoLane();
        long start = editor.getTimeLine().getCurrentTime();
        long end = start + 100;
        if (lanes.size() > 1) {
            for (int i = 1; i < lanes.size(); i++) {
                HVEVideoLane lane = lanes.get(i);
                boolean isCan = true;
                List<HVEAsset> assets = lane.getAssets();
                for (HVEAsset asset : assets) {
                    if (start <= asset.getStartTime() && end >= asset.getEndTime()
                        || (start >= asset.getStartTime() && start < asset.getEndTime())
                        || (end > asset.getStartTime() && end <= asset.getEndTime())) {
                        isCan = false;
                        break;
                    }
                }
                if (isCan) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return isPipLaneOutOfSize(editor, context);
    }

    public static boolean isCanAddAudio(HuaweiVideoEditor editor, Context context) {
        if (editor == null || editor.getTimeLine() == null || context == null) {
            return false;
        }
        List<HVEAudioLane> lanes = editor.getTimeLine().getAllAudioLane();
        long start = editor.getTimeLine().getCurrentTime();
        long end = start + 100;

        if (lanes.size() > 1) {
            for (int i = 0; i < lanes.size(); i++) {
                HVEAudioLane lane = lanes.get(i);
                boolean isCan = true;
                if (lane == null) {
                    return true;
                }
                List<HVEAsset> assets = lane.getAssets();
                if (assets.isEmpty()) {
                    return true;
                }
                for (HVEAsset asset : assets) {
                    if (start <= asset.getStartTime() && end >= asset.getEndTime()
                        || (start >= asset.getStartTime() && start < asset.getEndTime())
                        || (end > asset.getStartTime() && end <= asset.getEndTime())) {
                        isCan = false;
                        break;
                    }
                }
                if (isCan) {
                    return true;
                }
            }
        } else {
            return true;
        }
        if (!isAudioLaneOutOfSize(editor, context)) {
            return false;
        }
        return true;
    }

    public static HVEAsset getHVEAsset(HuaweiVideoEditor editor, HVEPosition2D position, long currentTime,
        HVELane.HVELaneType laneType) {
        if (editor == null || editor.getTimeLine() == null || position == null) {
            return null;
        }

        HVEAsset asset = editor.getTimeLine().getRectByPosition(position, currentTime);

        if (asset != null) {
            return asset;
        }
        return null;

    }

    public static boolean isPipLaneOutOfSize(HuaweiVideoEditor editor, Context context) {
        int maxSize;
        if (context == null) {
            return false;
        }

        if (MemoryInfoUtil.isLowMemoryDevice(context)) {
            maxSize = LOW_MEMORY_MAX_PIP_SIZE;
        } else {
            maxSize = HIGH_MEMORY_MAX_PIP_SIZE;
        }

        if (editor.getTimeLine() == null || editor.getTimeLine().getAllVideoLane().size() >= maxSize) {
            return false;
        }
        return true;
    }

    public static boolean isAudioLaneOutOfSize(HuaweiVideoEditor editor, Context context) {
        int maxSize;
        if (context == null) {
            return false;
        }
        if (MemoryInfoUtil.isLowMemoryDevice(context)) {
            maxSize = LOW_MEMORY_MAX_AUDIO_SIZE;
        } else {
            maxSize = HIGH_MEMORY_MAX_AUDIO_SIZE;
        }
        if (editor == null || editor.getTimeLine() == null
            || editor.getTimeLine().getAllAudioLane().size() >= maxSize) {
            return false;
        }
        return true;
    }
}
