
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

package com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class ColorPickerView extends LinearLayout {

    private static final String TAG = "ColorPickerView";

    private final View llColorProgress;

    private final View vColorBar;

    private int red = 255;

    private int green = 0;

    private int blue = 0;

    private int index = 0;

    private LinearLayout llProgress;

    private View vLocation;

    private View vBgColor;

    private RelativeLayout.LayoutParams colorBarLayoutParams;

    private int transValue = 255;

    private OnColorChangeListener onColorChangeListener;

    private RelativeLayout.LayoutParams vLocationLayoutParams;

    private float xEvent = 0;

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_color_picker, this);
        vBgColor = view.findViewById(R.id.fl_color);
        llProgress = view.findViewById(R.id.ll_progress);
        vLocation = view.findViewById(R.id.view_location);
        vLocationLayoutParams = (RelativeLayout.LayoutParams) vLocation.getLayoutParams();

        llColorProgress = findViewById(R.id.ll_color_progress);
        vColorBar = view.findViewById(R.id.view_color_bar);
        colorBarLayoutParams = (RelativeLayout.LayoutParams) vColorBar.getLayoutParams();

        llColorProgress.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                int width = llColorProgress.getWidth();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float leftMargin = event.getX();
                        float x = 0;
                        if (leftMargin < vColorBar.getWidth() / 2.0f) {
                            colorBarLayoutParams.setMarginStart(0);
                        } else if (leftMargin > width - vColorBar.getWidth() / 2.0f) {
                            x = 100;
                            colorBarLayoutParams.setMarginStart(width - vColorBar.getWidth());
                        } else {
                            x = event.getX() / width * 100;
                            colorBarLayoutParams.setMarginStart((int) (leftMargin - vColorBar.getWidth() / 2f));
                        }
                        vColorBar.setLayoutParams(colorBarLayoutParams);
                        Log.i(TAG, "onTouch: x==" + x);
                        xEvent = x;
                        onProgressChanged((int) x);
                        break;
                    default:
                        SmartLog.d(TAG, "ColorProgress run in default case");
                }

                return true;
            }
        });

        vBgColor.setOnTouchListener((v, event) -> {
            int width = vBgColor.getWidth();
            int height = vBgColor.getHeight();
            int action = event.getAction();
            int leftMargin;
            int topMargin;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 防止越界处理
                    if (event.getX() > width - vLocation.getWidth() / 2F) {
                        leftMargin = width - vLocation.getWidth();
                    } else if (event.getX() < vLocation.getWidth() / 2F) {
                        leftMargin = 0;
                    } else {
                        leftMargin = (int) (event.getX() - vLocation.getWidth() / 2F);
                    }
                    if (event.getY() > height - vLocation.getHeight() / 2F) {
                        topMargin = height - vLocation.getHeight();
                    } else if (event.getY() <= vLocation.getHeight() / 2F) {
                        topMargin = 0;
                    } else {
                        topMargin = (int) (event.getY() - vLocation.getHeight() / 2F);
                    }
                    vLocationLayoutParams.setMarginStart(leftMargin);
                    vLocationLayoutParams.topMargin = topMargin;
                    vLocation.setLayoutParams(vLocationLayoutParams);
                    changeColor();
                    break;
                default:
                    SmartLog.d(TAG, "BgColor run in default case");
            }
            return true;
        });
    }

    public void setLocationLayoutParams(RelativeLayout.LayoutParams layoutParams,
        RelativeLayout.LayoutParams seekbarParams, float xParams) {
        if (vLocation == null || vColorBar == null) {
            return;
        }

        if (vLocationLayoutParams != null && layoutParams != null) {
            vLocationLayoutParams = layoutParams;
            vLocation.setLayoutParams(vLocationLayoutParams);
            changeColor();
        }

        if (colorBarLayoutParams != null && seekbarParams != null) {
            colorBarLayoutParams = seekbarParams;
            vColorBar.setLayoutParams(colorBarLayoutParams);
            onProgressChanged((int) xParams);
        }
    }

    private void onProgressChanged(int progressColor) {
        red = 0;
        green = 0;
        blue = 0;
        index = (int) (progressColor / (100 / 6F));
        float v = progressColor % (100 / 6F) / (100 / 6F);
        switch (index) {
            case 0:
                red = 255;
                green = (int) (255 * v);
                break;
            case 1:
                red = (int) (255 * (1 - v));
                green = 255;
                break;
            case 2:
                green = 255;
                blue = (int) (255 * v);
                break;
            case 3:
                green = (int) (255 * (1 - v));
                blue = 255;
                break;
            case 4:
                blue = 255;
                red = (int) (255 * v);
                break;
            case 5:
                blue = (int) (255 * (1 - v));
                red = 255;
                break;
            default:
                red = 255;
                break;
        }

        GradientDrawable mGroupDrawable = (GradientDrawable) vBgColor.getBackground();
        mGroupDrawable.setColor(Color.rgb(red, green, blue));
        mGroupDrawable.setCornerRadius(SizeUtils.dp2Px(getContext(), 15));
        changeColor();
    }

    private void changeColor() {
        int tempRed = red;
        int tempGreen = green;
        int tempBlue = blue;
        float hPercent = 1 - (vLocation.getX() / (vBgColor.getWidth() - vLocation.getWidth()));
        float vPercent = vLocation.getY() / (vBgColor.getHeight() - vLocation.getHeight());
        switch (index) {
            case 0:
            case 5:
            case 6:
                tempGreen = (int) (green + hPercent * (255 - green));
                tempBlue = (int) (blue + hPercent * (255 - blue));
                break;
            case 1:
            case 2:
                tempRed = (int) (red + hPercent * (255 - red));
                tempBlue = (int) (blue + hPercent * (255 - blue));
                break;
            case 3:
            case 4:
                tempRed = (int) (red + hPercent * (255 - red));
                tempGreen = (int) (green + hPercent * (255 - green));
                break;
            default:
                SmartLog.i(TAG, "changeColor run in default case");
        }
        tempRed = (int) (tempRed - tempRed * vPercent);
        tempGreen = (int) (tempGreen - tempGreen * vPercent);
        tempBlue = (int) (tempBlue - tempBlue * vPercent);
        int color = Color.argb(transValue, tempRed, tempGreen, tempBlue);
        if (onColorChangeListener != null) {
            onColorChangeListener.colorChanged(color, vLocationLayoutParams, colorBarLayoutParams, xEvent);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.onColorChangeListener = onColorChangeListener;
    }

    @Override
    protected void onSizeChanged(int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        vBgColor.post(() -> {
            LayoutParams layoutParams = (LayoutParams) vBgColor.getLayoutParams();
            layoutParams.height = h - llProgress.getHeight();
            vBgColor.setLayoutParams(layoutParams);
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}
