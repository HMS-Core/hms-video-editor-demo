
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

import com.huawei.hms.videoeditor.sdk.util.MemoryInfoUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.core.content.ContextCompat;

public class FrameRate extends View {
    private Paint mDegreePaint;

    private Paint mTextPaint;

    private int mDegreeCount = 100;

    private int mTextColor = R.color.speedBarTextColor;

    private int mDegreeColor = R.color.speedBarTextColor;

    private float mDegreeWidth = ScreenUtil.dp2px(1f);

    private float mTextSize = ScreenUtil.dp2px(10f);

    private float mThumbRadius = ScreenUtil.dp2px(9);

    private float mLength;

    private String[] textList = new String[] {"24", "25", "30", "50", "60"};

    private String[] lowTextList = new String[] {"24", "25", "30"};

    private float space;

    private String text;

    private boolean isLowMemory;

    public FrameRate(Context context) {
        super(context);
        init(context, null);
    }

    public FrameRate(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FrameRate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpeedBar);
            int markCount = ta.getInt(R.styleable.SpeedBar_speed_degreeCount, mDegreeCount);
            float markWidth = ta.getDimension(R.styleable.SpeedBar_speed_degreeWidth, mDegreeWidth);
            float wordSize = ta.getDimension(R.styleable.SpeedBar_speed_textSize, mTextSize);
            int wordColor =
                ta.getColor(R.styleable.SpeedBar_speed_textColor, ContextCompat.getColor(context, mTextColor));
            int markColor =
                ta.getColor(R.styleable.SpeedBar_speed_degreeColor, ContextCompat.getColor(context, mDegreeColor));
            float markRadius = ta.getDimension(R.styleable.SpeedBar_speed_thumbRadius, mThumbRadius);
            float degreelength = ta.getDimension(R.styleable.SpeedBar_speed_length, mLength);
            ta.recycle();
            this.mDegreeCount = markCount;
            this.mTextColor = wordColor;
            this.mDegreeWidth = markWidth;
            this.mTextSize = wordSize;
            this.mDegreeColor = markColor;
            this.mThumbRadius = markRadius;
            this.mLength = degreelength;
        }

        isLowMemory = MemoryInfoUtil.isLowMemoryDevice();
        space = mLength / mDegreeCount;
        setDegreePaint(mDegreeColor, mDegreeWidth);
        setTextPaint(mTextColor, mTextSize);
    }

    private void setDegreePaint(int degreeColor, float degreeWidth) {
        mDegreePaint = new Paint();
        mDegreePaint.setColor(degreeColor);
        mDegreePaint.setAntiAlias(true);
        mDegreePaint.setStyle(Paint.Style.STROKE);
        mDegreePaint.setStrokeWidth(degreeWidth);
    }

    private void setTextPaint(int textColor, float textSize) {
        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(textSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec + 2 * (int) mThumbRadius, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i <= mDegreeCount; i++) {
            if (isLowMemory) {
                if (i % 20 == 0) {
                    text = lowTextList[i / 20];
                    drawText(canvas, space * i + mThumbRadius, text);
                }
            } else {
                if (i % 10 == 0) {
                    text = textList[i / 10];
                    drawText(canvas, space * i + mThumbRadius, text);
                }
            }
        }
    }

    private void drawText(Canvas canvas, float offset, String text) {
        Rect mBounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), mBounds);
        float textWidth = mBounds.width();
        float textHeight = mBounds.height();
        canvas.drawText(text, offset - textWidth / 2, getBottom() - getTop() - textHeight, mTextPaint);
    }
}
