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

package com.huawei.hms.videoeditor.ui.common.view.crop;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;

/**
 * CropDrawingUtils
 */
public abstract class CropDrawingUtils {
    public static void drawRuleOfThird(Canvas canvas, RectF bounds) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(128, 255, 255, 255));
        paint.setStrokeWidth(2);
        float stepY = (float) BigDecimalUtils.div(bounds.height(), 3.0f, 1);
        float stepX = (float) BigDecimalUtils.div(bounds.width(), 3.0f, 1);
        float countY = bounds.top + stepY;
        float countX = bounds.left + stepX;
        for (int i = 0; i < 2; i++) {
            canvas.drawLine(countX, bounds.top, countX, bounds.bottom, paint);
            countX += stepX;
        }
        for (int j = 0; j < 2; j++) {
            canvas.drawLine(bounds.left, countY, bounds.right, countY, paint);
            countY += stepY;
        }
    }

    public static void drawDashRuleOfThird(Canvas canvas, RectF bounds) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(128, 255, 255, 255));
        paint.setStrokeWidth(2);
        float stepY = (float) BigDecimalUtils.div(bounds.height(), 3.0f, 1);
        float stepX = (float) BigDecimalUtils.div(bounds.width(), 3.0f, 1);
        float y = bounds.top + stepY;
        float x = bounds.left + stepX;
        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        for (int i = 0; i < 2; i++) {
            Path path = new Path();
            path.moveTo(x, bounds.top);
            path.lineTo(x, bounds.bottom);
            paint.setPathEffect(effects);
            canvas.drawPath(path, paint);
            x += stepX;
        }
        for (int j = 0; j < 2; j++) {
            Path path = new Path();
            path.moveTo(bounds.left, y);
            path.lineTo(bounds.right, y);
            paint.setPathEffect(effects);
            canvas.drawPath(path, paint);
            y += stepY;
        }
    }

    public static void drawCropRect(Canvas canvas, RectF bounds) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        canvas.drawRect(bounds, paint);
    }

    public static void drawShade(Canvas canvas, RectF bounds) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK & 0x88000000);

        RectF r = new RectF();
        r.set(0, 0, canvasWidth, bounds.top);
        canvas.drawRect(r, paint);
        r.set(0, bounds.top, bounds.left, canvasHeight);
        canvas.drawRect(r, paint);
        r.set(bounds.left, bounds.bottom, canvasWidth, canvasHeight);
        canvas.drawRect(r, paint);
        r.set(bounds.right, bounds.top, canvasWidth, bounds.bottom);
        canvas.drawRect(r, paint);
    }

    public static void drawIndicator(Canvas canvas, Drawable indicator, int indicatorSize, float centerX,
        float centerY) {
        int left = (int) centerX - indicatorSize / 2;
        int top = (int) centerY - indicatorSize / 2;
        indicator.setBounds(left, top, left + indicatorSize, top + indicatorSize);
        indicator.draw(canvas);
    }

    public static void drawIndicators(Canvas canvas, Drawable[] cropIndicators, int indicatorSize, RectF bounds,
        boolean fixedAspect, int selection) {
        boolean isMoving = (selection != CropObject.MOVE_NONE);
        // cropIndicators:
        // TOP_LEFT,TOP_RIGHT,BOTTON_LEFT_BOTTON_RIGHT,MOVE_LEFT_RIGHTL,MOVE_TOP_BOTTOM
        if ((selection == CropObject.TOP_LEFT) || !isMoving) {
            drawIndicator(canvas, cropIndicators[0], indicatorSize, bounds.left, bounds.top);
        }
        if ((selection == CropObject.TOP_RIGHT) || !isMoving) {
            drawIndicator(canvas, cropIndicators[1], indicatorSize, bounds.right, bounds.top);
        }
        if ((selection == CropObject.BOTTOM_LEFT) || !isMoving) {
            drawIndicator(canvas, cropIndicators[2], indicatorSize, bounds.left, bounds.bottom);
        }
        if ((selection == CropObject.BOTTOM_RIGHT) || !isMoving) {
            drawIndicator(canvas, cropIndicators[3], indicatorSize, bounds.right, bounds.bottom);
        }
        drawIndicatorsFixedAspect(canvas, cropIndicators, indicatorSize, bounds, fixedAspect, selection, isMoving);
    }

    private static void drawIndicatorsFixedAspect(Canvas canvas, Drawable[] cropIndicators, int indicatorSize, RectF bounds,
        boolean fixedAspect, int selection, boolean isMoving) {
        if (!fixedAspect) {
            if (((selection & CropObject.MOVE_LEFT) != 0) || !isMoving) {
                drawIndicator(canvas, cropIndicators[4], indicatorSize, bounds.left, bounds.centerY());
            }
            if (((selection & CropObject.MOVE_RIGHT) != 0) || !isMoving) {
                drawIndicator(canvas, cropIndicators[4], indicatorSize, bounds.right, bounds.centerY());
            }
            if (((selection & CropObject.MOVE_TOP) != 0) || !isMoving) {
                drawIndicator(canvas, cropIndicators[5], indicatorSize, bounds.centerX(), bounds.top);
            }
            if (((selection & CropObject.MOVE_BOTTOM) != 0) || !isMoving) {
                drawIndicator(canvas, cropIndicators[5], indicatorSize, bounds.centerX(), bounds.bottom);
            }
        }
    }

    public static void drawWallpaperSelectionFrame(Canvas canvas, RectF cropBounds, float spotX, float spotY, Paint p,
        Paint shadowPaint) {
        float sy = cropBounds.height() * spotY;
        float sx = cropBounds.width() * spotX;
        float cy = cropBounds.centerY();
        float cx = cropBounds.centerX();
        RectF rectF1 = new RectF(cx - sx / 2, cy - sy / 2, cx + sx / 2, cy + sy / 2);
        float temporary = sx;
        sx = sy;
        sy = temporary;
        RectF rectF2 = new RectF(cx - sx / 2, cy - sy / 2, cx + sx / 2, cy + sy / 2);
        canvas.save();
        canvas.clipRect(cropBounds);
        canvas.clipRect(rectF1, Region.Op.DIFFERENCE);
        canvas.clipRect(rectF2, Region.Op.DIFFERENCE);
        canvas.drawPaint(shadowPaint);
        canvas.restore();
        Path path = new Path();
        path.moveTo(rectF1.left, rectF1.top);
        path.lineTo(rectF1.right, rectF1.top);
        path.moveTo(rectF1.left, rectF1.top);
        path.lineTo(rectF1.left, rectF1.bottom);
        path.moveTo(rectF1.left, rectF1.bottom);
        path.lineTo(rectF1.right, rectF1.bottom);
        path.moveTo(rectF1.right, rectF1.top);
        path.lineTo(rectF1.right, rectF1.bottom);
        path.moveTo(rectF2.left, rectF2.top);
        path.lineTo(rectF2.right, rectF2.top);
        path.moveTo(rectF2.right, rectF2.top);
        path.lineTo(rectF2.right, rectF2.bottom);
        path.moveTo(rectF2.left, rectF2.bottom);
        path.lineTo(rectF2.right, rectF2.bottom);
        path.moveTo(rectF2.left, rectF2.top);
        path.lineTo(rectF2.left, rectF2.bottom);
        canvas.drawPath(path, p);
    }

    public static void drawShadows(Canvas canvas, Paint p, RectF innerBounds, RectF outerBounds) {
        canvas.drawRect(outerBounds.left, outerBounds.top, innerBounds.right, innerBounds.top, p);
        canvas.drawRect(innerBounds.right, outerBounds.top, outerBounds.right, innerBounds.bottom, p);
        canvas.drawRect(innerBounds.left, innerBounds.bottom, outerBounds.right, outerBounds.bottom, p);
        canvas.drawRect(outerBounds.left, innerBounds.top, innerBounds.left, outerBounds.bottom, p);
    }

    public static Matrix getBitmapToDisplayMatrix(RectF imageBounds, RectF displayBounds) {
        Matrix m = new Matrix();
        CropDrawingUtils.setBitmapToDisplayMatrix(m, imageBounds, displayBounds);
        return m;
    }

    public static boolean setBitmapToDisplayMatrix(Matrix m, RectF imageBounds, RectF displayBounds) {
        m.reset();
        return m.setRectToRect(imageBounds, displayBounds, Matrix.ScaleToFit.CENTER);
    }

    public static boolean setImageToScreenMatrix(Matrix matrix, RectF imageRectF, RectF screen, int rotation) {
        RectF rectF = new RectF();
        matrix.setRotate(rotation, imageRectF.centerX(), imageRectF.centerY());
        if (!matrix.mapRect(rectF, imageRectF)) {
            return false; // fails for rotations that are not multiples of 90
            // degrees
        }
        boolean rToR = matrix.setRectToRect(rectF, screen, Matrix.ScaleToFit.CENTER);
        boolean rot = matrix.preRotate(rotation, imageRectF.centerX(), imageRectF.centerY());
        return rToR && rot;
    }

}
