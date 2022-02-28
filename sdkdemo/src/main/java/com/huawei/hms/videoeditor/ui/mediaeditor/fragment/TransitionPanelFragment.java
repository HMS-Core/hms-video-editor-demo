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

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialConstant;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.adapter.TransitionItemAdapter;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.LanguageUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.utils.Utils;
import com.huawei.hms.videoeditor.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopInfo;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopLayout;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.animation.transitionpanel.TransitionPanelViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.TransitionSeekBar;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

public class TransitionPanelFragment extends BaseFragment {
    private static final String TAG = "TransitionPanelFragment";

    private ImageView mCertainTv;

    private TabTopLayout mTabTopLayout;

    private RecyclerView mRecyclerView;

    private TransitionSeekBar mSeekBar;

    private TextView mApplyToAllTv;

    protected EditPreviewViewModel mEditPreviewViewModel;

    protected MenuViewModel mMenuViewModel;

    private TransitionPanelViewModel transitionPanelViewModel;

    private TransitionItemAdapter transitionItemAdapter;

    private int mProgress = 50;

    private EditorTextView transTime;

    private int mLastPosition = 0;

    private long maxTransTime;

    private int currentTransTime = 500;

    private List<HVEColumnInfo> columnList = new ArrayList<>();

    private List<CloudMaterialBean> animList = new ArrayList<>();

    private List<CloudMaterialBean> initAnim = new ArrayList<>(1);

    private int mCurrentIndex = 0;

    private int mCurrentPage = 0;

    private Boolean mHasNextPage = false;

    private int mSelectPosition = 0;

    private RelativeLayout mErrorLayout;

    private TextView mErrorTv;

    private LinearLayout mLineTransition;

    private LoadingIndicatorView mLoadingIndicatorView;

    public CloudMaterialBean mLastContent;

    private TextView tv_title;

    private boolean hasAddPosition;

    private boolean isFirst;

    private HVEColumnInfo mContent;

