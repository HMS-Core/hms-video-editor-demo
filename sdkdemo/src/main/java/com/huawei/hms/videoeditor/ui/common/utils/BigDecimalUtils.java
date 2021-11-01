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

package com.huawei.hms.videoeditor.ui.common.utils;

import java.math.BigDecimal;

public class BigDecimalUtils {

    private static final int DEF_DIV_SCALE = 10;

    public static double add(double v1, double v2) {
        BigDecimal decimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal decimal2 = new BigDecimal(Double.toString(v2));
        return decimal1.add(decimal2).doubleValue();
    }

    public static BigDecimal add(String v1, String v2) {
        BigDecimal decimal1 = new BigDecimal(v1);
        BigDecimal decimal2 = new BigDecimal(v2);
        return decimal1.add(decimal2);
    }

    public static double sub(double v1, double v2) {
        BigDecimal decimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal decimal2 = new BigDecimal(Double.toString(v2));
        return decimal1.subtract(decimal2).doubleValue();
    }

    public static BigDecimal round(String v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal decimal = new BigDecimal(v);
        return decimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    public static double mul(double v1, double v2) {
        BigDecimal decimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal decimal2 = new BigDecimal(Double.toString(v2));
        return round(decimal1.multiply(decimal2).doubleValue(), DEF_DIV_SCALE);
    }

    public static double mul2(double v1, double v2, int scale) {
        BigDecimal decimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal decimal2 = new BigDecimal(Double.toString(v2));
        return round(decimal1.multiply(decimal2).doubleValue(), scale);
    }

    public static BigDecimal div(String v1, String v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    public static double div(double v1, double value2) {
        if (value2 == 0) {
            return v1;
        }
        return div(v1, value2, DEF_DIV_SCALE);
    }

    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }

        BigDecimal decimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal decimal2 = new BigDecimal(Double.toString(v2));
        return decimal1.divide(decimal2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static float div(float v1, float v2) {
        if (v2 == 0) {
            return v1;
        }
        return div(v1, v2, DEF_DIV_SCALE);
    }

    public static float div(float v1, float v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static BigDecimal div(String v1, String v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal decimal1 = new BigDecimal(v1);
        BigDecimal decimal2 = new BigDecimal(v2);
        return decimal1.divide(decimal2, scale, BigDecimal.ROUND_HALF_UP);
    }

    public static boolean compareTo(double v1, double v2) {
        BigDecimal decimal1 = new BigDecimal(v1);
        BigDecimal decimal2 = new BigDecimal(v2);
        int bj = decimal1.compareTo(decimal2);
        boolean res;
        if (bj > 0) {
            res = true;
        } else {
            res = false;
        }
        return res;
    }

    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal decimal = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return decimal.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
