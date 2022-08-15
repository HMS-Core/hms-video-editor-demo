/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2016-2019. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.utils;

import com.huawei.hms.videoeditor.ui.common.utils.ReflectionUtils;

import java.lang.reflect.Method;

public class SystemPropertiesInvokeUtil {
    public static boolean getBoolean(final String key, final boolean def) {
        Method getMethod =
            ReflectionUtils.getMethod("android.os.SystemProperties", "getBoolean", String.class, boolean.class);
        Object object = ReflectionUtils.invoke(getMethod, null, key, def);
        if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue();
        }

        return def;
    }

    public static int getInt(String key, int def) {
        Method getIntMethod =
            ReflectionUtils.getMethod("android.os.SystemProperties", "getInt", String.class, int.class);
        Object object = ReflectionUtils.invoke(getIntMethod, null, key, def);
        if (object instanceof Integer) {
            return ((Integer) object).intValue();
        }

        return def;
    }

    public static String getString(String key, String def) {
        Method getMethod = ReflectionUtils.getMethod("android.os.SystemProperties", "get", String.class, String.class);
        Object object = ReflectionUtils.invoke(getMethod, null, key, def);
        if (object instanceof String) {
            return (String) object;
        }
        return def;
    }
}
