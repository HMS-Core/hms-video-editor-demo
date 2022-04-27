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

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.template.bean.Constant;
import com.huawei.hms.videoeditor.ui.template.utils.ModuleSelectManager;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

public class VideoModuleReplaceFragment extends BaseFragment {
    public static final String TAG = "VideoModulePickFragment";

    public static final String MEDIA_PICK_POSITION = "position";
    private ImageView mCloseIcon;
    private TextView mVideoTitleTv;
    private TextView mPictureTitleTv;

    private int contentId;
    private int currIndex = 0;
    private List<Fragment> fragments;
    private NavController mNavController;
    private View mVideoTitleIndicator;
    private View mPictureTitleIndicator;

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_video_module_replace;
    }

    @Override
    protected void initView(View view) {
        contentId = R.id.fragment_content;
        mCloseIcon = view.findViewById(R.id.iv_close);
        mVideoTitleTv = view.findViewById(R.id.tv_video);
        mPictureTitleTv = view.findViewById(R.id.tv_picture);
        mVideoTitleIndicator = view.findViewById(R.id.indicator_video);
        mPictureTitleIndicator = view.findViewById(R.id.indicator_picture);
    }

    @Override
    protected void initObject() {
        SafeBundle safeBundle = new SafeBundle(getArguments());
        int mReplacePosition = safeBundle.getInt(MEDIA_PICK_POSITION);
        ModuleVideoFragment mVideoFragment = new ModuleVideoFragment();
        ModulePictureFragment mPictureFragment = new ModulePictureFragment();
        if (mReplacePosition != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.EXTRA_SELECT_RESULT, mReplacePosition);
            mVideoFragment.setArguments(bundle);
            mPictureFragment.setArguments(bundle);
        }
        fragments = new ArrayList<>();
        fragments.add(mVideoFragment);
        fragments.add(mPictureFragment);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(contentId, fragments.get(0));
        transaction.commit();
        currIndex = 0;

        mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment_module_detail);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavDestination navDestination = mNavController.getCurrentDestination();
                if (navDestination != null && navDestination.getId() == R.id.videoModuleReplaceFragment) {
                    ModuleSelectManager.getInstance().destroy();
                    mNavController.navigateUp();
                }
            }
        });

        mCloseIcon.setOnClickListener(new OnClickRepeatedListener(v -> {
            NavDestination navDestination = mNavController.getCurrentDestination();
            if (navDestination != null && navDestination.getId() == R.id.videoModuleReplaceFragment) {
                ModuleSelectManager.getInstance().destroy();
                mNavController.navigateUp();
            }
        }));
        mVideoTitleTv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (currIndex != 0) {
                currIndex = 0;
                selectChanged(0);
                resetVideoOrPicture();
            }
        }));
        mPictureTitleTv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (currIndex == 0) {
                currIndex = 1;
                selectChanged(1);
                resetVideoOrPicture();
            }
        }));
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    private void resetVideoOrPicture() {
        mVideoTitleTv.setTextColor(currIndex == 0 ? ContextCompat.getColor(context, R.color.tab_text_tint_color) :
                ContextCompat.getColor(context, R.color.color_text_second_level));
        mPictureTitleTv.setTextColor(currIndex == 0 ? ContextCompat.getColor(context, R.color.color_text_second_level) :
                ContextCompat.getColor(context, R.color.tab_text_tint_color));
        mVideoTitleIndicator.setVisibility(currIndex == 0 ? View.VISIBLE : View.INVISIBLE);
        mPictureTitleIndicator.setVisibility(currIndex == 1 ? View.VISIBLE : View.INVISIBLE);
    }

    public void selectChanged(int index) {
        Fragment fragment = fragments.get(index);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        getCurrentFragment().onPause();
        if (fragment.isAdded()) {
            fragment.onResume();
        } else {
            ft.add(contentId, fragment);
        }
        showTab(index);
        ft.commit();
    }

    protected void showTab(int idx) {
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            if (idx == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commit();
        }
        currIndex = idx;
    }

    public Fragment getCurrentFragment() {
        return fragments.get(currIndex);
    }
}
