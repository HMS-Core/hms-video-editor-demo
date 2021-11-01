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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.DisplayMetrics;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;

import androidx.annotation.NonNull;

public class FoldScreenUtil {
    public static final int FOLDABLE_STATE_EXPAND = 1;

    private static final String TAG = "FoldScreenUtil";

    private static final String FOLDING_SCREEN_MANAGER_CLASS_NAME = "com.huawei.android.fsm.HwFoldScreenManagerEx";

    private static final String IS_FOLDABLE_METHOD_NAME = "isFoldable";

    private static final String GET_FOLDABLE_STATE_METHOD_NAME = "getFoldableState";

    private static int sOnlyOrientation = -1;

    private static volatile Method IS_FOLDABLE_METHOD;

    private static volatile Method GET_FOLDABLE_STATE_METHOD;

    private static volatile boolean sIsHwFoldScreenManagerExClassNotFound;

    public static boolean isFoldable() {
        if (IS_FOLDABLE_METHOD == null) {
            IS_FOLDABLE_METHOD = getMethod(IS_FOLDABLE_METHOD_NAME);
        }
        if (IS_FOLDABLE_METHOD != null) {
            Object result = invokeMethod(IS_FOLDABLE_METHOD);
            SmartLog.d(TAG, IS_FOLDABLE_METHOD_NAME + " result = " + result);
            return (result != null) && (boolean) result;
        }
        return false;
    }

    private static Object getFoldableState() {
        if (GET_FOLDABLE_STATE_METHOD == null) {
            GET_FOLDABLE_STATE_METHOD = getMethod(GET_FOLDABLE_STATE_METHOD_NAME);
        }
        if (GET_FOLDABLE_STATE_METHOD != null) {
            return invokeMethod(GET_FOLDABLE_STATE_METHOD);
        }
        return null;
    }

    public static boolean isFoldableScreenExpand(@NonNull Context context) {
        Object result = getFoldableState();
        if (result instanceof Integer) {
            return (int) result == FOLDABLE_STATE_EXPAND;
        }
        return isFoldableScreenExpandByScreenRadio(context);
    }

    private static boolean isFoldableScreenExpandByScreenRadio(@NonNull Context context) {
        DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        if (metrics != null && metrics.widthPixels != 0 && metrics.heightPixels != 0) {
            float ratioOfHeightToWidth = (float) metrics.heightPixels / (float) metrics.widthPixels;
            return ratioOfHeightToWidth >= 0.667F && ratioOfHeightToWidth <= 1.5F;
        }
        return false;
    }

    private static Object invokeMethod(@NonNull Method method) {
        Object object = null;
        try {
            object = method.invoke(null);
        } catch (IllegalAccessException e) {
            SmartLog.e(TAG, "invokeMethod failed, first exception : " + e.getMessage());
        } catch (InvocationTargetException e) {
            SmartLog.e(TAG, "invokeMethod failed, second exception :　" + e.getMessage());
        }
        return object;
    }

    private static Class getHwFoldScreenManagerExClass() {
        if (sIsHwFoldScreenManagerExClassNotFound) {
            return null;
        }
        Class clazz = null;
        try {
            clazz = Class.forName(FOLDING_SCREEN_MANAGER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            SmartLog.e(TAG, FOLDING_SCREEN_MANAGER_CLASS_NAME + " not found");
        }
        if (clazz == null) {
            sIsHwFoldScreenManagerExClassNotFound = true;
        }
        return clazz;
    }

    private static Method getMethod(@NonNull String methodName) {
        Class<?> clazz = getHwFoldScreenManagerExClass();
        if (clazz == null) {
            return null;
        }
        try {
            Method method = clazz.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            SmartLog.e(TAG, methodName + " no such method");
        }
        return null;
    }
}
