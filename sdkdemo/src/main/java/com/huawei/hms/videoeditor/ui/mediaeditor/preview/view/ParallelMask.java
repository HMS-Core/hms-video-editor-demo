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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.huawei.hms.videoeditor.ui.common.utils.MatrixUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class ParallelMask extends MaskShape {
    private Rect blurRect;

    private Rect rotateRect;

    private RectF lineRect;

    private Drawable blurDrawable;

    private Drawable rotateDrawable;

    private Path linePath;

    private Matrix defMatrix;

    private Matrix tmpMatrix;

    private int defBlurDistance;

    private int defLineWidth = 0;

    private int rotateOffX;

    private int rotateOffY;

    public int blurAlter = 5;

    public float lineWidthRate = 0.3f;

    public ParallelMask(Context context) {
        super(context);
        aParameterSet = new ParallelParameterSet();
    }

    @Override
    protected void initShape(Context context) {
        defMatrix = new Matrix();
        tmpMatrix = new Matrix();

        blurRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        rotateRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);

        linePath = new Path();
        linePath.addCircle(aParameterSet.position.x, aParameterSet.position.y, cycleR, Path.Direction.CCW);

        if (aParameterSet instanceof ParallelParameterSet) {
            defLineWidth = ((ParallelParameterSet) aParameterSet).getHeightDisatance() / 2;
        }
        int b = (int) (aParameterSet.getMaskBlur() / blurAlter);
        defBlurDistance = ScreenUtil.dp2px(30);
        rotateOffX = ScreenUtil.dp2px(80);
        rotateOffY = ScreenUtil.dp2px(30);

        lineRect = new RectF(-200000, aParameterSet.position.y + defLineWidth, 200000,
            aParameterSet.position.y - defLineWidth);

        blurDrawable = context.getDrawable(R.drawable.ico_trans);
        rotateDrawable = context.getDrawable(R.drawable.ico_rotate);

        blurRect.offset(0, -defBlurDistance - b - defLineWidth);
        rotateRect.offset(rotateOffX, rotateOffY + defLineWidth);

        blurDrawable.setBounds(blurRect);
        rotateDrawable.setBounds(rotateRect);

    }

    public class ParallelParameterSet extends ParameterSet {

        public int heightDisatance;

        public ParallelParameterSet updateRotateDistance(Matrix matrix, int height) {
            maskRotation = MatrixUtils.getRotate(matrix);
            heightDisatance = height;
            return this;
        }

        public ParallelParameterSet updateHeight(int height) {
            heightDisatance = height;
            return this;
        }

        public int getHeightDisatance() {
            return heightDisatance;
        }

        public void setHeightDisatance(int heightDisatance) {
            this.heightDisatance = heightDisatance;
        }
    }

    @Override
    public void drawShape(Canvas canvas, Paint linePaint, Paint bitmapPaint) {
        super.drawShape(canvas, linePaint, bitmapPaint);

        if (defMatrix == null) {
            return;
        }

        canvas.save();
        defMatrix.set(aParameterSet.pipMatrix);
        defMatrix.postConcat(aParameterSet.maskMatrix);
        canvas.setMatrix(defMatrix);

        canvas.drawPath(linePath, linePaint);
        canvas.drawRect(lineRect, linePaint);

        blurDrawable.setBounds(blurRect);
        blurDrawable.draw(canvas);
        rotateDrawable.draw(canvas);
        canvas.restore();
    }

    private Mode currentMode = Mode.NONE;

    private float[] downP;

    private float[] conPoint;

    private float oldRotate;

    private float oldScaleDis;

    private int oldX = 0;

    private int oldY = 0;

    private int thisLineDistance = defLineWidth;

    @Override
    public boolean onTouch(MotionEvent event) {
        if (defMatrix == null) {
            return false;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                oldX = (int) event.getX();
                oldY = (int) event.getY();

                downP = new float[] {oldX, oldY};
                defMatrix.invert(tmpMatrix);
                tmpMatrix.mapPoints(downP);

                if (isContainers(blurRect, downP)) {
                    currentMode = Mode.BLUR;
                } else if (isContainers(rotateRect, downP)) {
                    oldRotate = rotation(aParameterSet.position.x, aParameterSet.position.y, downP[0], downP[1]);
                    currentMode = Mode.ROTATION;
                } else if (isContainers(lineRect, downP)) {
                    currentMode = Mode.MOVE;
                } else {
                    currentMode = Mode.NONE;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    currentMode = Mode.SCALE;
                    float[] pointerDown = new float[] {event.getX(0), event.getY(0), event.getX(1), event.getY(1)};
                    tmpMatrix.mapPoints(pointerDown);
                    oldScaleDis = distanceY(pointerDown[1], pointerDown[3]);

                    thisLineDistance = defLineWidth;

                }
                break;

            case MotionEvent.ACTION_MOVE:
                int curX = (int) event.getX();
                int currentY = (int) event.getY();

                switch (currentMode) {
                    case SCALE:

                        if (event.getPointerCount() == 2) {
                            float[] pointerDown =
                                new float[] {event.getX(0), event.getY(0), event.getX(1), event.getY(1)};
                            tmpMatrix.mapPoints(pointerDown);

                            float curScaleDis = distanceY(pointerDown[1], pointerDown[3]);
                            if (thisLineDistance == 0) {
                                thisLineDistance = 1;
                            }
                            defLineWidth = (int) (thisLineDistance * curScaleDis / oldScaleDis);

                            lineRect.top = aParameterSet.position.y + defLineWidth;
                            lineRect.bottom = aParameterSet.position.y - defLineWidth;
                            if (lineRect.top <= lineRect.bottom) {
                                lineRect.top = lineRect.bottom;
                            }

                            blurRect.offsetTo(aParameterSet.position.x - icoWidthHalf, (int) (aParameterSet.position.y
                                - (lineRect.top - lineRect.bottom) / 2 - defBlurDistance - icoWidthHalf));
                            if (aParameterSet instanceof ParallelParameterSet) {
                                onChange(((ParallelParameterSet) aParameterSet)
                                    .updateHeight((int) (lineRect.top - lineRect.bottom)));
                            }
                            invalidateShape();
                        }
                        break;
                    case MOVE:
                        Matrix matrix = new Matrix();
                        aParameterSet.pipMatrix.invert(matrix);

                        conPoint = new float[] {curX, currentY};
                        matrix.mapPoints(conPoint);

                        float[] oldP = new float[] {oldX, oldY};
                        matrix.mapPoints(oldP);

                        int pipOffX = (int) (conPoint[0] - oldP[0]);
                        int pipOffY = (int) (conPoint[1] - oldP[1]);
                        if (pipOffX == 0 && pipOffY == 0) {
                            break;
                        }

                        float[] f = new float[] {aParameterSet.position.x, aParameterSet.position.y};
                        defMatrix.mapPoints(f);

                        matrix.mapPoints(f);

                        Point p = new Point((int) f[0], (int) f[1]);
                        Offset offsets = measureOffsetSafe(p, pipOffX, pipOffY, pipRect);
                        float[] f2 = new float[] {offsets.offX, offsets.offY};
                        matrix.mapVectors(f2);

                        aParameterSet.maskMatrix.postTranslate(f2[0], f2[1]);

                        onChange(aParameterSet.updatePointLine(f[0], f[1]));
                        oldY = currentY;
                        oldX = curX;
                        invalidateShape();
                        break;
                    case ROTATION:
                        conPoint = new float[] {curX, currentY};
                        tmpMatrix.mapPoints(conPoint);

                        float[] rotateCenter = new float[] {aParameterSet.position.x, aParameterSet.position.y};
                        defMatrix.mapPoints(rotateCenter);

                        float curRotate =
                            rotation(aParameterSet.position.x, aParameterSet.position.y, conPoint[0], conPoint[1]);
                        if (oldRotate - curRotate == 0) {
                            break;
                        }

                        aParameterSet.maskMatrix.postRotate(oldRotate - curRotate, rotateCenter[0], rotateCenter[1]);

                        oldRotate = curRotate;

                        onChange(aParameterSet.updateRotation(aParameterSet.maskMatrix));

                        invalidateShape();
                        break;
                    case BLUR:
                        conPoint = new float[] {curX, currentY};
                        tmpMatrix.mapPoints(conPoint);
                        int disY = (int) (conPoint[1] - downP[1]);
                        if (disY == 0) {
                            break;
                        }
                        blurRect.offset(0, disY);
                        int nowDistance = spacing(aParameterSet.position, blurRect);
                        if (nowDistance > max_distance + defBlurDistance + defLineWidth) {
                            int convDy = max_distance + defBlurDistance + defLineWidth
                                - spacing(aParameterSet.position, blurRect);
                            blurRect.offset(0, -convDy);
                        } else if (nowDistance < defBlurDistance + defLineWidth) {
                            int convDy = defBlurDistance + defLineWidth - nowDistance;
                            blurRect.offset(0, -convDy);
                        }
                        downP = conPoint;
                        int blurDis = (int) (lineRect.bottom - blurRect.bottom - defBlurDistance + icoWidthHalf);
                        onChange(aParameterSet.updateBlur(blurDis * blurAlter));
                        invalidateShape();
                        break;
                    case NONE:
                        break;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    currentMode = Mode.SCALE;
                } else {
                    currentMode = Mode.NONE;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    enum Mode {
        MOVE,
        ROTATION,
        BLUR,
        SCALE,
        NONE
    }

    private float distanceY(float y1, float y2) {
        return (int) Math.abs(y1 - y2);
    }
}
