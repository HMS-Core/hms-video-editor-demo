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

import android.graphics.Paint;
import android.graphics.Path;

public class DrawPath {
    public Path path;

    public Paint paint;

    public Position position;

    public GraffitiInfo.Shape shape = GraffitiInfo.Shape.LINE;

    public static class Position {
        public float startX;

        public float startY;

        public float endX;

        public float endY;
    }
}
