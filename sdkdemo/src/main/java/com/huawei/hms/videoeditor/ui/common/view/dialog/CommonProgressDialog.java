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

package com.huawei.hms.videoeditor.ui.common.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class CommonProgressDialog extends Dialog {
    private static final String TAG = "CommonProgressDialog";

    private final TextView titleTv;

    private final TextView progressTv;

    private final ProgressBar progressBar;

    private final OnClickListener onClickListener;

    public CommonProgressDialog(Context context, OnClickListener onClickListener) {
        super(context, R.style.DialogTheme);
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.simple_export_dialog, null, false);
        setContentView(view);
        this.onClickListener = onClickListener;
        final ImageView btnCancel = view.findViewById(R.id.iv_close);
        btnCancel.setOnClickListener(new OnClickRepeatedListener(View -> {
            cancel();
            if (onClickListener != null) {
                onClickListener.onCancel();
            }
        }));
        titleTv = view.findViewById(R.id.tv_export_tips);
        progressTv = view.findViewById(R.id.tv_export_progress);
        progressBar = view.findViewById(R.id.progress_bar);

        Window window = getWindow();
        if (window == null) {
            SmartLog.d(TAG, "window is null");
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setLayout(SizeUtils.screenWidth(context) - SizeUtils.dp2Px(context, 32), SizeUtils.dp2Px(context, 88));
        getWindow().getDecorView().setPaddingRelative(0, 0, 0, SizeUtils.dp2Px(context, 16));
    }

    public void setTitleValue(String value) {
        if (!StringUtil.isEmpty(value)) {
            titleTv.setText(value);
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateProgress(int progress) {
        if (progress <= 100 && progress >= 0) {
            progressTv.setText(progress + "%");
            progressBar.setProgress(progress);
        }
    }

    public interface OnClickListener {
        void onCancel();
    }

    @Override
    public void onBackPressed() {
        if (isShowing() && onClickListener != null) {
            onClickListener.onCancel();
        }
        super.onBackPressed();
    }
}
