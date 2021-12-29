
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.view.tab.ITabLayout;
import com.huawei.hms.videoeditor.ui.common.view.tab.bottom.TabBottom;
import com.huawei.hms.videoeditor.ui.common.view.tab.bottom.TabBottomInfo;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EditMenuFirstLayout extends HorizontalScrollView implements ITabLayout<TabBottom, TabBottomInfo<?>> {
    private static final String TAG = "EditMenuFirstLayout";

    private final List<OnTabSelectedListener<TabBottomInfo<?>>> tabSelectedChangeListeners = new ArrayList<>();

    private TabBottomInfo<?> selectedInfo;

    private List<TabBottomInfo<?>> infoList;

    public EditMenuFirstLayout(Context context) {
        this(context, null);
    }

    public EditMenuFirstLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditMenuFirstLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVerticalScrollBarEnabled(false);
    }

    @Override
    public void inflateInfo(@NonNull List<TabBottomInfo<?>> infoList) {
        if (infoList.isEmpty()) {
            return;
        }
        this.infoList = infoList;
        LinearLayout linearLayout = getRootLayout(true);
        selectedInfo = null;
        Iterator<OnTabSelectedListener<TabBottomInfo<?>>> iterator = tabSelectedChangeListeners.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof TabBottom) {
                iterator.remove();
            }
        }
        for (int i = 0; i < infoList.size(); i++) {
            final TabBottomInfo<?> info = infoList.get(i);
            TabBottom tab = new TabBottom(getContext());
            tabSelectedChangeListeners.add(tab);
            tab.setHiTabInfo(info);
            linearLayout.addView(tab);
            tab.setTag(R.id.tabBottomTag, info);

            tab.setOnClickListener(new OnClickRepeatedListener(v -> {
                if (info.enable) {
                    onSelected(info);
                }
            }));
        }
    }

    private LinearLayout getRootLayout(boolean clear) {
        LinearLayout rootView = (LinearLayout) getChildAt(0);
        if (rootView == null) {
            rootView = new LinearLayout(getContext());
            rootView.setOrientation(LinearLayout.HORIZONTAL);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            addView(rootView, layoutParams);
        } else if (clear) {
            rootView.removeAllViews();
        }
        return rootView;
    }

    @Override
    public void addTabSelectedChangeListener(OnTabSelectedListener<TabBottomInfo<?>> listener) {
        tabSelectedChangeListeners.add(listener);
    }

    @Nullable
    @Override
    public TabBottom findTab(@NonNull TabBottomInfo<?> info) {
        ViewGroup ll = getRootLayout(false);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (child instanceof TabBottom) {
                TabBottom tab = (TabBottom) child;
                if (tab.getHiTabInfo() == info) {
                    return tab;
                }
            }
        }
        return null;
    }

    @Override
    public void defaultSelected(@NonNull TabBottomInfo<?> defaultInfo) {
        onSelected(defaultInfo);
    }

    private void onSelected(@NonNull TabBottomInfo<?> nextInfo) {
        try {
            for (OnTabSelectedListener<TabBottomInfo<?>> listener : tabSelectedChangeListeners) {
                listener.onTabSelectedChange(infoList.indexOf(nextInfo), selectedInfo, nextInfo);
            }
            this.selectedInfo = nextInfo;
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
        }

    }
}
