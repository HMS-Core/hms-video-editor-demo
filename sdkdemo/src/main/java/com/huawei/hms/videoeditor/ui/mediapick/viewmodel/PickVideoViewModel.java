
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

import com.huawei.hms.videoeditor.sdk.bean.HVEVisibleFormatBean;
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

public class PickVideoViewModel extends AndroidViewModel {
    private static final String TAG = "PickVideoViewModel";

    private static final int MAX_VIDEO_WIDTH = 4096;

    private DataSource mDataSource;

    private LiveData<PagedList<MediaData>> mPageData;

    private final MutableLiveData<Boolean> mBoundaryPageData = new MutableLiveData<>();

    private boolean loadContinue = false;

    private int aPageSize = 20;

    private String aDirName = "";

    private int rotationState = 0;

    public PickVideoViewModel(@NonNull Application application) {
        super(application);
        PagedList.Config pageConfig = new PagedList.Config.Builder().setPageSize(aPageSize)
            .setInitialLoadSizeHint(aPageSize * 2)
            .setPrefetchDistance(aPageSize * 2)
            .build();

        mPageData =
            new LivePagedListBuilder(factory, pageConfig).setInitialLoadKey(0).setBoundaryCallback(aCallback).build();
    }

    PagedList.BoundaryCallback<MediaData> aCallback = new PagedList.BoundaryCallback<MediaData>() {
        @Override
        public void onZeroItemsLoaded() {
            mBoundaryPageData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull MediaData itemAtFront) {
            mBoundaryPageData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull MediaData itemAtEnd) {
        }
    };

    DataSource.Factory factory = new DataSource.Factory() {
        @NonNull
        @Override
        public DataSource create() {
            if (mDataSource == null || mDataSource.isInvalid()) {
                mDataSource = new MediaDataSource();
            }
            return mDataSource;
        }
    };

