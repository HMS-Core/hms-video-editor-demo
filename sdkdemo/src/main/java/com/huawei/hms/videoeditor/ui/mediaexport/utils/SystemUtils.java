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

package com.huawei.hms.videoeditor.ui.mediaexport.utils;

import android.os.Build;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.utils.SmartLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SystemUtils {
    private static final String TAG = "SystemUtils";

    public static final String ROM_MIUI = "MIUI";

    public static final String ROM_EMUI = "EMUI";

    public static final String ROM_FLYME = "FLYME";

    public static final String ROM_OPPO = "OPPO";

    public static final String ROM_SMARTISAN = "SMARTISAN";

    public static final String ROM_VIVO = "VIVO";

    public static final String ROM_QIKU = "QIKU";

    public static final String ROM_HUAWEI = "HUAWEI";

    public static final String ROM_HONOR = "HONOR";

    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";

    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";

    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";

    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";

    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";

    private static String sName;

    private static String sVersion;

    public static boolean is360() {
        return check(ROM_QIKU) || check("360");
    }

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

    public static boolean checkIsDeviceType(String deviceType) {
        String deviceModel = DeviceUtils.getDeviceModel();
        if (StringUtil.isEmpty(deviceModel)) {
            return false;
        }
        SmartLog.i(TAG, "deviceModel==" + deviceModel);
        if (StringUtil.isEmpty(deviceType)) {
            return false;
        }
        SmartLog.i(TAG, "deviceType==" + deviceType);

        String modelForDeviceType = null;
        if (TextUtils.equals(InfoStateUtil.PRODUCT_MODEL_FOR_NOVA9, deviceType)) {
            modelForDeviceType = InfoStateUtil.getInstance().getProductModelForNOVA9();
        } else if (TextUtils.equals(InfoStateUtil.PRODUCT_MODEL_FOR_NOVA9SE, deviceType)) {
            modelForDeviceType = InfoStateUtil.getInstance().getProductModelForNOVA9SE();
        } else if (TextUtils.equals(InfoStateUtil.PRODUCT_MODEL_FOR_NOVA10, deviceType)) {
            modelForDeviceType = InfoStateUtil.getInstance().getProductModelForNOVA10();
        } else if (TextUtils.equals(InfoStateUtil.PRODUCT_MODEL_FOR_P50, deviceType)) {
            modelForDeviceType = InfoStateUtil.getInstance().getProductModelForP50();
        } else if (TextUtils.equals(InfoStateUtil.PRODUCT_MODEL_FOR_P50E, deviceType)) {
            modelForDeviceType = InfoStateUtil.getInstance().getProductModelForP50E();
        } else if (TextUtils.equals(InfoStateUtil.PRODUCT_MODEL_FOR_MATE20PRO, deviceType)) {
            modelForDeviceType = InfoStateUtil.getInstance().getProductModelForMATE20PRO();
        } else if (TextUtils.equals(InfoStateUtil.PRODUCT_MODEL_FOR_P40PROJ, deviceType)) {
            modelForDeviceType = InfoStateUtil.getInstance().getProductModelForP40proj();
        } else if (TextUtils.equals(InfoStateUtil.PRODUCT_MODEL_FOR_MATE30, deviceType)) {
            modelForDeviceType = InfoStateUtil.getInstance().getProductModelForMate30();
        } else if (TextUtils.equals(InfoStateUtil.PRODUCT_MODEL_FOR_MATE50, deviceType)) {
            modelForDeviceType = InfoStateUtil.getInstance().getProductModelForMate50();
        }
        if (!TextUtils.isEmpty(modelForDeviceType)) {
            SmartLog.i(TAG, "modelForDeviceType==" + modelForDeviceType);

            List<String> mDeviceModelList = new ArrayList<>(Arrays.asList(modelForDeviceType.split(",")));
            SmartLog.i(TAG, "mDeviceModelList==" + mDeviceModelList.toString());

            if (mDeviceModelList.isEmpty()) {
                SmartLog.i(TAG, "unsupported device:" + deviceModel);
                return false;
            }
            for (String device : mDeviceModelList) {
                if (deviceModel.contains(device.trim())) {
                    SmartLog.i(TAG, "supported device: " + deviceModel);
                    return true;
                }
            }
        }
        SmartLog.i(TAG, "unsupported device:" + deviceModel);
        return false;
    }
}
