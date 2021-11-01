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

package com.huawei.hms.videoeditor.ui.common.view.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class ProgressCircle extends View {
    private static final String TAG = ProgressCircle.class.getSimpleName();

    public static final int DEF_SIZE_IN_DP = 50;

    public static final int DEF_CIRCLE_BORDER_WIDTH_IN_DP = 1;

    private static final int DEF_CIRCLE_BORDER_COLOR = Color.parseColor("#D9FFFFFF");

    private static final int DEF_INNER_PIE_COLOR = Color.parseColor("#D9FFFFFF");

    private static final int DEF_MAX_PROGRESS = 100;

    private static final int DEF_PROGRESS = 0;

    private static final float DEF_PIE_STARGING_ANGLE = -90;

    private Paint mBorderPaint;

    private Paint mBackgroundPaint;

    private Paint mContentPaint;

    private float mDefSize;

    private RectF mInnerArcRectF;

    private float mCircleRadius;

    private int mCircleBorderColor;

    private float mCircleBorderWidth;

    private float mCircleOuterRadius;

    private float mCircleInnerRadius;

    private int mInnerPieColor;

    private int mInnerPieMaxProgress;

    private int mInnerPieProgress;

    private float mInnerPieStartingAngle;

    private int mHalfSize;

    public ProgressCircle(Context context) {
        this(context, null);
    }

    public ProgressCircle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void setProgress(int progress, int maxProgress) {
        if (progress > maxProgress) {
            SmartLog.w(TAG, "Progress can't exceed max progress");
            return;
        }
        if (progress < 0) {
            SmartLog.w(TAG, "Progress can't be less than zero");
            return;
        }
        if (maxProgress < 0) {
            SmartLog.w(TAG, "Max progress can't be less than zero");
            return;
        }
        mInnerPieMaxProgress = maxProgress;
        setProgress(progress);
    }

    public void setProgress(int progress) {
        if (progress < 0) {
            SmartLog.w(TAG, "Max progress can't be less than zero");
            return;
        }
        if (progress > mInnerPieMaxProgress) {
            SmartLog.w(TAG, "Max progress can't be less than zero");
            return;
        }
        mInnerPieProgress = progress;
        if (isMainThread()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public int getProgress() {
        return mInnerPieProgress;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initDefValues(context);
        initCustomAttrs(context, attrs, defStyleAttr);
        initActualValues();
        initPaint();
    }

    private void initDefValues(Context context) {
        mDefSize = SizeUtils.dp2Px(context, DEF_SIZE_IN_DP);

        mCircleBorderColor = DEF_CIRCLE_BORDER_COLOR;
        mCircleBorderWidth = SizeUtils.dp2Px(getContext(), DEF_CIRCLE_BORDER_WIDTH_IN_DP);
        mCircleOuterRadius = mDefSize / 2;
        mInnerPieColor = DEF_INNER_PIE_COLOR;
        mInnerPieMaxProgress = DEF_MAX_PROGRESS;
        mInnerPieProgress = DEF_PROGRESS;
        mInnerPieStartingAngle = DEF_PIE_STARGING_ANGLE;
    }

    private void initCustomAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) {
            return;
        }

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressCircle, defStyleAttr, 0);
        int indexCount = a.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int index = a.getIndex(i);

            if (index == R.styleable.ProgressCircle_circleBorderColor) {
                mCircleBorderColor = a.getColor(index, DEF_CIRCLE_BORDER_COLOR);
            }
            if (index == R.styleable.ProgressCircle_circleBorderWidth) {
                mCircleBorderWidth = a.getDimension(index, mCircleBorderWidth);
            }
            if (index == R.styleable.ProgressCircle_circleOuterRadius) {
                mCircleOuterRadius = a.getDimension(index, mCircleOuterRadius);
            }
            if (index == R.styleable.ProgressCircle_innerPieColor) {
                mInnerPieColor = a.getColor(index, DEF_INNER_PIE_COLOR);
            }
            if (index == R.styleable.ProgressCircle_innerPieMaxProgress) {
                mInnerPieMaxProgress = a.getInt(index, DEF_MAX_PROGRESS);
            }
            if (index == R.styleable.ProgressCircle_innerPieProgress) {
                mInnerPieProgress = a.getInt(index, DEF_PROGRESS);
            }
            if (index == R.styleable.ProgressCircle_innerPieStartingAngle) {
                mInnerPieStartingAngle = a.getFloat(index, DEF_PIE_STARGING_ANGLE);
            }
        }

        a.recycle();
    }

    private void initActualValues() {
        mCircleRadius = mCircleOuterRadius - mCircleBorderWidth / 2;
    }

    private void initPaint() {
        initBorderPaint();
        initContentPaint();
        initBackgroundPaint();
    }

    private void initBorderPaint() {
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(mCircleBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mCircleBorderWidth);
    }

    private void initBackgroundPaint() {
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(Color.BLACK);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setStrokeWidth(mCircleBorderWidth);
    }

    private void initContentPaint() {
        mContentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mContentPaint.setColor(mInnerPieColor);
        mContentPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        recalcValues();
        drawCircle(canvas);
        drawInnerPie(canvas);
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(mHalfSize, mHalfSize, mCircleRadius, mBackgroundPaint);
        canvas.drawCircle(mHalfSize, mHalfSize, mCircleRadius, mBorderPaint);
    }

    private void drawInnerPie(Canvas canvas) {
        if (mInnerArcRectF == null) {
            float innerCircleOffset = mHalfSize - mCircleInnerRadius;
            mInnerArcRectF = new RectF(innerCircleOffset, innerCircleOffset, getWidth() - innerCircleOffset,
                getHeight() - innerCircleOffset);
        }
        float sweepAngle = 1.0f * 360 * mInnerPieProgress / mInnerPieMaxProgress;
        canvas.drawArc(mInnerArcRectF, mInnerPieStartingAngle, sweepAngle, true, mContentPaint);
    }

    private void recalcValues() {
        mHalfSize = getWidth() >> 1;
        mCircleOuterRadius = Math.min(mHalfSize, mCircleOuterRadius);
        mCircleBorderWidth = Math.min(mCircleOuterRadius, mCircleBorderWidth);

        mCircleRadius = mCircleOuterRadius - mCircleBorderWidth / 2;
        mCircleInnerRadius = mCircleRadius - mCircleBorderWidth / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int measuredWidth = widthSpecSize;
        int measuredHeight = heightSpecSize;

        if (widthSpecMode == MeasureSpec.AT_MOST) {
            measuredWidth = (int) (mDefSize + 0.5f);
        }
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            measuredHeight = (int) (mDefSize + 0.5f);
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}