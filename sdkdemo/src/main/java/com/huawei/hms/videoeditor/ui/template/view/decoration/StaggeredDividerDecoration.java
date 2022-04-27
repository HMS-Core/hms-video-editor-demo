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

package com.huawei.hms.videoeditor.ui.template.view.decoration;

import android.graphics.Rect;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.view.decoration.MStaggeredGridLayoutManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StaggeredDividerDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "StaggeredDrviderDecorat";

    private final int space;

    private final int parentMargin;

    public StaggeredDividerDecoration(int space, int parentMargin) {
        this.space = space;
        this.parentMargin = parentMargin;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
        @NonNull RecyclerView.State state) {
        MStaggeredGridLayoutManager.LayoutParams params =
            (MStaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex = params.getSpanIndex();
        SmartLog.i(TAG, "spanIndex:" + spanIndex);
        if (spanIndex % 2 == 0) {
            outRect.set(parentMargin, 0, space / 2, space);
        } else {
            outRect.set(space / 2, 0, parentMargin, space);
        }
    }
}
