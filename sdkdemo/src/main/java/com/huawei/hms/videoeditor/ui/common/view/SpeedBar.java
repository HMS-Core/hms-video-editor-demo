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

package com.huawei.hms.videoeditor.ui.common.view;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.DrawableUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.MySeekBar;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.core.content.ContextCompat;

public class SpeedBar extends View {
    private Paint degreePaint;

    private Paint degreePaint2;

    private Paint textPaint;

    private Paint progressTextPaint;

    private Paint thumbPaint;

    private int degreeCount = 100;

    private int textColor = R.color.speedBarTextColor;

    private int degreeColor = R.color.translucent_white_00;

    private int degreeColor2 = R.color.translucent_white_80;

    private float degreeWidth = ScreenUtil.dp2px(1f);

    private float textSize = ScreenUtil.dp2px(10f);

    private float thumbRadius = ScreenUtil.dp2px(12f);

    private float length;

    private Bitmap thumb;

    private String[] textList = new String[] {"0.5x", "1x", "2x", "3x", "4x", "5x"};

    private float space;

    private String textContent;

    private float thumbLeft = 0;

    private int progress = 0;

    private boolean isShowText = true;

    public SpeedBar(Context context) {
        super(context);
        init(context, null);
    }

    public SpeedBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpeedBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("ResourceAsColor")
    private void init(Context context, AttributeSet attrs) {
        Drawable thumbDrawable = ContextCompat.getDrawable(context, R.drawable.circlethumb);
        if (thumbDrawable != null) {
            Drawable drawable = DrawableUtils.zoomDrawable(thumbDrawable, ScreenUtil.dp2px(24f), ScreenUtil.dp2px(24f));
            thumb = drawableToBitmap(drawable, (int) thumbRadius * 2, (int) thumbRadius * 2);
        }

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpeedBar);
            int markCount = ta.getInt(R.styleable.SpeedBar_speed_degreeCount, degreeCount);
            float markWidth = ta.getDimension(R.styleable.SpeedBar_speed_degreeWidth, degreeWidth);
            float wordSize = ta.getDimension(R.styleable.SpeedBar_speed_textSize, textSize);
            int wordColor =
                ta.getColor(R.styleable.SpeedBar_speed_textColor, ContextCompat.getColor(context, textColor));
            float markRadius = ta.getDimension(R.styleable.SpeedBar_speed_thumbRadius, thumbRadius);
            Drawable drawableThumb = ta.getDrawable(R.styleable.SpeedBar_speed_thumb);
            float degreelength = ta.getDimension(R.styleable.SpeedBar_speed_length, length);
            isShowText = ta.getBoolean(R.styleable.SpeedBar_speed_show_text, true);
            ta.recycle();

