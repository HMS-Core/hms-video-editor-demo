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

package com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean;

public class MainViewState {
    public static final int NORMAL_STATE = 0;

    public static final int EDIT_VIDEO_STATE = 101;

    public static final int EDIT_AUDIO_STATE = 102;

    public static final int EDIT_STICKER_STATE = 103;

    public static final int EDIT_TEXT_STATE = 104;

    public static final int EDIT_PIP_STATE = 105;

    public static final int EDIT_SPECIAL_STATE = 106;

    public static final int EDIT_FILTER_STATE = 107;

    public static final int EDIT_RATIO_STATE = 108;

    public static final int EDIT_BACKGROUND_STATE = 109;

    public final static int EDIT_VIDEO_STATE_TRIM = 101200;

    public final static int EDIT_VIDEO_STATE_SPLIT = 101201;

    public final static int EDIT_VIDEO_STATE_PROPORTION = 101202;

    public final static int EDIT_VIDEO_STATE_SPEED = 101204;

    public final static int EDIT_VIDEO_STATE_ANIMATION = 101205;

    public final static int EDIT_VIDEO_STATE_DELETE = 101206;

    public final static int EDIT_VIDEO_STATE_TAILORING = 101207;

    public final static int EDIT_VIDEO_STATE_COPY = 101208;

    public final static int EDIT_VIDEO_STATE_REPLACE = 101209;

    public final static int EDIT_VIDEO_STATE_MASK = 101210;

    public final static int EDIT_VIDEO_STATE_MIRROR = 101212;

    public final static int EDIT_VIDEO_STATE_TRANSPARENCY = 101213;

    public final static int EDIT_VIDEO_STATE_INVERTED = 101214;

    public final static int EDIT_VIDEO_STATE_VOLUME = 101217;

    public final static int EDIT_VIDEO_STATE_FILTER = 101218;

    public final static int EDIT_VIDEO_STATE_BLOCK_FACE = 101219;

    public final static int EDIT_VIDEO_STATE_HUMAN_TRACKING = 101220;

    public final static int EDIT_VIDEO_STATE_AI_FUN = 101223;
    public final static int EDIT_VIDEO_STATE_KEYFRAME = 101224;
    public final static int EDIT_VIDEO_STATE_AI_HAIR = 101221;
    public final static int EDIT_VIDEO_STATE_AI_SELECTION = 101227;
    public final static int EDIT_VIDEO_STATE_AI_SEGMENTATION = 101225;
    public final static int EDIT_VIDEO_STATE_TIME_LAPSE = 101226;
    public final static int EDIT_VIDEO_STATE_WINGS = 101228;
    public final static int EDIT_VIDEO_STATE_BODY_SEG = 101229;
    public final static int EDIT_VIDEO_STATE_HEAD_SEG = 101230;

    public final static int EDIT_AUDIO_STATE_HWMUSIC = 102202;

    public final static int EDIT_AUDIO_STATE_ACOUSTICS = 102203;

    public final static int EDIT_STICKER_STATE_ADD_STICKER = 103201;

    public final static int EDIT_STICKER_STATE_ADD_TUYA = 103202;

    public final static int EDIT_TEXT_STATE_ADD = 104201;

    public final static int EDIT_TEXT_STATE_ADD_WATER_MARK = 104203;

    public final static int EDIT_TEXT_STATE_STICKER = 104204;

    public final static int EDIT_TEXT_STATE_ADD_ADDTUYA = 104205;

    public final static int EDIT_PIP_STATE_ADD = 105201;

    public final static int EDIT_SPECIAL_STATE_ADD = 106201;

    public final static int EDIT_FILTER_STATE_ADD = 107201;

    public final static int EDIT_FILTER_STATE_EXCLUSIVE = 107203;

    public final static int EDIT_FILTER_STATE_ADJUST = 107202;

    public final static int EDIT_VIDEO_STATE_ROTATION = 118;

    public final static int EDIT_VIDEO_STATE_ADJUST = 121;

    public final static int EDIT_VIDEO_OPERATION = 201;

