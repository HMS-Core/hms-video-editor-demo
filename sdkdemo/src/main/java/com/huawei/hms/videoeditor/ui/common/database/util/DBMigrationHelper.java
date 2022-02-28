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

package com.huawei.hms.videoeditor.ui.common.database.util;

import android.database.Cursor;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.sdk.util.KeepOriginal;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.database.dao.DaoMaster;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@KeepOriginal
public class DBMigrationHelper {
    private static final String TAG = "DBMigrationHelper";

    private static class DBMigrationHelperHolder {
        private static final DBMigrationHelper INSTANCE = new DBMigrationHelper();
    }

    public static DBMigrationHelper getInstance() {
        return DBMigrationHelperHolder.INSTANCE;
    }

    private static List<String> getColumns(Database db, String tableName) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 1", null);
            if (cursor != null) {
                columns = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
            }
        } catch (Exception e) {
            SmartLog.e(tableName, e.getMessage(), e);
            SmartLog.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return columns;
    }

    public void migrate(Database db, Class... daoClasses) {
        generateTempTables(db, daoClasses);
        DaoMaster.dropAllTables(db, true);
        DaoMaster.createAllTables(db, false);
        restoreData(db, daoClasses);
    }

    private void generateTempTables(Database db, Class... daoClass) {
        for (int i = 0; i < daoClass.length; i++) {
            DaoConfig dDaoConfig = new DaoConfig(db, daoClass[i]);
            String dDivider = "";
            String tTableName = dDaoConfig.tablename;
            String tTempTableName = dDaoConfig.tablename.concat("_TEMP");

            ArrayList pProperties = new ArrayList<>();
            StringBuilder cCreateTableStringBuilder = new StringBuilder();

            cCreateTableStringBuilder.append("CREATE TABLE ").append(tTempTableName).append(" (");
            for (int j = 0; j < dDaoConfig.properties.length; j++) {
                String cColumnName = dDaoConfig.properties[j].columnName;
                if (getColumns(db, tTableName).contains(cColumnName)) {
                    pProperties.add(cColumnName);
                    String tType = null;
                    try {
                        tType = getTypeByClass(dDaoConfig.properties[j].type);
                    } catch (Exception exception) {
                        SmartLog.e(TAG, exception.getMessage() + "");
                    }
                    cCreateTableStringBuilder.append(dDivider).append(cColumnName).append(" ").append(tType);
                    if (dDaoConfig.properties[j].primaryKey) {
                        cCreateTableStringBuilder.append(" PRIMARY KEY");
                    }
                    dDivider = ",";
                }
            }

            cCreateTableStringBuilder.append(");");
            db.execSQL(cCreateTableStringBuilder.toString());

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("INSERT INTO ").append(tTempTableName).append(" (");
            stringBuilder.append(TextUtils.join(",", pProperties));
            stringBuilder.append(") SELECT ");
            stringBuilder.append(TextUtils.join(",", pProperties));
            stringBuilder.append(" FROM ").append(tTableName).append(";");

            db.execSQL(stringBuilder.toString());
        }
    }

    private void restoreData(Database db, Class... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig dDaoConfig = new DaoConfig(db, daoClasses[i]);
            String tTableName = dDaoConfig.tablename;
            String tTempTableName = dDaoConfig.tablename.concat("_TEMP");
            ArrayList pProperties = new ArrayList();
            ArrayList pPropertiesQuery = new ArrayList();
            for (int j = 0; j < dDaoConfig.properties.length; j++) {
                String cColumnName = dDaoConfig.properties[j].columnName;
                if (getColumns(db, tTempTableName).contains(cColumnName)) {
                    pProperties.add(cColumnName);
                    pPropertiesQuery.add(cColumnName);
                } else {
                    try {
                        if (getTypeByClass(dDaoConfig.properties[j].type).equals("INTEGER")) {
                            pPropertiesQuery.add("0 as " + cColumnName);
                            pProperties.add(cColumnName);
                        }
                    } catch (Exception e) {
                        SmartLog.e(TAG, e.getMessage());
                    }
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("INSERT INTO ").append(tTableName).append(" (");
            stringBuilder.append(TextUtils.join(",", pProperties));
            stringBuilder.append(") SELECT ");
            stringBuilder.append(TextUtils.join(",", pPropertiesQuery));
            stringBuilder.append(" FROM ").append(tTempTableName).append(";");

            StringBuilder dropTableStringBuilder = new StringBuilder();
            dropTableStringBuilder.append("DROP TABLE ").append(tTempTableName);

            db.execSQL(stringBuilder.toString());
            db.execSQL(dropTableStringBuilder.toString());
        }
    }

    private String getTypeByClass(Class type) throws Exception {
        if (type.equals(String.class)) {
            return "TEXT";
        }

        if (type.equals(Long.class) || type.equals(Integer.class) ||
                type.equals(long.class) || type.equals(int.class)) {
            return "INTEGER";
        }

        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return "BOOLEAN";
        }

        Exception exception = new Exception("migration helper - class doesn't match with the current parameters".concat(" - Class: ").concat(type.toString()));
        SmartLog.e(TAG, exception.getMessage());
        throw exception;
    }
}
