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

package com.huawei.hms.videoeditor.view;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.huawei.hms.videoeditor.sdk.HVEProject;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.appcompat.widget.LinearLayoutCompat;

public class HomeClipPopWindow extends PopupWindow {
    private Activity activity;

    private HVEProject position;

    private ActionOnClickListener mListener;

    private int popWidth = 0;

    private int popHeight = 0;

    public HomeClipPopWindow(Activity mActivity) {
        activity = mActivity;
        init();
    }

    protected void init() {
        View view = LayoutInflater.from(activity).inflate(R.layout.activity_clip_popup, null, false);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        LinearLayoutCompat rename = view.findViewById(R.id.line1_clip_popup);
        LinearLayoutCompat draftCopy = view.findViewById(R.id.line2_clip_popup);
        LinearLayoutCompat popupDel = view.findViewById(R.id.line3_clip_popup);
        this.setContentView(view);
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
        popWidth = view.getMeasuredWidth();
        popHeight = view.getMeasuredHeight();

        this.setOutsideTouchable(true);

        this.setFocusable(true);
        view.setFocusableInTouchMode(true);

        rename.setOnClickListener(new OnClickRepeatedListener(v -> {

            if (mListener != null) {
                mListener.onRenameClick(position);
            }
            dismiss();
        }));

        draftCopy.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mListener != null) {
                mListener.onCopyClick();
            }
            dismiss();
        }));

        popupDel.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mListener != null) {
                mListener.onDeleteClick();
            }
            dismiss();
        }));

        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
                return true;
            }
            return false;
        });

    }

    public void setPosition(HVEProject position) {
        this.position = position;
    }

    public int getPopWidth() {
        return popWidth;
    }

    public int getPopHeight() {
        return popHeight;
    }

    public void setOnActionClickListener(ActionOnClickListener listener) {
        mListener = listener;
    }

    public interface ActionOnClickListener {
        void onRenameClick(HVEProject item);

        void onCopyClick();

        void onDeleteClick();
    }
}
