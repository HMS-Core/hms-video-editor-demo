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

package com.huawei.hms.videoeditor.ui.common.view.tab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TabTopLayout extends HorizontalScrollView implements ITabLayout<TabTop, TabTopInfo<?>> {
    private List<OnTabSelectedListener<TabTopInfo<?>>> tabSelectedChangeListeners = new ArrayList<>();

    private TabTopInfo<?> selectedInfo;

    private List<TabTopInfo<?>> infoList;

    public TabTopLayout(Context context) {
        this(context, null);
    }

    public TabTopLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabTopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVerticalScrollBarEnabled(false);
    }

    @Override
    public void inflateInfo(@NonNull List<TabTopInfo<?>> tabTopInfoList) {
        if (tabTopInfoList.isEmpty()) {
            return;
        }
        this.infoList = tabTopInfoList;
        LinearLayout layout = getRootLayout(true);
        selectedInfo = null;
        Iterator<OnTabSelectedListener<TabTopInfo<?>>> iterator = tabSelectedChangeListeners.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof TabTop) {
                iterator.remove();
            }
        }
        for (int i = 0; i < tabTopInfoList.size(); i++) {
            final TabTopInfo<?> info = tabTopInfoList.get(i);
            TabTop tab = new TabTop(getContext());
            tabSelectedChangeListeners.add(tab);
            tab.setHiTabInfo(info);
            layout.addView(tab);
            tab.setOnClickListener(new OnClickRepeatedListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelected(info);
                }
            }));
        }
    }

    private LinearLayout getRootLayout(boolean clear) {
        LinearLayout linearLayout = (LinearLayout) getChildAt(0);
        if (linearLayout == null) {
            linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            addView(linearLayout, layoutParams);
        } else if (clear) {
            linearLayout.removeAllViews();
        }
        linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        return linearLayout;
    }

    @Override
    public void addTabSelectedChangeListener(OnTabSelectedListener<TabTopInfo<?>> listener) {
        tabSelectedChangeListeners.add(listener);
    }

    @Nullable
    @Override
    public TabTop findTab(@NonNull TabTopInfo info) {
        ViewGroup viewGroup = getRootLayout(false);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof TabTop) {
                TabTop tabTop = (TabTop) child;
                if (tabTop.getHiTabInfo() == info) {
                    return tabTop;
                }
            }
        }
        return null;
    }

    @Override
    public void defaultSelected(@NonNull TabTopInfo defaultInfo) {
        onSelected(defaultInfo);
    }

    private void onSelected(@NonNull TabTopInfo nextInfo) {
        for (OnTabSelectedListener<TabTopInfo<?>> onTabSelectedListener : tabSelectedChangeListeners) {
            onTabSelectedListener.onTabSelectedChange(infoList.indexOf(nextInfo), selectedInfo, nextInfo);
        }
        this.selectedInfo = nextInfo;
        autoScroll(nextInfo);
    }

    int mTabWith;

    private void autoScroll(TabTopInfo nextInfo) {
        TabTop tab = findTab(nextInfo);
        if (tab == null) {
            return;
        }
        int index = infoList.indexOf(nextInfo);
        int[] loc = new int[2];
        tab.getLocationInWindow(loc);
        int scrollWidth;
        if (mTabWith == 0) {
            mTabWith = tab.getWidth();
        }
        if ((loc[0] + mTabWith / 2) > SizeUtils.screenWidth(getContext()) / 2) {
            scrollWidth = rangeScrollWidth(index, 2);
        } else {
            scrollWidth = rangeScrollWidth(index, -2);
        }
        scrollTo(getScrollX() + scrollWidth, 0);
    }

    private int rangeScrollWidth(int index, int range) {
        int mScrollWidth = 0;
        for (int i = 0; i <= Math.abs(range); i++) {
            int next;
            if (range < 0) {
                next = range + i + index;
            } else {
                next = range - i + index;
            }
            if (next >= 0 && next < infoList.size()) {
                if (range < 0) {
                    mScrollWidth -= scrollWidth(next, false);
                } else {
                    mScrollWidth += scrollWidth(next, true);
                }
            }
        }
        return mScrollWidth;

    }

    private int scrollWidth(int index, boolean toRight) {
        TabTop targetTab = findTab(infoList.get(index));
        if (targetTab == null) {
            return 0;
        }
        Rect rect = new Rect();
        targetTab.getLocalVisibleRect(rect);
        if (toRight) {
            if (rect.right > mTabWith) {
                return mTabWith;
            } else {
                return mTabWith - rect.right;
            }
        } else {
            if (rect.left <= -mTabWith) {
                return mTabWith;
            } else if (rect.left > 0) {
                return rect.left;
            }
            return 0;
        }
    }
}
