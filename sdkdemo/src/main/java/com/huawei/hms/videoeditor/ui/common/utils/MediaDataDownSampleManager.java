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

package com.huawei.hms.videoeditor.ui.common.utils;

import static com.huawei.hms.videoeditor.sdk.HVEDownSamplingManager.DOWN_SAMPLING_DONE;
import static com.huawei.hms.videoeditor.sdk.HVEDownSamplingManager.NEED_DOWN_SAMPLING;
import static com.huawei.hms.videoeditor.sdk.HVEDownSamplingManager.NO_NEED_DOWN_SAMPLING;
import static com.huawei.hms.videoeditor.sdk.HVEDownSamplingManager.needDownSampling;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.huawei.hms.videoeditor.sdk.HVEDownSamplingManager;
import com.huawei.hms.videoeditor.sdk.HVEErrorCode;
import com.huawei.hms.videoeditor.sdk.util.HVEUtil;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.view.dialog.CommonProgressDialog;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class MediaDataDownSampleManager {
    private static final String TAG = "MediaDataDownSampleManager";

    private ArrayList<MediaData> originalList = new ArrayList<>();

    private List<DownSampleItem> downSamplingList = new ArrayList<>();

    private DownSampleCallBack downSampleCallBack;

    private CommonProgressDialog commonProgressDialog;

    private Activity context;

    public MediaDataDownSampleManager(Activity context) {
        this.context = context;
    }

    public void downSampleList(ArrayList<MediaData> list, DownSampleCallBack callBack) {
        if (context == null) {
            downSampleCallBack.onFailed("DownSample context is null");
            return;
        }
        if (list == null || list.isEmpty()) {
            return;
        }
        originalList = list;
        downSamplingList = new ArrayList<>();
        downSampleCallBack = callBack;
        for (int i = 0; i < list.size(); i++) {
            MediaData data = list.get(i);

            if (data == null) {
                downSampleCallBack.onFailed("data is null");
                return;
            }

            if (HVEUtil.isLegalImage(data.getPath())) {
                continue;
            }

            int sampling = data.getDownSamplingState();

            if (sampling == -2) {
                sampling = needDownSampling(data.getPath(), data.getWidth(), data.getHeight());
                data.setDownSamplingState(sampling);
            }
            switch (sampling) {
                case DOWN_SAMPLING_DONE:
                    String downSamplingFile = HVEDownSamplingManager.getDownSamplingFilePath(data.getPath());
                    data.setPath(downSamplingFile);
                    data.setDownSamplingState(NO_NEED_DOWN_SAMPLING);
                    originalList.set(i, data);
                    break;
                case NEED_DOWN_SAMPLING:
                    downSamplingList.add(new DownSampleItem(data, i));
                    break;
                default:
                    break;
            }
        }

        if (downSamplingList.size() == 0) {
            downSampleCallBack.onSuccess(originalList);
        } else {
            showDialog();
            startDownSampling();
        }
    }

    private void showDialog() {
        if (context == null) {
            downSampleCallBack.onFailed("DownSample context is null");
            return;
        }
        commonProgressDialog = new CommonProgressDialog(context, () -> {
            downSampleCallBack.onCancel();
            if (!downSamplingList.isEmpty()) {
                String path = downSamplingList.get(0).mediaData.getPath();
                HVEDownSamplingManager.stopDownSampling(path);
            }
        });
        commonProgressDialog.setTitleValue(context.getString(R.string.sampling_tips));
        commonProgressDialog.setCanceledOnTouchOutside(false);
        commonProgressDialog.setCancelable(false);
        commonProgressDialog.show();
    }

    private void updateProgress(int progress) {
        if (context == null) {
            downSampleCallBack.onFailed("DownSample context is null");
            return;
        }
        if (commonProgressDialog != null) {
            context.runOnUiThread(() -> {
                commonProgressDialog.updateProgress(progress);
            });
        }
    }

    private void startDownSampling() {
        if (commonProgressDialog == null) {
            downSampleCallBack.onFailed("DownSamplingDialog is null");
            return;
        }

        if (downSamplingList.size() == 0) {
            commonProgressDialog.dismiss();
            downSampleCallBack.onSuccess(originalList);
            return;
        }

        if (context == null) {
            downSampleCallBack.onFailed("DownSample context is null");
            return;
        }

        context.runOnUiThread(() -> {
            commonProgressDialog.setTitleValue(context.getString(R.string.sampling_tips) + " ("
                + (originalList.size() - downSamplingList.size()) + "/" + originalList.size() + ")");
        });

        String samplingPath = downSamplingList.get(0).mediaData.getPath();
        HVEDownSamplingManager.startDownSampling(samplingPath, new HVEDownSamplingManager.HVEDownSamplingCallback() {
            @Override
            public void onProgress(int progress) {
                SmartLog.d(TAG, "DownSampling progress: " + progress);
                updateProgress(progress);
            }

            @Override
            public void onFinished(int result) {
                if (result == HVEErrorCode.SUCCESS) {
                    String getSamplingFile = HVEDownSamplingManager.getDownSamplingFilePath(samplingPath);
                    MediaData mediaData = downSamplingList.get(0).mediaData;
                    int originalIndex = downSamplingList.get(0).index;
                    mediaData.setPath(getSamplingFile);
                    mediaData.setDownSamplingState(NO_NEED_DOWN_SAMPLING);
                    originalList.set(originalIndex, mediaData);
                    SmartLog.d(TAG, "Index:" + originalIndex + "DownSample onFinished.");
                    downSamplingList.remove(0);
                    startDownSampling();
                } else {
                    SmartLog.e(TAG, "DownSample failed.");
                    if (commonProgressDialog != null && commonProgressDialog.isShowing()) {
                        commonProgressDialog.dismiss();
                    }
                    downSampleCallBack.onFailed("DownSample failed");
                }
            }
        });
    }

    public interface DownSampleCallBack {

        void onSuccess(ArrayList<MediaData> list);

        void onCancel();

        void onFailed(String message);
    }

    private static class DownSampleItem {
        public MediaData mediaData;

        public int index;

        private DownSampleItem(MediaData mediaData, int index) {
            this.mediaData = mediaData;
            this.index = index;
        }
    }
}
