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

import java.util.Objects;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

/**
 * CropView
 */
public class CropView extends View {
    private static final String TAG = "CropView";

    private static final int MAXOFF = 5;

    private final RectF imageBounds = new RectF();

    private final RectF screenBounds = new RectF();

    private final RectF mScreenImageBounds = new RectF();

    private final RectF mScreenCropBounds = new RectF();

    private final Paint mPaint = new Paint();

    private final Paint mShadowPaint = new Paint();

    private final TextPaint mTextPaint = new TextPaint();

    private ITouchListener mTouchListener;

    private int rotation = 0;

    private CropObject mCropObject = null;

    private int mIndicatorSize;

    private Matrix mMatrixInverse = null;

    private float mY = 0;

    private Matrix matrix = null;

    private float mSpotY = 0;

    private boolean dirty = false;

    private Drawable[] mCropIndicators;

    private float mX = 0;

    private int margin = 32;

    private int mOverlayShadowColor = 0xCF000000;

    private float mDashOnLength = 20;

    private float mSpotX = 0;

    private float mDashOffLength = 10;

    private float mAspectTextSize = 20;

    private boolean mDoSpot = false;

    private int mWPMarkerColor = 0x7FFFFFFF;

    private int mMinSideSize = 90;

    private int mOverlayWPShadowColor = 0x5F000000;

    private int mTouchTolerance = 40;

    private boolean isCanMove = true;

    private int px = 0;

    private int py = 0;

    private boolean bLock = false;

    private Mode mModeState = Mode.NONE;

    private enum Mode {
        NONE,
        MOVE
    }

