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

package com.huawei.hms.videoeditor.ui.common.database.dao;

import com.huawei.hms.videoeditor.sdk.util.KeepOriginal;
import com.huawei.hms.videoeditor.ui.common.database.bean.CloudMaterialDaoBean;
import com.huawei.hms.videoeditor.ui.common.database.bean.CloudMaterialsBeanDao;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;

@KeepOriginal
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig cloudMaterialsBeanDaoConfig;

    private final CloudMaterialsBeanDao cloudMaterialsBeanDao;

    public DaoSession(Database db, IdentityScopeType type,
        Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap) {
        super(db);

        cloudMaterialsBeanDaoConfig = daoConfigMap.get(CloudMaterialsBeanDao.class).clone();
        cloudMaterialsBeanDaoConfig.initIdentityScope(type);

        cloudMaterialsBeanDao = new CloudMaterialsBeanDao(cloudMaterialsBeanDaoConfig, this);

        registerDao(CloudMaterialDaoBean.class, cloudMaterialsBeanDao);
    }

    public void clear() {
        cloudMaterialsBeanDaoConfig.clearIdentityScope();
    }

    public CloudMaterialsBeanDao getCloudMaterialsBeanDao() {
        return cloudMaterialsBeanDao;
    }
}
