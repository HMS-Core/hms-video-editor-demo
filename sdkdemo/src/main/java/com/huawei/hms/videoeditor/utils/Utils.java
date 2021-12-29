
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

package com.huawei.hms.videoeditor.utils;

import android.app.Activity;
import android.graphics.Point;
import android.util.Log;

public class Utils {
    private static final String TAG = "Utils";

    public static boolean isMateX(Activity context) {
        Point p = new Point();
        if (context.getWindowManager() != null && context.getWindowManager().getDefaultDisplay() != null) {
            context.getWindowManager().getDefaultDisplay().getSize(p);
        }
        Log.i(TAG, "isMateX: " + p.x + "y:" + p.y);
        return p.x > 2000;
    }

}
