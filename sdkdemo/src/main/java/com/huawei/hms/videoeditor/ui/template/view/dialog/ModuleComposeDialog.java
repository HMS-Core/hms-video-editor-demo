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

package com.huawei.hms.videoeditor.ui.template.view.dialog;

import java.text.NumberFormat;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtil;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;

public class ModuleComposeDialog extends Dialog {
    private LinearLayout mCloseLayout;
    private TextView mMessageTv;
    private LoadingIndicatorView mIndicatorView;
    private String message;
    private int progress;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public ModuleComposeDialog(@NonNull Context context, String message) {
        super(context, R.style.DialogTheme);
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_compose_dialog_layout);
        initView();
        initEvent();
    }

    private void initView() {
        mCloseLayout = findViewById(R.id.close_layout);
        mIndicatorView = findViewById(R.id.indicator_community_detail);
        mMessageTv = findViewById(R.id.tv_message);
        updateMessage(message);
    }

    private void initEvent() {
        mCloseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCancelClickListener != null) {
                    mCancelClickListener.onCancelClick();
                }
                dismiss();
            }
        });
    }

    public void updateMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            mMessageTv.setText(message);
        }
    }

    public void updateMessage(String message, int progress) {
        if (progress < 0 || progress > 100) {
            this.updateMessage(message);
        } else {
            if (progress > this.getProgress()) {
                this.progress = progress;
            }
            this.updateMessage(message + " " +
                NumberFormat.getPercentInstance().format(BigDecimalUtil.div(this.progress, 100, 2)));
        }
    }

    public void showLoading() {
        mIndicatorView.show();
    }

    public void hideLoading() {
        mIndicatorView.hide();
    }

    private OnCancelClickListener mCancelClickListener;


    public void setOnCancelClickListener(OnCancelClickListener cancelClickListener) {
        this.mCancelClickListener = cancelClickListener;
    }

    public interface OnCancelClickListener {
        void onCancelClick();
    }
}
