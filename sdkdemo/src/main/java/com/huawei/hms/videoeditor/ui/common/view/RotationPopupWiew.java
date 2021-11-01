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

import java.util.List;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class RotationPopupWiew extends PopupWindow {
    private LinearLayout mFullView;

    private LinearLayout mPortraitView;

    private LinearLayout mLandView;

    private TextView mFullScreen;

    private TextView mPortraitScreen;

    private TextView mLandScreen;

    private Activity mActivity;

    private List<String> mList;

    private OnActionClickListener mOnActionClickListener;

    private int popupWidth = 0;

    private int popupHeight = 0;

    public RotationPopupWiew(Activity activity, List<String> list) {
        this.mActivity = activity;
        this.mList = list;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.rotation_popup, null, false);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mFullView = view.findViewById(R.id.rotation_child_full);
        mFullScreen = view.findViewById(R.id.rotation_full_screen);
        mPortraitView = view.findViewById(R.id.rotation_child_portrait);
        mPortraitScreen = view.findViewById(R.id.rotation_portrait_screen);
        mLandView = view.findViewById(R.id.rotation_child_land);
        mLandScreen = view.findViewById(R.id.rotation_landscape);
        mFullScreen.setText(mList.size() > 0 ? mList.get(0) : "");
        mPortraitScreen.setText(mList.size() > 1 ? mList.get(1) : "");
        mLandScreen.setText(mList.size() > 2 ? mList.get(2) : "");

        this.setContentView(view);
        this.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWidth = view.getMeasuredWidth();
        popupHeight = view.getMeasuredHeight();

        this.setOutsideTouchable(true);

        this.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
                return true;
            }
            return false;
        });

        initEvent();
    }

    private void initEvent() {
        mFullView.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnActionClickListener != null) {
                mOnActionClickListener.onFullClick();
            }
            dismiss();
        }));

        mPortraitView.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnActionClickListener != null) {
                mOnActionClickListener.onPortraitClick();
            }
            dismiss();
        }));

        mLandView.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnActionClickListener != null) {
                mOnActionClickListener.onLandClick();
            }
            dismiss();
        }));
    }

    public interface OnActionClickListener {
        void onFullClick();

        void onPortraitClick();

        void onLandClick();
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        this.mOnActionClickListener = listener;
    }

    public int getPopupWidth() {
        return popupWidth;
    }

    public void setPopupWidth(int popupWidth) {
        this.popupWidth = popupWidth;
    }

    public int getPopupHeight() {
        return popupHeight;
    }

    public void setPopupHeight(int popupHeight) {
        this.popupHeight = popupHeight;
    }
}
