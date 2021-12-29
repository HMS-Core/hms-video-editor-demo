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

package com.huawei.hms.videoeditor.ui.mediaeditor.menu;

import com.huawei.hms.videoeditor.sdk.util.KeepOriginal;

@KeepOriginal
public class DeviceModel {

    private String key;

    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DeviceModel{" + "key='" + key + '\'' + ", value='" + value + '\'' + '}';
    }

    @Override
    public boolean equals(Object deviceModelObject) {
        if (this == deviceModelObject) {
            return true;
        }
        if (deviceModelObject == null || getClass() != deviceModelObject.getClass()) {
            return false;
        }
        DeviceModel that = (DeviceModel) deviceModelObject;
        return key.equals(that.key) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
