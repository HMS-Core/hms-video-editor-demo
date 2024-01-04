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

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.videoeditor.utils.SmartLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {
    private static final String TAG = "FileUtil";

    private static final String GALLERY_NEW_PACKAGE_NAME = "com.huawei.photos";

    private static final String GALLERY_OLD_PACKAGE_NAME = "com.android.gallery3d";

    private static final String SINGLE_PHOTO_ACTIVITY = "com.huawei.gallery.app.SinglePhotoActivity";

    public static final int SIZETYPE_B = 1;

    public static final int SIZETYPE_KB = 2;

    public static final int SIZETYPE_MB = 3;

    public static final int SIZETYPE_GB = 4;

    public static String[] getFileList(String path, FilenameFilter filter) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            List<String> list = new ArrayList<>();
            try {
                Stream<Path> entrys = Files.walk(Paths.get(path), 1);
                list = entrys.filter(Files::isRegularFile)
                        .filter(p -> filter.accept(null, p.getFileName().toString()))
                        .map(Path::toString)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                Log.e(TAG, "Failed to walk " + path + ", " + e.getMessage());
            }
            return list.toArray(new String[0]);
        }

        File file = new File(path);
        return file.list(filter);
    }

    public static boolean isImageByPath(String imagePath) {
        if (StringUtil.isEmpty(imagePath)) {
            return false;
        }
        String localPath = imagePath.trim();
        return localPath.toLowerCase(Locale.ENGLISH).endsWith(".jpg")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".jpe")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".png")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".gif")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".jpeg")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".bmp")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".wbmp")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".webp")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".heic")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".dng")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".arw")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".cr2")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".nef")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".nrw")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".rw2")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".orf")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".raf")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".pef")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".srw")
            || localPath.toLowerCase(Locale.ENGLISH).endsWith(".heif");
    }

    public static long getFileSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0L;
        if (file.isDirectory()) {
            SmartLog.e(TAG, "please input file,not directory");
        } else {
            blockSize = getFileSize(file);
        }
        return (long) formetFileSize(blockSize, sizeType);
    }

    public static String readAssetsFile(Context context, String fileName) {
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try {
            inputReader =
                new InputStreamReader(context.getResources().getAssets().open(fileName), StandardCharsets.UTF_8);
            bufReader = new BufferedReader(inputReader);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException error) {
            SmartLog.e(TAG, "readAssetsFile failed");
            return "";
        } finally {
            try {
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (IOException error) {
                SmartLog.e(TAG, "readAssetsFile failed");
            }

            try {
                if (bufReader != null) {
                    bufReader.close();
                }
            } catch (IOException error) {
                SmartLog.e(TAG, "readAssetsFile failed");
            }
        }
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
        long size = 0L;
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

    public static boolean isVideo(String path) {
        return isVideoByPath(path);
    }

    public static boolean isVideoByPath(String filePath) {
        if (StringUtil.isEmpty(filePath)) {
            return false;
        }
        String localFilePath = filePath.trim();
        return localFilePath.toLowerCase(Locale.ENGLISH).endsWith(".mp4")
            || localFilePath.toLowerCase(Locale.ENGLISH).endsWith(".3gp")
            || localFilePath.toLowerCase(Locale.ENGLISH).endsWith(".3g2")
            || localFilePath.toLowerCase(Locale.ENGLISH).endsWith(".mkv")
            || localFilePath.toLowerCase(Locale.ENGLISH).endsWith(".mov")
            || localFilePath.toLowerCase(Locale.ENGLISH).endsWith(".m4v");
    }

    /**
     * bitmap 2 File
     *
     * @param bitmap bitmap
     * @param bitName bitName
     * @throws IOException
     */
    public static String saveBitmap(Context context, Bitmap bitmap, String bitName) throws IOException {
        String sdPath = context.getFilesDir().getAbsolutePath();
        File file = new File(sdPath + File.separator + bitName);
        if (file.exists()) {
            if (!file.delete()) {
                SmartLog.i(TAG, "saveBitmap file.delete fail");
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            SmartLog.e(TAG, e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                SmartLog.e(TAG, e.getMessage());
            }
        }
        return sdPath + File.separator + bitName;
    }

    /**
     * bitmap 2 File
     */
    public static String saveBitmap(Context context, String projectId, Bitmap bitmap, String bitName) {
        String sdPath = context.getFilesDir().getAbsolutePath() + File.separator + "project/" + projectId;
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

    public static boolean saveBitmap(Bitmap bitmap, int quality, File file) {
        FileOutputStream out = null;
        try {
            out = openOutputStream(file, false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
        } catch (Exception error) {
            SmartLog.e(TAG, error.getMessage());
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    SmartLog.e(TAG, e.getMessage());
                }
            }
        }
        return true;
    }

    public static FileOutputStream openOutputStream(final File outFile, final boolean append) throws IOException {
        String filePath = outFile.getCanonicalPath();
        if (isIllegalPath(filePath)) {
            throw new IOException("outFile path illegal");
        }
        if (outFile.exists()) {
            if (outFile.isDirectory()) {
                throw new IOException("outFile '" + outFile + "' exists but is a directory");
            }
            if (!outFile.canWrite()) {
                throw new IOException("outFile '" + outFile + "' cannot be written to");
            }
        } else {
            final File parent = outFile.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(filePath, append);
    }

    public static boolean isIllegalPath(String url) {
        return null == url || url.contains("../") || url.contains("./") || url.contains("%00")
            || url.contains(".\\.\\");
    }

    public static boolean isPathExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File file = new File(path);
        return file.exists();
    }

    private static boolean copyFile(String sourcePath, String targetPath) {
        if (TextUtils.isEmpty(sourcePath) || TextUtils.isEmpty(targetPath)) {
            return false;
        }
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);
        if (sourcePath.equals(targetPath) && targetFile.exists()) {
            return false;
        }
        if (!targetFile.exists()) {
            String path = targetPath.substring(0, targetPath.lastIndexOf("/"));
            File file = new File(path);
            file.mkdirs();
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] bytes = new byte[8192];
            int i;
            while ((i = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, i);
            }
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                SmartLog.e(TAG, "IOException: " + e.getMessage());
            }
        }
        return true;
    }

    private static boolean copyFile(String oldPath, OutputStream outputStream) {
        try {
            int byteSum = 0;
            int byteRead = 0;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                byte[] buffer = new byte[2048];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    byteSum += byteRead;
                    System.out.println(byteSum);
                    outputStream.write(buffer, 0, byteRead);
                }
                inStream.close();
                outputStream.close();
                return true;
            } else {
                SmartLog.e(TAG, "The file does not exist.");
            }
        } catch (Exception e) {
            SmartLog.e(TAG, "Failed to copy a single file.");
        }
        return false;
    }

    /**
     * Save to Album
     */
    public static boolean saveToLocalSystem(Context context, boolean isVideo, String filePath, String videoOutputPath,
        String photoOutputPath) {
        boolean isSuccess = false;
        File file = new File(filePath);
        if (file.exists()) {
            if (isVideo) {
                copyFile(file.getAbsolutePath(), videoOutputPath);
                isSuccess = saveVideoToSystemAlbum(context, videoOutputPath);
            } else {
                copyFile(file.getAbsolutePath(), photoOutputPath);
                isSuccess = saveImageToSystemAlbum(context, photoOutputPath);
            }
        }
        return isSuccess;
    }

    public static boolean saveImageToSystemAlbum(Context context, String imageFile) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            ContentValues contentValues = getImageContentValues(imageFile, System.currentTimeMillis());
            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            contentResolver.update(uri, contentValues, null, null);
            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            final Uri localUri = Uri.fromFile(new File(imageFile));
            intent.setData(localUri);
            context.sendBroadcast(intent);
            return true;
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
            return false;
        }
    }

    private static boolean saveVideoToSystemAlbum(Context context, String videoFile) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            ContentValues contentValues = getVideoContentValues(new File(videoFile), System.currentTimeMillis());
            Uri localUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            contentResolver.update(localUri, contentValues, null, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.Q) {
                try {
                    OutputStream out = context.getContentResolver().openOutputStream(localUri);
                    copyFile(videoFile, out);
                } catch (IOException e) {
                    SmartLog.e(TAG, "IOException: " + e.getMessage());
                }
            }
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri));
            return true;
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
            return false;
        }
    }
    private static ContentValues getImageContentValues(String imagePath, long paramLong) {
        File imageFile = new File(imagePath);
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", imageFile.getName());
        contentValues.put("_display_name", imageFile.getName());
        contentValues.put("mime_type", "image/jpeg");
        contentValues.put("datetaken", paramLong);
        contentValues.put("date_modified", paramLong);
        contentValues.put("date_added", paramLong);
        contentValues.put("orientation", 0);
        contentValues.put("_data", imageFile.getAbsolutePath());
        contentValues.put("_size", imageFile.length());
        contentValues.put("width", ImageUtil.getImageWidthAndHeight(imagePath)[0]);
        contentValues.put("height", ImageUtil.getImageWidthAndHeight(imagePath)[1]);
        return contentValues;
    }

    private static ContentValues getVideoContentValues(File videoFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", videoFile.getName());
        localContentValues.put("_display_name", videoFile.getName());
        localContentValues.put("mime_type", "video/mp4");
        localContentValues.put("datetaken", paramLong);
        localContentValues.put("date_modified", paramLong);
        localContentValues.put("date_added", paramLong);
        localContentValues.put("_data", videoFile.getAbsolutePath());
        localContentValues.put("_size", videoFile.length());
        return localContentValues;
    }

    /**
     * Save the file to a local directory.
     *
     * @param pathName File path name
     * @throws RemoteException Exception thrown
     * @return File path
     */
    public static String copyFiles(Context context, String pathName) throws RemoteException {
        String filePath = null;
        InputStream in = null;
        FileOutputStream out = null;
        String path = context.getFilesDir().getAbsolutePath() + File.separator + pathName; // data/dataĿ¼
        File file = new File(path);
        if (file.exists()) {
            SmartLog.d(TAG, "copyData file exists.");
            try {
                filePath = file.getCanonicalPath();
            } catch (IOException e) {
                SmartLog.e(TAG, e.getMessage());
            }
            return filePath;
        }
        try {
            in = context.getAssets().open(pathName);
            out = new FileOutputStream(file);
            int length = -1;
            byte[] buf = new byte[1024];
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            out.flush();
        } catch (RuntimeException e) {
            SmartLog.e(TAG, e.getMessage());
            throw new RemoteException("initialize|initialize fail! Model copy failed.");
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
            throw new RemoteException("initialize|initialize fail! Model load failed.");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    SmartLog.e(TAG, e1.getMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    SmartLog.e(TAG, e1.getMessage());
                }
            }
        }
        try {
            filePath = file.getCanonicalPath();
        } catch (IOException e) {
            SmartLog.e(TAG, e.getMessage());
        }
        return filePath;
    }
}
