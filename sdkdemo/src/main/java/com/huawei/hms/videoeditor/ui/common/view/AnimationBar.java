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

package com.huawei.hms.videoeditor.ui.common.view;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.math.BigDecimal;
import java.text.NumberFormat;

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

import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AnimationBar extends View {
    private static final String TAG = "AnimationBar";

    private static final int MAX_PROGRESS = 100;

    private Paint backPaint;

    private Paint progressPaint;

    private Paint textPaint;

    private Paint thumbPaint;

    private int backLineColor = R.color.animBackColor;

    private int enterLineColor = R.color.entaerAnimColor;

    private int leaveLineColor = R.color.leaveAnimColor;

    private int textColor = R.color.speedBarTextColor;

    private float textSize = ScreenUtil.dp2px(14);

    private float thumbRadius = ScreenUtil.dp2px(8);

    private float lineHeight = ScreenUtil.dp2px(4);

    private float lineLength = ScreenUtil.dp2px(200);

    private Bitmap enterThumb;

    private Bitmap leaveThumb;

    private float interval = ScreenUtil.dp2px(4);

    private float textHeight = ScreenUtil.dp2px(17);

    private float enterThumbLeft = 0;

    private float enterThumbTop = interval + textHeight;

    private float leaveThumbLeft = lineLength;

    private float leaveThumbTop = interval + textHeight;

    private Rect touchEnterRect;

    private Rect touchLeaveRect;

    private int degreeCount = 100;

    private float space = 0;

    private int enterProgress = 0;

    private int leaveProgress = 0;

    private long duration = 0L;

    private boolean isEnterShow = true;

    private boolean isLeaveShow = true;

    private long enterDuration = 0;

    private long leaveDuration = 0;

    private boolean couldMove = true;

    private boolean isTouchEntre;

    private AnimationBar.TouchListener cTouchListener;

    private boolean isEnter = true;

    public AnimationBar(Context context) {
        super(context);
        init(context, null);
    }

    public AnimationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AnimationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Drawable initEnterThumb = ContextCompat.getDrawable(context, R.drawable.enterthumb);
        Drawable initLeaveThumb = ContextCompat.getDrawable(context, R.drawable.leavethumb);
        if (initEnterThumb != null) {
            enterThumb = drawableToBitmap(initEnterThumb, (int) thumbRadius * 2, (int) thumbRadius * 2);
        }
        if (initLeaveThumb != null) {
            leaveThumb = drawableToBitmap(initLeaveThumb, (int) thumbRadius * 2, (int) thumbRadius * 2);
        }

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AnimationBar);
            int backColor =
                ta.getColor(R.styleable.AnimationBar_anim_backColor, ContextCompat.getColor(context, backLineColor));
            int enterColor =
                ta.getColor(R.styleable.AnimationBar_anim_enterColor, ContextCompat.getColor(context, enterLineColor));
            int leaveColor =
                ta.getColor(R.styleable.AnimationBar_anim_leaveColor, ContextCompat.getColor(context, leaveLineColor));
            int wordColor =
                ta.getColor(R.styleable.AnimationBar_anim_textColor, ContextCompat.getColor(context, textColor));
            float wordSize = ta.getDimension(R.styleable.AnimationBar_anim_textSize, textSize);
            float markRadius = ta.getDimension(R.styleable.AnimationBar_anim_thumbRadius, thumbRadius);
            float markHeight = ta.getDimension(R.styleable.AnimationBar_anim_lineHeight, lineHeight);
            float markLength = ta.getDimension(R.styleable.AnimationBar_anim_lineLength, lineLength);
            int markCount = ta.getInt(R.styleable.AnimationBar_anim_degreeCount, degreeCount);
            Drawable enterThumbDrawable = ta.getDrawable(R.styleable.AnimationBar_anim_enterThumb);
            Drawable leaveThumbDrawable = ta.getDrawable(R.styleable.AnimationBar_anim_leaveThumb);
            ta.recycle();

            this.backLineColor = backColor;
            this.enterLineColor = enterColor;
            this.leaveLineColor = leaveColor;
            this.textColor = wordColor;
            this.textSize = wordSize;
            this.thumbRadius = markRadius;
            this.lineHeight = markHeight;
            this.lineLength = markLength;
            this.degreeCount = markCount;
            if (enterThumbDrawable != null) {
                this.enterThumb = drawableToBitmap(enterThumbDrawable, (int) thumbRadius * 2, (int) thumbRadius * 2);
            }
            if (leaveThumbDrawable != null) {
                this.leaveThumb = drawableToBitmap(leaveThumbDrawable, (int) thumbRadius * 2, (int) thumbRadius * 2);
            }
        }

        space = lineLength / degreeCount;
        setBackPaint(backLineColor, lineHeight);
        setProgressPaint(lineHeight);
        setTextPaint(textColor, textSize);
        setThumbPaint();
    }

    private void setBackPaint(int backLineColor, float lineHeight) {
        backPaint = new Paint();
        backPaint.setColor(backLineColor);
        backPaint.setAntiAlias(true);
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeCap(Paint.Cap.ROUND);
        backPaint.setStrokeWidth(lineHeight);
    }

    private void setProgressPaint(float lineHeight) {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(lineHeight);
    }

    private boolean setProgressColor(int progressColor) {
        if (progressPaint == null) {
            return false;
        }
        progressPaint.setColor(progressColor);
        return true;
    }

    private void setTextPaint(int textColor, float textSize) {
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void setThumbPaint() {
        thumbPaint = new Paint();
        thumbPaint.setAntiAlias(true);
        thumbPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec + 2 * (int) (thumbRadius + interval), heightMeasureSpec);
    }

    private boolean showEnterText = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        touchEnterRect = null;
        touchLeaveRect = null;
        canvas.drawLine(interval + thumbRadius, enterThumbTop + thumbRadius, interval + thumbRadius + lineLength,
            enterThumbTop + thumbRadius, backPaint);
        if (isEnter) {
            if (isLeaveShow) {
                touchLeaveRect = new Rect((int) (interval + leaveThumbLeft), (int) leaveThumbTop,
                    (int) (interval + leaveThumbLeft + 2 * thumbRadius), (int) (leaveThumbTop + 2 * thumbRadius));
                drawProgress(canvas, leaveLineColor, interval + thumbRadius + lineLength, leaveThumbTop + thumbRadius,
                    interval + thumbRadius + leaveThumbLeft, leaveThumbTop + thumbRadius);
                canvas.drawBitmap(leaveThumb, interval + leaveThumbLeft, leaveThumbTop, thumbPaint);
                if (!showEnterText) {
                    drawText(canvas, leaveThumbLeft, leaveDuration);
                }
            } else {
                touchLeaveRect = null;
            }

            if (isEnterShow) {
                touchEnterRect = new Rect((int) (interval + enterThumbLeft), (int) enterThumbTop,
                    (int) (interval + enterThumbLeft + 2 * thumbRadius), (int) (enterThumbTop + 2 * thumbRadius));
                drawProgress(canvas, enterLineColor, interval + thumbRadius, enterThumbTop + thumbRadius,
                    interval + thumbRadius + enterThumbLeft, enterThumbTop + thumbRadius);
                canvas.drawBitmap(enterThumb, interval + enterThumbLeft, enterThumbTop, thumbPaint);
                if (showEnterText) {
                    drawText(canvas, enterThumbLeft, enterDuration);
                }
            } else {
                touchEnterRect = null;
            }
        } else {
            if (isEnterShow) {
                touchEnterRect = new Rect((int) (interval + enterThumbLeft), (int) enterThumbTop,
                    (int) (interval + enterThumbLeft + 2 * thumbRadius), (int) (enterThumbTop + 2 * thumbRadius));
                drawProgress(canvas, enterLineColor, interval + thumbRadius, enterThumbTop + thumbRadius,
                    interval + thumbRadius + enterThumbLeft, enterThumbTop + thumbRadius);
                canvas.drawBitmap(enterThumb, interval + enterThumbLeft, enterThumbTop, thumbPaint);
                if (showEnterText) {
                    drawText(canvas, enterThumbLeft, enterDuration);
                }
            } else {
                touchEnterRect = null;
            }

            if (isLeaveShow) {
                touchLeaveRect = new Rect((int) (interval + leaveThumbLeft), (int) leaveThumbTop,
                    (int) (interval + leaveThumbLeft + 2 * thumbRadius), (int) (leaveThumbTop + 2 * thumbRadius));
                drawProgress(canvas, leaveLineColor, interval + thumbRadius + lineLength, leaveThumbTop + thumbRadius,
                    interval + thumbRadius + leaveThumbLeft, leaveThumbTop + thumbRadius);
                canvas.drawBitmap(leaveThumb, interval + leaveThumbLeft, leaveThumbTop, thumbPaint);
                if (!showEnterText) {
                    drawText(canvas, leaveThumbLeft, leaveDuration);
                }
            } else {
                touchLeaveRect = null;
            }
        }
    }

    private void drawProgress(Canvas canvas, int progressColor, float startX, float startY, float endX, float endY) {
        if (!setProgressColor(progressColor)) {
            return;
        }
        canvas.drawLine(startX, startY, endX, endY, progressPaint);
    }

    private Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void drawText(Canvas canvas, float offset, long duration) {
        String str = getText(duration);
        Rect mBounds = new Rect();
        textPaint.getTextBounds(str, 0, str.length(), mBounds);
        float wordOffset = 0;
        if (ScreenUtil.isRTL()) {
            textPaint.setTextScaleX(RTL_UI);
        } else {
            textPaint.setTextScaleX(LTR_UI);
        }
        if (interval + thumbRadius + offset - getTextWidth(str, textPaint) / 2 <= 0) {
            wordOffset = (float) (getTextWidth(str, textPaint) / 2);
        } else if (interval + thumbRadius + offset + getTextWidth(str, textPaint) / 2 >= lineLength
            + 2 * (interval + thumbRadius)) {
            wordOffset = (float) (lineLength + 2 * (interval + thumbRadius) - getTextWidth(str, textPaint) / 2);
        } else {
            wordOffset = interval + thumbRadius + offset;
        }
        canvas.drawText(str, wordOffset, textHeight, textPaint);
    }

    public double getTextWidth(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return Math.abs(rect.width());
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private String getText(long animationDuration) {
        if (animationDuration == 0.0 || animationDuration <= 100) {
            return getResources().getQuantityString(R.plurals.seconds_time, 1, NumberFormat.getInstance().format(0.1));
        }
        BigDecimal bd1 = new BigDecimal(animationDuration);
        BigDecimal bd2 = new BigDecimal(1000f);
        BigDecimal text = bd1.divide(bd2).setScale(1, BigDecimal.ROUND_DOWN);
        return getResources().getQuantityString(R.plurals.seconds_time, 1, NumberFormat.getInstance().format(text));
    }

    private boolean isEnterSliding = false;

    private boolean isLeaveSliding = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                isEnterSliding = false;
                isLeaveSliding = false;
                if (!couldMove) {
                    return true;
                }
                if (isTouchEntre) {
                    setSlidStatus(event, touchEnterRect, true, false, touchLeaveRect);
                } else {
                    setSlidStatus(event, touchLeaveRect, false, true, touchEnterRect);
                }
                if (cTouchListener != null) {
                    cTouchListener.isTouch(true);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isEnterShow || isLeaveShow) {
                    handEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isEnterSliding) {
                    if (listener != null) {
                        listener.onEnterProgressChanged(enterProgress);
                    }
                } else if (isLeaveSliding) {
                    if (listener != null) {
                        listener.onLeaveProgressChanged(degreeCount - leaveProgress);
                    }
                }
                if (cTouchListener != null) {
                    cTouchListener.isTouch(false);
                }
                break;
            default:
                if (cTouchListener != null) {
                    cTouchListener.isTouch(false);
                }
                break;
        }
        return true;
    }

    private void setSlidStatus(MotionEvent event, Rect touchEnterRect, boolean b, boolean b2, Rect touchLeaveRect) {
        if (touchEnterRect != null && touchEnterRect.contains((int) event.getX(), (int) event.getY())
            && touchLeaveRect != null && touchLeaveRect.contains((int) event.getX(), (int) event.getY())) {
            if (isEnter) {
                isEnterSliding = true;
                isLeaveSliding = false;
            } else {
                isEnterSliding = false;
                isLeaveSliding = true;
            }
        } else if (touchEnterRect != null && touchEnterRect.contains((int) event.getX(), (int) event.getY())) {
            isEnterSliding = b;
            isLeaveSliding = b2;
        } else if (touchLeaveRect != null && touchLeaveRect.contains((int) event.getX(), (int) event.getY())) {
            isEnterSliding = b2;
            isLeaveSliding = b;
        }
    }

    private void handEvent(MotionEvent motionEvent) {
        int thisProgress = 0;
        if (isEnterSliding) {
            isTouchEntre = true;
            enterThumbLeft = motionEvent.getX();
            if (isLeaveShow) {
                if (enterThumbLeft > leaveThumbLeft) {
                    thisProgress = leaveProgress;
                    enterThumbLeft = leaveThumbLeft;
                }
            } else {
                if (enterThumbLeft >= lineLength) {
                    thisProgress = degreeCount;
                    enterThumbLeft = lineLength;
                }
            }

            if (enterThumbLeft <= 0) {
                thisProgress = 0;
                enterThumbLeft = 0;
            }
            if (enterThumbLeft > 0 && enterThumbLeft <= lineLength) {
                thisProgress = (int) (enterThumbLeft / space);
                enterThumbLeft = thisProgress * space;
            }
            enterProgress = thisProgress;
            enterDuration = duration / degreeCount * enterProgress;
            if (enterDuration < minAnimationDuration(duration)) {
                enterDuration = minAnimationDuration(duration);
            }
            if (cTouchListener != null) {
                cTouchListener.isTouch(true);
            }
            setEnterAnimation(true);
        }

        if (isLeaveSliding) {
            isTouchEntre = false;
            leaveThumbLeft = motionEvent.getX();
            if (isEnterShow) {
                if (leaveThumbLeft < enterThumbLeft) {
                    thisProgress = enterProgress;
                    leaveThumbLeft = enterThumbLeft;
                }
            } else {
                if (leaveThumbLeft <= 0) {
                    thisProgress = 0;
                    leaveThumbLeft = 0;
                }
            }

            if (leaveThumbLeft >= lineLength) {
                thisProgress = degreeCount;
                leaveThumbLeft = lineLength;
            }

            if (leaveThumbLeft > 0 && leaveThumbLeft <= lineLength) {
                thisProgress = (int) (leaveThumbLeft / space);
                leaveThumbLeft = thisProgress * space;
            }
            leaveProgress = thisProgress;
            leaveDuration = duration / degreeCount * (degreeCount - leaveProgress);
            if (leaveDuration < minAnimationDuration(duration)) {
                leaveDuration = minAnimationDuration(duration);
            }
            if (cTouchListener != null) {
                cTouchListener.isTouch(true);
            }
            setEnterAnimation(false);
        }
    }

    private int minAnimationDuration(long duration) {
        return (int) (BigDecimalUtil.mul(duration, (BigDecimalUtil.div(0.1, (BigDecimalUtil.div(duration, 1000))))));
    }

    private OnProgressChangedListener listener;

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        this.listener = onProgressChangedListener;
    }

    public void setcTouchListener(AnimationBar.TouchListener listener) {
        this.cTouchListener = listener;
    }

    public String getProgress() {
        if (isEnterSliding) {
            return getText(enterDuration);
        } else if (isLeaveSliding) {
            return getText(leaveDuration);
        }
        return getResources().getQuantityString(R.plurals.seconds_time, 0, NumberFormat.getInstance().format(0));
    }

    public interface OnProgressChangedListener {

        void onEnterProgressChanged(int progress);

        void onLeaveProgressChanged(int progress);
    }

    public void setEnterProgress(int progress) {
        this.enterProgress = progress;
        enterThumbLeft = space * progress;
        invalidate();
    }

    public void setLeaveProgress(int progress) {
        this.leaveProgress = degreeCount - progress;
        leaveThumbLeft = space * (degreeCount - progress);
        invalidate();
    }

    public void setEnterDuration(long enterDuration) {
        if (duration == 0 || degreeCount == 0) {
            this.enterProgress = 0;
            enterThumbLeft = 0;
            return;
        }

        enterDuration = Math.max(enterDuration, 0);
        this.enterDuration = enterDuration;
        this.enterProgress = (int) (enterDuration / (duration / degreeCount));

        if (this.enterProgress > MAX_PROGRESS) {
            this.enterProgress = MAX_PROGRESS;
        }
        enterThumbLeft = this.enterProgress * space;
        invalidate();
    }

    public void setLeaveDuration(long leaveDuration) {
        if (duration == 0 || degreeCount == 0) {
            this.leaveProgress = 0;
            leaveThumbLeft = lineLength;
            return;
        }
        this.leaveDuration = leaveDuration;
        this.leaveProgress = (int) (leaveDuration / (duration / degreeCount));

        if (this.leaveProgress > MAX_PROGRESS) {
            this.leaveProgress = MAX_PROGRESS;
        }
        leaveThumbLeft = lineLength - this.leaveProgress * space;
        invalidate();
    }

    public void setEnterShow(boolean isShow) {
        this.isEnterShow = isShow;
        invalidate();
    }

    public boolean getEnterShow() {
        return isEnterShow;
    }

    public void setLeaveShow(boolean isShow) {
        this.isLeaveShow = isShow;
        invalidate();
    }

    public boolean getLeaveShow() {
        return isLeaveShow;
    }

    public long getEnterDuration() {
        return this.enterDuration;
    }

    public long getLeaveDuration() {
        if (duration <= 0 || degreeCount <= 0 || space <= 0) {
            return 0;
        }
        return this.leaveDuration;
    }

    public void setCouldMove(boolean couldMove) {
        this.couldMove = couldMove;
    }

    public interface TouchListener {
        void isTouch(boolean isTouch);
    }

    public void setEnterAnimation(boolean isEnter) {
        this.isEnter = isEnter;
        showEnterText = isEnter;
        invalidate();
    }
}
