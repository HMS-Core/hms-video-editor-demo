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

package com.huawei.hms.videoeditor.ui.mediaeditor.blockface.cropimage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class CropZoomImageView extends AppCompatImageView
    implements OnScaleGestureListener, OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = CropZoomImageView.class.getSimpleName();

    private static float SCALE_MAX = 4.0f;

    private static float SCALE_MID = 2.0f;

    private float mInitScale = 1.0f;

    private boolean oOnce = true;

    private final float[] mMatrixValues = new float[9];

    private ScaleGestureDetector sScaleGestureDetector;

    private final Matrix mScaleMatrix = new Matrix();

    private GestureDetector gGestureDetector;

    private boolean mIsAutoScale;

    private static final int TOUCH_SLOP = 0;

    private float lastX;

    private float lastY;

    private boolean mIsCanDrag;

    private int mLastPointerCount;

    private int mHorizontalPadding;

    public CropZoomImageView(Context context) {
        this(context, null);
    }

    public CropZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setScaleType(ScaleType.MATRIX);
        setBackgroundColor(context.getResources().getColor(R.color.edit_background));
        gGestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mIsAutoScale) {
                    return true;
                }
                float x = e.getX();
                float y = e.getY();
                if (getScale() < SCALE_MID) {
                    CropZoomImageView.this.postDelayed(new ScaleRunnable(SCALE_MID, x, y), 16);
                    mIsAutoScale = true;
                } else {
                    CropZoomImageView.this.postDelayed(new ScaleRunnable(mInitScale, x, y), 16);
                    mIsAutoScale = true;
                }

                return true;
            }
        });
        sScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);
    }

    private class ScaleRunnable implements Runnable {
        static final float SMALLER = 0.93f;

        static final float BIGGER = 1.07f;

        private float tTargetScale;

        private float tTmpScale;

        private float positonX;

        private float positonY;

        ScaleRunnable(float targetScale, float positonX, float positonY) {
            this.tTargetScale = targetScale;
            this.positonX = positonX;
            this.positonY = positonY;
            if (getScale() < this.tTargetScale) {
                tTmpScale = BIGGER;
            } else {
                tTmpScale = SMALLER;
            }

        }

        @Override
        public void run() {
            mScaleMatrix.postScale(tTmpScale, tTmpScale, positonX, positonY);
            checkBorders();
            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            if (((tTmpScale > 1f) && (currentScale < tTargetScale))
                || ((tTmpScale < 1f) && (tTargetScale < currentScale))) {
                CropZoomImageView.this.postDelayed(this, 16);
            } else {
                final float deltaScale = tTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, positonX, positonY);
                checkBorders();
                setImageMatrix(mScaleMatrix);
                mIsAutoScale = false;
            }

        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float sScale = getScale();
        float sScaleFactor = detector.getScaleFactor();

        if (getDrawable() == null) {
            return true;
        }

        if ((sScale < SCALE_MAX && sScaleFactor > 1.0f) || (sScale > mInitScale && sScaleFactor < 1.0f)) {
            if (sScaleFactor * sScale < mInitScale) {
                sScaleFactor = mInitScale / sScale;
            }
            if (sScaleFactor * sScale > SCALE_MAX) {
                sScaleFactor = SCALE_MAX / sScale;
            }
            mScaleMatrix.postScale(sScaleFactor, sScaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorders();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    private RectF getMatrixRectF() {
        RectF rRect = new RectF();
        Drawable drawable = getDrawable();
        if (null != drawable) {
            rRect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mScaleMatrix.mapRect(rRect);
        }
        return rRect;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (gGestureDetector.onTouchEvent(motionEvent)) {
            return true;
        }
        sScaleGestureDetector.onTouchEvent(motionEvent);

        float x = 0;
        float y = 0;
        final int pPointerCount = motionEvent.getPointerCount();
        for (int i = 0; i < pPointerCount; i++) {
            x += motionEvent.getX(i);
            y += motionEvent.getY(i);
        }
        x = x / pPointerCount;
        y = y / pPointerCount;

        if (pPointerCount != mLastPointerCount) {
            mIsCanDrag = false;
            lastX = x;
            lastY = y;
        }

        mLastPointerCount = pPointerCount;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - lastX;
                float dy = y - lastY;

                if (!mIsCanDrag) {
                    mIsCanDrag = isCanDrag(dx, dy);
                }
                if (mIsCanDrag) {
                    if (getDrawable() != null) {

                        RectF rectF = getMatrixRectF();
                        if (rectF.width() <= getWidth() - mHorizontalPadding * 2) {
                            dx = 0;
                        }
                        if (rectF.height() <= getHeight() - getHVerticalPadding() * 2) {
                            dy = 0;
                        }
                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorders();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                lastX = x;
                lastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
            default:
                SmartLog.i(TAG, "OnTouch run in default case");
        }

        return true;
    }

    public final float getScale() {
        mScaleMatrix.getValues(mMatrixValues);
        return mMatrixValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (oOnce) {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            int wWidth = getWidth();
            int hHeight = getHeight();
            int dDrawableW = drawable.getIntrinsicWidth();
            int dDrawableH = drawable.getIntrinsicHeight();
            float sScale = 1.0f;

            int frameSize = getWidth() - mHorizontalPadding * 2;

            if (dDrawableW > frameSize && dDrawableH < frameSize) {
                sScale = 1.0f * frameSize / dDrawableH;
            } else if (dDrawableH > frameSize && dDrawableW < frameSize) {
                sScale = 1.0f * frameSize / dDrawableW;
            } else if (dDrawableW > frameSize && dDrawableH > frameSize) {
                float scaleW = frameSize * 1.0f / dDrawableW;
                float scaleH = frameSize * 1.0f / dDrawableH;
                sScale = Math.max(scaleW, scaleH);
            }

            if (dDrawableW < frameSize && dDrawableH > frameSize) {
                sScale = 1.0f * frameSize / dDrawableW;
            } else if (dDrawableH < frameSize && dDrawableW > frameSize) {
                sScale = 1.0f * frameSize / dDrawableH;
            } else if (dDrawableW < frameSize && dDrawableH < frameSize) {
                float scaleW = 1.0f * frameSize / dDrawableW;
                float scaleH = 1.0f * frameSize / dDrawableH;
                sScale = Math.max(scaleW, scaleH);
            }

            mInitScale = sScale;
            SCALE_MID = mInitScale * 2;
            SCALE_MAX = mInitScale * 4;
            mScaleMatrix.postTranslate((wWidth - dDrawableW) / 2.0F, (hHeight - dDrawableH) / 2.0F);
            mScaleMatrix.postScale(sScale, sScale, getWidth() / 2.0F, getHeight() / 2.0F);

            setImageMatrix(mScaleMatrix);
            oOnce = false;
        }
    }

    public Bitmap clip() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return changeImgSize(bitmap);
    }

    public Bitmap changeImgSize(Bitmap bitmap) {
        Bitmap pic = Bitmap.createBitmap(bitmap, mHorizontalPadding, getHVerticalPadding(),
            getWidth() - 2 * mHorizontalPadding, getWidth() - 2 * mHorizontalPadding);
        int width = pic.getWidth();
        int height = pic.getHeight();
        float scaleWidth = 500.0f / width;
        float scaleHeight = 500.0f / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(pic, 0, 0, width, height, matrix, true);
    }

    private void checkBorders() {
        RectF rRect = getMatrixRectF();
        float dDeltaX = 0;
        float dDeltaY = 0;

        int wWidth = getWidth();
        int hHeight = getHeight();

        if (rRect.width() + 0.01 >= wWidth - 2 * mHorizontalPadding) {
            if (rRect.left > mHorizontalPadding) {
                dDeltaX = -rRect.left + mHorizontalPadding;
            }

            if (rRect.right < wWidth - mHorizontalPadding) {
                dDeltaX = wWidth - mHorizontalPadding - rRect.right;
            }
        }

        if (rRect.height() >= hHeight - 2 * getHVerticalPadding()) {
            if (rRect.top > getHVerticalPadding()) {
                dDeltaY = -rRect.top + getHVerticalPadding();
            }

            if (rRect.bottom < hHeight - getHVerticalPadding()) {
                dDeltaY = hHeight - getHVerticalPadding() - rRect.bottom;
            }
        }

        mScaleMatrix.postTranslate(dDeltaX, dDeltaY);
    }

    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= TOUCH_SLOP;
    }

    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    private int getHVerticalPadding() {
        return (getHeight() - (getWidth() - 2 * mHorizontalPadding)) / 2;
    }
}
