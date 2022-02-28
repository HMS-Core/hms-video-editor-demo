/*
 *   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.hms.videoeditor.ui.common.database.bean;

import com.huawei.hms.videoeditor.sdk.util.KeepOriginal;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@KeepOriginal
@Entity
public class CloudMaterialDaoBean {
    @Id
    String id;

    String previewUrl;

    String name;

    String localPath;

    long duration;

    int type;

    String categoryName;

    int localDrawableId;

    @Generated(hash = 1311768791)
    public CloudMaterialDaoBean() {
    }

    @Generated(hash = 1362536480)
    public CloudMaterialDaoBean(String id, String previewUrl, String name, String localPath, long duration, int type,
                                String categoryName, int localDrawableId) {
        this.id = id;
        this.previewUrl = previewUrl;
        this.name = name;
        this.localPath = localPath;
        this.duration = duration;
        this.type = type;
        this.categoryName = categoryName;
        this.localDrawableId = localDrawableId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getLocalDrawableId() {
        return localDrawableId;
    }

    public void setLocalDrawableId(int localDrawableId) {
        this.localDrawableId = localDrawableId;
    }
}
