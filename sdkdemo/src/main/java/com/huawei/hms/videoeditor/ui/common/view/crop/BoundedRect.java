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

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import com.huawei.hms.videoeditor.ui.common.utils.MathUtils;

import java.util.Arrays;

/**
 * Maintains invariant that mInnerRect rectangle is constrained to be within the
 * mOuterRect, rotated rectangle.
 */
public class BoundedRect {
    private float mRotation;

    private final RectF rectF;

    private RectF mInnerRect;

    private float[] mInnerRotated;

    public BoundedRect(float rotation, Rect outerRect, Rect innerRect) {
        mRotation = rotation;
        rectF = new RectF(outerRect);
        mInnerRect = new RectF(innerRect);
        mInnerRotated = CropMath.getCornersFromRect(mInnerRect);
        rotateInner();
        if (!isConstrained(mInnerRect, rectF)) {
            reconstrain();
        }
    }

    public BoundedRect(float rotation, RectF outerRect, RectF innerRect) {
        mRotation = rotation;
        rectF = new RectF(outerRect);
        mInnerRect = new RectF(innerRect);
        mInnerRotated = CropMath.getCornersFromRect(mInnerRect);
        rotateInner();
        if (!isConstrained(mInnerRect, rectF)) {
            reconstrain();
        }
    }

    public static boolean isConstrained(RectF innerRectF, RectF outerRectF) {
        float[] innerPoints = CropMath.getCornersFromRect(innerRectF);
        for (int i = 0; i < 8; i += 2) {
            if (!CropMath.inclusiveContains(outerRectF, innerPoints[i], innerPoints[i + 1])) {
                return false;
            }
        }
        return true;
    }

    public void resetTo(float rotation, RectF outerRect, RectF innerRect) {
        mRotation = rotation;
        rectF.set(outerRect);
        mInnerRect.set(innerRect);
        mInnerRotated = CropMath.getCornersFromRect(mInnerRect);
        rotateInner();
        if (!isConstrained(mInnerRect, rectF)) {
            reconstrain();
        }
    }

    /**
     * Sets mInnerRect, and re-constrains it to fit within the rotated bounding rect.
     */
    public void setInner(RectF newInner) {
        if (mInnerRect.equals(newInner)) {
            return;
        }
        mInnerRect = new RectF(newInner);
        mInnerRotated = CropMath.getCornersFromRect(mInnerRect);
        rotateInner();
        if (!isConstrained(mInnerRect, rectF)) {
            reconstrain();
        }
    }

    public void setToInner(RectF r) {
        r.set(mInnerRect);
    }

    public void setToOuter(RectF r) {
        r.set(rectF);
    }

    public RectF getInner() {
        return new RectF(mInnerRect);
    }

    public RectF getOuter() {
        return new RectF(rectF);
    }

    /**
     * Tries to move the mInnerRect rectangle by (dx, dy). If this would cause it to
     * leave the bounding rectangle, snaps the mInnerRect rectangle to the edge of
     * the bounding rectangle.
     */
    public void moveInner(float dx, float dy) {
        Matrix matrix = getInverseRotMatrix();

        RectF translatedInner = new RectF(mInnerRect);
        translatedInner.offset(dx, dy);

        float[] translatedInnerCorners = CropMath.getCornersFromRect(translatedInner);
        float[] outerCorners = CropMath.getCornersFromRect(rectF);

        matrix.mapPoints(translatedInnerCorners);
        float[] corrections = {0, 0};

        // find correction vectors for corners that have moved out of bounds
        for (int i = 0; i < translatedInnerCorners.length; i += 2) {
            float correctedInnerX = translatedInnerCorners[i] + corrections[0];
            float correctedInnerY = translatedInnerCorners[i + 1] + corrections[1];
            if (!CropMath.inclusiveContains(rectF, correctedInnerX, correctedInnerY)) {
                float[] badCorner = {correctedInnerX, correctedInnerY};
                float[] nearestSide = CropMath.closestSide(badCorner, outerCorners);
                float[] correctionVec = GeometryMathUtils.shortestVectorFromPointToLine(badCorner, nearestSide);
                corrections[0] += correctionVec[0];
                corrections[1] += correctionVec[1];
            }
        }

        for (int i = 0; i < translatedInnerCorners.length; i += 2) {
            float correctedInnerX = translatedInnerCorners[i] + corrections[0];
            float correctedInnerY = translatedInnerCorners[i + 1] + corrections[1];
            if (!CropMath.inclusiveContains(rectF, correctedInnerX, correctedInnerY)) {
                float[] correctionVec = {correctedInnerX, correctedInnerY};
                CropMath.getEdgePoints(rectF, correctionVec);
                correctionVec[0] -= correctedInnerX;
                correctionVec[1] -= correctedInnerY;
                corrections[0] += correctionVec[0];
                corrections[1] += correctionVec[1];
            }
        }

        // Set correction
        for (int i = 0; i < translatedInnerCorners.length; i += 2) {
            float correctedInnerX = translatedInnerCorners[i] + corrections[0];
            float correctedInnerY = translatedInnerCorners[i + 1] + corrections[1];
            // update translated corners with correction vectors
            translatedInnerCorners[i] = correctedInnerX;
            translatedInnerCorners[i + 1] = correctedInnerY;
        }

        mInnerRotated = translatedInnerCorners;
        // reconstrain to update mInnerRect
        reconstrain();
    }

