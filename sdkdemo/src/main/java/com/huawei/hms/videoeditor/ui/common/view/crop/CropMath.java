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

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;

import java.util.Arrays;

/**
 * CropMath
 */
public class CropMath {
    public static float[] getCornersFromRect(RectF rectF) {
        float[] corners = {rectF.left, rectF.top, rectF.right, rectF.top, rectF.right, rectF.bottom, rectF.left, rectF.bottom};
        return corners;
    }

    public static boolean inclusiveContains(RectF rectF, float pointX, float pointY) {
        return !(pointX > rectF.right || pointX < rectF.left || pointY > rectF.bottom || pointY < rectF.top);
    }

    public static RectF trapToRect(float[] array) {
        RectF rectF = new RectF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
                Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        for (int i = 1; i < array.length; i += 2) {
            float y = array[i];
            float x = array[i - 1];
            rectF.left = (x < rectF.left) ? x : rectF.left;
            rectF.top = (y < rectF.top) ? y : rectF.top;
            rectF.right = (x > rectF.right) ? x : rectF.right;
            rectF.bottom = (y > rectF.bottom) ? y : rectF.bottom;
        }
        rectF.sort();
        return rectF;
    }

    public static void getEdgePoints(RectF bound, float[] array) {
        if (array.length < 2) {
            return;
        }
        for (int i = 0; i < array.length; i += 2) {
            array[i] = GeometryMathUtils.clamp(array[i], bound.left, bound.right);
            array[i + 1] = GeometryMathUtils.clamp(array[i + 1], bound.top, bound.bottom);
        }
    }

    public static float[] closestSide(float[] point, float[] corners) {
        float oldMag = Float.POSITIVE_INFINITY;
        int length = corners.length;
        float[] bestLine = null;
        for (int j = 0; j < length; j += 2) {
            float[] line = {corners[j], corners[(j + 1) % length], corners[(j + 2) % length], corners[(j + 3) % length]};
            float mag = GeometryMathUtils.vectorLength(GeometryMathUtils.shortestVectorFromPointToLine(point, line));
            if (mag < oldMag) {
                oldMag = mag;
                bestLine = line;
            }
        }
        return bestLine;
    }

    public static boolean pointInRotatedRect(float[] point, RectF rectF, float rot) {
        Matrix matrix = new Matrix();
        float[] p = Arrays.copyOf(point, 2);
        matrix.setRotate(rot, rectF.centerX(), rectF.centerY());
        Matrix m0 = new Matrix();
        if (!matrix.invert(m0)) {
            return false;
        }
        m0.mapPoints(p);
        return inclusiveContains(rectF, p[0], p[1]);
    }

    public static boolean pointInRotatedRect(float[] point, float[] rotatedRect, float[] center) {
        RectF unrotated = new RectF();
        float angle = getUnrotated(rotatedRect, center, unrotated);
        return pointInRotatedRect(point, unrotated, angle);
    }

    public static void fixAspectRatio(RectF rectF, float width, float height) {
        float scale = Math.min(rectF.width() / width, rectF.height() / height);
        float centX = rectF.centerX();
        float centY = rectF.centerY();
        float hw = scale * width / 2;
        float hh = scale * height / 2;
        rectF.set(centX - hw, centY - hh, centX + hw, centY + hh);
    }

    public static void fixAspectRatioContained(RectF rectF, float width, float height) {
        float origW = rectF.width();
        float origH = rectF.height();
        float origA = origW / origH;
        float a = width / height;
        if (origA < a) {
            float finalH = origW / a;
            rectF.top = rectF.centerY() - finalH / 2;
            rectF.bottom = rectF.top + finalH;
        } else {
            float finalW = origH * a;
            rectF.left = rectF.centerX() - finalW / 2;
            rectF.right = rectF.left + finalW;
        }
    }

    public static RectF getScaledCropBounds(RectF cropBounds, RectF photoBounds, RectF displayBounds) {
        Matrix matrix = new Matrix();
        matrix.setRectToRect(photoBounds, displayBounds, Matrix.ScaleToFit.FILL);
        RectF resultCrop = new RectF(cropBounds);
        if (!matrix.mapRect(resultCrop)) {
            return null;
        }
        return resultCrop;
    }

    public static int getBitmapSize(Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static int constrainedRotation(float rotation) {
        int resultRotation = (int) ((rotation % 360) / 90);
        resultRotation = (resultRotation < 0) ? (resultRotation + 4) : resultRotation;
        return resultRotation * 90;
    }

    private static float getUnrotated(float[] rotatedRect, float[] center, RectF unRotated) {
        float dy = rotatedRect[1] - rotatedRect[3];
        float dx = rotatedRect[0] - rotatedRect[2];
        float angle = (float) (Math.atan(dy / dx) * 180 / Math.PI);
        Matrix matrix = new Matrix();
        matrix.setRotate(-angle, center[0], center[1]);
        float[] unRotatedRect = new float[rotatedRect.length];
        matrix.mapPoints(unRotatedRect, rotatedRect);
        unRotated.set(trapToRect(unRotatedRect));
        return angle;
    }

}
