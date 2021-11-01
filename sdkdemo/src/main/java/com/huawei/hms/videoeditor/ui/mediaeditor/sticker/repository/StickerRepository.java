
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

package com.huawei.hms.videoeditor.ui.mediaeditor.sticker.repository;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEStickerAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVEStickerLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.utils.LaneSizeCheckUtils;

public class StickerRepository {
    public static HVEStickerAsset addStickerAsset(CloudMaterialBean content, long startTime) {
        if (content == null) {
            return null;
        }
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (timeLine == null || videoLane == null || editor == null) {
            return null;
        }
        long endTime = startTime + 3000;
        HVEStickerLane stickerLane = LaneSizeCheckUtils.getStickerFreeLan(editor, startTime, endTime);
        if (stickerLane == null) {
            return null;
        }
        HVEStickerAsset asset = stickerLane.appendStickerAsset(content.getLocalPath(), startTime, endTime - startTime);
        if (asset == null) {
            return null;
        }
        return asset;
    }

    public static boolean deleteAsset(HVEAsset asset) {
        if (asset == null) {
            return false;
        }
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (editor == null || timeLine == null) {
            return false;
        }
        HVEStickerLane lane = timeLine.getStickerLane(asset.getLaneIndex());
        if (lane == null) {
            return false;
        }
        boolean isDelete = lane.removeAsset(asset.getIndex());
        editor.seekTimeLine(timeLine.getCurrentTime());
        return isDelete;
    }

    public static HVEStickerAsset replaceStickerAsset(HVEAsset asset, CloudMaterialBean content) {
        if (content == null || asset == null) {
            return null;
        }
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (videoLane == null || editor == null || timeLine == null) {
            return null;
        }

        if (!(asset instanceof HVEStickerAsset)) {
            return null;
        }
        HVEStickerAsset stickerAsset = (HVEStickerAsset) asset;

        HVEStickerLane stickerLane = timeLine.getStickerLane(stickerAsset.getLaneIndex());
        if (stickerLane == null) {
            return null;
        }
        if (!stickerLane.replaceAssetPath(content.getLocalPath(), stickerAsset.getIndex())) {
            return null;
        }

        return (HVEStickerAsset) stickerLane.getAssetByIndex(asset.getIndex());
    }
}
