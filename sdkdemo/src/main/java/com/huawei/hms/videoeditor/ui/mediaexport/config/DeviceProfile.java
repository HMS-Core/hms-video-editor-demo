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

import android.content.Context;

import com.huawei.hms.videoeditor.VideoEditorApplication;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ui.common.utils.GsonUtils;
import com.huawei.hms.videoeditor.ui.common.utils.KeepOriginal;
import com.huawei.hms.videoeditor.ui.common.utils.MemoryInfoUtil;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.mediaexport.utils.DeviceUtils;
import com.huawei.hms.videoeditor.utils.SmartLog;

import java.util.List;

@KeepOriginal
public class DeviceProfile {
    private static final String TAG = "DeviceProfile";

    public static final int MAX_PIP_LOW_PROFILE = 3;

    public static final int MAX_PIP_HIGH_PROFILE = 6;

    public static final String RESOLUTION_1080P = "1080p";

    public static final String RESOLUTION_4K = "4k";

    private int mMaxPipNum = MAX_PIP_HIGH_PROFILE;

    private int exportThreadNum = 2;

    private boolean isUseSoftEncoder = false;

    private static final class Holder {
        static DeviceProfile sInstance = new DeviceProfile();
    }

    public static DeviceProfile getInstance() {
        return Holder.sInstance;
    }

    private DeviceProfile() {
        String cpu = DeviceUtils.getCPUModel();
        long sizeM = MemoryInfoUtil.getMemorySizeM();
        int sizeG = (int) Math.ceil((double) sizeM / 1024);

        Context context = VideoEditorApplication.getInstance().getContext();
        if (context == null) {
            return;
        }
        String profileContent = FileUtil.readAssetsFile(context, "device_profile.json");
        DeviceProfileCfg profileCfg = GsonUtils.fromJson(profileContent, DeviceProfileCfg.class);
        if (profileCfg == null) {
            SmartLog.w(TAG, "parse device_profile.json failed");
            return;
        }

        List<DeviceProfileCfg.ProfileItem> items = profileCfg.getProfiles();
        for (DeviceProfileCfg.ProfileItem item : items) {
            if (StringUtil.match(item.cpus, cpu) && isBetween(item.memorySizeFrom, item.memorySizeTo, sizeG)) {
                SmartLog.i(TAG, cpu + " matched profile " + item);
                mMaxPipNum = item.maxPipNum;
                exportThreadNum = item.exportThreadNum;
                isUseSoftEncoder = item.useSoftEncoder;
                return;
            }
        }
        SmartLog.i(TAG, "use default profile for " + cpu);
    }

    private static boolean isBetween(int from, int to, int current) {
        if (from <= current && current <= to) {
            return true;
        }
        return false;
    }

    public int getMaxPipNum() {
        if (MemoryInfoUtil.isLowMemoryDevice() && MAX_PIP_LOW_PROFILE < mMaxPipNum) {
            return MAX_PIP_LOW_PROFILE;
        }
        return mMaxPipNum;
    }

    public int getExportThreadNum() {
        if (MemoryInfoUtil.isLowMemoryDevice()) {
            return 1;
        }
        return exportThreadNum;
    }

    public boolean isUseSoftEncoder() {
        return isUseSoftEncoder;
    }

    public static boolean isSupportCuvaHdr() {
        return false;
    }
}