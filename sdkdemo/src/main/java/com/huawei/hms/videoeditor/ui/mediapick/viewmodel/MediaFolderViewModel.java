
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

package com.huawei.hms.videoeditor.ui.mediapick.viewmodel;

import android.app.Application;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.videoeditor.sdk.util.HVEUtil;
import com.huawei.hms.videoeditor.ui.mediapick.bean.MediaFolder;
import com.huawei.hms.videoeditor.utils.SmartLog;
import com.huawei.hms.videoeditor.utils.Utils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MediaFolderViewModel extends AndroidViewModel {
    private static final String TAG = "MediaFolderViewModel";

    private final MutableLiveData<Boolean> mGalleryVideoSelect = new MutableLiveData<>(true);

    private final MutableLiveData<MediaFolder> mFolderSelect = new MutableLiveData<>();

    private final MutableLiveData<List<MediaFolder>> mVideoMediaFolderData = new MutableLiveData<>();

    private final MutableLiveData<List<MediaFolder>> mImageMediaFolderData = new MutableLiveData<>();

    private final MutableLiveData<Integer> mRotationState = new MutableLiveData<>(0);

    private HashSet<String> mVideoDirPaths = new HashSet<>();

    private HashSet<String> mImageDirPaths = new HashSet<>();

    public MediaFolderViewModel(@NonNull Application application) {
        super(application);
    }

    public void initFolder() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                getVideoMediaFolder();
                getImageMediaFolder();
            }
        }.start();
    }

    private void getVideoMediaFolder() {
        List<MediaFolder> videoFolders = new ArrayList<>();
        int mVideoTotalCount = 0;
        int mVideoSize = 0;
        long firstMediaTime = 0;
        String firstMediaPath = null;
        final String[] videoProjection =
            {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATE_MODIFIED};

        try {
            Cursor cursor = getApplication().getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null,
                    MediaStore.Video.Media.DATE_MODIFIED + " DESC ");
            while (cursor != null && cursor.moveToNext()) {
                try {
                    String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(videoProjection[0]));
                    long videoDuration = cursor.getInt(cursor.getColumnIndexOrThrow(videoProjection[1]));
                    long videoAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(videoProjection[2]));

                    if (videoDuration < 500) {
                        continue;
                    }

                    if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                        if (TextUtils.isEmpty(videoPath)) {
                            continue;
                        }

                        File file = new File(videoPath);
                        if (!file.exists() || file.length() <= 0) {
                            continue;
                        }
                    }
                    File parentFile = new File(videoPath).getParentFile();
                    if (parentFile == null) {
                        continue;
                    }
                    String dirPath = parentFile.getCanonicalPath();
                    MediaFolder videoFolder;
                    if (mVideoDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mVideoDirPaths.add(dirPath);
                        videoFolder = new MediaFolder();
                        videoFolder.setDirName(parentFile.getName());
                        videoFolder.setDirPath(dirPath);
                        videoFolder.setFirstMediaPath(videoPath);

                        if (firstMediaTime == 0) {
                            firstMediaTime = videoAddTime;
                            firstMediaPath = videoPath;
                        } else {
                            firstMediaPath = (videoAddTime > firstMediaTime) ? firstMediaPath : videoPath;
                            firstMediaTime = Math.min(videoAddTime, firstMediaTime);
                        }
                    }
                    String[] picSize =
                        parentFile.list((dir, filename) -> Utils.isVideoByPath(dir + File.separator + filename));
                    if (picSize == null) {
                        continue;
                    }
                    mVideoTotalCount += picSize.length;
                    videoFolder.setMediaCount(picSize.length);
                    videoFolders.add(videoFolder);
                    if (picSize.length > mVideoSize) {
                        mVideoSize = picSize.length;
                    }
                } catch (IOException | RuntimeException e) {
                    SmartLog.d(TAG, e.getMessage());
                }
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (SecurityException e) {
            SmartLog.e(TAG, e.getMessage());
        }
        MediaFolder videoFolder = new MediaFolder();
        videoFolder.setDirPath("");
        videoFolder.setDirName(getApplication().getString(R.string.select_media_recent_projects));
        videoFolder.setFirstMediaPath(firstMediaPath);
        videoFolder.setMediaCount(mVideoTotalCount);
        videoFolders.add(0, videoFolder);
        mVideoMediaFolderData.postValue(videoFolders);
        mVideoDirPaths = null;
        SmartLog.d(TAG, "videoFolder:" + videoFolder.toString());
    }

    private void getImageMediaFolder() {
        List<MediaFolder> imageFolders = new ArrayList<>();
        int mImageTotalCount = 0;
        int mImageSize = 0;
        long firstMediaTime = 0;
        String firstMediaPath = null;
        final String[] imageProjection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED};

        try {
            Cursor cursor = getApplication().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageProjection, null, null,
                    MediaStore.Images.Media.DATE_MODIFIED + " DESC ");
            while (cursor != null && cursor.moveToNext()) {
                try {
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(imageProjection[0]));
                    long imageAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(imageProjection[1]));
                    if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                        if (TextUtils.isEmpty(imagePath)) {
                            continue;
                        }

                        File file = new File(imagePath);
                        if (!file.exists() || file.length() <= 0) {
                            continue;
                        }
                    }

                    if (!HVEUtil.isLegalImage(imagePath)) {
                        continue;
                    }
                    File parentFile = new File(imagePath).getParentFile();
                    if (parentFile == null) {
                        continue;
                    }
                    String dirPath = parentFile.getCanonicalPath();
                    MediaFolder imageFolder;
                    if (mImageDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mImageDirPaths.add(dirPath);
                        imageFolder = new MediaFolder();
                        imageFolder.setDirName(parentFile.getName());
                        imageFolder.setDirPath(dirPath);
                        imageFolder.setFirstMediaPath(imagePath);

                        if (firstMediaTime == 0) {
                            firstMediaTime = imageAddTime;
                            firstMediaPath = imagePath;
                        } else {
                            firstMediaPath = (imageAddTime > firstMediaTime) ? firstMediaPath : imagePath;
                            firstMediaTime = Math.min(imageAddTime, firstMediaTime);
                        }
                    }
                } catch (RuntimeException | IOException e) {
                    SmartLog.e(TAG, e.getMessage());
                }
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (SecurityException e) {
            SmartLog.e(TAG, e.getMessage());
        }
        MediaFolder imageFolder = new MediaFolder();
        imageFolder.setDirPath("");
        imageFolder.setDirName(getApplication().getString(R.string.select_media_recent_projects));
        imageFolder.setFirstMediaPath(firstMediaPath);
        imageFolder.setMediaCount(mImageTotalCount);
        imageFolders.add(0, imageFolder);
        mImageMediaFolderData.postValue(imageFolders);
        mImageDirPaths = null;
        SmartLog.d(TAG, "imageFolders:" + imageFolders.toString());
    }

    public void setGalleryVideoSelect(boolean videoSelect) {
        mGalleryVideoSelect.postValue(videoSelect);
    }

    public MutableLiveData<Boolean> getGalleryVideoSelect() {
        return mGalleryVideoSelect;
    }

    public void setFolderPathSelect(MediaFolder folderPath) {
        mFolderSelect.postValue(folderPath);
    }

    public MutableLiveData<MediaFolder> getFolderSelect() {
        return mFolderSelect;
    }

    public LiveData<List<MediaFolder>> getVideoMediaData() {
        return mVideoMediaFolderData;
    }

    public LiveData<List<MediaFolder>> getImageMediaData() {
        return mImageMediaFolderData;
    }

    public MutableLiveData<Integer> getRotationState() {
        return mRotationState;
    }

    public void setRotationState(int state) {
        mRotationState.postValue(state);
    }
}
