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

package com.huawei.hms.videoeditor.ui.common.view;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class ExclusiveFilterPopupView extends PopupWindow {
    private final Activity mActivity;

    private LinearLayout mCloneFilter;

    private LinearLayout mImitationFilter;

    private LinearLayout mRenameFilter;

    private LinearLayout mDeleteFilter;

    private OnCreateFilterClickListener mOnCreateFilterClickListener;

    private OnEditorFilterClickListener mOnEditorFilterClickListener;

    public void setOnCreateFilterClickListener(OnCreateFilterClickListener onCreateFilterClickListener) {
        this.mOnCreateFilterClickListener = onCreateFilterClickListener;
    }

    public void setOnEditorFilterClickListener(OnEditorFilterClickListener onEditorFilterClickListener) {
        this.mOnEditorFilterClickListener = onEditorFilterClickListener;
    }

    public ExclusiveFilterPopupView(Activity activity, int viewTag) {
        mActivity = activity;
        initView(viewTag);
    }

    private void initView(int viewTag) {
        View popupView = null;
        if (viewTag == Constant.FILTER_POPUP_VIEW_CREATE || viewTag == Constant.FILTER_POPUP_VIEW_EDITOR) {
            popupView = LayoutInflater.from(mActivity).inflate(R.layout.layout_filter_exclusive_popupview, null, false);
            initPopView(popupView, viewTag);
        } else {
            popupView =
                LayoutInflater.from(mActivity).inflate(R.layout.layout_filter_exclusive_guide_popupview, null, false);
        }
        this.setContentView(popupView);
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        popupView.setFocusableInTouchMode(true);
    }

    private void initPopView(View view, int viewTag) {
        mCloneFilter = view.findViewById(R.id.ll_clone_filter);
        mImitationFilter = view.findViewById(R.id.ll_imitation_filter);
        mRenameFilter = view.findViewById(R.id.ll_rename);
        mDeleteFilter = view.findViewById(R.id.ll_delete);
        if (viewTag == Constant.FILTER_POPUP_VIEW_CREATE) {
            mCloneFilter.setVisibility(View.VISIBLE);
            mImitationFilter.setVisibility(View.VISIBLE);
            mRenameFilter.setVisibility(View.GONE);
            mDeleteFilter.setVisibility(View.GONE);
        } else {
            mCloneFilter.setVisibility(View.GONE);
            mImitationFilter.setVisibility(View.GONE);
            mRenameFilter.setVisibility(View.VISIBLE);
            mDeleteFilter.setVisibility(View.VISIBLE);
        }
        this.setOutsideTouchable(true);
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
                return true;
            }
            return false;
        });
        initEvent();
    }

    private void initEvent() {
        mCloneFilter.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnCreateFilterClickListener != null) {
                mOnCreateFilterClickListener.onCloneFilterClick();
            }
            dismiss();
        }));

        mImitationFilter.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnCreateFilterClickListener != null) {
                mOnCreateFilterClickListener.onImitFilterClick();
            }
            dismiss();
        }));

        mRenameFilter.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnEditorFilterClickListener != null) {
                mOnEditorFilterClickListener.onRenameFilterClick();
            }
            dismiss();
        }));

        mDeleteFilter.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnEditorFilterClickListener != null) {
                mOnEditorFilterClickListener.onDeleteFilterClick();
            }
            dismiss();
        }));
    }

    public interface OnCreateFilterClickListener {
        void onCloneFilterClick();

        void onImitFilterClick();
    }

    public interface OnEditorFilterClickListener {
        void onRenameFilterClick();

        void onDeleteFilterClick();
    }
}
