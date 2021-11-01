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

package com.huawei.hms.videoeditor.ui.common.view.loading;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

public class HwLoadingDialog extends Dialog {

    private ProgressBar hwProgressBar;

    private TextView tvContent;

    public HwLoadingDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hw_loading_dialog);
        Window window = getWindow();
        window.setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 1.0F;
        lp.dimAmount = 0.0F;
        lp.verticalMargin = 0.415F;
        window.setAttributes(lp);
        initView();
    }

    private void initView() {
        hwProgressBar = findViewById(R.id.hw_progress_bar);
        tvContent = findViewById(R.id.tv_content);
    }

    public void show(int width) {
        if (!isShowing()) {
            show();
        }
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(width, width);
        hwProgressBar.setLayoutParams(layoutParams);
    }

    public void setHwCancelable(boolean isCancelable) {
        setCancelable(isCancelable);
    }

    public void setHwCanceledOnTouchOutside(boolean isCanceledOnTouchOutside) {
        setCanceledOnTouchOutside(isCanceledOnTouchOutside);
    }

    public void setTvContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
        }
    }

    public void setVisibility(boolean isVisibility) {
        if (!isVisibility) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
        }
    }
}
