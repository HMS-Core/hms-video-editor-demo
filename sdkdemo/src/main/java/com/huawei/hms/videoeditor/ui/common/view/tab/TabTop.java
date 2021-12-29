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

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

public class TabTop extends RelativeLayout implements ITab<TabTopInfo<?>> {
    private TabTopInfo<?> mTabInfo;

    private RelativeLayout mTabRootView;

    private TextView mTabNameView;

    private ImageView mIvTabIcon;

    private View mIndicator;

    public TabTop(Context context) {
        this(context, null);
    }

    public TabTop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabTop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.tab_top_layout, this);
        mTabRootView = findViewById(R.id.rl_root);
        mTabNameView = findViewById(R.id.tv_name);
        if (ScreenUtil.isRTL()) {
            mTabNameView.setScaleX(RTL_UI);
        } else {
            mTabNameView.setScaleX(LTR_UI);
        }
        mIndicator = findViewById(R.id.tab_top_indicator);
        mIvTabIcon = findViewById(R.id.iv_tab_icon);
    }

    @Override
    public void setHiTabInfo(@NonNull TabTopInfo<?> hiTabInfo) {
        this.mTabInfo = hiTabInfo;
        inflateInfo(false, true);
    }

    public TabTopInfo<?> getHiTabInfo() {
        return mTabInfo;
    }

    public TextView getTabNameView() {
        return mTabNameView;
    }

    public View getIndicatorView() {
        return mIndicator;
    }

    public ImageView getIvTabIcon() {
        return mIvTabIcon;
    }

    @Override
    public void resetHeight(@Px int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        getTabNameView().setVisibility(View.GONE);
    }

    private void inflateInfo(boolean selected, boolean init) {
        Typeface normalTypeface = Typeface.create(mTabInfo.mFamilyName, Typeface.NORMAL);
        mTabNameView.setTypeface(normalTypeface);
        if (init) {
            mTabRootView.setPaddingRelative(mTabInfo.mPaddingLeft, 0, mTabInfo.mPaddingRight, 0);
            mTabNameView.setVisibility(VISIBLE);
            mTabNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTabInfo.mShowIndicator ? 14 : 16);
            if (mTabInfo.mDefaultTextSize != 0) {
                mTabNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTabInfo.mDefaultTextSize);
            }
            if (!TextUtils.isEmpty(mTabInfo.mName)) {
                mTabNameView.setText(mTabInfo.mName);
            }
        }
        if (selected) {
            if (mTabInfo.mSelectTextSize != 0) {
                mTabNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTabInfo.mSelectTextSize);
            }
            this.measure(0, 0);
            LayoutParams params = new LayoutParams(mTabNameView.getMeasuredWidth(), ScreenUtil.dp2px(2));
            params.setMarginStart(mTabNameView.getLeft());
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.bottomMargin = ScreenUtil.dp2px(4);
            mIndicator.setLayoutParams(params);
            mIndicator.setVisibility(mTabInfo.mShowIndicator ? VISIBLE : GONE);
            mTabNameView.setTextColor(getTextColor(mTabInfo.mTintColor));
            mTabNameView.setAlpha(1f);
            mTabNameView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        } else {
            if (mTabInfo.mDefaultTextSize != 0) {
                mTabNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTabInfo.mDefaultTextSize);
            }
            mIndicator.setVisibility(GONE);
            mTabNameView.setTextColor(getTextColor(mTabInfo.mDefaultColor));
            mTabNameView.setAlpha(1f);
            mTabNameView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        }
    }

    @Override
    public void onTabSelectedChange(int index, @Nullable TabTopInfo<?> prevInfo, @NonNull TabTopInfo<?> nextInfo) {
        if (prevInfo != mTabInfo && nextInfo != mTabInfo || prevInfo == nextInfo) {
            return;
        }
        if (prevInfo == mTabInfo) {
            inflateInfo(false, false);
        } else {
            inflateInfo(true, false);
        }
    }

    @ColorInt
    private int getTextColor(Object textColor) {
        if (textColor instanceof String) {
            return Color.parseColor((String) textColor);
        } else {
            return (int) textColor;
        }
    }

}
