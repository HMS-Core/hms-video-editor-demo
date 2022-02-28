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

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalUtil {
    private static final int DEF_DIV_SCALE = 10;

    private static final DecimalFormat DF = new DecimalFormat("#");

    public static double add(double num1, double num2) {
        BigDecimal temp1 = new BigDecimal(Double.toString(num1));
        BigDecimal temp2 = new BigDecimal(Double.toString(num2));
        return temp1.add(temp2).doubleValue();
    }

    public static float add(float num1, float num2) {
        BigDecimal temp1 = new BigDecimal(Float.toString(num1));
        BigDecimal temp2 = new BigDecimal(Float.toString(num2));
        return temp1.add(temp2).floatValue();
    }

    public static BigDecimal add(String num1, String num2) {
        BigDecimal temp1 = new BigDecimal(num1);
        BigDecimal temp2 = new BigDecimal(num2);
        return temp1.add(temp2);
    }

    public static float sub(float num1, float num2) {
        BigDecimal temp1 = new BigDecimal(Float.toString(num1));
        BigDecimal temp2 = new BigDecimal(Float.toString(num2));
        return temp1.subtract(temp2).floatValue();
    }

    public static BigDecimal sub(String num1, String num2) {
        BigDecimal decimal = new BigDecimal(num1);
        BigDecimal decimal1 = new BigDecimal(num2);
        return decimal.subtract(decimal1);
    }

    public static BigDecimal round(String num, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal decimal = new BigDecimal(num);
        return decimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    public static double mul(double num1, double num2) {
        BigDecimal decimal = new BigDecimal(Double.toString(num1));
        BigDecimal decimal1 = new BigDecimal(Double.toString(num2));
        return round(decimal.multiply(decimal1).doubleValue(), DEF_DIV_SCALE);
    }

    public static float mul(float num1, float num2) {
        BigDecimal bigDecimal = new BigDecimal(Float.toString(num1));
        BigDecimal bigDecimal1 = new BigDecimal(Float.toString(num2));
        return round(bigDecimal.multiply(bigDecimal1).floatValue(), DEF_DIV_SCALE);
    }

    public static BigDecimal div(String num1, String num2) {
        return div(num1, num2, DEF_DIV_SCALE);
    }

    public static double div(double num1, double num2) {
        if (num2 == 0) {
            return num1;
        }
        return div(num1, num2, DEF_DIV_SCALE);
    }

    public static float div(float v1, float v2) {
        if (v2 == 0) {
            return v1;
        }
        return div(v1, v2, DEF_DIV_SCALE);
    }

    public static double div(double num1, double num2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal bigDecimal = new BigDecimal(Double.toString(num1));
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(num2));
        return bigDecimal.divide(bigDecimal1, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static float div(float num1, float num2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal bigDecimal = new BigDecimal(Float.toString(num1));
        BigDecimal bigDecimal1 = new BigDecimal(Float.toString(num2));
        return bigDecimal.divide(bigDecimal1, scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static BigDecimal div(String num1, String num2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal decimal1 = new BigDecimal(num1);
        BigDecimal decimal2 = new BigDecimal(num2);
        return decimal1.divide(decimal2, scale, BigDecimal.ROUND_HALF_UP);
    }

    public static boolean isEqual(double num1, double num2) {
        BigDecimal temp1 = new BigDecimal(num1);
        BigDecimal temp2 = new BigDecimal(num2);
        int bj = temp1.compareTo(temp2);
        return bj == 0;
    }

    public static double round(double num, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal decimal = new BigDecimal(Double.toString(num));
        BigDecimal one = new BigDecimal("1");
        return decimal.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static float round(float num, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal decimal = new BigDecimal(Float.toString(num));
        BigDecimal one = new BigDecimal("1");
        return decimal.divide(one, scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static String format(double num) {
        return DF.format(num);
    }
}
