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
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.sdk.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferenceUtils {

    private static final String TAG = "SharedPreferenceUtils";

    private static volatile Map<String, SharedPreferenceUtils> instanceMap = new HashMap<>();

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;

    private SharedPreferenceUtils(Context context, String name) {
        if (StringUtil.isEmpty(name)) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        editor = sharedPreferences.edit();
    }

    public static SharedPreferenceUtils get(String name) {
        if (instanceMap.get(name) == null) {
            synchronized (SharedPreferenceUtils.class) {
                if (instanceMap.get(name) == null) {
                    Context context = VideoEditorApplication.getInstance().getContext();
                    if (context != null) {
                        instanceMap.put(name, new SharedPreferenceUtils(context, name));
                    }
                }
            }
        }
        return instanceMap.get(name);
    }

    public static SharedPreferenceUtils get(Context context, String name) {
        if (instanceMap.get(name) == null) {
            synchronized (SharedPreferenceUtils.class) {
                if (instanceMap.get(name) == null) {
                    instanceMap.put(name, new SharedPreferenceUtils(context, name));
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

    public int getInt(String key, int defaultIntValue) {
        try {
            return sharedPreferences.getInt(key, defaultIntValue);
        } catch (Exception e) {
            SmartLog.e(TAG, "get int value failed, key=" + key);
        }
        return defaultIntValue;
    }

    public long getLong(String key, long defaultLongValue) {
        try {
            return sharedPreferences.getLong(key, defaultLongValue);
        } catch (Exception e) {
            SmartLog.e(TAG, "get long value failed, key=" + key);
        }
        return defaultLongValue;
    }

    public float getFloat(String key, float defaultFloatValue) {
        try {
            return sharedPreferences.getFloat(key, defaultFloatValue);
        } catch (Exception e) {
            SmartLog.e(TAG, "get float value failed, key=" + key);
        }
        return defaultFloatValue;
    }

    public boolean getBoolean(String key, boolean defaultBooleanValue) {
        try {
            return sharedPreferences.getBoolean(key, defaultBooleanValue);
        } catch (Exception e) {
            SmartLog.e(TAG, "get boolean value failed, key=" + key);
        }
        return defaultBooleanValue;
    }

    public void remove(String spKey) {
        editor.remove(spKey).apply();
    }

    public void clear() {
        editor.clear().apply();
    }
}
