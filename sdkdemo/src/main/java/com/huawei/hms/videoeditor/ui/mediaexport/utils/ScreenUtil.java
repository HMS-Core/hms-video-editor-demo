/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class ScreenUtil {
    private ScreenUtil() {
    }

    public static int getScreenWidth(Context context) {
        if (context == null) {
            return 0;
        }
        Resources resources = context.getResources();
        if (resources == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (displayMetrics == null) {
            return 0;
        }
        return displayMetrics.widthPixels;
    }
}
