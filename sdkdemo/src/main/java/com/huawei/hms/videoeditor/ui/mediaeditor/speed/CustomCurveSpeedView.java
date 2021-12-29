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

package com.huawei.hms.videoeditor.ui.mediaeditor.speed;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVESpeedCurvePoint;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.ArrayUtils;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;

public class CustomCurveSpeedView extends View {
    private static final String TAG = "CustomCurveSpeedView";

    private List<Point> mSpeedPoints = new ArrayList<>();

    private List<HVESpeedCurvePoint> mItems = new ArrayList<>();

    private static int padding = ScreenUtil.dp2px(16);

    private static int viewHeight = ScreenUtil.dp2px(167);

    private static int lineWidth = ScreenUtil.dp2px(1);

    private static int pointRadius = ScreenUtil.dp2px(10);

    private static int pointRadius2 = ScreenUtil.dp2px(8);

    private CustomCurveCallBack customCurveCallBack;

    private int mCurrentX = padding;

    private int isTouchInIndex = -1;

    private Point lastDownPoint = new Point();

    public CustomCurveSpeedView(Context context) {
        super(context);
    }

    public CustomCurveSpeedView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void init(HVEAsset asset, List<HVESpeedCurvePoint> speedPoints) {
        mItems = speedPoints;
        postInvalidate();
    }

    public void refresh(List<HVESpeedCurvePoint> speedPoints) {
        mItems = speedPoints;
        mSpeedPoints.clear();
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(widthMeasureSpec, viewHeight + pointRadius * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSpeedPoints.size() == 0 && !ArrayUtils.isEmpty(mItems)) {
            for (HVESpeedCurvePoint item : mItems) {
                mSpeedPoints.add(getPointXY(item));
            }
        }
        drawScale(canvas);
        drawBezierPath(canvas);
        drawPoint(canvas);
    }

