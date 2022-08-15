/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.
 */

package com.hwa.demo.screenrecorddemo;

import java.io.File;

import android.content.Context;

public class StorageFolders {
    private File defaultInternal;
    private File customFolderInDCIM;
    private File customInternalFolder;
    private File cameraFolder;
    private static StorageFolders instance;

    private StorageFolders(Context appContext) {
        customFolderInDCIM = new File("/sdcard/DCIM/CustomFolder");
        cameraFolder = new File("/sdcard/DCIM/Camera");
        customInternalFolder = appContext.getExternalFilesDir("CustomFolder");
        defaultInternal = appContext.getExternalFilesDir("ScreenRecords");
    }

    public static StorageFolders getInstance(Context appContext) {
        if (instance == null)
            instance = new StorageFolders(appContext);
        return instance;
    }

    public File getDefaultInternal() {
        return defaultInternal;
    }

    public File getCustomFolderInDCIM() {
        return customFolderInDCIM;
    }

    public File getCustomInternalFolder() {
        return customInternalFolder;
    }

    public File getCameraFolder() {
        return cameraFolder;
    }

}
