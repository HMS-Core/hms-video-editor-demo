
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

package com.huawei.hms.videoeditor.ui.mediaeditor.menu;

import static com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor.TIMER_PLAY_PERIOD;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.persontrack.PersonTrackingViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class VideoClipsPlayFragment extends BaseFragment implements HuaweiVideoEditor.PlayCallback {
    private static final String TAG = "VideoClipsPlayFragment";

    private LinearLayout mSdkPreviewContainer;

    private LinearLayout mNoCoverLayout;

    private DefaultPlayControlView mDefaultPlayControlView;

    private FullScreenPlayControlView mFullScreenPlayControlView;

    private ConstraintLayout mLoadingLayout;

    private LoadingIndicatorView mIndicatorView;

    private TextView mAudioRecorderCaption;

    private TextView mToastTimeView;

    private EditPreviewViewModel mViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    private VideoClipsPlayViewModel mPlayViewModel;

    private EditPreviewViewModel mEditPreviewVieModel;

    private PersonTrackingViewModel mPersonTrackingViewModel;

    private long mCurrentTime = 0;

    private long mVideoDuration = 0;

    private boolean isPlayState = false;

    private boolean isShowLoadingView = false;

    private ToastWrapper mToastState;

    private boolean hasInitCover;

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.home_color_FF181818;
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentTime = savedInstanceState.getLong("mCurrentTime");
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_clips_play_layout;
    }

    @Override
    protected void initView(View view) {
        mSdkPreviewContainer = view.findViewById(R.id.video_content_layout);
        mNoCoverLayout = view.findViewById(R.id.no_cover_layout);
        mDefaultPlayControlView = view.findViewById(R.id.top_play_control_view);
        mFullScreenPlayControlView = view.findViewById(R.id.bottom_play_control_view);
        mAudioRecorderCaption = view.findViewById(R.id.tv_audio_recorder_caption);
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mIndicatorView = view.findViewById(R.id.indicator);
        mToastTimeView = view.findViewById(R.id.toast_time);
    }

    @Override
    protected void initObject() {
        mViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mPlayViewModel = new ViewModelProvider(mActivity, mFactory).get(VideoClipsPlayViewModel.class);
        mEditPreviewVieModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mMaterialEditViewModel = new ViewModelProvider(mActivity, mFactory).get(MaterialEditViewModel.class);
        mPersonTrackingViewModel = new ViewModelProvider(mActivity, mFactory).get(PersonTrackingViewModel.class);
        mDefaultPlayControlView.setVideoPlaying(false);
        mFullScreenPlayControlView.setVideoPlaying(false);
        mToastState = new ToastWrapper();
    }

    @Override
    protected void initData() {
        mPlayViewModel.getVideoDuration().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                mVideoDuration = time;
                mDefaultPlayControlView.setTotalTime(time);
                mFullScreenPlayControlView.setTotalTime(time);
            }
        });

        mPlayViewModel.getCurrentTime().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                if (time == -1) {
                    mCurrentTime = 0;
                    return;
                }
                mCurrentTime = time;
                mDefaultPlayControlView.updateRunningTime(time);
                mFullScreenPlayControlView.updateRunningTime(time);
            }
        });

        mPlayViewModel.getPlayState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mDefaultPlayControlView.setVideoPlaying(aBoolean);
                mFullScreenPlayControlView.setVideoPlaying(aBoolean);
                isPlayState = aBoolean;
            }
        });

        mPlayViewModel.getFullScreenState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null) {
                    return;
                }
                editor.setFullScreenMode(aBoolean);
                mDefaultPlayControlView.setVisibility(aBoolean ? View.GONE : View.VISIBLE);
                mFullScreenPlayControlView.setVisibility(!aBoolean ? View.GONE : View.VISIBLE);
                if (aBoolean) {
                    mFullScreenPlayControlView.refreshButtonIcon();
                }
            }
        });
        mPlayViewModel.getShowFrameAddDelete().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
            }
        });
        mEditPreviewVieModel.getRefreshRecorderCaption().observe(this, s -> {
            if (TextUtils.isEmpty(s)) {
                mAudioRecorderCaption.setText("");
                mAudioRecorderCaption.setVisibility(View.GONE);
            } else {
                mAudioRecorderCaption.setText(s);
                mAudioRecorderCaption.setVisibility(View.VISIBLE);
            }
        });

        mEditPreviewVieModel.getToastTime().observe(this, time -> {
            mToastTimeView.setText(time);
            mToastTimeView.setVisibility(StringUtil.isEmpty(time) ? View.GONE : View.VISIBLE);
        });
    }

    public void initEditor() {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor == null) {
            return;
        }
        HVETimeLine timeLine = editor.getTimeLine();
        if (timeLine != null) {
            mDefaultPlayControlView.setVideoSeekBarLength((int) timeLine.getEndTime());
        }
        addCover();
    }

    public void setSeekBarProgress(Long progress) {
        mDefaultPlayControlView.setVideoSeekBar(progress);
    }

    @Override
    protected void initEvent() {
        mDefaultPlayControlView.setSoundListener(new DefaultPlayControlView.OnSoundSwitchClickListener() {
            @Override
            public void onSoundSwitchClick(boolean isPlay) {
                viewModel.setSoundTrack(isPlay);
            }
        });
        mDefaultPlayControlView.setSeekListener(new DefaultPlayControlView.OnSeekListener() {
            @Override
            public void onSeek(int progress) {
                mEditPreviewVieModel.setCurrentTimeLine(progress);
            }
        });

        mDefaultPlayControlView.setOnPlayControlListener(new DefaultPlayControlView.OnPlayControlClickListener() {
            @Override
            public void onVoiceStateChange(boolean isMute) {
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null || viewModel == null) {
                    return;
                }
                MutableLiveData<Boolean> mutableLiveData = viewModel.getIsFootShow();
                if (mutableLiveData == null) {
                    return;
                }
                Boolean isFootShow = mutableLiveData.getValue();
                if (isFootShow != null && isFootShow) {
                    return;
                }
                editor.setGlobalMuteState(isMute);
            }

            @Override
            public void onPlayStateChange(boolean isPlay) {
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null) {
                    return;
                }
                mCurrentTime = mDefaultPlayControlView.getCurrentTime();

                isPlayState = isPlay;
                if (isPlayState) {
                    if (mVideoDuration - mCurrentTime < TIMER_PLAY_PERIOD) {
                        mCurrentTime = 0;
                    }
                    if (mEditPreviewVieModel.isPersonTrackingStatus()) {
                        editor.playTimeLine(mCurrentTime, mPersonTrackingViewModel.getVideoEndTime());
                    } else {
                        editor.playTimeLine(mCurrentTime, mVideoDuration);
                    }
                } else {
                    editor.pauseTimeLine();
                }
            }

            @Override
            public void onFullScreenClick() {
                navigationBarColor = R.color.home_color_FF181818;
                setStatusBarColor(mActivity);
                mPlayViewModel.setFullScreenState(true);
                mFullScreenPlayControlView.refreshButtonIcon();
            }
        });

        mFullScreenPlayControlView.setOnPlayControlListener(new FullScreenPlayControlView.OnPlayControlClickListener() {
            @Override
            public void onVoiceStateChange(boolean isMute) {
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null || viewModel == null) {
                    return;
                }
                MutableLiveData<Boolean> mutableLiveData = viewModel.getIsFootShow();
                if (mutableLiveData == null) {
                    return;
                }
                Boolean isFootShow = mutableLiveData.getValue();
                if (isFootShow != null && isFootShow) {
                    return;
                }
                editor.setGlobalMuteState(isMute);
            }

            @Override
            public void onPlayStateChange(boolean isPlay) {
                playOrParseActionOfFullScreen(isPlay);
            }

            @Override
            public void onSmallScreenClick() {
                navigationBarColor = R.color.color_20;
                setStatusBarColor(mActivity);
                mPlayViewModel.setFullScreenState(false);
            }

            @Override
            public void onSeekChange(int process) {
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null) {
                    return;
                }
                mCurrentTime = process;
                editor.seekTimeLine(process);
                mPlayViewModel.setCurrentTime((long) process);
            }

            @Override
            public void onAutoTemplateClick() {
            }
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    private void playOrParseActionOfFullScreen(boolean isPlay) {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor == null) {
            return;
        }
        isPlayState = isPlay;
        if (!isPlayState) {
            editor.pauseTimeLine();
        } else {
            HVETimeLine timeLine = editor.getTimeLine();
            if (timeLine != null) {
                if (timeLine.getDuration() - mCurrentTime < TIMER_PLAY_PERIOD) {
                    mCurrentTime = 0;
                }
                editor.playTimeLine(mCurrentTime, timeLine.getDuration());
            }
        }
    }

    public void setHideLockButton(DefaultPlayControlView.HideLockButton hideLockButton) {
        if (mDefaultPlayControlView != null) {
            mDefaultPlayControlView.setHideLockButton(hideLockButton);
        }
    }

    public void showLoadingView() {
        if (!isShowLoadingView) {
            isShowLoadingView = true;
            if (mLoadingLayout != null) {
                mLoadingLayout.setVisibility(View.VISIBLE);
            }
            mIndicatorView.show();
        }
    }

    public void hideLoadingView() {
        isShowLoadingView = false;
        if (mLoadingLayout != null) {
            mLoadingLayout.setVisibility(View.GONE);
        }
        mIndicatorView.hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        setDisplay();
    }

    private void setDisplay() {
        EditorManager instance = EditorManager.getInstance();
        if (instance == null) {
            return;
        }
        HuaweiVideoEditor editor = instance.getEditor();
        if (editor == null) {
            return;
        }

        editor.setPlayCallback(this);
        editor.setSurfaceCallback(new HuaweiVideoEditor.SurfaceCallback() {
            @Override
            public void surfaceCreated() {

            }

            @Override
            public void surfaceDestroyed() {

            }

            @Override
            public void surfaceChanged(int width, int height) {
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null) {
                    return;
                }
                HVETimeLine timeLine = editor.getTimeLine();
                if (timeLine == null) {
                    return;
                }

                if (hasInitCover) {
                    if (!isPlayState) {
                        editor.seekTimeLine(timeLine.getCurrentTime(), new HuaweiVideoEditor.SeekCallback() {
                            @Override
                            public void onSeekFinished() {
                                SmartLog.i(TAG, "surfaceChanged success: " + editor);
                            }
                        });
                    }
                } else {
                    addCover();
                }
                editor.refresh(mCurrentTime);
            }
        });
        editor.setDisplay(mSdkPreviewContainer);
    }

    private void addCover() {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor == null) {
            return;
        }
        editor.seekTimeLine(0, new HuaweiVideoEditor.SeekCallback() {
            @Override
            public void onSeekFinished() {
                HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
                if (timeLine == null) {
                    return;
                }
                hasInitCover = true;

                // 刷新
                editor.getBitmapAtSelectedTime(0, new HuaweiVideoEditor.ImageCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, long time) {
                        if (bitmap != null) {
                            if (mActivity instanceof VideoClipsActivity) {
                                VideoClipsActivity activity = (VideoClipsActivity) mActivity;
                                activity.initSetCoverData(editor.getProjectId(), bitmap, -1);
                            }
                        } else {
                            SmartLog.d(TAG, "setBitmapCover bitmap is null");
                        }
                    }

                    @Override
                    public void onFail(int errorCode) {
                        SmartLog.e(TAG, "setBitmapCover errorCode " + errorCode);
                    }
                });

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor != null) {
            if (isPlayState) {
                editor.pauseTimeLine();
            }
            if (mActivity instanceof VideoClipsActivity) {
                VideoClipsActivity activity = (VideoClipsActivity) mActivity;
                if (activity.isFromSelfMode()) {
                    editor.saveProject();
                }
            }
        }
        if (mToastState != null) {
            mToastState.cancelToast();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("mCurrentTime", mCurrentTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mToastState != null) {
            mToastState.cancelToast();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mToastState != null) {
            mToastState.cancelToast();
        }
    }

    @Override
    public void onPlayProgress(long timeStamp) {
        if (mActivity != null && isAdded()) {
            mActivity.runOnUiThread(() -> mPlayViewModel.setPlayState(true));
        }
        mPlayViewModel.setCurrentTime(timeStamp);
        mDefaultPlayControlView.setVideoSeekBar((int) timeStamp);
    }

    @Override
    public void onPlayStopped() {
        if (mActivity == null) {
            return;
        }
        mActivity.runOnUiThread(() -> {
            if (mPlayViewModel == null) {
                return;
            }
            mPlayViewModel.setPlayState(false);
        });
    }

    @Override
    public void onPlayFinished() {
        mPlayViewModel.setCurrentTime(-1L);
    }

    @Override
    public void onPlayFailed() {
        mActivity.runOnUiThread(() -> mPlayViewModel.setPlayState(false));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor == null) {
            return;
        }
        editor.seekTimeLine(mCurrentTime, () -> {
            if (mMaterialEditViewModel == null) {
                return;
            }
            mMaterialEditViewModel.refresh();
        });
    }
}
