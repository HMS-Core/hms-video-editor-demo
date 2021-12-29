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

package com.huawei.hms.videoeditor.ui.mediaeditor.crop;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVELane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.shot.VideoCropView;
import com.huawei.hms.videoeditor.ui.common.shot.VideoTrackView;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.VideoClipsPlayViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

public class AssetCropFragment extends BaseFragment {
    public static final String TAG = "AssetSplitFragment";

    private VideoTrackView videoTrackView;

    private EditPreviewViewModel mEditPreviewViewModel;

    private VideoClipsPlayViewModel mSdkPlayViewModel;

    private HVEAsset mHveAsset;

    private TextView tvTitle;

    private ImageView ivCertain;

    protected HVETimeLine mHVETimeLane;

    private boolean isCutSuccess;

    protected HuaweiVideoEditor mEditor;

    private VideoCropView mVideoCropView;

    public static AssetCropFragment newInstance(int operateId) {
        Bundle args = new Bundle();
        args.putInt("operateId", operateId);
        AssetCropFragment fragment = new AssetCropFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_asset_crop;
    }

    @Override
    protected void initView(View view) {
        videoTrackView = view.findViewById(R.id.rv_split);
        ivCertain = view.findViewById(R.id.iv_certain);
        mVideoCropView = view.findViewById(R.id.crop_video_view);
        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.cut_second_menu_trim);
        tvTitle.setTextColor(ContextCompat.getColor(mActivity, R.color.clip_color_E6FFFFFF));
    }

    @Override
    protected void initObject() {
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mSdkPlayViewModel = new ViewModelProvider(mActivity, mFactory).get(VideoClipsPlayViewModel.class);
        mEditor = viewModel.getEditor();
        mHVETimeLane = viewModel.getTimeLine();
        resetView();
    }

    @Override
    protected void initData() {
        mHveAsset = mEditPreviewViewModel.getMainLaneAsset();

        if (mHveAsset == null) {
            SmartLog.e(TAG, "SelectedAsset is null!");
            return;
        }
        mVideoCropView.init(mHveAsset.getDuration(), mHveAsset.getOriginLength(), mHveAsset.getTrimIn(),
            mHveAsset.getTrimOut());
        videoTrackView.setVideoAsset((HVEVideoAsset) mHveAsset);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initEvent() {
        ivCertain.setOnClickListener(new OnClickRepeatedListener(v -> {
            onBackPressed();
            MenuClickManager.getInstance().popView();
        }));

        mVideoCropView.setCutVideoListener(new VideoCropView.CutVideoResult() {
            @Override
            public void isInCut() {
                mEditor.pauseTimeLine();
            }

            @Override
            public void cut(long time) {

            }

            @Override
            public void cutResult(long cutTime, int isCanMove) {
                SmartLog.e(TAG, "mVideoCropView setCutVideoListener cutResult");
                handleCutEvent(cutTime, isCanMove == 0 ? 1 : -1);
                if (mHveAsset == null) {
                    return;
                }
                mSdkPlayViewModel.setVideoDuration(mHVETimeLane.getVideoLane(0).getDuration());
            }
        });
        mVideoCropView.setOnTouchListener((view, motionEvent) -> {
            if (mHveAsset instanceof HVEVideoAsset && mEditor != null) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        mEditor.pauseTimeLine();
                        break;
                    default:
                        SmartLog.i(TAG, "initEvent mVideoCropView OnTouch run in default case");
                }
            }
            return false;
        });
    }

    private void handleCutEvent(long time, int direction) {
        if (time == 0) {
            return;
        }
        if (time < 0) {
            if (direction == 1 && -time > mHveAsset.getTrimIn()) {
                time = -mHveAsset.getTrimIn();
            } else if (direction == -1 && -time > mHveAsset.getTrimOut()) {
                time = -mHveAsset.getTrimOut();
            }
        }
        cutVideo(mHveAsset, time, direction);
    }

    private void cutVideo(HVEAsset asset, Long time, int direction) {
        if (asset == null) {
            return;
        }
        if (mEditor == null || mEditor.getTimeLine() == null) {
            return;
        }
        cutAsset(mEditor.getTimeLine().getVideoLane(mHveAsset.getLaneIndex()), asset, time, direction);
    }

    private void cutAsset(HVELane lane, HVEAsset asset, Long time, int direction) {
        if (asset == null || lane == null) {
            return;
        }
        if (direction > 0) {
            isCutSuccess = lane.cutAsset(asset.getIndex(), time, HVELane.HVETrimType.TRIM_IN);
        } else {
            isCutSuccess = lane.cutAsset(asset.getIndex(), time, HVELane.HVETrimType.TRIM_OUT);
        }
        if (isCutSuccess) {
            mVideoCropView.resetFactor(0, 0, 0, mHveAsset.getTrimIn(), mHveAsset.getTrimOut(), mHveAsset.getDuration());
        }
    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    private void resetView() {
        if (mEditPreviewViewModel == null) {
            return;
        }
        mEditPreviewViewModel.updateTimeLine();
    }

    @Override
    protected void initViewModelObserve() {
    }
}