    public final static int EDIT_VIDEO_OPERATION_TRIM = 201100;

    public final static int EDIT_VIDEO_OPERATION_SPLIT = 201101;

    public final static int EDIT_VIDEO_OPERATION_SPEED = 201102;

    public final static int EDIT_VIDEO_OPERATION_VOLUME = 201103;

    public final static int EDIT_VIDEO_OPERATION_ANIMATION = 201104;

    public final static int EDIT_VIDEO_OPERATION_FILTER_ADD = 201105;

    public final static int EDIT_VIDEO_OPERATION_DELETE = 201106;

    public final static int EDIT_VIDEO_OPERATION_TAILORING = 201107;

    public final static int EDIT_VIDEO_OPERATION_ADJUST = 201108;

    public final static int EDIT_VIDEO_OPERATION_COPY = 201109;

    public final static int EDIT_VIDEO_OPERATION_REPLACE = 201110;

    public final static int EDIT_VIDEO_OPERATION_MASK = 201111;

    public final static int EDIT_VIDEO_OPERATION_MIRROR = 201113;

    public final static int EDIT_VIDEO_OPERATION_TRANSPARENCY = 201114;

    public final static int EDIT_VIDEO_OPERATION_INVERTED = 201115;

    public final static int EDIT_VIDEO_OPERATION_PROPORTION = 201117;

    public final static int EDIT_VIDEO_OPERATION_HUMAN_TRACKING = 201119;

    public final static int EDIT_VIDEO_OPERATION_AI_HAIR = 201120;

    public final static int EDIT_VIDEO_OPERATION_AI_SELECTION = 201126;

    public final static int EDIT_VIDEO_OPERATION_AI_FUN = 201123;

    public final static int EDIT_VIDEO_OPERATION_AI_SEGMENTATION = 201127;

    public final static int EDIT_VIDEO_OPERATION_TIME_LAPSE = 201125;

    public final static int EDIT_VIDEO_OPERATION_WINGS = 201128;

    public final static int EDIT_VIDEO_OPERATION_BODY_SEG = 201129;

    public final static int EDIT_VIDEO_OPERATION_HEAD_SEG = 201130;

    public final static int EDIT_VIDEO_OPERATION_ROTATION = 1007;

    public final static int EDIT_VIDEO_OPERATION_KEYFRAME = 201124;

    public final static int EDIT_AUDIO_OPERATION = 202;

    public final static int EDIT_AUDIO_STATE_SPLIT = 202101;

    public final static int EDIT_AUDIO_STATE_VOLUME = 202102;

    public final static int EDIT_AUDIO_STATE_SPEED = 202103;

    public final static int EDIT_AUDIO_STATE_DELETE = 202105;

    public final static int EDIT_AUDIO_STATE_COPY = 202107;

    public final static int EDIT_AUDIO_STATE_ADD = 202108;

    public static final int EDIT_STICKER_OPERATION = 204;

    public static final int EDIT_STICKER_OPERATION_SPLIT = 204101;

    public static final int EDIT_STICKER_OPERATION_REPLACE = 204102;

    public static final int EDIT_STICKER_OPERATION_ANIMATION = 204103;

    public static final int EDIT_STICKER_OPERATION_COPY = 204104;

    public static final int EDIT_STICKER_OPERATION_DELETE = 204105;

    public static final int EDIT_STICKER_OPERATION_ADD = 204106;

    public static final int EDIT_GRAFFIT_OPERATION_DELETE = 211101;

    public final static int EDIT_TEXT_OPERATION = 205;

    public final static int EDIT_TEXT_OPERATION_SPLIT = 205101;

    public final static int EDIT_TEXT_OPERATION_EDIT = 205102;

    public final static int EDIT_TEXT_OPERATION_COPY = 205103;

    public final static int EDIT_TEXT_OPERATION_ANIMATION = 205104;

    public final static int EDIT_TEXT_OPERATION_DELETE = 205105;

    public final static int EDIT_TEXT_OPERATION_ADD = 205106;

