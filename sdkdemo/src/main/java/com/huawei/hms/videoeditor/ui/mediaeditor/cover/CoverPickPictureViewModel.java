
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
import java.util.ArrayList;
import java.util.List;

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

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

/**
 * @author xwx882936
 * @since 2020/11/18
 */
public class CoverPickPictureViewModel extends AndroidViewModel {

    private static final String TAG = "PickPictureViewModel";

    private DataSource mediaDataSource;

    private final LiveData<PagedList<MediaData>> mPageData;

    private final MutableLiveData<Boolean> boundPageData = new MutableLiveData<>();

    private boolean loadContinue = false;

    private final int pageSize = 20;

    private String dirName = "";

    public CoverPickPictureViewModel(@NonNull Application application) {
        super(application);
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setPrefetchDistance(pageSize * 2)
            .build();

        mPageData =
            new LivePagedListBuilder(dataFactory, config).setInitialLoadKey(0).setBoundaryCallback(callback).build();
    }

    PagedList.BoundaryCallback<MediaData> callback = new PagedList.BoundaryCallback<MediaData>() {
        @Override
        public void onZeroItemsLoaded() {
            // 新提交的PagedList中没有数据
            boundPageData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull MediaData itemAtFront) {
            // 新提交的PagedList中第一条数据被加载到列表上
            boundPageData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull MediaData itemAtEnd) {
            // 新提交的PagedList中最后一条数据被加载到列表上
        }
    };

    DataSource.Factory dataFactory = new DataSource.Factory() {
        @NonNull
        @Override
        public DataSource create() {
            if (mediaDataSource == null || mediaDataSource.isInvalid()) {
                mediaDataSource = new MediaDataSource();
            }
            return mediaDataSource;
        }
    };

    class MediaDataSource extends PageKeyedDataSource<Integer, MediaData> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params,
            @NonNull LoadInitialCallback<Integer, MediaData> callback) {
            // 加载初始化数据的
            List<MediaData> media = loadImage(getApplication().getApplicationContext(), 0);
            boolean hasNextPage = true;
            if (media.size() == 0 || media.size() % pageSize != 0) {
                hasNextPage = false;
            }
            if (loadContinue) {
                loadContinue = false;
                hasNextPage = true;
            }
            callback.onResult(media, null, hasNextPage ? 1 : null);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, MediaData> callback) {
            // 能够向后加载数据的
            List<MediaData> mediaDataList = loadImage(getApplication().getApplicationContext(), params.key);
            boolean hasNextPage = true;
            if (mediaDataList.size() == 0 || mediaDataList.size() % pageSize != 0) {
                hasNextPage = false;
            }
            if (loadContinue) {
                loadContinue = false;
                hasNextPage = true;
            }
            callback.onResult(mediaDataList, hasNextPage ? params.key + 1 : null);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params,
            @NonNull LoadCallback<Integer, MediaData> callback) {
            // 向后加载分页数据的
        }
    }

    /**
     * 加载图片
     *
     * @return 本地图片列表
     */
    private List<MediaData> loadImage(Context context, int page) {
        List<MediaData> dataList = new ArrayList<>();
        dataList.clear();
        final String[] imageProjection = {     // 查询图片需要的数据列
            MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,   // 图片的显示名称 aaa.jpg
            MediaStore.Images.Media.DATA,           // 图片的真实路径 /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.SIZE,           // 图片的大小 long型 132492
            MediaStore.Images.Media.MIME_TYPE,      // 图片的类型 image/jpeg
            MediaStore.Images.Media.DATE_MODIFIED,     // 图片被添加的时间 long型 1450518608
            MediaStore.MediaColumns.WIDTH,          // 图片的宽
            MediaStore.MediaColumns.HEIGHT};        // 图片的高
        String mImageSelection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ? ";
        String[] mImageSelectionArgs = new String[] {dirName};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageProjection,
                    StringUtil.isEmpty(dirName) ? null : mImageSelection,
                    StringUtil.isEmpty(dirName) ? null : mImageSelectionArgs,
                    imageProjection[5] + " DESC LIMIT " + page * pageSize + " , " + pageSize);
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
                    // 不合法格式图片
                    loadContinue = true;
                    continue;
                }

                // 封面过滤.gif图片
                if (imagePath.endsWith(".gif")) {
                    SmartLog.d(TAG, "gif:" + imagePath);
                    loadContinue = true;
                    continue;
                }

                MediaData item = new MediaData();
                item.setName(imageName);
                item.setPath(imagePath);
                item.setUri(uri);
                item.setSize(imgSize);
                item.setMimeType(imageMimeType);
                item.setAddTime(imageAddTime);
                item.setWidth(imageWidth);
                item.setHeight(imageHeight);
                dataList.add(item);
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
        return mediaDataSource;
    }

    public LiveData<Boolean> getBoundaryPageData() {
        return boundPageData;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

}
