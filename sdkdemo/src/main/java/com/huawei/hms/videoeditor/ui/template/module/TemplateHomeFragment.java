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

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopInfo;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopLayout;
import com.huawei.hms.videoeditor.ui.template.bean.TemplateCacheData;
import com.huawei.hms.videoeditor.ui.template.utils.ObjectUtils;
import com.huawei.hms.videoeditor.ui.template.viewmodel.HVETemplateHomeModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class TemplateHomeFragment extends BaseFragment {
    private static final String TAG = "TemplateHomeFragment";

    private static final int DEFAULT_SELECT_INDEX = 0;

    public static final String SOURCE_KEY = "source";

    private TabTopLayout mTabTopLayout;

    private ViewPager2 mViewPager2;

    private List<TabTopInfo<?>> mInfoList;

    private HVETemplateHomeModel mHVETemplateModel;

    private List<HVEColumnInfo> hVECategoryList;

    private int mTopTabSelectIndex = 0;

    private TextView mTemplateHomeErrorTV;

    private RelativeLayout mTemplateHomeErrorLayout;

    private TextView mErrorTv;

    private RelativeLayout mLoadingLayout;

    private String mSource;

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        navigationBarColor = R.color.home_color_FF181818;
        super.onCreate(savedInstanceState);
        SafeBundle args = new SafeBundle(getArguments());
        mSource = args.getString(SOURCE_KEY, "");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_template_home_t;
    }

    @Override
    protected void initView(View view) {
        mTabTopLayout = view.findViewById(R.id.tab_top_layout);
        if (ScreenUtil.isRTL()) {
            mTabTopLayout.setScaleX(RTL_UI);
        } else {
            mTabTopLayout.setScaleX(LTR_UI);
        }
        mViewPager2 = view.findViewById(R.id.view_pager2);
        mTemplateHomeErrorTV = view.findViewById(R.id.empty_layout);
        mTemplateHomeErrorLayout = view.findViewById(R.id.error_layout);
        mErrorTv = view.findViewById(R.id.error_text);
        mLoadingLayout = view.findViewById(R.id.loading_layout);
    }

    @Override
    protected void initObject() {
        mHVETemplateModel = new ViewModelProvider(this, mFactory).get(HVETemplateHomeModel.class);
        hVECategoryList = new ArrayList<>();
        mInfoList = new ArrayList<>();
    }

    @Override
    protected void initData() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mHVETemplateModel.initColumns();
        mHVETemplateModel.getHVECategoryList().observe(this, HVECategoryList -> {
            SmartLog.i(TAG, "category observe");
            mLoadingLayout.setVisibility(View.GONE);
            if (HVECategoryList == null || HVECategoryList.size() == 0) {
                SmartLog.i(TAG, "HVECategoryList is null");
                mTemplateHomeErrorTV.setVisibility(View.VISIBLE);
                return;
            }
            SmartLog.i(TAG, "HVECategoryList is not null");
            mTemplateHomeErrorTV.setVisibility(View.GONE);
            SmartLog.i(TAG, "HVECategoryList=" + HVECategoryList.size());
            hVECategoryList.clear();
            hVECategoryList.addAll(HVECategoryList);
            initTabTop();
        });

        mHVETemplateModel.getErrorString().observe(getViewLifecycleOwner(), errorString -> {
            mTemplateHomeErrorLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setVisibility(View.GONE);
            mErrorTv.setText(errorString);
        });
    }

    private void initTabTop() {
        int defaultColor = ContextCompat.getColor(context, R.color.color_text_unselected);
        int color = ContextCompat.getColor(context, R.color.tab_text_tint_color);
        int leftRightPadding = SizeUtils.dp2Px(context, 8);

        for (HVEColumnInfo hveCategory : hVECategoryList) {
            SmartLog.i(TAG, hveCategory.getColumnName());
        }
        hVECategoryList = ObjectUtils.removeDupliByRecordId(hVECategoryList);
        mInfoList.clear();
        for (HVEColumnInfo hveCategory : hVECategoryList) {
            SmartLog.i(TAG, hveCategory.getColumnName());
            TabTopInfo<?> info = new TabTopInfo<>(hveCategory.getColumnName(), false, defaultColor, color, 18, 24,
                leftRightPadding, leftRightPadding);
            info.setFamilyName("HarmonyHeiTi");
            mInfoList.add(info);
        }
        mViewPager2.setAdapter(new FragmentStateAdapter(getChildFragmentManager(), this.getLifecycle()) {
            @NonNull
            @Override
            public VideoModulePagerFragment createFragment(int position) {
                return VideoModulePagerFragment.newInstance(position, hVECategoryList.get(position), mSource);
            }

            @Override
            public int getItemCount() {
                return hVECategoryList.size();
            }
        });

        mViewPager2.setOffscreenPageLimit(hVECategoryList.size());

        if (mInfoList != null) {
            mTabTopLayout.inflateInfo(mInfoList);
            int cacheTabIndex = TemplateCacheData.getTemplateTabIndex();
            int size = mInfoList.size();
            if (cacheTabIndex >= size) {
                mTabTopLayout.defaultSelected(mInfoList.get(DEFAULT_SELECT_INDEX));
                mViewPager2.setCurrentItem(DEFAULT_SELECT_INDEX, false);
            } else {
                mTabTopLayout.defaultSelected(mInfoList.get(cacheTabIndex >= 0 ? cacheTabIndex : DEFAULT_SELECT_INDEX));
                if (cacheTabIndex >= 0) {
                    mViewPager2.setCurrentItem(cacheTabIndex, false);
                }
            }
        }
    }

    @Override
    protected void initEvent() {
        mTemplateHomeErrorTV.setOnClickListener(new OnClickRepeatedListener(v -> {
            mTemplateHomeErrorTV.setVisibility(View.GONE);
            mHVETemplateModel.initColumns();
        }));
        mTabTopLayout.addTabSelectedChangeListener((index, prevInfo, nextInfo) -> {
            if (mViewPager2.getCurrentItem() != index) {
                mViewPager2.setCurrentItem(index, false);
            }
            TemplateCacheData.setTemplateTabIndex(index);
            TemplateCacheData.setColumnName(mInfoList.get(index).mName);
        });

        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position != mTopTabSelectIndex) {
                    mTabTopLayout.defaultSelected(mInfoList.get(position));
                    mTopTabSelectIndex = position;
                }
                TemplateCacheData.setTemplateTabIndex(position);
                TemplateCacheData.setColumnName(mInfoList.get(position).mName);
            }
        });

        mTemplateHomeErrorLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            mLoadingLayout.setVisibility(View.VISIBLE);
            mTemplateHomeErrorLayout.setVisibility(View.GONE);
            mHVETemplateModel.initColumns();
        }));
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) {
            return;
        }
        List<Fragment> fragmentList = fragmentActivity.getSupportFragmentManager().getFragments();
        if (fragmentList.size() > 0) {
            for (Fragment mFragment : fragmentList) {
                mFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
