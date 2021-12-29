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
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class LineMask extends MaskShape {
    private Rect blurRect;

    private Rect rotateRect;

    private Rect lineRect;

    private Drawable blurDrawable;

    private Drawable rotateDrawable;

    private Path linePath;

    private Matrix defMatrix;

    private Matrix tmpMatrix;

    private int defBlurDistance;

    public LineMask(Context context) {
        super(context);
        blurMuti = 5;
        aParameterSet = new LineParameterSet();
    }

    @Override
    protected void initShape(Context context) {
        defMatrix = new Matrix();
        tmpMatrix = new Matrix();

        linePath = new Path();
        linePath.addCircle(aParameterSet.position.x, aParameterSet.position.y, cycleR, Path.Direction.CCW);

        linePath.moveTo(Integer.MIN_VALUE, aParameterSet.position.y);
        linePath.lineTo(aParameterSet.position.x - cycleR, aParameterSet.position.y);

        linePath.moveTo(aParameterSet.position.x + cycleR, aParameterSet.position.y);
        linePath.lineTo(Integer.MAX_VALUE, aParameterSet.position.y);

        defBlurDistance = ScreenUtil.dp2px(40);

        blurRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        float blur = aParameterSet.getMaskBlur();
        blurRect.offset(0, (int) (-defBlurDistance - (blur / blurMuti)));

        rotateRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        rotateRect.offset(ScreenUtil.dp2px(40), ScreenUtil.dp2px(40));

        blurDrawable = context.getDrawable(R.drawable.ico_trans);
        blurDrawable.setBounds(blurRect);
        rotateDrawable = context.getDrawable(R.drawable.ico_rotate);
        rotateDrawable.setBounds(rotateRect);

        lineRect = new Rect(Integer.MIN_VALUE, aParameterSet.position.y - icoWidthHalf, Integer.MAX_VALUE,
            aParameterSet.position.y + icoWidthHalf);

    }

    @Override
    public void drawShape(Canvas canvas, Paint linePaint, Paint bitmapPaint) {
        super.drawShape(canvas, linePaint, bitmapPaint);

        canvas.save();

        if (defMatrix == null) {
            return;
        }

        defMatrix.set(aParameterSet.pipMatrix);
        defMatrix.postConcat(aParameterSet.maskMatrix);

        canvas.setMatrix(defMatrix);

        canvas.drawPath(linePath, linePaint);

        blurDrawable.setBounds(blurRect);
        blurDrawable.draw(canvas);
        rotateDrawable.draw(canvas);
        canvas.restore();
    }

    private Mode currentMode = Mode.NONE;

    private float[] downP;

    private float[] conP;

    private float defRotate;

    private int oldX = 0;

    private int oldY = 0;

    @Override
    public boolean onTouch(MotionEvent event) {
        super.onTouch(event);

        if (defMatrix == null || tmpMatrix == null) {
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
                    defRotate = rotation(aParameterSet.position.x, aParameterSet.position.y, downP[0], downP[1]);
                    currentMode = Mode.ROTATION;
                } else if (isContainers(lineRect, downP)) {
                    currentMode = Mode.MOVE;
                } else {
                    currentMode = Mode.NONE;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int currentX = (int) event.getX();
                int curY = (int) event.getY();

                switch (currentMode) {
                    case MOVE:

                        Matrix matrix = new Matrix();
                        aParameterSet.pipMatrix.invert(matrix);
                        conP = new float[] {currentX, curY};
                        matrix.mapPoints(conP);

                        float[] oldPoints = new float[] {oldX, oldY};
                        matrix.mapPoints(oldPoints);

                        int pipOffX = (int) (conP[0] - oldPoints[0]);
                        int pipOffY = (int) (conP[1] - oldPoints[1]);
                        if (pipOffX == 0 && pipOffY == 0) {
                            break;
                        }

                        float[] f = new float[] {aParameterSet.position.x, aParameterSet.position.y};
                        defMatrix.mapPoints(f);

                        matrix.mapPoints(f);

                        Point point = new Point((int) f[0], (int) f[1]);
                        Offset offset = measureOffsetSafe(point, pipOffX, pipOffY, pipRect);
                        float[] f2 = new float[] {offset.offX, offset.offY};
                        matrix.mapVectors(f2);

                        aParameterSet.maskMatrix.postTranslate(f2[0], f2[1]);

                        onChange(aParameterSet.updatePointLine(f[0], f[1]));
                        oldY = curY;
                        oldX = currentX;
                        invalidateShape();
                        break;
                    case ROTATION:
                        conP = new float[] {currentX, curY};
                        tmpMatrix.mapPoints(conP);

                        float[] rotateCenter = new float[] {aParameterSet.position.x, aParameterSet.position.y};
                        defMatrix.mapPoints(rotateCenter);

                        float curRotate =
                            rotation(aParameterSet.position.x, aParameterSet.position.y, conP[0], conP[1]);
                        if (defRotate - curRotate == 0) {
                            break;
                        }

                        aParameterSet.maskMatrix.postRotate(defRotate - curRotate, rotateCenter[0], rotateCenter[1]);

                        defRotate = curRotate;

                        onChange(aParameterSet.updateRotation(aParameterSet.maskMatrix));

                        invalidateShape();
                        break;
                    case BLUR:
                        conP = new float[] {currentX, curY};
                        tmpMatrix.mapPoints(conP);
                        int disY = (int) (conP[1] - downP[1]);
                        if (disY == 0) {
                            break;
                        }
                        blurRect.offset(0, disY);
                        int nowDistance = spacing(aParameterSet.position, blurRect);
                        if (nowDistance > max_distance + defBlurDistance) {
                            int convDy = max_distance + defBlurDistance - spacing(aParameterSet.position, blurRect);
                            blurRect.offset(0, -convDy);
                        } else if (nowDistance < defBlurDistance) {
                            int convDy = defBlurDistance - nowDistance;
                            blurRect.offset(0, -convDy);
                        }
                        downP = conP;
                        int blurDis = spacing(aParameterSet.position, blurRect) - defBlurDistance;
                        onChange(aParameterSet.updateBlur(blurDis * blurMuti));
                        invalidateShape();
                        break;
                    case NONE:
                        break;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    enum Mode {
        MOVE,
        ROTATION,
        BLUR,
        NONE;
    }

    public class LineParameterSet extends ParameterSet {

    }
}
