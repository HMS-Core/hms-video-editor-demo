
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

package com.huawei.hms.videoeditor.ui.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.huawei.hms.videoeditor.ui.common.utils.MemoryInfoUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.core.content.ContextCompat;

public class ResolutionRateView extends View {
    private Paint degreePaint;

    private Paint textPaint;

    private int degreeCount = 100;

    private int textColor = R.color.speedBarTextColor;

    private int degreeColor = R.color.speedBarTextColor;

    private float degreeWidth = ScreenUtil.dp2px(1f);

    private float textSize = ScreenUtil.dp2px(10f);

    private float thumbRadius = ScreenUtil.dp2px(16);

    private float length;

    private String[] textList = new String[] {"720P", "1080P", "2K", "4K"};

    private String[] lowTextList = new String[] {"720P", "1080P"};

    private float space;

    private String text;

    private boolean isLowMemory;

    public ResolutionRateView(Context context) {
        super(context);
        init(context, null);
    }

    public ResolutionRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ResolutionRateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpeedBar);
            int markCount = ta.getInt(R.styleable.SpeedBar_speed_degreeCount, degreeCount);
            float markWidth = ta.getDimension(R.styleable.SpeedBar_speed_degreeWidth, degreeWidth);
            float wordSize = ta.getDimension(R.styleable.SpeedBar_speed_textSize, textSize);
            int wordColor =
                ta.getColor(R.styleable.SpeedBar_speed_textColor, ContextCompat.getColor(context, textColor));
            int markColor =
                ta.getColor(R.styleable.SpeedBar_speed_degreeColor, ContextCompat.getColor(context, degreeColor));
            float markRadius = ta.getDimension(R.styleable.SpeedBar_speed_thumbRadius, thumbRadius);
            float degreelength = ta.getDimension(R.styleable.SpeedBar_speed_length, length);
            ta.recycle();
            this.degreeCount = markCount;
            this.textColor = wordColor;
            this.degreeWidth = markWidth;
            this.textSize = wordSize;
            this.degreeColor = markColor;
            this.thumbRadius = markRadius;
            this.length = degreelength;
        }

        isLowMemory = MemoryInfoUtil.isLowMemoryDevice(context);
        space = length / degreeCount;
        setDegreePaint(degreeColor, degreeWidth);
        setTextPaint(textColor, textSize);
    }

    private void setDegreePaint(int degreeColor, float degreeWidth) {
        degreePaint = new Paint();
        degreePaint.setColor(degreeColor);
        degreePaint.setAntiAlias(true);
        degreePaint.setStyle(Paint.Style.STROKE);
        degreePaint.setStrokeWidth(degreeWidth);
    }

    private void setTextPaint(int textColor, float textSize) {
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec + 2 * (int) thumbRadius, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i <= degreeCount; i++) {
            if (isLowMemory) {
                if (i % 30 == 0) {
                    int index = i / 30;
                    text = lowTextList[index];
                    drawText(canvas, index == lowTextList.length - 1 ? space * i + thumbRadius - ScreenUtil.dp2px(16)
                        : space * i + thumbRadius, text);
                }
            } else {
                if (i % 10 == 0) {
                    text = textList[i / 10];
                    drawText(canvas, space * i + thumbRadius, text);
                }
            }
        }

    }

    private void drawText(Canvas canvas, float offset, String text) {
        Rect mBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), mBounds);
        float textWidth = mBounds.width();
        float textHeight = mBounds.height();
        canvas.drawText(text, offset - textWidth / 2, getBottom() - getTop() - textHeight, textPaint);
    }
}
