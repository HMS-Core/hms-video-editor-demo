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

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.BaseDialog;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;

public class HumanTrackingProgressDialog extends BaseDialog {

    private TextView tvName;

    private TextView tvProgress;

    private ProgressBar progress;

    private ImageView ivStop;

    private String title;

    private OnProgressClick onProgressClick;

    public void setOnProgressClick(OnProgressClick onProgressClick) {
        this.onProgressClick = onProgressClick;
    }

    public HumanTrackingProgressDialog(@NonNull Context context, String title) {
        super(context, R.style.DialogProgress);
        this.title = title;
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setDimAmount(0.5f);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_downloading_progress);

        tvName = findViewById(R.id.tv_name);
        tvProgress = findViewById(R.id.tv_progress);
        progress = findViewById(R.id.progress);
        ivStop = findViewById(R.id.iv_stop);

        tvName.setText(title);

        ivStop.setOnClickListener(new OnClickRepeatedListener(v -> {
            dismiss();
            if (onProgressClick != null) {
                onProgressClick.onCancel();
            }
        }));
    }

    public void setProgress(int progress) {
        this.progress.setProgress(progress);
        tvProgress.setText(progress + "%");
    }

    public void setStopVisble(boolean visble) {
        ivStop.setVisibility(visble ? View.VISIBLE : View.GONE);
    }

    public void show(WindowManager windowManager) {
        show();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        Point point = new Point();
        defaultDisplay.getSize(point);
        params.width = (int) (point.x * 1);
        getWindow().setAttributes(params);
    }

    public interface OnProgressClick {
        void onCancel();
    }
}
