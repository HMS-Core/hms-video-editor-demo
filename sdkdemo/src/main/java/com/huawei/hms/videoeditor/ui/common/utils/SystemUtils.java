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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import android.os.Build;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.common.utils.SystemPropertiesInvoke;
import com.huawei.hms.videoeditor.sdk.util.MemoryInfoUtil;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;

public class SystemUtils {

    private static final String TAG = "SystemUtils";

    public static final String ROM_MIUI = "MIUI";

    public static final String ROM_EMUI = "EMUI";

    public static final String ROM_FLYME = "FLYME";

    public static final String ROM_OPPO = "OPPO";

    public static final String ROM_SMARTISAN = "SMARTISAN";

    public static final String ROM_VIVO = "VIVO";

    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";

    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";

    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";

    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";

    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";

    private static String sName;

    private static String sVersion;

    public static String getName() {
        if (sName == null) {
            check("");
        }
        return sName;
    }

    public static String getVersion() {
        if (sVersion == null) {
            check("");
        }
        return sVersion;
    }

    public static boolean check(String rom) {
        if (sName != null) {
            return sName.equals(rom);
        }

        if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) {
            sName = ROM_MIUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_EMUI))) {
            sName = ROM_EMUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_OPPO))) {
            sName = ROM_OPPO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_VIVO))) {
            sName = ROM_VIVO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_SMARTISAN))) {
            sName = ROM_SMARTISAN;
        } else {
            sVersion = Build.DISPLAY;
            if (sVersion.toUpperCase(Locale.getDefault()).contains(ROM_FLYME)) {
                sName = ROM_FLYME;
            } else {
                sVersion = Build.UNKNOWN;
                sName = Build.MANUFACTURER.toUpperCase(Locale.getDefault());
            }
        }
        return sName.equals(rom);
    }

    public static String getProp(String name) {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            SmartLog.e(TAG, "Unable to read prop " + name, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    SmartLog.e(TAG, e.getMessage());
                }
            }
        }
        return line;
    }

    public static boolean isLowDevice() {
        boolean ultraLite = SystemPropertiesInvoke.getBoolean("ro.build.hw_emui_ultra_lite", false);
        boolean emuiLite = SystemPropertiesInvoke.getBoolean("ro.build.hw_emui_lite.enable", false);
        boolean isLowMem = MemoryInfoUtil.isLowMemoryDevice(MemoryInfoUtil.MEMORY_THRESHOLD_6G);
        return ultraLite || emuiLite || isLowMem;
    }
}
