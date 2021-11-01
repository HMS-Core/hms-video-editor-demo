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

package com.huawei.hms.videoeditor.ui.mediaeditor.cover;

import java.io.File;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.common.agc.HVEApplication;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

/**
 * @author xwx882936
 * @since 2020/11/18
 */
public class CoverImageViewModel extends AndroidViewModel {

    private static final String TAG = "CoverImageViewModel";

    public static final String COVER_IMAGE_NAME_SUFFIX = "cover.png";

    public static final String SOURCE_COVER_IMAGE_NAME_SUFFIX = "source.png";

    private final MutableLiveData<String> mInitImageData = new MutableLiveData<>(); // 封面默认取首帧缩略图

    private final MutableLiveData<String> mCoverImageData = new MutableLiveData<>();

    private boolean isForCover; // 是否是封面

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

    /**
     * 删除上次的封面图片
     * 还需进行优化
     */
    public void removeBitmapCover(String projectId) {
        if (TextUtils.isEmpty(projectId)) {
            return;
        }
        String sdPath = new StringBuilder(getApplication().getFilesDir().getAbsolutePath() + File.separator)
            .append(HVEApplication.getInstance().getTag())
            .append("project/")
            .append(projectId)
            .toString();
        String sdTempPath = new StringBuilder(getApplication().getFilesDir().getAbsolutePath() + File.separator)
            .append(HVEApplication.getInstance().getTag())
            .append("project")
            .append(File.separator)
            .append(projectId)
            .append(File.separator)
            .append("temp")
            .toString();

        File[] files = new File(sdPath).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.getName().contains(COVER_IMAGE_NAME_SUFFIX)) {
                File tempFile = new File(sdTempPath + File.separator + file.getName());
                if (!tempFile.getParentFile().exists()) {
                    if (!tempFile.getParentFile().mkdirs()) {
                        SmartLog.e("CropImageLayout", "fail to make dir parent file");
                    }
                }
                if (!file.renameTo(tempFile)) {
                    SmartLog.e("CropImageLayout", "fail rename temp file");
                }
            }
        }
    }

    /**
     * 删除原始图片
     */
    public void removePictureByProjectId(String projectId) {
        if (TextUtils.isEmpty(projectId)) {
            SmartLog.e(TAG, "projectId is empty");
            return;
        }
        try {
            String sdPath = getApplication().getFilesDir().getAbsolutePath() + File.separator
                + HVEApplication.getInstance().getTag() + "project/" + projectId;
            File[] files = new File(sdPath).listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.getName().contains(COVER_IMAGE_NAME_SUFFIX)
                    || file.getName().contains(SOURCE_COVER_IMAGE_NAME_SUFFIX)) {
                    boolean delete = file.delete();
                    if (!delete) {
                        SmartLog.e(TAG, "removeSourcePicture false");
                    }
                }
            }
        } catch (NullPointerException | SecurityException e) {
            SmartLog.e(TAG, "removeSourcePictureByProjectId error");
        }
    }

    /**
     * 删除原始图片
     */
    public void removeSourcePicture(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            SmartLog.e(TAG, "filePath is empty");
            return;
        }
        try {
            File source = new File(filePath);
            if (source.exists()) {
                boolean delete = source.delete();
                if (!delete) {
                    SmartLog.e(TAG, "removeSourcePicture false");
                }
            }
        } catch (NullPointerException | SecurityException e) {
            SmartLog.e(TAG, "removeSourcePicture error");
        }
    }

    public boolean isForCover() {
        return isForCover;
    }

    public void setForCover(boolean forCover) {
        isForCover = forCover;
    }
}
