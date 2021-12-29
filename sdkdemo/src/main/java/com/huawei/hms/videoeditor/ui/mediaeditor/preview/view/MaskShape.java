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

package com.huawei.hms.videoeditor.ui.mediaeditor.preview.view;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.huawei.hms.videoeditor.sdk.bean.HVEPosition2D;
import com.huawei.hms.videoeditor.ui.common.utils.MatrixUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;

public abstract class MaskShape<T extends MaskShape.ParameterSet> {
    protected int max_distance = 60;

    protected int cycleR = 8;

    protected int icoWidthHalf = 16;

    protected int width;

    protected int height;

    protected int videoWidth;

    protected int videoHeight;

    protected WeakReference<Context> contextWeakReference;

    public ParameterSetChange parameterSetChange;

    private PaintFlagsDrawFilter paintFlagsDrawFilter;

    protected T aParameterSet;

    protected Paint testPaint;

    public int blurMuti = 3;

    protected Rect pipRect;

    public MaskShape(Context context) {
        contextWeakReference = new WeakReference<>(context);
    }

    public void init(int width, int height, List<HVEPosition2D> mList, int videoWidth, int videoHeight,
        float maskCenterx, float maskCentery, float pipRotate, float maskRotate, float blur) {
        this.width = width;
        this.height = height;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;

        testPaint = new Paint();
        testPaint.setColor(Color.YELLOW);
        testPaint.setStrokeWidth(1);
        testPaint.setStyle(Paint.Style.STROKE);

        Matrix matrixPip = aParameterSet.getPipMatrix();
        Matrix matrixMask = aParameterSet.getMaskMatrix();

        matrixPip.postScale(1, -1, 0, 0);
        matrixPip.postTranslate(mList.get(0).xPos, mList.get(0).yPos);
        matrixPip.postRotate(-pipRotate, mList.get(0).xPos, mList.get(0).yPos);

        pipRect = new Rect(0, videoHeight, videoWidth, 0);

        aParameterSet.position.x = (int) (maskCenterx * videoWidth);
        aParameterSet.position.y = (int) (maskCentery * videoHeight);

        aParameterSet.maskPosition.x = maskCenterx;
        aParameterSet.maskPosition.y = maskCentery;

        float[] p = new float[] {aParameterSet.position.x, aParameterSet.position.y};
        matrixPip.mapPoints(p);
        matrixMask.postRotate(-maskRotate, p[0], p[1]);
        aParameterSet.updateRotation(matrixMask);
        aParameterSet.updateBlur(blur);

        icoWidthHalf = ScreenUtil.dp2px(icoWidthHalf);
        cycleR = ScreenUtil.dp2px(cycleR);
        max_distance = ScreenUtil.dp2px(60);
        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        initShape(contextWeakReference.get());
    }

    protected abstract void initShape(Context context);

    public void drawShape(Canvas canvas, Paint linePaint, Paint bitmapPaint) {
        canvas.setDrawFilter(paintFlagsDrawFilter);
    }

    public boolean onTouch(MotionEvent event) {
        return false;
    }

    protected Offset measureOffsetSafe(Point point, int offX, int offY, Rect rect) {
        point.offset(offX, offY);
        if (isContainers(rect, point)) {
            return new Offset(offX, offY);
        }
        Offset offset = new Offset(0, 0);
        if (offX != 0) {
            boolean isInX = isInValue(point.x, rect.left, rect.right);
            if (!isInX) {
                if (Math.abs(point.x - rect.left) > Math.abs(point.x - rect.right)) {
                    offset.offX = offX - point.x + rect.right;
                } else {
                    offset.offX = offX - point.x + rect.left;
                }
            } else {
                offset.offX = offX;
            }
        }
        if (offY != 0) {
            boolean isInY = isInValue(point.y, rect.top, rect.bottom);
            if (!isInY) {
                if (Math.abs(point.y - rect.top) > Math.abs(point.y - rect.bottom)) {
                    offset.offY = offY - point.y + rect.bottom;
                } else {
                    offset.offY = offY - point.y + rect.top;
                }
            } else {
                offset.offY = offY;
            }
        }
        return offset;
    }

    public Rect getCenterRect(int x, int y, int widthHalf) {
        return new Rect(x - widthHalf, y - widthHalf, x + widthHalf, y + widthHalf);
    }

    public RectF getCenterRectF(float x, float y, float widthHalf) {
        return new RectF(x - widthHalf, y - widthHalf, x + widthHalf, y + widthHalf);
    }

    public Rect getCenterRect(int x, int y, int wh, int hh) {
        return new Rect(x - wh, y - hh, x + wh, y + hh);
    }

    public class Offset {
        int offX;

        int offY;

        public Offset(int offX, int offY) {
            this.offX = offX;
            this.offY = offY;
        }

        public Offset() {
        }
    }

    public class ParameterSet {
        protected Matrix pipMatrix = new Matrix();

        protected Matrix maskMatrix = new Matrix();

        protected Point position = new Point();

