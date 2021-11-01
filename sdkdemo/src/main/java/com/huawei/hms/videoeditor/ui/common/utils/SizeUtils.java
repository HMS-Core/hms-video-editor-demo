
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

package com.huawei.hms.videoeditor.ui.common.utils;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class SizeUtils {
    public static int dp2Px(Context context, float dp) {
        Resources resources = context.getResources();
        if (resources == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (displayMetrics == null) {
            return 0;
        }
        float scale = displayMetrics.density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * convert sp to its equivalent px
     */
    public static int sp2px(Context context, float spValue) {
        Resources resources = context.getResources();
        if (resources == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (displayMetrics == null) {
            return 0;
        }
        final float fontScale = displayMetrics.scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int screenWidth(Context context) {
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

    public static int screenHeight(Context context) {
        Resources resources = context.getResources();
        if (resources == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (displayMetrics == null) {
            return 0;
        }
        return displayMetrics.heightPixels;
    }

    private static final float GB = (float) BigDecimalUtils.mul2(BigDecimalUtils.mul(1024f, 1024f), 1024f, 0);

    private static final float MB = (float) BigDecimalUtils.mul2(1024f, 1024f, 0);

    private static final float KB = 1024f;

    public static String bytes2kb(long bytes) {
        DecimalFormat format = new DecimalFormat("###.02");
        if (bytes / GB >= 1) {
            return format.format((float) (bytes / GB)) + "GB";
        } else if (bytes / MB >= 1) {
            return format.format((float) (bytes / MB)) + "MB";
        } else if (bytes / KB >= 1) {
            return format.format((float) (bytes / KB)) + "KB";
        } else {
            return bytes + "B";
        }
    }
}