    class MediaDataSource extends PageKeyedDataSource<Integer, MediaData> {
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params,
            @NonNull LoadInitialCallback<Integer, MediaData> callback) {
            List<MediaData> mediaData = loadVideo(getApplication().getApplicationContext(), 0);
            boolean hasNextPage = true;
            if (mediaData.size() == 0 || mediaData.size() % aPageSize != 0) {
                hasNextPage = false;
            }
            if (loadContinue) {
                loadContinue = false;
                hasNextPage = true;
            }
            callback.onResult(mediaData, null, hasNextPage ? 1 : null);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, MediaData> callback) {
            List<MediaData> mediaData = loadVideo(getApplication().getApplicationContext(), params.key);
            boolean hasNextPage = true;
            if (mediaData.size() == 0 || mediaData.size() % aPageSize != 0) {
                hasNextPage = false;
            }
            if (loadContinue) {
                loadContinue = false;
                hasNextPage = true;
            }
            callback.onResult(mediaData, hasNextPage ? params.key + 1 : null);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params,
            @NonNull LoadCallback<Integer, MediaData> callback) {
        }
    }

    private List<MediaData> loadVideo(Context context, int page) {
        List<MediaData> videos = new ArrayList<>();
        final String[] videoProjections =
            {MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE, MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DURATION, MediaStore.MediaColumns.WIDTH, MediaStore.MediaColumns.HEIGHT};

        String mVideoSelection = MediaStore.Video.Media.DATA + " like?";
        String[] mVideoSelectionArgs = new String[] {"%" + aDirName + "%"};
        Cursor cursor;
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                Bundle bundle = new Bundle();
                bundle.putString(ContentResolver.QUERY_ARG_SQL_SELECTION,
                    StringUtil.isEmpty(aDirName) ? null : mVideoSelection);
                bundle.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                    StringUtil.isEmpty(aDirName) ? null : mVideoSelectionArgs);
                bundle.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, videoProjections[5] + " DESC");
                bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, aPageSize);
                bundle.putInt(ContentResolver.QUERY_ARG_OFFSET, page * aPageSize);
                cursor = context.getContentResolver()
                    .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjections, bundle, null);
            } else {
                cursor = context.getContentResolver()
                    .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjections,
                        StringUtil.isEmpty(aDirName) ? null : mVideoSelection,
                        StringUtil.isEmpty(aDirName) ? null : mVideoSelectionArgs,
                        videoProjections[5] + " DESC LIMIT " + page * aPageSize + " , " + aPageSize);
            }
        } catch (Exception exception) {
            SmartLog.e(TAG, exception.getMessage());
            return videos;
        }

        while (cursor != null && cursor.moveToNext()) {
            try {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(videoProjections[0]));
                String videoName = cursor.getString(cursor.getColumnIndexOrThrow(videoProjections[1]));
                String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(videoProjections[2]));
                long videoSize = cursor.getLong(cursor.getColumnIndexOrThrow(videoProjections[3]));
                String videoMimeType = cursor.getString(cursor.getColumnIndexOrThrow(videoProjections[4]));
                long videoAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(videoProjections[5]));
                long videoDuration = cursor.getInt(cursor.getColumnIndexOrThrow(videoProjections[6]));
                int videoWidth = cursor.getInt(cursor.getColumnIndexOrThrow(videoProjections[7]));
                int videoHeight = cursor.getInt(cursor.getColumnIndexOrThrow(videoProjections[8]));

                Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));

                HVEVisibleFormatBean bean = HVEUtil.getVideoProperty(videoPath);
                if (bean == null) {
                    loadContinue = true;
                    continue;
                }

                if (videoDuration == 0 || videoWidth == 0 || videoHeight == 0) {
                    videoDuration = bean.getDuration();
                    videoWidth = bean.getWidth();
                    videoHeight = bean.getHeight();
                }

                if (videoDuration < 500) {
                    loadContinue = true;
                    continue;
                }

                if (videoWidth > MAX_VIDEO_WIDTH || videoHeight > MAX_VIDEO_WIDTH) {
                    loadContinue = true;
                    continue;
                }

                if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                    if (TextUtils.isEmpty(videoPath)) {
                        loadContinue = true;
                        continue;
                    }

                    File file = new File(videoPath);
                    if (!file.exists() || file.length() <= 0) {
                        loadContinue = true;
                        continue;
                    }
                }

                if (rotationState == 1) {
                    if (videoWidth > videoHeight) {
                        loadContinue = true;
                        continue;
                    }
                }

                if (rotationState == 2) {
                    if (videoWidth < videoHeight) {
                        loadContinue = true;
                        continue;
                    }
                }

                MediaData mediaData = MediaPickManager.getInstance().updateSelectMediaData(videoPath);
                if (mediaData != null) {
                    mediaData.setDirName(aDirName);
                    videos.add(mediaData);
                } else {
                    MediaData item = new MediaData();
                    item.setDirName(aDirName);
                    item.setName(videoPath);
                    item.setPath(videoPath);
                    item.setUri(uri);
                    item.setSize(videoSize);
                    item.setMimeType(videoMimeType);
                    item.setAddTime(videoAddTime);
                    item.setDuration(videoDuration);
                    item.setWidth(videoWidth);
                    item.setHeight(videoHeight);
                    videos.add(item);
                }
            } catch (RuntimeException e) {
                SmartLog.e("MediaViewModel", e.getMessage());
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        SmartLog.d("MediaViewModel", videos.size() + "");
        return videos;
    }

    public LiveData<PagedList<MediaData>> getaPageData() {
        return mPageData;
    }

    public DataSource getDataSource() {
        return mDataSource;
    }

    public LiveData<Boolean> getaBoundaryPageData() {
        return mBoundaryPageData;
    }

    public void setaDirName(String aDirName) {
        this.aDirName = aDirName;
    }

    public void setRotationState(int state) {
        this.rotationState = state;
    }

    public int getRotationState() {
        return rotationState;
    }
}
