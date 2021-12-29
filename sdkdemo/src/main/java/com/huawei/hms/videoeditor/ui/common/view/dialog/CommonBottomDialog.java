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

package com.huawei.hms.videoeditor.ui.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CommonBottomDialog extends Dialog {
    private TextView tvTitle;

    private TextView tvCancel;

    private TextView tvAllow;

    private ConstraintLayout clParent;

    private OnDialogClickLister onDialogClickLister;

    private Context context;

    private View viewLine;

    public CommonBottomDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public void setOnDialogClickLister(CommonBottomDialog.OnDialogClickLister onDialogClickLister) {
        this.onDialogClickLister = onDialogClickLister;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common_utils);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setDimAmount(0.5f);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
        setCanceledOnTouchOutside(true);
        initView();
        initEvent();
    }

    public void setBackground(int background) {
        clParent.setBackground(context.getResources().getDrawable(background));
    }

    public void setColor(int titleColor, int otherColor, int lineColor) {
        tvTitle.setTextColor(context.getColor(titleColor));
        tvAllow.setTextColor(context.getColor(otherColor));
        tvCancel.setTextColor(context.getColor(otherColor));
        viewLine.setBackgroundColor(context.getColor(lineColor));
    }

    private void initEvent() {
        tvAllow.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (onDialogClickLister != null) {
                onDialogClickLister.onAllowClick();
            }
            if (isShowing()) {
                dismiss();
            }
        }));

        tvCancel.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (onDialogClickLister != null) {
                onDialogClickLister.onCancelClick();
            }
            if (isShowing()) {
                dismiss();
            }
        }));
    }

    private void initView() {
        clParent = findViewById(R.id.cl_parent);
        tvTitle = findViewById(R.id.tv_title_hint);
        tvCancel = findViewById(R.id.tv_cancel);
        tvAllow = findViewById(R.id.tv_allow);
        viewLine = findViewById(R.id.view_line);
    }

    public void show(String title, String allow, String cacel) {
        show();
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(allow)) {
            tvAllow.setText(allow);
        }
        if (!TextUtils.isEmpty(cacel)) {
            tvCancel.setText(cacel);
        }
    }

    public interface OnDialogClickLister {
        void onCancelClick();

        void onAllowClick();
    }
}
