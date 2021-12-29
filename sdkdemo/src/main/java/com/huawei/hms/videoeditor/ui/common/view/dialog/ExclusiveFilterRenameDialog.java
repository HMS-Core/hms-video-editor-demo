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
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.huawei.hms.videoeditor.sdk.HVEProject;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;

public class ExclusiveFilterRenameDialog extends Dialog {
    private static final int MAX_TEXT = 5;

    private TextView mRenameConfirmView;

    private TextView mRenameCancelView;

    private EditText mNameEditorView;

    private HVEProject item;

    private Activity mActivity;

    private String mPrevName;

    private TextView mEditRenameLoad;

    public ExclusiveFilterRenameDialog(@NonNull Activity activity, String prevName) {
        super(activity, R.style.DialogTheme);
        this.mActivity = activity;
        mPrevName = prevName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exclusive_filter_rename);
        initView();
        initEvent();
    }

    @Override
    public void show() {
        super.show();

        WindowManager.LayoutParams renameParams = getWindow().getAttributes();
        renameParams.gravity = Gravity.BOTTOM;
        renameParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        renameParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPaddingRelative(0, 0, 0, 0);
        getWindow().setAttributes(renameParams);
        new Handler().postDelayed(() -> {
            if (mActivity != null) {
                mNameEditorView.setFocusable(true);
                mNameEditorView.setFocusableInTouchMode(true);
                mNameEditorView.requestFocus();
                showKeyboard(mActivity);

            }
        }, 300);
    }

    private void initView() {
        mRenameConfirmView = findViewById(R.id.home_clip_rename_dialog_ok);
        mRenameCancelView = findViewById(R.id.home_clip_rename_dialog_cancel);
        mNameEditorView = findViewById(R.id.home_clip_rename_dialog_name);
        if (item != null && item.getName() != null) {
            mNameEditorView.setHint(item.getName());
        }
        if (!TextUtils.isEmpty(mPrevName)) {
            mNameEditorView.setHint(mPrevName);
        }
        mEditRenameLoad = findViewById(R.id.text_rename_load);
        mEditRenameLoad.setText(String.format(Locale.ROOT,
            getContext().getResources().getString(R.string.exclusive_filter_tip_text11), MAX_TEXT));
    }

    private void initEvent() {
        mRenameCancelView.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRenameCancelClickListener != null) {
                    mRenameCancelClickListener.onCancelClick();
                }
                dismiss();
            }
        }));
        mRenameConfirmView.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRenamePositiveClickListener != null) {
                    mRenamePositiveClickListener.onPositiveClick(mNameEditorView.getText().toString());
                }
                dismiss();
            }
        }));

        mNameEditorView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int renameNum = charSequence.length();
                if (renameNum > 5) {
                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.filter_rename_line_red);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mNameEditorView.setCompoundDrawables(null, null, null, drawable);
                    mEditRenameLoad.setVisibility(View.VISIBLE);
                } else if (renameNum == 0) {
                    Drawable drawable =
                        getContext().getResources().getDrawable(R.drawable.filter_rename_line_default_white);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mNameEditorView.setCompoundDrawables(null, null, null, drawable);
                    mEditRenameLoad.setVisibility(View.GONE);
                } else {
                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.filter_rename_line);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mNameEditorView.setCompoundDrawables(null, null, null, drawable);
                    mEditRenameLoad.setVisibility(View.GONE);
                }

                boolean isConfirmEnable = renameNum <= 5 && renameNum != 0;
                mRenameConfirmView.setEnabled(isConfirmEnable);
                mRenameConfirmView
                    .setTextColor(isConfirmEnable ? getContext().getResources().getColor(R.color.color_text_focus)
                        : getContext().getResources().getColor(R.color.filter_rename_text_color));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private OnPositiveClickListener mRenamePositiveClickListener;

    public void setOnPositiveClickListener(OnPositiveClickListener positiveClickListener) {
        this.mRenamePositiveClickListener = positiveClickListener;
    }

    public interface OnPositiveClickListener {
        void onPositiveClick(String updateName);
    }

    private OnCancelClickListener mRenameCancelClickListener;

    public void setOnCancelClickListener(OnCancelClickListener cancelClickListener) {
        this.mRenameCancelClickListener = cancelClickListener;
    }

    public interface OnCancelClickListener {
        void onCancelClick();
    }

    private void showKeyboard(Activity context) {
        InputMethodManager inputMethodManager =
            (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(mNameEditorView.getWindowToken(), InputMethodManager.SHOW_IMPLICIT,
            0);
    }
}
