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

package com.huawei.hms.videoeditor.ui.mediaeditor.blockface;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.hms.videoeditor.ui.common.BaseDialog;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;

public class FaceStickerDeleteDialog extends BaseDialog {

    private static final int PADDING = 0;

    private TextView mFaceDeleteConfirmTv;

    private TextView mFaceDeleteCancelTv;

    public FaceStickerDeleteDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_face_sticker_delete);
        initFaceStickerView();
        initFaceStickerEvent();
    }

    @Override
    public void show() {
        super.show();

        if (getWindow() == null) {
            return;
        }
        WindowManager.LayoutParams faceDeleteParams = getWindow().getAttributes();
        faceDeleteParams.gravity = Gravity.BOTTOM;
        faceDeleteParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        faceDeleteParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPaddingRelative(PADDING, PADDING, PADDING, PADDING);
        getWindow().setAttributes(faceDeleteParams);
    }

    private void initFaceStickerView() {
        mFaceDeleteConfirmTv = findViewById(R.id.home_clip_delete_dialog_ok);
        mFaceDeleteCancelTv = findViewById(R.id.home_clip_delete_dialog_cancel);
    }

    private void initFaceStickerEvent() {
        mFaceDeleteConfirmTv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mFaceDeletePositiveClickListener != null) {
                mFaceDeletePositiveClickListener.onPositiveClick();
            }
            dismiss();
        }));
        mFaceDeleteCancelTv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mFaceDeleteCancelClickListener != null) {
                mFaceDeleteCancelClickListener.onCancelClick();
            }
            dismiss();
        }));
    }

    private OnPositiveClickListener mFaceDeletePositiveClickListener;

    public void setOnPositiveClickListener(OnPositiveClickListener positiveClickListener) {
        this.mFaceDeletePositiveClickListener = positiveClickListener;
    }

    public interface OnPositiveClickListener {
        void onPositiveClick();
    }

    private OnCancelClickListener mFaceDeleteCancelClickListener;

    public void setOnCancelClickListener(OnCancelClickListener cancelClickListener) {
        this.mFaceDeleteCancelClickListener = cancelClickListener;
    }

    public interface OnCancelClickListener {
        void onCancelClick();
    }
}
