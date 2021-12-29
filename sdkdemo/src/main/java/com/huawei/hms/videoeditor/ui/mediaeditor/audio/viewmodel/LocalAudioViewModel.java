
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

package com.huawei.hms.videoeditor.ui.mediaeditor.audio.viewmodel;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.AudioData;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

public class LocalAudioViewModel extends AndroidViewModel {
    private static final String TAG = "LocalAudioViewModel";

    private PagedList.Config config;

    private DataSource dataSource;

    private LiveData<PagedList<AudioData>> pageData;

    private MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    private int pageSize = 20;

    public LocalAudioViewModel(@NonNull Application application) {
        super(application);
        config = new PagedList.Config.Builder().setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setPrefetchDistance(pageSize * 2)
            .build();

        pageData = new LivePagedListBuilder(factory, config).setInitialLoadKey(0).setBoundaryCallback(callback).build();
    }

    PagedList.BoundaryCallback<AudioData> callback = new PagedList.BoundaryCallback<AudioData>() {
        @Override
        public void onZeroItemsLoaded() {
            boundaryPageData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull AudioData itemAtFront) {
            boundaryPageData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull AudioData itemAtEnd) {
        }
    };

    DataSource.Factory factory = new DataSource.Factory() {
        @NonNull
        @Override
        public DataSource create() {
            if (dataSource == null || dataSource.isInvalid()) {
                dataSource = new MediaDataSource();
            }
            return dataSource;
        }
    };

    class MediaDataSource extends PageKeyedDataSource<Integer, AudioData> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params,
            @NonNull LoadInitialCallback<Integer, AudioData> callback) {
            List<AudioData> audioData = loadAudio(getApplication().getApplicationContext(), 0);
            boolean hasNextPage = true;
            if (audioData.size() == 0 || audioData.size() % pageSize != 0) {
                hasNextPage = false;
            }
            callback.onResult(audioData, null, hasNextPage ? 1 : null);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, AudioData> callback) {
            List<AudioData> audioData = loadAudio(getApplication().getApplicationContext(), params.key);
            boolean hasNextPage = true;
            if (audioData.size() == 0 || audioData.size() % pageSize != 0) {
                hasNextPage = false;
            }
            callback.onResult(audioData, hasNextPage ? params.key + 1 : null);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params,
            @NonNull LoadCallback<Integer, AudioData> callback) {
        }
    }

    private List<AudioData> loadAudio(Context context, int page) {
        List<AudioData> audios = new ArrayList<>();
        audios.clear();
        final String[] videoProjection = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST};
        final String mAudioSelection = MediaStore.Audio.Media.DATA + " like ?";
        try {
            Cursor cursor = context.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null,
                    videoProjection[4] + " DESC LIMIT " + page * pageSize + " , " + pageSize);
            while (cursor != null && cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(videoProjection[1]));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(videoProjection[2]));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(videoProjection[3]));
                long addTime = cursor.getLong(cursor.getColumnIndexOrThrow(videoProjection[4]));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(videoProjection[5]));

                AudioData item = new AudioData();
                item.setName(name);
                item.setPath(path);
                item.setSize(size);
                item.setAddTime(addTime);
                item.setDuration(duration);
                audios.add(item);
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (RuntimeException e) {
            SmartLog.e(TAG, e.getMessage());
        }

        return audios;
    }

    public LiveData<PagedList<AudioData>> getPageData() {
        return pageData;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public LiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

}
