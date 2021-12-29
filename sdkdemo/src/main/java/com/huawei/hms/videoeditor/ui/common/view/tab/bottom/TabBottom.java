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

package com.huawei.hms.videoeditor.ui.common.view.tab.bottom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.ui.common.view.tab.ITab;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

public class TabBottom extends RelativeLayout implements ITab<TabBottomInfo<?>> {
    private TabBottomInfo<?> mTabInfo;

    private ImageView mTabImageView;

    private EditorTextView mTabNameView;

    public TabBottom(Context context) {
        this(context, null);
    }

    public TabBottom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabBottom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.tab_bottom_layout, this);
        mTabImageView = findViewById(R.id.iv_image);
        mTabNameView = findViewById(R.id.tv_name);
    }

    @Override
    public void setHiTabInfo(@NonNull TabBottomInfo<?> hiTabBottomInfo) {
        this.mTabInfo = hiTabBottomInfo;
        inflateInfo(false, true);
    }

    public TabBottomInfo<?> getHiTabInfo() {
        return mTabInfo;
    }

    @Override
    public void resetHeight(@Px int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        setLayoutParams(params);
    }

    private void inflateInfo(boolean selected, boolean init) {
        if (init) {
            int mMenuWidth = (int) (SizeUtils.screenWidth(getContext()) / 6.5f);
            ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(mMenuWidth, SizeUtils.dp2Px(getContext(), 64));
            setLayoutParams(layoutParams);
            mTabNameView.setText(mTabInfo.nameResId);
            mTabImageView.setImageResource(mTabInfo.drawableIcon);
        }
        mTabNameView.setTextColor(getTextColor(selected ? mTabInfo.textSelectColor : mTabInfo.textDefaultColor));
        mTabImageView.setSelected(selected);
    }

    @Override
    public void onTabSelectedChange(int index, @Nullable TabBottomInfo<?> prevInfo,
        @NonNull TabBottomInfo<?> nextInfo) {
        if (prevInfo != mTabInfo && nextInfo != mTabInfo || prevInfo == nextInfo) {
            return;
        }
        inflateInfo(prevInfo != mTabInfo, false);
    }

    @ColorInt
    private int getTextColor(Object color) {
        if (color instanceof String) {
            return Color.parseColor((String) color);
        } else {
            return (int) color;
        }
    }
}
