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

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;

import com.huawei.hms.videoeditor.VideoEditorApplication;
import com.huawei.hms.videoeditor.utils.SmartLog;

public final class ResUtils {
    private static final String TAG = "ResUtils";

    private ResUtils() {
    }

    public static Resources getResources() {
        return VideoEditorApplication.getInstance().getContext().getResources();
    }

    public static int getColor(int resId) {
        try {
            return getResources().getColor(resId);
        } catch (NotFoundException e) {
            SmartLog.e(TAG, e.getMessage());
        }
        return 0;
    }
}