    private CloudMaterialBean mMaterialsCutContent;

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_panel_transition;
    }

    @Override
    public void initView(View view) {
        mTabTopLayout = view.findViewById(R.id.tab_top_layout);
        mRecyclerView = view.findViewById(R.id.rl_pic);
        mCertainTv = view.findViewById(R.id.iv_certain);
        tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.trans_title);
        mSeekBar = view.findViewById(R.id.sb_items);
        transTime = view.findViewById(R.id.duration_transition);
        mErrorLayout = view.findViewById(R.id.error_layout);
        mErrorTv = view.findViewById(R.id.error_text);
        mLoadingIndicatorView = view.findViewById(R.id.indicator);
        mApplyToAllTv = view.findViewById(R.id.apply_to_all);
        mLineTransition = view.findViewById(R.id.line_transition);
        if (ScreenUtil.isRTL()) {
            mLineTransition.setScaleX(RTL_UI);
            transTime.setScaleX(RTL_UI);
            mTabTopLayout.setScaleX(RTL_UI);
        } else {
            mLineTransition.setScaleX(LTR_UI);
            transTime.setScaleX(LTR_UI);
            mTabTopLayout.setScaleX(LTR_UI);
        }
        LinearLayout.LayoutParams transParams;
        if (LanguageUtils.isZh()) {
            transParams = new LinearLayout.LayoutParams(SizeUtils.dp2Px(context, 48.0f),
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
        } else {
            transParams = new LinearLayout.LayoutParams(SizeUtils.dp2Px(context, 64.0f),
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
        }
        transParams.setMargins(SizeUtils.dp2Px(context, 16.0f), 0, 0, SizeUtils.dp2Px(context, 3.0f));
        transParams.gravity = Gravity.BOTTOM;
        transTime.setLayoutParams(transParams);
    }

    public List<CloudMaterialBean> loadLocalData() {
        CloudMaterialBean transitionNothing = new CloudMaterialBean();
        transitionNothing.setName(
                this.getResources().getString(R.string.none));
        transitionNothing.setLocalDrawableId(R.drawable.icon_no);
        transitionNothing.setId("-1");
        List<CloudMaterialBean> list = new ArrayList<>();
        list.add(transitionNothing);
        return list;
    }

    @Override
    protected void initObject() {
        setTimeoutEnable();
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mMenuViewModel = new ViewModelProvider(mActivity, mFactory).get(MenuViewModel.class);
        transitionPanelViewModel = new ViewModelProvider(this, mFactory).get(TransitionPanelViewModel.class);
        maxTransTime = mEditPreviewViewModel.getTransMinDuration();
        mSeekBar.setMinProgress(1);
        mSeekBar.setMaxProgress(100);
        mSeekBar.setAnchorProgress(0);
        mSeekBar.setMaxTransTime(maxTransTime);
        mProgress = (int) (BigDecimalUtils.div(500f, (float) maxTransTime) * 100);
        mSeekBar.setSeekBarProgress(mProgress);
        if (maxTransTime < 500) {
            mProgress = (int) (maxTransTime / 5);
            currentTransTime = 100;
        }
        animList.addAll(loadLocalData());
        transitionItemAdapter = new TransitionItemAdapter(mActivity, animList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        if (mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView
                    .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.color_20),
                            SizeUtils.dp2Px(mActivity, 64), SizeUtils.dp2Px(mActivity, 6)));
        }
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(transitionItemAdapter);
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchListener);
        }
    }

    @Override
    protected void initData() {
        initAnim.addAll(loadLocalData());
        int defaultColor = ContextCompat.getColor(mActivity, R.color.color_fff_86);
        int color = ContextCompat.getColor(mActivity, R.color.tab_text_tint_color);
        int leftRightPadding = SizeUtils.dp2Px(mActivity, 15);
        mCurrentIndex = 0;
        mCurrentPage = 0;
        transitionPanelViewModel.initColumns(HVEMaterialConstant.TRANSITION_FATHER_COLUMN);
        transitionPanelViewModel.getColumns().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.size() <= 0) {
                return;
            }
            columnList.clear();
            columnList.addAll(list);
            List<TabTopInfo<?>> mInfoList = new ArrayList<>();
            for (HVEColumnInfo item : list) {
                mInfoList.add(new TabTopInfo<>(item.getColumnName(), true, defaultColor, color, leftRightPadding,
                        leftRightPadding));
            }
            mTabTopLayout.inflateInfo(mInfoList);
            mTabTopLayout.defaultSelected(mInfoList.get(mCurrentIndex));
        });

        mTabTopLayout.addTabSelectedChangeListener((index, prevInfo, nextInfo) -> {
            mCurrentIndex = index;
            mCurrentPage = 0;
            animList.clear();
            isFirst = false;
            animList.addAll(initAnim);
            transitionItemAdapter.setData(animList);
            if (columnList == null) {
                return;
            }
            mContent = columnList.get(mCurrentIndex);
            transitionPanelViewModel.loadMaterials(mContent, mCurrentPage);
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isSlidingToLeft = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (mHasNextPage && manager != null && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int itemCount = manager.getItemCount();
                    if (lastItemPosition == (itemCount - 1) && isSlidingToLeft) {
                        mCurrentPage++;
                        transitionPanelViewModel.loadMaterials(columnList.get(mCurrentIndex), mCurrentPage);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLeft = dx > 0;
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager != null) {
                    int visibleItemCount = manager.getChildCount();
                    int firstPosition = manager.findFirstVisibleItemPosition();
                    if (firstPosition != -1 && visibleItemCount > 0 && !isFirst && animList.size() > 1) {
                        isFirst = true;
                        SmartLog.w(TAG, "HianalyticsEvent10007 postEvent");
                    }
                }
            }
        });

        transitionPanelViewModel.getBoundaryPageData().observe(this, aBoolean -> {
            mLoadingIndicatorView.hide();
            mHasNextPage = aBoolean;
        });
    }

    @SuppressLint("StringFormatMatches")
    @Override
    protected void initEvent() {
        mEditPreviewViewModel.getTimeout()
                .observe(this, isTimeout -> {
                    if (isTimeout && !isBackground) {
                        mActivity.onBackPressed();
                    }
                });
        transitionPanelViewModel.getPageData().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.size() <= 0) {
                mSelectPosition = 0;
                transitionItemAdapter.setSelectPosition(0);
                return;
            }

            if (mCurrentPage == 0) {
                mSelectPosition = 0;
                transitionItemAdapter.setSelectPosition(0);
            }

            mLoadingIndicatorView.hide();

            if (!animList.containsAll(list)) {
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                animList.addAll(list);
                transitionItemAdapter.setData(animList);
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
            }
            mSelectPosition = 0;
            transitionItemAdapter.setSelectPosition(mSelectPosition);

            HVEEffect effect = mEditPreviewViewModel.getEffectedTransition();
            if (effect != null) {
                String name = effect.getOptions().getEffectName();
                if (TextUtils.isEmpty(name)) {
                    transitionItemAdapter.setSelectPosition(0);
                } else {
                    for (int i = 0; i < animList.size(); i++) {
                        String contentName = animList.get(i).getName();
                        if (name.equals(contentName)) {
                            transitionItemAdapter.setSelectPosition(i);
                            mSeekBar.setVisibility(View.VISIBLE);
                            transTime.setVisibility(View.VISIBLE);
                        }
                    }

                    mLastPosition = transitionItemAdapter.getSelectPosition();
                    float duration = effect.getEndTime() - effect.getStartTime();
                    int process = (int) ((duration / maxTransTime) * 100);
                    calculateTransTime(process);
                    mSeekBar.setSeekBarProgress(process);
                }
            }
        });

        mSeekBar.setSeekBarProgress(mProgress);
        mSeekBar.invalidate();

        mSeekBar.setOnProgressChangedListener(progress -> {
            calculateTransTime(progress);
            float textTimer = BigDecimal.valueOf(BigDecimalUtils.div(currentTransTime, 1000f, 1)).floatValue();
            mEditPreviewViewModel.setToastTime(mActivity.getResources()
                    .getQuantityString(R.plurals.seconds_time, Double.valueOf(textTimer).intValue(),
                            NumberFormat.getInstance().format(textTimer)));
            HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
            editor.pauseTimeLine();
        });

        mSeekBar.setListener1((TransitionSeekBar.TouchListener) isTouch -> {
            if (isTouch) {
                int transitionTime = (int) Math.max(100, (float) mSeekBar.getProgress() / 100 * maxTransTime);
                float textTimer = BigDecimal.valueOf(BigDecimalUtils.div(transitionTime, 1000f, 1)).floatValue();
                String v = mActivity.getResources()
                        .getQuantityString(R.plurals.seconds_time, Double.valueOf(textTimer).intValue(),
                                NumberFormat.getInstance().format(textTimer));
                mEditPreviewViewModel.setToastTime(v);
            } else {
                mEditPreviewViewModel.setToastTime("");
            }
        });

        mSeekBar.setmSeekBarListener(() -> {
            addByPosition(null, true);
        });

        transitionItemAdapter.setOnItemClickListener(new TransitionItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, boolean itemClick) {
                if (!itemClick) {
                    return;
                }
                if (position == 0) {
                    mSeekBar.setVisibility(View.INVISIBLE);
                    transTime.setVisibility(View.INVISIBLE);
                } else {
                    mSeekBar.setVisibility(View.VISIBLE);
                    transTime.setVisibility(View.VISIBLE);
                }
                if (mLastPosition == position) {
                    return;
                }
                mLastPosition = position;
                SmartLog.i(TAG, "initEvent:" + position);
                mSelectPosition = transitionItemAdapter.getSelectPosition();
                if (mSelectPosition != position) {
                    transitionItemAdapter.setSelectPosition(position);
                    if (mSelectPosition != -1) {
                        transitionItemAdapter.notifyItemChanged(mSelectPosition);
                    }
                    transitionItemAdapter.notifyItemChanged(position);
                }

                if (animList == null || animList.size() <= 1) {
                    return;
                }
                mMaterialsCutContent = animList.get(mLastPosition);
                addByPosition(mMaterialsCutContent, true);
            }

            @Override
            public void onDownloadClick(int position) {
                transitionItemAdapter.setItemClick(false);
                if (animList == null || animList.size() <= 1) {
                    return;
                }
                mLastPosition = position;
                int previousPosition = transitionItemAdapter.getSelectPosition();
                transitionItemAdapter.setSelectPosition(position);
                CloudMaterialBean content = animList.get(position);
                if (content != null) {
                    transitionItemAdapter.addDownloadMaterial(content);
                    transitionPanelViewModel.downloadColumn(previousPosition, position, content);
                } else {
                    transitionItemAdapter.setItemClick(true);
                }
            }
        });

        mCertainTv.setOnClickListener(new OnClickRepeatedListener(view -> {
            mActivity.onBackPressed();
            if (hasAddPosition && animList != null && animList.size() > 0) {
                addByPosition(animList.get(mLastPosition), false);
                mEditPreviewViewModel.transitionReloadUI();
                mEditPreviewViewModel.pause();
            }
        }));

        transitionPanelViewModel.getDownloadSuccess().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "getDownloadSuccess");

            if (downloadInfo == null) {
                transitionItemAdapter.setItemClick(true);
                return;
            }

            transitionItemAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            int downloadPosition = downloadInfo.getDataPosition();
            transitionItemAdapter.setSelectPosition(downloadPosition);
            mSelectPosition = downloadPosition;
            if (downloadPosition <= 0) {
                return;
            }
            transitionItemAdapter.notifyDataSetChanged();

            if (mSelectPosition == 0) {
                mSeekBar.setVisibility(View.INVISIBLE);
                transTime.setVisibility(View.INVISIBLE);
            } else {
                mSeekBar.setVisibility(View.VISIBLE);
                transTime.setVisibility(View.VISIBLE);
            }

            transitionItemAdapter.setItemClick(true);
            if (downloadPosition == transitionItemAdapter.getSelectPosition()) {
                CloudMaterialBean materialsCutContent = downloadInfo.getMaterialBean();
                addByPosition(materialsCutContent, true);
            }
        });

        transitionPanelViewModel.getDownloadProgress().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "progress:" + downloadInfo.getProgress());
            updateProgress(downloadInfo);
        });

        transitionPanelViewModel.getDownloadFail().observe(this, downloadInfo -> {
            transitionItemAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            transitionItemAdapter.setItemClick(true);
            ToastWrapper.makeText(mActivity,
                    downloadInfo.getMaterialBean().getName() + Utils.setNumColor(
                            String.format(Locale.ROOT, mActivity.getResources().getString(R.string.download_failed), 0),
                            mActivity.getResources().getColor(R.color.transparent)),
                    Toast.LENGTH_SHORT).show();
        });

        transitionPanelViewModel.getErrorString().observe(this, errorString -> {
            if (!TextUtils.isEmpty(errorString) && animList.size() == 0) {
                mErrorTv.setText(errorString);
                mLoadingIndicatorView.hide();
                mErrorLayout.setVisibility(View.VISIBLE);
            }
        });

        mErrorLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mCurrentPage == 0) {
                mErrorLayout.setVisibility(View.GONE);
                mLoadingIndicatorView.show();
            }
            transitionPanelViewModel.initColumns(HVEMaterialConstant.TRANSITION_FATHER_COLUMN);
        }));

        mApplyToAllTv.setOnClickListener(new OnClickRepeatedListener(v -> {
            boolean isSuccess = false;
            CloudMaterialBean beforeContent = getBeforeContent();
            if (mLastPosition == 0) {
                isSuccess = mMenuViewModel.removeAllTransition();
            } else if (beforeContent != null) {
                if (!TextUtils.isEmpty(transTime.getText())) {
                    beforeContent.setDuration(currentTransTime);
                }
                isSuccess = mMenuViewModel.applyTransitionToAll(beforeContent);
            }
            if (isSuccess) {
                ToastWrapper.makeText(context, context.getText(R.string.applied_to_all), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    private void updateProgress(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        if (downloadPosition < 0) {
            return;
        }
        if (downloadInfo.getDataPosition() < animList.size()
                && downloadInfo.getContentId().equals(animList.get(downloadInfo.getDataPosition()).getId())) {
            TransitionItemAdapter.ViewHolder viewHolder = (TransitionItemAdapter.ViewHolder) mRecyclerView
                    .findViewHolderForAdapterPosition(downloadInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mHwProgressBar = viewHolder.itemView.findViewById(R.id.item_progress);
                mHwProgressBar.setProgress(downloadInfo.getProgress());
            }
        }
    }

    public void addByPosition(CloudMaterialBean content, boolean preview) {
        hasAddPosition = true;
        if (mLastPosition == 0) {
            deleteTransitionEffect();
            mLastContent = null;
        } else {
            if (content != null) {
                mLastContent = content;
            }
        }
        if (mLastContent != null) {
            mMenuViewModel.addTransitionEffect(mLastContent, currentTransTime, preview);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        }
        if (mEditPreviewViewModel != null) {
            mEditPreviewViewModel.destroyTimeoutManager();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        }
        if (mEditPreviewViewModel != null) {
            mEditPreviewViewModel.destroyTimeoutManager();
        }
    }

    private void calculateTransTime(int process) {
        currentTransTime = (int) Math.max(100, (float) process / 100 * maxTransTime);
    }

    private void deleteTransitionEffect() {
        if (mEditPreviewViewModel.getTransLengthByIndex() == -1) {
            return;
        }
        HVEVideoLane videoLane = mEditPreviewViewModel.getVideoLane();
        for (int i = 0; i < videoLane.getTransitionEffects().size(); i++) {
            if (videoLane.getTransitionEffects().get(i).getIntVal("from") == mEditPreviewViewModel
                    .getTransLengthByIndex()
                    || videoLane.getTransitionEffects()
                    .get(i)
                    .getIntVal("to") == mEditPreviewViewModel.getTransLengthByIndex() + 1) {
                videoLane.removeTransitionEffect(i);
            }
        }
        mEditPreviewViewModel.updateDuration();
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchListener = new VideoClipsActivity.TimeOutOnTouchListener() {
        @Override
        public boolean onTouch(MotionEvent ev) {
            initTimeoutState();
            return false;
        }
    };

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDynamicViewLayoutChange();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirst = false;
    }

    public CloudMaterialBean getBeforeContent() {
        HVEEffect effect = mEditPreviewViewModel.getEffectedTransition();
        if (effect != null) {
            String name = effect.getOptions().getEffectName();
            if (TextUtils.isEmpty(name)) {
                transitionItemAdapter.setSelectPosition(0);
            } else {
                for (int i = 0; i < animList.size(); i++) {
                    String contentName = animList.get(i).getName();
                    if (name.equals(contentName)) {
                        return animList.get(i);
                    }
                }
            }
        }
        return null;
    }
}
