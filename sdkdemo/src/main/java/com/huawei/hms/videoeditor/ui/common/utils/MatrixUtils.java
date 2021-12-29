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

package com.huawei.hms.videoeditor.ui.common.utils;

import android.graphics.Matrix;

public class MatrixUtils {
    private final static float[] MATRIX_V = new float[9];

    public static float getRotate(Matrix matrix) {
        matrix.getValues(MATRIX_V);
        return Math.round(Math.atan2(MATRIX_V[Matrix.MSKEW_X], MATRIX_V[Matrix.MSCALE_X]) * (180 / Math.PI));
    }
}
