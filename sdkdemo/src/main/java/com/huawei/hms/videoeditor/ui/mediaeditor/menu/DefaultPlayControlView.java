
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

package com.huawei.hms.videoeditor.ui.mediaeditor.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SharedPreferencesUtils;
import com.huawei.hms.videoeditor.ui.common.utils.TimeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.VolumeChangeObserver;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class DefaultPlayControlView extends RelativeLayout {
    private static final String TAG = "DefaultPlayControlView";

    private final Context mContext;

    private final SharedPreferencesUtils mSpUtils = SharedPreferencesUtils.getInstance();

    private ImageView mIvPlayPause;

    private ImageView mFullScreen;

    private TextView mTvRunningTime;

    private TextView mTvTotalTime;

    private SeekBar mVideoSeekBar;

    private OnPlayControlClickListener listener;

    private OnSoundSwitchClickListener soundListener;

    private OnSeekListener seekListener;

    public boolean isVideoPlaying;

    public boolean isSoundPlay;

    private ImageView soundSwitch;

    private int systemVolume;

    public HideLockButton mHideLockButton;

    public interface HideLockButton {
        void isShowLockButton(boolean isShow);
    }

    public void setSoundListener(OnSoundSwitchClickListener soundListener) {
        this.soundListener = soundListener;
    }

    public void setHideLockButton(HideLockButton hideLockButton) {
        mHideLockButton = hideLockButton;
    }

    public OnSeekListener getSeekListener() {
        return seekListener;
    }

    public void setSeekListener(OnSeekListener seekListener) {
        this.seekListener = seekListener;
    }

    public DefaultPlayControlView(Context context) {
        this(context, null);
    }

    public DefaultPlayControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultPlayControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DefaultPlayControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.default_play_control_view_layout, this, true);
        initView();
        initEvent();
    }

    private void initView() {
        mIvPlayPause = findViewById(R.id.iv_top_play_pause);
        mVideoSeekBar = findViewById(R.id.videoseekbar);
        mTvRunningTime = findViewById(R.id.tv_top_running_time);
        mTvTotalTime = findViewById(R.id.tv_top_total_time);
        mFullScreen = findViewById(R.id.iv_full_screen);
        soundSwitch = findViewById(R.id.sound_switch);
    }

    private void initEvent() {
        soundSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSoundPlay = !isSoundPlay;
                soundSwitch.setSelected(isSoundPlay);
                soundListener.onSoundSwitchClick(isSoundPlay);
            }
        });

        VolumeChangeObserver.getInstace(mContext.getApplicationContext())
            .setOnVolumeChangeListener(new VolumeChangeObserver.OnVolumeChangeListener() {
                @Override
                public void onVolumeChange(int volume) {
                    SmartLog.i("volume", "volume = " + volume);
                    systemVolume = volume;
                    if (systemVolume == 0) {
                        mSpUtils.voiceSetting(mContext, true);
                    } else if (systemVolume > 0) {
                        mSpUtils.voiceSetting(mContext, false);
                    }
                    if (listener != null) {
                        listener.onVoiceStateChange(mSpUtils.getVoiceSetting(mContext));
                    }
                }
            });

        mIvPlayPause.setOnClickListener(new OnClickRepeatedListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (!isVideoPlaying && editor == null) {
                    SmartLog.w(TAG, "not ready to playTimeline");
                    return;
                }

                isVideoPlaying = !isVideoPlaying;
                mIvPlayPause.setSelected(isVideoPlaying);
                if (isVideoPlaying) {
                    mIvPlayPause.setContentDescription(getContext().getString(R.string.play_pause));
                } else {
                    mIvPlayPause.setContentDescription(getResources().getString(R.string.play));
                }
                if (listener != null) {
                    listener.onPlayStateChange(isVideoPlaying);
                }
            }
        }, 50));

        mFullScreen.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (listener != null) {
                listener.onFullScreenClick();
            }
        }, 50));

        mVideoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTvRunningTime.setText(TimeUtils.makeTimeString(mContext, i));
                if (!isVideoPlaying) {
                    HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                    if (editor == null) {
                        return;
                    }
                    editor.seekTimeLine(i);
                    seekListener.onSeek(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mTvRunningTime.setSelected(false);

                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null) {
                    return;
                }

                if (isVideoPlaying) {
                    editor.pauseTimeLine();
                    isVideoPlaying = false;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null) {
                    return;
                }

                if (isVideoPlaying) {
                    editor.pauseTimeLine();
                    isVideoPlaying = false;
                }
            }
        });
    }

    public void setVideoSeekBarLength(int length) {
        if (mVideoSeekBar == null) {
            return;
        }
        mVideoSeekBar.setMax(length);
    }

    public long getCurrentTime() {
        if (mVideoSeekBar == null) {
            return 0;
        }
        return mVideoSeekBar.getProgress();
    }

    public void setVideoSeekBar(long time) {
        if (mVideoSeekBar == null) {
            return;
        }
        mVideoSeekBar.setProgress((int) time);
    }

    public void setVideoPlaying(boolean isPlaying) {
        isVideoPlaying = isPlaying;
        mIvPlayPause.setSelected(isVideoPlaying);
    }

    @SuppressLint("SetTextI18n")
    public void setTotalTime(long totalTime) {
        mTvTotalTime.setText(TimeUtils.makeTimeString(mContext, totalTime));
        mVideoSeekBar.setMax((int) totalTime);
    }

    public void updateRunningTime(long currentTime) {
        mTvRunningTime.setText(TimeUtils.makeTimeString2(mContext, currentTime));
    }

    public void setOnPlayControlListener(OnPlayControlClickListener listener) {
        this.listener = listener;
    }

    public interface OnPlayControlClickListener {

        void onVoiceStateChange(boolean isMute);

        void onPlayStateChange(boolean isPlay);

        void onFullScreenClick();
    }

    public interface OnSoundSwitchClickListener {
        void onSoundSwitchClick(boolean isPlay);
    }

    public interface OnSeekListener {
        void onSeek(int progress);
    }
}
