
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

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.adapter.MaskEffectAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.MaskEffectOnlineViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.MaskEffectViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.CycleMask;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.FlowerMask;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.HeartMask;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.LineMask;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.ParallelMask;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.RoundRectMask;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.StarMask;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.secure.android.common.intent.SafeBundle;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MaskEffectFragment extends BaseFragment {
    private static final String TAG = "MaskEffectFragment";

    private RecyclerView mRecyclerView;

    private MaskEffectAdapter maskEffectAdapter;

    private MaskEffectViewModel viewModel;

    protected EditPreviewViewModel mEditPreviewViewModel;

    private MaskEffectOnlineViewModel maskEffectOnlineViewModel;

    private boolean isMain = true;

    private ImageView ivCertain;

    private LinearLayout mLayoutReverse;

    private boolean isInverse = false;

    private LoadingIndicatorView mIndicatorView;

    private RelativeLayout mErrorLayout;

    private TextView mErrorTv;

    private List<HVEColumnInfo> currentColumnList;

    private List<CloudMaterialBean> currentContentList;

    private View mFilterCancelRl;

    private int mCurrentIndex = 0;

    private int mCurrentPage = 0;

    private Boolean mHasNextPage = false;

    private String currentSelectPath;

    private String currentSelectedName;

    private int currentSelectPosition = -1;

    private boolean isFirst;

    private CloudMaterialBean mMaterialsCutContent;

    private CloudMaterialBean mContent;

    public static MaskEffectFragment newInstance(boolean flag) {
        MaskEffectFragment fragment = new MaskEffectFragment();
        Bundle args = new Bundle();
        args.putBoolean("isMain", flag);
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
        return R.layout.fragment_maskeffect;
    }

    @Override
    protected void initView(View view) {
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        maskEffectOnlineViewModel = new ViewModelProvider(mActivity, mFactory).get(MaskEffectOnlineViewModel.class);
        viewModel = new ViewModelProvider(mActivity, mFactory).get(MaskEffectViewModel.class);
        ((TextView) view.findViewById(R.id.tv_title)).setText(getString(R.string.mask));
        mRecyclerView = view.findViewById(R.id.recycler_view_mask_effect);
        ivCertain = view.findViewById(R.id.iv_certain);
        mLayoutReverse = view.findViewById(R.id.layout_reverse);
        mErrorLayout = view.findViewById(R.id.error_layout);
        mErrorTv = view.findViewById(R.id.error_text);
        mIndicatorView = view.findViewById(R.id.indicator);
        ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchListener);
    }

    @Override
    protected void initObject() {
        SafeBundle safeBundle = new SafeBundle(getArguments());
        this.isMain = safeBundle.getBoolean("isMain");

        setTimeoutEnable();
        View mCancelHeaderView = LayoutInflater.from(mActivity).inflate(R.layout.adapter_add_mask_header, null, false);
        mCancelHeaderView.setLayoutParams(
            new RelativeLayout.LayoutParams(SizeUtils.dp2Px(context, 58), SizeUtils.dp2Px(context, 75)));

        mFilterCancelRl = mCancelHeaderView.findViewById(R.id.rl_cancel_mask_header);
        mFilterCancelRl.setSelected(true);
        currentColumnList = new ArrayList<>();
        currentContentList = new ArrayList<>();
        mIndicatorView.setVisibility(View.VISIBLE);
        mIndicatorView.show();
        maskEffectOnlineViewModel.initColumns();
        maskEffectOnlineViewModel.getColumns().observe(getViewLifecycleOwner(), list -> {
            if (list != null && list.size() > 0) {
                currentColumnList.clear();
                currentColumnList.addAll(list);
                mIndicatorView.setVisibility(View.GONE);
                mIndicatorView.hide();
                mCurrentIndex = 0;
                HVEColumnInfo content = currentColumnList.get(mCurrentIndex);
                currentContentList.clear();
                maskEffectOnlineViewModel.loadMaterials(content.getColumnId(), mCurrentPage);
            }
        });
        maskEffectAdapter = new MaskEffectAdapter(mActivity, currentContentList, R.layout.adapter_add_mask_effect_item);
        if (mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView
                .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.color_20),
                    SizeUtils.dp2Px(mActivity, 75), SizeUtils.dp2Px(mActivity, 8)));
        }
        mRecyclerView.setItemAnimator(null);
        maskEffectAdapter.addHeaderView(mCancelHeaderView);
        mRecyclerView.setAdapter(maskEffectAdapter);
    }

    @Override
    protected void initData() {
        ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchSTListener);
    }

    @Override
    protected void initEvent() {
        mEditPreviewViewModel.getTimeout()
            .observe(getViewLifecycleOwner(), isTimeout -> {
                if (isTimeout && !isBackground) {
                    mActivity.onBackPressed();
                }
            });
        maskEffectOnlineViewModel.getMaskErrorString().observe(this, errorString -> {
            if (!TextUtils.isEmpty(errorString) && currentContentList.size() == 0) {
                mIndicatorView.setVisibility(View.GONE);
                mIndicatorView.hide();
                mErrorTv.setText(errorString);
                mErrorLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mLayoutReverse.setVisibility(View.INVISIBLE);
                maskEffectAdapter.setSelectPosition(-1);
                currentContentList.clear();
                maskEffectAdapter.notifyDataSetChanged();
            }
        });

        maskEffectOnlineViewModel.getMaskEmptyString().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (TextUtils.isEmpty(s)) {
                    SmartLog.i(TAG, "No data.");
                    maskEffectAdapter.setSelectPosition(-1);
                    currentContentList.clear();
                    maskEffectAdapter.notifyDataSetChanged();
                    if (mCurrentPage == 0) {
                        mIndicatorView.setVisibility(View.GONE);
                        mIndicatorView.hide();
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLayoutReverse.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        maskEffectOnlineViewModel.getPageData().observe(getViewLifecycleOwner(), list -> {
            if (mCurrentPage == 0) {
                mIndicatorView.setVisibility(View.GONE);
                mIndicatorView.hide();
                currentContentList.clear();
            }
            if (!currentContentList.containsAll(list)) {
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                currentContentList.addAll(list);
                if (!TextUtils.isEmpty(currentSelectedName)) {
                    for (int i = 0; i < currentContentList.size(); i++) {
                        if (currentSelectedName.equals(currentContentList.get(i).getName())) {
                            currentSelectPosition = i + 1;
                            maskEffectAdapter.setSelectPosition(currentSelectPosition);
                            mFilterCancelRl.setSelected(false);
                        }
                    }
                }
                maskEffectAdapter.notifyDataSetChanged();
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
            }
            mErrorLayout.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mLayoutReverse.setVisibility(View.VISIBLE);
        });

        maskEffectOnlineViewModel.getMaskPageData().observe(this, aBoolean -> {
            mHasNextPage = aBoolean;
        });

        maskEffectOnlineViewModel.getDownloadSuccess().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "getDownloadSuccess");
            maskEffectAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            int downloadPosition = downloadInfo.getPosition();
            if (downloadPosition >= 0
                && (downloadInfo.getDataPosition() < currentContentList.size() && downloadInfo.getContentId()
                    .equals(currentContentList.get(downloadInfo.getDataPosition()).getId()))) {
                mFilterCancelRl.setSelected(false);
                maskEffectAdapter.setSelectPosition(downloadPosition);
                CloudMaterialBean materialsCutContent = downloadInfo.getMaterialBean();
                currentContentList.set(downloadInfo.getDataPosition(), materialsCutContent);
                maskEffectAdapter.notifyDataSetChanged();

                if (downloadPosition == maskEffectAdapter.getSelectPosition()) {
                    currentSelectPath = materialsCutContent.getLocalPath();
                    currentSelectedName = materialsCutContent.getName();
                    if (TextUtils.isEmpty(currentSelectPath)) {
                        return;
                    }
                    selectResource(materialsCutContent);
                }
            }

        });

        maskEffectOnlineViewModel.getDownloadProgress().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "progress:" + downloadInfo.getProgress());
            updateProgress(downloadInfo);
        });

        maskEffectOnlineViewModel.getDownloadFail().observe(this, downloadInfo -> {
            maskEffectAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            cancelProgress(downloadInfo);
            ToastWrapper.makeText(mActivity, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();

        });

        mErrorLayout.setOnClickListener(v -> {
            if (mCurrentPage == 0) {
                mErrorLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                mLayoutReverse.setVisibility(View.GONE);
                mIndicatorView.setVisibility(View.VISIBLE);
                mIndicatorView.show();
            }
            if (currentColumnList.size() > 0) {
                HVEColumnInfo content = currentColumnList.get(mCurrentIndex);
                mCurrentPage = 0;
                maskEffectOnlineViewModel.loadMaterials(content.getColumnId(), mCurrentPage);
            } else {
                maskEffectOnlineViewModel.initColumns();
            }
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
                            maskEffectOnlineViewModel.loadMaterials(currentContentList.get(mCurrentIndex).getId(),
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
                    if (firstPosition != -1 && visibleItemCount > 0 && !isFirst && currentContentList.size() > 0) {
                        isFirst = true;
                        for (int i = 0; i < visibleItemCount - 1; i++) {
                            CloudMaterialBean cutContent = currentContentList.get(i);
                            maskEffectAdapter.addFirstScreenMaterial(cutContent);
                        }
                    }
                }
            }
        });

        maskEffectAdapter.setOnItemClickListener(new MaskEffectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int dataPosition) {
                mFilterCancelRl.setSelected(false);
                int mSelectPosition = maskEffectAdapter.getSelectPosition();
                if (mSelectPosition != position) {
                    maskEffectAdapter.setSelectPosition(position);
                    if (mSelectPosition != -1) {
                        maskEffectAdapter.notifyItemChanged(mSelectPosition);
                    }
                    maskEffectAdapter.notifyItemChanged(position);
                    mMaterialsCutContent = currentContentList.get(dataPosition);
                    if (mMaterialsCutContent == null) {
                        return;
                    }
                    currentSelectPath = mMaterialsCutContent.getLocalPath();
                    if (TextUtils.isEmpty(currentSelectPath)) {
                        return;
                    }
                    currentSelectedName = mMaterialsCutContent.getName();
                    if (TextUtils.isEmpty(currentSelectedName)) {
                        viewModel.setMaterialsCutContentMutableLiveData(null);
                        viewModel.setShapeClass((Class) null);
                    } else {
                        selectResource(mMaterialsCutContent);
                    }
                }

            }

            @Override
            public void onDownloadClick(int position, int dataPosition) {
                int previousPosition = maskEffectAdapter.getSelectPosition();
                maskEffectAdapter.setSelectPosition(position);
                mContent = currentContentList.get(dataPosition);

                maskEffectAdapter.addDownloadMaterial(mContent);
                maskEffectOnlineViewModel.downloadColumn(previousPosition, position, dataPosition, mContent);

            }
        });

        mFilterCancelRl.setOnClickListener(v -> {
            mFilterCancelRl.setContentDescription(getString(R.string.no_filter));
            mFilterCancelRl.setSelected(true);
            if (mFilterCancelRl.isSelected()) {
                int mSelectPosition = maskEffectAdapter.getSelectPosition();
                maskEffectAdapter.setSelectPosition(-1);
                if (mSelectPosition != -1) {
                    maskEffectAdapter.notifyItemChanged(mSelectPosition);
                }
                currentSelectedName = null;
                currentSelectPath = null;
                viewModel.setShapeClass(null);
                viewModel.setMaterialsCutContentMutableLiveData(null);
                viewModel.removeCurrentEffect();
            }
        });

        mLayoutReverse.setOnClickListener(new OnClickRepeatedListener(v -> {
            isInverse = !isInverse;
            viewModel.setIsInverse(isInverse);
        }));

        ivCertain.setOnClickListener(new OnClickRepeatedListener(v -> {
            mActivity.onBackPressed();
        }));
    }

    @Override
    protected int setViewLayoutEvent() {
        return FIXED_HEIGHT_210;
    }

    @Override
    public void onBackPressed() {
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        }
        if (viewModel != null) {
            viewModel.setIsShow(false);
            viewModel.refresh();
        }
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewModelProvider.AndroidViewModelFactory mFactory =
            new ViewModelProvider.AndroidViewModelFactory(mActivity.getApplication());
        viewModel = new ViewModelProvider(mActivity, mFactory).get(MaskEffectViewModel.class);
        viewModel.setIsShow(true);
        viewModel.getHveVideoAsset().observe(this, new AssetObserver());
        if (isMain) {
            viewModel.getMainAsset();
        }
        HVEAsset assetValue = viewModel.getHveVideoAsset().getValue();
        if (assetValue == null) {
            return;
        }
        List<HVEEffect> hveEffects = assetValue.getEffectsWithType(HVEEffect.HVEEffectType.MASK);
        if (hveEffects.isEmpty()) {
            return;
        }

        HVEEffect effect = hveEffects.get(0);
        HVEEffect.Options options = effect.getOptions();
        if (options == null) {
            return;
        }

        currentSelectedName = options.getEffectName();
        if (currentContentList != null && !currentContentList.isEmpty()) {
            if (!TextUtils.isEmpty(currentSelectedName)) {
                for (int i = 0; i < currentContentList.size(); i++) {
                    if (currentSelectedName.equals(currentContentList.get(i).getName())) {
                        currentSelectPosition = i;
                        maskEffectAdapter.setSelectPosition(currentSelectPosition);
                        mFilterCancelRl.setSelected(false);
                    }
                }
            }
            maskEffectAdapter.notifyDataSetChanged();
        }
    }

    private void initSTTimeoutState() {
        mEditPreviewViewModel.initTimeoutManager();
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchSTListener = new VideoClipsActivity.TimeOutOnTouchListener() {
        @Override
        public boolean onTouch(MotionEvent ev) {
            initSTTimeoutState();
            return false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            viewModel.setIsShow(false);
        }
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchSTListener);
        }
        if (mEditPreviewViewModel != null) {
            mEditPreviewViewModel.destroyTimeoutManager();
        }
    }

    private class AssetObserver implements androidx.lifecycle.Observer<HVEAsset> {
        @Override
        public void onChanged(HVEAsset o) {
            List<HVEEffect> hveEffects = o.getEffectsWithType(HVEEffect.HVEEffectType.MASK);
            if (hveEffects.isEmpty()) {
                mFilterCancelRl.setSelected(true);
                int mSelectPosition = maskEffectAdapter.getSelectPosition();
                maskEffectAdapter.setSelectPosition(-1);
                if (mSelectPosition != -1) {
                    maskEffectAdapter.notifyItemChanged(mSelectPosition);
                }
                currentSelectedName = null;
                currentSelectPath = null;
                viewModel.setShapeClass(null);
                viewModel.setMaterialsCutContentMutableLiveData(null);
            } else {
                currentSelectedName = hveEffects.get(0).getOptions().getEffectName();
                if (currentContentList != null && !currentContentList.isEmpty()) {
                    if (!TextUtils.isEmpty(currentSelectedName)) {
                        for (int i = 0; i < currentContentList.size(); i++) {
                            if (currentSelectedName.equals(currentContentList.get(i).getName())) {
                                currentSelectPosition = i;
                                maskEffectAdapter.setSelectPosition(currentSelectPosition);
                                mFilterCancelRl.setSelected(false);
                            }
                        }
                    }
                    maskEffectAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchListener = new VideoClipsActivity.TimeOutOnTouchListener() {
        @Override
        public boolean onTouch(MotionEvent ev) {
            initTimeoutState();
            return false;
        }
    };

    private void updateProgress(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        if (downloadPosition >= 0 && downloadInfo.getDataPosition() < currentContentList.size()
            && downloadInfo.getContentId().equals(currentContentList.get(downloadInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder =
                (RViewHolder) mRecyclerView.findViewHolderForAdapterPosition(downloadInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mDownloadPb = viewHolder.itemView.findViewById(R.id.item_progress_mask_effect);
                mDownloadPb.setProgress(downloadInfo.getProgress());
            }
        }
    }

    private void cancelProgress(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        if (downloadPosition >= 0 && downloadInfo.getDataPosition() < currentContentList.size()
            && downloadInfo.getContentId().equals(currentContentList.get(downloadInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder =
                (RViewHolder) mRecyclerView.findViewHolderForAdapterPosition(downloadInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mDownloadPb = viewHolder.itemView.findViewById(R.id.item_progress_mask_effect);
                mDownloadPb.setVisibility(View.GONE);
                ImageView mDownloadIv = viewHolder.itemView.findViewById(R.id.item_download_view_mask_effect);
                mDownloadIv.setVisibility(View.VISIBLE);
            }
        }
    }

    private void selectResource(CloudMaterialBean materialsCutContent) {
        viewModel.setMaterialsCutContentMutableLiveData(materialsCutContent);
        viewModel
            .setFirstEffect(viewModel.appendHVEEffect(viewModel.getHveVideoAsset().getValue(), materialsCutContent));
        viewModel.appendHVEEffect(viewModel.getHveVideoAsset().getValue(), materialsCutContent);

        viewModel.refresh();
        HVEEffect effect = viewModel.getFirstEffect();
        if (effect == null) {
            return;
        }
        String effectName = effect.getEffectName();
        if (!TextUtils.isEmpty(effectName)) {
            switch (effectName) {
                case HVEEffect.MASK_SEMIPLANEMASK:
                    viewModel.setShapeClass(LineMask.class);
                    break;
                case HVEEffect.MASK_CYCLE:
                    viewModel.setShapeClass(CycleMask.class);
                    break;
                case HVEEffect.MASK_RECTANGLE:
                    viewModel.setShapeClass(RoundRectMask.class);
                    break;
                case HVEEffect.MASK_STIPEFFECT:
                    viewModel.setShapeClass(ParallelMask.class);
                    break;
                case HVEEffect.MASK_FLOWER:
                    viewModel.setShapeClass(FlowerMask.class);
                    break;
                case HVEEffect.MASK_HEART:
                    viewModel.setShapeClass(HeartMask.class);
                    break;
                case HVEEffect.MASK_STAR:
                    viewModel.setShapeClass(StarMask.class);
                    break;
                default:
                    SmartLog.i(TAG, "selectResource run in default case");
            }
        }
    }
}