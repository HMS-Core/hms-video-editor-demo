
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

package com.huawei.hms.videoeditor.ui.mediaeditor.repository;

import java.util.ArrayList;
import java.util.List;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialsManager;
import com.huawei.hms.videoeditor.materials.HVEMaterialsResponseCallback;
import com.huawei.hms.videoeditor.materials.HVETopColumnInfo;
import com.huawei.hms.videoeditor.materials.HVETopColumnRequest;
import com.huawei.hms.videoeditor.materials.HVETopColumnResponse;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;

public class ColumnsRespository {

    private static final String TAG = "ColumnsRespository";

    public static final int RESULT_ILLEGAL = 0;

    public static final int RESULT_EMPTY = 1;

    private ColumnsListener columnsListener;

    public void initColumns(String type) {
        if (columnsListener == null) {
            return;
        }
        List<String> fatherColumn = new ArrayList<>();
        fatherColumn.add(type);

        HVETopColumnRequest request = new HVETopColumnRequest(fatherColumn);

        HVEMaterialsManager.getTopColumnById(request, new HVEMaterialsResponseCallback<HVETopColumnResponse>() {
            @Override
            public void onFinish(HVETopColumnResponse response) {
                initMaterialsCutColumnResp(response, type);
            }

            @Override
            public void onUpdate(HVETopColumnResponse response) {
                initMaterialsCutColumnResp(response, type);
            }

            @Override
            public void onError(Exception e) {
                columnsListener.errorType(RESULT_ILLEGAL);
            }
        });
    }

    private void initMaterialsCutColumnResp(HVETopColumnResponse response, String type) {
        if (columnsListener == null) {
            return;
        }
        List<HVETopColumnInfo> topColumnInfos = response.getColumnInfos();

        if (topColumnInfos.isEmpty()) {
            SmartLog.i(TAG, "materialsCutContents:" + topColumnInfos.size());
            columnsListener.errorType(RESULT_EMPTY);
            return;
        }

        for (HVETopColumnInfo columnInfo : topColumnInfos) {
            if (!columnInfo.getColumnId().equals(type) || columnInfo.getChildInfoList().size() <= 0) {
                if (topColumnInfos.size() == 1) {
                    columnsListener.errorType(RESULT_EMPTY);
                    break;
                }
                continue;
            }

            List<HVEColumnInfo> list = new ArrayList<>(columnInfo.getChildInfoList());
            columnsListener.columsData(list);
            break;
        }
    }

    public void seatColumnsListener(ColumnsListener columnsListener) {
        this.columnsListener = columnsListener;
    }
}
