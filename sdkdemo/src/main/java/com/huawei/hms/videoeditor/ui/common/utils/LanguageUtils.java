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

package com.huawei.hms.videoeditor.ui.common.utils;

import java.util.Locale;

public final class LanguageUtils {
    private static final String CHINESE_LANGUAGE = "zh";

    private static final String ARABIC_LANGUAGE = "ar";

    private static final String FARSI_LANGUAGE = "fa";

    private static final String IW_LANGUAGE = "iw";

    private static final String URDU_LANGUAGE = "ur";

    private static final String UG_LANGUAGE = "ug";

    private static final String EN_LANGUAGE = "en";

    private LanguageUtils() {

    }

    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static boolean isZh() {
        String lang = getLanguage();

        return CHINESE_LANGUAGE.equals(lang);
    }

    public static boolean isEn() {
        String lang = getLanguage();

        return EN_LANGUAGE.equals(lang);
    }

    public static boolean isRTL() {
        String lang = getLanguage();

        return ARABIC_LANGUAGE.equals(lang) || FARSI_LANGUAGE.equals(lang) || IW_LANGUAGE.equals(lang)
                || URDU_LANGUAGE.equals(lang) || UG_LANGUAGE.equals(lang);
    }
}
