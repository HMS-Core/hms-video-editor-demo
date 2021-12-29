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

package com.huawei.hms.videoeditor.ui.common.shot;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;

public class EditHorizontalScrollView extends HorizontalScrollView {
    private ScrollChangeCallback scrollChangeCallback;

    private int lastL;

    private int lastOldL;

    public EditHorizontalScrollView(Context context) {
        super(context);
        initData();
    }

    public EditHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public EditHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    protected void initData() {
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollChangeCallback != null) {
            if (lastL == l && lastOldL == oldl) {
                return;
            }
            lastOldL = oldl;
            lastL = l;
            scrollChangeCallback.scrollChanged(l, t, oldl, oldt);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    public void setCallback(ScrollChangeCallback callback) {
        scrollChangeCallback = callback;
    }

    public interface ScrollChangeCallback {
        void scrollChanged(int l, int t, int oldl, int oldt);
    }
}
