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

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.huawei.hms.videoeditor.utils.SmartLog;

public final class GsonUtils {
    private static final String TAG = "GsonUtils";

    private static ExclusionStrategy serializeExclusionStrategy = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            Expose expose = fieldAttributes.getAnnotation(Expose.class);
            if (expose != null && !expose.serialize()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    };

    private static final Gson GSON =
            new GsonBuilder().disableHtmlEscaping().addSerializationExclusionStrategy(serializeExclusionStrategy).create();

    private GsonUtils() {
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            SmartLog.e(TAG, "GsonUtils.fromJson(String, clazz) exception", e);
            SmartLog.d(TAG, "json=" + json + ", exception=" + e);
            return null;
        }
    }
}
