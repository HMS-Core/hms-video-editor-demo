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

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.Context;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;

public class MemoryInfoUtil {
    private static final String TAG = "MemoryInfoUtil";

    public static final int MEMORY_THRESHOLD_4G = 4 * 1024;

    public static final int MEMORY_THRESHOLD_6G = 6 * 1024;

    private MemoryInfoUtil() {
    }

    public static boolean isLowMemoryDevice(Context context) {
        return isLowMemoryDevice(MEMORY_THRESHOLD_4G, context);
    }

    public static boolean isLowMemoryDevice(int size, Context context) {
        if (context == null) {
            return false;
        }

        long totalMem = getMemorySizeM(context);
        SmartLog.d(TAG, "isLowMemoryDevice: " + totalMem + " MB");
        return (int) totalMem <= size;
    }

    public static long getMemorySizeM(Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        return info.totalMem >> 20;
    }
}