        protected PointF maskPosition = new PointF();

        protected float maskRotation = 0;

        protected float maskBlur = 0;

        public Point getPosition() {
            return position;
        }

        public void setPosition(Point position) {
            this.position = position;
        }

        public ParameterSet updatePoint(int x, int y) {
            float[] f = new float[] {x, y};
            Matrix matrix1 = new Matrix();
            pipMatrix.invert(matrix1);
            matrix1.mapPoints(f);

            maskPosition.x = f[0] / videoWidth;
            maskPosition.y = f[1] / videoHeight;

            return this;
        }

        public ParameterSet updatePointLine(float x, float y) {
            maskPosition.x = x / videoWidth;
            maskPosition.y = y / videoHeight;
            return this;
        }

        public ParameterSet updateRotation(Matrix matrix) {
            maskRotation = MatrixUtils.getRotate(matrix);
            return this;
        }

        public ParameterSet updateBlur(float blur) {
            this.maskBlur = blur;
            return this;
        }

        public ParameterSet() {
        }

        public Matrix getPipMatrix() {
            return pipMatrix;
        }

        public void setPipMatrix(Matrix pipMatrix) {
            this.pipMatrix = pipMatrix;
        }

        public Matrix getMaskMatrix() {
            return maskMatrix;
        }

        public void setMaskMatrix(Matrix maskMatrix) {
            this.maskMatrix = maskMatrix;
        }

        public PointF getMaskPosition() {
            return maskPosition;
        }

        public void setMaskPosition(PointF maskPosition) {
            this.maskPosition = maskPosition;
        }

        public float getMaskRotation() {
            return maskRotation;
        }

        public void setMaskRotation(float maskRotation) {
            this.maskRotation = maskRotation;
        }

        public float getMaskBlur() {
            return maskBlur;
        }

        public void setMaskBlur(int maskBlur) {
            this.maskBlur = maskBlur;
        }
    }

    public void setOnInvalidate(OnInvalidate onInvalidate) {
        this.onInvalidate = onInvalidate;
    }

    public OnInvalidate onInvalidate;

    public void invalidateShape() {
        if (onInvalidate != null) {
            onInvalidate.invalidateShape();
        }
    }

    public void invalidateDrawable(Drawable drawable) {
        if (onInvalidate != null) {
            onInvalidate.invalidateDrawable(drawable);
        }
    }

    public interface OnInvalidate {
        void invalidateShape();

        void invalidateDrawable(Drawable drawable);
    }

    protected float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    protected float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    protected int spacing(Point point, Rect rect) {
        float x = point.x - rect.centerX();
        float y = point.y - rect.centerY();
        return (int) Math.sqrt(x * x + y * y);
    }

    protected int spacing(float x, float y, Rect rect) {
        float dx = x - rect.centerX();
        float dy = y - rect.centerY();
        return (int) Math.sqrt(dx * dx + dy * dy);
    }

    protected float rotation(MotionEvent event) {
        double deltaX = (event.getX(0) - event.getX(1));
        double deltaY = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    protected float rotation(float x1, float y1, float x2, float y2) {
        double deltaX = x1 - x2;
        double deltaY = y1 - y2;
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setOnParameterSetChange(ParameterSetChange parameterSetChange) {
        this.parameterSetChange = parameterSetChange;
    }

    public interface ParameterSetChange<T> {
        void onParameterSetChange(T parameterSet);
    }

    public void onChange(ParameterSet parameterSet) {
        if (parameterSetChange != null) {
            parameterSetChange.onParameterSetChange(parameterSet);
        }
    }

    public T getParameterSet() {
        return aParameterSet;
    }

    public void setParameterSet(T parameterSet) {
        this.aParameterSet = parameterSet;
    }

    protected void drawRect(Canvas canvas, Rect rect) {
        canvas.drawRect(rect, testPaint);
    }

    protected boolean isContainers(Rect rect, float[] point) {
        return isContainers(rect.left, rect.right, rect.top, rect.bottom, (int) point[0], (int) point[1]);
    }

    protected boolean isContainers(RectF rect, float[] point) {
        return isContainers(rect.left, rect.right, rect.top, rect.bottom, (int) point[0], (int) point[1]);
    }

    protected boolean isContainers(Rect rect, Point point) {
        return isContainers(rect.left, rect.right, rect.top, rect.bottom, point.x, point.y);
    }

    protected boolean isInValue(int value, int l1, int l2) {
        if ((value > l1 && value > l2) || (value < l1 && value < l2)) {
            return false;
        }
        return true;
    }

    protected boolean isContainers(int l, int r, int t, int b, int x, int y) {
        if ((l < x && r < x) || (t < y && b < y) || (l > x && r > x) || (t > y && b > y)) {
            return false;
        }
        return true;
    }

    protected boolean isContainers(float l, float r, float t, float b, int x, int y) {
        if ((l < x && r < x) || (t < y && b < y) || (l > x && r > x) || (t > y && b > y)) {
            return false;
        }
        return true;
    }
}
