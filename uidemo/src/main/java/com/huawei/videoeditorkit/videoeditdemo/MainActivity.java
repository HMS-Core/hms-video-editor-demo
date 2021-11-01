/*
 * Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.videoeditorkit.videoeditdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.videoeditor.ui.api.DraftInfo;
import com.huawei.hms.videoeditor.ui.api.MediaApplication;
import com.huawei.hms.videoeditor.ui.api.MediaExportCallBack;
import com.huawei.hms.videoeditor.ui.api.MediaInfo;
import com.huawei.hms.videoeditor.ui.api.VideoEditorLaunchOption;
import com.huawei.videoeditorkit.videoeditdemo.adpter.DraftAdapter;
import com.huawei.videoeditorkit.videoeditdemo.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUESTS = 1;

    private LinearLayout startEdit;

    private ImageView mSetting;

    private Context mContext;

    private RecyclerView recyclerView;

    private DraftAdapter draftAdapter;

    private List<DraftInfo> draftInfos;

    private int position;
    private final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        initSetting();
        initView();
        initData();
        initEvent();
    }

    private void requestPermission() {
        PermissionUtils.checkManyPermissions(mContext, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                startUIActivity();
            }

            @Override
            public void onUserHasReject(String... permission) {
                PermissionUtils.requestManyPermissions(mContext, PERMISSIONS, PERMISSION_REQUESTS);
            }

            @Override
            public void onUserRejectAndDontAsk(String... permission) {
                PermissionUtils.requestManyPermissions(mContext, PERMISSIONS, PERMISSION_REQUESTS);
            }
        });
    }

    private void initSetting() {
        UUID uuid = UUID.randomUUID();
        // Unique ID generated when the  VideoEdit Kit is integrated.
        MediaApplication.getInstance().setLicenseId(uuid.toString());

        // Setting the APIKey of an Application
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(this);
        MediaApplication.getInstance().setApiKey(config.getString("client/api_key"));

        // Setting the Application Token
        // MediaApplication.getInstance().setAccessToken(config.getString("client/api_key"));
        // Setting Exported Callbacks
        MediaApplication.getInstance().setOnMediaExportCallBack(CALL_BACK);
    }

    @Override
    protected void onResume() {
        super.onResume();
        draftInfos = MediaApplication.getInstance().getDraftList();
        draftAdapter.setData(draftInfos);
        draftAdapter.notifyDataSetChanged();
    }

    private void initEvent() {
        startEdit.setOnClickListener(v -> requestPermission());
        draftAdapter.setSelectedListener(new DraftAdapter.OnStyleSelectedListener() {
            @Override
            public void onStyleSelected(int position) {
                String draftId = draftInfos.get(position).getDraftId();
                MediaApplication.getInstance()
                        .launchEditorActivity(MainActivity.this,
                                new VideoEditorLaunchOption.Builder()
                                        .setStartMode(MediaApplication.START_MODE_IMPORT_FROM_DRAFT)
                                        .setDraftId(draftId)
                                        .build());
            }
        });

        draftAdapter.setLongSelectedListener(new DraftAdapter.OnStyleLongSelectedListener() {
            @Override
            public void onStyleLongSelected(View v, int position) {
                MainActivity.this.position = position;
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu, popup.getMenu());
                popup.setOnMenuItemClickListener(MainActivity.this);
                popup.show();
            }
        });


        mSetting.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, SettingActivity.class)));
    }

    private void initData() {
        draftAdapter = new DraftAdapter(this);
        draftAdapter.setData(draftInfos);
        draftAdapter.notifyDataSetChanged();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(draftAdapter);
    }

    private void initView() {
        startEdit = findViewById(R.id.start_edit);
        mSetting = findViewById(R.id.setting);
        recyclerView = findViewById(R.id.draft_rv);
        recyclerView.setVisibility(View.VISIBLE);
    }

    // The default UI is displayed.

    /**
     * Startup mode (START_MODE_IMPORT_FROM_MEDIA): Startup by importing videos or images.
     */
    private void startUIActivity() {
        // VideoEditorLaunchOption build = new VideoEditorLaunchOption
        // .Builder()
        // .setStartMode(START_MODE_IMPORT_FROM_MEDIA)
        // .build();
        // The default startup mode is (START_MODE_IMPORT_FROM_MEDIA) when the option parameter is set to null.
        MediaApplication.getInstance().launchEditorActivity(this, null);
    }

    // Export interface callback
    private static final MediaExportCallBack CALL_BACK = new MediaExportCallBack() {
        @Override
        public void onMediaExportSuccess(MediaInfo mediaInfo) {
            String mediaPath = mediaInfo.getMediaPath();
            Log.i(TAG, "The current video export path is" + mediaPath);
        }

        @Override
        public void onMediaExportFailed(int errorCode) {
            Log.d(TAG, "errorCode" + errorCode);
        }
    };

    /**
     * Display Go to App Settings Dialog
     */
    private void showToAppSettingDialog() {
        new AlertDialog.Builder(this).setMessage(getString(R.string.permission_tips))
                .setPositiveButton(getString(R.string.setting), (dialog, which) -> PermissionUtils.toAppSetting(mContext))
                .setNegativeButton(getString(R.string.cancels), null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUESTS) {
            PermissionUtils.onRequestMorePermissionsResult(mContext, PERMISSIONS,
                    new PermissionUtils.PermissionCheckCallBack() {
                        @Override
                        public void onHasPermission() {
                            startUIActivity();
                        }

                        @Override
                        public void onUserHasReject(String... permission) {

                        }

                        @Override
                        public void onUserRejectAndDontAsk(String... permission) {
                            showToAppSettingDialog();
                        }
                    });
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                String draftId = draftInfos.get(MainActivity.this.position).getDraftId();
                List<String> draftIds = new ArrayList<>();
                draftIds.add(draftId);
                MediaApplication.getInstance().deleteDrafts(draftIds);

                draftInfos = MediaApplication.getInstance().getDraftList();
                draftAdapter.setData(draftInfos);
                draftAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return false;
    }
}