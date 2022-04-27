/*
 *   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.hms.videoeditor.ui.template.view.exoplayer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSinkFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.huawei.hms.videoeditor.VideoEditorApplication;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;

public class PageListPlayManager {
    private static final String TAG = "PageListPlayManager";

    private static HashMap<String, PageListPlay> sPageListPlayHashMap = new HashMap<>();

    private static ProgressiveMediaSource.Factory MEDIA_SOURCE_FACTORY;

    static {
        try {
            Context context = VideoEditorApplication.getInstance().getContext();
            SmartLog.i(TAG, "getCanonicalPath failed! +++");
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(
                Util.getUserAgent(context, context.getPackageName()), 5 * 1000, 5 * 1000, false);
            File file = new File(context.getCacheDir().getCanonicalPath() + "/video_cache");
            Cache cache = new SimpleCache(file, new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 200));
            CacheDataSinkFactory cacheDataSinkFactory = new CacheDataSinkFactory(cache, Long.MAX_VALUE);

            CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(cache, dataSourceFactory,
                new FileDataSourceFactory(), cacheDataSinkFactory, CacheDataSource.FLAG_BLOCK_ON_CACHE, null);

            MEDIA_SOURCE_FACTORY = new ProgressiveMediaSource.Factory(cacheDataSourceFactory);
        } catch (IOException e) {
            SmartLog.i(TAG, "getCanonicalPath failed!");
            MEDIA_SOURCE_FACTORY = null;
        }
    }

    public static MediaSource createMediaSource(String url) {
        if (StringUtil.isEmpty(url) || MEDIA_SOURCE_FACTORY == null) {
            return null;
        }
        return MEDIA_SOURCE_FACTORY.createMediaSource(Uri.parse(url));
    }

    public static PageListPlay get(String pageName) {
        PageListPlay pageListPlay = sPageListPlayHashMap.get(pageName);
        if (pageListPlay == null) {
            pageListPlay = new PageListPlay();
            sPageListPlayHashMap.put(pageName, pageListPlay);
        }
        return pageListPlay;
    }

    public static void release(String pageName) {
        PageListPlay pageListPlay = sPageListPlayHashMap.remove(pageName);
        if (pageListPlay != null) {
            pageListPlay.release();
        }
    }

    public static void releaseAll() {
        Iterator<Map.Entry<String, PageListPlay>> iterator = sPageListPlayHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next().getKey();
            PageListPlay pageListPlay = sPageListPlayHashMap.get(next);
            if (pageListPlay != null) {
                pageListPlay.release();
            }
            iterator.remove();
        }
    }
}
