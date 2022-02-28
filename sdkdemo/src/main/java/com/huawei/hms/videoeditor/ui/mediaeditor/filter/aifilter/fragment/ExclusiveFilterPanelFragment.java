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

package com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.fragment;

import static com.huawei.hms.videoeditor.sdk.effect.impl.ColorFilterEffect.FILTER_AI_TYPE;
import static com.huawei.hms.videoeditor.sdk.effect.impl.ColorFilterEffect.FILTER_TYPE_CLONE_VALUE;
import static com.huawei.hms.videoeditor.sdk.effect.impl.ColorFilterEffect.FILTER_TYPE_SINGLE_VALUE;
import static com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.viewmodel.ExclusiveFilterPanelViewModel.FILTER_TYPE_SINGLE;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.materials.HVELocalMaterialInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialConstant;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.lane.HVEEffectLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.LaneSizeCheckUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.view.ExclusiveFilterPopupView;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.dialog.CommonBottomDialog;
import com.huawei.hms.videoeditor.ui.common.view.dialog.ExclusiveFilterRenameDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.activity.CreateFilterActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.adapter.ExclusiveFilterItemAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.viewmodel.ExclusiveFilterPanelViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.FilterSeekBar;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExclusiveFilterPanelFragment extends BaseFragment {
    private static final String TAG = "ExclusiveFilterPanelFragment";

    private static final String FILTER_CLONE = "clone";

    private static final String FILTER_IMITATION = "imitation";

    private ImageView mCertainIv;

    private RecyclerView mRecyclerView;

    private LinearLayout mFilterCancelView;

    private LinearLayout mFilterAddView;

    private ExclusiveFilterPopupView mCreatePopupWindow;

    private ExclusiveFilterPopupView mEditorPopupWindow;

    private ImageView mPopupMarkPosView;

    private ExclusiveFilterItemAdapter mExclusiveFilterItemAdapter;

    private List<CloudMaterialBean> mCutContentList = new ArrayList<>();

    private ExclusiveFilterPanelViewModel mExclusiveFilterPanelViewModel;

    protected EditPreviewViewModel mEditPreviewViewModel;

    private FilterSeekBar mSeekBar;

    private static float DEFAULT_STRENGTH_VALUE = 1.0f;

    private HVEEffect mLastEffect;

    private int mSelectedDataPosition;

    private int mSelectpostion;

    private HuaweiVideoEditor mEditor;

    private HVETimeLine timeLine;

    private static final int DEFAULT_PROGRESS_VALUE = 100;

    private static final int REFRESH = 5;

    private AIFilterHandler aiFilterHandler = new AIFilterHandler(this);

    private CloudMaterialBean mContent;

    private static class AIFilterHandler extends Handler {
        private WeakReference<ExclusiveFilterPanelFragment> weakReference;

        AIFilterHandler(ExclusiveFilterPanelFragment exclusiveFilterPanelFragment) {
            weakReference = new WeakReference<>(exclusiveFilterPanelFragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ExclusiveFilterPanelFragment exclusiveFilterPanelFragment = weakReference.get();
            switch (msg.what) {
                case REFRESH:
                    if (exclusiveFilterPanelFragment.mEditPreviewViewModel == null) {
                        return;
                    }
                    long refershTime = (long) msg.obj;
                    exclusiveFilterPanelFragment.mEditPreviewViewModel.playTimeLine(refershTime, refershTime + 3000);
                    break;
                default:
                    break;
            }
        }
    }

    public static ExclusiveFilterPanelFragment newInstance() {
        return new ExclusiveFilterPanelFragment();
    }

    @Override
    protected void initViewModelObserve() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_panel_filter_exclusive;
    }

    @Override
    public void initView(View view) {
        navigationBarColor = R.color.color_20;
        TextView tvTitle = view.findViewById(R.id.tv_title);
        mRecyclerView = view.findViewById(R.id.recycler_exclusive_filter);
        mCertainIv = view.findViewById(R.id.iv_certain);
        mSeekBar = view.findViewById(R.id.sb_items);
        tvTitle.setText(getString(R.string.filter_second_menu_add_exclusive));
        View headerView =
            LayoutInflater.from(mActivity).inflate(R.layout.adapter_add_filter_exclusive_head, null, false);
        headerView.setLayoutParams(
            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, SizeUtils.dp2Px(context, 75)));
        mFilterCancelView = headerView.findViewById(R.id.rl_cancel);
        mFilterCancelView.setSelected(true);
        mFilterAddView = headerView.findViewById(R.id.rl_add);
        mPopupMarkPosView = headerView.findViewById(R.id.iv_mark_position);

        mExclusiveFilterItemAdapter =
            new ExclusiveFilterItemAdapter(mActivity, mCutContentList, R.layout.adapter_add_exclusive_filter_item);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        if (mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView
                .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.color_20),
                    SizeUtils.dp2Px(mActivity, 75), SizeUtils.dp2Px(mActivity, 8)));
        }
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mExclusiveFilterItemAdapter);
        mExclusiveFilterItemAdapter.addHeaderView(headerView);
        mExclusiveFilterItemAdapter.setSelectPosition(-1);
        mSeekBar.setmMinProgress(0);
        mSeekBar.setmMaxProgress(100);
        mSeekBar.setmAnchorProgress(0);
    }

    @Override
    protected void initObject() {
        setTimeoutEnable();
        ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchListener);
        mCreatePopupWindow = new ExclusiveFilterPopupView(mActivity, Constant.FILTER_POPUP_VIEW_CREATE);
        mEditorPopupWindow = new ExclusiveFilterPopupView(mActivity, Constant.FILTER_POPUP_VIEW_EDITOR);
        mExclusiveFilterPanelViewModel = new ViewModelProvider(this, mFactory).get(ExclusiveFilterPanelViewModel.class);
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mEditor = EditorManager.getInstance().getEditor();
        timeLine = EditorManager.getInstance().getTimeLine();
    }

    @Override
    protected void initData() {
        mCutContentList.clear();

        List<HVELocalMaterialInfo> cutContentList =
            mExclusiveFilterPanelViewModel.queryAllMaterialsByType(HVEMaterialConstant.AI_FILTER);

        for (int i = 0; i < cutContentList.size(); i++) {
            HVELocalMaterialInfo materialInfo = cutContentList.get(i);
            if (materialInfo.getMaterialPath() != null) {
                CloudMaterialBean cloudMaterialBean = new CloudMaterialBean();
                cloudMaterialBean.setId(materialInfo.getMaterialId());
                cloudMaterialBean.setLocalPath(materialInfo.getMaterialPath());
                cloudMaterialBean.setName(materialInfo.getMaterialName());
                mCutContentList.add(cloudMaterialBean);
            }
        }

        Collections.reverse(mCutContentList);
        mExclusiveFilterItemAdapter.notifyDataSetChanged();
        mLastEffect = mEditPreviewViewModel.getSelectedEffect();
        if (mLastEffect != null) {
            int progress = (int) (mLastEffect.getFloatVal(HVEEffect.FILTER_STRENTH_KEY) * 100);
            showSeekBar(true, progress);
            for (int i = 0; i < mCutContentList.size(); i++) {
                if (mCutContentList.get(i) != null) {
                    if (mLastEffect.getOptions().getEffectId().equals(mCutContentList.get(i).getId())) {
                        mExclusiveFilterItemAdapter.setSelectPosition(i + 1);
                        mExclusiveFilterItemAdapter.notifyItemChanged(i + 1);
                        mFilterCancelView.setSelected(false);
                        mFilterAddView.setSelected(false);
                        break;
                    }
                }
            }
        } else {
            showSeekBar(false, DEFAULT_PROGRESS_VALUE);
        }
    }

    @Override
    protected void initEvent() {
        mCertainIv.setOnClickListener(view -> mActivity.onBackPressed());

        mFilterCancelView.setOnClickListener(new OnClickRepeatedListener(v -> {
            mFilterCancelView.setSelected(true);
            mFilterAddView.setSelected(false);
            int selectPosition = mExclusiveFilterItemAdapter.getSelectPosition();
            mExclusiveFilterItemAdapter.setSelectPosition(-1);
            if (selectPosition != -1) {
                mExclusiveFilterItemAdapter.notifyItemChanged(selectPosition);
            }
            mLastEffect = mEditPreviewViewModel.getSelectedEffect();
            if (mLastEffect == null) {
                return;
            }
            deleteEffect(mLastEffect);
        }));

        mFilterAddView.setOnClickListener(new OnClickRepeatedListener(v -> {
            mFilterCancelView.setSelected(false);
            mFilterAddView.setSelected(true);
            mCreatePopupWindow.showAsDropDown(mPopupMarkPosView,
                mPopupMarkPosView.getRight() - mPopupMarkPosView.getLeft(), 0);
        }));

        mExclusiveFilterItemAdapter.setOnItemClickListener(new ExclusiveFilterItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int dataPosition) {
                if (mCutContentList.isEmpty() || mEditor == null || timeLine == null) {
                    return;
                }
                mContent = mCutContentList.get(dataPosition);
                if (mContent == null) {
                    return;
                }
                mFilterCancelView.setSelected(false);
                mFilterAddView.setSelected(false);

                int previousPosition = mExclusiveFilterItemAdapter.getSelectPosition();
                if (previousPosition == position) {
                    return;
                }

                mExclusiveFilterItemAdapter.setSelectPosition(position);
                if (previousPosition != -1) {
                    mExclusiveFilterItemAdapter.notifyItemChanged(previousPosition);
                }
                mExclusiveFilterItemAdapter.notifyItemChanged(position);
                long effectStartTime = timeLine.getCurrentTime();
                long effectEndTime = effectStartTime + 3000;
                mLastEffect = mEditPreviewViewModel.getSelectedEffect();
                if (mLastEffect == null) {
                    mLastEffect = addFilterEffect(mContent, effectStartTime, effectEndTime);
                } else {
                    mLastEffect = replaceFilterEffect(mLastEffect, mContent);
                }
            }

            @Override
            public void onItemLongClick(int position, int dataPosition, View markPosView) {
                mSelectedDataPosition = dataPosition;
                mSelectpostion = position;
                mEditorPopupWindow.showAsDropDown(markPosView, markPosView.getRight() - markPosView.getLeft(), 0);
            }
        });

        mCreatePopupWindow.setOnCreateFilterClickListener(new ExclusiveFilterPopupView.OnCreateFilterClickListener() {
            @Override
            public void onCloneFilterClick() {
                startExclusiveFilterEditerActivity(FILTER_CLONE);
            }

            @Override
            public void onImitFilterClick() {
                startExclusiveFilterEditerActivity(FILTER_IMITATION);
            }
        });

        mCreatePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFilterAddView.setSelected(false);
            }
        });

        mEditorPopupWindow.setOnEditorFilterClickListener(new ExclusiveFilterPopupView.OnEditorFilterClickListener() {
            @Override
            public void onRenameFilterClick() {
                if (timeLine == null) {
                    return;
                }
                CloudMaterialBean materialsCutContent = mCutContentList.get(mSelectedDataPosition);
                String prevName = "";
                if (materialsCutContent != null) {
                    prevName = materialsCutContent.getName();
                }
                ExclusiveFilterRenameDialog dialog = new ExclusiveFilterRenameDialog(mActivity, prevName);
                dialog.show();
                dialog.setOnPositiveClickListener((updateName) -> {
                    if (materialsCutContent == null || TextUtils.isEmpty(updateName)) {
                        return;
                    }
                    materialsCutContent.setName(updateName);
                    mExclusiveFilterPanelViewModel.updateFilter(materialsCutContent);
                    mCutContentList.set(mSelectedDataPosition, materialsCutContent);
                    mExclusiveFilterItemAdapter.notifyItemChanged(mSelectpostion);
                });
            }

            @Override
            public void onDeleteFilterClick() {
                Context context = getContext();
                if (context == null) {
                    return;
                }
                CommonBottomDialog dialog = new CommonBottomDialog(context);
                dialog.show(getString(R.string.exclusive_filter_tip_text7), getString(R.string.app_confirm),
                    getString(R.string.app_cancel));
                dialog.setColor(R.color.color_text_first_level, R.color.color_text_focus, R.color.color_fff_20);
                dialog.setOnDialogClickLister(new CommonBottomDialog.OnDialogClickLister() {
                    @Override
                    public void onCancelClick() {
                    }

                    @Override
                    public void onAllowClick() {
                        SmartLog.i(TAG, "onAllowClick start.");
                        CloudMaterialBean materialsCutContent = mCutContentList.get(mSelectedDataPosition);
                        if (materialsCutContent == null || TextUtils.isEmpty(materialsCutContent.getId())) {
                            return;
                        }
                        mExclusiveFilterPanelViewModel.deleteFilterData(materialsCutContent.getId());
                        mCutContentList.remove(mSelectedDataPosition);
                        mExclusiveFilterItemAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        mSeekBar.setOnProgressChangedListener(progress -> {
            mSeekBar.setContentDescription(getString(R.string.filter_strength) + progress);
            mEditPreviewViewModel.setToastTime(String.valueOf(progress));
            refreshProgress(progress);
        });

        mSeekBar.setbTouchListener(
            isTouch -> mEditPreviewViewModel.setToastTime(isTouch ? String.valueOf((int) mSeekBar.getProgress()) : ""));
    }

    private void refreshProgress(int progress) {
        if (mEditor == null) {
            return;
        }
        if (mLastEffect == null) {
            return;
        }
        float strength = (float) progress / 100;
        mLastEffect.setFloatVal(HVEEffect.FILTER_STRENTH_KEY, strength);
        mEditor.refresh(timeLine.getCurrentTime());
    }

    private void showSeekBar(boolean show, int progress) {
        if (show) {
            mSeekBar.setVisibility(View.VISIBLE);
            mSeekBar.setProgress(progress);
        } else {
            mSeekBar.setVisibility(View.INVISIBLE);
            mSeekBar.setProgress(progress);
        }
    }

    private void startExclusiveFilterEditerActivity(String extraValue) {
        Intent intent = new Intent(mActivity, CreateFilterActivity.class);
        intent.putExtra("filterType", extraValue);
        startActivityForResult(intent, Constant.REQ_CODE_CHOOSE_IMAGE);
    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        aiFilterHandler.removeCallbacksAndMessages(null);
        aiFilterHandler = null;
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchListener = ev -> {
        initTimeoutState();
        return false;
    };

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDynamicViewLayoutChange();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == Constant.REQ_CODE_CHOOSE_IMAGE && resultCode == Constant.RESP_CODE_CHOOSE_IMAGE) {
            handleCustomFilter(data);
        }
    }

    private void handleCustomFilter(@Nullable Intent data) {
        if (data == null) {
            return;
        }
        if (timeLine == null) {
            return;
        }
        String filterPath = data.getStringExtra("FilterPath");
        String filterId = data.getStringExtra("FilterId");
        String filterName = data.getStringExtra("FilterName");
        String filterType = data.getStringExtra("FilterType");
        if (TextUtils.isEmpty(filterPath) || TextUtils.isEmpty(filterId) || TextUtils.isEmpty(filterName)) {
            return;
        }

        CloudMaterialBean customFilter = new CloudMaterialBean();
        customFilter.setType(HVEMaterialConstant.AI_FILTER);
        customFilter.setId(filterId);
        customFilter.setName(filterName);
        customFilter.setLocalPath(filterPath);
        customFilter.setPreviewUrl(filterPath);
        customFilter.setCategoryName(filterType);
        mCutContentList.add(0, customFilter);
        mExclusiveFilterItemAdapter.setSelectPosition(1);
        mExclusiveFilterItemAdapter.notifyDataSetChanged();
        SmartLog.d(TAG, "filter count ==" + (mCutContentList.size() + 1));
        long effectStartTime = timeLine.getCurrentTime();
        long effectEndTime = effectStartTime + 3000;
        mLastEffect = mEditPreviewViewModel.getSelectedEffect();
        if (mLastEffect == null) {
            mLastEffect = addFilterEffect(customFilter, effectStartTime, effectEndTime);
        } else {
            mLastEffect = replaceFilterEffect(mLastEffect, customFilter);
        }
        mFilterCancelView.setSelected(false);
        mFilterAddView.setSelected(false);
    }

    private HVEEffect addFilterEffect(CloudMaterialBean cutContent, long startTime, long endTime) {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor == null) {
            return null;
        }
        if (cutContent == null) {
            return null;
        }
        String mSelectName = cutContent.getName();
        String mSelectPath = cutContent.getLocalPath();
        String mContentId = cutContent.getId();
        if (TextUtils.isEmpty(mSelectName) || TextUtils.isEmpty(mSelectPath) || TextUtils.isEmpty(mContentId)) {
            return null;
        }
        HVEEffect effect = LaneSizeCheckUtils.getFilterFreeLan(editor, startTime, endTime)
            .appendEffect(
                new HVEEffect.Options(HVEEffect.CUSTOM_FILTER,
                    mContentId, mSelectPath),
                startTime, endTime - startTime);
        if (effect == null) {
            return null;
        }

        if (FILTER_TYPE_SINGLE.equals(cutContent.getCategoryName())) {
            effect.setIntVal(FILTER_AI_TYPE, FILTER_TYPE_SINGLE_VALUE);
        } else if (ExclusiveFilterPanelViewModel.FILTER_TYPE_CLONE.equals(cutContent.getCategoryName())) {
            effect.setIntVal(FILTER_AI_TYPE, FILTER_TYPE_CLONE_VALUE);
        }

        effect.setFloatVal(HVEEffect.FILTER_STRENTH_KEY, DEFAULT_STRENGTH_VALUE);
        mEditPreviewViewModel.setSelectedUUID(effect.getUuid());
        mEditor.seekTimeLine(timeLine.getCurrentTime());
        showSeekBar(true, DEFAULT_PROGRESS_VALUE);
        return effect;
    }

    private HVEEffect replaceFilterEffect(HVEEffect lastEffect, CloudMaterialBean cutContent) {
        if (cutContent == null || lastEffect == null) {
            return null;
        }
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        HVETimeLine mCurrentTimeLine = EditorManager.getInstance().getTimeLine();
        if (editor == null || mCurrentTimeLine == null) {
            return null;
        }

        int lastEffectIndex = lastEffect.getIndex();
        int lastEffectLaneIndex = lastEffect.getLaneIndex();
        float strength = lastEffect.getFloatVal(HVEEffect.FILTER_STRENTH_KEY);
        if (lastEffectIndex < 0 || lastEffectLaneIndex < 0) {
            return null;
        }

        HVEEffectLane effectLane = mCurrentTimeLine.getEffectLane(lastEffectLaneIndex);
        if (effectLane == null) {
            return null;
        }

        long lastStartTime = lastEffect.getStartTime();
        long lastEndTime = lastEffect.getEndTime();

        lastEffect = effectLane.replaceEffect(
            new HVEEffect.Options(HVEEffect.CUSTOM_FILTER,
                cutContent.getId(), cutContent.getLocalPath()),
            lastEffectIndex, lastStartTime, lastEndTime - lastStartTime);
        if (lastEffect == null) {
            return null;
        }

        if (FILTER_TYPE_SINGLE.equals(cutContent.getCategoryName())) {
            lastEffect.setIntVal(FILTER_AI_TYPE, FILTER_TYPE_SINGLE_VALUE);
        } else if (ExclusiveFilterPanelViewModel.FILTER_TYPE_CLONE.equals(cutContent.getCategoryName())) {
            lastEffect.setIntVal(FILTER_AI_TYPE, FILTER_TYPE_CLONE_VALUE);
        }
        lastEffect.setFloatVal(HVEEffect.FILTER_STRENTH_KEY, strength);
        mEditPreviewViewModel.setSelectedUUID(lastEffect.getUuid());
        mEditor.seekTimeLine(timeLine.getCurrentTime());
        showSeekBar(true, DEFAULT_PROGRESS_VALUE);
        return lastEffect;
    }

    public void deleteEffect(HVEEffect effect) {
        if (effect == null) {
            return;
        }

        mExclusiveFilterPanelViewModel.deleteFilterEffect(effect);
        showSeekBar(false, DEFAULT_PROGRESS_VALUE);
        if (mEditor != null && timeLine != null) {
            long currentTime = timeLine.getCurrentTime();
            mEditor.refresh(currentTime);
        }
    }
}
