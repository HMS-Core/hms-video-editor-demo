/*
 *   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.hms.videoeditor.ui.common.view;

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

import androidx.core.content.ContextCompat;

import com.huawei.hms.videoeditor.ui.common.utils.DrawableUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenBuilderUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.text.NumberFormat;

public class ScaleBar extends View {

    private Paint degreeBigPaint;

    private Paint degreeSmallPaint;

    private Paint textPaint;

    private Paint progressTextPaint;

    private Paint cursorPaint;

    private int degreeCount = 360;

    private int textColor = R.color.grey_text;

    private int degreeBigColor = R.color.translucent_white_40;

    private int degreeSmallColor = R.color.translucent_white_60;

    private int cursorColor = R.color.color_text_focus;

    private float degreeWidth;

    private float textSize;

    private float cursorTextSize;

    private float cursorMargin;

    private float length;

    private Bitmap cursor;

    protected float mScaleBigHeight;

    protected float mScaleSmallHeight;

    protected int mRectHeight;

    private float space;

    private String textContent;

    private float cursorLeft = 0;

    private int progress = 0;

    private String[] textList = new String[] {NumberFormat.getInstance().format(-180) + "°",
        NumberFormat.getInstance().format(0) + "°", NumberFormat.getInstance().format(180) + "°"};

    private String[] textListRtl = new String[] {"°" + NumberFormat.getInstance().format(-180),
        "°" + NumberFormat.getInstance().format(0), "°" + NumberFormat.getInstance().format(180)};

    public ScaleBar(Context context) {
        super(context);
        init(context, null);
    }

    public ScaleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScaleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("ResourceAsColor")
    private void init(Context context, AttributeSet attrs) {
        Context applicationContext = context.getApplicationContext();
        mScaleBigHeight = SizeUtils.dp2Px(applicationContext, 12f);
        mScaleSmallHeight = SizeUtils.dp2Px(applicationContext, 10f);
        mRectHeight = SizeUtils.dp2Px(applicationContext, 50f);
        degreeWidth = SizeUtils.dp2Px(applicationContext, 2f);
        textSize = SizeUtils.dp2Px(applicationContext, 12f);
        cursorTextSize = SizeUtils.dp2Px(applicationContext, 14f);
        cursorMargin = SizeUtils.dp2Px(applicationContext, 12f);

        Drawable thumbDrawable = ContextCompat.getDrawable(context, R.drawable.icon_cursor);
        if (thumbDrawable != null) {
            Drawable drawable = DrawableUtils.zoomDrawable(thumbDrawable, SizeUtils.dp2Px(applicationContext, 3f),
                    SizeUtils.dp2Px(applicationContext, 18f));
            cursor = drawableToBitmap(drawable, SizeUtils.dp2Px(applicationContext, 3f), SizeUtils.dp2Px(applicationContext, 18f));
        }

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScaleBar);
            int markCount = ta.getInt(R.styleable.ScaleBar_scale_degreeCount, degreeCount);
            int wordColor =
                ta.getColor(R.styleable.ScaleBar_scale_textColor, ContextCompat.getColor(context, textColor));
            int bigCursorColor =
                ta.getColor(R.styleable.ScaleBar_scale_degreeBigColor, ContextCompat.getColor(context, degreeBigColor));
            int smallCursorColor = ta.getColor(R.styleable.ScaleBar_scale_degreeSmallColor,
                ContextCompat.getColor(context, degreeSmallColor));
            int scaleCursorColor =
                ta.getColor(R.styleable.ScaleBar_cursor_color, ContextCompat.getColor(context, cursorColor));
            float markWidth = ta.getDimension(R.styleable.ScaleBar_scale_degreeWidth, degreeWidth);
            float wordSize = ta.getDimension(R.styleable.ScaleBar_scale_textSize, textSize);
            float cursorSize = ta.getDimension(R.styleable.ScaleBar_cursor_textSize, cursorTextSize);
            float margin = ta.getDimension(R.styleable.ScaleBar_cursor_margin, cursorMargin);
            Drawable drawableThumb = ta.getDrawable(R.styleable.ScaleBar_scale_thumb);
            float degreelength = ta.getDimension(R.styleable.ScaleBar_scale_length, length);
            ta.recycle();

            this.degreeCount = markCount;
            this.textColor = wordColor;
            this.degreeBigColor = bigCursorColor;
            this.degreeSmallColor = smallCursorColor;
            this.cursorColor = scaleCursorColor;
            this.degreeWidth = markWidth;
            this.textSize = wordSize;
            this.cursorTextSize = cursorSize;
            this.cursorMargin = margin;
            this.length = degreelength;
            if (drawableThumb != null) {
                Drawable drawable = DrawableUtils.zoomDrawable(drawableThumb, SizeUtils.dp2Px(applicationContext, 3f),
                                SizeUtils.dp2Px(applicationContext, 18f));
                this.cursor = drawableToBitmap(drawable, SizeUtils.dp2Px(applicationContext, 3f),
                        SizeUtils.dp2Px(applicationContext, 18f));
            }
        }

        space = length / degreeCount;

        setDegreePaint(degreeWidth);
        setTextPaint(textColor, textSize);
        setProgressTextPaint(cursorColor, cursorTextSize);
        setThumbPaint();
    }

    @SuppressLint("ResourceAsColor")
    private void setDegreePaint(float degreeWidth) {
        degreeBigPaint = new Paint();
        degreeBigPaint.setColor(degreeBigColor);
        degreeBigPaint.setAntiAlias(true);
        degreeBigPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        degreeBigPaint.setStrokeWidth(degreeWidth);

        degreeSmallPaint = new Paint();
        degreeSmallPaint.setColor(degreeSmallColor);
        degreeSmallPaint.setAntiAlias(true);
        degreeSmallPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        degreeSmallPaint.setStrokeWidth(degreeWidth / 2);
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
        cursorPaint = new Paint();
        cursorPaint.setAntiAlias(true);
        cursorPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec + 2 * SizeUtils.dp2Px(
                getContext().getApplicationContext(), 14f), heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i <= degreeCount; i++) {
            if (i % 15 != 0) {
                continue;
            }
            if (i % 45 == 0) {
                canvas.drawLine(space * i + cursorMargin, mRectHeight, space * i + cursorMargin,
                    mRectHeight - mScaleBigHeight, degreeBigPaint);
                if (progress >= 0 && progress <= 30) {
                    if (i != 0 && i % 180 == 0) {
                        drawScaleText(canvas, i);
                    }
                }
                if (progress >= 150 && progress <= 210) {
                    if (i != 180 && i % 180 == 0) {
                        drawScaleText(canvas, i);
                    }
                }
                if (progress >= 330 && progress <= 360) {
                    if (i != 360 && i % 180 == 0) {
                        drawScaleText(canvas, i);
                    }
                }
                if (progress > 30 && progress < 150 || progress > 210 && progress < 330) {
                    if (i % 180 == 0) {
                        drawScaleText(canvas, i);
                    }
                }
            } else {
                canvas.drawLine(space * i + cursorMargin, mRectHeight, space * i + cursorMargin,
                    mRectHeight - mScaleSmallHeight, degreeSmallPaint);
            }
        }

        if (cursor == null) {
            return;
        }

        drawProgressText(canvas, cursorLeft, getProgressText(progress));
        canvas.drawBitmap(cursor, cursorLeft + SizeUtils.dp2Px(getContext().getApplicationContext(), 11f),
                cursor.getHeight() / 2F + cursorMargin * 2F, cursorPaint);
    }

    private void drawScaleText(Canvas canvas, int i) {
        if (ScreenBuilderUtil.isRTL()) {
            textContent = textListRtl[i / 180];
        } else {
            textContent = textList[i / 180];
        }
        drawText(canvas, space * i + cursorMargin, textContent);
    }

    public String getProgressText(int progress) {
        String progressText = ScreenBuilderUtil.isRTL() ? textListRtl[0] : textList[0];
        if (progress >= 0 && progress < 180) {
            float progressNumber = -(180 - progress);
            progressText = ScreenBuilderUtil.isRTL() ? ("°" + NumberFormat.getInstance().format(progressNumber))
                : (NumberFormat.getInstance().format(progressNumber) + "°");
        } else if (progress >= 180 && progress <= 360) {
            float progressNumber = progress - 180;
            progressText = ScreenBuilderUtil.isRTL() ? ("°" + NumberFormat.getInstance().format(progressNumber))
                : (NumberFormat.getInstance().format(progressNumber) + "°");
        }
        return progressText;
    }

    public int getScaleByProgress(int progress) {
        int progressNumber = 0;
        if (progress >= 0 && progress < 180) {
            progressNumber = -(180 - progress);
        } else if (progress >= 180 && progress <= 360) {
            progressNumber = progress - 180;
        }
        return progressNumber;
    }

    public float getScale(int scale) {
        if (scale >= -180 && scale < 0) {
            progress = 180 - (-scale);
        } else if (scale >= 0 && scale <= 180) {
            progress = 180 + scale;
        }
        return progress;
    }

    public void setScale(int scale) {
        if (scale >= -180 && scale < 0) {
            progress = 180 - (-scale);
            cursorLeft = space * progress;
        } else if (scale >= 0 && scale <= 180) {
            progress = 180 + scale;
            cursorLeft = space * progress;
        }
        invalidate();
    }

    private void drawProgressText(Canvas canvas, float offset, String text) {
        Rect mBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), mBounds);
        float textWidth = mBounds.width();
        float offsetX;
        if (ScreenBuilderUtil.isRTL()) {
            progressTextPaint.setTextScaleX(-1);
            offsetX = offset + cursorMargin + textWidth / 2F;
        } else {
            offsetX = offset + cursorMargin - textWidth / 2F;
        }
        canvas.drawText(text, offsetX, cursor.getHeight() / 2F + cursorMargin * 2F -
                        SizeUtils.dp2Px(getContext().getApplicationContext(),8F),
            progressTextPaint);
    }

    private void drawText(Canvas canvas, float offset, String text) {
        Rect mBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), mBounds);
        float textWidth = mBounds.width();
        float offsetX;
        if (ScreenBuilderUtil.isRTL()) {
            textPaint.setTextScaleX(Constant.RTL_UI);
            offsetX = offset + textWidth / 2F;
        } else {
            textPaint.setTextScaleX(Constant.LTR_UI);
            offsetX = offset - textWidth / 2F;
        }
        canvas.drawText(text, offsetX, cursor.getHeight() / 2F + cursorMargin * 2F -
                SizeUtils.dp2Px(getContext().getApplicationContext(), 8F), textPaint);
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
        cursorLeft = motionEvent.getX();
        int thisProgress = 0;

        if (cursorLeft >= length) {
            thisProgress = degreeCount;
            cursorLeft = length;
        }
        if (cursorLeft <= 0) {
            thisProgress = 0;
            cursorLeft = 0;
        }

        if (cursorLeft > 0 && cursorLeft < length) {
            thisProgress = (int) (cursorLeft / space);
            cursorLeft = thisProgress * space;
        }

        if (thisProgress != progress) {
            progress = thisProgress;
            invalidate();
            if (this.listener != null) {
                listener.onProgressChanged(progress);
            }
        }
    }

    public interface TouchListener {
        void isTouch(boolean isTouch);
    }

    private TouchListener aTouchListener;

    public void setaTouchListener(TouchListener listener) {
        this.aTouchListener = listener;
    }
}
