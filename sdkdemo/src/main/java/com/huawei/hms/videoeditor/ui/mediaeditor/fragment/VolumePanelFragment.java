
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

package com.huawei.hms.videoeditor.ui.mediaeditor.fragment;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEAudioAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.VideoClipsPlayViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.MySeekBar;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.secure.android.common.intent.SafeBundle;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class VolumePanelFragment extends BaseFragment {
    private static final String TAG = "VolumePanelFragment";

    private static final String VIDEO_MAIN = "video_main";

    private static final int MAIN_VIDEO = 0;

    private static final int PIP_VIDEO = 1;

    private static final int AUDIO = 2;

    private TextView mTitleTv;

    private ImageView mCertainIv;

    private CheckBox other;

    private MySeekBar mSeekBar;

    protected EditPreviewViewModel mEditPreviewViewModel;

    private int mProgress = 100;

    private int assetType = 0;

    private TextView volume;

    private VideoClipsPlayViewModel mPlayViewModel;

    private String volumeStr;

    private boolean isMute;

    public static VolumePanelFragment newInstance(int id) {
        Bundle args = new Bundle();
        if (id == MainViewState.EDIT_VIDEO_OPERATION_VOLUME) {
            args.putInt(VIDEO_MAIN, MAIN_VIDEO);
        } else if (id == MainViewState.EDIT_PIP_OPERATION_VOLUME) {
            args.putInt(VIDEO_MAIN, PIP_VIDEO);
        } else if (id == MainViewState.EDIT_AUDIO_STATE_VOLUME) {
            args.putInt(VIDEO_MAIN, AUDIO);
        }
        VolumePanelFragment fragment = new VolumePanelFragment();
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
        return R.layout.fragment_panel_volume;
    }

    @Override
    public void initView(View view) {
        mTitleTv = view.findViewById(R.id.tv_title);
        mCertainIv = view.findViewById(R.id.iv_certain);
        mSeekBar = view.findViewById(R.id.sb_items);
        if (ScreenUtil.isRTL()) {
            mSeekBar.setScaleX(RTL_UI);
        } else {
            mSeekBar.setScaleX(LTR_UI);
        }
        other = view.findViewById(R.id.cb_apply);
        volume = view.findViewById(R.id.sb_items_detail);
    }

    @Override
    protected void initObject() {
        SafeBundle safeBundle = new SafeBundle(getArguments());
        assetType = safeBundle.getInt(VIDEO_MAIN);
        mTitleTv.setText(R.string.cut_second_menu_volume);
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mPlayViewModel = new ViewModelProvider(mActivity, mFactory).get(VideoClipsPlayViewModel.class);
        other.setVisibility(View.GONE);

        mSeekBar.setMinProgress(0);
        mSeekBar.setMaxProgress(200);
        mSeekBar.setAnchorProgress(0);
    }

    @Override
    protected void initData() {
        float mValue;
        if (assetType == MAIN_VIDEO) {
            mValue = getVideoVolume(true);
        } else if (assetType == PIP_VIDEO) {
            mValue = getVideoVolume(false);
        } else {
            mValue = getAudioVolume();
        }

        isMute = isMute();

        if (isMute) {
            mProgress = 0;
        } else {
            mProgress = (int) (mValue / 0.01f);
        }

        SmartLog.d(TAG + this.hashCode(), "initData mProgress is " + mProgress);
        mSeekBar.setProgress(mProgress);
        volume.setText(String.valueOf(mProgress));
    }

    private boolean isMute() {
        boolean isVideoMute = false;
        if (assetType == MAIN_VIDEO) {
            HVEVideoLane mainLane = EditorManager.getInstance().getMainLane();
            if (mainLane != null) {
                boolean muteState = mainLane.getMuteState();
                isVideoMute = muteState;
                SmartLog.i(TAG, "isMute = " + muteState);
            }
        }
        return isVideoMute;
    }

    @Override
    protected void initEvent() {
        mSeekBar.setOnProgressChangedListener(progress -> {
            mSeekBar.setContentDescription(getString(R.string.voice_current) + progress);
            mProgress = progress;
            volumeStr = String.valueOf(mProgress);
            volume.setText(String.valueOf(mProgress));
            mEditPreviewViewModel.setToastTime(String.valueOf(mProgress));
        });

        mSeekBar.setcTouchListener(isTouch -> {
            mEditPreviewViewModel.setToastTime(isTouch ? volumeStr : "");
        });
        mSeekBar.setcSeekBarListener(new MySeekBar.SeekBarListener() {
            @Override
            public void seekFinished() {
                float volumeValue = (float) BigDecimalUtils.mul2(mProgress, 0.01f, 2);
                if (assetType == MAIN_VIDEO) {
                    if (isMute) {
                        viewModel.setSoundTrack(false);
                    }
                    if (!adjustVideoVolume(volumeValue)) {
                        ToastWrapper.makeText(context, getString(R.string.novolume), Toast.LENGTH_SHORT).show();
                    }
                } else if (assetType == PIP_VIDEO) {
                    if (!adjustVideoVolume(volumeValue)) {
                        ToastWrapper.makeText(context, getString(R.string.novolume), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    adjustAudioVolume(volumeValue);
                }
            }
        });

        mCertainIv.setOnClickListener(new OnClickRepeatedListener(view -> {
            mActivity.onBackPressed();
        }));
    }

    @Override
    protected int setViewLayoutEvent() {
        return FIXED_HEIGHT_210;
    }

    public float getVideoVolume(boolean isMainVideo) {
        HVEAsset selectedAsset = null;
        if (isMainVideo) {
            selectedAsset = mEditPreviewViewModel.getSelectedAsset();
            if (selectedAsset == null) {
                selectedAsset = mEditPreviewViewModel.getMainLaneAsset();
            }
        } else {
            selectedAsset = mEditPreviewViewModel.getSelectedAsset();
        }

        if (selectedAsset == null || selectedAsset.getType() != HVEAsset.HVEAssetType.VIDEO) {
            return 0;
        }
        if (selectedAsset instanceof HVEVideoAsset) {
            return ((HVEVideoAsset) selectedAsset).getVolume();
        }
        return 0;
    }

    public float getAudioVolume() {
        HVEAsset audioAsset = mEditPreviewViewModel.getSelectedAsset();
        if (audioAsset == null || audioAsset.getType() != HVEAsset.HVEAssetType.AUDIO) {
            return 0;
        }
        return ((HVEAudioAsset) audioAsset).getVolume();
    }

    private boolean adjustVideoVolume(float mProgress) {
        if (mEditPreviewViewModel == null) {
            SmartLog.i(TAG, "mEditPreviewViewModel is null");
            return false;
        }
        HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (selectedAsset == null || selectedAsset.getType() != HVEAsset.HVEAssetType.VIDEO) {
            SmartLog.i(TAG, "selectedAsset is empty or selectedAsset is not a video");
            return false;
        }
        ((HVEVideoAsset) selectedAsset).setVolume(mProgress);
        mEditPreviewViewModel.refreshTrackView(selectedAsset.getUuid());
        EditorManager.getInstance().getEditor().playTimeLine(selectedAsset.getStartTime(), selectedAsset.getEndTime());
        return true;
    }

    private void adjustAudioVolume(float mProgress) {
        HVEAsset asset = viewModel.getSelectedAsset();
        if (asset == null || asset.getType() != HVEAsset.HVEAssetType.AUDIO) {
            return;
        }
        HVEAudioAsset hveAudioAsset = (HVEAudioAsset) asset;
        hveAudioAsset.setVolume(mProgress);
        mEditPreviewViewModel.refreshTrackView(hveAudioAsset.getUuid());
        EditorManager.getInstance().getEditor().playTimeLine(hveAudioAsset.getStartTime(), hveAudioAsset.getEndTime());
    }

    @Override
    public void onBackPressed() {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor == null) {
            return;
        }
        editor.pauseTimeLine();
    }
}
