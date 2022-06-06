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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.huawei.hms.videoeditor.VideoEditorApplication;
import com.huawei.hms.videoeditor.utils.SmartLog;

import java.util.HashMap;
import java.util.Map;

public final class SPManager {
    public static final String LOG_PREFIX = "SP_";

    private static final String TAG = LOG_PREFIX + "SPManager";

    private static volatile Map<String, SPManager> instanceMap = new HashMap<>();

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;

    private SPManager(String name, Context context) {
        if (StringUtil.isEmpty(name)) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        editor = sharedPreferences.edit();
    }

    public static SPManager get(String name) {
        return get(name, VideoEditorApplication.getInstance().getContext());
    }

    public static SPManager get(String name, Context context) {
        if (context == null) {
            return null;
        }
        if (instanceMap.get(name) == null) {
            synchronized (SPManager.class) {
                if (instanceMap.get(name) == null) {
                    instanceMap.put(name, new SPManager(name, context));
                }
            }
        }
        return instanceMap.get(name);
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void put(String key, String value) {
        editor.putString(key, value).apply();
    }

    public void put(String key, int value) {
        editor.putInt(key, value).apply();
    }

    public void put(String key, long value) {
        editor.putLong(key, value).apply();
    }

    public void put(String key, float value) {
        editor.putFloat(key, value).apply();
    }

    public void put(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        try {
            return sharedPreferences.getString(key, defaultValue);
        } catch (Exception e) {
            SmartLog.e(TAG, "get string value failed, key=" + key);
        }
        return defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        try {
            return sharedPreferences.getInt(key, defaultValue);
        } catch (Exception e) {
            SmartLog.e(TAG, "get int value failed, key=" + key);
        }
        return defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        try {
            return sharedPreferences.getLong(key, defaultValue);
        } catch (Exception e) {
            SmartLog.e(TAG, "get long value failed, key=" + key);
        }
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return sharedPreferences.getBoolean(key, defaultValue);
        } catch (Exception e) {
            SmartLog.e(TAG, "get boolean value failed, key=" + key);
        }
        return defaultValue;
    }

    public void remove(String key) {
        editor.remove(key).apply();
    }

    public void clear() {
        editor.clear().apply();
    }
}
