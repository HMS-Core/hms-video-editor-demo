
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

package com.huawei.hms.videoeditor.ui.mediaeditor.persontrack;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;

public class HumanTrackingConfirmDialog extends Dialog {

    private TextView mDeleteConfirmTv;

    private TextView mDeleteCancelTv;

    public HumanTrackingConfirmDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_human_tracking_confirm);
        initView();
        initEvent();
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams deleteParams = getWindow().getAttributes();
        deleteParams.gravity = Gravity.BOTTOM;
        deleteParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        deleteParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPaddingRelative(0, 0, 0, 0);
        getWindow().setAttributes(deleteParams);
    }

    private void initView() {
        mDeleteConfirmTv = findViewById(R.id.home_clip_delete_dialog_ok);
        mDeleteCancelTv = findViewById(R.id.home_clip_delete_dialog_cancel);
    }

    private void initEvent() {
        mDeleteCancelTv.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeleteCancelClickListener != null) {
                    mDeleteCancelClickListener.onCancelClick();
                }
                dismiss();
            }
        }));
        mDeleteConfirmTv.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeletePositiveClickListener != null) {
                    mDeletePositiveClickListener.onPositiveClick();
                }
                dismiss();
            }
        }));
    }

    private OnPositiveClickListener mDeletePositiveClickListener;

    public void setOnPositiveClickListener(OnPositiveClickListener positiveClickListener) {
        this.mDeletePositiveClickListener = positiveClickListener;
    }

    public interface OnPositiveClickListener {
        void onPositiveClick();
    }

    private OnCancelClickListener mDeleteCancelClickListener;

    public void setOnCancelClickListener(OnCancelClickListener cancelClickListener) {
        this.mDeleteCancelClickListener = cancelClickListener;
    }

    public interface OnCancelClickListener {
        void onCancelClick();
    }
}