    public CropView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        setUp(context);
    }

    public CropView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setUp(context);
    }

    public CropView(Context context) {
        super(context);
        setUp(context);
    }

    public void setOverlayShadowColor(int color) {
        mOverlayShadowColor = color;
    }

    private void setUp(Context context) {
        if (isInEditMode()) {
            return;
        }
        Resources rsc = context.getResources();
        mCropIndicators = new Drawable[] {rsc.getDrawable(R.drawable.video_crop_top_left),
            rsc.getDrawable(R.drawable.video_crop_top_right), rsc.getDrawable(R.drawable.video_crop_bottom_left),
            rsc.getDrawable(R.drawable.video_crop_bottom_right), rsc.getDrawable(R.drawable.video_crop_move_left_right),
            rsc.getDrawable(R.drawable.video_crop_move_top_bottom)};

        mIndicatorSize = (int) rsc.getDimension(R.dimen.crop_indicator_size);
        margin = (int) rsc.getDimension(R.dimen.preview_margin);
        mMinSideSize = (int) rsc.getDimension(R.dimen.crop_min_side);
        mTouchTolerance = (int) rsc.getDimension(R.dimen.crop_touch_tolerance);
        mOverlayShadowColor = rsc.getColor(R.color.crop_shadow_color);
        mOverlayWPShadowColor = rsc.getColor(R.color.crop_shadow_wp_color);
        mWPMarkerColor = rsc.getColor(R.color.crop_wp_markers);
        mDashOnLength = rsc.getDimension(R.dimen.wp_selector_dash_length);
        mDashOffLength = rsc.getDimension(R.dimen.wp_selector_off_length);
        mAspectTextSize = rsc.getDimension(R.dimen.crop_aspect_text_size);
    }

    private static void rotate(RectF dest, RectF out) {
        float width = dest.width();
        float height = dest.height();
        float left = dest.left;
        float top = dest.top;

        dest.top = out.width() - width - left;
        dest.left = top;
        dest.bottom = out.width() - left;
        dest.right = top + height;
    }

    public void initialize(RectF newCropBounds, RectF newPhotoBounds, int rotation) {
        if (newCropBounds == null || newPhotoBounds == null) {
            return;
        }

        imageBounds.set(newPhotoBounds);
        if (mCropObject != null) {
            RectF crop = mCropObject.getInnerBounds();
            RectF containing = mCropObject.getOuterBounds();
            if (crop != newCropBounds || containing != newPhotoBounds || this.rotation != rotation) {
                this.rotation = rotation;
                if (!BoundedRect.isConstrained(newCropBounds, newPhotoBounds)) {
                    newCropBounds = new RectF(newPhotoBounds);
                }

                mCropObject.resetBoundsTo(newCropBounds, newPhotoBounds);
                clearDisplay();
            } else {
                invalidate();
            }
        } else {
            this.rotation = rotation;
            mCropObject = new CropObject(newPhotoBounds, newCropBounds, 0);
            clearDisplay();
        }
    }

    public RectF getCropF() {
        RectF clip = new RectF(getCrop());
        RectF photo = getPhoto();
        if (photo == null) {
            return new RectF(0, 0, 1f, 1f);
        }
        clip.left /= photo.width();
        clip.top /= photo.height();
        clip.right /= photo.width();
        clip.bottom /= photo.height();
        return clip;
    }

    public RectF getCrop() {
        if (mCropObject == null) {
            return null;
        }
        return mCropObject.getInnerBounds();
    }

    public RectF getPhoto() {
        if (mCropObject == null) {
            return null;
        }
        return mCropObject.getOuterBounds();
    }

    public RectF getRotateToCrop() {
        RectF rectF = new RectF(getCrop());

        if (Objects.equals(rectF.width(), getPhoto().width()) || Objects.equals(rectF.height(), getPhoto().height())) {
            return new RectF(getPhoto());
        }

        rotate(rectF, getPhoto());

        return rectF;
    }

    public void setLockSize(boolean lock) {
        bLock = lock;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float eventX = motionEvent.getX();
        float eventY = motionEvent.getY();
        if (matrix == null || mMatrixInverse == null) {
            return true;
        }
        float[] points = {eventX, eventY};
        mMatrixInverse.mapPoints(points);
        eventX = points[0];
        eventY = points[1];
        switch (motionEvent.getActionMasked()) {
            case (MotionEvent.ACTION_DOWN):
                if (!touchDownEvent(motionEvent, eventX, eventY)) {
                    return false;
                }
                break;
            case (MotionEvent.ACTION_UP):
            case (MotionEvent.ACTION_CANCEL):
                touchUpAndCancelEvent(motionEvent, eventX, eventY);
                break;
            case (MotionEvent.ACTION_MOVE):
                if (mModeState == Mode.MOVE) {
                    if (null != icropListener) {
                        icropListener.onMove();
                    }
                    float dx = eventX - mX;
                    float dy = eventY - mY;
                    mCropObject.moveCurrentSelection(dx, dy);
                    mX = eventX;
                    mY = eventY;
                    if (null != mTouchListener) {
                        mTouchListener.onTouchUp();
                    }
                }
                break;
            default:
                break;
        }

        invalidate();
        return true;
    }

    private boolean touchDownEvent(MotionEvent event, float x, float y) {
        px = (int) event.getX();
        py = (int) event.getY();

        if (null != mTouchListener) {
            mTouchListener.onTouchDown();
        }

        if (null != icropListener) {
            if (!isCanMove) {
                return false;
            }
        }
        if (mModeState == Mode.NONE) {
            if (!mCropObject.selectEdge(x, y)) {
                mCropObject.selectEdge(CropObject.MOVE_BLOCK);
            }
            if (bLock) {
                mCropObject.selectEdge(CropObject.MOVE_BLOCK);
            }
            mX = x;
            mY = y;
            mModeState = Mode.MOVE;
        }
        return true;
    }

    private void touchUpAndCancelEvent(MotionEvent event, float x, float y) {
        if (null != mTouchListener) {
            mTouchListener.onTouchUp();
        }
        if (null != icropListener) {
            if ((Math.abs(event.getY() - py) > MAXOFF || Math.abs(event.getX() - px) > MAXOFF)) {
                mCropObject.selectEdge(CropObject.MOVE_NONE);
                mX = x;
                mY = y;
            } else {
                if ((Math.abs(event.getY() - py) < MAXOFF && Math.abs(event.getX() - px) < MAXOFF)) {
                    icropListener.onPlayState();
                }
            }
        } else {
            if (mModeState == Mode.MOVE) {
                mCropObject.selectEdge(CropObject.MOVE_NONE);
                mX = x;
                mY = y;
            }
        }
        mModeState = Mode.NONE;
    }

    private void clearDisplay() {
        matrix = null;
        mMatrixInverse = null;
        invalidate();
    }

    public void applyFreeAspect() {
        if (mCropObject == null) {
            return;
        }
        mCropObject.unsetAspectRatio();
        invalidate();
    }

    private void reset() {
        Log.w(TAG, "crop reset called");
        mModeState = Mode.NONE;
        mCropObject = null;
        bLock = false;
        rotation = 0;
        clearDisplay();
    }

    protected void configChanged() {
        dirty = true;
    }

    public void applyAspect(float ponitX, float pointY) {
        if (ponitX <= 0 || pointY <= 0) {
            SmartLog.e(TAG, "Bad arguments");
            return;
        }
        if (((rotation < 0) ? -rotation : rotation) % 180 == 90) {
            float tmpX = ponitX;
            ponitX = pointY;
            pointY = tmpX;
        }
        if (mCropObject == null) {
            return;
        }
        if (!mCropObject.setInnerAspectRatio(ponitX, pointY)) {
            Log.w(TAG, "failed");
        }
        invalidate();
    }

    public void setEnableDrawSelectionFrame(boolean enableDrawSelectionFrame) {
        this.enableDrawSelectionFrame = enableDrawSelectionFrame;
    }

    private boolean enableDrawSelectionFrame = true;

    private int doBitCycleLeft(int xValue, int timesValue, int d) {
        int maskValue = (1 << d) - 1;
        int mout = xValue & maskValue;
        timesValue %= d;
        int hi = mout >> (d - timesValue);
        int low = (mout << timesValue) & maskValue;
        int ret = xValue & ~maskValue;
        ret |= low;
        ret |= hi;
        return ret;
    }

    private int decode(int edges, float rota) {
        int rotation = CropMath.constrainedRotation(rota);
        switch (rotation) {
            case 90:
                return doBitCycleLeft(edges, 1, 4);
            case 180:
                return doBitCycleLeft(edges, 2, 4);
            case 270:
                return doBitCycleLeft(edges, 3, 4);
            default:
                return edges;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            configChanged();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (dirty) {
            dirty = false;
            clearDisplay();
        }
        screenBounds.set(0, 0, getWidth(), getHeight());

        screenBounds.inset(margin, margin);

        if (mCropObject == null) {
            reset();
            mCropObject = new CropObject(imageBounds, imageBounds, 0);
        }

        // If display matrix doesn't exist, create it and its dependencies
        if (matrix == null || mMatrixInverse == null) {
            matrix = new Matrix();
            matrix.reset();
            if (!CropDrawingUtils.setImageToScreenMatrix(matrix, imageBounds, screenBounds, rotation)) {
                Log.w(TAG, "failed get matrix");
                matrix = null;
                return;
            }
            mMatrixInverse = new Matrix();
            mMatrixInverse.reset();
            if (!matrix.invert(mMatrixInverse)) {
                Log.w(TAG, "invert display matrix failed");
                mMatrixInverse = null;
                return;
            }
            mCropObject.setMinInnerSideSize(mMatrixInverse.mapRadius(mMinSideSize));
            mCropObject.setTouchTolerance(mMatrixInverse.mapRadius(mTouchTolerance));
        }

        mScreenImageBounds.set(0, 0, getWidth(), getHeight());

        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        mCropObject.getInnerBounds(mScreenCropBounds);

        if (matrix.mapRect(mScreenCropBounds)) {
            // Draw overlay shadows
            mShadowPaint.setColor(mOverlayShadowColor);
            mShadowPaint.setStyle(Paint.Style.FILL);
            CropDrawingUtils.drawShadows(canvas, mShadowPaint, mScreenCropBounds, mScreenImageBounds);

            // Draw crop rect and markers
            CropDrawingUtils.drawCropRect(canvas, mScreenCropBounds);

            mTextPaint.setAntiAlias(true);
            mTextPaint.setColor(Color.WHITE);
            mTextPaint.setTextSize(mAspectTextSize);
            mTextPaint.setShadowLayer(5, 1, 1, Color.BLACK);

            if (!mUnableBorder) {
                if (null != tempState && !tempState.isRecycled()) {
                    canvas.drawBitmap(tempState,
                        mScreenCropBounds.left + (mScreenCropBounds.width() - tempState.getWidth()) / 2,
                        mScreenCropBounds.top + (mScreenCropBounds.height() - tempState.getHeight()) / 2, null);
                }
            }

            if (enableDrawSelectionFrame) {
                if (!mDoSpot) {
                    CropDrawingUtils.drawRuleOfThird(canvas, mScreenCropBounds);
                } else {
                    Paint wpPaint = new Paint();
                    wpPaint.setColor(mWPMarkerColor);
                    wpPaint.setStrokeWidth(3);
                    wpPaint.setStyle(Paint.Style.STROKE);
                    wpPaint.setPathEffect(
                        new DashPathEffect(new float[] {mDashOnLength, mDashOnLength + mDashOffLength}, 0));
                    mShadowPaint.setColor(mOverlayWPShadowColor);
                    CropDrawingUtils.drawWallpaperSelectionFrame(canvas, mScreenCropBounds, mSpotX, mSpotY, wpPaint,
                        mShadowPaint);
                }
            }

            CropDrawingUtils.drawIndicators(canvas, mCropIndicators, mIndicatorSize, mScreenCropBounds,
                mCropObject.isFixedAspect(), decode(mCropObject.getSelectState(), rotation));
        }
    }

    private Bitmap tempState;

    public void setStatebmp(Bitmap b) {
        if (null != tempState) {
            tempState.recycle();
        }
        tempState = b;
        invalidate();
    }

    public void setCanMove(boolean isCanMove) {
        this.isCanMove = isCanMove;
    }

    private boolean mUnableBorder = true;

    public void setUnAbleBorder() {

        mUnableBorder = false;
    }

    public RectF getScreenBounds() {
        return screenBounds;
    }

    public RectF getScreenImageBounds() {
        return mScreenImageBounds;
    }

    public RectF getScreenCropBounds() {
        return mScreenCropBounds;
    }

    private ICropListener icropListener;

    public void setIcropListener(ICropListener icropListener) {
        this.icropListener = icropListener;
    }

    public interface ICropListener {
        void onPlayState();

        void onMove();
    }

    public void setTouchListener(ITouchListener touchListener) {
        mTouchListener = touchListener;
    }

    public interface ITouchListener {
        void onTouchDown();

        void onTouchUp();

        void onMove();
    }
}