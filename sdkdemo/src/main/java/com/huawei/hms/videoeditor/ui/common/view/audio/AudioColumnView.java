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

package com.huawei.hms.videoeditor.ui.common.view.audio;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.huawei.hms.videoeditor.ui.common.utils.SafeSecureRandom;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class AudioColumnView extends View {
    private int random;

    private boolean isStart = true;

    private int mRect_t1;

    private int mRect_t2;

    private int mRect_t3;

    private int mRect_t4;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x1234) {
                invalidate();
            }
        }
    };

    private Paint mPaint;

    private int mHeight;

    private RectF r1;

    private RectF r2;

    private RectF r3;

    private RectF r4;

    public AudioColumnView(Context context) {
        this(context, null);
    }

    public AudioColumnView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AudioColumnView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AudioColumnView);
        int height = ta.getInteger(R.styleable.AudioColumnView_column_height, 0);
        random = height;
        mHeight = SizeUtils.dp2Px(getContext(), height);
        ta.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        r1 = new RectF();
        r2 = new RectF();
        r3 = new RectF();
        r4 = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void start() {
        isStart = true;
        invalidate();
    }

    public void stop() {
        isStart = false;
        invalidate();
    }

    public boolean isStart() {
        return isStart;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isStart) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mRect_t1 = SafeSecureRandom.nextInt(random);
                    mRect_t2 = SafeSecureRandom.nextInt(random);
                    mRect_t3 = SafeSecureRandom.nextInt(random);
                    mRect_t4 = SafeSecureRandom.nextInt(random);
                    mHandler.sendEmptyMessage(0x1234);
                }
            }, 150);
        }
        r1.set((float) (0), mRect_t1, (float) (SizeUtils.dp2Px(getContext(), 2)), (float) (mHeight * 0.9));
        r2.set((float) (SizeUtils.dp2Px(getContext(), 5)), mRect_t2, (float) (SizeUtils.dp2Px(getContext(), 7)),
            (float) (mHeight * 0.9));
        r3.set((float) (SizeUtils.dp2Px(getContext(), 10)), mRect_t3, (float) (SizeUtils.dp2Px(getContext(), 12)),
            (float) (mHeight * 0.9));
        r4.set((float) (SizeUtils.dp2Px(getContext(), 15)), mRect_t4, (float) (SizeUtils.dp2Px(getContext(), 17)),
            (float) (mHeight * 0.9));
        canvas.drawRect(r1, mPaint);
        canvas.drawRect(r2, mPaint);
        canvas.drawRect(r3, mPaint);
        canvas.drawRect(r4, mPaint);
    }
}
