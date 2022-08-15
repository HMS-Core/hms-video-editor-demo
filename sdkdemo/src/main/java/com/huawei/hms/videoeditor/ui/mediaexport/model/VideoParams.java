/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.model;

public class VideoParams {
    private int layoutWidth;

    private int layoutHeight;

    public VideoParams() {

    }

    public VideoParams(int layoutWidth, int layoutHeight) {
        this.layoutWidth = layoutWidth;
        this.layoutHeight = layoutHeight;
    }

    public int getLayoutWidth() {
        return layoutWidth;
    }

    public int getLayoutHeight() {
        return layoutHeight;
    }
}
