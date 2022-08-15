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

package com.huawei.hms.videoeditor.ui.mediaexport.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.huawei.hms.videoeditor.ui.common.utils.KeepOriginal;

import java.util.ArrayList;
import java.util.List;

@KeepOriginal
public class DeviceProfileCfg {

    @SerializedName("profiles")
    @Expose
    List<ProfileItem> profiles = new ArrayList<>();

    public List<ProfileItem> getProfiles() {
        return profiles;
    }

    @KeepOriginal
    public static class ProfileItem {
        @SerializedName("cpus")
        @Expose
        List<String> cpus = new ArrayList<>();

        @SerializedName("memorySizeFrom")
        @Expose
        int memorySizeFrom = 0;

        @SerializedName("memorySizeTo")
        @Expose
        int memorySizeTo = 1024;

        @SerializedName("maxPipNum")
        @Expose
        int maxPipNum = 6;

        @SerializedName("exportThreadNum")
        @Expose
        int exportThreadNum = 2;

        @SerializedName("supportResolution")
        @Expose
        String supportResolution = "4k";

        @SerializedName("useSoftEncoder")
        @Expose
        boolean useSoftEncoder = false;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ProfileItem{")
                .append("memorySizeFrom:")
                .append(memorySizeFrom)
                .append(", memorySizeTo:")
                .append(memorySizeTo)
                .append(", maxPipNum:")
                .append(maxPipNum)
                .append(", exportThreadNum:")
                .append(exportThreadNum)
                .append(", supportResolution:")
                .append(supportResolution)
                .append('}');
            return sb.toString();
        }
    }
}