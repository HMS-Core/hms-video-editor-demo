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
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class RoundRectMask extends MaskShape {
    private Drawable blurDrawable;

    private Drawable rotateDrawable;

    private Drawable roundDrawable;

    private Drawable dragXDrawable;

    private Drawable dragYDrawable;

    private Rect blurRect;

    private Rect rotateRect;

    private Rect roundRect;

    private Rect dragXRect;

    private Rect dragYRect;

    private RectF lineRect;

    private Matrix defaultMatrix;

    private Matrix tmpMatrix;

    private int defBlurDis;

    private int defXDis;

    private int defYDis;

    private int defRoundDis;

    private int defRotateDis;

    private int defRoundOffDis;

    public float lineWidthRate = 0.5f;

    public RoundRectMask(Context context) {
        super(context);
        blurMuti = 5;
        aParameterSet = new RoundRectParameterSet();
    }

    @Override
    protected void initShape(Context context) {

        defaultMatrix = new Matrix();
        tmpMatrix = new Matrix();

        blurDrawable = context.getDrawable(R.drawable.ico_trans);
        rotateDrawable = context.getDrawable(R.drawable.ico_rotate);
        roundDrawable = context.getDrawable(R.drawable.mask_roate);
        dragXDrawable = context.getDrawable(R.drawable.ico_drag_x);
        dragYDrawable = context.getDrawable(R.drawable.ico_drag_y);

        blurRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        rotateRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        roundRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        dragXRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        dragYRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        int xOff = 0;
        int yOff = 0;
        if (aParameterSet instanceof RoundRectParameterSet) {
            xOff = ((RoundRectParameterSet) aParameterSet).defLineRectX;
            yOff = ((RoundRectParameterSet) aParameterSet).defLineRectY;
        }
        lineRect = new RectF(aParameterSet.position.x - xOff, aParameterSet.position.y + yOff,
            aParameterSet.position.x + xOff, aParameterSet.position.y - yOff);

        defBlurDis = ScreenUtil.dp2px(20);
        defXDis = ScreenUtil.dp2px(20);
        defYDis = ScreenUtil.dp2px(20);
        defRoundDis = ScreenUtil.dp2px(16);
        defRotateDis = ScreenUtil.dp2px(16);

        int curBlurDis = (int) (aParameterSet.getMaskBlur() / blurMuti);
        int xl = ((RoundRectParameterSet) aParameterSet).defLineRectX;
        int yl = ((RoundRectParameterSet) aParameterSet).defLineRectY;
        double angle;
        if (xl == 0) {
            angle = (double) 90;
        } else {
            angle = (double) Math.toDegrees(Math.tan((double) yl / (double) xl));
        }
        float curRoundSize = ((RoundRectParameterSet) aParameterSet).defRoundAgle;
        int ox = (int) (Math.sin(angle) * curRoundSize);
        int oy = (int) (Math.cos(angle) * curRoundSize);
        if (Math.abs(ox) > Math.abs(oy)) {
            oy = ox;
        } else if (Math.abs(ox) < Math.abs(oy)) {
            ox = oy;
        }
        blurRect.offset(0, -yOff - defBlurDis - curBlurDis);
        rotateRect.offset(+xOff + defRotateDis, +yOff + defRotateDis);
        roundRect.offset(-xOff - defRoundDis - ox, +yOff + defRoundDis + oy);
        dragXRect.offset(+xOff + defXDis, 0);
        dragYRect.offset(0, +yOff + defYDis);

        defRoundOffDis = (int) Math.sqrt((defRoundDis + icoWidthHalf) * (defRoundDis + icoWidthHalf));

        blurDrawable.setBounds(blurRect);
        rotateDrawable.setBounds(rotateRect);
        roundDrawable.setBounds(roundRect);
        dragXDrawable.setBounds(dragXRect);
        dragYDrawable.setBounds(dragYRect);
    }

    @Override
    public void drawShape(Canvas canvas, Paint linePaint, Paint bitmapPaint) {
        super.drawShape(canvas, linePaint, bitmapPaint);

        if (defaultMatrix == null) {
            return;
        }

        canvas.save();
        defaultMatrix.set(aParameterSet.pipMatrix);
        defaultMatrix.postConcat(aParameterSet.maskMatrix);
        canvas.setMatrix(defaultMatrix);

        canvas.drawCircle(aParameterSet.position.x, aParameterSet.position.y, cycleR, linePaint);
        if (aParameterSet instanceof RoundRectParameterSet) {
            canvas.drawRoundRect(lineRect, ((RoundRectParameterSet) aParameterSet).defRoundAgle,
                ((RoundRectParameterSet) aParameterSet).defRoundAgle, linePaint);
        }

        blurDrawable.setBounds(blurRect);
        rotateDrawable.setBounds(rotateRect);
        roundDrawable.setBounds(roundRect);
        dragXDrawable.setBounds(dragXRect);
        dragYDrawable.setBounds(dragYRect);

        blurDrawable.draw(canvas);
        rotateDrawable.draw(canvas);
        roundDrawable.draw(canvas);
        dragXDrawable.draw(canvas);
        dragYDrawable.draw(canvas);

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
        if (defaultMatrix == null) {
            return false;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                oldX = (int) event.getX();
                oldY = (int) event.getY();

                // 把当前点逆矩阵
                downP = new float[] {oldX, oldY};
                defaultMatrix.invert(tmpMatrix);
                tmpMatrix.mapPoints(downP);

                if (isContainers(blurRect, downP)) {
                    currentMode = Mode.BLUR;
                } else if (isContainers(dragXRect, downP)) {
                    currentMode = Mode.XDRAG;
                } else if (isContainers(dragYRect, downP)) {
                    currentMode = Mode.YDRAG;
                } else if (isContainers(roundRect, downP)) {
                    currentMode = Mode.ROUNDDRAG;
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
                int currentY = (int) event.getY();

                switch (currentMode) {
                    case MOVE:
                        Matrix matrix = new Matrix();
                        aParameterSet.pipMatrix.invert(matrix);
                        conP = new float[] {currentX, currentY};
                        matrix.mapPoints(conP);

                        float[] oldP = new float[] {oldX, oldY};
                        matrix.mapPoints(oldP);

                        int pipOffX = (int) (conP[0] - oldP[0]);
                        int pipOffY = (int) (conP[1] - oldP[1]);
                        if (pipOffX == 0 && pipOffY == 0) {
                            break;
                        }

                        float[] f = new float[] {aParameterSet.position.x, aParameterSet.position.y};
                        defaultMatrix.mapPoints(f);

                        matrix.mapPoints(f);

                        Point p = new Point((int) f[0], (int) f[1]);
                        Offset offset = measureOffsetSafe(p, pipOffX, pipOffY, pipRect);
                        float[] f2 = new float[] {offset.offX, offset.offY};
                        matrix.mapVectors(f2);

                        aParameterSet.maskMatrix.postTranslate(f2[0], f2[1]);

                        onChange(aParameterSet.updatePointLine(f[0], f[1]));
                        oldY = currentY;
                        oldX = currentX;
                        invalidateShape();
                        break;
                    case ROTATION:
                        conP = new float[] {currentX, currentY};
                        tmpMatrix.mapPoints(conP);

                        float[] rotateCenter = new float[] {aParameterSet.position.x, aParameterSet.position.y};
                        defaultMatrix.mapPoints(rotateCenter);

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
                        conP = new float[] {currentX, currentY};
                        tmpMatrix.mapPoints(conP);
                        int disY = (int) (conP[1] - downP[1]);
                        if (disY == 0) {
                            break;
                        }

                        int ifDis = (int) (lineRect.bottom - blurRect.bottom - disY);
                        int curDis = (int) (lineRect.bottom - blurRect.bottom);

                        if (ifDis > (max_distance + defBlurDis - icoWidthHalf)) {
                            disY = curDis - max_distance - defBlurDis + icoWidthHalf;
                        } else if (ifDis < (defBlurDis - icoWidthHalf)) {
                            disY = curDis - defBlurDis + icoWidthHalf;
                        }
                        blurRect.offset(0, disY);
                        downP = conP;
                        ifDis = (int) (lineRect.bottom - blurRect.bottom + icoWidthHalf - defBlurDis);
                        onChange(aParameterSet.updateBlur(ifDis * blurMuti));
                        invalidateShape();
                        break;
                    case XDRAG:
                        conP = new float[] {currentX, currentY};
                        tmpMatrix.mapPoints(conP);
                        int dx = (int) (conP[0] - downP[0]);
                        if (dx == 0) {
                            break;
                        }
                        dragXRect.offset(dx, 0);
                        roundRect.offset(-dx, 0);
                        rotateRect.offset(dx, 0);
                        lineRect.left = lineRect.left - dx;
                        lineRect.right = lineRect.right + dx;
                        downP = conP;
                        if (aParameterSet instanceof RoundRectParameterSet) {
                            onChange(((RoundRectParameterSet) aParameterSet)
                                .updateRectX((int) (Math.abs(lineRect.width()) / 2)));
                        }
                        invalidateShape();
                        break;
                    case YDRAG:
                        conP = new float[] {currentX, currentY};
                        tmpMatrix.mapPoints(conP);
                        int dy = (int) (conP[1] - downP[1]);
                        if (dy == 0) {
                            break;
                        }
                        dragYRect.offset(0, dy);
                        blurRect.offset(0, -dy);
                        roundRect.offset(0, dy);
                        rotateRect.offset(0, dy);
                        lineRect.bottom = lineRect.bottom - dy;
                        lineRect.top = lineRect.top + dy;
                        downP = conP;
                        onChange(((RoundRectParameterSet) aParameterSet)
                            .updateRectY((int) (Math.abs(lineRect.height()) / 2)));
                        invalidateShape();
                        break;

                    case ROUNDDRAG:
                        conP = new float[] {currentX, currentY};
                        tmpMatrix.mapPoints(conP);
                        dx = (int) (conP[0] - downP[0]);
                        dy = (int) (conP[1] - downP[1]);
                        if (dx * dy > 0) {
                            break;
                        }

                        if (Math.abs(dx) > Math.abs(dy)) {
                            dy = -dx;
                        } else if (Math.abs(dx) < Math.abs(dy)) {
                            dx = -dy;
                        }

                        roundRect.offset(dx, dy);

                        int dRound = spacing(lineRect.left, lineRect.top, roundRect);

                        if (dRound > max_distance + defRoundOffDis) {
                            int convDy = (int) ((max_distance + defRoundOffDis - dRound) / Math.sqrt(2));
                            roundRect.offset(-convDy, convDy);
                        } else if (dRound < defRoundOffDis) {
                            int convDy = (int) ((defRoundOffDis - dRound) / Math.sqrt(2));
                            roundRect.offset(-convDy, convDy);
                        }

                        downP = conP;
                        dRound = spacing(lineRect.left, lineRect.top, roundRect) - defRoundOffDis;
                        onChange(((RoundRectParameterSet) aParameterSet).updateRoundsize(dRound));
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
        XDRAG,
        YDRAG,
        ROUNDDRAG,
        NONE;
    }

    public class RoundRectParameterSet extends ParameterSet {
        private int defLineRectX;

        private int defLineRectY;

        private float defRoundAgle;

        private float round;

        public RoundRectParameterSet updateRectX(int x) {
            defLineRectX = x;
            updateRoundsize(round);
            return this;
        }

        public RoundRectParameterSet updateRectY(int y) {
            defLineRectY = y;
            updateRoundsize(round);
            return this;
        }

        public RoundRectParameterSet updateRoundsize(float round) {
            this.round = round;
            int r;
            if (defLineRectX > defLineRectY) {
                r = defLineRectY;
            } else {
                r = defLineRectX;
            }
            this.defRoundAgle = r * round * 1.0f / max_distance;
            return this;
        }

        public int getMaskRectX() {
            return defLineRectX * 2;
        }

        public int getMaskRectY() {
            return defLineRectY * 2;
        }

        public void setMaskRectX(int rectX) {
            this.defLineRectX = rectX / 2;
        }

        public void setMaskRectY(int rectY) {
            this.defLineRectY = rectY / 2;
        }

        public float getDefRoundAgle() {
            return defRoundAgle;
        }

        public void setDefRoundAgle(float defRoundAgle) {
            int r;
            if (defLineRectX > defLineRectY) {
                r = defLineRectY;
            } else {
                r = defLineRectX;
            }
            round = max_distance * defRoundAgle * 1.0f / r;
            this.defRoundAgle = defRoundAgle;
        }
    }
}
