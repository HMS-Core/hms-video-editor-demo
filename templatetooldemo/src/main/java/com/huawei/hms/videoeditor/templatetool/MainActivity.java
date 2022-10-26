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

package com.huawei.hms.videoeditor.templatetool;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.agconnect.AGConnectInstance;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FrameLayout album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);

        Window activityWindow = getWindow();
        activityWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activityWindow.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        activityWindow.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

        setContentView(R.layout.activity_main_s);
        album = findViewById(R.id.from_album);
        init();
        album.setOnClickListener(this);
    }

    private void init() {
        String apiKey = AGConnectInstance.getInstance().getOptions().getString("client/api_key");
        TemplateToolApplication.getInstance().setAccessToken(apiKey);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.from_album:
                startUIActivity();
                break;
        }
    }

    private void startUIActivity() {
        TemplateToolApplication.getInstance().launchToolActivity(this);
    }

    private void verifyStoragePermissions(MainActivity activity) {
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE =
            {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        try {
            int permissionRead =
                ActivityCompat.checkSelfPermission(activity, "android.permission.READ_EXTERNAL_STORAGE");
            if (permissionRead != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }

            int permissionWrite =
                ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
        }
    }
}