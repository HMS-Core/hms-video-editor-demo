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

package com.huawei.hms.videoeditor.ui.template.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class ClippingMaskTimeLine extends View {
    private Context mContext;
    private int mRectWith = dipToPx(192);
    private int mRectHeight = dipToPx(48);
    private Paint mPaintLine;

    public ClippingMaskTimeLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        mPaintLine = new Paint();
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(dipToPx(1.8f));
        mPaintLine.setColor(ContextCompat.getColor(mContext, R.color.clip_color_E6FFFFFF));
    }

    public void setInnerRect(int with, int height) {
        mRectWith = with;
        mRectHeight = height;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = mRectWith >= getMeasuredWidth() ? 0 : (getMeasuredWidth() - mRectWith) / 2;
        int top = mRectHeight >= getMeasuredHeight() ? 0 : (getMeasuredHeight() - mRectHeight) / 2;
        int i = ((top + mRectHeight) - (left + mRectWith)) / 2;
        canvas.drawLine(left + dipToPx(2), top - i, left + dipToPx(2), (top + mRectHeight) + i, mPaintLine);
    }

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }
}
