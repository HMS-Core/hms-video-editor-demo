/*
 * Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.videoeditorkit.videoeditdemo.util;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    /**
     * Check Permissions
     *
     * @return true：authorized； false：unauthorized；
     */
    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Detect Multiple Permissions
     *
     * @return Unauthorized Permission
     */
    public static List<String> checkManyPermissions(Context context, String[] permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (!checkPermission(context, permission))
                permissionList.add(permission);
        }
        return permissionList;
    }

    /**
     * Request Multiple Permissions
     */
    public static void requestManyPermissions(Context context, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
    }

    /**
     * Determine whether the permission has been denied.
     *
     * @return Returns the status of the permission
     * @describe :This method returns true if the application has previously requested this permission but the user
     *           denies it.
     *           ----------- if an application request permission for that first time or a us denied permission request
     *           in the past,
     *           -----------If the Don't ask again option is selected in the permission request system dialog box, this
     *           method returns false.
     */
    public static boolean judgePermission(Context context, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission);
    }

    /**
     * Detect Multiple Permissions
     *
     * @param context context
     * @param permissions Permission
     * @param callBack Callback Listening
     */
    public static void checkManyPermissions(Context context, String[] permissions, PermissionCheckCallBack callBack) {
        List<String> permissionList = checkManyPermissions(context, permissions);
        if (permissionList.size() == 0) {  // User Granted Permissions
            callBack.onHasPermission();
        } else {
            boolean isFirst = true;
            for (int i = 0; i < permissionList.size(); i++) {
                String permission = permissionList.get(i);
                if (judgePermission(context, permission)) {
                    isFirst = false;
                    break;
                }
            }
            String[] unauthorizedMorePermissions = permissionList.toArray(new String[0]);
            if (isFirst) {
                // The user has rejected the permission application before.
                callBack.onUserRejectAndDontAsk(unauthorizedMorePermissions);
            } else {
                // The user has previously rejected and selected Do not ask, and the user applies for permission for the
                // first time.
                callBack.onUserHasReject(unauthorizedMorePermissions);
            }
        }
    }

    /**
     * The user applies for multiple permissions.
     */
    public static void onRequestMorePermissionsResult(Context context, String[] permissions,
        PermissionCheckCallBack callback) {
        boolean isBannedPermission = false;
        List<String> permissionList = checkManyPermissions(context, permissions);
        if (permissionList.size() == 0)
            callback.onHasPermission();
        else {
            for (int i = 0; i < permissionList.size(); i++) {
                if (!judgePermission(context, permissionList.get(i))) {
                    isBannedPermission = true;
                    break;
                }
            }
            // Re-ask permission disabled
            if (isBannedPermission) {
                callback.onUserRejectAndDontAsk(permissions);
            } else {
                // Deny Permissions
                callback.onUserHasReject(permissions);
            }
        }
    }

    /**
     * The permission setting page is displayed.
     */
    @SuppressLint("ObsoleteSdkInt")
    public static void toAppSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }

    public interface PermissionCheckCallBack {

        /**
         * User Granted Permissions
         */
        void onHasPermission();

        /**
         * User Denied Permissions
         *
         * @param permission Denied Permissions
         */
        void onUserHasReject(String... permission);

        /**
         * The user has rejected the request and selected Do not ask again.
         * The user applies for permission for the first time.
         *
         * @param permission Denied Permissions
         */
        void onUserRejectAndDontAsk(String... permission);
    }
}
