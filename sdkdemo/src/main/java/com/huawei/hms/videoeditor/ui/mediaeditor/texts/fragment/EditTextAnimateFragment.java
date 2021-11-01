
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

package com.huawei.hms.videoeditor.ui.mediaeditor.texts.fragment;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialConstant;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.AnimationBar;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopInfo;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopLayout;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.MaterialsRespository;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.adapter.TextAnimationItemAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel.TextAnimationViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel.TextPanelViewModel;
import com.huawei.secure.android.common.intent.SafeBundle;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EditTextAnimateFragment extends BaseFragment implements AnimationBar.OnProgressChangedListener {
    private static final String TAG = "EditTextAnimateFragment";

    private List<HVEColumnInfo> columnList = new ArrayList<>();

    private List<CloudMaterialBean> animList = new ArrayList<>();

    private List<CloudMaterialBean> initAnim = new ArrayList<>(1);

    private HVEAsset hveAsset = null;

    private TabTopLayout tabTopLayout;

    private RelativeLayout errorLayout;

    private TextView errorTv;

    private LoadingIndicatorView loadingIndicatorView;

    private RecyclerView recyclerView;

    private LinearLayout seek_container;

    private AnimationBar animationBar;

    private TextAnimationItemAdapter textAnimationItemAdapter;

    protected TextAnimationViewModel textAnimationViewModel;

    private TextPanelViewModel textPanelViewModel;

    private boolean isFirst;

    private Boolean mHasNextPage = false;

    private HVEColumnInfo content;

    private int currentIndex = 0;

    private int currentPage = 0;

    private String animType = HVEEffect.ENTER_ANIMATION;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_edit_text_animate;
    }

    @Override
    protected void initView(View view) {
        tabTopLayout = view.findViewById(R.id.tab_top_layout);
        errorLayout = view.findViewById(R.id.error_layout);
        errorTv = view.findViewById(R.id.error_text);
        loadingIndicatorView = view.findViewById(R.id.indicator);
        recyclerView = view.findViewById(R.id.rl_pic);
        animationBar = view.findViewById(R.id.sb_items);
        seek_container = view.findViewById(R.id.seek_container);

        TextView animationText = view.findViewById(R.id.animtext);

        loadingIndicatorView.show();

        textAnimationItemAdapter = new TextAnimationItemAdapter(mActivity, animList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        if (recyclerView.getItemDecorationCount() == 0) {
            recyclerView
                .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.color_20),
                    SizeUtils.dp2Px(mActivity, 76), SizeUtils.dp2Px(mActivity, 8)));
        }
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(textAnimationItemAdapter);

        if (ScreenUtil.isRTL()) {
            tabTopLayout.setScaleX(RTL_UI);
            seek_container.setScaleX(RTL_UI);
            animationText.setScaleX(RTL_UI);
        } else {
            tabTopLayout.setScaleX(LTR_UI);
            seek_container.setScaleX(LTR_UI);
            animationText.setScaleX(LTR_UI);
        }
    }

    @Override
    protected void initObject() {
        setTimeoutEnable();
        textAnimationViewModel = new ViewModelProvider(this, mFactory).get(TextAnimationViewModel.class);
        textPanelViewModel = new ViewModelProvider(mActivity).get(TextPanelViewModel.class);
    }

    @Override
    protected void initData() {
        hveAsset = viewModel.getSelectedAsset();
        if (hveAsset != null) {
            animationBar.setDuration(hveAsset.getDuration());
        }
        textAnimationViewModel.initColumns(HVEMaterialConstant.TEXT_ANIMATION_FATHER_COLUMN);
    }

    @SuppressLint({"StringFormatInvalid", "StringFormatMatches"})
    @Override
    protected void initEvent() {
        animationBar.setOnProgressChangedListener(this);

        tabTopLayout.addTabSelectedChangeListener((index, prevInfo, nextInfo) -> {
            if (columnList == null) {
                return;
            }
            errorLayout.setVisibility(View.GONE);
            currentIndex = index;
            if (index == 0) {
                animType = HVEEffect.ENTER_ANIMATION;
            } else if (index == 1) {
                animType = HVEEffect.LEAVE_ANIMATION;
            } else if (index == 2) {
                animType = HVEEffect.CYCLE_ANIMATION;
            }
            currentPage = 0;
            isFirst = false;
            animList.clear();
            animList.addAll(initAnim);
            textAnimationItemAdapter.setData(animList);

            content = columnList.get(currentIndex);
            if (content == null) {
                return;
            }
            textPanelViewModel.setAnimColumn(content.getColumnName());
            textAnimationViewModel.loadMaterials(content, currentPage);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isSlidingToLeft = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (mHasNextPage && manager != null && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int itemCount = manager.getItemCount();
                    if (content == null) {
                        return;
                    }
                    if (lastItemPosition != (itemCount - 1) || !isSlidingToLeft) {
                        return;
                    }
                    currentPage++;
                    textAnimationViewModel.loadMaterials(content, currentPage);
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
                    if (firstPosition != -1 && visibleItemCount > 0 && !isFirst && animList != null
                        && animList.size() > 1) {
                        isFirst = true;
                        SmartLog.w(TAG, "HianalyticsEvent10007 postEvent");
                    }
                }
            }
        });

        errorLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (currentPage == 0) {
                errorLayout.setVisibility(View.GONE);
                loadingIndicatorView.show();
            }
            textAnimationViewModel.initColumns(HVEMaterialConstant.TEXT_ANIMATION_FATHER_COLUMN);
        }));
        animationBar.setcTouchListener(isTouch -> viewModel.setToastTime(isTouch ? animationBar.getProgress() : ""));

        textAnimationItemAdapter.setOnItemClickListener(new TextAnimationItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (animList == null || animList.isEmpty()) {
                    return;
                }

                int previousPosition = textAnimationItemAdapter.getSelectPosition();
                if (previousPosition == position) {
                    return;
                }
                textAnimationItemAdapter.setSelectPosition(position);
                if (previousPosition != -1) {
                    textAnimationItemAdapter.notifyItemChanged(previousPosition);
                }
                textAnimationItemAdapter.notifyItemChanged(position);
                textAnimationViewModel.setSelectCutContent(animList.get(position));
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
                int previousPosition = textAnimationItemAdapter.getSelectPosition();
                textAnimationItemAdapter.setSelectPosition(position);
                if (previousPosition != -1) {
                    textAnimationItemAdapter.notifyItemChanged(previousPosition);
                }
                textAnimationItemAdapter.notifyItemChanged(position);
                textAnimationItemAdapter.addDownloadMaterial(content1);
                textAnimationViewModel.downloadMaterials(previousPosition, position, content1);
            }
        });
    }

    @Override
    protected void initViewModelObserve() {
        textAnimationViewModel.getColumns().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.size() <= 0) {
                return;
            }
            columnList.clear();
            columnList.addAll(list);
            List<TabTopInfo<?>> tabTopInfoList = new ArrayList<>();
            for (HVEColumnInfo item : list) {
                tabTopInfoList.add(new TabTopInfo<>(item.getColumnName(), false,
                    ContextCompat.getColor(mActivity, R.color.color_fff_86),
                    ContextCompat.getColor(mActivity, R.color.tab_text_tint_color), SizeUtils.dp2Px(mActivity, 15),
                    SizeUtils.dp2Px(mActivity, 15)));
            }
            tabTopLayout.inflateInfo(tabTopInfoList);
            loadingIndicatorView.hide();
            SafeBundle safeBundle = new SafeBundle(getArguments());
            boolean isOperate = safeBundle.getBoolean(Constant.TEXT_ANIM_OPERATE, false);
            if (isOperate) {
                tabTopLayout.defaultSelected(tabTopInfoList.get(currentIndex));
            } else {
                tabTopLayout.defaultSelected(tabTopInfoList.get(0));
            }
        });

        textAnimationViewModel.getPageData().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.size() <= 0) {
                textAnimationItemAdapter.setSelectPosition(0);
                return;
            }
            if (currentPage == 0) {
                loadingIndicatorView.hide();
            }

            if (!animList.containsAll(list)) {
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                animList.addAll(list);
                textAnimationItemAdapter.setData(animList);
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
            }

            setAnimationSelected(hveAsset, animList, animType);
            setAnimationBarDuration(hveAsset);
        });

        textAnimationViewModel.getBoundaryPageData().observe(this, aBoolean -> {
            loadingIndicatorView.hide();
            mHasNextPage = aBoolean;
        });

        textAnimationViewModel.getErrorType().observe(this, type -> {
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

        textAnimationViewModel.getDownloadInfo().observe(this, materialsDownloadInfo -> {
            int downloadState = materialsDownloadInfo.getState();
            switch (downloadState) {
                case MaterialsRespository.DOWNLOAD_SUCCESS:
                    textAnimationItemAdapter.removeDownloadMaterial(materialsDownloadInfo.getContentId());
                    int downloadPosition = materialsDownloadInfo.getDataPosition();
                    if (downloadPosition <= 0 || downloadPosition >= animList.size()) {
                        return;
                    }
                    if (!materialsDownloadInfo.getContentId().equals(animList.get(downloadPosition).getId())) {
                        return;
                    }
                    animList.set(downloadPosition, materialsDownloadInfo.getMaterialBean());
                    textAnimationItemAdapter.notifyItemChanged(downloadPosition);
                    if (downloadPosition == textAnimationItemAdapter.getSelectPosition()) {
                        textAnimationViewModel.setSelectCutContent(animList.get(downloadPosition));
                    }
                    break;
                case MaterialsRespository.DOWNLOAD_FAIL:
                    textAnimationItemAdapter.removeDownloadMaterial(materialsDownloadInfo.getContentId());
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

        textAnimationViewModel.getLoadUrlEvent().observe(this, loadUrlEvent -> {
            if (loadUrlEvent == null) {
                return;
            }
            if (loadUrlEvent.getContent() == null) {
                ToastWrapper.makeText(context, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
                textAnimationItemAdapter.setSelectPosition(loadUrlEvent.getPreviousPosition());
                if (loadUrlEvent.getPreviousPosition() != -1) {
                    textAnimationItemAdapter.notifyItemChanged(loadUrlEvent.getPreviousPosition());
                }
                textAnimationItemAdapter.notifyItemChanged(loadUrlEvent.getPosition());
            } else {
                textAnimationItemAdapter.addDownloadMaterial(loadUrlEvent.getContent());
                textAnimationViewModel.downloadMaterials(loadUrlEvent.getPreviousPosition(), loadUrlEvent.getPosition(),
                    loadUrlEvent.getContent());
            }
        });

        textAnimationViewModel.getSelectData().observe(this, cutContent -> {
            if (cutContent == null) {
                return;
            }
            if (hveAsset == null) {
                return;
            }
            if (cutContent.getId().equals("-1")) {
                textAnimationViewModel.removeAnimation(hveAsset, animType);
                setAnimationBarDuration(hveAsset);
                return;
            }

            HVEEffect enterEffect = textAnimationViewModel.getEnterAnimation(hveAsset);
            HVEEffect leaveEffect = textAnimationViewModel.getLeaveAnimation(hveAsset);
            long startTime = 0;
            long endTime = 0;
            long duration = 500;
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
                endTime = hveAsset.getEndTime();
            }
            HVEEffect effect = textAnimationViewModel.appendAnimation(hveAsset, cutContent, duration, animType);
            setAnimationBarDuration(hveAsset);
            if (effect == null) {
                return;
            }

            if (endTime >= hveAsset.getEndTime()) {
                endTime = endTime - 1;
            }
            viewModel.updateDuration();
            viewModel.playTimeLine(startTime, endTime);
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirst = false;
        if (hveAsset != null) {
            viewModel.refreshTrackView(hveAsset.getUuid());
        }
        columnList = null;
        animList = null;
        initAnim = null;
    }

    @Override
    public void onEnterProgressChanged(int progress) {
        if (hveAsset == null) {
            return;
        }
        HVEEffect effect = textAnimationViewModel.getEnterAnimation(hveAsset);
        if (effect == null) {
            return;
        }
        long duration = animationBar.getEnterDuration();
        long startTime = hveAsset.getStartTime();
        long endTime = startTime + duration;
        textAnimationViewModel.setAnimationDuration(hveAsset, duration, HVEEffect.ENTER_ANIMATION);
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
        HVEEffect effect = textAnimationViewModel.getLeaveAnimation(hveAsset);
        if (effect == null) {
            return;
        }
        long duration = animationBar.getLeaveDuration();
        long endTime = hveAsset.getEndTime();
        long startTime = endTime - duration;
        textAnimationViewModel.setAnimationDuration(hveAsset, duration, HVEEffect.LEAVE_ANIMATION);
        if (endTime >= hveAsset.getEndTime()) {
            endTime = endTime - 1;
        }
        viewModel.playTimeLine(startTime, endTime);
    }

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
            textAnimationItemAdapter.notifyItemChanged(downloadInfo.getPreviousPosition());
        }
        textAnimationItemAdapter.notifyItemChanged(downloadPosition);
    }

    private void setAnimationSelected(HVEAsset hveAsset, List<CloudMaterialBean> animList, String animType) {
        int selectedPosition = textAnimationViewModel.getSelectedPosition(hveAsset, animList, animType);
        textAnimationItemAdapter.setSelectPosition(selectedPosition);
    }

    private void setAnimationBarDuration(HVEAsset hveAsset) {
        HVEEffect cycleEffect = textAnimationViewModel.getCycleAnimation(hveAsset);
        HVEEffect enterEffect = textAnimationViewModel.getEnterAnimation(hveAsset);
        HVEEffect leaveEffect = textAnimationViewModel.getLeaveAnimation(hveAsset);
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