    /**
     * Attempts to resize the mInnerRect rectangle. If this would cause it to leave
     * the bounding rect, clips the mInnerRect rectangle to fit.
     */
    public void resizeInner(RectF newInner) {
        Matrix m = getRotMatrix();
        Matrix m0 = getInverseRotMatrix();

        float[] outerCorners = CropMath.getCornersFromRect(rectF);
        m.mapPoints(outerCorners);
        float[] oldInnerCorners = CropMath.getCornersFromRect(mInnerRect);
        float[] newInnerCornerArray = CropMath.getCornersFromRect(newInner);
        RectF ret = new RectF(newInner);

        for (int i = 0; i < newInnerCornerArray.length; i += 2) {
            float[] c = {newInnerCornerArray[i], newInnerCornerArray[i + 1]};
            float[] copy = Arrays.copyOf(c, 2);
            m0.mapPoints(copy);
            if (!CropMath.inclusiveContains(rectF, copy[0], copy[1])) {
                float[] outerSide = CropMath.closestSide(c, outerCorners);
                float[] pathOfCorner =
                        {newInnerCornerArray[i], newInnerCornerArray[i + 1], oldInnerCorners[i], oldInnerCorners[i + 1]};
                float[] size = GeometryMathUtils.lineIntersect(pathOfCorner, outerSide);
                if (size.length == 0) {
                    // lines are parallel or not well defined, so don't resize
                    size = new float[2];
                    size[0] = oldInnerCorners[i];
                    size[1] = oldInnerCorners[i + 1];
                }
                // relies on corners being in same order as method
                // getCornersFromRect
                ret = resizeInnerInitRectFSize(ret, size, i);
            }
        }
        float[] retCorners = CropMath.getCornersFromRect(ret);
        m0.mapPoints(retCorners);
        mInnerRotated = retCorners;
        // reconstrain to update mInnerRect
        reconstrain();
    }

    private RectF resizeInnerInitRectFSize(RectF ret, float[] p, int i) {
        switch (i) {
            case 0:
            case 1:
                ret.left = Math.max(p[0], ret.left);
                ret.top = Math.max(p[1], ret.top);
                break;
            case 2:
            case 3:
                ret.right = Math.min(p[0], ret.right);
                ret.top = Math.max(p[1], ret.top);
                break;
            case 4:
            case 5:
                ret.right = Math.min(p[0], ret.right);
                ret.bottom = Math.min(p[1], ret.bottom);
                break;
            case 6:
            case 7:
                ret.left = Math.max(p[0], ret.left);
                ret.bottom = Math.min(p[1], ret.bottom);
                break;
            default:
                break;
        }
        return ret;
    }

