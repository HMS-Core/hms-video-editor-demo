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

package com.huawei.hms.videoeditor.ui.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.videoeditor.common.agc.HVEApplication;
import com.huawei.hms.videoeditor.sdk.bean.HVEVisibleFormatBean;
import com.huawei.hms.videoeditor.sdk.util.HVEUtil;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.tools.EditorRuntimeException;

public class FileUtil {
    private static final String TAG = "FileUtil";

    private static final String GALLERY_NEW_PACKAGE_NAME = "com.huawei.photos";

    private static final String GALLERY_OLD_PACKAGE_NAME = "com.android.gallery3d";

    private static final String SINGLE_PHOTO_ACTIVITY = "com.huawei.gallery.app.SinglePhotoActivity";

    public static final int SIZETYPE_B = 1;

    public static final int SIZETYPE_KB = 2;

    public static final int SIZETYPE_MB = 3;

    public static final int SIZETYPE_GB = 4;

    public static long getFileSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        if (file.isDirectory()) {
            SmartLog.e(TAG, "please input file,not directory");
        } else {
            blockSize = getFileSize(file);
        }
        return (long) formetFileSize(blockSize, sizeType);
    }

    public static String readJsonFile(String path) {
        String jsonStr = "";
        Reader reader = null;
        try {
            File jsonFile = new File(path);
            reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch;
            StringBuffer sb = new StringBuffer();
            char[] buff = new char[1024];
            while ((ch = reader.read(buff, 0, 1024)) != -1) {
                sb.append(buff, 0, ch);
            }
            jsonStr = sb.toString();
        } catch (IOException e) {
            SmartLog.e(TAG, "IOException: " + e.toString());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                SmartLog.e(TAG, "IOException: " + e.toString());
            }
        }
        return jsonStr;
    }

    private static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (IOException e) {
                SmartLog.e(TAG, "getFileSize IOException");
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        SmartLog.e(TAG, "getFileSize IOException");
                    }
                }
            }
        } else {
            try {
                boolean isSuccess = file.createNewFile();
                SmartLog.i(TAG, "file.createNewFile" + isSuccess);
            } catch (IOException e) {
                SmartLog.e(TAG, "getFileSize IOException");
            }
            SmartLog.e(TAG, "getFileSize file not exists");
        }
        return size;
    }

    private static double formetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        try {
            switch (sizeType) {
                case SIZETYPE_B:
                    fileSizeLong = Double.valueOf(df.format((double) fileS));
                    break;
                case SIZETYPE_KB:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                    break;
                case SIZETYPE_MB:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                    break;
                case SIZETYPE_GB:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                    break;
                default:
                    break;
            }
        } catch (NumberFormatException e) {
            SmartLog.e(TAG, "" + e.getMessage());
        }
        return fileSizeLong;
    }

    public static Uri insert(ContentResolver resolver, Uri tableUrl, ContentValues contentValues) {
        if (resolver == null || contentValues == null) {
            SmartLog.w(TAG, "insert: invalid input");
            return null;
        }
        try {
            return resolver.insert(tableUrl, contentValues);
        } catch (SQLiteException ex) {
            SmartLog.w(TAG, "insert: SQLiteException");
        }

        return null;
    }

    public static void goToPhotoBrowser(Context context, Uri videoUri) {
        if (context == null || videoUri == null) {
            Log.w(TAG, "goToPhotoBrowser: invalid input");
            return;
        }
        boolean isNew = context.getPackageManager().getLaunchIntentForPackage(GALLERY_NEW_PACKAGE_NAME) != null;
        Log.i(TAG, "Is new gallery package ? " + isNew);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(videoUri, "video/mp4");
        intent.setClassName(isNew ? GALLERY_NEW_PACKAGE_NAME : GALLERY_OLD_PACKAGE_NAME, SINGLE_PHOTO_ACTIVITY);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex1) {
            Log.w(TAG, "goToPhotoBrowser: package not found");
        }
    }

    public static boolean isVideo(String path) throws EditorRuntimeException {
        HVEVisibleFormatBean bean = HVEUtil.getVideoProperty(path);
        if (bean == null) {
            return false;
        }
        return true;
    }

    /**
     * bitmap 2 File
     */
    public static String saveBitmap(Context context, String projectId, Bitmap bitmap, String bitName) {
        String sdPath = context.getFilesDir().getAbsolutePath() + File.separator + HVEApplication.getInstance().getTag()
            + "project/" + projectId;
        File fileDir = new File(sdPath);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                SmartLog.d(TAG, "saveBitmap fileDir.mkdirs fail");
                return "";
            }
        }
        File file = new File(sdPath + File.separator + bitName);
        if (file.exists()) {
            if (!file.delete()) {
                SmartLog.e(TAG, "saveBitmap file.delete fail");
                return "";
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
            return "";
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    SmartLog.e(TAG, e.getMessage());
                }
            }
        }
        return sdPath + File.separator + bitName;
    }

    public static boolean isPathExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File file = new File(path);
        return file.exists();
    }
}
