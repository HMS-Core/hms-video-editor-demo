
/*
 *   Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.hms.videoeditor.ui.mediaeditor.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuConfigBean {

    private List<EditMenuBean> editMenu;

    private List<EditMenuBean> operates;

    public List<EditMenuBean> getEditMenu() {
        List<EditMenuBean> menus = new ArrayList<>();
        if (editMenu != null) {
            menus.addAll(editMenu);
        }
        return menus;
    }

    public void setEditMenu(List<EditMenuBean> editMenu) {
        this.editMenu = editMenu;
    }

    public List<EditMenuBean> getOperates() {
        return operates;
    }

    public void setOperates(List<EditMenuBean> operates) {
        this.operates = operates;
    }
}
