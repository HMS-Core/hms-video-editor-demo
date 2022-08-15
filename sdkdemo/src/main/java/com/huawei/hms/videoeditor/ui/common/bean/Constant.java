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

package com.huawei.hms.videoeditor.ui.common.bean;

public class Constant {
    public static final String DEFAULT_VERSION = "1.0.0";

    public static final int MAX_PICK_NUM = 300;

    public static final int MAX_AUTO_TEMPLATE = 30;

    public static final int PAGE_SIZE = 20;

    public static final String EXTRA_SELECT_RESULT = "select_result";

    public static final String TEXT_TEMPLATE_REPLACE = "TEXT_TEMPLATE_REPLACE";

    public static final String EXTRA_AUDIO_SELECT_RESULT = "audio_select_result";

    public static final int RESULT_CODE = 200;

    public static final String IS_EXTRA_AUDIO = "is_extra_audio";

    public static final String EXTRACT_TRIM_IN = "extract_trim_in";

    public static final String EXTRACT_TRIM_OUT = "extract_trim_out";

    public static final int REQ_CODE_CHOOSE_IMAGE = 100;

    public static final int RESP_CODE_CHOOSE_IMAGE = 200;

    public static final String CUSTOM_FILTER_JOIN_STRING = "#@$%{}#@$%";

    public static final String TEXT_ANIM_OPERATE = "TEXT_ANIM_OPERATE";

    public static final String TEXT_ADD_OPERATE = "TEXT_ADD_OPERATE";

    public static final Object ADDTEXTSTICK = "addTextSticker";

    public static final Object ADDANIMATION = "ADDANIMATION";

    public static final int IMAGE_WIDTH_MARGIN = 62;

    public static final int IMAGE_COUNT_4 = 4;

    public static final int IMAGE_COUNT_8 = 8;

    public static final int RADIUS_VALUE = 5;

    public static final int TEXT_HALF = 2;

    public static final int TEXT_FONT_MARGIN_BOTTOM = 8;

    public static final String DEFAULT_FONT_PATH = "/system/fonts/HwChinese-Medium.ttf";

    public static class IntentFrom {
        public static int INTENT_WHERE_FROM = 0;

        public static final int INTENT_FROM_SELF = 1;

        public static final int INTENT_FROM_IMAGE_LIB = 2;
    }

    public static final String LOCAL_VIDEO_SAVE_PATH = "VideoEditor";

    public static final String LOCAL_VIDEO_SAVE_TIME = "yyyyMMdd_HHmmss";

    public static final int STAGGERED_COL_HEIGHT_DIFF_VALUE = 20;

    public static final int RTL_UI = -1;

    public static final int LTR_UI = 1;

    public static final int FILTER_POPUP_VIEW_CREATE = 1;

    public static final int FILTER_POPUP_VIEW_EDITOR = 2;
}
