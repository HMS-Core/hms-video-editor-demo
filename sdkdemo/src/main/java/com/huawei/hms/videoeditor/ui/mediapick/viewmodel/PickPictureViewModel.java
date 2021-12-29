
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

public class PickPictureViewModel extends AndroidViewModel {
    private static final String TAG = "PickPictureViewModel";

    private DataSource dataSource;

    private LiveData<PagedList<MediaData>> mPageDataList;

    private final MutableLiveData<Boolean> isBoundPageData = new MutableLiveData<>();

    private boolean isLoadContinue = false;

    private final int pageSizeData = 20;

    private String dirPathName = "";

    private int rotationState = 0;

    public PickPictureViewModel(@NonNull Application application) {
        super(application);
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(pageSizeData)
            .setInitialLoadSizeHint(pageSizeData * 2)
            .setPrefetchDistance(pageSizeData * 2)
            .build();

        mPageDataList = new LivePagedListBuilder(dataSourceFactory, config).setInitialLoadKey(0)
            .setBoundaryCallback(boundaryCallback)
            .build();
    }

    PagedList.BoundaryCallback<MediaData> boundaryCallback = new PagedList.BoundaryCallback<MediaData>() {
        @Override
        public void onZeroItemsLoaded() {
            isBoundPageData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull MediaData itemFront) {
            isBoundPageData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull MediaData itemEnd) {
        }
    };

    DataSource.Factory dataSourceFactory = new DataSource.Factory() {
        @NonNull
        @Override
        public DataSource create() {
            if (dataSource == null || dataSource.isInvalid()) {
                dataSource = new MediaDataSource();
            }

            return dataSource;
        }
    };

    class MediaDataSource extends PageKeyedDataSource<Integer, MediaData> {
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params,
            @NonNull LoadInitialCallback<Integer, MediaData> initialCallback) {
            List<MediaData> media = loadImageData(getApplication().getApplicationContext(), 0);
            boolean hasNextPage = true;
            if (media.size() == 0 || media.size() % pageSizeData != 0) {
                hasNextPage = false;
            }
            if (isLoadContinue) {
                isLoadContinue = false;
                hasNextPage = true;
            }
            initialCallback.onResult(media, null, hasNextPage ? 1 : null);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params,
            @NonNull LoadCallback<Integer, MediaData> dataLoadCallback) {
            List<MediaData> dataList = loadImageData(getApplication().getApplicationContext(), params.key);
            boolean isHaveNextPage = true;
            if (dataList.size() == 0 || dataList.size() % pageSizeData != 0) {
                isHaveNextPage = false;
            }
            if (isLoadContinue) {
                isLoadContinue = false;
                isHaveNextPage = true;
            }
            dataLoadCallback.onResult(dataList, isHaveNextPage ? params.key + 1 : null);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params,
            @NonNull LoadCallback<Integer, MediaData> callback) {
        }
    }

    private List<MediaData> loadImageData(Context context, int page) {
        List<MediaData> mediaDataList = new ArrayList<>();
        final String[] imageProjection = {
            MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME};
        String mImageSelection = MediaStore.Images.Media.DATA + " like?";
        String[] mImageSelectionArgs = new String[] {"%" + dirPathName + "%"};
        Cursor cursor = null;
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                Bundle bundle = new Bundle();
                bundle.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, StringUtil.isEmpty(dirPathName) ? null : mImageSelection);
                bundle.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, StringUtil.isEmpty(dirPathName) ? null : mImageSelectionArgs);
                bundle.putString(ContentResolver.QUERY_ARG_SORT_DIRECTION, imageProjection[5] + " DESC LIMIT " + page * pageSizeData + " , " + pageSizeData);
                cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageProjection, bundle, null);
            } else {
                cursor = context.getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageProjection,
                                StringUtil.isEmpty(dirPathName) ? null : mImageSelection,
                                StringUtil.isEmpty(dirPathName) ? null : mImageSelectionArgs,
                                imageProjection[5] + " DESC LIMIT " + page * pageSizeData + " , " + pageSizeData);
            }

        } catch (SecurityException e) {
            SmartLog.e(TAG, e.getMessage());
        }

        while (cursor != null && cursor.moveToNext()) {
            try {
                int imgId = cursor.getInt(cursor.getColumnIndexOrThrow(imageProjection[0]));
                String imageName = cursor.getString(cursor.getColumnIndexOrThrow(imageProjection[1]));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(imageProjection[2]));
                long imgSize = cursor.getLong(cursor.getColumnIndexOrThrow(imageProjection[3]));
                String imageMimeType = cursor.getString(cursor.getColumnIndexOrThrow(imageProjection[4]));
                long imageAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(imageProjection[5]));
                int imageWidth = cursor.getInt(cursor.getColumnIndexOrThrow(imageProjection[6]));
                int imageHeight = cursor.getInt(cursor.getColumnIndexOrThrow(imageProjection[7]));

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
                    isLoadContinue = true;
                    continue;
                }

                if (rotationState == 1) {
                    if (imageWidth > imageHeight) {
                        isLoadContinue = true;
                        continue;
                    }
                }

                if (rotationState == 2) {
                    if (imageWidth < imageHeight) {
                        isLoadContinue = true;
                        continue;
                    }
                }

                MediaData mediaData = MediaPickManager.getInstance().updateSelectMediaData(imagePath);
                if (mediaData != null) {
                    mediaData.setDirName(dirPathName);
                    mediaDataList.add(mediaData);
                } else {
                    MediaData item = new MediaData();
                    item.setDirName(dirPathName);
                    item.setName(imagePath);
                    item.setPath(imagePath);
                    item.setUri(uri);
                    item.setSize(imgSize);
                    item.setMimeType(imageMimeType);
                    item.setAddTime(imageAddTime);
                    item.setWidth(imageWidth);
                    item.setHeight(imageHeight);
                    mediaDataList.add(item);
                }
            } catch (Exception e) {
                SmartLog.e(TAG, e.getMessage());
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return mediaDataList;
    }

    public LiveData<PagedList<MediaData>> getPageData() {
        return mPageDataList;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public LiveData<Boolean> getBoundaryPageData() {
        return isBoundPageData;
    }

    public void setDirPathName(String dirPathName) {
        this.dirPathName = dirPathName;
    }

    public void setRotationState(int state) {
        this.rotationState = state;
    }

    public int getRotationState() {
        return rotationState;
    }
}
