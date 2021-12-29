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

package com.huawei.hms.videoeditor.ui.common.view.crop;

/**
 * GeometryMathUtils
 */
public class GeometryMathUtils {
    // Math operations for 2d vectors
    public static float clamp(float i, float low, float high) {
        return Math.max(Math.min(i, high), low);
    }

    public static float vectorLength(float[] a) {
        return (float) Math.sqrt(a[0] * a[0] + a[1] * a[1]);
    }

    public static float[] shortestVectorFromPointToLine(float[] point, float[] line) {
        float x1 = line[0];
        float x2 = line[2];
        float y1 = line[1];
        float y2 = line[3];
        float xdelt = x2 - x1;
        float ydelt = y2 - y1;
        if (xdelt == 0 && ydelt == 0) {
            return new float[]{};
        }
        float u = ((point[0] - x1) * xdelt + (point[1] - y1) * ydelt) / (xdelt * xdelt + ydelt * ydelt);
        float[] ret = {(x1 + u * (x2 - x1)), (y1 + u * (y2 - y1))};
        return new float[]{ret[0] - point[0], ret[1] - point[1]};
    }

    public static float[] lineIntersect(float[] line1, float[] line2) {
        float a0 = line1[0];
        float a1 = line1[1];
        float a2 = line1[2];
        float a3 = line1[3];

        float b0 = line2[0];
        float b1 = line2[1];
        float b2 = line2[2];
        float b3 = line2[3];

        float t0 = a0 - a2;
        float t1 = a1 - a3;
        float t2 = a2 - b2;
        float t3 = b3 - a3;
        float t4 = b0 - b2;
        float t5 = b1 - b3;

        float d = t1 * t4 - t0 * t5;
        if (d == 0) {
            return new float[]{};
        }
        float u = (t3 * t4 + t5 * t2) / d;
        return new float[]{a2 + u * t0, a3 + u * t1};
    }

    public static float scalarProjection(float[] a, float[] b) {
        float l = (float) Math.sqrt(b[0] * b[0] + b[1] * b[1]);
        return dotProduct(a, b) / l;
    }

    public static float[] normalize(float[] a) {
        float l = (float) Math.sqrt(a[0] * a[0] + a[1] * a[1]);
        float[] b = {a[0] / l, a[1] / l};
        return b;
    }

    public static float dotProduct(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1];
    }
}
