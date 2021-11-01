
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

package com.huawei.hms.videoeditor.ui.mediaeditor.effect.fragment;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialConstant;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTop;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopInfo;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopLayout;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.effect.viewmodel.EffectPanelViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.ColumnsRespository;
import com.huawei.secure.android.common.intent.SafeBundle;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class EffectPanelFragment extends BaseFragment {
    private static final String TAG = "EffectPanelFragment";

    private static final int EFFECT = 1;

    private static final int DYNAMIC_HEIGHT = 3;

    private static final int DEFAULT_SELECT_INDEX = 0;

    private EffectPanelViewModel mEffectPanelViewModel;

    private boolean isReplace = false;

    private ViewPager2 mViewPager2;

    private ImageView mConfirmIv;

    private TabTopLayout mTabTopLayout;

    private RelativeLayout mEffectErrorLayout;

    private TextView mEffectErrorView;

    private RelativeLayout mLoadingLayout;

    private LoadingIndicatorView mIndicatorView;

    private long mTime;

    private List<HVEColumnInfo> mColumnList = new ArrayList<>();

    private List<TabTopInfo<?>> mInfoList = new ArrayList<>();

    private int mTopTabSelectIndex = 0;

    private CloudMaterialBean mCutContent;

    private HVEEffect mLastEffect;

    private int defaultColor;

    private int color;

    private int leftRightPadding;

    private HandlerThread handlerThread = new HandlerThread("work");

    private WorkHandler workHandler;

    private static class WorkHandler extends Handler {
        private final WeakReference<EffectPanelFragment> weakReference;

        WorkHandler(EffectPanelFragment effectPanelFragment, Looper looper) {
            super(looper);
            weakReference = new WeakReference<>(effectPanelFragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (weakReference == null) {
                return;
            }
            final EffectPanelFragment effectPanelFragment = weakReference.get();
            if (effectPanelFragment == null) {
                return;
            }
            if (effectPanelFragment.viewModel == null) {
                return;
            }
            if (effectPanelFragment.mEffectPanelViewModel == null) {
                return;
            }
            CloudMaterialBean cutContent = (CloudMaterialBean) msg.obj;
            if (cutContent == null) {
                return;
            }

            effectPanelFragment.mCutContent = cutContent;

            if (effectPanelFragment.isReplace) {
                effectPanelFragment.mLastEffect = effectPanelFragment.viewModel.getSelectedEffect();
            }
            effectPanelFragment.mLastEffect = effectPanelFragment.mEffectPanelViewModel
                .replaceEffect(effectPanelFragment.mLastEffect, cutContent, effectPanelFragment.mTime);

            if (effectPanelFragment.mLastEffect == null) {
                return;
            }

            effectPanelFragment.viewModel.setSelectedUUID(effectPanelFragment.mLastEffect.getUuid());
            effectPanelFragment.viewModel.updateDuration();
            effectPanelFragment.viewModel.playTimeLine(effectPanelFragment.mLastEffect.getStartTime(),
                effectPanelFragment.mLastEffect.getEndTime());
        }
    }

    public static EffectPanelFragment newInstance(boolean isReplace) {
        Bundle args = new Bundle();
        args.putBoolean(Constant.TEXT_TEMPLATE_REPLACE, isReplace);
        EffectPanelFragment fragment = new EffectPanelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        setStatusBarColor(mActivity);
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
        mEffectErrorLayout = view.findViewById(R.id.error_layout);
        mEffectErrorView = view.findViewById(R.id.error_text);

        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mIndicatorView = view.findViewById(R.id.indicator);
        TextView tvTitle = view.findViewById(R.id.tv_title);

        tvTitle.setText(R.string.first_menu_special);

        mLoadingLayout.setVisibility(View.VISIBLE);
        mIndicatorView.show();
    }

    protected void initObject() {
        setTimeoutEnable();
        mEffectPanelViewModel = new ViewModelProvider(this, mFactory).get(EffectPanelViewModel.class);
        ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchSTListener);
        if (viewModel != null) {
            viewModel.setNeedAddTextOrSticker(true);
        }
        handlerThread.start();
        workHandler = new WorkHandler(this, handlerThread.getLooper());
    }

    @Override
    protected void initData() {
        SafeBundle safeBundle = new SafeBundle(getArguments());
        isReplace = safeBundle.getBoolean(Constant.TEXT_TEMPLATE_REPLACE);

        HVETimeLine mTimeLine = EditorManager.getInstance().getTimeLine();
        if (mTimeLine == null) {
            mTime = 0;
        } else {
            mTime = mTimeLine.getCurrentTime();
        }

        defaultColor = ContextCompat.getColor(mActivity, R.color.color_fff_86);
        color = ContextCompat.getColor(mActivity, R.color.tab_text_tint_color);
        leftRightPadding = SizeUtils.dp2Px(mActivity, 15);

        if (mEffectPanelViewModel != null) {
            mEffectPanelViewModel.initColumns(HVEMaterialConstant.EFFECT_FATHER_COLUMN);
        }
    }

    @Override
    protected void initEvent() {
        mEffectErrorLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mEffectPanelViewModel == null) {
                return;
            }
            mEffectErrorLayout.setVisibility(View.GONE);
            mLoadingLayout.setVisibility(View.VISIBLE);
            mEffectPanelViewModel.initColumns(HVEMaterialConstant.EFFECT_FATHER_COLUMN);
        }));

        mConfirmIv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (viewModel == null) {
                return;
            }
            if (mEffectPanelViewModel == null) {
                return;
            }
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
    }

    @Override
    protected void initViewModelObserve() {
        mEffectPanelViewModel.getColumns().observe(this, list -> {
            if (list != null && list.size() > 0) {
                mColumnList.clear();
                mColumnList.addAll(list);
                mInfoList.clear();
                mEffectErrorLayout.setVisibility(View.INVISIBLE);
                for (HVEColumnInfo item : list) {
                    TabTopInfo<?> info = new TabTopInfo<>(item.getColumnName(), true, defaultColor, color,
                        leftRightPadding, leftRightPadding);
                    mInfoList.add(info);
                    mLoadingLayout.setVisibility(View.GONE);
                    mIndicatorView.hide();
                }
                mViewPager2.setAdapter(
                    new FragmentStateAdapter(getChildFragmentManager(), EffectPanelFragment.this.getLifecycle()) {
                        @NonNull
                        @Override
                        public Fragment createFragment(int position) {
                            return EffectItemFragment.newInstance(list.get(position));
                        }

                        @Override
                        public int getItemCount() {
                            return list.size();
                        }
                    });

                mTabTopLayout.inflateInfo(mInfoList);

                TabTop tabList = mTabTopLayout.findTab(mInfoList.get(0));
                FrameLayout.LayoutParams newYearImage =
                    new FrameLayout.LayoutParams(SizeUtils.dp2Px(mActivity, 24), SizeUtils.dp2Px(mActivity, 24));
                newYearImage.setMargins(0, SizeUtils.dp2Px(mActivity, 10), 0, SizeUtils.dp2Px(mActivity, 10));
                FrameLayout.LayoutParams newYearText =
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, SizeUtils.dp2Px(mActivity, 20));
                RelativeLayout.LayoutParams newYearLine =
                    new RelativeLayout.LayoutParams(SizeUtils.dp2Px(mActivity, 28), SizeUtils.dp2Px(mActivity, 2));

                if (tabList != null && tabList.getIvTabIcon() != null) {
                    newYearImage.setMargins(0, SizeUtils.dp2Px(mActivity, 14), 0, SizeUtils.dp2Px(mActivity, 10));
                    tabList.getIvTabIcon().setImageResource(R.drawable.icon_cancel_wu);
                    tabList.getIvTabIcon().setAlpha(0.9f);
                    tabList.getIvTabIcon().setContentDescription(mActivity.getResources().getString(R.string.no));
                    tabList.getIvTabIcon().setVisibility(View.VISIBLE);
                    newYearText.setMargins(SizeUtils.dp2Px(mActivity, 48), SizeUtils.dp2Px(mActivity, 14), 0,
                        SizeUtils.dp2Px(mActivity, 14));
                    newYearLine.setMargins(SizeUtils.dp2Px(mActivity, 56), 0, 0, SizeUtils.dp2Px(mActivity, 5));
                    tabList.getIvTabIcon().setOnClickListener(new OnClickRepeatedListener(view -> {
                        if (viewModel == null) {
                            return;
                        }
                        if (mEffectPanelViewModel == null) {
                            return;
                        }
                        boolean isDelete = mEffectPanelViewModel.deleteEffect(viewModel.getSelectedEffect());
                        if (!isDelete) {
                            return;
                        }
                        mEffectPanelViewModel.getCancelSelected().postValue(true);
                        mLastEffect = null;
                        mCutContent = null;
                    }));
                }

                if (tabList != null && tabList.getIvTabIcon() != null) {
                    tabList.getIvTabIcon().setLayoutParams(newYearImage);
                    tabList.getTabNameView().setLayoutParams(newYearText);
                    newYearLine.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    tabList.getIndicatorView().setLayoutParams(newYearLine);
                }

                mViewPager2.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
                mTabTopLayout.defaultSelected(mInfoList.get(DEFAULT_SELECT_INDEX));
            }
        });

        mEffectPanelViewModel.getErrorType().observe(this, errorType -> {
            if (errorType == ColumnsRespository.RESULT_ILLEGAL) {
                if (mInfoList == null || mInfoList.isEmpty()) {
                    mEffectErrorView.setText(getString(R.string.result_illegal));
                    mEffectErrorLayout.setVisibility(View.VISIBLE);
                    mLoadingLayout.setVisibility(View.GONE);
                }
            } else if (errorType == ColumnsRespository.RESULT_EMPTY) {
                SmartLog.i(TAG, "No data.");
                mLoadingLayout.setVisibility(View.GONE);
                mIndicatorView.hide();
            }
        });

        viewModel.getTimeout().observe(getViewLifecycleOwner(), isTimeout -> {
            if (isTimeout && !isBackground) {
                SmartLog.i(TAG, "timeout, close this page");
                mActivity.onBackPressed();
            }
        });

        mEffectPanelViewModel.getSelectEffect().observe(getViewLifecycleOwner(), selectEffect -> {
            if (selectEffect == null) {
                return;
            }
            viewModel.setSelectedUUID(selectEffect.getUuid());
        });

        mEffectPanelViewModel.getRemoveData().observe(getViewLifecycleOwner(), removeData -> {
            if (!removeData) {
                return;
            }
            if (mEffectPanelViewModel == null) {
                return;
            }
            if (viewModel == null) {
                return;
            }

            boolean isDelete = mEffectPanelViewModel.deleteEffect(viewModel.getSelectedEffect());
            if (!isDelete) {
                return;
            }
            mLastEffect = null;
            mCutContent = null;
        });

        mEffectPanelViewModel.getSelectData().observe(getViewLifecycleOwner(), cutContent -> {
            if (cutContent == null) {
                return;
            }
            workHandler.removeMessages(EFFECT);
            Message message = workHandler.obtainMessage();
            message.obj = cutContent;
            message.what = EFFECT;
            workHandler.sendMessage(message);
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    @Override
    public void onBackPressed() {
        if (mActivity != null) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchSTListener);
        }

        if (viewModel == null) {
            return;
        }
        viewModel.destroyTimeoutManager();
        viewModel.pause();

        if (mLastEffect == null || mCutContent == null) {
            viewModel.setSelectedUUID("");
            return;
        }
        if (StringUtil.isEmpty(mCutContent.getLocalPath()) || StringUtil.isEmpty(mCutContent.getId())) {
            viewModel.setSelectedUUID("");
            return;
        }

        viewModel.setSelectedUUID(mLastEffect.getUuid());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDynamicViewLayoutChange();
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchSTListener = ev -> {
        initTimeoutState();
        return false;
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchSTListener);
        handlerThread.quit();
        workHandler.removeCallbacksAndMessages(null);
        handlerThread = null;
        workHandler = null;
        if (viewModel == null) {
            return;
        }
        viewModel.destroyTimeoutManager();
    }
}
