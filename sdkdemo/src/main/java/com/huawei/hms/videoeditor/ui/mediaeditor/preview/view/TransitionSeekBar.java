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

import java.math.BigDecimal;
import java.text.NumberFormat;

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

public class TransitionSeekBar extends View {
    private static final int LINEWIDTH = 6;

    private static final int CYCLEWIDTH = 10;

    private static final int CYCLEREDIUS = 20;

    private static final int TEXTSIZE = 50;

    private static final float DEFAULT_THUMB_RADIUS = 20.0f;

    private float thumbRadius;

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

    private long maxTransTime = 0L;

    private int progress = 0;

    private boolean isShowText = true;

    Paint linePainter;

    Paint cyclePainter;

    Paint textPainter;

    private OnProgressChangedListener listener;

    private TouchListener listener1;

    private int mTextSize;

    public TransitionSeekBar(Context context) {
        this(context, null, 0);
    }

    public TransitionSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransitionSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    public TransitionSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.MySeekBar_text_size, ScreenUtil.dp2px(14));
        minProgress = typedArray.getInt(R.styleable.MySeekBar_progress_min, minProgress);
        maxProgress = typedArray.getInt(R.styleable.MySeekBar_progress_max, maxProgress);
        progress = typedArray.getInt(R.styleable.MySeekBar_progress, maxProgress);
        anchorProgress = typedArray.getInt(R.styleable.MySeekBar_progress_anchor, minProgress);
        isShowText = typedArray.getBoolean(R.styleable.MySeekBar_show_text, true);
        thumbRadius = typedArray.getDimension(R.styleable.MySeekBar_thumb_radius, DEFAULT_THUMB_RADIUS);
        typedArray.recycle();

        linePainter = new Paint();
        linePainter.setStrokeWidth(lineWidth);
        linePainter.setStrokeCap(Paint.Cap.ROUND);
        linePainter.setStyle(Paint.Style.STROKE);
        linePainter.setTextSize(TEXTSIZE);

        cyclePainter = new Paint();
        cyclePainter.setStrokeWidth(CYCLEWIDTH);

        textPainter = new TextPaint();
        textPainter.setColor(textColor);
        textPainter.setTextSize(mTextSize);
        textPainter.setStrokeWidth(CYCLEWIDTH);

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

        xStartPosition = (float) paddingLeft;
        xEndPosition = (float) mWidth - paddingRight;
        if (isShowText) {
            yProgress = (float) (mHeight - paddingTop - paddingBottom) * 3f / 4f;
        } else {
            yProgress = (float) (mHeight - paddingTop - paddingBottom) / 2f;
        }

        length = xEndPosition - xStartPosition;

        xProgress = ((float) progress - minProgress) / (maxProgress - minProgress) * length + xStartPosition;
        xAnchorPosition =
            ((float) anchorProgress - minProgress) / (maxProgress - minProgress) * length + xStartPosition;

        perPerX = length / (maxProgress - minProgress);

        setMeasuredDimension(mWidth, mHeight);
    }

    float xStartPosition;

    float xAnchorPosition;

    float xProgress;

    float xEndPosition;

    float yProgress;

    float length;

    float perPerX;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawColor(mainBgColor);

        linePainter.setColor(bgLineColor);
        canvas.drawLine(paddingLeft, yProgress, mWidth - paddingRight, yProgress, linePainter);

        linePainter.setColor(valueLineColor);

        canvas.drawLine(xAnchorPosition, yProgress, xProgress, yProgress, linePainter);

        if (defaultThumb) {
            cyclePainter.setColor(outCycleColor);
            cyclePainter.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(xProgress, yProgress, thumbRadius, cyclePainter);

            cyclePainter.setColor(inCycleColor);
            cyclePainter.setStyle(Paint.Style.FILL);
            canvas.drawCircle(xProgress, yProgress, thumbRadius, cyclePainter);
        } else {
            canvas.drawBitmap(thumb, xProgress - thumb.getWidth() / 2F, yProgress - thumb.getHeight() / 2F,
                cyclePainter);
        }

        if (isShowText) {
            if (ScreenUtil.isRTL()) {
                textPainter.setTextScaleX(RTL_UI);
            } else {
                textPainter.setTextScaleX(LTR_UI);
            }
            int currentTransTime = (int) Math.max(100, (float) progress / 100 * maxTransTime);
            float textTimer = BigDecimal.valueOf(BigDecimalUtils.div(currentTransTime, 1000f, 1)).floatValue();
            String v = getResources().getQuantityString(R.plurals.seconds_time, Double.valueOf(textTimer).intValue(),
                NumberFormat.getInstance().format(textTimer));
            float h = textPainter.measureText(v);
            canvas.drawText(v, xProgress - (float) BigDecimalUtils.div(h, 2f), (float) BigDecimalUtils.div(mHeight, 3f),
                textPainter);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent mEvent) {
        switch (mEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (listener1 != null) {
                    listener1.isTouch(true);
                }
                handEvent(mEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                handEvent(mEvent);
                break;
            default:
                if (listener1 != null) {
                    listener1.isTouch(false);
                }
                if (mSeekBarListener != null) {
                    mSeekBarListener.seekFinished();
                }
                break;
        }
        return true;
    }

    private void handEvent(MotionEvent motionEvent) {
        xProgress = motionEvent.getX();
        int thisProgress = 0;
        if (xProgress > xStartPosition && xProgress < xEndPosition) {
            thisProgress = (int) ((xProgress - xAnchorPosition) / length * (maxProgress - minProgress));
            xProgress = thisProgress * perPerX + xAnchorPosition;
        }
        if (xProgress >= xEndPosition) {
            thisProgress = maxProgress;
            xProgress = xEndPosition;
        }
        if (xProgress <= xStartPosition) {
            thisProgress = minProgress;
            xProgress = xStartPosition;
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

    public void setListener1(TouchListener listener) {
        this.listener1 = listener;
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

    public void setSeekBarProgress(int seekBarProgress) {
        this.progress = seekBarProgress;
        xStartPosition = paddingLeft;
        xEndPosition = mWidth - paddingRight;
        if (isShowText) {
            yProgress = (float) (mHeight - paddingTop - paddingBottom) * 3f / 4f;
        } else {
            yProgress = (float) (mHeight - paddingTop - paddingBottom) / 2f;
        }
        length = xEndPosition - xStartPosition;

        xProgress = ((float) seekBarProgress - minProgress) / (maxProgress - minProgress) * length + xStartPosition;
        xAnchorPosition =
            ((float) anchorProgress - minProgress) / (maxProgress - minProgress) * length + xStartPosition;

        perPerX = length / (maxProgress - minProgress);
        invalidate();
    }

    public void setMaxTransTime(long maxTransTime) {
        this.maxTransTime = maxTransTime;
    }

    public float getProgress() {
        return progress;
    }

    public SeekBarListener mSeekBarListener;

    public interface SeekBarListener {
        void seekFinished();
    }

    public interface TouchListener {
        void isTouch(boolean isTouch);
    }

    public void setmSeekBarListener(SeekBarListener mSeekBarListener) {
        this.mSeekBarListener = mSeekBarListener;
    }

    int lastXPosition = -1;

    int lastYPosition = -1;

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
                downX += Math.abs(x - lastXPosition);
                downY += Math.abs(y - lastYPosition);
                if (downX >= downY) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                lastXPosition = x;
                lastYPosition = y;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
