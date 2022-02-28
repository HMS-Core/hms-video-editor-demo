
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

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.audio.AudioColumnView;
import com.huawei.hms.videoeditor.ui.common.view.decoration.DividerLine;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.mediaeditor.audio.adapter.SoundEffectItemAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.audio.viewmodel.SoundEffectItemViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.audio.viewmodel.SoundEffectViewModel;
import com.huawei.secure.android.common.intent.SafeBundle;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SoundEffectItemFragment extends BaseFragment
    implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "SoundEffectItemFragment";

    private ConstraintLayout mLoadingLayout;

    private LoadingIndicatorView mIndicatorView;

    private TextView mErrorTv;

    private RelativeLayout mErrorLayout;

    private RecyclerView mRecyclerView;

    private SoundEffectViewModel mSoundEffectViewModel;

    private SoundEffectItemViewModel mSoundEffectItemViewModel;

    private MediaPlayer mMediaPlayer;

    private CloudMaterialBean materialsCutContent = new CloudMaterialBean();

    private boolean mHasNextPage = false;

    private int mCurrentPage = 0;

    private SoundEffectItemAdapter mSoundEffectItemAdapter;

    private List<CloudMaterialBean> mList;

    private int mCurrentPosition = -1;

    private boolean isScrolled = false;

    private long startTimeDiff;

    private boolean isFirst;

    public static SoundEffectItemFragment newInstance(HVEColumnInfo materialsCutContent) {
        Bundle args = new Bundle();
        args.putString("columnId", materialsCutContent.getColumnId());
        SoundEffectItemFragment fragment = new SoundEffectItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_add_sound_effect_page;
    }

    @Override
    protected void initView(View view) {
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mIndicatorView = view.findViewById(R.id.indicator);
        mErrorTv = view.findViewById(R.id.error_text);
        mErrorLayout = view.findViewById(R.id.error_layout);
        mRecyclerView = view.findViewById(R.id.pager_recycler_view);
    }

    @Override
    protected void initObject() {
        SafeBundle safeBundle = new SafeBundle(getArguments());
        materialsCutContent.setId(safeBundle.getString("columnId"));
        mSoundEffectViewModel =
            new ViewModelProvider(requireParentFragment(), mFactory).get(SoundEffectViewModel.class);
        mSoundEffectItemViewModel = new ViewModelProvider(this, mFactory).get(SoundEffectItemViewModel.class);
        mList = new ArrayList<>();
        mSoundEffectItemAdapter = new SoundEffectItemAdapter(context, mList, R.layout.adapter_sound_effect_item);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        mRecyclerView.addItemDecoration(DividerLine.getLine(context, 16, R.color.color_20));
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mSoundEffectItemAdapter);
        initMediaPlayer();
    }

    @Override
    protected void initData() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mIndicatorView.show();
        mSoundEffectItemViewModel.loadMaterials(materialsCutContent.getId(), mCurrentPage);
        mSoundEffectItemViewModel.getPageData().observe(this, materialsCutContents -> {
            if (mCurrentPage == 0) {
                mLoadingLayout.setVisibility(View.GONE);
                mIndicatorView.hide();
                mList.clear();
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        setAdapterPlayOrStop(mCurrentPosition, false);
                    }
                }
            }
            if (!mList.containsAll(materialsCutContents)) {
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                mList.addAll(materialsCutContents);
                mSoundEffectItemAdapter.notifyDataSetChanged();
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
            }
        });

        mSoundEffectItemViewModel.getErrorString().observe(this, errorMessage -> {
            if (!TextUtils.isEmpty(errorMessage) && mList.size() == 0) {
                mErrorTv.setText(errorMessage);
                mErrorLayout.setVisibility(View.VISIBLE);
                mLoadingLayout.setVisibility(View.GONE);
            }
        });

        mSoundEffectItemViewModel.getEmptyString().observe(this, emptyString -> {
            if (mCurrentPage == 0) {
                mLoadingLayout.setVisibility(View.GONE);
                mIndicatorView.hide();
            }
            ToastWrapper.makeText(mActivity, emptyString, Toast.LENGTH_SHORT).show();
        });

        mSoundEffectItemViewModel.getBoundaryPageData().observe(this, aBoolean -> {
            mHasNextPage = aBoolean;
        });

        mErrorLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            mErrorLayout.setVisibility(View.GONE);
            mLoadingLayout.setVisibility(View.VISIBLE);
            mSoundEffectItemViewModel.loadMaterials(materialsCutContent.getId(), mCurrentPage);
        }));
    }

    @Override
    protected void initEvent() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE
                    && mSoundEffectItemAdapter.getItemCount() >= mSoundEffectItemAdapter.getOriginalItemCount()) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (!isScrolled && mHasNextPage && layoutManager != null) {
                        int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                        if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1) {
                            mCurrentPage++;
                            mSoundEffectItemViewModel.loadMaterials(materialsCutContent.getId(), mCurrentPage);
                            isScrolled = false;
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (mHasNextPage && layoutManager != null) {
                    int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1 && dy > 0) {
                        mCurrentPage++;
                        mSoundEffectItemViewModel.loadMaterials(materialsCutContent.getId(), mCurrentPage);
                        isScrolled = true;
                    }
                }

                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int firstPosition = layoutManager.findFirstVisibleItemPosition();
                    if (firstPosition != -1 && visibleItemCount > 0 && !isFirst && mList.size() > 0) {
                        isFirst = true;
                        SmartLog.w(TAG, "HianalyticsEvent10007 postEvent");
                    }
                }
            }
        });

        mSoundEffectItemAdapter.setOnItemClickListener(new SoundEffectItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int dataPosition) {
                int mSelectPosition = mSoundEffectItemAdapter.getSelectPosition();
                if (mSelectPosition != position) {
                    mSoundEffectItemAdapter.setSelectPosition(position);
                    if (mSelectPosition != -1) {
                        mSoundEffectItemAdapter.notifyItemChanged(mSelectPosition);
                    }
                    mSoundEffectItemAdapter.notifyItemChanged(position);
                    startOrStopAudio(dataPosition, mList.get(dataPosition));
                } else {
                    if (mMediaPlayer != null) {
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.pause();
                            setAdapterPlayOrStop(position, false);
                        } else {
                            mMediaPlayer.start();
                            setAdapterPlayOrStop(position, true);
                        }
                    }
                }
            }

            @Override
            public void onDownloadClick(int position, int dataPosition) {
                startTimeDiff = System.currentTimeMillis();
                int previousPosition = mSoundEffectItemAdapter.getSelectPosition();
                mSoundEffectItemAdapter.setSelectPosition(position);
                CloudMaterialBean content = mList.get(dataPosition);
                mSoundEffectItemAdapter.addDownloadMaterial(content);
                mSoundEffectItemViewModel.downloadColumn(previousPosition, position, content);
            }

            @Override
            public void onUseClick(int position, int dataPosition) {
                mSoundEffectViewModel.setSelectCutContent(mList.get(dataPosition));
            }
        });

        mSoundEffectItemViewModel.getDownloadProgress().observe(this, downloadInfo -> {
            SmartLog.i(TAG, "progress:" + downloadInfo.getProgress());
            updateProgress(downloadInfo);
        });

        mSoundEffectItemViewModel.getDownloadSuccess().observe(this, downloadInfo -> {
            SmartLog.i(TAG, "success:" + downloadInfo.getMaterialBean().getLocalPath());
            mSoundEffectItemAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            int downloadPosition = downloadInfo.getPosition();
            if (downloadPosition >= 0 && downloadInfo.getDataPosition() < mList.size()
                && downloadInfo.getContentId().equals(mList.get(downloadInfo.getDataPosition()).getId())) {
                mList.set(downloadInfo.getDataPosition(), downloadInfo.getMaterialBean());
                mSoundEffectItemAdapter.notifyDataSetChanged();
                if (downloadPosition == mSoundEffectItemAdapter.getSelectPosition()) {
                    startOrStopAudio(downloadInfo.getDataPosition(), downloadInfo.getMaterialBean());
                }
            }
        });

        mSoundEffectItemViewModel.getDownloadFail().observe(this, downloadInfo -> {
            mSoundEffectItemAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            updateFail(downloadInfo);
            ToastWrapper.makeText(mActivity, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
        });

        mSoundEffectItemViewModel.getBoundaryPageData().observe(this, aBoolean -> {
            mHasNextPage = aBoolean;
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    private void updateFail(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        int dataPosition = downloadInfo.getDataPosition();
        if (downloadPosition >= 0 && dataPosition < mList.size()
            && downloadInfo.getContentId().equals(mList.get(dataPosition).getId())) {
            mList.set(dataPosition, downloadInfo.getMaterialBean());
            mSoundEffectItemAdapter.notifyItemChanged(downloadPosition);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateProgress(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        if (downloadPosition >= 0 && downloadInfo.getDataPosition() < mList.size()
            && downloadInfo.getContentId().equals(mList.get(downloadInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder = (RViewHolder) mRecyclerView.findViewHolderForAdapterPosition(downloadPosition);
            if (viewHolder != null) {
                ProgressBar mDownloadPb = viewHolder.itemView.findViewById(R.id.progress_bar);
                TextView mDownloadPbTv = viewHolder.itemView.findViewById(R.id.progress_value_text);
                mDownloadPb.setProgress(downloadInfo.getProgress());
                mDownloadPbTv.setText(downloadInfo.getProgress() + "%");
            }
        }
    }

    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            setAdapterPlayOrStop(mCurrentPosition, false);
        }
        isFirst = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFirst = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void startOrStopAudio(int position, CloudMaterialBean item) {
        if (mCurrentPosition != position) {
            resetMediaPlayer(item.getLocalPath());
            mCurrentPosition = position;
        } else {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    setAdapterPlayOrStop(position, false);
                } else {
                    mMediaPlayer.start();
                    setAdapterPlayOrStop(position, true);
                }
            }
        }
    }

    private void setAdapterPlayOrStop(int position, boolean isPlay) {
        if (position >= 0 && position < mList.size()) {
            RViewHolder viewHolder = (RViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
                AudioColumnView columnView = viewHolder.itemView.findViewById(R.id.audio_column_view);
                if (isPlay) {
                    columnView.start();
                } else {
                    columnView.stop();
                }
            }
        }
    }

    private void resetMediaPlayer(String path) {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepareAsync();
            }
        } catch (RuntimeException e) {
            SmartLog.e(TAG, "prepare fail RuntimeException");
        } catch (Exception e) {
            SmartLog.e(TAG, "prepare fail Exception");
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        int mSelectPosition = mSoundEffectItemAdapter.getSelectPosition();
        mSoundEffectItemAdapter.setSelectPosition(-1);
        mSoundEffectItemAdapter.notifyItemChanged(mSelectPosition);
        mCurrentPosition = -1;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            setAdapterPlayOrStop(mCurrentPosition, true);
        }
    }
}
