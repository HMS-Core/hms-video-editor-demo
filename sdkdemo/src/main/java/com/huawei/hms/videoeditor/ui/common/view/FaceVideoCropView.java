
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

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class FaceVideoCropView extends View {
    private static final String TAG = "FaceVideoCropView";

    private static final int MIN_SELECT_TIME = 600;

    private static final int MAX_SELECT_TIME = 3 * 60 * 1000;

    private final int frameWidth = ScreenUtil.dp2px(18);

    private final int halfFrameWidth = ScreenUtil.dp2px(9);

    protected int adsorbWidth = ScreenUtil.dp2px(3);

    private final int maxWidth;

    private Paint paint;

    private Paint linePaint;

    private double startMovedOffset;

    private double endMovedOffset;

    private long maxDuration = 0;

    private long lastTrimIn = 0;

    private long lastTrimOut = 0;

    protected int lastAdsorbX = -1;

    private long trimIn = 0;

    private long trimOut = 0;

    private int isCanMove = -1;

    private float lengthOld = 0;

    private final int colorRect = Color.WHITE;

    private final float lineWidth = ScreenUtil.dp2px(3);

    private final int lineHeight = ScreenUtil.dp2px(14);

    private CutVideoResult mCutVideoResult;

    private AssetLocation location;

    private Context mContext;

    private boolean isHaveLast;

    private Bitmap logo;

    public FaceVideoCropView(Context context) {
        super(context);
        mContext = context;
        maxWidth = ScreenUtil.getScreenWidth(context);
    }

    public FaceVideoCropView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        maxWidth = ScreenUtil.getScreenWidth(context);
    }

    public FaceVideoCropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        maxWidth = ScreenUtil.getScreenWidth(context);
    }

    public void init(long maxDuration, long trimIn, long trimOut) {
        this.maxDuration = maxDuration;
        this.trimIn = trimIn;
        this.trimOut = trimOut;
        this.startMovedOffset = timeToX(trimIn);
        this.endMovedOffset = timeToX(trimOut);
        paint = new Paint();
        paint.setColor(colorRect);
        paint.setAntiAlias(true);

        linePaint = new Paint();
        int colorStoke = Color.BLACK;
        linePaint.setColor(colorStoke);
        linePaint.setAntiAlias(true);
    }

    public void setLastTrim(long trimIn, long trimOut) {
        isHaveLast = true;
        this.lastTrimIn = trimIn;
        this.lastTrimOut = trimOut;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(maxWidth, ScreenUtil.dp2px(67));
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
                    getParent().requestDisallowInterceptTouchEvent(true);
                    if (checkActionLocation(event.getX(0))) {
                        if (mCutVideoResult != null) {
                            mCutVideoResult.isInCut();
                        }
                        lengthOld = calculation(event);
                    } else {
                        lengthOld = 0;
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "ACTION_UP");
                if (event.getPointerCount() == 1) {
                    if (isCanMove != -1 && mCutVideoResult != null) {
                        mCutVideoResult.cutResult(trimIn, trimOut);
                    }
                    isCanMove = -1;
                    lengthOld = 0;
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                SmartLog.d(TAG, "ACTION_MOVE：" + event.getX(0));
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

                if (isCanMove == 2) {
                    float x = calculation(event);
                    cutVideo((int) (x - lengthOld), isCanMove);
                    lengthOld = x;
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void drawRect(Canvas canvas) {
        if (canvas == null) {
            return;
        }
        Log.d(TAG, "drawRect ");
        int startX = getStartX();
        float mWidth = (float) (startX + getRealWidth() + frameWidth * 2);
        Log.e(TAG, "startX= " + startX + ", mWidth= " + mWidth + ", realWidth= " + getRealWidth());
        float height = getMeasuredHeight();
        float lineIntervalWidth = ScreenUtil.dp2px(1);
        float lineStartPadding = (float) (startX + ScreenUtil.dp2px(10));

        float lineEndPadding = (float) (ScreenUtil.dp2px(9));

        Paint blackPaint = new Paint();
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        blackPaint.setColor(ContextCompat.getColor(getContext(), R.color.ai_blank_80));
        RectF rBlack1 = new RectF(0, 0, startX + frameWidth, (int) height);
        RectF rBlack2 = new RectF((int) mWidth - frameWidth, 0, getWidth(), (int) height);
        canvas.drawRoundRect(rBlack1, 5, 5, blackPaint);
        canvas.drawRoundRect(rBlack2, 5, 5, blackPaint);

        if (isHaveLast) {
            Paint purplePaint = new Paint();
            purplePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            purplePaint.setColor(ContextCompat.getColor(getContext(), R.color.purple_50));
            Rect purpleRect = new Rect((int) (timeToX(lastTrimIn) + frameWidth), 0,
                (int) (maxWidth - frameWidth - timeToX(lastTrimOut)), (int) height);
            canvas.drawRect(purpleRect, purplePaint);
            if (logo == null || logo.isRecycled()) {
                logo = BitmapFactory.decodeResource(getResources(), R.drawable.icon_renlian_track);
            }
            canvas.drawBitmap(logo, (float) (timeToX(lastTrimIn) + frameWidth + ScreenUtil.dp2px(5)),
                ScreenUtil.dp2px(5), purplePaint);
        }

        if (paint != null) {
            RectF rectF = new RectF(startX, 0, startX + frameWidth, height);
            canvas.drawRoundRect(rectF, 20, 20, paint);
            RectF rectF2 = new RectF(mWidth - frameWidth, 0, mWidth, height);
            canvas.drawRoundRect(rectF2, 20, 20, paint);

            Rect r1 = new Rect(startX + halfFrameWidth, 0, startX + frameWidth, (int) height);
            canvas.drawRect(r1, paint);
            Rect r2 = new Rect((int) (mWidth - frameWidth), 0, (int) (mWidth - halfFrameWidth), (int) height);
            canvas.drawRect(r2, paint);
        }

        Paint rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(ScreenUtil.dp2px(2));
        rectPaint.setColor(colorRect);
        Rect rec3 = new Rect(startX + frameWidth, ScreenUtil.dp2px(1) - 1, (int) (mWidth - frameWidth),
            (int) height - ScreenUtil.dp2px(1) + 1);
        canvas.drawRect(rec3, rectPaint);

        if (linePaint != null) {
            RectF rec1 = new RectF(lineStartPadding, (height - lineHeight) / 2, lineStartPadding + lineWidth,
                (height + lineHeight) / 2);
            canvas.drawRoundRect(rec1, lineWidth / 2, lineWidth / 2, linePaint);

            RectF rect3 = new RectF(mWidth - lineEndPadding - lineWidth - lineIntervalWidth, (height - lineHeight) / 2,
                mWidth - lineEndPadding - lineIntervalWidth, (height + lineHeight) / 2);

            canvas.drawRoundRect(rect3, lineWidth / 2, lineWidth / 2, linePaint);
        }
    }

    private boolean checkActionLocation(float x) {
        SmartLog.d(TAG, "x= " + x + ", getRealWidth= " + getRealWidth() + ", getStartX= " + getStartX());
        if (x < getStartX() + frameWidth && x > getStartX()) {
            SmartLog.d(TAG, "checkActionLocation 左边框 isTrue");
            isCanMove = 0;
            getAdsorbLocation();
            return true;
        }
        if (x > getStartX() + getRealWidth() + frameWidth && x < getStartX() + getRealWidth() + frameWidth * 2) {
            SmartLog.d(TAG, "checkActionLocation 右边框 isTrue");
            isCanMove = 1;
            getAdsorbLocation();
            return true;
        }

        if (x > getStartX() + frameWidth && x < getStartX() + getRealWidth() + frameWidth) {
            SmartLog.d(TAG, "checkActionLocation 中间区域");
            isCanMove = 2;
            getAdsorbLocation();
            return true;
        }
        return false;
    }

    private float calculation(MotionEvent event) {
        return event.getX();
    }

    private int getStartX() {
        return (int) startMovedOffset;
    }

    public double getRealWidth() {
        return maxWidth - frameWidth * 2 - startMovedOffset - endMovedOffset;
    }

    protected void cutVideo(int offset, int isCanMove) {
        if (isCanMove == 2) {
            double startMovedTemp = startMovedOffset + offset;
            double endMovedTemp = endMovedOffset - offset;

            SmartLog.d(TAG, "startMovedTemp= " + startMovedTemp + ", endMovedTemp= " + endMovedTemp);
            if (startMovedTemp < 0 || endMovedTemp < 0) {
                return;
            }
            startMovedOffset = startMovedTemp;
            endMovedOffset = endMovedTemp;
            trimIn = xToTime(startMovedOffset);
            trimOut = xToTime(endMovedOffset);
        } else {
            long time = xToTime(offset);
            if (maxDuration - trimIn - trimOut - time < MIN_SELECT_TIME) {
                if (isCanMove == 0) {
                    trimIn = maxDuration - trimOut - MIN_SELECT_TIME;
                    startMovedOffset = timeToX(trimIn);
                }
                if (isCanMove == 1) {
                    trimOut = maxDuration - trimIn - MIN_SELECT_TIME;
                    endMovedOffset = timeToX(trimOut);
                }
                postInvalidate();
            } else if (maxDuration - trimIn - trimOut - time > MAX_SELECT_TIME) {
                if (isCanMove == 0) {
                    trimIn = maxDuration - trimOut - MAX_SELECT_TIME;
                    startMovedOffset = timeToX(trimIn);
                }
                if (isCanMove == 1) {
                    trimOut = maxDuration - trimIn - MAX_SELECT_TIME;
                    endMovedOffset = timeToX(trimOut);
                }
                postInvalidate();
            } else if (isCanMove == 0) {
                if (time < 0 && trimIn + time < 0) {
                    return;
                }
                startMovedOffset += offset;
                trimIn = xToTime(startMovedOffset);
            } else if (isCanMove == 1) {
                if (time < 0 && trimOut + time < 0) {
                    return;
                }
                endMovedOffset += offset;
                trimOut = xToTime(endMovedOffset);
            }
        }
        if (mCutVideoResult != null) {
            mCutVideoResult.cut(trimIn, trimOut, isCanMove);
        }

        boolean isSlideLeft = false;
        if (isCanMove == 0) {
            isSlideLeft = offset < 0;
            if (lastAdsorbX > 0 && Math.abs(getStartX() - lastAdsorbX) < adsorbWidth
                || Math.abs(getStartX() + frameWidth - lastAdsorbX) < adsorbWidth) {
                return;
            } else {
                lastAdsorbX = -1;
            }
        } else if (isCanMove == 1) {
            isSlideLeft = offset > 0;
            if (lastAdsorbX > 0 && Math.abs(getStartX() + getRealWidth() + frameWidth * 2 - lastAdsorbX) < adsorbWidth
                || Math.abs(getStartX() + getRealWidth() + frameWidth - lastAdsorbX) < adsorbWidth) {
                return;
            } else {
                lastAdsorbX = -1;
            }
        }

        handleAdsorb(isSlideLeft, isCanMove);
        postInvalidate();
    }

    private void handleAdsorb(boolean isSlideLeft, int isCanMove) {
        double offset;
        if (isSlideLeft) {
            if (isCanMove == 0) {
                offset = getStartX() + frameWidth - location.startX;
                if (offset > 0 && offset < adsorbWidth) {
                    startMovedOffset -= offset;
                    lastAdsorbX = location.startX;
                    vibrate();
                    return;
                }
                offset = getStartX() - location.endX;
                if (offset > 0 && offset < adsorbWidth) {
                    startMovedOffset -= offset;
                    lastAdsorbX = location.endX;
                    vibrate();
                    return;
                }
            }
            if (isCanMove == 1) {
                offset = getStartX() + getRealWidth() + frameWidth * 2 - location.startX;
                if (offset > 0 && offset < adsorbWidth) {
                    endMovedOffset += offset;
                    lastAdsorbX = location.startX;
                    vibrate();
                    return;
                }
                offset = getStartX() + getRealWidth() + frameWidth - location.endX;
                if (offset > 0 && offset < adsorbWidth) {
                    endMovedOffset += offset;
                    lastAdsorbX = location.endX;
                    vibrate();
                }
            }
        } else {
            if (isCanMove == 0) {
                offset = location.startX - getStartX() - frameWidth;
                if (offset > 0 && offset < adsorbWidth) {
                    startMovedOffset += offset;
                    lastAdsorbX = location.startX;
                    vibrate();
                    return;
                }
                offset = location.endX - getStartX();
                if (offset > 0 && offset < adsorbWidth) {
                    startMovedOffset += offset;
                    lastAdsorbX = location.endX;
                    vibrate();
                    return;
                }
            }

            if (isCanMove == 1) {
                offset = location.startX - (getStartX() + getRealWidth() + frameWidth * 2);
                if (offset > 0 && offset < adsorbWidth) {
                    endMovedOffset -= offset;
                    lastAdsorbX = location.startX;
                    vibrate();
                    return;
                }
                offset = location.endX - (getStartX() + getRealWidth() + frameWidth);
                if (offset > 0 && offset < adsorbWidth) {
                    endMovedOffset -= offset;
                    lastAdsorbX = location.endX;
                    vibrate();
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void vibrate() {
    }

    private void getAdsorbLocation() {
        location = new AssetLocation((int) (timeToX(lastTrimIn) + frameWidth),
            (int) (maxWidth - frameWidth - timeToX(lastTrimOut)), lastTrimIn, lastTrimOut);
    }

    private double timeToX(long time) {
        return BigDecimalUtils.mul(BigDecimalUtils.div(time, maxDuration), maxWidth - frameWidth * 2);
    }

    private long xToTime(double x) {
        return (long) BigDecimalUtils
            .round(BigDecimalUtils.mul(maxDuration, BigDecimalUtils.div(x, maxWidth - frameWidth * 2)), 0);
    }

    public interface CutVideoResult {
        void isInCut();

        void cut(long trimIn, long trimOut, int cutType);

        void cutResult(long trimIn, long trimOut);
    }

    public void setCutVideoListener(CutVideoResult cutVideoListener) {
        this.mCutVideoResult = cutVideoListener;
    }

    private static class AssetLocation {
        public int startX;

        public int endX;

        public long startTime;

        public long endTime;

        AssetLocation(int startX, int endX, long startTime, long endTime) {
            this.startX = startX;
            this.endX = endX;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public String toString() {
            return "AssetLocation{" + "startX=" + startX + ", endX=" + endX + ", startTime=" + startTime + ", endTime="
                + endTime + '}';
        }
    }
}
