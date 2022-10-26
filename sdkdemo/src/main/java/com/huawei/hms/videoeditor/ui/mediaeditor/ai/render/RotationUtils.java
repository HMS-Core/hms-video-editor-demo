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

package com.huawei.hms.videoeditor.ui.mediaeditor.ai.render;

public class RotationUtils {

    public static final int CoordsPerVertex = 2;

    public static final float CubeVertices[] = new float[] {-1.0f, -1.0f,  // 0 bottom left
        1.0f, -1.0f,  // 1 bottom right
        -1.0f, 1.0f,  // 2 top left
        1.0f, 1.0f,  // 3 top right
    };

    public static final float TextureVertices[] = new float[] {0.0f, 0.0f, // 0 left bottom
        1.0f, 0.0f, // 1 right bottom
        0.0f, 1.0f, // 2 left top
        1.0f, 1.0f  // 3 right top
    };

    public static final float TextureVertices_180[] = new float[] {1.0f, 1.0f, // right top
        0.0f, 1.0f, // left top
        1.0f, 0.0f, // right bottom
        0.0f, 0.0f, // left bottom
    };
}
