/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.viewmodel;

import static com.huawei.hms.videoeditor.ui.mediaexport.model.ExportConstants.FRAME_RATE_MAX_PROGRESS;
import static com.huawei.hms.videoeditor.ui.mediaexport.model.ExportConstants.LOW_FRAME_RATE_MAX_PROGRESS;
import static com.huawei.hms.videoeditor.ui.mediaexport.model.ExportConstants.LOW_RESOLUTION_MAX_PROGRESS;
import static com.huawei.hms.videoeditor.ui.mediaexport.model.ExportConstants.MIN_RESOLUTION_MAX_PROGRESS;
import static com.huawei.hms.videoeditor.ui.mediaexport.model.ExportConstants.NOVA_RESOLUTION_MAX_PROGRESS;
import static com.huawei.hms.videoeditor.ui.mediaexport.model.ExportConstants.RESOLUTION_MAX_PROGRESS;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.DeviceProfile;
import com.huawei.hms.videoeditor.sdk.util.HVEUtil;
import com.huawei.hms.videoeditor.ui.common.utils.MemoryInfoUtil;
import com.huawei.hms.videoeditor.ui.mediaexport.utils.InfoStateUtil;
import com.huawei.hms.videoeditor.ui.mediaexport.utils.SystemUtils;
import com.huawei.hms.videoeditor.utils.SmartLog;

public class VerificationViewModel extends AndroidViewModel {
    private static final String TAG = "VerificationViewModel";

    private HuaweiVideoEditor editor;

    private boolean isLowMemory = MemoryInfoUtil.isLowMemoryDevice();

    // is nova9
    private boolean isNova9 = false;

    private int originWidth = 0;

    private int originHeight = 0;

    private int originFrameRate = 0;

    public VerificationViewModel(@NonNull Application application) {
        super(application);
        checkIsNova9Config();
    }

    public void setEditUuid(String uuid) {
        editor = HuaweiVideoEditor.getInstance(uuid);
    }

    public void checkIsNova9Config() {
        isNova9 = SystemUtils.checkIsDeviceType(InfoStateUtil.PRODUCT_MODEL_FOR_NOVA9)
            || SystemUtils.checkIsDeviceType(InfoStateUtil.PRODUCT_MODEL_FOR_NOVA10);
        SmartLog.i(TAG, "[onCreate] current device is nova9 : " + isNova9);
        if (!isNova9 && DeviceProfile.getInstance().isUseSoftEncoder()) {
            SmartLog.i(TAG,
                "[onCreate] current device is isUseSoftEncoder : " + DeviceProfile.getInstance().isUseSoftEncoder());
            isNova9 = true;
        }
    }

    public boolean isNormalAsset() {
        if (editor == null) {
            SmartLog.d(TAG, "export fail, editor is null");
            return false;
        }
        HVETimeLine timeLine = editor.getTimeLine();
        if (timeLine == null || timeLine.getAllVideoLane().isEmpty()) {
            return false;
        }
        HVEVideoLane videoLane = timeLine.getVideoLane(0);
        if (videoLane.getAssets().isEmpty()) {
            return false;
        }
        HVEVisibleAsset asset = (HVEVisibleAsset) videoLane.getAssetByIndex(0);
        return asset != null;
    }

    public int getResolutionMaxProgress() {
        int maxProgress;
        switch (HVEUtil.getExportMaxResolution()) {
            case UHD_4K:
                maxProgress = RESOLUTION_MAX_PROGRESS;
                break;
            case QHD_2K:
                maxProgress = NOVA_RESOLUTION_MAX_PROGRESS;
                break;
            case HD_720P:
                maxProgress = MIN_RESOLUTION_MAX_PROGRESS;
                break;
            default:
                maxProgress = LOW_RESOLUTION_MAX_PROGRESS;
                break;
        }
        if (isLowMemory) {
            maxProgress = Math.min(maxProgress, LOW_RESOLUTION_MAX_PROGRESS);
        } else if (isNova9) {
            maxProgress = Math.min(maxProgress, NOVA_RESOLUTION_MAX_PROGRESS);
        }
        if (!DeviceProfile.getInstance().isSupportUHD()) {
            // 不支持4K的设备显示低内存进度值
            maxProgress = Math.min(maxProgress, LOW_RESOLUTION_MAX_PROGRESS);
        }
        return maxProgress;
    }

    public int getFrameRateMaxProgress() {
        if (isLowMemory) {
            return LOW_FRAME_RATE_MAX_PROGRESS;
        } else if (isNova9) {
            return LOW_FRAME_RATE_MAX_PROGRESS;
        }
        if (!DeviceProfile.getInstance().isSupportUHD()) {
            // 不支持4K的设备显示低内存进度值
            return LOW_FRAME_RATE_MAX_PROGRESS;
        }
        return FRAME_RATE_MAX_PROGRESS;
    }
}
