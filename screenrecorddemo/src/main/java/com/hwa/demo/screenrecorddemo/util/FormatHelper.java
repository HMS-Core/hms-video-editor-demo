/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.
 */

package com.hwa.demo.screenrecorddemo.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatHelper {

    private FormatHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String durationFormat(long milliseconds) {
        StringBuilder buf = new StringBuilder();
        long hours = (milliseconds / (1000 * 60 * 60));
        long minutes = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60));
        long seconds = (((milliseconds % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        if (hours > 0)
            buf.append(String.format(Locale.ENGLISH,"%02d", hours)).append(":");

        buf.append(String.format(Locale.ENGLISH,"%02d", minutes))
                .append(":")
                .append(String.format(Locale.ENGLISH,"%02d", seconds));

        return buf.toString();
    }

    public static String durationFormatWithMillisecond(long milliseconds) {
        StringBuilder buf = new StringBuilder();
        long hours = (milliseconds / (1000 * 60 * 60));
        long minutes = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60));
        long seconds = (((milliseconds % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        long ms = milliseconds % 1000;
        if (hours > 0)
            buf.append(String.format(Locale.ENGLISH,"%02d", hours)).append(":");

        buf.append(String.format(Locale.ENGLISH,"%02d", minutes))
                .append(":")
                .append(String.format(Locale.ENGLISH,"%02d", seconds));

        if(ms > 0){
            buf.append(":").append(ms);
        }

        return buf.toString();
    }

    public static String dateFormat(Date date){
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        return dt.format(date);
    }

    public static Uri contentUriFormat(Context context, File videoFile){
        Uri uri = null;
        String filePath;
        try {
            filePath = videoFile.getCanonicalPath();
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{(MediaStore.Video.Media._ID)},
                    MediaStore.Video.Media.DATA + "=? ",
                    new String[]{(filePath)}, null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/video/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            } else if (videoFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, filePath);
                uri = context.getContentResolver().insert(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            }
            if(cursor != null) {
                cursor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return uri;
    }

}
