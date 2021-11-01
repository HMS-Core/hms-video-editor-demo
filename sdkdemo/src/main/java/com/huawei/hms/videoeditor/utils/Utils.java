
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

package com.huawei.hms.videoeditor.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.security.acl.NotOwnerException;
import java.text.NumberFormat;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.jar.JarException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.huawei.hms.videoeditor.sdk.HVEErrorCode;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class Utils {
    private static final String TAG = "Utils";

    private static final int MAX_VIDEO_WIDTH = 4096;

    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo =
                packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            String mVersionName = packageInfo.versionName;
            return mVersionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersionName: " + e.getMessage());
        }
        return Constant.DEFAULT_VERSION;
    }

    public static boolean isMateX(Activity context) {
        Point p = new Point();
        if (context.getWindowManager() != null && context.getWindowManager().getDefaultDisplay() != null) {
            context.getWindowManager().getDefaultDisplay().getSize(p);
        }
        Log.i(TAG, "isMateX: " + p.x + "y:" + p.y);
        return p.x > 2000;
    }

    public static int getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            SmartLog.e(TAG, e.getMessage());
        }
        return code;
    }

    /**
     * Get exception message, it exclude the sensitive exceptions.
     *
     * @param exception The exception that get message from.
     * @return Exception message, it exclude the sensitive exceptions.
     */
    public static String formateExceptionMessage(Object exception) {
        if (exception == null) {
            return "Null exception message.";
        }

        if (exception instanceof FileNotFoundException) {
            return "Invalid file.";
        } else if (exception instanceof JarException) {
            return "Invalid JAR.";
        } else if (exception instanceof MissingResourceException) {
            return "Invalid resource.";
        } else if (exception instanceof NotOwnerException) {
            return "Invalid owner.";
        } else if (exception instanceof ConcurrentModificationException) {
            return "Detected concurrent modification.";
        } else if (exception instanceof BindException) {
            return "Bind fail.";
        } else if (exception instanceof OutOfMemoryError) {
            return "Internal error.";
        } else if (exception instanceof StackOverflowError) {
            return "Internal error.";
        } else if (exception instanceof SQLException) {
            return "Database error.";
        } else if (exception instanceof IOException) {
            return "IO error.";
        } else if (exception instanceof Exception) {
            return ((Exception) exception).getMessage();
        } else {
            return "Unknown error.";
        }
    }

    public static boolean showFormatSupportToast(Context context, List<Uri> originUriList,
        List<MediaData> nowFileList) {
        if (nowFileList != null) {
            Iterator<MediaData> iterator = nowFileList.iterator();
            while (iterator.hasNext()) {
                MediaData mediaData = iterator.next();
                if (isOutOf4K(mediaData)) {
                    iterator.remove();
                }
            }
        }

        if (context == null) {
            SmartLog.e(TAG, "context is null");
            return false;
        }
        if (originUriList == null || originUriList.isEmpty() || nowFileList == null || nowFileList.isEmpty()) {
            SmartLog.e(TAG, "all materials format not support");
            String tips = context.getString(R.string.error_file_tips_single_video);
            showToastQuickly(context, tips, Toast.LENGTH_LONG);
            return true;
        } else {
            int originSize = originUriList.size();
            int nowSize = nowFileList.size();
            if (originSize > nowSize) {
                String format =
                    context.getResources().getQuantityString(R.plurals.error_file_tips, originSize - nowSize);
                String tips =
                    String.format(Locale.ROOT, format, NumberFormat.getInstance().format(originSize - nowSize));
                showToastQuickly(context, tips, Toast.LENGTH_LONG);
            }
        }
        return false;
    }

    private static Toast mToast;

    public static void showToastQuickly(Context context, String tips, int duration) {
        if (mToast != null) {
            mToast.cancel();
        }
        int normalThemeId = context.getResources().getIdentifier("androidhwext:style/Theme.Emui", null, null);
        context.getTheme().applyStyle(normalThemeId, true);
        mToast = Toast.makeText(context.getApplicationContext(), tips, duration);
        mToast.show();
    }

    public static boolean isNetwork(Context context) {
        ConnectivityManager manager =
            (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public static boolean getDarkModeStatus(Context context) {
        if (context == null) {
            return false;
        }
        int mode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean isOutOf4K(MediaData mediaData) {
        if (mediaData == null) {
            return true;
        }

        if (mediaData.getType() == MediaData.MEDIA_IMAGE) {
            return false;
        }

        if (mediaData.getWidth() > MAX_VIDEO_WIDTH || mediaData.getHeight() > MAX_VIDEO_WIDTH) {
            SmartLog.e(TAG, "isOutOf4K  Width:  " + mediaData.getWidth() + "  ,Height:  " + mediaData.getWidth());
            return true;
        }
        return false;
    }
}