    /**
     * Attempts to resize the mInnerRect rectangle. If this would cause it to leave
     * the bounding rect, clips the mInnerRect rectangle to fit while maintaining
     * aspect ratio.
     */
    public void fixedAspectResizeInner(RectF newInner) {
        Matrix m = getRotMatrix();
        Matrix m0 = getInverseRotMatrix();

        float aspectW = mInnerRect.width();
        float aspectH = mInnerRect.height();
        float aspRatio = aspectW / aspectH;
        float[] corners = CropMath.getCornersFromRect(rectF);

        m.mapPoints(corners);
        float[] oldCorners = CropMath.getCornersFromRect(mInnerRect);
        float[] newCorners = CropMath.getCornersFromRect(newInner);

        // find fixed corner
        int fixed = findFixedCorner(newInner);

        if (fixed == -1) {
            return;
        }
        float width = newInner.width();
        int moved = -1;
        for (int i = 0; i < newCorners.length; i += 2) {
            float[] floats = {newCorners[i], newCorners[i + 1]};
            float[] floats1 = Arrays.copyOf(floats, 2);
            m0.mapPoints(floats1);
            if (!CropMath.inclusiveContains(rectF, floats1[0], floats1[1])) {
                moved = i;
                if (moved == fixed) {
                    continue;
                }
                float[] floats2 = CropMath.closestSide(floats, corners);
                float[] floats3 = {newCorners[i], newCorners[i + 1], oldCorners[i], oldCorners[i + 1]};
                float[] floats4 = GeometryMathUtils.lineIntersect(floats3, floats2);
                if (floats4.length == 0) {
                    floats4 = new float[2];
                    floats4[0] = oldCorners[i];
                    floats4[1] = oldCorners[i + 1];
                }
                float fixedX = oldCorners[fixed];
                float fixedY = oldCorners[fixed + 1];
                float newWidth = Math.abs(fixedX - floats4[0]);
                float newHeight = Math.abs(fixedY - floats4[1]);
                newWidth = Math.max(newWidth, aspRatio * newHeight);
                if (newWidth < width) {
                    width = newWidth;
                }
            }
        }

        float heightSoFar = width / aspRatio;
        RectF rectF = new RectF(mInnerRect);
        if (fixed == 0) {
            rectF.right = rectF.left + width;
            rectF.bottom = rectF.top + heightSoFar;
        } else if (fixed == 2) {
            rectF.left = rectF.right - width;
            rectF.bottom = rectF.top + heightSoFar;
        } else if (fixed == 4) {
            rectF.left = rectF.right - width;
            rectF.top = rectF.bottom - heightSoFar;
        } else if (fixed == 6) {
            rectF.right = rectF.left + width;
            rectF.top = rectF.bottom - heightSoFar;
        }
        float[] retCorners = CropMath.getCornersFromRect(rectF);
        m0.mapPoints(retCorners);
        mInnerRotated = retCorners;
        // reconstrain to update mInnerRect
        reconstrain();
    }

    private int findFixedCorner(RectF newInner) {
        int fixed = -1;
        if (MathUtils.isEqual(mInnerRect.top, newInner.top)) {
            if (Float.compare(mInnerRect.left, newInner.left) == 0) {
                fixed = 0; // top left
            } else if (Float.compare(mInnerRect.right, newInner.right) == 0) {
                fixed = 2; // top right
            }
        } else if (Float.compare(mInnerRect.bottom, newInner.bottom) == 0) {
            if (MathUtils.isEqual(mInnerRect.right, newInner.right)) {
                fixed = 4; // bottom right
            } else if (Float.compare(mInnerRect.left, newInner.left) == 0) {
                fixed = 6; // bottom left
            }
        }
        return fixed;
    }

    // internal methods

    /**
     * Sets rotation, and re-constrains mInnerRect to fit within the rotated bounding
     * rect.
     */
    public void setRotation(float rotation) {
        if (rotation == mRotation) {
            return;
        }
        mRotation = rotation;
        mInnerRotated = CropMath.getCornersFromRect(mInnerRect);
        rotateInner();
        if (!isConstrained(mInnerRect, rectF)) {
            reconstrain();
        }
    }

    private void reconstrain() {
        // mInnerRotated has been changed to have incorrect values
        CropMath.getEdgePoints(rectF, mInnerRotated);
        Matrix m = getRotMatrix();
        float[] unrotated = Arrays.copyOf(mInnerRotated, 8);
        m.mapPoints(unrotated);
        mInnerRect = CropMath.trapToRect(unrotated);
    }

    private void rotateInner() {
        Matrix m = getInverseRotMatrix();
        m.mapPoints(mInnerRotated);
    }

    private Matrix getRotMatrix() {
        Matrix m = new Matrix();
        m.setRotate(mRotation, rectF.centerX(), rectF.centerY());
        return m;
    }

    private Matrix getInverseRotMatrix() {
        Matrix m = new Matrix();
        m.setRotate(-mRotation, rectF.centerX(), rectF.centerY());
        return m;
    }
}
