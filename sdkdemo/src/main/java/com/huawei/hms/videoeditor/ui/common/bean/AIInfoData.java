/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.common.bean;

public class AIInfoData {
    private String title;

    private String subTitle;

    private int imgResId;

    public AIInfoData(String title, String subTitle, int imgResId) {
        this.title = title;
        this.subTitle = subTitle;
        this.imgResId = imgResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }

    @Override
    public String toString() {
        return "AIInfoData{" + "title='" + title + '\'' + ", subTitle='" + subTitle + '\'' + ", imgResId=" + imgResId
            + '}';
    }
}
