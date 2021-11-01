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

package com.huawei.hms.videoeditor.ui.mediaeditor.materialedit;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.bean.HVEPosition2D;
import com.huawei.hms.videoeditor.sdk.bean.HVESize;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;

public class TransformView extends View {

    private static final String TAG = "TransformView";

    public final static int DOUBLE_TAP_TIMEOUT = 500;

    protected RectF mDeleteRect;

    protected RectF mScaleRect;

    protected RectF mEditRect;

    protected RectF mCopyRect;

    protected Bitmap mDeleteBitmap;

    protected Bitmap mScaleBitmap;

    protected Bitmap mEditBitmap;

    protected Bitmap mCopyBitmap;

    private Drawable mRotateDrawable;

    private int mRotateSize;

    private int mBitmapRadius;

    private static final int STICKER_LENGTH = 40;

    private static final int STICKER_ROTATION = 5;

    private Paint mTextPaint;

    protected Paint mPaint;

    protected int mRectangularColor = Color.WHITE;

    protected List<HVEPosition2D> mHVEPosition2DList = new ArrayList<>();

    private HVESize mSize;

    private final PointF mDoubleCenterPoint = new PointF();

    protected Mode mCurrentMode = Mode.NONE;

    protected boolean isTransForm = true;

    protected boolean isTouchAble = true;

    protected boolean isDrawDelete = false;

    protected boolean isDrawEdit = false;

    protected boolean isDrawCopy = false;

    protected boolean isDrawScale = false;

    private boolean isDrawXLine = false;

    private boolean isDrawYLine = false;

    private boolean isDrawRotate = false;

    private boolean isRotateVib = true;

    private float mOldX;

    private float mOldY;

    protected float mOldDist = 1f;

    protected float mOldRotation = 0;

    private float mSrcRotation = 0;

    private int mShowRotate = 0;

    protected float mEventUpX;

    protected float mEventUpY;

    protected MotionEvent mPreviousUpEvent;

    protected MotionEvent mCurrentDownEvent;

    private final int mBitWidthMid = 36;

    private final int mBitHeightMid = 36;

    private final int mMinSize = mBitWidthMid + mBitHeightMid;

    protected OnMaterialEditListener onEditListener;

    protected boolean isEnableOutAreaLimit = false;

    protected boolean isLandscapeVideo = false;

    protected int assetWidth;

    protected int assetHeight;

    public TransformView(Context context) {
        this(context, null);
    }

