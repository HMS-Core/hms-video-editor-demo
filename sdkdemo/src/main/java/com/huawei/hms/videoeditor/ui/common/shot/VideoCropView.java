
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

package com.huawei.hms.videoeditor.ui.common.shot;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class VideoCropView extends View {
    private static final String TAG = "VideoCropView";

    private final int frameWidth = ScreenUtil.dp2px(18);

    private final int halfFrameWidth = ScreenUtil.dp2px(9);

    private int maxWidth = 0;

    private Paint paint;

    private Paint blackPaint;

    private Paint linePaint;

    private long cutTime = 0;

    private double startMovedOffset;

    private double endMovedOffset;

    private double maxDuration = 0;

    private long duration = 0;

    private long trimIn = 0;

    private long trimOut = 0;

    private int isCanMove = -1;

    private float lengthOld = 0;

    private final int colorRect = Color.WHITE;

    private final float lineWidth = ScreenUtil.dp2px(3);

    private final int lineHeight = ScreenUtil.dp2px(14);

    private CutVideoResult mCutVideoResult;

    private Context mContext;

    public VideoCropView(Context context) {
        super(context);
        mContext = context;
        maxWidth = ScreenUtil.getScreenWidth(context);
    }

    public VideoCropView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        maxWidth = ScreenUtil.getScreenWidth(context);
    }

    public VideoCropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        maxWidth = ScreenUtil.getScreenWidth(context);
    }

    public void resetFactor(double startMovedOffset, double endMovedOffset, long cutTime, long trimIn, long trimOut,
        long duration) {
        this.startMovedOffset = startMovedOffset;
        this.endMovedOffset = endMovedOffset;
        this.cutTime = cutTime;
        this.trimIn = trimIn;
        this.trimOut = trimOut;
        this.duration = duration;
        this.post(this::invalidate);
    }

    public void init(long duration, long maxDuration, long trimIn, long trimOut) {
        this.duration = duration;
        this.maxDuration = maxDuration;
        this.trimIn = trimIn;
        this.trimOut = trimOut;

        paint = new Paint();
        paint.setColor(colorRect);
        paint.setAntiAlias(true);

        blackPaint = new Paint();
        blackPaint.setColor(ContextCompat.getColor(getContext(), R.color.translucent_black_45));
        blackPaint.setAntiAlias(true);

        linePaint = new Paint();
        int colorStoke = Color.BLACK;
        linePaint.setColor(colorStoke);
        linePaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(maxWidth, ScreenUtil.dp2px(50));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setHotRect();
        drawRect(canvas);
    }

    private void setHotRect() {
        if (Build.VERSION.SDK_INT < 29) {
            SmartLog.w(TAG, "android sdk version is too low");
            return;
        }
        List<Rect> rects = new ArrayList<>();
        rects.add(new Rect(0, 0, ScreenUtil.dp2px(24), getHeight()));
        rects.add(new Rect(ScreenUtil.getScreenWidth(mContext) - ScreenUtil.dp2px(24), 0,
            ScreenUtil.getScreenWidth(mContext), getHeight()));
        setSystemGestureExclusionRects(rects);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 1) {
                    if (checkActionLocation(event.getX(0))) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        if (mCutVideoResult != null) {
                            mCutVideoResult.isInCut();
                        }
                        lengthOld = calculation(event);
                        return true;
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        lengthOld = 0;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 1) {
                    if (mCutVideoResult != null) {
                        mCutVideoResult.cutResult(cutTime, isCanMove);
                    }
                    isCanMove = -1;
                    lengthOld = 0;
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                SmartLog.i(TAG, "ACTION_MOVE：" + event.getX(0));
                if (isCanMove == 0) {
                    float x = calculation(event);
                    SmartLog.i(TAG, "ACTION_MOVE：" + isCanMove);
                    cutVideo((int) (x - lengthOld), isCanMove);
                    lengthOld = x;
                    return true;
                }
                if (isCanMove == 1) {
                    float x = calculation(event);
                    cutVideo((int) (lengthOld - x), isCanMove);
                    lengthOld = x;
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void drawRect(Canvas canvas) {
        if (canvas == null || paint == null || linePaint == null || blackPaint == null) {
            return;
        }
        float mWidth = (float) (getStartX() + getRealWidth() + frameWidth * 2);
        int startX = getStartX();
        float height = getMeasuredHeight();
        float lineIntervalWidth = ScreenUtil.dp2px(1);
        float lineStartPadding = (float) (getStartX() + ScreenUtil.dp2px(10));

        float lineEndPadding = (float) (ScreenUtil.dp2px(9));
        RectF rBlack1 = new RectF(0, 0, startX + 20, (int) height);
        RectF rBlack2 = new RectF((int) mWidth - 20, 0, getWidth(), (int) height);
        canvas.drawRoundRect(rBlack1, 0, 0, blackPaint);
        canvas.drawRoundRect(rBlack2, 0, 0, blackPaint);

        RectF rectF = new RectF(startX, 0, startX + frameWidth, height);
        canvas.drawRoundRect(rectF, 20, 20, paint);
        RectF rectF2 = new RectF(mWidth - frameWidth, 0, mWidth, height);
        canvas.drawRoundRect(rectF2, 20, 20, paint);

        Rect r1 = new Rect(startX + halfFrameWidth, 0, startX + frameWidth, (int) height);
        canvas.drawRect(r1, paint);
        Rect r2 = new Rect((int) (mWidth - frameWidth), 0, (int) (mWidth - halfFrameWidth), (int) height);
        canvas.drawRect(r2, paint);

        Paint rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(ScreenUtil.dp2px(2));
        rectPaint.setColor(colorRect);
        Rect rec3 = new Rect(startX + frameWidth, ScreenUtil.dp2px(1) - 1, (int) (mWidth - frameWidth),
            (int) height - ScreenUtil.dp2px(1) + 1);
        canvas.drawRect(rec3, rectPaint);
        RectF rec1 = new RectF(lineStartPadding, (height - lineHeight) / 2, lineStartPadding + lineWidth,
            (height + lineHeight) / 2);
        canvas.drawRoundRect(rec1, lineWidth / 2, lineWidth / 2, linePaint);

        RectF rect3 = new RectF(mWidth - lineEndPadding - lineWidth - lineIntervalWidth, (height - lineHeight) / 2,
            mWidth - lineEndPadding - lineIntervalWidth, (height + lineHeight) / 2);

        canvas.drawRoundRect(rect3, lineWidth / 2, lineWidth / 2, linePaint);
    }

    private boolean checkActionLocation(float x) {
        if (x < getStartX() + frameWidth && x > getStartX()) {
            SmartLog.i(TAG, "checkActionLocation isTrue ");
            isCanMove = 0;
            return true;
        }
        if (x > getStartX() + getRealWidth() + frameWidth && x < getStartX() + getRealWidth() + frameWidth * 2) {
            isCanMove = 1;
            return true;
        }
        return false;
    }

    private float calculation(MotionEvent event) {
        float x1 = event.getX();
        SmartLog.i(TAG, "calculation x1: " + x1);
        return x1;
    }

    private int getStartX() {
        double percent = BigDecimalUtils.div(trimIn, maxDuration);
        return (int) (BigDecimalUtils.mul(percent, maxWidth - frameWidth * 2) + startMovedOffset);
    }

    public double getRealWidth() {
        double percent = BigDecimalUtils.div(duration, maxDuration);
        return BigDecimalUtils.mul(percent, maxWidth - frameWidth * 2) - startMovedOffset - endMovedOffset;
    }

    protected void cutVideo(int offset, int isCanMove) {
        long time = (long) BigDecimalUtils.mul(maxDuration, BigDecimalUtils.div(offset, maxWidth - frameWidth * 2));
        if (maxDuration - trimIn - trimOut - cutTime - time <= 100) {
            return;
        }
        if (isCanMove == 0) {
            if (time < 0 && trimIn + cutTime + time < 0) {
                return;
            }
            startMovedOffset += offset;
            cutTime = (long) BigDecimalUtils.mul(maxDuration,
                BigDecimalUtils.div(startMovedOffset, maxWidth - frameWidth * 2));
        } else {
            if (time < 0 && trimOut + cutTime + time < 0) {
                return;
            }
            endMovedOffset += offset;
            cutTime =
                (long) BigDecimalUtils.mul(maxDuration, BigDecimalUtils.div(endMovedOffset, maxWidth - frameWidth * 2));
        }
        if (mCutVideoResult != null) {
            mCutVideoResult.cut(cutTime);
        }
        this.post(this::invalidate);
    }

    public interface CutVideoResult {
        void isInCut();

        void cut(long time);

        void cutResult(long cutTime, int isCanMove);
    }

    public void setCutVideoListener(CutVideoResult cutVideoListener) {
        this.mCutVideoResult = cutVideoListener;
    }
}
