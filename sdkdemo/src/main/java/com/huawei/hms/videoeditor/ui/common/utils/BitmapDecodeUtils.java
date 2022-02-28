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

package com.huawei.hms.videoeditor.ui.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.widget.ImageView;

import com.huawei.hms.videoeditor.utils.SmartLog;

import java.io.IOException;
import java.io.InputStream;

public final class BitmapDecodeUtils {
    private static final String TAG = "BitmapDecodeUtils";

    private static final long BITMAP_THRESHOLD_SIZE = 1024;

    public static Bitmap decodeFile(String path) {
        if (TextUtils.isEmpty(path)) {
            SmartLog.w(TAG, "decodeFile path invalid return!");
            return null;
        }
        Bitmap bitmap = null;

        BitmapFactory.Options options = getBitmapOptions(path);
        int max = Math.max(options.outWidth, options.outHeight);
        float scale = 1;
        if (max > BITMAP_THRESHOLD_SIZE) {
            scale = max / (BITMAP_THRESHOLD_SIZE * 1.0f);
        }
        options.inSampleSize = (int) (scale < 1 ? 1 : scale);
        options.inJustDecodeBounds = false;

        try {
            bitmap = BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            SmartLog.e(TAG, "decodeFile failed :" + e.getMessage());
        }

        if (bitmap != null) {
            int degree = getImageDegree(path);
            if (degree > 0) {
                Matrix matrix = new Matrix();

                matrix.postRotate(degree);

                Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                bitmap = bm;
            }
        }

        return bitmap;
    }

    public static int getImageDegree(String path) {
        if (TextUtils.isEmpty(path)) {
            SmartLog.w(TAG, "getImageDegree path invalid return!");
            return -1;
        }
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            SmartLog.e(TAG, "exif failed :" + e.getMessage());
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            return orientation2Degree(orientation);
        }
        SmartLog.w(TAG, "getImageDegree exif invalid return!");
        return -1;
    }

    private static int orientation2Degree(int orientation) {
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }

    public static Bitmap decodeStream(InputStream inputStream) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            SmartLog.e(TAG, "decodeStream failed :" + e.getMessage());
        }
        return bitmap;
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeByteArray(data, offset, length);
        } catch (Exception e) {
            SmartLog.e(TAG, "decodeByteArray failed :" + e.getMessage());
        }
        return bitmap;
    }

    public static int getFitInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options) {
        int inSampleSize = 1;
        if (options.outWidth > reqWidth || options.outHeight > reqHeight) {
            int widthRatio = Math.round((float) options.outWidth / (float)reqWidth);
            int heightRatio = Math.round((float) options.outHeight / (float)reqHeight);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    public static Bitmap getFitSampleBitmap(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = getFitInSampleSize(width, height, options);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            SmartLog.e(TAG, "decodeFile failed :" + e.getMessage());
        }
        return bitmap;
    }

    public static BitmapFactory.Options getBitmapOptions(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options;
    }

    public static void loadBitmap2ImageView(ImageView iv,String path) {
        if (iv == null) {
            SmartLog.e(TAG, "loadBitmap2ImageView ImageView null return");
            return;
        }
        if (iv.getWidth() < 1 || iv.getHeight() < 1) {
            SmartLog.e(TAG, "loadBitmap2ImageView ImageView invalid size return width:"
                + iv.getWidth() + " height:" + iv.getHeight());
            return;
        }
        iv.setImageBitmap(getFitSampleBitmap(path, iv.getWidth(), iv.getHeight()));
    }
}
