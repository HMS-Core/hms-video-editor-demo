
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

package com.huawei.hms.videoeditor.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;

public class Utils {
    private static final String TAG = "Utils";

    public interface ThumbnailCallback {
        void onBitmap(Bitmap bitmap);

        void onSuccess();

        void onFail(String errorCode, String errMsg);
    }

    public static boolean isMateX(Activity context) {
        Point p = new Point();
        if (context.getWindowManager() != null && context.getWindowManager().getDefaultDisplay() != null) {
            context.getWindowManager().getDefaultDisplay().getSize(p);
        }
        Log.i(TAG, "isMateX: " + p.x + "y:" + p.y);
        return p.x > 2000;
    }

    public static void getThumbnails(String path, long spaceTime, int width, int height, ThumbnailCallback callback) {
        new Thread() {
            @Override
            public void run() {
                Thread.currentThread().setName("UtilThumbnail");
                MediaMetadataRetriever media = new MediaMetadataRetriever();
                try {
                    media.setDataSource(path);
                    String durationStr = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    if (durationStr == null) {
                        if (callback != null) {
                            callback.onFail("1", "Illegal Video");
                        }
                        return;
                    }
                    long duration = Long.parseLong(durationStr);
                    long time = 0;
                    while (time < duration) {
                        Bitmap bitmap = media.getFrameAtTime(time * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                        Bitmap result = scaleAndCutBitmap(bitmap, width, height, 0);
                        if (bitmap != null) {
                            bitmap.recycle();
                        }
                        if (callback != null) {
                            callback.onBitmap(result);
                        }
                        time = time + spaceTime;
                    }
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (IllegalArgumentException e) {
                    SmartLog.e(TAG, "getThumbnail error");
                    if (callback != null) {
                        callback.onFail("IllegalArgumentException", "");
                    }
                } finally {
                    media.release();
                }
            }
        }.start();
    }

    private static Bitmap scaleAndCutBitmap(Bitmap bitmap, int width, int height, int rotation) {
        if (bitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        double scale = getScale(bitmap.getWidth(), bitmap.getHeight(), width, height);

        matrix.postScale((float) scale, (float) scale);
        matrix.postRotate(rotation);
        Bitmap bitmap1;
        int space;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            space = (bitmap.getHeight() - bitmap.getWidth()) / 2;
            bitmap1 = Bitmap.createBitmap(bitmap, 0, space, bitmap.getWidth(), bitmap.getWidth(), matrix, true);
        } else {
            space = (bitmap.getWidth() - bitmap.getHeight()) / 2;
            bitmap1 = Bitmap.createBitmap(bitmap, space, 0, bitmap.getHeight(), bitmap.getHeight(), matrix, true);
        }
        return bitmap1;
    }

    private static double getScale(int oriWidth, int oriHeight, int targetWidth, int targetHeight) {
        double scale = 0;
        if (oriHeight <= oriWidth) {
            scale = 1.0 * targetHeight / oriHeight;
        } else {
            scale = 1.0 * targetWidth / oriWidth;
        }

        return scale > 1 ? 1 : scale;
    }
}
