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

package com.huawei.hms.videoeditor.ui.common.view.decoration;

import android.content.Context;

import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

public class DividerLine {
    public static RecyclerViewDivider getLine(Context context, float height, int colorRes) {
        return new RecyclerViewDivider(context, LinearLayoutManager.VERTICAL, SizeUtils.dp2Px(context, height),
            ContextCompat.getColor(context, colorRes));
    }
}
