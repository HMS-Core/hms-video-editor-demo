/*
 *  Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.hms.videoeditor.ui.mediaeditor.materialedit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;

import androidx.annotation.Nullable;

public class ReferenceLineView extends View {

    private static final int LINE_LENGTH = 100;

    private Paint mLinePaint;

    private int mLineColor = Color.WHITE;

    private Paint mSafeRegionPaint;

    private int mSafeRegionColor = Color.RED;

    private Rect mSafeRegionRect = new Rect();

    private boolean isDrawXLine = false;

    private boolean isDrawYLine = false;

    private boolean isDrawSafeRegion = false;

    public ReferenceLineView(Context context) {
        this(context, null);
    }

    public ReferenceLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReferenceLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(3);

        mSafeRegionPaint = new Paint();
        mSafeRegionPaint.setStyle(Paint.Style.STROKE);
        mSafeRegionPaint.setColor(mSafeRegionColor);
        mSafeRegionPaint.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        if (isDrawSafeRegion) {
            canvas.drawRect(mSafeRegionRect, mSafeRegionPaint);
        }

        Path path = new Path();

        if (!isDrawSafeRegion && isDrawXLine) {
            float div = BigDecimalUtils.div(getWidth(), 2f);
            path.moveTo(div, 0);
            path.lineTo(div, LINE_LENGTH);
            path.moveTo(div, getHeight());
            path.lineTo(div, getHeight() - LINE_LENGTH);
        }

        if (!isDrawSafeRegion && isDrawYLine) {
            float div = BigDecimalUtils.div(getHeight(), 2f);
            path.moveTo(0, div);
            path.lineTo(LINE_LENGTH, div);
            path.moveTo(getWidth(), div);
            path.lineTo(getWidth() - LINE_LENGTH, div);
        }

        canvas.drawPath(path, mLinePaint);
    }

    public void setShowReferenceLine(boolean isShow, boolean isHorizontal) {
        if (isHorizontal) {
            setXLineVisibility(isShow);
        } else {
            setYLineVisibility(isShow);
        }
    }

    private void setXLineVisibility(boolean isVisible) {
        this.isDrawXLine = isVisible;
        invalidate();
    }

    private void setYLineVisibility(boolean isVisible) {
        this.isDrawYLine = isVisible;
        invalidate();
    }
}
