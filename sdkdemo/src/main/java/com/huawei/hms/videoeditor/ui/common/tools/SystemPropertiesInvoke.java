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

package com.huawei.hms.videoeditor.ui.common.tools;

import java.lang.reflect.Method;

import com.huawei.hms.videoeditor.common.utils.ReflectionUtils;

public class SystemPropertiesInvoke {
    public SystemPropertiesInvoke() {
    }

    public static boolean getBoolean(String key, boolean def) {
        Method getMethod = ReflectionUtils.getMethod("android.os.SystemProperties", "getBoolean",
            new Class[] {String.class, Boolean.TYPE});
        Object object = ReflectionUtils.invoke(getMethod, (Object) null, new Object[] {key, def});
        return object instanceof Boolean ? (Boolean) object : def;
    }

    public static int getInt(String key, int def) {
        Method getIntMethod = ReflectionUtils.getMethod("android.os.SystemProperties", "getInt",
            new Class[] {String.class, Integer.TYPE});
        Object object = ReflectionUtils.invoke(getIntMethod, (Object) null, new Object[] {key, def});
        return object instanceof Integer ? (Integer) object : def;
    }

    public static String getString(String key, String def) {
        Method getMethod =
            ReflectionUtils.getMethod("android.os.SystemProperties", "get", new Class[] {String.class, String.class});
        Object object = ReflectionUtils.invoke(getMethod, (Object) null, new Object[] {key, def});
        return object instanceof String ? (String) object : def;
    }
}