            this.degreeCount = markCount;
            this.textColor = wordColor;
            this.degreeWidth = markWidth;
            this.textSize = wordSize;
            degreeColor = ContextCompat.getColor(context, R.color.translucent_white_00);
            degreeColor2 = ContextCompat.getColor(context, R.color.translucent_white_00);
            this.thumbRadius = markRadius;
            if (drawableThumb != null) {
                Drawable drawable =
                    DrawableUtils.zoomDrawable(drawableThumb, ScreenUtil.dp2px(25), ScreenUtil.dp2px(25));
                this.thumb = drawableToBitmap(drawable, (int) thumbRadius * 2, (int) thumbRadius * 2);
            }
            this.length = degreelength;
        }

        space = length / degreeCount;

        setDegreePaint(degreeWidth);
        setTextPaint(textColor, textSize);
        setProgressTextPaint(textColor, textSize);
        setThumbPaint();
    }

    @SuppressLint("ResourceAsColor")
    private void setDegreePaint(float degreeWidth) {
        degreePaint = new Paint();
        degreePaint.setColor(degreeColor);
        degreePaint.setAntiAlias(true);
        degreePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        degreePaint.setStrokeWidth(degreeWidth);

        // 画小刻度
        degreePaint2 = new Paint();
        degreePaint2.setColor(degreeColor2);
        degreePaint2.setAntiAlias(true);
        degreePaint2.setStyle(Paint.Style.FILL_AND_STROKE);
        degreePaint2.setStrokeWidth(degreeWidth);
    }

    private void setTextPaint(int textColor, float textSize) {
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
    }

    private void setProgressTextPaint(int textColor, float textSize) {
        progressTextPaint = new Paint();
        progressTextPaint.setColor(textColor);
        progressTextPaint.setAntiAlias(true);
        progressTextPaint.setStyle(Paint.Style.FILL);
        progressTextPaint.setTextSize(textSize);
    }

    private void setThumbPaint() {
        thumbPaint = new Paint();
        thumbPaint.setAntiAlias(true);
        thumbPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec + 2 * (int) thumbRadius, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i <= degreeCount; i++) {
            if (i % 10 == 0) {
                canvas.drawCircle(space * i + thumbRadius,
                    BigDecimalUtils.div(thumb.getHeight(), 2F) + thumbRadius * 3F, 3f, degreePaint);
                if (isShowText) {
                    float fProgress;
                    if (progress > 0 && progress <= 6) {
                        fProgress = BigDecimalUtils.div(progress - 3, 10F);
                    } else {
                        fProgress = BigDecimalUtils.div(progress, 10F);
                    }
                    if (fProgress <= BigDecimalUtils.div(i - 3, 10F) || fProgress > BigDecimalUtils.div(i + 3, 10F)) {
                        textContent = textList[i / 10];
                        drawText(canvas, space * i + thumbRadius, textContent);
                    }
                }
            } else {
                canvas.drawCircle(space * i + thumbRadius,
                    BigDecimalUtils.div(thumb.getHeight(), 2F) + thumbRadius * 3F, 1f, degreePaint2);

            }
        }

        if (thumb == null) {
            return;
        }
        String v;
        if (progress <= 0) {
            v = "0.5x";
        } else if (progress < 10) {
            v = progress / 10F + "x";
        } else {
            if ((progress % 10) == 0) {
                v = progress / 10 + "x";
            } else {
                v = progress / 10F + "x";
            }
        }
        drawProgressText(canvas, thumbLeft, v);
        canvas.drawBitmap(thumb, thumbLeft, thumb.getHeight() / 2F + thumbRadius * 2F, thumbPaint);
    }

    private void drawProgressText(Canvas canvas, float offset, String text) {
        Rect mBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), mBounds);
        float textWidth = mBounds.width();
        float offsetX;
        if (ScreenUtil.isRTL()) {
            progressTextPaint.setTextScaleX(-1);
            offsetX = offset + thumbRadius + textWidth / 2F;
        } else {
            offsetX = offset + thumbRadius - textWidth / 2F;
        }
        canvas.drawText(text, offsetX, thumb.getHeight() / 2F + thumbRadius * 2F - ScreenUtil.dp2px(8F),
            progressTextPaint);
    }

    private void drawText(Canvas canvas, float offset, String text) {
        Rect mBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), mBounds);
        float textWidth = mBounds.width();
        float offsetX;
        if (ScreenUtil.isRTL()) {
            textPaint.setTextScaleX(RTL_UI);
            offsetX = offset + textWidth / 2F;
        } else {
            textPaint.setTextScaleX(LTR_UI);
            offsetX = offset - textWidth / 2F;
        }
        canvas.drawText(text, offsetX, thumb.getHeight() / 2F + thumbRadius * 2F - ScreenUtil.dp2px(8F), textPaint);
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

    public void setProgress(float progress) {
        this.progress = (int) progress;
        if (progress > 0 && progress < 10) {
            thumbLeft = space * (progress - 5);
        } else {
            thumbLeft = space * progress;
        }
        invalidate();
    }

    private OnProgressChangedListener listener;

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        this.listener = onProgressChangedListener;
    }

    public interface OnProgressChangedListener {
        void onProgressChanged(int progress);
    }

    @Override
    public boolean onTouchEvent(MotionEvent aMotionEvent) {
        switch (aMotionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (aTouchListener != null) {
                    aTouchListener.isTouch(true);
                }
                handEvent(aMotionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                handEvent(aMotionEvent);
                break;
            default:
                if (aTouchListener != null) {
                    aTouchListener.isTouch(false);
                }
                break;
        }
        return true;
    }

    private void handEvent(MotionEvent motionEvent) {
        thumbLeft = motionEvent.getX();
        int thisProgress = 0;

        if (thumbLeft >= length) {
            thisProgress = degreeCount;
            thumbLeft = length;
        }
        if (thumbLeft <= 0) {
            thisProgress = 0;
            thumbLeft = 0;
        }

        if (thumbLeft > 0 && thumbLeft < length) {
            thisProgress = (int) (thumbLeft / space);
            thumbLeft = thisProgress * space;
        }

        if (thisProgress != progress) {
            progress = thisProgress;
            invalidate();
            if (progress > 0 && progress < 10) {
                progress = 5 + progress / 2;
            }
            if (this.listener != null) {
                listener.onProgressChanged(progress);
            }
        }
    }

    private MySeekBar.TouchListener aTouchListener;

    public void setaTouchListener(MySeekBar.TouchListener listener) {
        this.aTouchListener = listener;
    }
}
