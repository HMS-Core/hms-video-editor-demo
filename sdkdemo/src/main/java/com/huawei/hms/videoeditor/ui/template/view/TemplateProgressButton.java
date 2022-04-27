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

package com.huawei.hms.videoeditor.ui.template.view;

import java.text.NumberFormat;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

@SuppressLint("AppCompatCustomView")
public class TemplateProgressButton extends Button {
    private int mProgress;

    private int mMaxProgress = 100;

    private int mMinProgress = 0;

    private GradientDrawable mProgressDrawable;

    private GradientDrawable mProgressDrawableBg;

    private StateListDrawable mNormalDrawable;

    private boolean isShowProgress;

    private boolean isFinish;

    private boolean isStop;

    private OnStateListener onStateListener;

    private float cornerRadius;

    public TemplateProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TemplateProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TemplateProgressButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init(Context context, AttributeSet attributeSet) {
        mNormalDrawable = new StateListDrawable();

        mProgressDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.use_module_btn_bg).mutate();

        mProgressDrawableBg = (GradientDrawable) getResources().getDrawable(R.drawable.unuse_module_btn_bg).mutate();

        @SuppressLint("CustomViewStyleable")
        TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.progressbutton);

        try {
            float defValue = getResources().getDimension(R.dimen.dp_20);
            cornerRadius = attr.getDimension(R.styleable.progressbutton_buttonCornerRadius, defValue);

            isShowProgress = attr.getBoolean(R.styleable.progressbutton_showProgressNum, true);

            mNormalDrawable.addState(new int[] {android.R.attr.state_pressed}, getPressedDrawable(attr));

            mNormalDrawable.addState(new int[] {}, getNormalDrawable(attr));

            int defaultProgressColor = getResources().getColor(R.color.color_text_focus);
            int progressColor = attr.getColor(R.styleable.progressbutton_progressColor, defaultProgressColor);

            mProgressDrawable.setColor(progressColor);

            int defaultProgressBgColor = getResources().getColor(R.color.color_666);
            int progressBgColor = attr.getColor(R.styleable.progressbutton_progressBgColor, defaultProgressBgColor);

            mProgressDrawableBg.setColor(progressBgColor);
        } finally {
            attr.recycle();
        }

        isFinish = false;
        isStop = true;

        mProgressDrawable.setCornerRadius(cornerRadius);
        mProgressDrawableBg.setCornerRadius(cornerRadius);

        setBackgroundCompat(mNormalDrawable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mProgress > mMinProgress && mProgress <= mMaxProgress && !isFinish) {
            float scale = (float) getProgress() / (float) mMaxProgress;
            float indicatorWidth = (float) getMeasuredWidth() * scale;

            mProgressDrawable.setBounds(0, 0, (int) indicatorWidth, getMeasuredHeight());
            mProgressDrawable.draw(canvas);
            if (mProgress == mMaxProgress) {
                setBackgroundCompat(mProgressDrawable);
                isFinish = true;
                if (onStateListener != null) {
                    onStateListener.onFinish();
                }
            }
        }
        super.onDraw(canvas);
    }

    public void setProgress(int progress) {

        if (!isFinish && !isStop) {
            mProgress = progress;
            if (isShowProgress) {
                setText(String.format(Locale.ROOT, " %s ",
                    NumberFormat.getPercentInstance().format(BigDecimalUtil.div(mProgress, 100, 2))));
            }
            setBackgroundCompat(mProgressDrawableBg);
            invalidate();
        }
    }

    public int getProgress() {
        return mProgress;
    }

    public void setStop(boolean stop) {
        isStop = stop;
        invalidate();
    }

    public boolean isStop() {
        return isStop;
    }

    public boolean isFinish() {
        return isFinish;
    }

    private void setBackgroundCompat(Drawable drawable) {
        int pL = getPaddingStart();
        int pT = getPaddingTop();
        int pR = getPaddingEnd();
        int pB = getPaddingBottom();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
        setPaddingRelative(pL, pT, pR, pB);
    }

    public void initState() {
        setBackgroundCompat(mNormalDrawable);
        isFinish = false;
        isStop = true;
        mProgress = 0;
    }

    private Drawable getNormalDrawable(TypedArray attr) {

        GradientDrawable drawableNormal =
            (GradientDrawable) getResources().getDrawable(R.drawable.unuse_module_btn_bg).mutate();
        drawableNormal.setCornerRadius(cornerRadius);

        int defaultNormal = getResources().getColor(R.color.color_666);
        int colorNormal = attr.getColor(R.styleable.progressbutton_buttonNormalColor, defaultNormal);
        drawableNormal.setColor(colorNormal);

        return drawableNormal;
    }

    private Drawable getPressedDrawable(TypedArray attr) {
        GradientDrawable drawablePressed =
            (GradientDrawable) getResources().getDrawable(R.drawable.use_module_btn_bg).mutate();
        drawablePressed.setCornerRadius(cornerRadius);

        int defaultPressed = getResources().getColor(R.color.color_text_focus);
        int colorPressed = attr.getColor(R.styleable.progressbutton_buttonPressedColor, defaultPressed);
        drawablePressed.setColor(colorPressed);

        return drawablePressed;
    }

    public interface OnStateListener {

        void onFinish();

        void onStop();

        void onContinue();
    }

    public void setOnStateListener(OnStateListener onStateListener) {
        this.onStateListener = onStateListener;
    }

    public void isShowProgressNum(boolean b) {
        this.isShowProgress = b;
    }
}
