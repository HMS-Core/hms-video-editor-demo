/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaeditor.menu;

import android.text.TextUtils;

import java.util.List;

public class DeviceModelConfigBean {
    private List<DeviceModel> deviceModels;

    public String getDeviceModels(String deviceType) {
        if (deviceModels == null || deviceModels.isEmpty()) {
            return null;
        }
        if (TextUtils.isEmpty(deviceType)) {
            return null;
        }
        StringBuilder models = new StringBuilder();

        for (DeviceModel deviceModel : deviceModels) {
            if (deviceModel == null) {
                continue;
            }
            if (deviceType.equals(deviceModel.getKey().trim())) {
                models.append(deviceModel.getValue());
            }
        }
        return models.toString();
    }
}
