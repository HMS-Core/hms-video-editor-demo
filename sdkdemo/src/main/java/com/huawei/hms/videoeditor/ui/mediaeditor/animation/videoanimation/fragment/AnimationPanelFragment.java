
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

package com.huawei.hms.videoeditor.ui.mediaeditor.animation.videoanimation.fragment;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_OPERATION_ANIMATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_STATE_ANIMATION;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialConstant;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.LanguageUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.AnimationBar;
import com.huawei.hms.videoeditor.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopInfo;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopLayout;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.animation.videoanimation.adapter.AnimationItemAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.animation.videoanimation.viewmodel.AnimationViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.MaterialsRespository;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

import java.util.ArrayList;
import java.util.List;

public class AnimationPanelFragment extends BaseFragment implements AnimationBar.OnProgressChangedListener {
    private static final String TAG = "AnimationPanelFragment";

    private static final String VIDEO_MAIN = "video_main";

    private List<HVEColumnInfo> animationColumnList = new ArrayList<>();

    private List<CloudMaterialBean> initAnim = new ArrayList<>(1);

    private List<CloudMaterialBean> animList = new ArrayList<>();

    private HVEAsset hveAsset;

    private TabTopLayout tabTopLayout;

    private RelativeLayout errorLayout;

    private TextView errorTv;

    private LoadingIndicatorView loadingIndicatorView;

    private ImageView certain;

    private RecyclerView recyclerView;

    private LinearLayout seek_container;

    private AnimationBar animationBar;

    private AnimationItemAdapter animationItemAdapter;

    private AnimationViewModel animationViewModel;

    private boolean isFirst;

    private Boolean mHasNextPage = false;

    private HVEColumnInfo content;

    private int currentIndex = 0;

    private int currentPage = 0;

    private String animType = HVEEffect.ENTER_ANIMATION;

    public static AnimationPanelFragment newInstance(int id) {
        Bundle args = new Bundle();
        boolean mainVideo;
        mainVideo = id == EDIT_VIDEO_STATE_ANIMATION || id == EDIT_VIDEO_OPERATION_ANIMATION;
        args.putBoolean(VIDEO_MAIN, mainVideo);
        AnimationPanelFragment fragment = new AnimationPanelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_panel_animation;
    }

