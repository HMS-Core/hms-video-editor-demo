/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.
 */

package com.hwa.demo.screenrecorddemo;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.videoeditor.screenrecord.HVERecord;
import com.huawei.hms.videoeditor.screenrecord.data.HVERecordFile;
import com.hwa.demo.screenrecorddemo.activity.VideoPreviewActivity;
import com.hwa.demo.screenrecorddemo.util.Constants;
import com.hwa.demo.screenrecorddemo.util.DialogHelper;
import com.hwa.demo.screenrecorddemo.util.FormatHelper;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordListAdapterViewHolder> {
    List<HVERecordFile> recordFiles;
    Context context;

    public RecordListAdapter(Context context, List<HVERecordFile> recordFiles) {
        this.context = context;
        this.recordFiles = recordFiles;
    }

    @NonNull
    @Override
    public RecordListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.hms_scr_layout_item_record_app, viewGroup, false);
        return new RecordListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordListAdapterViewHolder adaptorHolder,
                                 @SuppressLint("RecyclerView") int position) {
        HVERecordFile recordFile = recordFiles.get(position);
        String filePath = recordFile.getUri().getPath().replace(recordFile.getName() + ".mp4", "");
        adaptorHolder.txtName.setText(recordFile.getName());
        String readableFileFormat = Formatter.formatFileSize(context, recordFile.size);
        adaptorHolder.txtSize.setText(readableFileFormat);
        adaptorHolder.txtDuration.setText(FormatHelper.durationFormatWithMillisecond(recordFile.getDuration()));
        adaptorHolder.txtTime.setText(FormatHelper.dateFormat(recordFile.getUpdateTime()));
        adaptorHolder.txtPath.setText(filePath);
        adaptorHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoPreviewActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_RECORD_FILE, recordFile);
            context.startActivity(intent);
        });
        adaptorHolder.imgBtnMenu.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.PopupMenu);
            PopupMenu popup = new PopupMenu(wrapper, adaptorHolder.imgBtnMenu);
            popup.getMenuInflater()
                    .inflate(R.menu.hms_scr_layout_menu_record, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.renameRecord) {
                    DialogHelper.showRenameDialog(context, recordFile.getName(),
                            value -> renameRecordFile(position, value));

                } else if (item.getItemId() == R.id.deleteRecord) {
                    DialogHelper.showDeleteDialog(context,
                            value -> deleteRecordFile(position));
                } else {
                    showShareIntent(recordFile);
                }
                return true;
            });
            try {
                Field[] fields = popup.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object mpHelper = field.get(popup);
                        Class<?> classPopupHelper = Class.forName(mpHelper.getClass().getName());
                        Method showIcon = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        showIcon.invoke(mpHelper, true);
                        break;
                    }
                }
            } catch (Exception ex) {
                Log.e("RecordListAdapter", "Failed to set fields: " + ex.getMessage());
            }
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return recordFiles.size();
    }

    private void renameRecordFile(int position, String name) {
        HVERecordFile recordFile = HVERecord.renameRecord(recordFiles.get(position), name);
        if (recordFile != recordFiles.get(position)) {
            recordFiles.set(position, recordFile);
            notifyItemChanged(position);
        }
    }

    private void showShareIntent(HVERecordFile recordFile) {
        File file = new File(recordFile.getUri().getPath());
        Uri uri = FileProvider.getUriForFile(context, "com.hwa.demo.screenrecorddemo.provider",
                file);
        context.startActivity(
                Intent.createChooser(
                        new Intent().setAction(Intent.ACTION_SEND)
                                .setType("video/*")
                                .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                .putExtra(
                                        Intent.EXTRA_STREAM,
                                        uri
                                ), context.getString(R.string.hms_scr_str_choose)
                )
        );
    }

    private void deleteRecordFile(int position) {
        if (HVERecord.deleteRecord(recordFiles.get(position))) {
            recordFiles.remove(position);
            notifyDataSetChanged();
        }
    }

    public void addItem(HVERecordFile hveRecordFile) {
        recordFiles.add(0, hveRecordFile);
        notifyDataSetChanged();
    }

    class RecordListAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        private final TextView txtSize;
        private final TextView txtDuration;
        private final TextView txtTime;
        private final TextView txtPath;
        private final ImageButton imgBtnMenu;

        public RecordListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.item_title);
            txtSize = itemView.findViewById(R.id.item_size);
            txtDuration = itemView.findViewById(R.id.item_duration);
            txtTime = itemView.findViewById(R.id.item_updatetime);
            imgBtnMenu = itemView.findViewById(R.id.imgItemMenu);
            txtPath = itemView.findViewById(R.id.item_path);
        }
    }
}