    private void drawScale(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#4DFFFFFF"));
        paint.setStrokeWidth(lineWidth);
        paint.setStyle(Paint.Style.STROKE);
        RectF frameRect = new RectF(padding + lineWidth / 2, lineWidth / 2 + pointRadius,
            getMeasuredWidth() - lineWidth - padding, viewHeight - lineWidth + pointRadius);
        canvas.drawRect(frameRect, paint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#66FFFFFF"));

        textPaint.setTypeface(Typeface.create("HarmonyHeiTi", Typeface.NORMAL));
        textPaint.setTextSize(ScreenUtil.dp2px(10));
        float x = getMeasuredWidth() - ScreenUtil.dp2px(40);

        canvas.drawText("10x", x, ScreenUtil.dp2px(20), textPaint);
        canvas.drawText(" 1x", x, ScreenUtil.dp2px(12) + viewHeight / 2, textPaint);
        canvas.drawText("0.1x", x, viewHeight, textPaint);

        canvas.drawLine(padding, viewHeight / 2 + pointRadius, getMeasuredWidth() - padding * 2 - ScreenUtil.dp2px(20),
            viewHeight / 2 + pointRadius, paint);
        paint.setPathEffect(new DashPathEffect(new float[] {10, 10}, 0));
        canvas.drawLine(padding, viewHeight / 4 + pointRadius, getMeasuredWidth() - padding * 2,
            viewHeight / 4 + pointRadius, paint);
        canvas.drawLine(padding, viewHeight / 4 * 3 + pointRadius, getMeasuredWidth() - padding * 2,
            viewHeight / 4 * 3 + pointRadius, paint);

    }

    private void drawPoint(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        Paint paint2 = new Paint();
        paint2.setColor(Color.BLACK);
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        int index = -1;
        for (int i = 0; i < mSpeedPoints.size(); i++) {
            Point point = mSpeedPoints.get(i);
            canvas.drawCircle(point.x, point.y, pointRadius, paint);
            if (mCurrentX >= point.x - pointRadius2 && mCurrentX <= point.x + pointRadius2) {
                index = i;
                continue;
            }
            canvas.drawCircle(point.x, point.y, pointRadius2, paint2);
        }
        if (customCurveCallBack != null) {
            customCurveCallBack.isOnPoint(index);
        }
        paint.setStrokeWidth(lineWidth);
        canvas.drawLine(mCurrentX, pointRadius, mCurrentX, viewHeight + pointRadius, paint);
    }

    private void drawBezierPath(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStrokeWidth(lineWidth);
        paint.setColor(Color.parseColor("#FFB662D9"));
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < mSpeedPoints.size(); i++) {
            if (i + 1 == mSpeedPoints.size()) {
                return;
            }

            Point start = mSpeedPoints.get(i);
            Point end = mSpeedPoints.get(i + 1);
            Point control1 = new Point();
            control1.x = start.x + (end.x - start.x) / 2;
            control1.y = start.y;
            Point control2 = new Point();
            control2.x = start.x + (end.x - start.x) / 2;
            control2.y = end.y;

            Path mBezierPath = new Path();
            mBezierPath.moveTo(start.x, start.y);
            mBezierPath.cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y);
            canvas.drawPath(mBezierPath, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isTouchInIndex == -1) {
                    checkTouchInPoint(event);
                } else {
                    mCurrentX = (int) event.getX();
                    if (customCurveCallBack != null) {
                        customCurveCallBack.lineChanged(xToTime(mCurrentX));
                    }
                    if (mCurrentX > padding && mCurrentX < getWidth() - padding) {
                        postInvalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isTouchInIndex = -1;
                if (customCurveCallBack != null) {
                    customCurveCallBack.touchUp();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouchInIndex != -1) {
                    movePoint(event);
                } else {
                    mCurrentX = (int) event.getX();
                    if (customCurveCallBack != null) {
                        customCurveCallBack.lineChanged(xToTime(mCurrentX));
                    }
                    if (mCurrentX > padding && mCurrentX < getWidth() - padding) {
                        postInvalidate();
                    }
                }
                break;
            default:
                SmartLog.d(TAG, "onTouch run in default case");
                break;
        }
        return true;
    }

    private void checkTouchInPoint(MotionEvent event) {
        for (int i = 0; i < mSpeedPoints.size(); i++) {
            double distance =
                Math.sqrt(Math.abs((mSpeedPoints.get(i).x - event.getX()) * (mSpeedPoints.get(i).x - event.getX())
                    + (mSpeedPoints.get(i).y - event.getY()) * (mSpeedPoints.get(i).y - event.getY())));
            if (BigDecimalUtils.compareTo(pointRadius, distance)) {
                isTouchInIndex = i;
                lastDownPoint.x = (int) event.getX();
                lastDownPoint.y = (int) event.getY();
                mCurrentX = mSpeedPoints.get(i).x;
                if (customCurveCallBack != null) {
                    customCurveCallBack.lineChanged(xToTime(mCurrentX));
                }
                return;
            }
        }
    }

    private void movePoint(MotionEvent event) {
        if (mSpeedPoints == null) {
            SmartLog.e(TAG, "[movePoint] mSpeedPoints is null");
            return;
        }
        if (isTouchInIndex >= mSpeedPoints.size() || isTouchInIndex < 0) {
            SmartLog.e(TAG, "[movePoint] isTouchInIndex is out list size");
            return;
        }
        Point point = mSpeedPoints.get(isTouchInIndex);
        boolean isChange = false;
        if (isTouchInIndex == 0 || isTouchInIndex == mSpeedPoints.size() - 1) {
            if ((int) (point.y + event.getY() - lastDownPoint.y) >= pointRadius
                && (int) (point.y + event.getY() - lastDownPoint.y) <= viewHeight + pointRadius) {
                point.y = (int) (point.y + event.getY() - lastDownPoint.y);
                isChange = true;
            }
        } else {
            if ((int) (point.y + event.getY() - lastDownPoint.y) >= pointRadius
                && (int) (point.y + event.getY() - lastDownPoint.y) <= viewHeight + pointRadius) {
                point.y = (int) (point.y + event.getY() - lastDownPoint.y);
                isChange = true;
            }
            if (!(isTouchInIndex - 1 >= mSpeedPoints.size() || isTouchInIndex - 1 < 0)) {
                if ((int) (point.x + event.getX() - lastDownPoint.x) >= mSpeedPoints.get(isTouchInIndex - 1).x
                    + pointRadius
                    && (int) (point.x + event.getX() - lastDownPoint.x) <= mSpeedPoints.get(isTouchInIndex + 1).x
                        - pointRadius) {
                    point.x = (int) (point.x + event.getX() - lastDownPoint.x);
                    isChange = true;
                }
            }
        }
        lastDownPoint.x = (int) event.getX();
        lastDownPoint.y = (int) event.getY();
        if (isChange) {
            mCurrentX = point.x;
            if (customCurveCallBack != null) {
                customCurveCallBack.lineChanged(xToTime(mCurrentX));
                customCurveCallBack.pointChanged(isTouchInIndex, xToTime(mSpeedPoints.get(isTouchInIndex).x),
                    yToSpeed(mSpeedPoints.get(isTouchInIndex).y));
            }
            postInvalidate();
        }
    }

    private Point getPointXY(HVESpeedCurvePoint item) {
        Point point = new Point();
        point.x = padding
            + (int) BigDecimalUtils.round(BigDecimalUtils.mul(item.timeFactor, getMeasuredWidth() - padding * 2), 0);
        if (item.speed > 1) {
            point.y =
                viewHeight / 2
                    - (int) BigDecimalUtils
                        .round(BigDecimalUtils.mul(BigDecimalUtils.div(item.speed, 10), viewHeight / 2D), 0)
                    + pointRadius;
        } else if (item.speed == 1) {
            point.y = viewHeight / 2 + pointRadius;
        } else {
            point.y = viewHeight / 2
                + (int) BigDecimalUtils
                    .round(BigDecimalUtils.mul(BigDecimalUtils.div(1 - item.speed, 1), viewHeight / 2D), 0)
                + pointRadius;
        }
        return point;
    }

    private float yToSpeed(int y) {
        float speed = 1;
        if (y < viewHeight / 2 + pointRadius) {
            speed = Double
                .valueOf(
                    BigDecimalUtils.mul(BigDecimalUtils.div(viewHeight / 2D - y + pointRadius, viewHeight / 2D), 10))
                .floatValue();

        } else if (y > viewHeight / 2 + pointRadius) {
            speed = Double.valueOf(BigDecimalUtils.div(viewHeight - y + pointRadius, viewHeight / 2D)).floatValue();
        }
        return speed;
    }

    private float xToTime(int mCurrentX) {
        return Double.valueOf(BigDecimalUtils.div(mCurrentX - padding, getMeasuredWidth() - padding * 2)).floatValue();

    }

    public void setCustomCurveCallBack(CustomCurveCallBack customCurveCallBack) {
        this.customCurveCallBack = customCurveCallBack;
    }

    public interface CustomCurveCallBack {
        void lineChanged(float timeFactor);

        void pointChanged(int index, float timeFactor, float speed);

        void isOnPoint(int index);

        void touchUp();
    }

}
