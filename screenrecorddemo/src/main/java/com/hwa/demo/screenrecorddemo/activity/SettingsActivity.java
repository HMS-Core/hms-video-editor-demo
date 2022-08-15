/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.
 */

package com.hwa.demo.screenrecorddemo.activity;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.huawei.hms.videoeditor.screenrecord.HVERecord;
import com.huawei.hms.videoeditor.screenrecord.HVERecordConfiguration;
import com.huawei.hms.videoeditor.screenrecord.data.HVENotificationConfig;
import com.huawei.hms.videoeditor.screenrecord.enums.HVEOrientationMode;
import com.huawei.hms.videoeditor.screenrecord.enums.HVEResolutionMode;
import com.hwa.demo.screenrecorddemo.R;
import com.hwa.demo.screenrecorddemo.StorageFolders;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private StorageFolders storagePaths;
    private ImageView backImg;
    private RadioButton radioResolutionMode480;
    private RadioButton radioResolutionMode720;
    private RadioButton radioResolutionMode1080;
    private RadioButton radioOriantationAdaptive;
    private RadioButton radioOriantationLandscape;
    private RadioButton radioOriantationPortrait;
    private RadioButton radioStorageDefaultInternal;
    private RadioButton radioStorageCustomInternal;
    private RadioButton radioStorageDCIM_Camera;
    private RadioButton radioStorageDCIM_Custom;
    private SwitchMaterial switchMicStatus;
    private SwitchMaterial switchNotificationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hms_scr_layout_activity_settings);
        storagePaths = StorageFolders.getInstance(getApplicationContext());
        initViews();
        setListeners();
        setResolutionRadioStatus();
        setOriantationRadioStatus();
        setDirectoryRadioStatus();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == backImg.getId()) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setConfig();
        setResult(0);
    }

    private void initViews() {
        switchMicStatus = findViewById(R.id.switchMicStatus);
        switchNotificationStatus = findViewById(R.id.switch_notification);
        backImg = findViewById(R.id.img_back);
        switchMicStatus.setChecked(HVERecord.getConfigurations().getMicIsOpen());
        switchNotificationStatus.setChecked(HVERecord.getNotificationConfig().isCustomLayoutSet());

        radioResolutionMode480 = findViewById(R.id.radio_button_480);
        radioResolutionMode720 = findViewById(R.id.radio_button_720);
        radioResolutionMode1080 = findViewById(R.id.radio_button_1080);

        radioOriantationAdaptive = findViewById(R.id.radio_button_adaptive);
        radioOriantationLandscape = findViewById(R.id.radio_button_landscape);
        radioOriantationPortrait = findViewById(R.id.radio_button_portrait);

        radioStorageDefaultInternal = findViewById(R.id.radio_storage_default_internal);
        radioStorageCustomInternal = findViewById(R.id.radio_storage_custom_internal);
        radioStorageDCIM_Camera = findViewById(R.id.radio_storage_dcim_camera);
        radioStorageDCIM_Custom = findViewById(R.id.radio_button_dcim_custom);
    }

    private void setListeners() {
        backImg.setOnClickListener(this);
        switchMicStatus.setOnClickListener(this);
        switchNotificationStatus.setOnClickListener(v -> setNotificationConfig());

        radioResolutionMode480.setOnClickListener(this);
        radioResolutionMode720.setOnClickListener(this);
        radioResolutionMode1080.setOnClickListener(this);

        radioOriantationAdaptive.setOnClickListener(this);
        radioOriantationLandscape.setOnClickListener(this);
        radioOriantationPortrait.setOnClickListener(this);

        radioStorageDefaultInternal.setOnClickListener(this);
        radioStorageCustomInternal.setOnClickListener(this);
        radioStorageDCIM_Camera.setOnClickListener(this);
        radioStorageDCIM_Custom.setOnClickListener(this);
    }

    private void setResolutionRadioStatus(){
        switch (HVERecord.getConfigurations().getResolutionMode()) {
            case RES_1080P:
                radioResolutionMode1080.setChecked(true);
                break;
            case RES_720P:
                radioResolutionMode720.setChecked(true);
                break;
            case RES_480P:
                radioResolutionMode480.setChecked(true);
                break;
        }
    }

    private void setOriantationRadioStatus(){
        switch (HVERecord.getConfigurations().getOrientationMode()) {
            case ADAPTIVE:
                radioOriantationAdaptive.setChecked(true);
                break;
            case PORTRAIT:
                radioOriantationPortrait.setChecked(true);
                break;
            case LANDSCAPE:
                radioOriantationLandscape.setChecked(true);
                break;
        }
    }
    private void setDirectoryRadioStatus(){
        File directory = HVERecord.getConfigurations().getStorageFile(getApplicationContext());
        if (null == directory) {
            radioStorageDefaultInternal.setChecked(true);
        } else if (directory == storagePaths.getDefaultInternal()) {
            radioStorageDefaultInternal.setChecked(true);
        } else if (directory == storagePaths.getCustomInternalFolder()) {
            radioStorageCustomInternal.setChecked(true);
        } else if (directory == storagePaths.getCameraFolder()) {
            radioStorageDCIM_Camera.setChecked(true);
        } else if (directory == storagePaths.getCustomFolderInDCIM()) {
            radioStorageDCIM_Custom.setChecked(true);
        }
    }

    private void setConfig() {
        HVERecordConfiguration configuration = new HVERecordConfiguration.Builder()
                .setMicStatus(switchMicStatus.isChecked())
                .setOrientationMode(getSelectedOrientationMode())
                .setResolutionMode(getSelectedResolutionMode())
                .setStorageFile(getSelectedStorage())
                .build();
        HVERecord.setConfigurations(configuration);
    }

    private File getSelectedStorage() {
        if (radioStorageDefaultInternal.isChecked()) {
            return storagePaths.getDefaultInternal();
        }
        else if(radioStorageCustomInternal.isChecked()) {
            return storagePaths.getCustomInternalFolder();
        }
        else if (radioStorageDCIM_Camera.isChecked()) {
            return storagePaths.getCameraFolder();
        }
        else if (radioStorageDCIM_Custom.isChecked()) {
            return storagePaths.getCustomFolderInDCIM();
        }
        return null;
    }

    private HVEResolutionMode getSelectedResolutionMode() {
        if (radioResolutionMode480.isChecked()) {
            return HVEResolutionMode.RES_480P;
        } else if (radioResolutionMode720.isChecked()) {
            return HVEResolutionMode.RES_720P;
        } else {
            return HVEResolutionMode.RES_1080P;
        }
    }

    private HVEOrientationMode getSelectedOrientationMode() {
        if (radioOriantationLandscape.isChecked()) {
            return HVEOrientationMode.LANDSCAPE;
        } else if (radioOriantationPortrait.isChecked()) {
            return HVEOrientationMode.PORTRAIT;
        } else {
            return HVEOrientationMode.ADAPTIVE;
        }
    }

    private void setNotificationConfig() {
        if (switchNotificationStatus.isChecked()) {
            HVENotificationConfig notificationData = new HVENotificationConfig(R.layout.hms_scr_layout_custom_notification);
            notificationData.addClickEvent(R.id.btn_1, () -> {
                HVERecord.stopRecord();
            });

            notificationData.addClickEvent(R.id.btn_2, () -> {
                HVERecord.stopRecord();
            });
            notificationData.setDurationViewId(R.id.duration);
            notificationData.setCallingIntent(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            HVERecord.setNotificationConfig(notificationData);
        }
        else {
            HVERecord.setNotificationConfig(null);
        }
    }
}
