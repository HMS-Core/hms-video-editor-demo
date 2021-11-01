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

package com.huawei.hms.videoeditor.ui.common;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.UpdateTimesManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuControlViewRouter;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    private static final int VIEW_HEIGHT_210 = 210;

    private static final int VIEW_HEIGHT_266 = 266;

    private static final int VIEW_HEIGHT_226 = 226;

    protected static final int NOMERA_HEIGHT = 0;

    protected static final int FIXED_HEIGHT_210 = 1;

    protected static final int FIXED_HEIGHT_266 = 2;

    protected static final int FIXED_HEIGHT_226 = 7;

    protected static final int DYNAMIC_HEIGHT = 3;

    protected static final int USUALLY_HEIGHT = 4;

    protected static final int SHOW_KEYBORD = 5;

    protected static final int RECORD_AUDIO = 6;

    private View mView;

    protected boolean isBackground;

    protected boolean isMaterials;

    protected abstract void initViewModelObserve();

    protected EditPreviewViewModel viewModel;

    protected FragmentActivity mActivity;

    protected Context context;

    protected ViewModelProvider.AndroidViewModelFactory mFactory;

    protected int statusBarColor = R.color.app_statusBarColor;

    protected int navigationBarColor = R.color.app_navigationBarColor;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        this.context = mActivity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        context = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        isBackground = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isBackground = false;
        if (mActivity != null) {
            setStatusBarColor(mActivity);
            if (isMaterials && UpdateTimesManager.getInstance().getState() == UpdateTimesManager.State.TIMEOUT) {
                SmartLog.i(TAG, "timeout, close page");
                mActivity.onBackPressed();
                UpdateTimesManager.getInstance().setState(UpdateTimesManager.State.INIT);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity != null) {
            setStatusBarColor(mActivity);
            mFactory = new ViewModelProvider.AndroidViewModelFactory(mActivity.getApplication());
            viewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModelObserve();
    }

    protected void setTimeoutEnable() {
        initTimeoutState();
        isMaterials = true;
    }

    protected void initTimeoutState() {
        viewModel.initTimeoutManager();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getContentViewId(), container, false);
        int viewLayoutEvent = setViewLayoutEvent();
        switch (viewLayoutEvent) {
            case NOMERA_HEIGHT:
                break;
            case FIXED_HEIGHT_210:
                setViewLayoutParams(mView, SizeUtils.dp2Px(mActivity, VIEW_HEIGHT_210));
                break;
            case FIXED_HEIGHT_226:
                setViewLayoutParams(mView, SizeUtils.dp2Px(mActivity, VIEW_HEIGHT_226));
                break;
            case FIXED_HEIGHT_266:
                setViewLayoutParams(mView, SizeUtils.dp2Px(mActivity, VIEW_HEIGHT_266));
                break;
            case DYNAMIC_HEIGHT:
                setViewLayoutParams(mView, (int) (SizeUtils.screenHeight(mActivity) * 0.425f));
                break;
            case USUALLY_HEIGHT:
                setViewLayoutParams(mView,
                    (int) (SizeUtils.screenHeight(mActivity) * 0.425f + SizeUtils.dp2Px(mActivity, 30)));
                break;
            case SHOW_KEYBORD:
                setViewLayoutParams(mView,
                    (int) (SizeUtils.screenHeight(mActivity) * 0.425f + SizeUtils.dp2Px(mActivity, 70)));
                break;
            case RECORD_AUDIO:
                setViewLayoutParams(mView, (int) (SizeUtils.screenHeight(mActivity) * 0.25f));
                break;
            default:
                break;
        }
        initView(mView);
        initObject();
        initData();
        initEvent();
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        reSetStatusBarColor(mActivity);
    }

    protected abstract int getContentViewId();

    protected abstract void initView(View view);

    protected abstract void initObject();

    protected abstract void initData();

    protected abstract void initEvent();

    protected abstract int setViewLayoutEvent();

    public void onBackPressed() {
    }

    protected void setStatusBarColor(Activity activity) {
        Window activityWindow = activity.getWindow();
        activityWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activityWindow.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
        activityWindow.setNavigationBarColor(ContextCompat.getColor(activity, navigationBarColor));
    }

    protected void reSetStatusBarColor(Activity activity) {
        Window activityWindow = activity.getWindow();
        activityWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activityWindow.setStatusBarColor(ContextCompat.getColor(activity, R.color.video_clips_color));
        activityWindow.setNavigationBarColor(ContextCompat.getColor(activity, R.color.home_color_FF181818));
    }

    private void setViewLayoutParams(View view, int viewHeight) {
        view.setLayoutParams(new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, viewHeight));
    }

    protected void setDynamicViewLayoutChange() {
        if (mView == null) {
            return;
        }
        mView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            (int) (SizeUtils.screenHeight(mActivity) * 0.425f)));
    }

    protected void setUsuallyViewLayoutChange() {
        if (mView == null) {
            return;
        }
        mView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            (int) (SizeUtils.screenHeight(mActivity) * 0.425f + SizeUtils.dp2Px(mActivity, 30))));
    }

    protected void setKeyBordViewLayoutChange() {
        if (mView == null) {
            return;
        }
        mView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            (int) (SizeUtils.screenHeight(mActivity) * 0.425f + SizeUtils.dp2Px(mActivity, 70))));
    }

    protected Stack<MenuControlViewRouter.Panel> getViewStack() {
        return MenuClickManager.getInstance().getViewStack();
    }

    protected boolean isValidActivity() {
        return mActivity != null && !mActivity.isFinishing() && !mActivity.isDestroyed();
    }
}
