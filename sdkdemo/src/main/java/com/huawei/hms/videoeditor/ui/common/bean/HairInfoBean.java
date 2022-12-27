/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.common.bean;

import java.util.Objects;

/**
 * 功能描述
 *
 * @since 2022-11-02
 */
public class HairInfoBean {
    private String localPath;

    private String name;

    public HairInfoBean(String localPath, String name) {
        this.localPath = localPath;
        this.name = name;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "HairInfoBean{" + "localPath='" + localPath + '\'' + ", name='" + name + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HairInfoBean that = (HairInfoBean) o;
        return Objects.equals(localPath, that.localPath) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localPath, name);
    }
}
