
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

package com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.viewmodel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.app.Application;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.mediapick.bean.MediaFolder;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class FolderListViewModel extends AndroidViewModel {

    private static final String TAG = "MediaFolderViewModel";

    private final MutableLiveData<String> mFolderSelect = new MutableLiveData<>();

    private final MutableLiveData<List<MediaFolder>> mImageMediaFolderData = new MutableLiveData<>();

    private HashSet<String> mImageDirPaths = new HashSet<>();

    public FolderListViewModel(@NonNull Application application) {
        super(application);
    }

    public void initFolder() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                getImageMediaFolder();
            }
        }.start();
    }

    private void getImageMediaFolder() {
        List<MediaFolder> imageFolders = new ArrayList<>();
        int mImageTotalCount = 0;
        long firstMediaTime = 0;
        String firstMediaPath = null;
        final String[] imageProjection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,};

        try {
            Cursor cursor = getApplication().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageProjection, null, null,
                    MediaStore.Images.Media.DATE_ADDED + " DESC ");
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
                    String[] picSize =
                        parentFile.list((dir, filename) -> filename.toLowerCase(Locale.ENGLISH).endsWith(".jpg")
                            || filename.toLowerCase(Locale.ENGLISH).endsWith(".png"));
                    if (picSize == null) {
                        continue;
                    }

                    mImageTotalCount += picSize.length;
                    imageFolder.setMediaCount(picSize.length);
                    imageFolders.add(imageFolder);

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
        imageFolder.setDirName(getApplication().getString(R.string.media_picture));
        imageFolder.setFirstMediaPath(firstMediaPath);
        imageFolder.setMediaCount(mImageTotalCount);
        imageFolders.add(0, imageFolder);
        mImageMediaFolderData.postValue(imageFolders);
        mImageDirPaths = null;
    }

    public void setFolderPathSelect(String folderPath) {
        mFolderSelect.postValue(folderPath);
    }

    public MutableLiveData<String> getFolderSelect() {
        return mFolderSelect;
    }

    public LiveData<List<MediaFolder>> getImageMediaData() {
        return mImageMediaFolderData;
    }
}
