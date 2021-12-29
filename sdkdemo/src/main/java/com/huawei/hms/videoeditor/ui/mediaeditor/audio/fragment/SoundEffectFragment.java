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

package com.huawei.hms.videoeditor.ui.mediaeditor.audio.fragment;

import static com.huawei.hms.videoeditor.sdk.asset.HVEAudioAsset.AUDIO_TYPE_SOUND_EFFECT;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.sdk.materials.network.response.MaterialsCloudBean;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopInfo;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopLayout;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.audio.viewmodel.SoundEffectViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class SoundEffectFragment extends BaseFragment {

    private static final String TAG = "SoundEffectFragment";

    private static final int DEFAULT_SELECT_INDEX = 0;

    private ImageView mConfirmIv;

    private TabTopLayout mTabTopLayout;

    private ViewPager2 mViewPager2;

    private List<HVEColumnInfo> mColumnList;

    private List<TabTopInfo<?>> mInfoList;

    private SoundEffectViewModel mSoundEffectViewModel;

    private int mTopTabSelectIndex = 0;

    private EditPreviewViewModel mEditPreviewViewModel;

    private TextView tvTitle;

    private TextView mErrorTv;

    private RelativeLayout mErrorLy;

    private RelativeLayout layoutLoading;

    public static SoundEffectFragment newInstance() {
        return new SoundEffectFragment();
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_add_sticker;
    }

    protected void initView(View view) {
        mConfirmIv = view.findViewById(R.id.iv_certain);
        mTabTopLayout = view.findViewById(R.id.tab_top_layout);
        if (ScreenUtil.isRTL()) {
            mTabTopLayout.setScaleX(RTL_UI);
        } else {
            mTabTopLayout.setScaleX(LTR_UI);
        }
        mViewPager2 = view.findViewById(R.id.viewpager);
        tvTitle = view.findViewById(R.id.tv_title);
        mErrorLy = view.findViewById(R.id.error_layout);
        mErrorTv = view.findViewById(R.id.error_text);
        layoutLoading = view.findViewById(R.id.loading_layout);
    }

    protected void initObject() {
        setTimeoutEnable();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchListener);
        }
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mSoundEffectViewModel = new ViewModelProvider(this, mFactory).get(SoundEffectViewModel.class);
        mColumnList = new ArrayList<>();
        mInfoList = new ArrayList<>();
    }

    @Override
    protected void initData() {
        tvTitle.setText(R.string.audio_second_menu_sound_effect);
        tvTitle.setTextSize(16);
        int defaultColor = ContextCompat.getColor(mActivity, R.color.color_fff_86);
        int color = ContextCompat.getColor(mActivity, R.color.tab_text_tint_color);
        int leftRightPadding = SizeUtils.dp2Px(mActivity, 8);
        layoutLoading.setVisibility(View.VISIBLE);

        mSoundEffectViewModel.initColumns();
        mSoundEffectViewModel.getColumns().observe(getViewLifecycleOwner(), list -> {
            layoutLoading.setVisibility(View.GONE);
            if (list != null && list.size() > 0) {
                mColumnList.clear();
                mColumnList.addAll(list);
                for (HVEColumnInfo item : list) {
                    SmartLog.i(TAG, item.toString());
                    TabTopInfo<?> info = new TabTopInfo<>(item.getColumnName(), false, defaultColor, color, 14, 14,
                        leftRightPadding, leftRightPadding);
                    mInfoList.add(info);
                }
                mViewPager2.setAdapter(
                    new FragmentStateAdapter(getChildFragmentManager(), SoundEffectFragment.this.getLifecycle()) {
                        @NonNull
                        @Override
                        public Fragment createFragment(int position) {
                            return SoundEffectItemFragment.newInstance(list.get(position));
                        }

                        @Override
                        public int getItemCount() {
                            return list.size();
                        }
                    });
                mTabTopLayout.inflateInfo(mInfoList);
                mTabTopLayout.defaultSelected(mInfoList.get(DEFAULT_SELECT_INDEX));
            }
        });
    }

    @Override
    protected void initEvent() {
        mEditPreviewViewModel.getTimeout()
            .observe(this, isTimeout -> {
                if (isTimeout && !isBackground) {
                    mActivity.onBackPressed();
                }
            });
        mErrorLy.setOnClickListener(new OnClickRepeatedListener(v -> {
            mErrorLy.setVisibility(View.GONE);
            layoutLoading.setVisibility(View.VISIBLE);
            mSoundEffectViewModel.initColumns();
        }));

        mConfirmIv.setOnClickListener(new OnClickRepeatedListener(v -> {
            mActivity.onBackPressed();
        }));

        mTabTopLayout.addTabSelectedChangeListener((index, prevInfo, nextInfo) -> {
            if (mViewPager2.getCurrentItem() != index) {
                mViewPager2.setCurrentItem(index, false);
            }
        });

        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position != mTopTabSelectIndex) {
                    mTabTopLayout.defaultSelected(mInfoList.get(position));
                    mTopTabSelectIndex = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        mSoundEffectViewModel.getSelectData().observe(getViewLifecycleOwner(), cutContent -> {
            mActivity.onBackPressed();
            new Handler(Looper.getMainLooper()).postDelayed(() -> addSoundEffect(cutContent), 100);
        });

        mSoundEffectViewModel.getErrorString().observe(getViewLifecycleOwner(), errorString -> {
            mErrorLy.setVisibility(View.VISIBLE);
            layoutLoading.setVisibility(View.GONE);
            mErrorTv.setText(errorString);
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    private void addSoundEffect(MaterialsCloudBean cutContent) {
        if (cutContent != null) {
            mEditPreviewViewModel.addAudio(cutContent, AUDIO_TYPE_SOUND_EFFECT);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        }
        mEditPreviewViewModel.destroyTimeoutManager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        }
        mEditPreviewViewModel.destroyTimeoutManager();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDynamicViewLayoutChange();
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchListener = new VideoClipsActivity.TimeOutOnTouchListener() {
        @Override
        public boolean onTouch(MotionEvent ev) {
            initTimeoutState();
            return false;
        }
    };
}
