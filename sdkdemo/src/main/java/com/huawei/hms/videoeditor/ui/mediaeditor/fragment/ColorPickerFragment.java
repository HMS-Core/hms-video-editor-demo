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

package com.huawei.hms.videoeditor.ui.mediaeditor.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.colorpicker.ColorPickerView;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

public class ColorPickerFragment extends DialogFragment {
    private View view;

    protected FragmentActivity mActivity;

    private TextView tv_title;

    private ImageView iv_certain;

    private ImageView iv_cancels;

    private ColorPickerView colorPicker;

    private int mSelectedColor;

    private RelativeLayout.LayoutParams mLayoutParams = null;

    private RelativeLayout.LayoutParams mSeekbarParams = null;

    private float xEvent = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    public void setLayoutParams(RelativeLayout.LayoutParams mLayoutParams) {
        this.mLayoutParams = mLayoutParams;
    }

    public void setSeekbarParams(RelativeLayout.LayoutParams mSeekbarParams) {
        this.mSeekbarParams = mSeekbarParams;
    }

    public void setXEvent(float xEvent) {
        this.xEvent = xEvent;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setDialog();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
        }
        view = inflater.inflate(R.layout.fragment_color_picker, null);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        if (mActivity != null) {
            view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                (int) (SizeUtils.screenHeight(mActivity) * 0.425f)));
        }
        tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.custom));
        iv_certain = view.findViewById(R.id.iv_certain);
        iv_cancels = view.findViewById(R.id.iv_close);
        iv_cancels.setVisibility(View.VISIBLE);
        colorPicker = view.findViewById(R.id.cpv_color_picker);
        colorPicker.setLocationLayoutParams(mLayoutParams, mSeekbarParams, xEvent);
    }

    private void initEvent() {

        colorPicker.setOnColorChangeListener((color, layoutParams, seekbarParams, xParams) -> {
            mSelectedColor = color;
            mLayoutParams = layoutParams;
            mSeekbarParams = seekbarParams;
            xEvent = xParams;
        });

        iv_certain.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (selectedListener != null) {
                selectedListener.onStyleSelected(mSelectedColor, mLayoutParams, mSeekbarParams, xEvent);
            }
            dismiss();
        }));

        iv_cancels.setOnClickListener(new OnClickRepeatedListener(v -> {
            dismiss();
        }));
    }

    private void setDialog() {
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.getDecorView().setPaddingRelative(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.BottomDialogAnimation;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable());
    }

    OnStyleSelectedListener selectedListener;

    public void setSelectedListener(OnStyleSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    public interface OnStyleSelectedListener {
        void onStyleSelected(int color, RelativeLayout.LayoutParams layoutParams,
            RelativeLayout.LayoutParams seekbarParams, float xParams);
    }

}
