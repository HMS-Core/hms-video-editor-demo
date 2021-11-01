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

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.ui.common.utils.DrawableUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class FilterSeekBar extends View {
    private int mWidth;

    private int mHeight;

    private Paint degreePaint;

    private Paint degreePaint2;

    private Paint thumbPaint;

    private Paint textPaint;

    private final float thumbRadius = ScreenUtil.dp2px(12f);

    private final float degreeWidth = ScreenUtil.dp2px(1f);

    private final float progressTextSize = ScreenUtil.dp2px(12f);

    private final int degreeCount = 50;

    private Bitmap thumb;

    private float space;

    int paddingLeft;

    int paddingRight;

    int paddingBottom;

    int paddingTop;

    int defPaddingLeft = ScreenUtil.dp2px(12f);

    int defPaddingRight = ScreenUtil.dp2px(12f);

    private int mAnchorProgress = 0;

    private int mMinProgress = 0;

    int mMaxProgress = 100;

    private int progress = 0;

    private OnProgressChangedListener mProgressListener;

    private MySeekBar.TouchListener bTouchListener;

    public FilterSeekBar(Context context) {
        this(context, null, 0);
    }

    public FilterSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        Drawable thumbDrawable = ContextCompat.getDrawable(context, R.drawable.circlethumb);
        if (thumbDrawable != null) {
            Drawable drawable = DrawableUtils.zoomDrawable(thumbDrawable, ScreenUtil.dp2px(24f), ScreenUtil.dp2px(24f));
            thumb = drawableToBitmap(drawable, (int) thumbRadius * 2, (int) thumbRadius * 2);
        }

        int degreeColor = ContextCompat.getColor(context, R.color.white);

        int degreeColor2 = ContextCompat.getColor(context, R.color.white);

        degreePaint = new Paint();
        degreePaint.setColor(degreeColor);
        degreePaint.setAntiAlias(true);
        degreePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        degreePaint.setStrokeWidth(degreeWidth);

        textPaint = new TextPaint();
        textPaint.setColor(degreeColor);
        textPaint.setTextSize(progressTextSize);
        textPaint.setStyle(Paint.Style.FILL);

        degreePaint2 = new Paint();
        degreePaint2.setColor(degreeColor2);
        degreePaint2.setAntiAlias(true);
        degreePaint2.setStyle(Paint.Style.FILL_AND_STROKE);
        degreePaint2.setStrokeWidth(degreeWidth);

        thumbPaint = new Paint();
        thumbPaint.setAntiAlias(true);
        thumbPaint.setStyle(Paint.Style.FILL);

        paddingLeft = getPaddingStart() + defPaddingLeft;
        paddingRight = getPaddingEnd() + defPaddingRight;
        paddingBottom = getPaddingBottom();
        paddingTop = getPaddingTop();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mXStart = (float) paddingLeft;
        mXEnd = (float) mWidth - paddingRight;
        mYP = (float) (mHeight - paddingTop - paddingBottom) / 2f;

        mLen = mXEnd - mXStart;
        space = mLen / degreeCount;
        xProgress = ((float) progress - mMinProgress) / (mMaxProgress - mMinProgress) * mLen + mXStart;
        xAnchor = ((float) mAnchorProgress - mMinProgress) / (mMaxProgress - mMinProgress) * mLen + mXStart;
        perPx = mLen / (mMaxProgress - mMinProgress);
        setMeasuredDimension(mWidth, mHeight);
    }

    float mXStart;

    float xAnchor;

    float xProgress;

    float mXEnd;

    float mYP;

    float mLen;

    float perPx;

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i <= degreeCount; i++) {
            if (i % 5 == 0) {
                canvas.drawCircle(space * i + mXStart, mYP, 3f, degreePaint);
            } else {
                canvas.drawCircle(space * i + mXStart, mYP, 1f, degreePaint2);
            }
        }

        if (thumb == null) {
            return;
        }
        canvas.drawBitmap(thumb, xProgress - thumb.getWidth() / 2f, mYP - thumb.getHeight() / 2f, thumbPaint);
        drawProgressText(canvas, xProgress, String.valueOf(progress));
    }

    private void drawProgressText(Canvas canvas, float offset, String text) {
        Rect mBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), mBounds);
        float textWidth = mBounds.width();
        float offsetX;
        if (ScreenUtil.isRTL()) {
            textPaint.setTextScaleX(RTL_UI);
            offsetX = offset + textWidth / 2f;
        } else {
            textPaint.setTextScaleX(LTR_UI);
            offsetX = offset - textWidth / 2f;
        }
        canvas.drawText(text, offsetX, (float) mHeight / 3f - thumbRadius * 0.75f, textPaint);
    }

    public Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent bMotionEvent) {
        switch (bMotionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                if (bTouchListener != null) {
                    bTouchListener.isTouch(true);
                }
                handEvent(bMotionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                handEvent(bMotionEvent);
                break;
            default:
                if (bTouchListener != null) {
                    bTouchListener.isTouch(false);
                }
                if (bSeekBarListener != null) {
                    bSeekBarListener.seekFinished();
                }
                break;
        }
        return true;
    }

    private void handEvent(MotionEvent event) {
        xProgress = event.getX();
        int thisProgress = 0;
        if (xProgress > mXStart && xProgress < mXEnd) {
            thisProgress = (int) ((xProgress - xAnchor) / mLen * (mMaxProgress - mMinProgress));
            xProgress = thisProgress * perPx + xAnchor;
        }
        if (xProgress >= mXEnd) {
            thisProgress = mMaxProgress;
            xProgress = mXEnd;
        }
        if (xProgress <= mXStart) {
            thisProgress = mMinProgress;
            xProgress = mXStart;
        }
        if (thisProgress != progress) {
            progress = thisProgress;
            invalidate();
            if (this.mProgressListener != null) {
                mProgressListener.onProgressChanged(progress);
            }
        }
    }

    public void setbTouchListener(MySeekBar.TouchListener listener) {
        this.bTouchListener = listener;
    }

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        this.mProgressListener = onProgressChangedListener;
    }

    public interface OnProgressChangedListener {
        void onProgressChanged(int aProgress);
    }

    public void setmAnchorProgress(int mAnchorProgress) {
        this.mAnchorProgress = mAnchorProgress;
    }

    public void setmMinProgress(int mMinProgress) {
        this.mMinProgress = mMinProgress;
    }

    public void setmMaxProgress(int mMaxProgress) {
        this.mMaxProgress = mMaxProgress;
    }

    public void setProgress(int aProgress) {
        this.progress = aProgress;
        mXStart = paddingLeft;
        mXEnd = mWidth - paddingRight;
        mYP = (float) (mHeight - paddingTop - paddingBottom) / 2f;
        mLen = mXEnd - mXStart;

        xProgress = ((float) aProgress - mMinProgress) / (mMaxProgress - mMinProgress) * mLen + mXStart;
        xAnchor = ((float) mAnchorProgress - mMinProgress) / (mMaxProgress - mMinProgress) * mLen + mXStart;

        perPx = mLen / (mMaxProgress - mMinProgress);
        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    public SeekBarListener bSeekBarListener;

    public interface SeekBarListener {
        void seekFinished();
    }

    public void setbSeekBarListener(SeekBarListener bSeekBarListener) {
        this.bSeekBarListener = bSeekBarListener;
    }
}
