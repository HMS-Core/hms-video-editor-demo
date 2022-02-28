/*
 *   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

import android.text.TextUtils;

import com.huawei.hms.videoeditor.utils.SmartLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionUtils {
    private static final String TAG = "ReflectionUtils";

    private ReflectionUtils() {

    }

    public static Method getMethod(String className, String methodName, Class<?>... parameterTypes) {
        if (TextUtils.isEmpty(className) || TextUtils.isEmpty(methodName)) {
            SmartLog.w(TAG, "getMethod param className or methodname can not be null!");
            return null;
        }

        Method method = null;

        try {
            Class<?> c = Class.forName(className);
            method = c.getMethod(methodName, parameterTypes);
        } catch (ClassNotFoundException e) {
            SmartLog.e(TAG, "getMethod:" + e.getMessage());
        } catch (NoSuchMethodException e) {
            SmartLog.e(TAG, "getMethod:" + e.getMessage());
        } catch (Exception e) {
            SmartLog.e(TAG, "getMethod:" + e.getMessage());
        }

        return method;
    }

    public static Object invoke(Method method, Object receiver, Object... args) {
        if (null == method) {
            SmartLog.w(TAG, "invoke param method can not be null!");
            return null;
        }

        try {
            return method.invoke(receiver, args);
        } catch (InvocationTargetException e) {
            SmartLog.e(TAG, method + " invoke ", e.getTargetException());
        } catch (IllegalArgumentException e) {
            SmartLog.e(TAG, method + ", IllegalArgumentException: " + e.getMessage());
        } catch (Exception e) {
            SmartLog.e(TAG, method + " invoke " + e.getMessage());
        }
        return null;
    }


    public static Object newInstance(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            SmartLog.e(TAG, "newInstance InstantiationException " + e.getMessage());
        } catch (IllegalAccessException e) {
            SmartLog.e(TAG, "newInstance IllegalAccessException " + e.getMessage());
        }
        return null;
    }

    public static <T> T newInstance(String className, Class<T> baseClz) {
        if (!StringUtil.isEmpty(className) && baseClz != null) {
            try {
                Class clz = Class.forName(className);
                if (isSubClassOf(clz, baseClz)) {
                    return (T) clz.newInstance();
                }
            } catch (ClassNotFoundException e) {
                SmartLog.w(TAG, "newInstance ClassNotFoundException");
            } catch (InstantiationException e) {
                SmartLog.w(TAG, "newInstance InstantiationException");
            } catch (IllegalAccessException e) {
                SmartLog.w(TAG, "newInstance IllegalAccessException");
            }
        }

        return null;
    }

    public static boolean isSubClassOf(Class childClz, Class parentClz) {
        if (childClz != null && parentClz != null) {
            return parentClz.isAssignableFrom(childClz);
        }
        return false;
    }
}
