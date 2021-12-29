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

package com.huawei.hms.videoeditor.ui.common.view.tab.bottom;

public class TabBottomInfo<Color> {

    public int nameId;

    public int nameResId;

    public int drawableIcon;

    public Color textDefaultColor;

    public Color textSelectColor;

    public boolean enable;

    public boolean responseEnable = true;

    public TabBottomInfo(int nameRes, int nameId, int drawableIcon, Color textDefaultColor, Color textSelectColor,
        boolean enable) {
        this.nameResId = nameRes;
        this.nameId = nameId;
        this.drawableIcon = drawableIcon;
        this.textDefaultColor = textDefaultColor;
        this.textSelectColor = textSelectColor;
        this.enable = enable;
    }

    public void setResponseEnable(boolean responseEnable) {
        this.responseEnable = responseEnable;
    }

}
