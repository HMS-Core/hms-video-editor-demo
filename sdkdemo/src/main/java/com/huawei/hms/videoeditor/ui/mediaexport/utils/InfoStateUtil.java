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

package com.huawei.hms.videoeditor.ui.mediaexport.utils;

import android.content.Context;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.ui.common.utils.SharedPreferenceUtils;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuConfig;

public class InfoStateUtil {
    private static InfoStateUtil infoStateUtil = null;

    private final static String INTO_STATE_INFO = "into_info";

    private final static String INFO_STATE_NAME = "into_state";

    /**
     * p50
     */
    public final static String PRODUCT_MODEL_FOR_P50 = "productModelForP50";

    /**
     * P50E
     */
    public static final String PRODUCT_MODEL_FOR_P50E = "productModelForP50E";

    /**
     * Mate20pro
     */
    public static final String PRODUCT_MODEL_FOR_MATE20PRO = "productModelForMate20Pro";

    /**
     * NOVA9
     */
    public final static String PRODUCT_MODEL_FOR_NOVA9 = "productModelForNOVA9";

    /**
     * NOVA10
     */
    public final static String PRODUCT_MODEL_FOR_NOVA10 = "productModelForNOVA10";

    /**
     * NOVA9SE
     */
    public final static String PRODUCT_MODEL_FOR_NOVA9SE = "productModelForNOVA9SE";

    /**
     * p40proj
     */
    public static final String PRODUCT_MODEL_FOR_P40PROJ = "productModelForP40PRJ";

    /**
     * mate30
     */
    public static final String PRODUCT_MODEL_FOR_MATE30 = "productModelForMATE30";

    /**
     * mate50
     */
    public static final String PRODUCT_MODEL_FOR_MATE50 = "productModelForMATE50";

    public static InfoStateUtil getInstance() {
        synchronized (InfoStateUtil.class) {
            if (infoStateUtil == null) {
                infoStateUtil = new InfoStateUtil();
            }
        }
        return infoStateUtil;
    }

    public void saveIntoState(boolean isFirstInto) {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(INTO_STATE_INFO);
        if (sp == null) {
            return;
        }
        sp.put(INFO_STATE_NAME, isFirstInto);
    }

    public boolean getSaveIntoState() {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(INTO_STATE_INFO);
        if (sp == null) {
            return true;
        }
        return sp.getBoolean(INFO_STATE_NAME, true);
    }