    public final static int EDIT_TEXT_MODULE_OPERATION = 206;

    public final static int EDIT_TEXT_MODULE_OPERATION_REPLACE = 206101;

    public final static int EDIT_TEXT_MODULE_OPERATION_SPLIT = 206102;

    public final static int EDIT_TEXT_MODULE_OPERATION_COPY = 206103;

    public final static int EDIT_TEXT_MODULE_OPERATION_DELETE = 206104;

    public final static int EDIT_PIP_OPERATION = 207;

    public final static int EDIT_PIP_OPERATION_SPLIT = 207101;

    public final static int EDIT_PIP_OPERATION_SPEED = 207102;

    public final static int EDIT_PIP_OPERATION_VOLUME = 207103;

    public final static int EDIT_PIP_OPERATION_ANIMATION = 207104;

    public final static int EDIT_PIP_OPERATION_FILTER = 207105;

    public final static int EDIT_PIP_OPERATION_MIX = 207106;

    public final static int EDIT_PIP_OPERATION_DELETE = 207107;

    public final static int EDIT_PIP_OPERATION_CROP = 207108;

    public final static int EDIT_PIP_OPERATION_ADJUST = 207109;

    public final static int EDIT_PIP_OPERATION_COPY = 207110;

    public final static int EDIT_PIP_OPERATION_REPLACE = 207111;

    public final static int EDIT_PIP_OPERATION_MASK = 207112;

    public final static int EDIT_PIP_OPERATION_MIRROR = 207114;

    public final static int EDIT_PIP_OPERATION_TRANSPARENCY = 207115;

    public final static int EDIT_PIP_OPERATION_INVERTED = 207117;

    public final static int EDIT_PIP_OPERATION_ADD = 207118;

    public final static int EDIT_PIP_OPERATION_HUMAN_TRACKING = 207119;

    public final static int EDIT_PIP_OPERATION_AI_HAIR = 207120;

    public final static int EDIT_PIP_OPERATION_AI_SELECTION = 207121;

    public final static int EDIT_PIP_OPERATION_AI_FUN = 207123;

    public final static int EDIT_PIP_OPERATION_AI_SEGMENTATION = 207124;

    public final static int EDIT_PIP_OPERATION_TIME_LAPSE = 207125;

    public final static int EDIT_PIP_OPERATION_WINGS = 207128;

    public final static int EDIT_PIP_OPERATION_BODY_SEG = 207129;

    public final static int EDIT_PIP_OPERATION_HEAD_SEG = 207130;

    public final static int EDIT_PIP_OPERATION_ROTATION = 5007;

    public final static int EDIT_SPECIAL_OPERATION = 208;

    public final static int EDIT_SPECIAL_OPERATION_REPLACE = 208101;

    public final static int EDIT_SPECIAL_OPERATION_OBJECT = 208103;

    public final static int EDIT_SPECIAL_OPERATION_DELETE = 208104;

    public final static int EDIT_SPECIAL_OPERATION_ADD = 208105;

    public final static int EDIT_FILTER_OPERATION = 209;

    public final static int EDIT_FILTER_OPERATION_REPLACE = 209101;

    public final static int EDIT_FILTER_OPERATION_OBJECT = 209103;

    public final static int EDIT_FILTER_OPERATION_DELETE = 209104;

    public final static int EDIT_ADJUST_OPERATION = 210;

    public final static int EDIT_ADJUST_OPERATION_ADJUST = 210101;

    public final static int EDIT_ADJUST_OPERATION_OBJECT = 210103;

    public final static int EDIT_ADJUST_OPERATION_DELETE = 210104;

    public final static int EDIT_ADJUST_OPERATION_REPLACE = 9001;

    public static final int EDIT_AI_OPERATION = 8000;

    public static final int EDIT_AI_OPERATION_DELETE = 8002;

    public static final int TRANSITION_PANEL = 1100;

    public static final int EDIT_VIDEO_STATE_MOVE = 1300;

    public final static int EDIT_AUDIO_CUSTOM_CURVESPEED = 609;
}