    public TransformView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransformView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTransformView();
    }

    private void initTransformView() {
        mRotateSize = SizeUtils.dp2Px(getContext(), 24);
        int rotateTextSize = SizeUtils.dp2Px(getContext(), 18);
        mBitmapRadius = SizeUtils.dp2Px(getContext(), 12);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mRectangularColor);
        mPaint.setStrokeWidth(3);

        mTextPaint = new Paint();
        mTextPaint.setTextSize((float) BigDecimalUtils.mul2(rotateTextSize, 0.8f, 1));
        mTextPaint.setColor(Color.WHITE);

        mDeleteRect = new RectF(0, 0, 0, 0);
        mScaleRect = new RectF(0, 0, 0, 0);
        mEditRect = new RectF(0, 0, 0, 0);
        mCopyRect = new RectF(0, 0, 0, 0);

        if (mDeleteBitmap == null) {
            mDeleteBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icon_close_text);
        }
        if (mScaleBitmap == null) {
            mScaleBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icon_rotate_text);
        }
        if (mEditBitmap == null) {
            mEditBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icon_redact_text);
        }
        if (mCopyBitmap == null) {
            mCopyBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icon_copy_text);
        }

        if (mRotateDrawable == null) {
            mRotateDrawable = getContext().getDrawable(R.drawable.rotate_drawable);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        Path path = new Path();
        if (mHVEPosition2DList.size() != 4) {
            return;
        }
        if (mHVEPosition2DList.get(0) == null || mHVEPosition2DList.get(1) == null || mHVEPosition2DList.get(2) == null
            || mHVEPosition2DList.get(3) == null) {
            return;
        }
        path.moveTo(mHVEPosition2DList.get(0).xPos, mHVEPosition2DList.get(0).yPos);
        path.lineTo(mHVEPosition2DList.get(1).xPos, mHVEPosition2DList.get(1).yPos);
        path.lineTo(mHVEPosition2DList.get(2).xPos, mHVEPosition2DList.get(2).yPos);
        path.lineTo(mHVEPosition2DList.get(3).xPos, mHVEPosition2DList.get(3).yPos);
        path.close();

        if (isTransForm && isDrawRotate) {
            Rect rect = new Rect(getWidth() / 2 - mRotateSize - 10, mRotateSize, getWidth() / 2 + mRotateSize + 10,
                (int) (mRotateSize * 2.5f));
            mRotateDrawable.setBounds(rect);
            mRotateDrawable.draw(canvas);
            float width = mTextPaint.measureText(mShowRotate + "°");
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float height = fontMetrics.descent - fontMetrics.ascent;
            float offX = (rect.width() - width) / 2;
            float offY = (rect.height() - height) / 2;

            canvas.drawText(mShowRotate + "°", rect.left + offX, rect.bottom - offY, mTextPaint);
        }

        canvas.drawPath(path, mPaint);
    }

    private void calculateBitmapRect() {
        if (mHVEPosition2DList.get(0) == null || mHVEPosition2DList.get(1) == null || mHVEPosition2DList.get(2) == null
            || mHVEPosition2DList.get(3) == null) {
            return;
        }
        mDeleteRect.left = mHVEPosition2DList.get(1).xPos - mBitmapRadius;
        mDeleteRect.right = mHVEPosition2DList.get(1).xPos + mBitmapRadius;
        mDeleteRect.top = mHVEPosition2DList.get(1).yPos - mBitmapRadius;
        mDeleteRect.bottom = mHVEPosition2DList.get(1).yPos + mBitmapRadius;

        mEditRect.left = mHVEPosition2DList.get(2).xPos - mBitmapRadius;
        mEditRect.right = mHVEPosition2DList.get(2).xPos + mBitmapRadius;
        mEditRect.top = mHVEPosition2DList.get(2).yPos - mBitmapRadius;
        mEditRect.bottom = mHVEPosition2DList.get(2).yPos + mBitmapRadius;

        mScaleRect.left = mHVEPosition2DList.get(3).xPos - mBitmapRadius;
        mScaleRect.right = mHVEPosition2DList.get(3).xPos + mBitmapRadius;
        mScaleRect.top = mHVEPosition2DList.get(3).yPos - mBitmapRadius;
        mScaleRect.bottom = mHVEPosition2DList.get(3).yPos + mBitmapRadius;

        mCopyRect.left = mHVEPosition2DList.get(0).xPos - mBitmapRadius;
        mCopyRect.right = mHVEPosition2DList.get(0).xPos + mBitmapRadius;
        mCopyRect.top = mHVEPosition2DList.get(0).yPos - mBitmapRadius;
        mCopyRect.bottom = mHVEPosition2DList.get(0).yPos + mBitmapRadius;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTransForm && isTouchAble) {
            if (onEditListener != null) {
                onEditListener.onTap(new HVEPosition2D(event.getX(), event.getY()));
                return true;
            }
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mOldX = event.getX();
                mOldY = event.getY();
                if (isDrawScale && mScaleRect.contains(event.getX(), event.getY())) {
                    if (mHVEPosition2DList.get(0) == null || mHVEPosition2DList.get(1) == null
                        || mHVEPosition2DList.get(2) == null || mHVEPosition2DList.get(3) == null) {
                        break;
                    }
                    mCurrentMode = Mode.IMG_ZOOM;
                    mOldRotation = getRotation((mHVEPosition2DList.get(1).xPos + mHVEPosition2DList.get(3).xPos) / 2,
                        (mHVEPosition2DList.get(1).yPos + mHVEPosition2DList.get(3).yPos) / 2,
                        mHVEPosition2DList.get(3).xPos, mHVEPosition2DList.get(3).yPos);
                    mOldDist = getSpacing((mHVEPosition2DList.get(1).xPos + mHVEPosition2DList.get(3).xPos) / 2,
                        (mHVEPosition2DList.get(1).yPos + mHVEPosition2DList.get(3).yPos) / 2,
                        mHVEPosition2DList.get(3).xPos, mHVEPosition2DList.get(3).yPos);
                    break;
                } else if (isDrawDelete && mDeleteRect.contains(event.getX(), event.getY())) {
                    mCurrentMode = Mode.DELETE;
                } else if (isDrawEdit && mEditRect.contains(event.getX(), event.getY())) {
                    mCurrentMode = Mode.EDIT;
                } else if (isDrawCopy && mCopyRect.contains(event.getX(), event.getY())) {
                    mCurrentMode = Mode.COPY;
                } else {
                    mCurrentMode = Mode.NONE;
                    if (mPreviousUpEvent != null && mCurrentDownEvent != null
                        && isConsideredDoubleTap(mPreviousUpEvent, event)) {
                        if (onEditListener != null) {
                            onEditListener.onDoubleFingerTap();
                        }
                    }
                    mCurrentDownEvent = MotionEvent.obtain(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mCurrentMode == Mode.IMG_ZOOM) {
                    break;
                }
                mCurrentMode = Mode.ZOOM;
                mOldDist = getSpacing(event);
                getDoubleFingerPoint(event);
                mOldRotation = getRotation(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentMode == Mode.ZOOM) {
                    getMoveEventByZoom(event);
                } else if (mCurrentMode == Mode.IMG_ZOOM) {
                    getMoveEventByImgZoom(event);
                } else {
                    float dx = event.getX() - mOldX;
                    float dy = event.getY() - mOldY;
                    if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                        mCurrentMode = Mode.DRAG;
                        getMoveEventByDrag(event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (onEditListener == null) {
                    break;
                }
                mEventUpX = event.getX();
                mEventUpY = event.getY();
                mPreviousUpEvent = MotionEvent.obtain(event);
                switch (mCurrentMode) {
                    case DELETE:
                        onEditListener.onDelete();
                        break;
                    case EDIT:
                        onEditListener.onEdit();
                        break;
                    case COPY:
                        onEditListener.onCopy();
                        break;
                    default:
                        break;
                }
                if (mCurrentMode == Mode.NONE) {
                    onEditListener.onTap(new HVEPosition2D(mEventUpX, mEventUpY));
                }
                isDrawRotate = false;
                onEditListener.onFingerUp();
                mCurrentMode = Mode.NONE;
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 0) {
                    if (onEditListener != null) {
                        onEditListener.onFingerUp();
                    }
                    mCurrentMode = Mode.NONE;
                }
                break;

            default:
                break;
        }
        return true;
    }

    private boolean isConsideredDoubleTap(MotionEvent firstUp, MotionEvent secondDown) {
        if (secondDown.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
            return false;
        }
        int deltaX = (int) firstUp.getX() - (int) secondDown.getX();
        int deltaY = (int) firstUp.getY() - (int) secondDown.getY();
        return deltaX * deltaX + deltaY * deltaY < 10000;
    }

    private void getMoveEventByImgZoom(MotionEvent event) {
        if (mHVEPosition2DList.get(0) == null || mHVEPosition2DList.get(1) == null || mHVEPosition2DList.get(2) == null
            || mHVEPosition2DList.get(3) == null) {
            return;
        }
        isDrawRotate = true;
        float ro = getRotation((mHVEPosition2DList.get(1).xPos + mHVEPosition2DList.get(3).xPos) / 2,
            (mHVEPosition2DList.get(1).yPos + mHVEPosition2DList.get(3).yPos) / 2, event.getX(), event.getY());
        float rotation = ro - mOldRotation;

        float newDist = getSpacing((mHVEPosition2DList.get(1).xPos + mHVEPosition2DList.get(3).xPos) / 2,
            (mHVEPosition2DList.get(1).yPos + mHVEPosition2DList.get(3).yPos) / 2, event.getX(), event.getY());
        float scale = newDist / mOldDist;
        if (mSize == null || scale * mSize.width <= mMinSize) {
            return;
        }
        if (onEditListener != null) {
            float r = mSrcRotation - rotation;
            float r1 = Math.abs(r) % 360 % 90;
            if (Math.abs(r1 - 90) < STICKER_ROTATION || Math.abs(r1) < STICKER_ROTATION) {
                if (r < 0) {
                    r = -((int) Math.abs(r - STICKER_ROTATION - 1) / 90) * 90;
                } else {
                    r = (int) Math.abs(r + STICKER_ROTATION + 1) / 90 * 90;
                }
                if (isRotateVib) {
                    vibrate();
                    isRotateVib = false;
                }
            } else {
                isRotateVib = true;
            }
            mShowRotate = (int) r % 360;
            onEditListener.onScaleRotate(scale, r);
            mOldDist = newDist;
            if (isRotateVib) {
                mOldRotation = ro;
            }
        }
    }

    private void getMoveEventByZoom(MotionEvent event) {
        isDrawRotate = true;
        float ro = getRotation(event);
        float rotation = ro - mOldRotation;

        float newDist = getSpacing(event);
        float scale = newDist / mOldDist;
        if (mSize == null || scale * mSize.width <= mMinSize) {
            return;
        }

        if (onEditListener != null) {
            float r = mSrcRotation - rotation;
            float r1 = Math.abs(r) % 360 % 90;
            if (Math.abs(r1 - 90) < STICKER_ROTATION || Math.abs(r1) < STICKER_ROTATION) {
                if (r < 0) {
                    r = -((int) Math.abs(r - STICKER_ROTATION - 1) / 90) * 90;
                } else {
                    r = (int) Math.abs(r + STICKER_ROTATION + 1) / 90 * 90;
                }
                if (isRotateVib) {
                    vibrate();
                    isRotateVib = false;
                }
            } else {
                isRotateVib = true;
            }
            mShowRotate = (int) r % 360;
            onEditListener.onScaleRotate(scale, r);
            mOldDist = newDist;

            if (isRotateVib) {
                mOldRotation = ro;
            }
        }
    }

    private void getMoveEventByDrag(MotionEvent event) {
        float dx = event.getX() - mOldX;
        float dy = event.getY() - mOldY;
        if (onEditListener != null) {
            if (mHVEPosition2DList.isEmpty()) {
                return;
            }
            if (mHVEPosition2DList.get(0) == null || mHVEPosition2DList.get(1) == null
                || mHVEPosition2DList.get(2) == null || mHVEPosition2DList.get(3) == null) {
                return;
            }
            float cX = (mHVEPosition2DList.get(0).xPos + mHVEPosition2DList.get(2).xPos) / 2 + dx;
            float cY = (mHVEPosition2DList.get(0).yPos + mHVEPosition2DList.get(2).yPos) / 2 + dy;
            float cenX = (float) BigDecimalUtils.div(getWidth(), 2.0f, 1);
            float cenY = (float) BigDecimalUtils.div(getHeight(), 2.0f, 1);

            int assetLeftX = (getWidth() - assetWidth) / 2;
            int assetRightX = (getWidth() - assetWidth) / 2 + assetWidth;
            int assetTopY = (getHeight() - assetHeight) / 2;
            int assetBottomY = ((getHeight() - assetHeight) / 2) + assetHeight;

            if (isEnableOutAreaLimit) {
                if (cX <= assetLeftX || cX >= assetRightX) {
                    SmartLog.i(TAG, "[getMoveEventByDrag] Horizontal： center point out border");
                    return;
                }
            }
            if (Math.abs(cX - cenX) < STICKER_LENGTH) {
                if (dx < 0) {
                    dx = -Math.abs(cenX - ((mHVEPosition2DList.get(0).xPos + mHVEPosition2DList.get(2).xPos) / 2));
                } else {
                    dx = Math.abs(cenX - ((mHVEPosition2DList.get(0).xPos + mHVEPosition2DList.get(2).xPos) / 2));
                }
                if (!isDrawXLine) {
                    vibrate();
                }
                isDrawXLine = true;
                setReferenceLineState(true, true);
            } else {
                isDrawXLine = false;
                setReferenceLineState(false, true);
            }
            if (isEnableOutAreaLimit) {
                if (cY <= assetTopY || cY >= assetBottomY) {
                    SmartLog.i(TAG, "[getMoveEventByDrag] Vertical： center point out border");
                    return;
                }
            }
            if (Math.abs(cY - cenY) < STICKER_LENGTH) {
                if (dy < 0) {
                    dy = -Math.abs(cenY - ((mHVEPosition2DList.get(0).yPos + mHVEPosition2DList.get(2).yPos) / 2));
                } else {
                    dy = Math.abs(cenY - ((mHVEPosition2DList.get(0).yPos + mHVEPosition2DList.get(2).yPos) / 2));
                }
                if (!isDrawYLine) {
                    vibrate();
                }
                isDrawYLine = true;
                setReferenceLineState(true, false);
            } else {
                isDrawYLine = false;
                setReferenceLineState(false, false);
            }
            onEditListener.onFling(dx, dy);
            if (!isDrawXLine) {
                mOldX = event.getX();
            }
            if (!isDrawYLine) {
                mOldY = event.getY();
            }
        }
    }


    private void vibrate() {
        onEditListener.onVibrate();
    }

    private void setReferenceLineState(boolean isShow, boolean isHorizontal) {
        onEditListener.onShowReferenceLine(!isDrawRotate && isShow, isHorizontal);
    }

    public void setTransForm(boolean transForm) {
        isTransForm = transForm;
    }

    public void setTouchAble(boolean touchAble) {
        isTouchAble = touchAble;
    }

    protected float getSpacing(MotionEvent event) {
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(x * x + y * y);
    }

    protected float getSpacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    protected void getDoubleFingerPoint(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        mDoubleCenterPoint.set(x / 2, y / 2);
    }

    protected float getRotation(MotionEvent event) {
        try {
            double deltaX = (event.getX(0) - event.getX(1));
            double deltaY = (event.getY(0) - event.getY(1));
            double radians = Math.atan2(deltaY, deltaX);
            return (float) Math.toDegrees(radians);
        } catch (IllegalArgumentException e) {
            return mOldRotation;
        }
    }

    protected float getRotation(float x1, float y1, float x2, float y2) {
        double deltaX = x1 - x2;
        double deltaY = y1 - y2;
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    public void setDrawIconStatus(boolean isDrawDelete, boolean isDrawEdit, boolean isDrawCopy, boolean isDrawScale) {
        this.isDrawDelete = isDrawDelete;
        this.isDrawEdit = isDrawEdit;
        this.isDrawCopy = isDrawCopy;
        this.isDrawScale = isDrawScale;
    }

    public void setRectangularPoints(List<HVEPosition2D> list, HVESize hveSize, float rotation) {
        if (list == null || list.size() != 4 || hveSize == null) {
            SmartLog.w(TAG, "setRectangularPoints rect is inValid");
            return;
        }
        this.mHVEPosition2DList = list;
        this.mSize = hveSize;
        this.mSrcRotation = rotation;
        calculateBitmapRect();
        invalidate();
    }

    public void setRectangularPoints(List<HVEPosition2D> list) {
        if (list == null || list.size() != 4) {
            return;
        }
        this.mHVEPosition2DList = list;
        calculateBitmapRect();
        invalidate();
    }

    public float getSrcRotation() {
        return mSrcRotation;
    }

    public List<HVEPosition2D> getHVEPosition2DList() {
        return mHVEPosition2DList;
    }

    public HVESize getSize() {
        return mSize;
    }

    public boolean isEnableOutAreaLimit() {
        return isEnableOutAreaLimit;
    }

    public void setEnableOutAreaLimit(boolean enableOutAreaLimit) {
        isEnableOutAreaLimit = enableOutAreaLimit;
    }

    public boolean isLandscapeVideo() {
        return isLandscapeVideo;
    }

    public void setLandscapeVideo(boolean landscapeVideo) {
        isLandscapeVideo = landscapeVideo;
    }

    public int getAssetWidth() {
        return assetWidth;
    }

    public void setAssetWidth(int assetWidth) {
        this.assetWidth = assetWidth;
    }

    public int getAssetHeight() {
        return assetHeight;
    }

    public void setAssetHeight(int assetHeight) {
        this.assetHeight = assetHeight;
    }

    public enum Mode {
        NONE,
        ZOOM,
        DRAG,
        IMG_ZOOM,
        DELETE,
        EDIT,
        COPY
    }

    public void setOnEditListener(OnMaterialEditListener onEditListener) {
        this.onEditListener = onEditListener;
    }
}
