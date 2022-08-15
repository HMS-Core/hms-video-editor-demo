/*
 *   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.hms.videoeditor.ui.template.view.exoplayer;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.Timer;
import java.util.TimerTask;

public class ListPlayerView extends FrameLayout
    implements IPlayTarget, Player.EventListener, PlayerControlView.VisibilityListener {

    private static final String TAG = "ListPlayerView";

    public FrameLayout bufferLayout;

    private LoadingIndicatorView mIndicatorView;

    public ImageView cover;

    private RelativeLayout playLayout;

    private int maxWidth;

    protected String mCategory;

    protected String mVideoUrl;

    protected boolean isPlaying;

    private Context mContext;

    private PageListPlay pageListPlay;

    private AlarmManager alarmManager;

    private Timer timer;

    private static AudioManager mAudioManager;

    private static AudioFocusRequest mFocusRequest;

    private TimerTask timerTask;

    public ListPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_player_view_t, this, true);

        bufferLayout = findViewById(R.id.loading_layout);
        mIndicatorView = findViewById(R.id.indicator);

        cover = findViewById(R.id.imageView);
        playLayout = findViewById(R.id.play_layout);

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        playLayout.setOnClickListener(v -> {
            playControl();
        });
        this.setOnClickListener(new OnClickRepeatedListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playControl();
            }
        }));
        this.setTransitionName("listPlayerView");
    }

    private void playControl() {
        if (isPlaying()) {
            playLayout.setVisibility(VISIBLE);
            inActive();
            if (pageListPlay != null) {
                pageListPlay.showPlayOrPause(false);
            }
        } else {
            playLayout.setVisibility(GONE);
            onActive();
            if (pageListPlay != null) {
                pageListPlay.showPlayOrPause(true);
            }
        }

        if (pageListPlay != null) {
            pageListPlay.setPlayListener(isPlay -> {
                if (isPlay) {
                    playLayout.setVisibility(GONE);
                } else {
                    playLayout.setVisibility(VISIBLE);
                }
            });
        }
    }

    public void bindData(int maxWidth, int maxHeight, int widthPx, int heightPx, String categoryId, String coverUrl,
        String videoUrl, String columnName) {
        AudioAttributes playbackAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mFocusRequest =
                new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(mOnAudioFocusChangeListener)
                    .build();
        }

        this.maxWidth = maxWidth;
        mCategory = categoryId;
        mVideoUrl = videoUrl;
        Glide.with(mContext)
            .load(coverUrl)
            .apply(new RequestOptions().transform(new MultiTransformation<>(new CenterCrop())))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(cover);
        setSize(widthPx, heightPx);
    }

    public void setSize(int widthPx, int heightPx) {
        int layoutWidth = maxWidth;
        int layoutHeight = 0;
        int navHeight = ScreenUtil.getNavigationBarHeightIfRoom(mContext);
        int statusBarHeight = ScreenUtil.getStatusBarHeight(mContext);
        int deviceWidth = SizeUtils.screenWidth(mContext);
        int deviceHeight = SizeUtils.screenHeight(mContext);
        float viewAspectRatio = (float) deviceWidth / deviceHeight;
        float videoAspectRatio = (float) widthPx / heightPx;
        float aspectDeformation = videoAspectRatio / viewAspectRatio - 1;
        if (widthPx >= heightPx) {
            layoutWidth = maxWidth;
            layoutHeight = (int) (heightPx / (widthPx * 1.0f / maxWidth));
        } else {
            if (aspectDeformation > 0) {
                layoutWidth = deviceWidth;
                layoutHeight = heightPx * layoutWidth / widthPx;
            } else {
                layoutHeight = deviceHeight - navHeight - statusBarHeight;
                layoutWidth = widthPx * layoutHeight / heightPx;
            }
        }
        LayoutParams params = (LayoutParams) getLayoutParams();
        params.width = layoutWidth;
        params.height = layoutHeight;
        params.gravity = Gravity.CENTER;
        setLayoutParams(params);

        LayoutParams blurParams = (LayoutParams) bufferLayout.getLayoutParams();
        blurParams.width = layoutWidth;
        blurParams.height = layoutHeight;
        params.gravity = Gravity.CENTER;
        bufferLayout.setLayoutParams(blurParams);

        LayoutParams coverParams = (LayoutParams) cover.getLayoutParams();
        coverParams.width = layoutWidth;
        coverParams.height = layoutHeight;
        coverParams.gravity = Gravity.CENTER;
        cover.setLayoutParams(coverParams);

        LayoutParams playBtnParams = (LayoutParams) playLayout.getLayoutParams();
        playBtnParams.gravity = Gravity.CENTER;
        playLayout.setLayoutParams(playBtnParams);

    }

    @Override
    public ViewGroup getOwner() {
        return this;
    }

    @Override
    public void onActive() {
        requestAudioFocus();
        pageListPlay = PageListPlayManager.get(mCategory);
        PlayerView playerView = pageListPlay.playerView;
        PlayerControlView playerControlView = pageListPlay.controlView;
        if (playerControlView != null) {
            playerControlView.addVisibilityListener(this);
        }
        SimpleExoPlayer exoPlayer = pageListPlay.exoPlayer;
        if (playerView == null) {
            return;
        }
        pageListPlay.switchPlayerView(playerView, true);
        ViewParent parent = playerView.getParent();
        if (parent != this) {

            if (parent != null) {
                ((ViewGroup) parent).removeView(playerView);
                ((ListPlayerView) parent).inActive();
            }

            ViewGroup.LayoutParams coverParams = cover.getLayoutParams();
            this.addView(playerView, 1, coverParams);
        }

        if (TextUtils.equals(pageListPlay.playUrl, mVideoUrl)) {
            onPlayerStateChanged(true, Player.STATE_READY);
        } else {
            MediaSource source = PageListPlayManager.createMediaSource(mVideoUrl);
            if (source == null) {
                SmartLog.e(TAG, "source is null ！");
                return;
            }
            exoPlayer.prepare(source);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            pageListPlay.playUrl = mVideoUrl;
        }
        playerControlView.show();
        exoPlayer.addListener(this);
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isPlaying = false;
        bufferLayout.setVisibility(GONE);
        mIndicatorView.hide();
        cover.setVisibility(VISIBLE);

    }

    @Override
    public void inActive() {
        abandonAudioFocus();
        pageListPlay = PageListPlayManager.get(mCategory);
        if (pageListPlay.exoPlayer == null) {
            return;
        }
        pageListPlay.exoPlayer.setPlayWhenReady(false);
        pageListPlay.exoPlayer.removeListener(this);
        cover.setVisibility(VISIBLE);
        playLayout.setVisibility(VISIBLE);
        bufferLayout.setVisibility(GONE);
        mIndicatorView.hide();
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        PageListPlay aPageListPlay = PageListPlayManager.get(mCategory);
        SimpleExoPlayer exoPlayer = aPageListPlay.exoPlayer;
        if (playbackState == Player.STATE_READY && exoPlayer.getBufferedPosition() != 0 && playWhenReady) {
            cover.setVisibility(GONE);
            bufferLayout.setVisibility(GONE);
            mIndicatorView.hide();
            playLayout.setVisibility(GONE);
        } else if (playbackState == Player.STATE_BUFFERING) {
            bufferLayout.setVisibility(VISIBLE);
            mIndicatorView.show();
            playLayout.setVisibility(GONE);
        }
        isPlaying = playbackState == Player.STATE_READY && exoPlayer.getBufferedPosition() != 0 && playWhenReady;

    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (pageListPlay != null) {
                        pageListPlay.pause();
                        pageListPlay.showPlayOrPause(false);
                        pageListPlay.showControlView(true);
                        playLayout.setVisibility(VISIBLE);
                    }
                    break;
                default:
                    SmartLog.d(TAG, "handleMessage run in default case");
            }
        }
    };

    private boolean isAlarmClock(long time) {
        boolean isClock = false;
        if (alarmManager == null) {
            alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        }
        AlarmManager.AlarmClockInfo nextAlarmClock = alarmManager.getNextAlarmClock();
        long triggerTime = 0;
        if (nextAlarmClock != null) {
            triggerTime = nextAlarmClock.getTriggerTime();
            if (triggerTime - time <= 500 && triggerTime - time >= -500) {
                isClock = true;
            }
        } else {
            isClock = false;
        }

        return isClock;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        switch (visibility) {
            case VISIBLE:
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }

                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }

                if (pageListPlay != null) {
                    pageListPlay.play();
                    pageListPlay.showPlayOrPause(true);
                    pageListPlay.showControlView(false);
                    playLayout.setVisibility(GONE);
                }

                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        boolean alarmClock = isAlarmClock(System.currentTimeMillis());
                        if (alarmClock) {
                            handler.sendEmptyMessage(1);
                        }
                    }
                };
                timer.schedule(timerTask, 0, 50);
                break;

            case INVISIBLE:
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }

                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                break;
            default:
                SmartLog.d(TAG, "onVisibilityChanged run in default case");
        }
    }

    /**
     * requestAudioFocus()
     * 
     * @return boolean
     */
    public boolean requestAudioFocus() {
        if (mOnAudioFocusChangeListener != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.requestAudioFocus(mFocusRequest);
            } else {
                return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.requestAudioFocus(
                    mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
        }
        return false;
    }

    /**
     * abandonAudioFocus
     * 
     * @return boolean
     */
    public boolean abandonAudioFocus() {
        if (mOnAudioFocusChangeListener != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mFocusRequest == null) {
                    return false;
                }
                return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.abandonAudioFocusRequest(mFocusRequest);
            } else {
                return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager
                    .abandonAudioFocus(mOnAudioFocusChangeListener);
            }
        }
        return false;
    }

    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
        new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        inActive();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN:
                        onActive();
                        break;
                    default:
                        SmartLog.d(TAG, "OnAudioFocusChange run in default case");
                }
            }
        };
}
