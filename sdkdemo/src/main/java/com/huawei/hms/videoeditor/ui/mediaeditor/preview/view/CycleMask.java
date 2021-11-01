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

package com.huawei.hms.videoeditor.ui.mediaeditor.preview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class CycleMask extends MaskShape {
    private Rect blurRect;

    private Rect rotateRect;

    private Rect dragXRect;

    private Rect dragYRect;

    private RectF mainRect;

    private Drawable blurDrawable;

    private Drawable rotateDrawable;

    private Drawable dragXDrawable;

    private Drawable dragYDrawable;

    private Matrix defMatrix;

    private Matrix tmpMatrix;

    private int defBlurDistance;

    private int defBlur;

    private int mainRectWidthHalf;

    public float defSize = 0.8f;

    private int iBlur;

    public CycleMask(Context context) {
        super(context);
        blurMuti = 2;
        aParameterSet = new CycleParameterSet();
    }

    @Override
    protected void initShape(Context context) {
        defMatrix = new Matrix();
        tmpMatrix = new Matrix();

        if (!(aParameterSet instanceof CycleParameterSet)) {
            return;
        }
        mainRectWidthHalf = (int) (((CycleParameterSet) aParameterSet).xLen / 2.0f);
        defBlur = ScreenUtil.dp2px(20);
        defBlurDistance = defBlur + mainRectWidthHalf + icoWidthHalf;

        blurRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        blurRect.offset(0, -defBlurDistance);

        rotateRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        rotateRect.offset(mainRectWidthHalf, mainRectWidthHalf);

        mainRect = getCenterRectF(aParameterSet.position.x, aParameterSet.position.y, mainRectWidthHalf);

        dragXRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        dragYRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);

        dragXRect.offset(mainRectWidthHalf + icoWidthHalf, 0);
        dragYRect.offset(0, mainRectWidthHalf + icoWidthHalf);

        blurDrawable = context.getDrawable(R.drawable.ico_trans);
        blurDrawable.setBounds(blurRect);
        rotateDrawable = context.getDrawable(R.drawable.ico_rotate);
        rotateDrawable.setBounds(rotateRect);

        iBlur = spacingRectY(mainRect, blurRect);

        dragXDrawable = context.getDrawable(R.drawable.ico_drag_x);
        dragXDrawable.setBounds(dragXRect);
        dragYDrawable = context.getDrawable(R.drawable.ico_drag_y);
        dragYDrawable.setBounds(dragYRect);
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
        canvas.drawCircle(aParameterSet.position.x, aParameterSet.position.y, cycleR, linePaint);
        canvas.drawOval(mainRect, linePaint);

        blurDrawable.setBounds(blurRect);
        dragXDrawable.setBounds(dragXRect);
        dragYDrawable.setBounds(dragYRect);
        rotateDrawable.setBounds(rotateRect);

        blurDrawable.draw(canvas);
        dragXDrawable.draw(canvas);
        dragYDrawable.draw(canvas);
        rotateDrawable.draw(canvas);
        canvas.restore();

    }

    enum Mode {
        MOVE,
        ROTATION,
        BLUR,
        XSCALE,
        YSCALE,
        NONE;
    }

    private int oldX;

    private int oldY;

    private Mode currentMode = Mode.NONE;

    private float[] downP;

    private float[] conP;

    private float defRotate;

    @Override
    public boolean onTouch(MotionEvent event) {
        if (defMatrix == null) {
            return false;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();

                downP = new float[] {x, y};
                defMatrix.invert(tmpMatrix);
                tmpMatrix.mapPoints(downP);
                if (isRectContain(blurRect, downP)) {
                    currentMode = Mode.BLUR;
                } else if (isRectContain(rotateRect, downP)) {
                    defRotate = rotation(aParameterSet.position.x, aParameterSet.position.y, downP[0], downP[1]);

                    currentMode = Mode.ROTATION;
                } else if (isRectContain(dragXRect, downP)) {
                    currentMode = Mode.XSCALE;
                } else if (isRectContain(dragYRect, downP)) {
                    currentMode = Mode.YSCALE;
                } else if (isRectFContain(mainRect, downP)) {
                    currentMode = Mode.MOVE;
                } else {
                    currentMode = Mode.NONE;
                }
                oldX = x;
                oldY = y;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int curX = (int) event.getX();
                int curY = (int) event.getY();
                switch (currentMode) {
                    case MOVE:
                        aParameterSet.maskMatrix.postTranslate(curX - oldX, curY - oldY);

                        oldX = curX;
                        oldY = curY;

                        float[] f = new float[] {aParameterSet.position.x, aParameterSet.position.y};
                        defMatrix.mapPoints(f);

                        oldY = curY;
                        oldX = curX;
                        onChange(aParameterSet.updatePoint((int) f[0], (int) f[1]));
                        invalidateShape();
                        break;
                    case ROTATION:

                        conP = new float[] {curX, curY};
                        tmpMatrix.mapPoints(conP);

                        float[] rotateCenter = new float[] {aParameterSet.position.x, aParameterSet.position.y};
                        defMatrix.mapPoints(rotateCenter);

                        float curRotate =
                            rotation(aParameterSet.position.x, aParameterSet.position.y, conP[0], conP[1]);

                        aParameterSet.maskMatrix.postRotate(defRotate - curRotate, rotateCenter[0], rotateCenter[1]);

                        defRotate = curRotate;

                        onChange(aParameterSet.updateRotation(aParameterSet.maskMatrix));

                        invalidateShape();
                        break;
                    case BLUR:
                        conP = new float[] {curX, curY};
                        tmpMatrix.mapPoints(conP);
                        int disY = (int) (conP[1] - downP[1]);
                        blurRect.offset(0, disY);
                        int nowDistance = spacingRectY(mainRect, blurRect);
                        if (nowDistance > max_distance + defBlur) {
                            int convDy = nowDistance - max_distance - defBlur;
                            blurRect.offset(0, convDy);
                        } else if (nowDistance < defBlur) {
                            int convDy = defBlur - nowDistance;
                            blurRect.offset(0, -convDy);
                        }
                        downP = conP;
                        int b = spacingRectY(mainRect, blurRect);
                        onChange(aParameterSet.updateBlur((float) BigDecimalUtils.mul2((b - iBlur), 0.085f, 3)));
                        invalidateShape();
                        break;
                    case XSCALE:
                        conP = new float[] {curX, curY};
                        tmpMatrix.mapPoints(conP);
                        int disX = (int) (conP[0] - downP[0]);
                        mainRect.left = mainRect.left - disX;
                        mainRect.right = mainRect.right + disX;
                        dragXRect.offset(disX, 0);
                        rotateRect.offset(disX, 0);
                        if (dragXRect.width() < 0) {
                            dragXRect.offset(dragXRect.width() / 2, 0);
                            rotateRect.offset(dragXRect.width() / 2, 0);
                            mainRect.left = aParameterSet.position.x;
                            mainRect.right = aParameterSet.position.x;
                        }
                        downP = conP;
                        if (aParameterSet instanceof CycleParameterSet) {
                            onChange(((CycleParameterSet) aParameterSet).updateXLen(mainRect));
                        }
                        invalidateShape();
                        break;
                    case YSCALE:
                        conP = new float[] {curX, curY};
                        tmpMatrix.mapPoints(conP);

                        int dy = (int) (conP[1] - downP[1]);
                        mainRect.top = mainRect.top - dy;
                        mainRect.bottom = mainRect.bottom + dy;
                        dragYRect.offset(0, dy);
                        blurRect.offset(0, -dy);
                        rotateRect.offset(0, dy);
                        if (dragYRect.height() < 0) {
                            dragYRect.offset(0, dragYRect.height() / 2);
                            blurRect.offset(0, -dragYRect.height() / 2);
                            rotateRect.offset(0, dragYRect.height() / 2);
                            mainRect.top = aParameterSet.position.y;
                            mainRect.bottom = aParameterSet.position.y;
                        }
                        downP = conP;
                        onChange(((CycleParameterSet) aParameterSet).updateYLen(mainRect));
                        invalidateShape();
                        break;
                    case NONE:
                        break;
                    default:
                        break;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public class CycleParameterSet extends ParameterSet {
        public int xLen;

        public int yLen;

        public CycleParameterSet updateXLen(RectF rect) {
            xLen = (int) rect.width();
            return this;
        }

        public CycleParameterSet updateYLen(RectF rect) {
            yLen = (int) rect.height();
            return this;
        }

        public int getxLen() {
            return xLen;
        }

        public void setxLen(int xLen) {
            this.xLen = xLen;
        }

        public int getyLen() {
            return yLen;
        }

        public void setyLen(int yLen) {
            this.yLen = yLen;
        }
    }

    private boolean isRectContain(Rect rect, float[] point) {
        return rect.contains((int) point[0], (int) point[1]);
    }

    private boolean isRectFContain(RectF rectf, float[] point) {
        return rectf.contains(point[0], point[1]);
    }

    protected int spacingRectY(RectF rectF, Rect rect) {
        return (int) (rectF.top - rect.bottom);
    }
}
