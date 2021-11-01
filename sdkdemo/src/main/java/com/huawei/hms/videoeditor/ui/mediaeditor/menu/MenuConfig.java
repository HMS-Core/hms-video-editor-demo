
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

package com.huawei.hms.videoeditor.ui.mediaeditor.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.common.utils.CloseUtils;
import com.huawei.hms.videoeditor.common.utils.GsonUtils;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.ArrayUtils;
import com.huawei.hms.videoeditor.ui.common.utils.CpuUtils;
import com.huawei.hms.videoeditor.ui.common.utils.KeepOriginal;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState;

@KeepOriginal
public class MenuConfig {
    private static final String TAG = "MenuConfig";

    private static final String MENU_JSON_NAME = "edit_menu_config.json";

    private static MenuConfigBean menuConfigBean = null;

    private volatile static MenuConfig menuConfig = null;

    public static MenuConfig getInstance() {
        if (menuConfig == null) {
            synchronized (MenuConfig.class) {
                if (menuConfig == null) {
                    menuConfig = new MenuConfig();
                }
            }
        }
        return menuConfig;
    }

    public void initMenuConfig(Context context) {
        if (menuConfigBean == null) {
            String editMenuJson = getStringJson(context, MENU_JSON_NAME);
            menuConfigBean = GsonUtils.fromJson(editMenuJson, MenuConfigBean.class);
        }
    }

    public List<EditMenuBean> getEditMenus() {
        if (menuConfigBean != null) {
            String deviceType;
            try {
                deviceType = CpuUtils.getArchType();
            } catch (IOException e) {
                SmartLog.e(TAG, e.getMessage());
                return menuConfigBean.getEditMenu();
            }
            if (CpuUtils.CPU_ARCHITECTURE_TYPE_64.equals(deviceType)) {
                return menuConfigBean.getEditMenu();
            }
            List<EditMenuBean> menuBeanList = menuConfigBean.getEditMenu();
            if (ArrayUtils.isEmpty(menuBeanList)) {
                return menuConfigBean.getEditMenu();
            }
            for (EditMenuBean editMenuBean : menuConfigBean.getEditMenu()) {
                if (editMenuBean.getId() == MainViewState.EDIT_VIDEO_STATE) {
                    List<EditMenuBean> childList = editMenuBean.getChildren();
                    if (!ArrayUtils.isEmpty(childList)) {
                        Iterator<EditMenuBean> iterator = childList.iterator();
                        while (iterator.hasNext()) {
                            EditMenuBean menuBean = iterator.next();
                            if (menuBean.getId() == MainViewState.EDIT_VIDEO_STATE_HUMAN_TRACKING) {
                                iterator.remove();
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            return menuConfigBean.getEditMenu();
        }
        return new ArrayList<>();
    }

    public List<EditMenuBean> getMenuOperates() {
        if (menuConfigBean != null) {
            String deviceType;
            try {
                deviceType = CpuUtils.getArchType();
            } catch (IOException e) {
                SmartLog.e(TAG, e.getMessage());
                return menuConfigBean.getOperates();
            }
            if (CpuUtils.CPU_ARCHITECTURE_TYPE_64.equals(deviceType)) {
                return menuConfigBean.getOperates();
            }
            List<EditMenuBean> menuBeanList = menuConfigBean.getOperates();
            if (ArrayUtils.isEmpty(menuBeanList)) {
                return menuConfigBean.getOperates();
            }

            for (EditMenuBean editMenuBean : menuConfigBean.getOperates()) {
                switch (editMenuBean.getId()) {
                    case MainViewState.EDIT_VIDEO_OPERATION:
                        List<EditMenuBean> operatesList = editMenuBean.getOperates();
                        if (!ArrayUtils.isEmpty(operatesList)) {
                            Iterator<EditMenuBean> iterator = operatesList.iterator();
                            while (iterator.hasNext()) {
                                EditMenuBean menuBean = iterator.next();
                                if (menuBean.getId() == MainViewState.EDIT_VIDEO_OPERATION_HUMAN_TRACKING) {
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                        break;
                    case MainViewState.EDIT_PIP_OPERATION:
                        List<EditMenuBean> operateList = editMenuBean.getOperates();
                        if (!ArrayUtils.isEmpty(operateList)) {
                            Iterator<EditMenuBean> iterator = operateList.iterator();
                            while (iterator.hasNext()) {
                                EditMenuBean menuBean = iterator.next();
                                if (menuBean.getId() == MainViewState.EDIT_PIP_OPERATION_HUMAN_TRACKING) {
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            return menuConfigBean.getOperates();
        }
        return new ArrayList<>();
    }

    private static String getStringJson(Context context, String jsonName) {
        if (TextUtils.isEmpty(jsonName)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        InputStreamReader inputStreamReader = null;
        BufferedReader bf = null;
        try {
            AssetManager manager = context.getAssets();
            inputStreamReader = new InputStreamReader(manager.open(jsonName), StandardCharsets.UTF_8);
            bf = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bf.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            SmartLog.e(TAG, e.getMessage());
        } finally {
            CloseUtils.close(inputStreamReader);
            CloseUtils.close(bf);
        }
        return builder.toString();
    }
}
