
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

package com.huawei.hms.videoeditor.ui.mediaeditor.audio.fragment;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.LazyFragment;
import com.huawei.hms.videoeditor.ui.common.bean.AudioData;
import com.huawei.hms.videoeditor.ui.common.view.decoration.DividerLine;
import com.huawei.hms.videoeditor.ui.mediaeditor.audio.activity.AudioPickActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.audio.adapter.LocalMusicAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.audio.viewmodel.LocalAudioViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LocalLocalMusicFragment extends LazyFragment
    implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "LocalMusicFragment";

    private RecyclerView mRecyclerView;

    private LocalMusicAdapter mLocalMusicAdapter;

    private LocalAudioViewModel mLocalAudioViewModel;

    private MediaPlayer mMediaPlayer;

    private int mCurrentPosition = -1;

    private AudioManager mAudioManager;

    private static AudioFocusRequest mFocusRequest;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_local_local_music;
    }

    @Override
    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
    }

    @Override
    protected void initObject() {
        mLocalAudioViewModel = new ViewModelProvider(this, mFactory).get(LocalAudioViewModel.class);
        mRecyclerView.setHasFixedSize(true);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        mRecyclerView.setItemAnimator(itemAnimator);
        mLocalMusicAdapter = new LocalMusicAdapter(mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        mRecyclerView.addItemDecoration(DividerLine.getLine(mContext, 16, R.color.color_20));
        mRecyclerView.setAdapter(mLocalMusicAdapter);
        initMediaPlayer();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        initAudioManager(mActivity);
    }

    @Override
    protected void initData() {
        mLocalAudioViewModel.getPageData().observe(this, pagedList -> {
            if (pagedList.size() > 0) {
                mLocalMusicAdapter.submitList(pagedList);
            }
        });
    }

    @Override
    protected void initEvent() {
        mLocalMusicAdapter.setOnItemClickListener(new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, AudioData item) {
                SmartLog.i(TAG, "onItemClick");
                startOrStopAudio(position, item);
            }

            @Override
            public void onUseClick(AudioData item) {
                if (mActivity instanceof AudioPickActivity) {
                    AudioPickActivity pickActivity = (AudioPickActivity) mActivity;
                    pickActivity.setChoiceResult(item.getName(), item.getPath());
                }
            }
        });
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
            abandonFocus();
        }
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

    private void startOrStopAudio(int position, AudioData item) {
        if (mCurrentPosition != position) {
            resetMediaPlayer(item.getPath());
            if (mCurrentPosition != -1 && mCurrentPosition >= 0 && mLocalMusicAdapter.getCurrentList() != null
                && mCurrentPosition < mLocalMusicAdapter.getCurrentList().size()) {
                AudioData audioData = mLocalMusicAdapter.getCurrentList().get(mCurrentPosition);
                if (audioData != null) {
                    audioData.setPlaying(false);
                    mLocalMusicAdapter.notifyItemChanged(mCurrentPosition, audioData);
                }
            }
            item.setPlaying(true);
            mLocalMusicAdapter.notifyItemChanged(position, item);
            mCurrentPosition = position;
        } else {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    abandonFocus();
                    item.setPlaying(false);
                } else {
                    requestFocus();
                    mMediaPlayer.start();
                    item.setPlaying(true);
                }
                mLocalMusicAdapter.notifyItemChanged(position, item);
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
        if (mCurrentPosition != -1 && mCurrentPosition >= 0 && mLocalMusicAdapter.getCurrentList() != null
            && mCurrentPosition < mLocalMusicAdapter.getCurrentList().size()) {
            AudioData audioData = mLocalMusicAdapter.getCurrentList().get(mCurrentPosition);
            if (audioData != null) {
                audioData.setPlaying(false);
                mLocalMusicAdapter.notifyItemChanged(mCurrentPosition, audioData);
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mMediaPlayer != null) {
            requestFocus();
            mMediaPlayer.start();
        }
    }

    private void initAudioManager(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes playbackAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mFocusRequest =
                new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(ON_AUDIO_FOCUS_CHANGE_LISTENER)
                    .build();
        }
    }

    /**
     * requestFocus
     *
     * @return boolean
     */
    public boolean requestFocus() {
        if (ON_AUDIO_FOCUS_CHANGE_LISTENER != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.requestAudioFocus(mFocusRequest);
            } else {
                return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.requestAudioFocus(
                    ON_AUDIO_FOCUS_CHANGE_LISTENER, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
        }
        return false;
    }

    /**
     * abandonFocus
     *
     * @return boolean
     */
    public boolean abandonFocus() {
        if (ON_AUDIO_FOCUS_CHANGE_LISTENER != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.abandonAudioFocusRequest(mFocusRequest);
            } else {
                return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager
                    .abandonAudioFocus(ON_AUDIO_FOCUS_CHANGE_LISTENER);
            }
        }
        return false;
    }

    /**
     * 音频焦点监听
     */
    private static final AudioManager.OnAudioFocusChangeListener ON_AUDIO_FOCUS_CHANGE_LISTENER =
        new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    case AudioManager.AUDIOFOCUS_GAIN:
                        SmartLog.w(TAG, "focusChange value is : " + focusChange);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + focusChange);
                }
            }
        };
}
