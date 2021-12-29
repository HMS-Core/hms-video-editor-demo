
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

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SharedPreferencesUtils;
import com.huawei.hms.videoeditor.ui.common.utils.TimeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.VolumeChangeObserver;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.constraintlayout.widget.ConstraintLayout;

public class FullScreenPlayControlView extends ConstraintLayout {
    private static final String TAG = FullScreenPlayControlView.class.getSimpleName();

    private final Context mContext;

    private final SharedPreferencesUtils mSpUtils = SharedPreferencesUtils.getInstance();

    private ImageView mIvPlayPause;

    private ImageView mIvVoice;

    private ImageView mIvSmallScreen;

    private TextView mTvCurrentTime;

    private TextView mTvTotalTime;

    private SeekBar mSeekBar;

    private OnPlayControlClickListener listener;

    public boolean isVideoPlaying = false;

    private int systemVolume;

    private int location = Constant.IntentFrom.INTENT_FROM_SELF;

    public FullScreenPlayControlView(Context context) {
        this(context, null);
    }

    public FullScreenPlayControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullScreenPlayControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FullScreenPlayControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.full_screen_play_control_view_layout, this, true);
        initView();
        initEvent();
    }

    private void initView() {
        mIvPlayPause = findViewById(R.id.iv_bottom_play_pause);
        mIvVoice = findViewById(R.id.iv_bottom_voice);
        mIvSmallScreen = findViewById(R.id.iv_bottom_full_screen);
        mTvCurrentTime = findViewById(R.id.tv_bottom_current_time);
        mTvTotalTime = findViewById(R.id.tv_bottom_total_time);
        mSeekBar = findViewById(R.id.seek_bar);
        mIvVoice.setSelected(mSpUtils.getVoiceSetting(mContext));
    }

    private void initEvent() {
        VolumeChangeObserver.getInstace(mContext.getApplicationContext())
            .setOnVolumeChangeListener(new VolumeChangeObserver.OnVolumeChangeListener() {
                @Override
                public void onVolumeChange(int volume) {
                    SmartLog.i("volume", "volume = " + volume);
                    systemVolume = volume;
                    if (systemVolume == 0) {
                        mSpUtils.voiceSetting(mContext, true);
                        mIvVoice.setSelected(mSpUtils.getVoiceSetting(mContext));
                    } else if (systemVolume > 0) {
                        mSpUtils.voiceSetting(mContext, false);
                        mIvVoice.setSelected(false);
                    }
                    if (listener != null) {
                        listener.onVoiceStateChange(mSpUtils.getVoiceSetting(mContext));
                    }
                }
            });

        mIvVoice.setOnClickListener(new OnClickRepeatedListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpUtils.voiceSetting(mContext, !mSpUtils.getVoiceSetting(mContext));
                boolean voiceSetting = mSpUtils.getVoiceSetting(mContext);
                mIvVoice.setSelected(voiceSetting);
                if (listener != null) {
                    listener.onVoiceStateChange(voiceSetting);
                }
            }
        }, 50));

        mIvPlayPause.setOnClickListener(new OnClickRepeatedListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideoPlaying) {
                    mIvPlayPause.setContentDescription(getResources().getString(R.string.play_pause));
                } else {
                    mIvPlayPause.setContentDescription(getResources().getString(R.string.play));
                }
                isVideoPlaying = !isVideoPlaying;
                mIvPlayPause.setSelected(isVideoPlaying);
                if (listener != null) {
                    listener.onPlayStateChange(isVideoPlaying);
                }
            }
        }, 50));

        mIvSmallScreen.setOnClickListener(new OnClickRepeatedListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIvSmallScreen.setContentDescription(getContext().getString(R.string.edit));
                if (listener != null) {
                    if (location == Constant.IntentFrom.INTENT_FROM_IMAGE_LIB) {
                        listener.onAutoTemplateClick();
                    } else {

                        listener.onSmallScreenClick();
                    }
                }
            }
        }, 50));

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && listener != null) {
                    listener.onSeekChange(progress);
                    mTvCurrentTime.setText(TimeUtils.makeTimeString(mContext, progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (isVideoPlaying) {
                    isVideoPlaying = false;
                    mIvPlayPause.setSelected(false);
                    if (listener != null) {
                        listener.onPlayStateChange(isVideoPlaying);
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void setLocation(int location) {
        this.location = location;
    }

    private void handleIntentFrom(int from) {
        if (from == Constant.IntentFrom.INTENT_FROM_SELF) {
            mIvSmallScreen.setImageResource(R.drawable.cancel_full_screen);
        } else if (from == Constant.IntentFrom.INTENT_FROM_IMAGE_LIB) {
            mIvSmallScreen.setImageResource(R.drawable.ic_full_screen_edit);
        } else {
            mIvSmallScreen.setImageResource(R.drawable.cancel_full_screen);
            SmartLog.e(TAG, "IntentFrom NONE");
        }
    }

    public void refreshButtonIcon() {
        handleIntentFrom(location);
    }

    public void setVideoPlaying(boolean isPlaying) {
        isVideoPlaying = isPlaying;
        mIvPlayPause.setSelected(isVideoPlaying);
    }

    public void setTotalTime(long totalTime) {
        mTvTotalTime.setText(TimeUtils.makeTimeString(mContext, totalTime));
        mSeekBar.setMax((int) totalTime);
    }

    public void updateRunningTime(long currentTime) {
        mTvCurrentTime.setText(TimeUtils.makeTimeString(mContext, currentTime));
        mSeekBar.setProgress((int) currentTime);
    }

    public void setOnPlayControlListener(OnPlayControlClickListener listener) {
        this.listener = listener;
    }

    public interface OnPlayControlClickListener {

        void onVoiceStateChange(boolean isMute);

        void onPlayStateChange(boolean isPlay);

        void onSmallScreenClick();

        void onSeekChange(int process);

        void onAutoTemplateClick();
    }
}
