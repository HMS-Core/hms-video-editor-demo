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

import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;

public class TimeUtils {
    private static final String TAG = "TimeUtils";

    public static final String FROM_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static StringBuilder sFormatBuilder = new StringBuilder();

    private static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());

    private static final Object[] TIME_ARGS = new Object[2];

    private static final String DURATION_FORMAT = "%1$02d:%2$02d";

    private static ThreadLocal<HashMap<String, SimpleDateFormat>> localDateFormat =
            new ThreadLocal<HashMap<String, SimpleDateFormat>>() {
                @Override
                protected HashMap<String, SimpleDateFormat> initialValue() {
                    return new HashMap<>();
                }
            };

    public static String makeTimeString(Context context, long milliSecs) {
        int secs = milli2Secs(milliSecs);
        sFormatBuilder.setLength(0);
        final Object[] timeArgs = TIME_ARGS;
        timeArgs[0] = secs / 60;
        timeArgs[1] = secs % 60;
        return sFormatter.format(Locale.getDefault(), DURATION_FORMAT, timeArgs).toString();
    }

    public static String makeSoundTimeString(Context context, long milliSecs) {
        int secs = milli2Secs(milliSecs);
        if (secs <= 0) {
            secs = 1;
        }
        sFormatBuilder.setLength(0);
        final Object[] timeArgs = TIME_ARGS;
        timeArgs[0] = secs / 60;
        timeArgs[1] = secs % 60;
        return sFormatter.format(Locale.getDefault(), DURATION_FORMAT, timeArgs).toString();
    }

    public static String makeTimeString2(Context context, long milliSecs) {
        String string = makeTimeString(context, milliSecs);
        int secs = (int) Math.floor(BigDecimalUtils.div(milliSecs, 100f));
        return string;
    }

    public static int milli2Secs(long milliSecs) {
        return (int) Math.floor(BigDecimalUtils.div(milliSecs, 1000f));
    }

    public static String formatTimeByUS(long inputTime, String pattern) {
        if (pattern == null) {
            SmartLog.e(TAG, "Invalid type,please check");
            return null;
        }
        SimpleDateFormat f = getDateFormat(pattern, Locale.US);
        return f.format(inputTime);
    }

    private static SimpleDateFormat getDateFormat(String pattern, Locale locale) {
        return getDateFormat(pattern, locale, TimeZone.getDefault());
    }

    private static SimpleDateFormat getDateFormat(String pattern, Locale locale, TimeZone timeZone) {
        if (TextUtils.isEmpty(pattern)) {
            pattern = FROM_TIME_PATTERN;
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        HashMap<String, SimpleDateFormat> formatHashMap = localDateFormat.get();
        String localeBrief = locale.getCountry() + "," + locale.getLanguage();
        SimpleDateFormat sdf = formatHashMap.get(localeBrief);
        if (sdf != null) {
            if (!sdf.toPattern().equals(pattern)) {
                sdf.applyPattern(pattern);
            }
            sdf.setTimeZone(timeZone);
            return sdf;
        }
        sdf = new SimpleDateFormat(pattern, locale);
        sdf.setTimeZone(timeZone);
        formatHashMap.put(localeBrief, sdf);
        return sdf;
    }
}
