/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.
 */

package com.hwa.demo.screenrecorddemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.huawei.hms.videoeditor.screenrecord.data.HVERecordFile;
import com.hwa.demo.screenrecorddemo.util.FormatHelper;
import com.hwa.demo.screenrecorddemo.R;
import com.hwa.demo.screenrecorddemo.util.Constants;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public  class VideoPreviewActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView txtCurrentPosition;
    private FrameLayout videoContainer;
    private ImageView btnVideoController;
    private VideoView videoView;
    private Handler handler = new Handler(Looper.getMainLooper());
    private HVERecordFile recordFile;
    private long delay = 500L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hms_scr_layout_activity_video_preview);
        recordFile = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_RECORD_FILE);
        findViewById(R.id.btnBack).setOnClickListener(this);
        btnVideoController = findViewById(R.id.btnVideoController);
        videoView = findViewById(R.id.videoView);
        txtCurrentPosition = findViewById(R.id.txtCurrentPosition);
        videoContainer = findViewById(R.id.videoContainer);

        videoContainer.setOnClickListener(this);
        btnVideoController.setOnClickListener(this);

        startVideoStatusListener();
        handler.postDelayed(() -> initializePlayer(),200);
    }

    @Override
    protected void onPause() {
        pauseVideo();
        super.onPause();
    }

    private void pauseVideo() {
        btnVideoController.setVisibility(View.VISIBLE);
        btnVideoController.clearAnimation();
        setControllerImage(R.drawable.hms_scr_drawable_video_play);
        btnVideoController.setContentDescription("play");
        videoView.pause();
    }

    private void startVideoStatusListener() {
        handler.postDelayed(this::videoViewUpdate,delay);
    }

    private void videoViewUpdate(){
        if (videoView != null && videoView.isPlaying()) {
            runOnUiThread(() -> {
                delay = 1;
                txtCurrentPosition.setText( FormatHelper.durationFormat( (long) videoView.getCurrentPosition() ) );
            });
        }
        startVideoStatusListener();
    }

    private void setControllerImage(int id) {
        btnVideoController.setImageDrawable(
                ContextCompat.getDrawable(
                        this,
                        id
                )
        );
    }

    private void initializePlayer() {
        if(videoView != null && recordFile != null) {
            videoView.setVideoPath(recordFile.getUri().getPath());
            videoView.start();
        }
    }

    private void startHideAnimation() {
        setControllerImage(R.drawable.hms_scr_drawable_video_pause);
        AlphaAnimation fadeOut = new AlphaAnimation(1F, 0F);
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);
        fadeOut.setFillAfter( true);
        btnVideoController.startAnimation(fadeOut);
        btnVideoController.setContentDescription("pause");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;

            case R.id.btnVideoController :
                onVideoControlClick();
                break;

            case R.id.videoContainer :
                showControllerButton();
                break;

            default:
        }
    }

    private void onVideoControlClick() {
        if (videoView == null){
            return;
        }

        if (videoView.isPlaying()){
            pauseVideo();
        }else {
            if (videoView.getCurrentPosition() >= videoView.getDuration()){
                videoView.seekTo(0);
                txtCurrentPosition.setText(FormatHelper.durationFormat(0L));
            }
            videoView.start();
            startHideAnimation();
        }
    }

    private void showControllerButton() {
        btnVideoController.setVisibility(View.VISIBLE);
        onVideoControlClick();
    }
}
