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

package com.huawei.hms.videoeditor.ui.mediaeditor.ai;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.huawei.hms.videoeditor.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ui.common.utils.TimeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import java.io.File;

public class ViewFileActivity extends BaseActivity {
    private VideoView videoView;

    private ImageView imageView;

    private ImageView ivCertain;

    private TextView tvCertain;

    private String mFilePath = "";

    private boolean mIsVideo = false;

    private long mStartTime = 0;

    private long mDuration = 0;

    private String mVideoOutputPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            + File.separator + Constant.LOCAL_VIDEO_SAVE_PATH + File.separator
            + TimeUtils.formatTimeByUS(System.currentTimeMillis(), Constant.LOCAL_VIDEO_SAVE_TIME) + ".mp4";

    private String mPhotoOutputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
            + "DCIM" + File.separator + "Camera" + File.separator + Constant.LOCAL_VIDEO_SAVE_PATH + File.separator
            + TimeUtils.formatTimeByUS(System.currentTimeMillis(), Constant.LOCAL_VIDEO_SAVE_TIME) + ".jpg";

    public static void startActivity(Activity activity, String videoPath, boolean isVideo) {
        Intent intent = new Intent(activity, ViewFileActivity.class);
        intent.putExtra("filePath", videoPath);
        intent.putExtra("isVideo", isVideo);
        activity.startActivity(intent);
    }

    public static void startActivity(Activity activity, String videoPath, long startTime, int duration) {
        Intent intent = new Intent(activity, ViewFileActivity.class);
        intent.putExtra("filePath", videoPath);
        intent.putExtra("isVideo", true);
        intent.putExtra("startTime", startTime);
        intent.putExtra("duration", duration);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file);
        videoView = findViewById(R.id.video_view);
        imageView = findViewById(R.id.image_view);
        ivCertain = findViewById(R.id.iv_certain);
        tvCertain = findViewById(R.id.tv_certain);
        SafeIntent safeIntent = new SafeIntent(getIntent());
        mFilePath = safeIntent.getStringExtra("filePath");
        mIsVideo = safeIntent.getBooleanExtra("isVideo", false);
        mStartTime = safeIntent.getLongExtra("startTime", 0);
        mDuration = safeIntent.getIntExtra("duration", 0);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.view_file);

        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (mDuration != 0) {
            ivCertain.setVisibility(View.VISIBLE);
            tvCertain.setVisibility(View.GONE);
            ivCertain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            ivCertain.setVisibility(View.GONE);
            tvCertain.setVisibility(View.VISIBLE);
            tvCertain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(mFilePath)) {
                        boolean isSuccess = FileUtil.saveToLocalSystem(ViewFileActivity.this, mIsVideo, mFilePath,
                                mVideoOutputPath, mPhotoOutputPath);
                        if (isSuccess) {
                            ToastUtils.getInstance()
                                    .showToast(ViewFileActivity.this, getString(R.string.save_to_gallery_success),
                                            Toast.LENGTH_SHORT);
                        }
                    }
                    onBackPressed();
                }
            });
        }
        if (mIsVideo) {
            if (!TextUtils.isEmpty(mFilePath)) {
                videoView.setVideoPath(mFilePath);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 循环播放视频
                        mp.setLooping(true);
                    }
                });
                if (!videoView.isPlaying()) {
                    videoView.start();
                }
                if (mStartTime != 0) {
                    videoView.seekTo((int) mStartTime);
                }
                if (mDuration != 0) {
                    new Handler(Looper.myLooper()).postDelayed(() -> videoView.pause(), mDuration);
                }
            }
        } else {
            if (!TextUtils.isEmpty(mFilePath)) {
                Glide.with(getApplicationContext()).load(mFilePath).into(imageView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.suspend();
        }
    }
}
