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

package com.huawei.hms.videoeditor.ui.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;

public class ProgressView extends View {
    private Paint firstPaint;

    private Paint secondPaint;

    private int firstColor;

    private int secondColor;

    private int maxProgress = 100;

    private int currentProgress = 0;

    private int w;

    private int h;

    private float calibration;

    private Paint linePaint;

    private int lineColor;

    public ProgressView(Context context) {
        super(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        firstColor = typedArray.getColor(R.styleable.ProgressView_firstColor,
            getResources().getColor(R.color.clip_color_E6000000));
        secondColor = typedArray.getColor(R.styleable.ProgressView_secondColor, getResources().getColor(R.color.white));
        lineColor = typedArray.getColor(R.styleable.ProgressView_lineColor, getResources().getColor(R.color.white));
        typedArray.recycle();
        init();
    }

    private void init() {
        firstPaint = new Paint();
        firstPaint.setColor(firstColor);
        firstPaint.setStrokeWidth(5);
        firstPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        firstPaint.setAntiAlias(true);

        secondPaint = new Paint();
        secondPaint.setColor(secondColor);
        secondPaint.setStrokeWidth(5);
        secondPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        secondPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(5);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setAntiAlias(true);
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF = new RectF(0, ScreenUtil.dp2px(8), getWidth(), getHeight() - ScreenUtil.dp2px(8));
        canvas.drawRect(rectF, firstPaint);

        RectF rectF2 = new RectF(calibration * currentProgress, ScreenUtil.dp2px(8), getWidth(),
            getHeight() - ScreenUtil.dp2px(8));
        canvas.drawRect(rectF2, secondPaint);

        canvas.drawLine(calibration * currentProgress, 0, calibration * currentProgress, getHeight(), linePaint);
    }

    public void setProgress(int maxProgress, int currentProgress) {
        this.maxProgress = maxProgress;
        this.currentProgress = currentProgress;
        calibration = (float) BigDecimalUtils.div(getWidth(), maxProgress, 5);
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        w = MeasureSpec.getSize(widthMeasureSpec);
        h = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    public void setHeightByVideoHeight() {
        requestLayout();
        postInvalidate();
    }
}
