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
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;

public class MySeekBar extends View {
    private static final int LINEWIDTH = 6;

    private static final int CYCLEWIDTH = 10;

    private static final int CYCLEREDIUS = 20;

    private static final int TEXTSIZE = 50;

    private static final float DEFAULT_THUMB_RADIUS = 20.0f;

    private float mThumbRadius;

    private int mWidth;

    private int mHeight;

    private float lineWidth;

    private int mainBgColor;

    private int bgLineColor;

    private int valueLineColor;

    private int textColor;

    private int outCycleColor;

    private int inCycleColor;

    int paddingLeft;

    int paddingRight;

    int paddingBottom;

    int paddingTop;

    int defPadingLeft = CYCLEREDIUS;

    int defPadingRight = CYCLEREDIUS;

    private int anchorProgress = 0;

    private int minProgress = 0;

    int maxProgress = 100;

    private int progress = 0;

    private boolean isShowTv = true;

    Paint linePaint;

    Paint cyclePaint;

    Paint textPaint;

    private OnProgressChangedListener listener;

    private TouchListener cTouchListener;

    private int textSize;

    private float multiple;

    private boolean isInt;

    public MySeekBar(Context context) {
        this(context, null, 0);
    }

    public MySeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    public MySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MySeekBar);
        mainBgColor = typedArray.getColor(R.styleable.MySeekBar_main_bg, getResources().getColor(R.color.color_20));
        outCycleColor = typedArray.getColor(R.styleable.MySeekBar_outer_circle_bg,
            getResources().getColor(R.color.common_line_color));
        inCycleColor = typedArray.getColor(R.styleable.MySeekBar_inner_circle_bg,
            getResources().getColor(R.color.common_line_color));
        lineWidth = typedArray.getDimension(R.styleable.MySeekBar_line_width, LINEWIDTH);
        bgLineColor =
            typedArray.getColor(R.styleable.MySeekBar_line_bg, getResources().getColor(R.color.pick_line_color));
        valueLineColor =
            typedArray.getColor(R.styleable.MySeekBar_value_bg, getResources().getColor(R.color.color_text_focus));
        textColor = typedArray.getColor(R.styleable.MySeekBar_seek_text_color, getResources().getColor(R.color.white));
        textSize = typedArray.getDimensionPixelSize(R.styleable.MySeekBar_text_size, ScreenUtil.dp2px(14));
        minProgress = typedArray.getInt(R.styleable.MySeekBar_progress_min, minProgress);
        maxProgress = typedArray.getInt(R.styleable.MySeekBar_progress_max, maxProgress);
        progress = typedArray.getInt(R.styleable.MySeekBar_progress, maxProgress);
        anchorProgress = typedArray.getInt(R.styleable.MySeekBar_progress_anchor, minProgress);
        isShowTv = typedArray.getBoolean(R.styleable.MySeekBar_show_text, true);
        multiple = typedArray.getFloat(R.styleable.MySeekBar_text_multiple_relative_progress, 1);
        isInt = typedArray.getBoolean(R.styleable.MySeekBar_text_is_int, true);
        mThumbRadius = typedArray.getDimension(R.styleable.MySeekBar_thumb_radius, DEFAULT_THUMB_RADIUS);
        defPadingLeft = (int) typedArray.getDimension(R.styleable.MySeekBar_thumb_radius, DEFAULT_THUMB_RADIUS);
        defPadingRight = (int) typedArray.getDimension(R.styleable.MySeekBar_thumb_radius, DEFAULT_THUMB_RADIUS);
        typedArray.recycle();

        linePaint = new Paint();
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setTextSize(TEXTSIZE);

        cyclePaint = new Paint();
        cyclePaint.setStrokeWidth(CYCLEWIDTH);

        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setStrokeWidth(CYCLEWIDTH);

        paddingLeft = getPaddingStart() + defPadingLeft;
        paddingRight = getPaddingEnd() + defPadingRight;
        paddingBottom = getPaddingBottom();
        paddingTop = getPaddingTop();
    }

    private boolean defaultThumb = true;

    private Bitmap thumb;

    public void setThumb(Bitmap bitmap) {
        if (bitmap != null) {
            defaultThumb = false;
            this.thumb = bitmap;
        } else {
            defaultThumb = true;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        xStart = (float) paddingLeft;
        xEnd = (float) mWidth - paddingRight;
        if (isShowTv) {
            yP = (float) (mHeight - paddingTop - paddingBottom) * 3f / 4f;
        } else {
            yP = (float) (mHeight - paddingTop - paddingBottom) / 2f;
        }

        len = xEnd - xStart;

        xPro = ((float) progress - minProgress) / (maxProgress - minProgress) * len + xStart;
        xAnchor = ((float) anchorProgress - minProgress) / (maxProgress - minProgress) * len + xStart;

        perPx = len / (maxProgress - minProgress);

        setMeasuredDimension(mWidth, mHeight);
    }

    float xStart;

    float xAnchor;

    float xPro;

    float xEnd;

    float yP;

    float len;

    float perPx;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawColor(mainBgColor);

        linePaint.setColor(bgLineColor);
        canvas.drawLine(paddingLeft, yP, mWidth - paddingRight, yP, linePaint);

        linePaint.setColor(valueLineColor);

        canvas.drawLine(xAnchor, yP, xPro, yP, linePaint);

        if (defaultThumb) {
            cyclePaint.setColor(outCycleColor);
            cyclePaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(xPro, yP, mThumbRadius, cyclePaint);

            cyclePaint.setColor(inCycleColor);
            cyclePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(xPro, yP, mThumbRadius, cyclePaint);
        } else {
            canvas.drawBitmap(thumb, xPro - thumb.getWidth() / 2F, yP - thumb.getHeight() / 2F, cyclePaint);
        }

        if (isShowTv) {
            if (ScreenUtil.isRTL()) {
                textPaint.setTextScaleX(RTL_UI);
            } else {
                textPaint.setTextScaleX(LTR_UI);
            }
            if (multiple != 0 && isInt) {
                int text = (int) (progress / multiple);
                String v = String.valueOf(text);
                float h = textPaint.measureText(v);
                canvas.drawText(v, xPro - (float) BigDecimalUtils.div(h, 2f), (float) BigDecimalUtils.div(mHeight, 3f),
                    textPaint);
            } else if (multiple != 0 && !isInt) {
                float text = progress / multiple;
                String v = String.valueOf(text);
                float h = textPaint.measureText(v);
                canvas.drawText(v, xPro - (float) BigDecimalUtils.div(h, 2f), (float) BigDecimalUtils.div(mHeight, 3f),
                    textPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent cMotionEvent) {
        switch (cMotionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (cTouchListener != null) {
                    cTouchListener.isTouch(true);
                }
                handEvent(cMotionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                handEvent(cMotionEvent);
                break;
            default:
                if (cTouchListener != null) {
                    cTouchListener.isTouch(false);
                }
                if (cSeekBarListener != null) {
                    cSeekBarListener.seekFinished();
                }
                break;
        }
        return true;
    }

    private void handEvent(MotionEvent motionEvent) {
        xPro = motionEvent.getX();
        int thisProgress = 0;
        if (xPro > xStart && xPro < xEnd) {
            thisProgress = (int) ((xPro - xAnchor) / len * (maxProgress - minProgress));
            xPro = thisProgress * perPx + xAnchor;
        }
        if (xPro >= xEnd) {
            thisProgress = maxProgress;
            xPro = xEnd;
        }
        if (xPro <= xStart) {
            thisProgress = minProgress;
            xPro = xStart;
        }
        if (thisProgress != progress) {
            progress = thisProgress;
            invalidate();
            if (this.listener != null) {
                listener.onProgressChanged(progress);
            }
        }
    }

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        this.listener = onProgressChangedListener;
    }

    public void setcTouchListener(TouchListener listener) {
        this.cTouchListener = listener;
    }

    public interface OnProgressChangedListener {
        void onProgressChanged(int progress);
    }

    public void setAnchorProgress(int anchorProgress) {
        this.anchorProgress = anchorProgress;
    }

    public void setMinProgress(int minProgress) {
        this.minProgress = minProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        xStart = paddingLeft;
        xEnd = mWidth - paddingRight;
        if (isShowTv) {
            yP = (float) (mHeight - paddingTop - paddingBottom) * 3f / 4f;
        } else {
            yP = (float) (mHeight - paddingTop - paddingBottom) / 2f;
        }
        len = xEnd - xStart;

        xPro = ((float) progress - minProgress) / (maxProgress - minProgress) * len + xStart;
        xAnchor = ((float) anchorProgress - minProgress) / (maxProgress - minProgress) * len + xStart;

        perPx = len / (maxProgress - minProgress);
        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    public SeekBarListener cSeekBarListener;

    public interface SeekBarListener {
        void seekFinished();
    }

    public interface TouchListener {
        void isTouch(boolean isTouch);
    }

    public void setcSeekBarListener(SeekBarListener cSeekBarListener) {
        this.cSeekBarListener = cSeekBarListener;
    }

    int lastX = -1;

    int lastY = -1;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int downX = 0;
        int downY = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = 0;
                downY = 0;
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                downX += Math.abs(x - lastX);
                downY += Math.abs(y - lastY);
                if (downX >= downY) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
