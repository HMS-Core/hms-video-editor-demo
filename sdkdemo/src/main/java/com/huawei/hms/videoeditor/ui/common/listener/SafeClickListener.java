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

package com.huawei.hms.videoeditor.ui.common.listener;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;

public abstract class SafeClickListener implements View.OnClickListener {
    private static final String TAG = "SafeClickListener";

    private static final long INTERVAL = 1000;

    private long mLastClickTime;

    private long mInterval = INTERVAL;
    public SafeClickListener() {
    }

    public SafeClickListener(long interval) {
        mInterval = interval;
    }

    public abstract void onSafeClick(View view);

    @Override
    public void onClick(View view) {
        if (isInInterval()) {
            Log.d(TAG, "onClick too quickly!");
            return;
        }

        onSafeClick(view);
    }

    private boolean isInInterval() {
        long nowTime = SystemClock.elapsedRealtime();
        if (nowTime - mLastClickTime <= mInterval) {
            return true;
        }
        mLastClickTime = nowTime;
        return false;
    }
}
