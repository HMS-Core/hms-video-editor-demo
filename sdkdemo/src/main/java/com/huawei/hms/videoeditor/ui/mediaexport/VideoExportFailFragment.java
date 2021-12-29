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

package com.huawei.hms.videoeditor.ui.mediaexport;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.huawei.hms.videoeditor.sdk.HVEExportManager;
import com.huawei.hms.videoeditor.sdk.HVEExportManager.HVEExportVideoCallback;
import com.huawei.hms.videoeditor.sdk.bean.HVEVideoProperty;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.LazyFragment;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;

public class VideoExportFailFragment extends LazyFragment implements HVEExportVideoCallback {
    private static final String TAG = "VideoExportFailFragment";

    private Button mExportAgain;

    private Button mExportCancel;

    private int pResolution;

    private HVEExportManager exportManager;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_video_export_fail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.export_bg;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        pResolution = bundle.getInt(VideoExportSettingFragment.RESOLUTION);

        mExportAgain = view.findViewById(R.id.export_again);
        mExportCancel = view.findViewById(R.id.back);

        mExportAgain.setOnClickListener(view1 -> {
            VideoExportActivity activity = (VideoExportActivity) mActivity;
            if (activity == null) {
                return;
            }

            activity.exportAgain();
            activity.startConfirm();
            if (mExportAgain != null) {
                mExportAgain.setVisibility(View.INVISIBLE);
            }
            if (mExportCancel != null) {
                mExportCancel.setVisibility(View.INVISIBLE);
            }
            HVEVideoProperty videoProperty = new HVEVideoProperty(1920, 1080);
            switch (pResolution) {
                case -1:
                case 1:
                    videoProperty = new HVEVideoProperty(1920, 1080);
                    break;
                case 0:
                    videoProperty = new HVEVideoProperty(1280, 720);
                    break;
                case 2:
                    videoProperty = new HVEVideoProperty(2560, 1600);
                    break;
                case 3:
                    videoProperty = new HVEVideoProperty(3840, 2160);
                    break;
                default:
                    SmartLog.d(TAG, "initView run in default case");
            }
            String outputPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator
                    + Constant.LOCAL_VIDEO_SAVE_PATH + File.separator + VideoExportActivity.getTime() + ".mp4";

            exportManager = new HVEExportManager();
            if (activity.getEditor() == null) {
                return;
            }

            exportManager.exportVideo(activity.getEditor(), this, videoProperty, outputPath);
        });
    }

    @Override
    public void onCompileProgress(long time, long duration) {
        if (mActivity == null || mActivity.isDestroyed() || mActivity.isFinishing()) {
            return;
        }
        VideoExportActivity activity = (VideoExportActivity) mActivity;
        if (duration != 0) {
            int progress = (int) (time * 100 / duration);
            activity.setExportProgress(progress);
        }
    }

    @Override
    public void onCompileFinished(String path, Uri uri) {
        if (mActivity == null || mActivity.isDestroyed() || mActivity.isFinishing()) {
            return;
        }

        VideoExportActivity activity = (VideoExportActivity) mActivity;
        activity.exportSuccess(uri);
    }

    @Override
    public void onCompileFailed(int errCode, String errorMsg) {
        if (mActivity == null || mActivity.isDestroyed() || mActivity.isFinishing()) {
            return;
        }

        VideoExportActivity activity = (VideoExportActivity) mActivity;
        activity.exportAgainFail();
        activity.runOnUiThread(() -> {
            if (mExportAgain != null) {
                mExportAgain.setVisibility(View.VISIBLE);
            }
            if (mExportCancel != null) {
                mExportCancel.setVisibility(View.VISIBLE);
            }
        });
    }

    public void interruptVideoExport() {
        if (exportManager != null) {
            exportManager.interruptVideoExport();
        }
    }
}
