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

package com.huawei.hms.videoeditor.ui.common.database;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.greendao.query.WhereCondition;

import android.content.Context;

import com.huawei.hms.videoeditor.sdk.materials.network.response.MaterialsCutContentType;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.database.bean.CloudMaterialDaoBean;
import com.huawei.hms.videoeditor.ui.common.database.bean.CloudMaterialsBeanDao;
import com.huawei.hms.videoeditor.ui.common.utils.KeepOriginal;

@KeepOriginal
public class CloudMaterialsDataManager {
    private static final String TAG = "CloudMaterialsDataManager";

    private WeakReference<Context> mContext;

    public CloudMaterialsDataManager(Context context) {
        mContext = new WeakReference<>(context);
    }

    public boolean updateCloudMaterialsBean(CloudMaterialBean cloudMaterialBean) {
        if (mContext == null || mContext.get() == null) {
            return false;
        }
        if (cloudMaterialBean == null) {
            return false;
        }

        CloudMaterialDaoBean cloudMaterialDaoBean = new CloudMaterialDaoBean();
        cloudMaterialDaoBean.setId(cloudMaterialBean.getId());
        cloudMaterialDaoBean.setPreviewUrl(cloudMaterialBean.getPreviewUrl());
        cloudMaterialDaoBean.setName(cloudMaterialBean.getName());
        cloudMaterialDaoBean.setLocalPath(cloudMaterialBean.getLocalPath());
        cloudMaterialDaoBean.setDuration(cloudMaterialBean.getDuration());
        cloudMaterialDaoBean.setType(cloudMaterialBean.getType());
        cloudMaterialDaoBean.setCategoryName(cloudMaterialBean.getCategoryName());
        cloudMaterialDaoBean.setLocalDrawableId(cloudMaterialBean.getLocalDrawableId());

        Long insert = DBManager.getInstance(mContext.get()).insertOrReplace(cloudMaterialDaoBean);
        return insert != -1;
    }

    public List<CloudMaterialBean> queryCloudMaterialsBeanByType(int type) throws ClassCastException {
        Field[] fields = MaterialsCutContentType.class.getDeclaredFields();
        List<Integer> types = new ArrayList<>();
        try {
            for (Field field : fields) {
                String name = field.getName();
                Object tmp = field.get(name);
                if (tmp instanceof Integer) {
                    types.add((Integer) tmp);
                }
            }
        } catch (IllegalAccessException e) {
            SmartLog.e(TAG, "inner error." + e.getMessage());
        }

        if (!types.contains(type)) {
            SmartLog.e(TAG, "queryMaterialsCutContentById fail because type is illegal");
            return new ArrayList<>();
        }

        if (mContext == null || mContext.get() == null) {
            return new ArrayList<>();
        }
        List<WhereCondition> whereConditionList = new ArrayList<>();
        whereConditionList.add(CloudMaterialsBeanDao.Properties.TYPE.eq(type));
        List<CloudMaterialDaoBean> beanListTemp =
            DBManager.getInstance(mContext.get()).queryByCondition(CloudMaterialDaoBean.class, whereConditionList);

        List<CloudMaterialBean> contents = new ArrayList<>();
        if (beanListTemp != null && beanListTemp.size() > 0) {
            for (CloudMaterialDaoBean bean : beanListTemp) {
                CloudMaterialBean content = new CloudMaterialBean();
                content.setId(bean.getId());
                content.setPreviewUrl(bean.getPreviewUrl());
                content.setName(bean.getName());
                content.setLocalPath(bean.getLocalPath());
                content.setDuration(bean.getDuration());
                content.setType(bean.getType());
                content.setCategoryName(bean.getCategoryName());
                content.setLocalDrawableId(bean.getLocalDrawableId());
                contents.add(content);
            }
        }
        return contents;
    }
}
