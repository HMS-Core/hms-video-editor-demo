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

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.huawei.hms.videoeditor.HVEEditorLibraryApplication;
import com.huawei.hms.videoeditor.VideoEditorApplication;
import com.huawei.hms.videoeditor.ui.common.utils.ResUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.constraintlayout.widget.ConstraintLayout;

public class PageListPlay {
    public static final int VIDEO_DETAIL_HEIGHT = 60;
    public SimpleExoPlayer exoPlayer;

    public PlayerView playerView;

    public PlayerControlView controlView;

    public String playUrl;

    private ImageView imgPlay;

    boolean isPlaying = true;

    private ConstraintLayout controlLayout;

    private final View navBarView;

    private boolean isLayout = true;

    private OnStylePlayListener stylePlayListener;

    private DefaultTimeBar defaultTimeBar;
    public PageListPlay() {
        Context context = VideoEditorApplication.getInstance().getContext();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultRenderersFactory(context),
            new DefaultTrackSelector(), new DefaultLoadControl());
        playerView = (PlayerView) LayoutInflater.from(context).inflate(R.layout.layout_exo_player_view, null, false);

        controlView = (PlayerControlView) LayoutInflater.from(context)
            .inflate(R.layout.layout_exo_player_controller_view, null, false);
        defaultTimeBar = controlView.findViewById(R.id.exo_progress);
        defaultTimeBar.setBufferedColor(ResUtils.getResources().getColor(R.color.pick_line_color));
        defaultTimeBar.setPlayedColor(ResUtils.getResources().getColor(R.color.color_text_focus));
        refreshRtl();
        controlLayout = controlView.findViewById(R.id.linearLayout2);
        navBarView = controlView.findViewById(R.id.nav_bar_view);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            SizeUtils.dp2Px(context, VIDEO_DETAIL_HEIGHT));
        params.gravity = Gravity.BOTTOM;
        controlLayout.setLayoutParams(params);
        playerView.setPlayer(exoPlayer);
        controlView.setPlayer(exoPlayer);

        int resourceId = ResUtils.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        int height = ResUtils.getResources().getDimensionPixelSize(resourceId);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) navBarView.getLayoutParams();
        layoutParams.height = height;
        navBarView.setLayoutParams(layoutParams);

        imgPlay = controlView.findViewById(R.id.img_player);
        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    imgPlay.setImageResource(R.drawable.ic_play_video);
                    isPlaying = false;
                    exoPlayer.setPlayWhenReady(false);
                    isLayout = false;
                } else {
                    isPlaying = true;
                    exoPlayer.setPlayWhenReady(true);
                    imgPlay.setImageResource(R.drawable.edit_pause);
                    isLayout = true;
                }

                if (stylePlayListener != null) {
                    stylePlayListener.onStylePlay(isLayout);
                }
            }
        });
    }

    public void showPlayOrPause(boolean isPlaying) {
        imgPlay.setImageResource(isPlaying ? R.drawable.edit_pause : R.drawable.ic_play_video);
    }

    public void showControlView(boolean isShow) {
        controlLayout.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    public void showImagePlay(boolean isShow) {
        imgPlay.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void release() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop(true);
            exoPlayer.release();
            exoPlayer = null;
        }

        if (playerView != null) {
            playerView.setPlayer(null);
            playerView = null;
        }
        if (controlView != null) {
            controlView.setPlayer(null);
            controlView = null;
        }

    }

    public void pause() {
        if (isPlaying) {
            imgPlay.setImageResource(R.drawable.ic_play_video);
            isPlaying = false;
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(false);
            }
        }
    }

    public void play() {
        if (imgPlay != null) {
            imgPlay.setVisibility(View.GONE);
        }

        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    public void switchPlayerView(PlayerView newPlayerView, boolean attach) {
        playerView.setPlayer(attach ? null : exoPlayer);
        newPlayerView.setPlayer(attach ? exoPlayer : null);
    }

    public void setPlayListener(OnStylePlayListener stylePlayListener) {
        this.stylePlayListener = stylePlayListener;
    }

    public interface OnStylePlayListener {
        void onStylePlay(boolean isPlay);
    }

    private void refreshRtl() {
        if (defaultTimeBar == null) {
            return;
        }
        if (ScreenUtil.isRTL()) {
            defaultTimeBar.setScaleX(RTL_UI);
        } else {
            defaultTimeBar.setScaleX(LTR_UI);
        }
    }
}