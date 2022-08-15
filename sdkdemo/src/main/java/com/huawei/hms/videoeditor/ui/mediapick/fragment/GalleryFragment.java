
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

package com.huawei.hms.videoeditor.ui.mediapick.fragment;

import static com.huawei.hms.videoeditor.ui.mediapick.activity.MediaPickActivity.DURATION;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.view.RotationPopupWiew;
import com.huawei.hms.videoeditor.ui.mediapick.activity.MediaPickActivity;
import com.huawei.hms.videoeditor.ui.mediapick.viewmodel.MediaFolderViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;
import com.huawei.secure.android.common.intent.SafeIntent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GalleryFragment extends BaseFragment {
    public static final String SHOW_MEDIA_TYPE = "showMediaType";

    private LinearLayout mRotationSelectView;

    private TextView mVideoTv;

    private TextView mPictureTv;

    private RotationPopupWiew mPopupWiew;

    private TextView mRotationSelect;

    private View mVideoIndicator;

    private View mPictureIndicator;

    private List<Fragment> fragmentList;

    private int currentFragmentIndex = 0;

    private MediaFolderViewModel folderViewModel;

    private int mShowMediaType = 2; // 0 video 1 photo 2 both

    public static GalleryFragment newInstance(SafeBundle bound) {
        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(bound.getBundle());
        return fragment;
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.home_color_FF181818;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_gallery;
    }

    @Override
    protected void initView(View view) {
        mVideoTv = view.findViewById(R.id.tv_video);
        mPictureTv = view.findViewById(R.id.tv_picture);
        mVideoIndicator = view.findViewById(R.id.indicator_video);
        mPictureIndicator = view.findViewById(R.id.indicator_picture);
        mRotationSelectView = view.findViewById(R.id.rotation_select_view);
        mRotationSelect = view.findViewById(R.id.rotation_select);
    }

    @Override
    protected void initObject() {
        initSpinner();
        SafeIntent intent = new SafeIntent(mActivity.getIntent());
        mShowMediaType = intent.getIntExtra(SHOW_MEDIA_TYPE, mShowMediaType);
        List<MediaData> mImportedList = intent.getParcelableArrayListExtra(Constant.EXTRA_SELECT_RESULT);
        long mCheckDuration = intent.getLongExtra(DURATION, 0);
        int actionType = intent.getIntExtra(MediaPickActivity.ACTION_TYPE, 0);
        folderViewModel = new ViewModelProvider(mActivity, mFactory).get(MediaFolderViewModel.class);
        fragmentList = new ArrayList<>(2);

        PickVideoFragment mVideoFragment = new PickVideoFragment();
        PickPictureFragment mPictureFragment = new PickPictureFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DURATION, mCheckDuration);
        bundle.putInt(MediaPickActivity.ACTION_TYPE, actionType);
        bundle.putInt(SHOW_MEDIA_TYPE,mShowMediaType);
        if (mImportedList != null && mImportedList.size() > 0) {
            bundle.putParcelableArrayList(Constant.EXTRA_SELECT_RESULT,
                (ArrayList<? extends Parcelable>) mImportedList);
        }
        mVideoFragment.setArguments(bundle);
        mPictureFragment.setArguments(bundle);

        if (mShowMediaType == 0) {
            fragmentList.add(mVideoFragment);
        } else if (mShowMediaType == 1) {
            fragmentList.add(mPictureFragment);
        } else {
            fragmentList.add(mVideoFragment);
            fragmentList.add(mPictureFragment);
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_content, fragmentList.get(0));
        transaction.commit();
        currentFragmentIndex = 0;
    }

    @Override
    protected void initData() {
        resetVideoOrPicture();
    }

    @Override
    protected void initEvent() {
        mVideoTv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (currentFragmentIndex != 0) {
                selectChanged(0);
                resetVideoOrPicture();
            }
        }));

        mPictureTv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (currentFragmentIndex != 1) {
                if (mShowMediaType == 1) {
                    selectChanged(0);
                } else {
                    selectChanged(1);
                }
                resetVideoOrPicture();
            }
        }));
    }

    private void initSpinner() {
        final String[] rotationMenu = mActivity.getResources().getStringArray(R.array.rotation_menu);
        List<String> rotationList = new ArrayList<>(Arrays.asList(rotationMenu));
        mRotationSelect.setText(rotationMenu.length > 0 ? rotationMenu[0] : "");

        int width;

        if (mPopupWiew == null) {
            mPopupWiew = new RotationPopupWiew(mActivity, rotationList);
            width = mPopupWiew.getPopupWidth();
        } else {
            width = mPopupWiew.getContentView().getMeasuredWidth();
        }

        mPopupWiew.setOnActionClickListener(new RotationPopupWiew.OnActionClickListener() {
            @Override
            public void onFullClick() {
                mRotationSelect.setText(rotationMenu.length > 0 ? rotationMenu[0] : "");
                folderViewModel.setRotationState(0);
            }

            @Override
            public void onPortraitClick() {
                mRotationSelect.setText(rotationMenu.length > 1 ? rotationMenu[1] : "");
                folderViewModel.setRotationState(1);
            }

            @Override
            public void onLandClick() {
                mRotationSelect.setText(rotationMenu.length > 2 ? rotationMenu[2] : "");
                folderViewModel.setRotationState(2);
            }
        });

        mRotationSelectView.setOnClickListener(new OnClickRepeatedListener(v -> {
            mPopupWiew.showAsDropDown(mRotationSelectView, -width + mRotationSelectView.getWidth() + 2,
                mRotationSelectView.getHeight() - 40);

            mPopupWiew.setOnDismissListener(() -> {
                WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
                params.alpha = 1.0f;
                mActivity.getWindow().setAttributes(params);
            });

        }));
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    private void resetVideoOrPicture() {
        folderViewModel.setGalleryVideoSelect(currentFragmentIndex == 0);
        if (mShowMediaType == 0) {
            mVideoTv.setTextColor(ContextCompat.getColor(context, R.color.translucent_white_90));
            mVideoIndicator.setVisibility(View.INVISIBLE);
            mVideoTv.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);

            mPictureTv.setVisibility(View.GONE);
            mPictureIndicator.setVisibility(View.GONE);
        } else if (mShowMediaType == 1) {
            mPictureTv.setTextColor(ContextCompat.getColor(context, R.color.translucent_white_90));
            mPictureIndicator.setVisibility(View.INVISIBLE);
            mPictureTv.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);

            mVideoTv.setVisibility(View.GONE);
            mVideoIndicator.setVisibility(View.GONE);
        } else {
            mVideoTv.setTextColor(currentFragmentIndex == 0 ? ContextCompat.getColor(context, R.color.tab_text_tint_color) : ContextCompat.getColor(context, R.color.translucent_white_90));
            mPictureTv.setTextColor(currentFragmentIndex == 0 ? ContextCompat.getColor(context, R.color.translucent_white_90) : ContextCompat.getColor(context, R.color.tab_text_tint_color));
            mVideoIndicator.setVisibility(currentFragmentIndex == 0 ? View.VISIBLE : View.INVISIBLE);
            mPictureIndicator.setVisibility(currentFragmentIndex == 1 ? View.VISIBLE : View.INVISIBLE);
            if (currentFragmentIndex == 0) {
                mVideoTv.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                mPictureTv.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
            } else {
                mVideoTv.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                mPictureTv.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            }
        }
    }

    public void selectChanged(int index) {
        Fragment fragment = fragmentList.get(index);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        getCurrentFragment().onPause();
        if (fragment.isAdded()) {
            fragment.onResume();
        } else {
            transaction.add(R.id.fragment_content, fragment);
        }
        showTab(index);
        transaction.commit();
    }

    protected void showTab(int idx) {
        for (int i = 0; i < fragmentList.size(); i++) {
            Fragment fragment = fragmentList.get(i);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (idx == i) {
                transaction.show(fragment);
            } else {
                transaction.hide(fragment);
            }
            transaction.commit();
        }
        currentFragmentIndex = idx;
    }

    private Fragment getCurrentFragment() {
        return fragmentList.get(currentFragmentIndex);
    }
}