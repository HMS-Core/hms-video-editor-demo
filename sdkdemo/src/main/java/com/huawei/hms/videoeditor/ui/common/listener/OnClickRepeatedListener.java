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

import android.view.View;

public class OnClickRepeatedListener implements View.OnClickListener {
    private final View.OnClickListener onClickListener;

    private long lastClickTime = 0;

    private long spaceTimes = 500;

    public OnClickRepeatedListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public OnClickRepeatedListener(View.OnClickListener onClickListener, long spaceTimes) {
        this.onClickListener = onClickListener;
        this.spaceTimes = spaceTimes;
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - lastClickTime >= spaceTimes) {
            onClickListener.onClick(v);
            lastClickTime = System.currentTimeMillis();
        }
    }
}
