/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2016-2020. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.utils;

import android.text.TextUtils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DeviceUtils {
    private static final Pattern PATTERN = Pattern.compile("sm\\d+");

    public static String getDeviceModel() {
        return SystemPropertiesInvokeUtil.getString("ro.product.model", "");
    }

    public static String getCPUModel() {
        String cpuModel = SystemPropertiesInvokeUtil.getString("ro.product.vendor.device", "");
        if (TextUtils.isEmpty(cpuModel)) {
            cpuModel = SystemPropertiesInvokeUtil.getString("ro.board.platform", "");
        }
        if ("lahaina".equalsIgnoreCase(cpuModel)) {
            String chipsetVersion = SystemPropertiesInvokeUtil.getString("ro.comp.chipset_version", "");
            if (TextUtils.isEmpty(chipsetVersion)) {
                return cpuModel;
            }

            Matcher matcher = PATTERN.matcher(chipsetVersion.toLowerCase(Locale.ENGLISH));
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return cpuModel;
    }
}
