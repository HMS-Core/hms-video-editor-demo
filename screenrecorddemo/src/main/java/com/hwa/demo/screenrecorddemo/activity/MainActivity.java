/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.
 */

package com.hwa.demo.screenrecorddemo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.videoeditor.screenrecord.HVERecord;
import com.huawei.hms.videoeditor.screenrecord.HVERecordConfiguration;
import com.huawei.hms.videoeditor.screenrecord.HVERecordListener;
import com.huawei.hms.videoeditor.screenrecord.data.HVERecordFile;
import com.huawei.hms.videoeditor.screenrecord.enums.HVEErrorCode;
import com.huawei.hms.videoeditor.screenrecord.enums.HVERecordState;
import com.hwa.demo.screenrecorddemo.R;
import com.hwa.demo.screenrecorddemo.RecordListAdapter;
import com.hwa.demo.screenrecorddemo.StorageFolders;
import com.hwa.demo.screenrecorddemo.util.FormatHelper;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HVERecordListener, View.OnClickListener {

    private Button btnRecording;

    private TextView txtStatus;

    private TextView txtDropdown;

    private ImageView imgSettings;

    private RecyclerView recyclerView;

    private ConstraintLayout menuDropdown;

    private StorageFolders storagePaths;
    private RecordListAdapter recordsAdapter;
    private List<HVERecordFile> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hms_scr_layout_activity_main);
        setViews();
        setRecordingButtonText();
        setListeners();
        HVERecord.init(this, this);
        storagePaths = StorageFolders.getInstance(getApplicationContext());
        setRecords();
    }

    private boolean checkPermissions() {
        int hasStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (hasStorage == PackageManager.PERMISSION_GRANTED && hasAudio == PackageManager.PERMISSION_GRANTED)
            return true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }

        if (isGranted) {
            onClick(btnRecording);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            boolean shouldShowExternal =
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            boolean shouldShowAudio = shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO);
            if (!shouldShowExternal || !shouldShowAudio) {
                showPermissionForSettings();
            }
        }
    }

    private void showPermissionForSettings() {
        new AlertDialog.Builder(this).setTitle("Permissions Rejected")
            .setMessage("Go to setting to enable permissions ?")
            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                openSettings();
                dialog.dismiss();
            })
            .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
            .show();
    }

    private void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void setViews() {
        btnRecording = findViewById(R.id.btnRecording);
        txtStatus = findViewById(R.id.txtStatus);
        recyclerView = findViewById(R.id.recyclerView);
        imgSettings = findViewById(R.id.img_settings);
        menuDropdown = findViewById(R.id.dropdownLayout);
        txtDropdown = findViewById(R.id.dropdownText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        txtDropdown.setText(R.string.hms_scr_str_default_internal);
    }

    private void setListeners() {
        btnRecording.setOnClickListener(this);
        imgSettings.setOnClickListener(this);
        menuDropdown.setOnClickListener(this);
    }

    private void setRecordingButtonText() {
        if (HVERecord.isRecording()) {
            btnRecording.setText(R.string.hms_scr_str_string_stop_recording);
        } else {
            btnRecording.setText(R.string.hms_scr_str_string_start_recording);
        }
    }

    @Override
    public void onRecordComplete(@NonNull HVERecordFile hveRecordFile) {
        recordsAdapter.addItem(hveRecordFile);
    }

    @Override
    public void onRecordError(@NonNull HVEErrorCode err, @NonNull String msg) {
    }

    @Override
    public void onRecordStateChange(@NonNull HVERecordState recordingStateHVE) {
        if (recordingStateHVE == HVERecordState.START) {
            btnRecording.setText(R.string.hms_scr_str_string_stop_recording);
        } else {
            btnRecording.setText(R.string.hms_scr_str_string_start_recording);
            txtStatus.setText(R.string.hms_scr_str_string_title_main);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void openMenu() {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, menuDropdown, Gravity.END);
        popup.getMenuInflater().inflate(R.menu.hms_scr_layout_menu_drop_down, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            File selectedStorageFolder;
            switch (item.getItemId()) {
                case R.id.defaultInternal: {
                    selectedStorageFolder = StorageFolders.getInstance(getApplicationContext()).getDefaultInternal();
                    break;
                }
                case R.id.customInternal: {
                    selectedStorageFolder =
                        StorageFolders.getInstance(getApplicationContext()).getCustomInternalFolder();
                    break;
                }
                case R.id.cameraInDcim: {
                    selectedStorageFolder = StorageFolders.getInstance(getApplicationContext()).getCameraFolder();
                    break;
                }
                case R.id.customInDcim: {
                    selectedStorageFolder = StorageFolders.getInstance(getApplicationContext()).getCustomFolderInDCIM();
                    break;
                }
                default:
                    selectedStorageFolder = null;
                    break;
            }
            if (selectedStorageFolder != null) {
                HVERecordConfiguration config = HVERecord.getConfigurations();
                config.setStorageFile(selectedStorageFolder);
                HVERecord.setConfigurations(config);
                txtDropdown.setText(item.getTitle());
                setRecords();
            }
            return true;
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        popup.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnRecording.getId()) {
            if (checkPermissions()) {
                if (!HVERecord.isRecording()) {
                    HVERecord.startRecord();
                } else {
                    HVERecord.stopRecord();
                }
            }
        } else if (v.getId() == imgSettings.getId()) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivityForResult(intent, 0);
        } else if (v.getId() == menuDropdown.getId()) {
            openMenu();
        }
    }

    public void setRecords() {
        records = HVERecord.getRecordList();
        recordsAdapter = new RecordListAdapter(this, records);
        recyclerView.setAdapter(recordsAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File directory = HVERecord.getConfigurations().getStorageFile(getApplicationContext());
        if (null == directory) {
            txtDropdown.setText(getString(R.string.hms_scr_str_default_internal));
        } else if (directory == storagePaths.getDefaultInternal()) {
            txtDropdown.setText(getString(R.string.hms_scr_str_default_internal));
        } else if (directory == storagePaths.getCustomInternalFolder()) {
            txtDropdown.setText(getString(R.string.hms_scr_str_custom_internal));
        } else if (directory == storagePaths.getCameraFolder()) {
            txtDropdown.setText(getString(R.string.hms_scr_str_camera_in_dcim));
        } else if (directory == storagePaths.getCustomFolderInDCIM()) {
            txtDropdown.setText(getString(R.string.hms_scr_str_custom_in_dcim));
        }
        setRecords();
    }

    @Override
    public void onRecordProgress(int duration) {
        txtStatus.setText(FormatHelper.durationFormat((long) duration * 1000));
    }
}
