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
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.DrawableRes;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public class StickerMask extends MaskShape {
    private static final String TAG = "StickerMask";

    private Drawable blurDrawable;

    private Drawable rotateDrawable;

    private VectorDrawableCompat vectorDrawableCompat;

    private Matrix cDefMatrix;

    private Matrix cTmpMatrix;

    private Rect blurRect;

    private Rect rotateRect;

    private int defBlurDis;

    public float lineWidthRate = 0.8f;

    private Rect vectorRect;

    public float blurMuti = 0.01f;

    @DrawableRes
    public int vectorId;

    public StickerMask(Context context) {
        super(context);
        aParameterSet = new StickerSet();
    }

    @Override
    protected void initShape(Context context) {
        cDefMatrix = new Matrix();
        cTmpMatrix = new Matrix();
        defBlurDis = ScreenUtil.dp2px(20);

        int xOff = 0;
        int yOff = 0;
        if (aParameterSet instanceof StickerMask.StickerSet) {
            xOff = ((StickerSet) aParameterSet).defVectorWidthHalf;
            yOff = ((StickerSet) aParameterSet).defVectorHeightHalf;
        }

        blurRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        float blur = aParameterSet.getMaskBlur();
        blurRect.offset(0, (int) (-yOff - defBlurDis - (blur / blurMuti)));

        rotateRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, icoWidthHalf);
        rotateRect.offset(xOff, yOff);

        blurDrawable = context.getDrawable(R.drawable.ico_trans);
        blurDrawable.setBounds(blurRect);
        rotateDrawable = context.getDrawable(R.drawable.ico_rotate);
        rotateDrawable.setBounds(rotateRect);

        vectorRect = getCenterRect(aParameterSet.position.x, aParameterSet.position.y, xOff, yOff);
        vectorDrawableCompat = VectorDrawableCompat.create(context.getResources(), vectorId, null);
        if (vectorDrawableCompat != null) {
            vectorDrawableCompat.setBounds(vectorRect);
        }
    }

    @Override
    public void drawShape(Canvas canvas, Paint linePaint, Paint bitmapPaint) {
        super.drawShape(canvas, linePaint, bitmapPaint);

        if (cDefMatrix == null) {
            return;
        }

        canvas.save();
        cDefMatrix.set(aParameterSet.pipMatrix);
        cDefMatrix.postConcat(aParameterSet.maskMatrix);
        canvas.setMatrix(cDefMatrix);

        vectorDrawableCompat.setBounds(vectorRect);
        blurDrawable.setBounds(blurRect);
        rotateDrawable.setBounds(rotateRect);

        vectorDrawableCompat.draw(canvas);
        blurDrawable.draw(canvas);
        rotateDrawable.draw(canvas);

        canvas.restore();

    }

    enum Mode {
        MOVE,
        ROTATION,
        BLUR,
        SCALE,
        NONE
    }

    private float cOldX;

    private float cOldY;

    private float[] downP;

    private float[] cConP;

    private Mode currentMode = Mode.NONE;

    private float cDefRotate;

    private float defDistance;

    @Override
    public boolean onTouch(MotionEvent event) {
        if (cDefMatrix == null) {
            return false;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                cOldX = event.getX();
                cOldY = event.getY();
                downP = new float[] {cOldX, cOldY};

                cDefMatrix.invert(cTmpMatrix);
                cTmpMatrix.mapPoints(downP);

                if (isContainers(blurRect, downP)) {
                    currentMode = Mode.BLUR;
                } else if (isContainers(rotateRect, downP)) {
                    currentMode = Mode.ROTATION;
                    cDefRotate = rotation(aParameterSet.position.x, aParameterSet.position.y, downP[0], downP[1]);
                } else if (isContainers(vectorRect, downP)) {
                    currentMode = Mode.MOVE;
                } else {
                    currentMode = Mode.NONE;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float curX = event.getX();
                float curY = event.getY();

                switch (currentMode) {
                    case BLUR:
                        cConP = new float[] {curX, curY};
                        cTmpMatrix.mapPoints(cConP);
                        int disY = (int) (cConP[1] - downP[1]);
                        if (disY == 0) {
                            break;
                        }

                        int ifDis = (int) (vectorRect.top - blurRect.bottom - disY);
                        int curDis = (int) (vectorRect.top - blurRect.bottom);

                        if (ifDis > (max_distance + defBlurDis - icoWidthHalf)) {
                            disY = curDis - max_distance - defBlurDis + icoWidthHalf;
                        } else if (ifDis < (defBlurDis - icoWidthHalf)) {
                            disY = curDis - defBlurDis + icoWidthHalf;
                        }
                        blurRect.offset(0, disY);
                        downP = cConP;
                        ifDis = (int) (vectorRect.top - blurRect.bottom + icoWidthHalf - defBlurDis);
                        onChange(aParameterSet.updateBlur(ifDis * blurMuti));
                        invalidateShape();
                        break;

                    case MOVE:
                        Matrix matrix = new Matrix();
                        aParameterSet.pipMatrix.invert(matrix);

                        cConP = new float[] {curX, curY};
                        matrix.mapPoints(cConP);

                        float[] oldP = new float[] {cOldX, cOldY};
                        matrix.mapPoints(oldP);

                        int pipOffX = (int) (cConP[0] - oldP[0]);
                        int pipOffY = (int) (cConP[1] - oldP[1]);
                        if (pipOffX == 0 && pipOffY == 0) {
                            break;
                        }

                        float[] f = new float[] {aParameterSet.position.x, aParameterSet.position.y};
                        cDefMatrix.mapPoints(f);

                        matrix.mapPoints(f);

                        Point p = new Point((int) f[0], (int) f[1]);
                        Offset offset = measureOffsetSafe(p, pipOffX, pipOffY, pipRect);
                        float[] f2 = new float[] {offset.offX, offset.offY};
                        matrix.mapVectors(f2);

                        aParameterSet.maskMatrix.postTranslate(f2[0], f2[1]);

                        onChange(aParameterSet.updatePointLine(f[0], f[1]));
                        cOldY = curY;
                        cOldX = curX;
                        invalidateShape();
                        break;

                    case ROTATION:
                        cConP = new float[] {curX, curY};
                        cTmpMatrix.mapPoints(cConP);

                        float[] cRotateCenter = new float[] {aParameterSet.position.x, aParameterSet.position.y};
                        cDefMatrix.mapPoints(cRotateCenter);

                        float cCurRotate =
                            rotation(aParameterSet.position.x, aParameterSet.position.y, cConP[0], cConP[1]);
                        if (cDefRotate - cCurRotate == 0) {
                            break;
                        }

                        aParameterSet.maskMatrix.postRotate(cDefRotate - cCurRotate, cRotateCenter[0],
                            cRotateCenter[1]);

                        cDefRotate = cCurRotate;

                        onChange(aParameterSet.updateRotation(aParameterSet.maskMatrix));

                        invalidateShape();
                        break;

                    case SCALE:
                        if (event.getPointerCount() == 2) {
                            float ro = rotation(event);
                            float rotation = ro - cDefRotate;

                            float newDistance = spacing(event);
                            float scale = newDistance / defDistance;

                            int blurOffY = blurRect.bottom - vectorRect.top;

                            float[] rc = new float[] {aParameterSet.position.x, aParameterSet.position.y};
                            cDefMatrix.mapPoints(rc);

                            aParameterSet.maskMatrix.postRotate(rotation, rc[0], rc[1]);

                            float w = vectorRect.width() * scale;
                            Rect rect = getCenterRect(vectorRect.centerX(), vectorRect.centerY(), (int) (w / 2));

                            vectorRect.set(rect);

                            cDefRotate = ro;
                            defDistance = newDistance;

                            if (aParameterSet instanceof StickerSet) {
                                ((StickerSet) aParameterSet).updateSize(vectorRect.width(), vectorRect.height());
                            }

                            onChange(aParameterSet.updateRotation(aParameterSet.maskMatrix));

                            blurRect.bottom = vectorRect.top + blurOffY;
                            blurRect.top = blurRect.bottom - (2 * icoWidthHalf);

                            rotateRect = getCenterRect(vectorRect.right, vectorRect.bottom, icoWidthHalf);

                            invalidateShape();

                        }
                        break;
                    default:
                        SmartLog.i(TAG, "ACTION_MOVE run in default case");
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    currentMode = Mode.SCALE;
                    cDefRotate = rotation(event);
                    defDistance = spacing(event);
                    cTmpMatrix.reset();
                } else {
                    currentMode = Mode.NONE;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                SmartLog.i(TAG, "onTouch run in default case");
        }

        return true;
    }

    public class StickerSet extends ParameterSet {
        public float scale;

        private int defVectorWidthHalf;

        private int defVectorHeightHalf;

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public int getDefVectorWidthHalf() {
            return defVectorWidthHalf;
        }

        public void setDefVectorWidth(int defVectorWidthHalf) {
            this.defVectorWidthHalf = (int) (defVectorWidthHalf / 2.0f);
        }

        public int getDefVectorHeightHalf() {
            return defVectorHeightHalf;
        }

        public void setDefVectorHeight(int defVectorHeightHalf) {
            this.defVectorHeightHalf = (int) (defVectorHeightHalf / 2.0f);
        }

        public int getMaskRectX() {
            return defVectorWidthHalf * 2;
        }

        public int getMaskRectY() {
            return defVectorHeightHalf * 2;
        }

        public StickerSet updateSize(int w, int h) {
            defVectorWidthHalf = w / 2;
            defVectorHeightHalf = h / 2;
            return this;
        }

    }
}
