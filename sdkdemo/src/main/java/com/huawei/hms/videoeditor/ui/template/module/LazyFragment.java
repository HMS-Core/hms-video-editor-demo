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

package com.huawei.hms.videoeditor.ui.template.module;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.Objects;

public abstract class LazyFragment extends Fragment {
    protected FragmentActivity mActivity;

    protected Context mContext;

    private boolean isFirstLoad = true;

    protected ViewModelProvider.AndroidViewModelFactory mFactory;

    protected int navigationBarColor = R.color.app_navigationBarColor;

    protected int statusBarColor = R.color.app_statusBarColor;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        mContext = mActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity != null) {
            setStatusBarColor(Objects.requireNonNull(mActivity));
            mFactory = new ViewModelProvider.AndroidViewModelFactory(mActivity.getApplication());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(getContentViewId(), null);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            initObject();
            initData();
            initEvent();
            isFirstLoad = false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        mContext = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mActivity != null) {
            reSetStatusBarColor(Objects.requireNonNull(mActivity));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirstLoad = true;
    }

    protected abstract int getContentViewId();

    protected void initData() {

    }

    protected void initView(View view) {

    }

    protected void initEvent() {

    }

    protected void initObject() {

    }

    protected void reSetStatusBarColor(Activity activity) {
        Window activityWindow = activity.getWindow();
        activityWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activityWindow.setStatusBarColor(ContextCompat.getColor(activity, R.color.video_clips_color));
        activityWindow.setNavigationBarColor(ContextCompat.getColor(activity, R.color.home_color_FF181818));
    }

    protected void setStatusBarColor(Activity activity) {
        Window activityWindow = activity.getWindow();
        activityWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activityWindow.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
        activityWindow.setNavigationBarColor(ContextCompat.getColor(activity, navigationBarColor));
    }
}
