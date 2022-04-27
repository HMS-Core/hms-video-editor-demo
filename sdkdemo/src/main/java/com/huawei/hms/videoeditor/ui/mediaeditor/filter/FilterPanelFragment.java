
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

package com.huawei.hms.videoeditor.ui.mediaeditor.filter;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.FilterData;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTop;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopInfo;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopLayout;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.FilterSeekBar;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.secure.android.common.intent.SafeBundle;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FilterPanelFragment extends BaseFragment {
    private static final String TAG = "FilterPanelFragment";

    private ImageView mCertainIv;

    private TabTopLayout mTabTopLayout;

    private RecyclerView mRecyclerView;

    private FilterSeekBar mSeekBar;

    private RelativeLayout mErrorLayout;

    private TextView mErrorTv;

    private ConstraintLayout mLoadingLayout;

    private LoadingIndicatorView mIndicatorView;

    protected EditPreviewViewModel mEditPreviewViewModel;

    private FilterPanelViewModel mFilterPanelViewModel;

    private HuaweiVideoEditor mEditor;

    private int mCurrentIndex = 0;

    private List<TabTopInfo<?>> mInfoList;

    private List<HVEColumnInfo> mColumnList;

    private CloudMaterialBean mSelectedMaterialsCutContent;

    private List<CloudMaterialBean> mCutContentList;

    private FilterItemAdapter mFilterItemAdapter;

    private RelativeLayout mFilterCancelRl;

    private int mProgress = 80;

    private String mSelectPath;

    private String mSelectName;

    private String mSelectId;

    private float mSelectStrength;

    private int mCurrentPage = 0;

    private Boolean mHasNextPage = false;

    private boolean mAlreadyApplyAll = false;

    private boolean isAsset;

    public static final int FILTER_CHANGE = 0;

    public static final int FILTER_LAST = 1;

    private Map<Integer, FilterData> filterDataMap = new HashMap();

    private HVEVisibleAsset visibleAsset = null;

    private long effectStartTime = 0;

    private long effectEndTime = 0;

    private boolean isFirst;

    public static FilterPanelFragment newInstance(boolean isAsset) {
        Bundle args = new Bundle();
        args.putBoolean("isAsset", isAsset);
        FilterPanelFragment fragment = new FilterPanelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_panel_filter_add;
    }

    @Override
    public void initView(View view) {
        ((TextView) view.findViewById(R.id.tv_title)).setText(getString(R.string.first_menu_filter));
        mTabTopLayout = view.findViewById(R.id.tab_top_layout);
        mRecyclerView = view.findViewById(R.id.rl_pic);
        mCertainIv = view.findViewById(R.id.iv_certain);
        mSeekBar = view.findViewById(R.id.sb_items);
        mErrorLayout = view.findViewById(R.id.error_layout);
        mErrorTv = view.findViewById(R.id.error_text);
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mIndicatorView = view.findViewById(R.id.indicator);
        if (ScreenUtil.isRTL()) {
            mSeekBar.setScaleX(RTL_UI);
            mTabTopLayout.setScaleX(RTL_UI);
        } else {
            mSeekBar.setScaleX(LTR_UI);
            mTabTopLayout.setScaleX(LTR_UI);
        }
    }

    @Override
    protected void initObject() {
        setTimeoutEnable();
        SafeBundle safeBundle = new SafeBundle(getArguments());
        isAsset = safeBundle.getBoolean("isAsset");
        View mCancelHeaderView =
            LayoutInflater.from(mActivity).inflate(R.layout.adapter_add_filter_header, null, false);
        mCancelHeaderView.setLayoutParams(
            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, SizeUtils.dp2Px(context, 75)));

        mFilterCancelRl = mCancelHeaderView.findViewById(R.id.rl_cancel);
        mFilterCancelRl.setSelected(true);
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mFilterPanelViewModel = new ViewModelProvider(this, mFactory).get(FilterPanelViewModel.class);
        mFilterPanelViewModel.setContext(mActivity);
        mEditor = mEditPreviewViewModel.getEditor();
        mSeekBar.setmMinProgress(0);
        mSeekBar.setmMaxProgress(100);
        mSeekBar.setmAnchorProgress(0);
        mSeekBar.setProgress(80);
        mColumnList = new ArrayList<>();
        mInfoList = new ArrayList<>();
        mCutContentList = new ArrayList<>();
        mFilterItemAdapter = new FilterItemAdapter(mActivity, mCutContentList, R.layout.adapter_add_filter_item);
        mRecyclerView.setLayoutManager(new FilterLinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        if (mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView
                .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.color_20),
                    SizeUtils.dp2Px(mActivity, 75), SizeUtils.dp2Px(mActivity, 8)));
        }
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mFilterItemAdapter);
        mFilterItemAdapter.addHeaderView(mCancelHeaderView);
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchListener);
        }
    }

    @Override
    protected void initData() {
        if (isAsset) {
            HVEAsset asset = null;
            asset = mEditPreviewViewModel.getSelectedAsset();
            if (asset == null) {
                asset = mEditPreviewViewModel.getMainLaneAsset();
            }
            if (asset instanceof HVEVisibleAsset) {
                visibleAsset = (HVEVisibleAsset) asset;
            }
        }
        if (filterDataMap == null) {
            filterDataMap = new HashMap<>();
        }
        filterDataMap.put(FILTER_CHANGE, null);
        filterDataMap.put(FILTER_LAST, null);
        int defaultColor = ContextCompat.getColor(mActivity, R.color.tab_text_default_color);
        int selectColor = ContextCompat.getColor(mActivity, R.color.tab_text_tint_color);
        int leftRightPadding = SizeUtils.dp2Px(mActivity, 15);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mIndicatorView.show();
        mFilterPanelViewModel.initColumns();
        mFilterPanelViewModel.getColumns().observe(getViewLifecycleOwner(), list -> {
            if (list != null && list.size() > 0) {
                mColumnList.clear();
                mColumnList.addAll(list);
                mInfoList.clear();
                for (HVEColumnInfo item : list) {
                    mInfoList.add(new TabTopInfo<>(item.getColumnName(), true, defaultColor, selectColor,
                        leftRightPadding, leftRightPadding));
                }
                mTabTopLayout.inflateInfo(mInfoList);
                mLoadingLayout.setVisibility(View.GONE);
                mIndicatorView.hide();
                mCurrentIndex = 0;
                mTabTopLayout.defaultSelected(mInfoList.get(mCurrentIndex));
            }
        });

        mFilterPanelViewModel.initFilterData(filterDataMap, mEditPreviewViewModel, isAsset, visibleAsset);
    }

    @Override
    protected void initEvent() {
        mEditPreviewViewModel.getTimeout()
            .observe(this, isTimeout -> {
                if (isTimeout && !isBackground) {
                    mActivity.onBackPressed();
                }
            });
        mTabTopLayout.addTabSelectedChangeListener((index, prevInfo, nextInfo) -> {
            mCurrentIndex = index;
            mErrorLayout.setVisibility(View.GONE);
            mLoadingLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mIndicatorView.show();
            mCurrentPage = 0;
            isFirst = false;
            if (mTabTopLayout != null && mInfoList != null) {
                TabTop tab = mTabTopLayout.findTab(mInfoList.get(mCurrentIndex));
                if (tab == null) {
                    return;
                }
                ImageView iv = tab.getIvTabIcon();
                if (iv == null) {
                    return;
                }
                iv.setVisibility(View.GONE);
            }

            HVEColumnInfo content = mColumnList.get(mCurrentIndex);
            mCutContentList.clear();
            mFilterItemAdapter.notifyDataSetChanged();
            mFilterPanelViewModel.loadMaterials(content.getColumnId(), mCurrentPage);
            mFilterItemAdapter.setSelectPosition(-1);

            mSeekBar.setProgress(80);
            mSeekBar.invalidate();
        });

        mFilterCancelRl.setOnClickListener(v -> {
            mFilterCancelRl.setContentDescription(getString(R.string.no_filter));
            mFilterCancelRl.setSelected(true);
            if (mFilterCancelRl.isSelected()) {
                if (filterDataMap.get(FILTER_CHANGE) != null) {
                    HVEEffect effect = filterDataMap.get(FILTER_CHANGE).getEffect();
                    if (effect != null) {
                        mFilterPanelViewModel.deleteFilterEffect(mEditPreviewViewModel, mEditor, visibleAsset, effect,
                            isAsset);
                    }
                    filterDataMap.clear();
                    mSeekBar.setProgress(80);
                    mSeekBar.invalidate();
                    int mSelectPosition = mFilterItemAdapter.getSelectPosition();
                    mFilterItemAdapter.setSelectPosition(-1);
                    if (mSelectPosition != -1) {
                        mFilterItemAdapter.notifyItemChanged(mSelectPosition);
                    }
                }
                showSeekBar(false);
                if (mEditor != null && mEditor.getTimeLine() != null) {
                    long currentTime = mEditor.getTimeLine().getCurrentTime();
                    mEditor.refresh(currentTime);
                }
            }
        });

        mSeekBar.setOnProgressChangedListener(progress -> {
            mSeekBar.setContentDescription(getString(R.string.filter_strength) + progress);
            mEditPreviewViewModel.setToastTime(String.valueOf(progress));
            mProgress = progress;
            refreshProgress();
        });

        mSeekBar.setbTouchListener(
            isTouch -> mEditPreviewViewModel.setToastTime(isTouch ? String.valueOf((int) mSeekBar.getProgress()) : ""));

        mErrorLayout.setOnClickListener(v -> {
            if (mCurrentPage == 0) {
                mErrorLayout.setVisibility(View.GONE);
                mLoadingLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mIndicatorView.show();
            }
            if (mColumnList.size() > 0) {
                HVEColumnInfo content = mColumnList.get(mCurrentIndex);
                mCurrentPage = 0;
                mFilterPanelViewModel.loadMaterials(content.getColumnId(), mCurrentPage);
            } else {
                mFilterPanelViewModel.initColumns();
            }
            mSeekBar.setProgress(80);
            mSeekBar.invalidate();
        });

        mFilterPanelViewModel.getErrorString().observe(this, errorString -> {
            if (!TextUtils.isEmpty(errorString) && mCutContentList.size() == 0) {
                mLoadingLayout.setVisibility(View.GONE);
                mIndicatorView.hide();
                mErrorTv.setText(errorString);
                mErrorLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mFilterItemAdapter.setSelectPosition(-1);
                mCutContentList.clear();
                mFilterItemAdapter.notifyDataSetChanged();
                showSeekBar(false);
            }
        });

        mFilterPanelViewModel.getEmptyString().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (TextUtils.isEmpty(s)) {
                    mIndicatorView.hide();
                    SmartLog.i(TAG, "No data.");
                    mFilterItemAdapter.setSelectPosition(-1);
                    mCutContentList.clear();
                    mFilterItemAdapter.notifyDataSetChanged();
                    if (mCurrentPage == 0) {
                        mLoadingLayout.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        mErrorTv.setText(getString(R.string.result_empty));
                        mErrorTv.setVisibility(View.VISIBLE);
                        showSeekBar(false);
                    }
                }
            }
        });

        mFilterPanelViewModel.getPageData().observe(getViewLifecycleOwner(), list -> {
            if (mCurrentPage == 0) {
                mLoadingLayout.setVisibility(View.GONE);
                mIndicatorView.hide();
                mCutContentList.clear();
                mFilterItemAdapter.notifyDataSetChanged();
            }
            if (!mCutContentList.containsAll(list)) {
                int sizeOne = mCutContentList.size();
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                mCutContentList.addAll(list);
                int sizeTwo = mCutContentList.size();
                mFilterItemAdapter.notifyItemRangeChanged(sizeOne, sizeTwo);
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
            }
            getSelected();
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
                        if (mCurrentIndex > 0) {
                            mCurrentPage++;
                            mFilterPanelViewModel.loadMaterials(mCutContentList.get(mCurrentIndex).getId(),
                                mCurrentPage);
                        }
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
                    if (firstPosition != -1 && visibleItemCount > 0 && !isFirst && mCutContentList.size() > 1) {
                        isFirst = true;
                        SmartLog.w(TAG, "HianalyticsEvent10007 postEvent");
                    }
                }
            }
        });

        mFilterItemAdapter.setOnItemClickListener(new FilterItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int dataPosition) {
                if (dataPosition >= mCutContentList.size()) {
                    return;
                }

                showSeekBar(true);
                mFilterCancelRl.setSelected(false);
                CloudMaterialBean content = mCutContentList.get(dataPosition);
                int mSelectPosition = mFilterItemAdapter.getSelectPosition();
                mSelectedMaterialsCutContent = content;
                if (mSelectPosition == position) {
                    mFilterItemAdapter.setSelectPosition(-1);
                    mFilterItemAdapter.notifyItemChanged(position);
                }
                if (mSelectPosition != position) {
                    mFilterItemAdapter.setSelectPosition(position);
                    if (mSelectPosition != -1) {
                        mFilterItemAdapter.notifyItemChanged(mSelectPosition);
                    }
                    mFilterItemAdapter.notifyItemChanged(position);
                    if (content == null) {
                        return;
                    }
                    mSelectPath = content.getLocalPath();
                    mSelectId = content.getId();
                    mSelectStrength = (float) mProgress / (float) 100;
                    HVEEffect effect = null;
                    if (filterDataMap.get(FILTER_CHANGE) != null) {
                        effect = filterDataMap.get(FILTER_CHANGE).getEffect();
                    }

                    if (filterDataMap.get(FILTER_LAST) != null) {
                        effectStartTime = filterDataMap.get(FILTER_LAST).getEffect().getStartTime();
                        effectEndTime = filterDataMap.get(FILTER_LAST).getEffect().getEndTime();
                    } else {
                        effectStartTime = 0;
                        effectEndTime = 0;
                    }

                    if (TextUtils.isEmpty(mSelectPath)) {
                        return;
                    }

                    mAlreadyApplyAll = false;
                    mSelectName = content.getName();

                    mSeekBar.setProgress(mProgress);
                    mSeekBar.invalidate();

                    if (effect == null || isAsset) {
                        effect = mFilterPanelViewModel.disPlayFilter(mEditPreviewViewModel, mEditor, visibleAsset,
                            mSelectName, mSelectPath, mSelectId, mSelectStrength, effectStartTime, effectEndTime,
                            mAlreadyApplyAll, isAsset);
                    } else {
                        effect = mFilterPanelViewModel.replaceFilter(mEditPreviewViewModel, effect, mSelectName,
                            mSelectPath, mSelectId, mSelectStrength);
                    }

                    if (effect == null) {
                        return;
                    }
                    filterDataMap.put(FILTER_CHANGE, new FilterData(mSelectName, mSelectPath, mSelectId, effect,
                        effect.getStartTime(), effect.getEndTime(), effect.getFloatVal(HVEEffect.FILTER_STRENTH_KEY)));

                }

            }

            @Override
            public void onDownloadClick(int position, int dataPosition) {
                if (dataPosition >= mCutContentList.size()) {
                    return;
                }

                int previousPosition = mFilterItemAdapter.getSelectPosition();
                mFilterItemAdapter.setSelectPosition(position);
                CloudMaterialBean content = mCutContentList.get(dataPosition);

                mFilterItemAdapter.addDownloadMaterial(content);
                mFilterPanelViewModel.downloadColumn(previousPosition, position, dataPosition, content);
            }
        });

        mFilterPanelViewModel.getDownloadSuccess().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "getDownloadSuccess");
            mFilterItemAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            int downloadPosition = downloadInfo.getPosition();
            if (downloadPosition >= 0 && (downloadInfo.getDataPosition() < mCutContentList.size()
                && downloadInfo.getContentId().equals(mCutContentList.get(downloadInfo.getDataPosition()).getId()))) {
                mFilterCancelRl.setSelected(false);
                mFilterItemAdapter.setSelectPosition(downloadPosition);
                mCutContentList.set(downloadInfo.getDataPosition(), downloadInfo.getMaterialBean());
                mSelectedMaterialsCutContent = downloadInfo.getMaterialBean();
                mFilterItemAdapter.notifyDataSetChanged();

                if (downloadPosition == mFilterItemAdapter.getSelectPosition()) {
                    mSeekBar.setProgress(mProgress);
                    mSeekBar.invalidate();
                    mSelectPath = downloadInfo.getMaterialBean().getLocalPath();
                    mSelectName = downloadInfo.getMaterialBean().getName();
                    mSelectId = downloadInfo.getMaterialBean().getId();
                    mSelectStrength = (float) mProgress / (float) 100;

                    HVEEffect effect = null;
                    if (filterDataMap.get(FILTER_CHANGE) != null) {
                        effect = filterDataMap.get(FILTER_CHANGE).getEffect();
                    }

                    if (filterDataMap.get(FILTER_LAST) != null) {
                        effectStartTime = filterDataMap.get(FILTER_LAST).getEffect().getStartTime();
                        effectEndTime = filterDataMap.get(FILTER_LAST).getEffect().getEndTime();
                    } else {
                        effectStartTime = 0;
                        effectEndTime = 0;
                    }

                    if (TextUtils.isEmpty(mSelectPath)) {
                        return;
                    }

                    mAlreadyApplyAll = false;

                    if (effect == null || isAsset) {
                        effect = mFilterPanelViewModel.disPlayFilter(mEditPreviewViewModel, mEditor, visibleAsset,
                            mSelectName, mSelectPath, mSelectId, mSelectStrength, effectStartTime, effectEndTime,
                            mAlreadyApplyAll, isAsset);
                    } else {
                        effect = mFilterPanelViewModel.replaceFilter(mEditPreviewViewModel, effect, mSelectName,
                            mSelectPath, mSelectId, mSelectStrength);
                    }

                    if (effect != null) {
                        filterDataMap.put(FILTER_CHANGE,
                            new FilterData(mSelectName, mSelectPath, mSelectId, effect, effect.getStartTime(),
                                effect.getEndTime(), effect.getFloatVal(HVEEffect.FILTER_STRENTH_KEY)));
                    }

                    showSeekBar(true);
                }
            }

        });

        mFilterPanelViewModel.getDownloadProgress().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "progress:" + downloadInfo.getProgress());
            updateProgress(downloadInfo);
        });

        mFilterPanelViewModel.getDownloadFail().observe(this, downloadInfo -> {
            mFilterItemAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            cancelProgress(downloadInfo);
            if (mActivity != null) {
                ToastWrapper.makeText(mActivity, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
            }
        });

        mFilterPanelViewModel.getBoundaryPageData().observe(this, aBoolean -> {
            mHasNextPage = aBoolean;
        });

        mCertainIv.setOnClickListener(new OnClickRepeatedListener(v -> {
            HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
            mActivity.onBackPressed();
        }));

    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    private void showSeekBar(boolean show) {
        if (show) {
            mSeekBar.setVisibility(View.VISIBLE);
        } else {
            mSeekBar.setVisibility(View.INVISIBLE);
        }
    }

    private void updateProgress(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        if (downloadPosition >= 0 && downloadInfo.getDataPosition() < mCutContentList.size()
            && downloadInfo.getContentId().equals(mCutContentList.get(downloadInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder =
                (RViewHolder) mRecyclerView.findViewHolderForAdapterPosition(downloadInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mDownloadPb = viewHolder.itemView.findViewById(R.id.item_progress);
                mDownloadPb.setProgress(downloadInfo.getProgress());
            }
        }
    }

    private void cancelProgress(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        if (downloadPosition >= 0 && downloadInfo.getDataPosition() < mCutContentList.size()
            && downloadInfo.getContentId().equals(mCutContentList.get(downloadInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder =
                (RViewHolder) mRecyclerView.findViewHolderForAdapterPosition(downloadInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mDownloadPb = viewHolder.itemView.findViewById(R.id.item_progress);
                mDownloadPb.setVisibility(View.GONE);
                ImageView mDownloadIv = viewHolder.itemView.findViewById(R.id.item_download_view);
                mDownloadIv.setVisibility(View.VISIBLE);
            }
        }
    }

    private void refreshProgress() {
        if (filterDataMap.get(FILTER_CHANGE) != null) {
            HVEEffect effect = filterDataMap.get(FILTER_CHANGE).getEffect();
            mEditPreviewViewModel.refreshFilterFloatVal(effect, (float) mProgress / (float) 100);
        }
    }

    @Override
    public void onBackPressed() {
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        }
        if (mEditPreviewViewModel != null) {
            mEditPreviewViewModel.destroyTimeoutManager();
            mEditPreviewViewModel.pause();
        }
        if (mEditor != null && mEditor.getTimeLine() != null) {
            long currentTime = mEditor.getTimeLine().getCurrentTime();
            mEditor.refresh(currentTime);
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

        if (mSelectedMaterialsCutContent != null) {
            mSelectedMaterialsCutContent = null;
        }
    }

    private void getSelected() {
        showSeekBar(false);
        if (filterDataMap.get(FILTER_CHANGE) == null) {
            return;
        }
        for (int i = 0; i < mCutContentList.size(); i++) {
            if (filterDataMap.get(FILTER_CHANGE).getEffectId().equals(mCutContentList.get(i).getId())) {
                mFilterItemAdapter.setSelectPosition(i + mFilterItemAdapter.getHeaderCount());
                mFilterItemAdapter.notifyItemChanged(i + mFilterItemAdapter.getHeaderCount());
                mFilterCancelRl.setSelected(false);
                showSeekBar(true);
                if (filterDataMap.get(FILTER_CHANGE) != null) {
                    int progress = (int) (filterDataMap.get(FILTER_CHANGE).getStrength() * 100);
                    mSeekBar.setProgress(progress);
                }
            }
        }
        mRecyclerView.setVisibility(View.VISIBLE);
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
    public void onPause() {
        super.onPause();
        isFirst = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirst = false;
    }
}
