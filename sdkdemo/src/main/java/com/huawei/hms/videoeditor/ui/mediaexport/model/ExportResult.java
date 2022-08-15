/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.model;

import android.net.Uri;

public class ExportResult {
    private Uri uri;

    private String videoPath;

    private boolean isSuccess;

    private int resultCode;

    public ExportResult(Uri uri, String videoPath, boolean isSuccess) {
        this.uri = uri;
        this.videoPath = videoPath;
        this.isSuccess = isSuccess;
    }

    public ExportResult(boolean isSuccess, int resultCode) {
        this.isSuccess = isSuccess;
        this.resultCode = resultCode;
    }

    public Uri getUri() {
        return uri;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public int getResultCode() {
        return resultCode;
    }
}
