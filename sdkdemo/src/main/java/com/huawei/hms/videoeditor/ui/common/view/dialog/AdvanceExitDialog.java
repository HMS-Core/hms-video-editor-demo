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

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huawei.hms.videoeditor.common.utils.LanguageUtils;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class AdvanceExitDialog extends Dialog {
    private static final String TAG = "AdvanceExitDialog";

    public AdvanceExitDialog(Context context, OnClickListener onClickListener) {
        super(context, R.style.DialogTheme);
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.advance_exit_dialog, null, false);
        setContentView(view);
        TextView cancelTv = view.findViewById(R.id.cancel_tv);
        TextView saveTv = view.findViewById(R.id.confirm_tv);

        Resources resources = context.getResources();
        if (resources != null) {
            if (LanguageUtils.isEn()) {
                cancelTv.setText(resources.getString(R.string.is_give_up_no).toUpperCase(Locale.ENGLISH));
                saveTv.setText(resources.getString(R.string.save_wza).toUpperCase(Locale.ENGLISH));
            } else {
                cancelTv.setText(resources.getString(R.string.is_give_up_no));
                saveTv.setText(resources.getString(R.string.save_wza));
            }
        }
        saveTv.setOnClickListener(new OnClickRepeatedListener(View -> {
            cancel();
            if (onClickListener != null) {
                onClickListener.onSave();
            }
        }));

        cancelTv.setOnClickListener(new OnClickRepeatedListener(View -> {
            cancel();
            if (onClickListener != null) {
                onClickListener.onBack();
            }
        }));

        Window window = getWindow();
        if (window == null) {
            SmartLog.d(TAG, "window is null");
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(params);
        window.setLayout(SizeUtils.screenWidth(context) - SizeUtils.dp2Px(context, 32),
            WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().getDecorView().setPaddingRelative(0, 0, 0, SizeUtils.dp2Px(context, 16));
    }

    public interface OnClickListener {
        void onSave();

        void onBack();
    }
}