    public void putProductModelForP50(String value) {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_P50);
        if (sp == null) {
            return;
        }
        sp.put(PRODUCT_MODEL_FOR_P50, value);
    }

    public String getProductModelForP50() {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_P50);
        if (sp == null) {
            return "";
        }
        return sp.getString(PRODUCT_MODEL_FOR_P50, "");
    }

    public void putProductModelForP50E(String value) {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_P50E);
        if (sp == null) {
            return;
        }
        sp.put(PRODUCT_MODEL_FOR_P50E, value);
    }

    public String getProductModelForP50E() {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_P50E);
        if (sp == null) {
            return "";
        }
        return sp.getString(PRODUCT_MODEL_FOR_P50E, "");
    }

    public void putProductModelForNOVA9(String value) {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_NOVA9);
        if (sp == null) {
            return;
        }
        sp.put(PRODUCT_MODEL_FOR_NOVA9, value);
    }

    public String getProductModelForNOVA9() {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_NOVA9);
        if (sp == null) {
            return "";
        }
        return sp.getString(PRODUCT_MODEL_FOR_NOVA9, "");
    }

    public void putProductModelForNOVA10(String value) {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_NOVA10);
        if (sp == null) {
            return;
        }
        sp.put(PRODUCT_MODEL_FOR_NOVA10, value);
    }

    public String getProductModelForNOVA10() {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_NOVA10);
        if (sp == null) {
            return "";
        }
        return sp.getString(PRODUCT_MODEL_FOR_NOVA10, "");
    }

    public void putProductModelForNOVA9SE(String value) {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_NOVA9SE);
        if (sp == null) {
            return;
        }
        sp.put(PRODUCT_MODEL_FOR_NOVA9SE, value);
    }

    public String getProductModelForNOVA9SE() {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_NOVA9SE);
        if (sp == null) {
            return "";
        }
        return sp.getString(PRODUCT_MODEL_FOR_NOVA9SE, "");
    }

    public void putProductModelForMATE20PRO(String value) {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_MATE20PRO);
        if (sp == null) {
            return;
        }
        sp.put(PRODUCT_MODEL_FOR_MATE20PRO, value);
    }

    public String getProductModelForMATE20PRO() {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_MATE20PRO);
        if (sp == null) {
            return "";
        }
        return sp.getString(PRODUCT_MODEL_FOR_MATE20PRO, "");
    }

    public void putProductModelForP40proj(String value) {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_P40PROJ);
        if (sp == null) {
            return;
        }
        sp.put(PRODUCT_MODEL_FOR_P40PROJ, value);
    }

    public String getProductModelForP40proj() {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_P40PROJ);
        if (sp == null) {
            return "";
        }
        return sp.getString(PRODUCT_MODEL_FOR_P40PROJ, "");
    }

    public void putProductModelFoMate30(String value) {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_MATE30);
        if (sp == null) {
            return;
        }
        sp.put(PRODUCT_MODEL_FOR_MATE30, value);
    }

    public String getProductModelForMate30() {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_MATE30);
        if (sp == null) {
            return "";
        }
        return sp.getString(PRODUCT_MODEL_FOR_MATE30, "");
    }

    public void putProductModelFoMate50(String value) {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_MATE50);
        if (sp == null) {
            return;
        }
        sp.put(PRODUCT_MODEL_FOR_MATE50, value);
    }

    public String getProductModelForMate50() {
        SharedPreferenceUtils sp = SharedPreferenceUtils.get(PRODUCT_MODEL_FOR_MATE50);
        if (sp == null) {
            return "";
        }
        return sp.getString(PRODUCT_MODEL_FOR_MATE50, "");
    }

    public void checkInfoState(Context context) {
        if (!getSaveIntoState()) {
            return;
        }

        saveIntoState(false);

        // read json
        MenuConfig.getInstance().initDeviceModelConfig(context);

        String mDeviceModelForP50 = MenuConfig.getInstance().getDeviceModels(PRODUCT_MODEL_FOR_P50);
        String mDeviceModelForP50E = MenuConfig.getInstance().getDeviceModels(PRODUCT_MODEL_FOR_P50E);
        String mDeviceModelForNova9 = MenuConfig.getInstance().getDeviceModels(PRODUCT_MODEL_FOR_NOVA9);
        String mDeviceModelForNova10 = MenuConfig.getInstance().getDeviceModels(PRODUCT_MODEL_FOR_NOVA10);
        String mDeviceModelForNova9SE = MenuConfig.getInstance().getDeviceModels(PRODUCT_MODEL_FOR_NOVA9SE);
        String mDeviceModelForMATE20PRO = MenuConfig.getInstance().getDeviceModels(PRODUCT_MODEL_FOR_MATE20PRO);
        String mDeviceModelForP40Proj = MenuConfig.getInstance().getDeviceModels(PRODUCT_MODEL_FOR_P40PROJ);
        String mDeviceModelForMate30 = MenuConfig.getInstance().getDeviceModels(PRODUCT_MODEL_FOR_MATE30);
        String mDeviceModelForMate50 = MenuConfig.getInstance().getDeviceModels(PRODUCT_MODEL_FOR_MATE50);

        if (!TextUtils.isEmpty(mDeviceModelForP50)) {
            putProductModelForP50(mDeviceModelForP50);
        }
        if (!TextUtils.isEmpty(mDeviceModelForP50E)) {
            putProductModelForP50E(mDeviceModelForP50E);
        }
        if (!TextUtils.isEmpty(mDeviceModelForNova9)) {
            putProductModelForNOVA9(mDeviceModelForNova9);
        }
        if (!TextUtils.isEmpty(mDeviceModelForNova10)) {
            putProductModelForNOVA10(mDeviceModelForNova10);
        }
        if (!TextUtils.isEmpty(mDeviceModelForNova9SE)) {
            putProductModelForNOVA9SE(mDeviceModelForNova9SE);
        }
        if (!TextUtils.isEmpty(mDeviceModelForMATE20PRO)) {
            putProductModelForMATE20PRO(mDeviceModelForMATE20PRO);
        }
        if (!TextUtils.isEmpty(mDeviceModelForP40Proj)) {
            putProductModelForP40proj(mDeviceModelForP40Proj);
        }
        if (!TextUtils.isEmpty(mDeviceModelForMate30)) {
            putProductModelFoMate30(mDeviceModelForMate30);
        }
        if (!TextUtils.isEmpty(mDeviceModelForMate50)) {
            putProductModelFoMate50(mDeviceModelForMate50);
        }
    }
}