    @Override
    protected void initView(View view) {
        tabTopLayout = view.findViewById(R.id.tab_top_layout);
        errorLayout = view.findViewById(R.id.error_layout);
        errorTv = view.findViewById(R.id.error_text);
        loadingIndicatorView = view.findViewById(R.id.indicator);
        certain = view.findViewById(R.id.iv_certain);
        recyclerView = view.findViewById(R.id.rl_pic);
        seek_container = view.findViewById(R.id.seek_container);
        animationBar = view.findViewById(R.id.sb_items);

        EditorTextView animationText = view.findViewById(R.id.animtext);
        TextView title = view.findViewById(R.id.tv_title);

        title.setText(getString(R.string.cut_second_menu_animation));
        loadingIndicatorView.show();

        animationItemAdapter = new AnimationItemAdapter(mActivity, animList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        if (recyclerView.getItemDecorationCount() == 0) {
            recyclerView
                    .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.color_20),
                            SizeUtils.dp2Px(mActivity, 75), SizeUtils.dp2Px(mActivity, 8)));
        }
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(animationItemAdapter);

        if (ScreenUtil.isRTL()) {
            tabTopLayout.setScaleX(RTL_UI);
            seek_container.setScaleX(RTL_UI);
            animationText.setScaleX(RTL_UI);
        } else {
            tabTopLayout.setScaleX(LTR_UI);
            seek_container.setScaleX(LTR_UI);
            animationText.setScaleX(LTR_UI);
        }

        LinearLayout.LayoutParams transParams;
        if (LanguageUtils.isZh()) {
            transParams = new LinearLayout.LayoutParams(SizeUtils.dp2Px(context, 36.0f),
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);

        } else {
            transParams = new LinearLayout.LayoutParams(SizeUtils.dp2Px(context, 64.0f),
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
        }
        transParams.gravity = Gravity.BOTTOM;
        animationText.setLayoutParams(transParams);
    }

    @Override
    protected void initObject() {
        setTimeoutEnable();
        ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchStAnListener);
        animationViewModel = new ViewModelProvider(this, mFactory).get(AnimationViewModel.class);
    }

    @Override
    protected void initData() {
        SafeBundle safeBundle = new SafeBundle(getArguments());
        boolean isMainVideo = safeBundle.getBoolean(VIDEO_MAIN, true);
        if (isMainVideo) {
            hveAsset = viewModel.getSelectedAsset();
            if (hveAsset == null) {
                hveAsset = viewModel.getMainLaneAsset();
            }
        } else {
            hveAsset = viewModel.getSelectedAsset();
        }

        if (hveAsset != null) {
            animationBar.setDuration(hveAsset.getDuration());
        }
        if (getActivity() != null) {
            initAnim.addAll(animationViewModel.loadLocalData(getActivity().getResources().getString(R.string.none)));
        }
        animationViewModel.initColumns(HVEMaterialConstant.VIDEO_ANIMATION_FATHER_COLUMN);
    }

    @Override
    protected void initEvent() {
        animationBar.setOnProgressChangedListener(this);
        certain.setOnClickListener(view -> mActivity.onBackPressed());

        tabTopLayout.addTabSelectedChangeListener((index, prevInfo, nextInfo) -> {
            if (animationColumnList == null) {
                return;
            }
            errorLayout.setVisibility(View.GONE);
            currentIndex = index;
            if (index == 0) {
                animType = HVEEffect.ENTER_ANIMATION;
                animationBar.setEnterAnimation(true);
            } else if (index == 1) {
                animType = HVEEffect.LEAVE_ANIMATION;
                animationBar.setEnterAnimation(false);
            } else if (index == 2) {
                animType = HVEEffect.CYCLE_ANIMATION;
                animationBar.setEnterAnimation(true);
            }
            currentPage = 0;
            isFirst = false;
            animList.clear();
            animList.addAll(initAnim);
            animationItemAdapter.setData(animList);

            content = animationColumnList.get(currentIndex);
            if (content == null) {
                return;
            }
            animationViewModel.loadMaterials(content, currentPage);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isSlidingToLeft = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager layoutStAnManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (mHasNextPage && layoutStAnManager != null && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastItemStAnPosition = layoutStAnManager.findLastCompletelyVisibleItemPosition();
                    int itemCount = layoutStAnManager.getItemCount();
                    if (content == null) {
                        return;
                    }
                    if (lastItemStAnPosition != (itemCount - 1) || !isSlidingToLeft) {
                        return;
                    }

                    currentPage++;
                    animationViewModel.loadMaterials(content, currentPage);

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLeft = dx > 0;
                LinearLayoutManager layoutStAnManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutStAnManager != null) {
                    int visibleItemCount = layoutStAnManager.getChildCount();
                    int firstPosition = layoutStAnManager.findFirstVisibleItemPosition();
                    if (firstPosition != -1 && visibleItemCount > 0 && !isFirst && animList.size() > 1) {
                        isFirst = true;
                    }
                }
            }
        });

        errorLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (currentPage == 0) {
                errorLayout.setVisibility(View.GONE);
                loadingIndicatorView.show();
            }
            animationViewModel.initColumns(HVEMaterialConstant.VIDEO_ANIMATION_FATHER_COLUMN);
        }));

        animationBar.setcTouchListener(isTouch -> viewModel.setToastTime(isTouch ? animationBar.getProgress() : ""));

        animationItemAdapter.setOnItemClickListener(new AnimationItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (animList == null || animList.isEmpty()) {
                    return;
                }

                int previousPosition = animationItemAdapter.getSelectPosition();
                if (previousPosition == position) {
                    return;
                }
                animationItemAdapter.setSelectPosition(position);
                if (previousPosition != -1) {
                    animationItemAdapter.notifyItemChanged(previousPosition);
                }
                animationItemAdapter.notifyItemChanged(position);
                animationViewModel.setSelectCutContent(animList.get(position));
            }

            @Override
            public void onDownloadClick(int position) {
                if (animList == null || animList.isEmpty()) {
                    return;
                }
                CloudMaterialBean content1 = animList.get(position);
                if (content1 == null) {
                    return;
                }

                int previousPosition = animationItemAdapter.getSelectPosition();
                animationItemAdapter.setSelectPosition(position);
                if (previousPosition != -1) {
                    animationItemAdapter.notifyItemChanged(previousPosition);
                }
                animationItemAdapter.notifyItemChanged(position);
                animationItemAdapter.addDownloadMaterial(content1);
                animationViewModel.downloadMaterials(previousPosition, position, content1);
            }
        });
    }

    @Override
    protected void initViewModelObserve() {
        initColumns();
        animationViewModel.getPageData().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.isEmpty()) {
                animationItemAdapter.setSelectPosition(0);
                return;
            }
            if (currentPage == 0) {
                loadingIndicatorView.hide();
            }

            if (!animList.containsAll(list)) {
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                animList.addAll(list);
                animationItemAdapter.setData(animList);
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
            }
            setAnimationSelected(hveAsset, animList, animType);
            setAnimationBarDuration(hveAsset);
        });

        animationViewModel.getBoundaryPageData().observe(this, aBoolean -> {
            loadingIndicatorView.hide();
            mHasNextPage = aBoolean;
        });

        errorTypeObserve();
        downloadObserve();
        animationViewModel.getLoadUrlEvent().observe(this, loadUrlEvent -> {
            if (loadUrlEvent == null) {
                return;
            }

            if (mActivity == null) {
                return;
            }

            if (loadUrlEvent.getContent() == null) {
                ToastWrapper.makeText(context, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
                animationItemAdapter.setSelectPosition(loadUrlEvent.getPreviousPosition());
                if (loadUrlEvent.getPreviousPosition() != -1) {
                    animationItemAdapter.notifyItemChanged(loadUrlEvent.getPreviousPosition());
                }
                animationItemAdapter.notifyItemChanged(loadUrlEvent.getPosition());
            } else {
                animationItemAdapter.addDownloadMaterial(loadUrlEvent.getContent());
                animationViewModel.downloadMaterials(loadUrlEvent.getPreviousPosition(), loadUrlEvent.getPosition(),
                        loadUrlEvent.getContent());
            }
        });
        selectDataObserve();
        viewModel.getTimeout()
                .observe(this, isTimeout -> {
                    if (isTimeout && !isBackground) {
                        mActivity.onBackPressed();
                    }
                });
    }

    private void selectDataObserve() {
        animationViewModel.getSelectData().observe(this, cutContent -> {
            if (cutContent == null) {
                return;
            }
            if (hveAsset == null) {
                return;
            }
            if (animationViewModel == null) {
                return;
            }
            if (cutContent.getId().equals("-1")) {
                animationViewModel.removeAnimation(hveAsset, animType);
                setAnimationBarDuration(hveAsset);
                return;
            }

            HVEEffect enterEffect = animationViewModel.getEnterAnimation(hveAsset);
            HVEEffect leaveEffect = animationViewModel.getLeaveAnimation(hveAsset);
            long startTime = 0L;
            long endTime = 0L;
            long duration = 500L;
            if (animType.equals(HVEEffect.ENTER_ANIMATION)) {
                if (enterEffect != null) {
                    duration = enterEffect.getDuration();
                } else {
                    if (leaveEffect == null) {
                        duration = hveAsset.getDuration() >= duration ? duration : hveAsset.getDuration();
                    } else {
                        duration = Math.min((hveAsset.getDuration() - leaveEffect.getDuration()), duration);
                    }
                }
                startTime = hveAsset.getStartTime();
                endTime = startTime + duration;
            } else if (animType.equals(HVEEffect.LEAVE_ANIMATION)) {
                if (leaveEffect != null) {
                    duration = leaveEffect.getDuration();
                } else {
                    if (enterEffect == null) {
                        duration = Math.min(duration, hveAsset.getDuration());
                    } else {
                        duration = Math.min((hveAsset.getDuration() - enterEffect.getDuration()), duration);
                    }
                }
                startTime = hveAsset.getEndTime() - duration;
                endTime = startTime + duration;
            }
            HVEEffect effect = animationViewModel.appendAnimation(hveAsset, cutContent, duration, animType);
            if (effect == null) {
                return;
            }
            setAnimationBarDuration(hveAsset);

            if (endTime >= hveAsset.getEndTime()) {
                endTime = endTime - 1;
            }
            viewModel.updateDuration();
            viewModel.playTimeLine(startTime, endTime);
        });
    }

    private void downloadObserve() {
        animationViewModel.getDownloadInfo().observe(this, materialsDownloadInfo -> {
            if (mActivity == null) {
                return;
            }
            int downloadState = materialsDownloadInfo.getState();
            switch (downloadState) {
                case MaterialsRespository.DOWNLOAD_SUCCESS:
                    animationItemAdapter.removeDownloadMaterial(materialsDownloadInfo.getContentId());
                    int downloadPosition = materialsDownloadInfo.getDataPosition();
                    if (downloadPosition <= 0 || downloadPosition >= animList.size()) {
                        return;
                    }
                    if (!materialsDownloadInfo.getContentId().equals(animList.get(downloadPosition).getId())) {
                        return;
                    }
                    if (materialsDownloadInfo.getPreviousPosition() != -1) {
                        animationItemAdapter.notifyItemChanged(materialsDownloadInfo.getPreviousPosition());
                    }
                    animList.set(downloadPosition, materialsDownloadInfo.getMaterialBean());
                    animationItemAdapter.notifyItemChanged(downloadPosition);
                    if (downloadPosition == animationItemAdapter.getSelectPosition()) {
                        animationViewModel.setSelectCutContent(animList.get(downloadPosition));
                    }
                    break;
                case MaterialsRespository.DOWNLOAD_FAIL:
                    animationItemAdapter.removeDownloadMaterial(materialsDownloadInfo.getContentId());
                    updateFail(materialsDownloadInfo);
                    ToastWrapper.makeText(mActivity, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
                    break;
                case MaterialsRespository.DOWNLOAD_LOADING:
                    SmartLog.d(TAG, "progress:" + materialsDownloadInfo.getProgress());
                    break;
                default:
                    break;
            }
        });
    }

    private void errorTypeObserve() {
        animationViewModel.getErrorType().observe(this, type -> {
            if (mActivity == null) {
                return;
            }
            switch (type) {
                case MaterialsRespository.RESULT_ILLEGAL:
                    if (animList == null || animList.isEmpty()) {
                        loadingIndicatorView.hide();
                        errorTv.setText(getString(R.string.result_illegal));
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                case MaterialsRespository.RESULT_EMPTY:
                    if (currentPage == 0) {
                        loadingIndicatorView.hide();
                    }
                    ToastWrapper.makeText(mActivity, getString(R.string.result_empty), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        });
    }

    private void initColumns() {
        animationViewModel.getColumns().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.isEmpty()) {
                return;
            }
            animationColumnList.clear();
            animationColumnList.addAll(list);
            List<TabTopInfo<?>> tabTopInfoList = new ArrayList<>();
            for (HVEColumnInfo item : list) {
                tabTopInfoList.add(new TabTopInfo<>(item.getColumnName(), true,
                        ContextCompat.getColor(mActivity, R.color.tab_text_default_color),
                        ContextCompat.getColor(mActivity, R.color.tab_text_tint_color), SizeUtils.dp2Px(mActivity, 15),
                        SizeUtils.dp2Px(mActivity, 15)));
            }
            tabTopLayout.inflateInfo(tabTopInfoList);
            loadingIndicatorView.hide();
            tabTopLayout.defaultSelected(tabTopInfoList.get(currentIndex));
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mActivity != null) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchStAnListener);
        }
        if (hveAsset != null) {
            viewModel.refreshTrackView(hveAsset.getUuid());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirst = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchStAnListener);
        viewModel.destroyTimeoutManager();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDynamicViewLayoutChange();
    }

    @Override
    public void onEnterProgressChanged(int progress) {
        if (hveAsset == null) {
            return;
        }
        HVEEffect enterEffect = animationViewModel.getEnterAnimation(hveAsset);
        if (enterEffect == null) {
            return;
        }

        long duration = animationBar.getEnterDuration();
        long startTime = hveAsset.getStartTime();
        long endTime = startTime + duration;

        animationViewModel.setAnimationDuration(hveAsset, duration, HVEEffect.ENTER_ANIMATION);

        if (endTime >= hveAsset.getEndTime()) {
            endTime = endTime - 1;
        }
        viewModel.playTimeLine(startTime, endTime);
    }

    @Override
    public void onLeaveProgressChanged(int progress) {
        if (hveAsset == null) {
            return;
        }
        HVEEffect effect = animationViewModel.getLeaveAnimation(hveAsset);
        if (effect == null) {
            return;
        }
        long duration = animationBar.getLeaveDuration();
        long endTime = hveAsset.getEndTime();
        long startTime = endTime - duration;
        animationViewModel.setAnimationDuration(hveAsset, duration, HVEEffect.LEAVE_ANIMATION);
        if (endTime >= hveAsset.getEndTime()) {
            endTime = endTime - 1;
        }
        viewModel.playTimeLine(startTime, endTime);
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchStAnListener = ev -> {
        viewModel.initTimeoutManager();
        return false;
    };

    private void updateFail(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getDataPosition();
        if (downloadPosition < 0 || downloadPosition >= animList.size()) {
            return;
        }
        if (!downloadInfo.getContentId().equals(animList.get(downloadPosition).getId())) {
            return;
        }
        animList.set(downloadPosition, downloadInfo.getMaterialBean());
        if (downloadInfo.getPreviousPosition() != -1) {
            animationItemAdapter.notifyItemChanged(downloadInfo.getPreviousPosition());
        }
        animationItemAdapter.notifyItemChanged(downloadPosition);
    }

    private void setAnimationSelected(HVEAsset hveAsset, List<CloudMaterialBean> animList, String animType) {
        int selectedPosition = animationViewModel.getSelectedPosition(hveAsset, animList, animType);
        animationItemAdapter.setSelectPosition(selectedPosition);
    }

    private void setAnimationBarDuration(HVEAsset hveAsset) {
        HVEEffect cycleEffect = animationViewModel.getCycleAnimation(hveAsset);
        HVEEffect enterEffect = animationViewModel.getEnterAnimation(hveAsset);
        HVEEffect leaveEffect = animationViewModel.getLeaveAnimation(hveAsset);
        if (cycleEffect != null || (enterEffect == null && leaveEffect == null)) {
            animationBar.setEnterShow(false);
            animationBar.setLeaveShow(false);
            seek_container.setVisibility(View.GONE);
            return;
        }

        if (enterEffect != null) {
            seek_container.setVisibility(View.VISIBLE);
            animationBar.setEnterShow(true);
            animationBar.setEnterDuration(enterEffect.getDuration());
        } else {
            animationBar.setEnterShow(false);
        }

        if (leaveEffect != null) {
            seek_container.setVisibility(View.VISIBLE);
            animationBar.setLeaveShow(true);
            animationBar.setLeaveDuration(leaveEffect.getDuration());
        } else {
            animationBar.setLeaveShow(false);
        }
    }
}
