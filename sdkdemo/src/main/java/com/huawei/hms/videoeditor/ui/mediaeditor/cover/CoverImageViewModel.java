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

package com.huawei.hms.videoeditor.ui.mediaeditor.cover;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.utils.SmartLog;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class CoverImageViewModel extends AndroidViewModel {

    private static final String TAG = "CoverImageViewModel";

    public static final String COVER_IMAGE_NAME_SUFFIX = "cover.png";

    public static final String SOURCE_COVER_IMAGE_NAME_SUFFIX = "source.png";

    private final MutableLiveData<String> mInitImageData = new MutableLiveData<>();

    private final MutableLiveData<String> mCoverImageData = new MutableLiveData<>();

    private boolean isForCover;

    public CoverImageViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getInitImageData() {
        return mInitImageData;
    }

    public void setInitImageData(String initImageData) {
        mInitImageData.postValue(initImageData);
    }

    public MutableLiveData<String> getCoverImageData() {
        return mCoverImageData;
    }

    public void setCoverImageData(String imagePath) {
        mCoverImageData.postValue(imagePath);
    }

    public void setBitmapCover(String projectId, Bitmap bitmap, long time) {
        if (TextUtils.isEmpty(projectId)) {
            SmartLog.e(TAG, "projectId is empty");
            return;
        }
        new Thread("CoverImageViewModel-Thread-1") {
            @Override
            public void run() {
                super.run();
                try {
                    if (bitmap == null) {
                        setCoverImageData("");
                        return;
                    }
                    String path = FileUtil.saveBitmap(getApplication(), projectId, bitmap,
                        System.currentTimeMillis() + COVER_IMAGE_NAME_SUFFIX);
                    setInitImageData(path);
                } catch (Exception e) {
                    SmartLog.e(TAG, e.getMessage());
                }
            }
        }.start();
    }

    public boolean isForCover() {
        return isForCover;
    }

    public void setForCover(boolean forCover) {
        isForCover = forCover;
    }

    public void updateDefaultCover(HuaweiVideoEditor editor, long lastTime) {
        if (editor == null) {
            SmartLog.w(TAG, "updateDefaultCover editor null return");
            return;
        }

        HVETimeLine timeLine = editor.getTimeLine();
        if (timeLine == null) {
            SmartLog.w(TAG, "updateDefaultCover timeLine or cover type invalid return ");
            return;
        }

        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            editor.getBitmapAtSelectedTime(0, new HuaweiVideoEditor.ImageCallback() {
                @Override
                public void onSuccess(Bitmap bitmap, long time) {
                    SmartLog.i(TAG, "updateDefaultCover success time:" + time + "  bitmap:" + bitmap);
                    if (bitmap != null) {
                        String path = FileUtil.saveBitmap(getApplication(), editor.getProjectId(), bitmap,
                                System.currentTimeMillis() + COVER_IMAGE_NAME_SUFFIX);
                        if (timeLine != null) {
                            timeLine.addCoverImage(path);
                        }
                        setInitImageData(path);
                    } else {
                        SmartLog.d(TAG, "setBitmapCover bitmap is null");
                    }
                    countDownLatch.countDown();
                    if (lastTime != 0) {
                        editor.seekTimeLine(lastTime);
                    }
                }

                @Override
                public void onFail(int errorCode) {
                    SmartLog.e(TAG, "setBitmapCover errorCode " + errorCode);
                    countDownLatch.countDown();
                }
            });

            boolean isCountDown = countDownLatch.await(500, TimeUnit.MILLISECONDS);

            if (!isCountDown) {
                SmartLog.e(TAG, "updateDefaultCover timeOut !");
            }

        } catch (InterruptedException e) {
            SmartLog.e(TAG, "updateDefaultCover error " + e.getMessage());
        }
    }
}
