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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.sdk.util.HVEUtil;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.mediapick.manager.MediaPickManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

public class PictureListViewModel extends AndroidViewModel {
    private static final String TAG = "PictureListViewModel";

    private static final int RESOLUTION = 720;

    private static final int PAGE_SIZE = 20;

    private DataSource mDataSource;

    private final LiveData<PagedList<MediaData>> mPageData;

    private final MutableLiveData<Boolean> loadAfterData = new MutableLiveData<>();

    private String dirName = "";

    private int counts;

    public PictureListViewModel(@NonNull Application application) {
        super(application);
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE * 2)
            .setPrefetchDistance(PAGE_SIZE * 2)
            .build();
        mPageData = new LivePagedListBuilder(mDataFactory, config).setInitialLoadKey(0)
            .setBoundaryCallback(boundaryCallBack)
            .build();
    }

    PagedList.BoundaryCallback<MediaData> boundaryCallBack = new PagedList.BoundaryCallback<MediaData>() {
        @Override
        public void onZeroItemsLoaded() {
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull MediaData itemAtFront) {
        }

        @Override
        public void onItemAtEndLoaded(@NonNull MediaData itemAtEnd) {
        }
    };

    DataSource.Factory mDataFactory = new DataSource.Factory() {
        @NonNull
        @Override
        public DataSource create() {
            if (mDataSource == null || mDataSource.isInvalid()) {
                mDataSource = new PictureDataSource();
            }
            return mDataSource;
        }
    };

    class PictureDataSource extends PageKeyedDataSource<Integer, MediaData> {
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params,
            @NonNull LoadInitialCallback<Integer, MediaData> callback) {
            List<MediaData> initialMediaDataList = loadImage(getApplication().getApplicationContext(), 0);
            boolean nextPageExists = true;
            if (counts == 0 || counts % PAGE_SIZE != 0) {
                nextPageExists = false;
            }
            callback.onResult(initialMediaDataList, null, nextPageExists ? 1 : null);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, MediaData> callback) {
            List<MediaData> afterMediaDataList = loadImage(getApplication().getApplicationContext(), params.key);
            boolean nextPageExists = true;
            if (counts == 0 || counts % PAGE_SIZE != 0) {
                nextPageExists = false;
            }
            if (counts < PAGE_SIZE) {
                loadAfterData.postValue(true);
            }
            callback.onResult(afterMediaDataList, nextPageExists ? params.key + 1 : null);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params,
            @NonNull LoadCallback<Integer, MediaData> callback) {
        }
    }

    private List<MediaData> loadImage(Context context, int page) {
        List<MediaData> dataList = new ArrayList<>();
        dataList.clear();
        counts = 0;
        final String[] imageDataList = {
            MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT
        };
        String[] mImageSelectionArgs = new String[] {dirName};
        String mImageSelection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ? ";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageDataList,
                    StringUtil.isEmpty(dirName) ? null : mImageSelection,
                    StringUtil.isEmpty(dirName) ? null : mImageSelectionArgs,
                    imageDataList[5] + " DESC LIMIT " + page * PAGE_SIZE + " , " + PAGE_SIZE);
        } catch (SecurityException e) {
            SmartLog.e(TAG, e.getMessage());
        }
        while (cursor != null && cursor.moveToNext()) {
            counts += 1;
            try {
                int imgId = cursor.getInt(cursor.getColumnIndexOrThrow(imageDataList[0]));
                String imageName = cursor.getString(cursor.getColumnIndexOrThrow(imageDataList[1]));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(imageDataList[2]));
                long imgSize = cursor.getLong(cursor.getColumnIndexOrThrow(imageDataList[3]));
                String imageMimeType = cursor.getString(cursor.getColumnIndexOrThrow(imageDataList[4]));
                long imageAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(imageDataList[5]));
                int imageWidth = cursor.getInt(cursor.getColumnIndexOrThrow(imageDataList[6]));
                int imageHeight = cursor.getInt(cursor.getColumnIndexOrThrow(imageDataList[7]));

                Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(imgId));

                if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                    if (TextUtils.isEmpty(imagePath)) {
                        continue;
                    }

                    File imageFile = new File(imagePath);
                    if (!imageFile.exists() || imageFile.length() <= 0) {
                        continue;
                    }
                }
                if (!HVEUtil.isLegalImage(imagePath)) {
                    continue;
                }

                if (!imagePath.toLowerCase(Locale.ENGLISH).endsWith(".jpg")
                    && !imagePath.toLowerCase(Locale.ENGLISH).endsWith(".png")) {
                    continue;
                }
                if (imageWidth < RESOLUTION || imageHeight < RESOLUTION) {
                    continue;
                }

                MediaData mediaData = MediaPickManager.getInstance().updateSelectMediaData(imagePath);
                if (mediaData != null) {
                    mediaData.setDirName(dirName);
                    dataList.add(mediaData);
                } else {
                    MediaData item = new MediaData();
                    item.setDirName(dirName);
                    item.setName(imageName);
                    item.setPath(imagePath);
                    item.setUri(uri);
                    item.setSize(imgSize);
                    item.setMimeType(imageMimeType);
                    item.setAddTime(imageAddTime);
                    item.setWidth(imageWidth);
                    item.setHeight(imageHeight);
                    dataList.add(item);
                }
            } catch (RuntimeException e) {
                SmartLog.e("MediaViewModel", e.getMessage());
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return dataList;
    }

    public LiveData<PagedList<MediaData>> getPageData() {
        return mPageData;
    }

    public DataSource getDataSource() {
        return mDataSource;
    }

    public LiveData<Boolean> getLoadAfterData() {
        return loadAfterData;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }
}
