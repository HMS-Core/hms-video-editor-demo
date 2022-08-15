/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.utils;

import static com.huawei.hms.videoeditor.ui.mediaexport.model.ExportConstants.FRAME_RATE_24;
import static com.huawei.hms.videoeditor.ui.mediaexport.model.ExportConstants.FRAME_RATE_25;
import static com.huawei.hms.videoeditor.ui.mediaexport.model.ExportConstants.FRAME_RATE_30;
import static com.huawei.hms.videoeditor.ui.mediaexport.model.ExportConstants.FRAME_RATE_50;
import static com.huawei.hms.videoeditor.ui.mediaexport.model.ExportConstants.FRAME_RATE_60;

import android.util.Size;

public class ExportUtils {
    public static Size convertProgressToResolution(int progress) {
        switch (progress) {
            case 0:
                return new Size(853, 480);
            case 1:
                return new Size(1280, 720);
            case 3:
                return new Size(2560, 1440);
            case 4:
                return new Size(3840, 2160);
            default:
            case 2:
                return new Size(1920, 1080);
        }
    }

    public static int convertProgressToFrameRate(int progress) {
        switch (progress) {
            case 0:
                return FRAME_RATE_24;
            case 2:
                return FRAME_RATE_30;
            case 3:
                return FRAME_RATE_50;
            case 4:
                return FRAME_RATE_60;
            case 1:
            default:
                return FRAME_RATE_25;
        }
    }
}
