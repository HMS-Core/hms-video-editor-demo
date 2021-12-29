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

import android.content.Context;
import android.content.SharedPreferences;

public class SPGuideUtils {
    public final static String A_KEY_PIECE_INFO = "a_key_piece_info";

    public final static String A_KEY_PIECE_SHOW = "a_key_piece_show";

    public final static String MATERIAL_LIBRARY_INFO = "material_library_info";

    public final static String MATERIAL_LIBRARY_SHOW = "material_library_show";

    public final static String ADVANCE_BACKGROUND_INFO = "advance_background_info";

    public final static String ADVANCE_BACKGROUND_SHOW = "advance_background_show";

    public final static String ADVANCE_KEYFRAME_INFO = "advance_keyframe_info";

    public final static String ADVANCE_KEYFRAME_SHOW = "advance_keyframe_show";

    public final static String ADVANCE_ZOOM_INFO = "advance_zoom_info";

    public final static String ADVANCE_ZOOM_SHOW = "advance_zoom_show";

    public final static String ADVANCE_DRAG_INFO = "advance_drag_info";

    public final static String ADVANCE_DRAG_SHOW = "advance_drag_show";

    public final static String ADVANCE_CUT_TO_INFO = "advance_cut_to_info";

    public final static String ADVANCE_CUT_TO_SHOW = "advance_cut_to_show";

    public final static String ADVANCE_ORDER_INFO = "advance_order_info";

    public final static String ADVANCE_ORDER_SHOW = "advance_order_show";

    public final static String ADVANCE_STICKER_INFO = "advance_sticker_info";

    public final static String ADVANCE_STICKER_SHOW = "advance_sticker_show";

    public static final String FACE_LANE = "face_lane";

    public static final String FACE_LANE_HASH = "face_lane_hash";

    public static final String FACE_LANE_START = "face_lane_start";

    public static final String FACE_LANE_END = "face_lane_end";

    private static SPGuideUtils spGuideUtils = null;

    public static SPGuideUtils getInstance() {
        synchronized (SPGuideUtils.class) {
            if (spGuideUtils == null) {
                spGuideUtils = new SPGuideUtils();
            }
        }
        return spGuideUtils;
    }

    public String getFaceLaneHash(Context context, String projectId) {
        if (context == null) {
            return "";
        }

        SharedPreferences sp = context.getSharedPreferences(FACE_LANE, Context.MODE_PRIVATE);
        return sp.getString(FACE_LANE_HASH + projectId, "");
    }

    public void saveFaceDetectedTimes(Context context, long startTime, long endTime, String projectId, String hashCode) {
        if (context == null) {
            return;
        }

        SharedPreferences sp = context.getSharedPreferences(FACE_LANE, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong(FACE_LANE_START + projectId, startTime);
        edit.putLong(FACE_LANE_END + projectId, endTime);
        edit.putString(FACE_LANE_HASH + projectId, hashCode);
        edit.apply();
    }

    public long[] getFaceDetectedTimes(Context context, String projectId) {
        if (context == null) {
            return new long[0];
        }

        long[] timeStamp = new long[2];
        SharedPreferences sp = context.getSharedPreferences(FACE_LANE, Context.MODE_PRIVATE);
        timeStamp[0] = sp.getLong(FACE_LANE_START + projectId, 0);
        timeStamp[1] = sp.getLong(FACE_LANE_END + projectId, 0);
        if (timeStamp[0] == 0 && timeStamp[1] == 0) {
            return new long[0];
        }
        return timeStamp;
    }

    public void clear(Context context) {
        if (context == null) {
            return;
        }

        SharedPreferences sp = context.getSharedPreferences(A_KEY_PIECE_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(A_KEY_PIECE_SHOW, false);
        edit.apply();

        SharedPreferences sp1 = context.getSharedPreferences(ADVANCE_KEYFRAME_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit1 = sp1.edit();
        edit1.putBoolean(ADVANCE_KEYFRAME_SHOW, false);
        edit1.apply();

        SharedPreferences sp3 = context.getSharedPreferences(MATERIAL_LIBRARY_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit3 = sp3.edit();
        edit3.putBoolean(MATERIAL_LIBRARY_SHOW, false);
        edit3.apply();

        SharedPreferences sp4 = context.getSharedPreferences(ADVANCE_BACKGROUND_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit4 = sp4.edit();
        edit4.putBoolean(ADVANCE_BACKGROUND_SHOW, false);
        edit4.apply();

        SharedPreferences sp5 = context.getSharedPreferences(ADVANCE_ZOOM_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit5 = sp5.edit();
        edit5.putBoolean(ADVANCE_ZOOM_SHOW, false);
        edit5.apply();

        SharedPreferences sp6 = context.getSharedPreferences(ADVANCE_DRAG_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit6 = sp6.edit();
        edit6.putBoolean(ADVANCE_DRAG_SHOW, false);
        edit6.apply();

        SharedPreferences sp7 = context.getSharedPreferences(ADVANCE_CUT_TO_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit7 = sp7.edit();
        edit7.putBoolean(ADVANCE_CUT_TO_SHOW, false);
        edit7.apply();

        SharedPreferences sp8 = context.getSharedPreferences(ADVANCE_ORDER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit8 = sp8.edit();
        edit8.putBoolean(ADVANCE_ORDER_SHOW, false);
        edit8.apply();

        SharedPreferences sp9 = context.getSharedPreferences(ADVANCE_STICKER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit9 = sp9.edit();
        edit9.putBoolean(ADVANCE_STICKER_SHOW, false);
        edit9.apply();
    }
}
