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

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    private static SharedPreferencesUtils sharedPreferencesUtils = null;

    private final static String INTO_STATE_INFO = "into_info";

    private final static String VOICE_SETTING_INFO = "voice_setting_info";

    private final static String VOICE_SETTING_STATE = "voice_setting_state";

    public final static String COLOR_SELECT_INDEX = "COLOR_SELECT_INDEX";

    public final static String TEXT_COLOR_INDEX = "TEXT_COLOR_INDEX";

    public final static String TEXT_STROKE_INDEX = "TEXT_STROKE_INDEX";

    public final static String TEXT_SHAWDOW_INDEX = "COLOR_SHAWADEr_INDEX";

    public final static String TEXT_BACK_INDEX = "TEXT_BACK_INDEX";

    public final static String TEXT_TEMPLATE_HINT = "TEXT_TEMPLATE_HINT";

    private final static String COPY_DRAFT_TIMES = "COPY_DRAFT_TIMES";

    public static SharedPreferencesUtils getInstance() {
        synchronized (SharedPreferencesUtils.class) {
            if (sharedPreferencesUtils == null) {
                sharedPreferencesUtils = new SharedPreferencesUtils();
            }
        }
        return sharedPreferencesUtils;
    }

    public void voiceSetting(Context context, boolean isVoiceIconShow) {
        SharedPreferences sp = context.getSharedPreferences(VOICE_SETTING_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(VOICE_SETTING_STATE, isVoiceIconShow);
        edit.commit();
    }

    public boolean getVoiceSetting(Context context) {
        SharedPreferences sp = context.getSharedPreferences(VOICE_SETTING_INFO, Context.MODE_PRIVATE);
        return sp.getBoolean(VOICE_SETTING_STATE, false);
    }

    public void putStringValue(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(INTO_STATE_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value).apply();
    }

    public String getStringValue(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(INTO_STATE_INFO, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public void putIntValue(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key, value).apply();
    }

    public int getIntValue(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public int getIntValue2(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp.getInt(key, 1);
    }

    public void putCopyDraftTimes(Context context, String key, int value) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(COPY_DRAFT_TIMES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key, value).apply();
    }

    public int getCopyDraftTimes(Context context, String key) {
        if (context == null) {
            return 0;
        }
        SharedPreferences sp = context.getSharedPreferences(COPY_DRAFT_TIMES, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }
}
