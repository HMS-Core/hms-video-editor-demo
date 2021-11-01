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

package com.huawei.hms.videoeditor.ui.mediaeditor.graffiti;

public class GraffitiInfo {
    public int stokeWidth;

    public int stokeColor;

    public int stokeAlpha;

    public Shape shape = Shape.LINE;

    public TYPE type = TYPE.DEFAULT;

    public int visible;

    public enum TYPE {
        DEFAULT,
        COLOR,
        WIDTH,
        ALPHA,
        SHAPE,
        SAVE
    }

    public enum Shape {
        LINE,
        RECTANGLE,
        CIRCLE,
        TRIANGLE,
        STRACTLINE,
        FIVESTAR,
        DIAMOND,
        ARROW
    }
}
