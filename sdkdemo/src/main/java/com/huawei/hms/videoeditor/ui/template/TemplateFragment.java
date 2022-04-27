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

package com.huawei.hms.videoeditor.ui.template;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.template.module.TemplateHomeFragment;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class TemplateFragment extends BaseFragment {
    private TextView mTemplateTv;

    private TextView mGuideTv;

    private ImageView mIvBack;

    private int contentId;

    private int currIndex = 0;

    private List<Fragment> fragments;

    private String source;

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        navigationBarColor = R.color.home_color_FF181818;
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            source = args.getString("source", "");
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_main_template;
    }

    @Override
    protected void initView(View view) {
        mTemplateTv = view.findViewById(R.id.tv_title_template);
        mGuideTv = view.findViewById(R.id.tv_title_guide);
        mIvBack = view.findViewById(R.id.iv_back);
    }

    @Override
    protected void initObject() {
        contentId = R.id.fragment_content;

        TemplateHomeFragment mTemplateHomeFragment = new TemplateHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("source", source);
        mTemplateHomeFragment.setArguments(bundle);
        fragments = new ArrayList<>();
        fragments.add(mTemplateHomeFragment);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(contentId, fragments.get(0));
        transaction.commitAllowingStateLoss();
        currIndex = 0;
    }

    @Override
    protected void initData() {
        mIvBack.setVisibility(View.GONE);
    }

    @Override
    protected void initEvent() {
        mTemplateTv.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currIndex != 0) {
                    selectChanged(0);
                    resetSelect();
                }
            }
        }, 100));

        mGuideTv.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currIndex != 1) {
                    selectChanged(1);
                    resetSelect();
                }
            }
        }, 100));

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.onBackPressed();
                }
            }
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
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
        ft.commitAllowingStateLoss();
    }

    protected void showTab(int idx) {
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            if (!fragment.isAdded()) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                if (idx == i) {
                    ft.show(fragment);
                } else {
                    ft.hide(fragment);
                }
                ft.commitAllowingStateLoss();
            }
        }
        currIndex = idx;
    }

    public Fragment getCurrentFragment() {
        return fragments.get(currIndex);
    }

    private void resetSelect() {
        mTemplateTv.setTextColor(currIndex == 0 ? ContextCompat.getColor(context, R.color.translucent_white_10)
            : ContextCompat.getColor(context, R.color.color_text_second_level));
        mTemplateTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
            currIndex == 0 ? SizeUtils.dp2Px(context, 24) : SizeUtils.dp2Px(context, 16));
        mGuideTv.setTextColor(currIndex != 0 ? ContextCompat.getColor(context, R.color.translucent_white_10)
            : ContextCompat.getColor(context, R.color.color_text_second_level));
        mGuideTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
            currIndex != 0 ? SizeUtils.dp2Px(context, 24) : SizeUtils.dp2Px(context, 16));
    }
}