
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

package com.huawei.hms.videoeditor.ui.mediaeditor.menu;

import java.util.List;

import androidx.annotation.NonNull;

public class EditMenuBean implements Cloneable {
    private int id;

    private int parentId;

    private String name;

    private String cnName;

    private String drawableName;

    private int enable;

    private int isAdvanced;

    private List<EditMenuBean> children;

    private List<EditMenuBean> operates;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getDrawableName() {
        return drawableName;
    }

    public void setDrawableName(String drawableName) {
        this.drawableName = drawableName;
    }

    public boolean isEnable() {
        return enable == 1;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getIsAdvanced() {
        return isAdvanced;
    }

    public void setIsAdvanced(int isAdvanced) {
        this.isAdvanced = isAdvanced;
    }

    public List<EditMenuBean> getChildren() {
        return children;
    }

    public void setChildren(List<EditMenuBean> children) {
        this.children = children;
    }

    public List<EditMenuBean> getOperates() {
        return operates;
    }

    public void setOperates(List<EditMenuBean> operates) {
        this.operates = operates;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "EditMenuBean{" + "id=" + id + ", parentId=" + parentId + ", name='" + name + '\'' + ", cnName='"
            + cnName + '\'' + ", drawableName='" + drawableName + '\'' + ", enable=" + enable + ", isAdvanced="
            + isAdvanced + ", children=" + children + ", operates=" + operates + '}';
    }
}
