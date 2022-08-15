/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.
 */

package com.hwa.demo.screenrecorddemo.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hwa.demo.screenrecorddemo.IDialogCallback;
import com.hwa.demo.screenrecorddemo.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DialogHelper {

    private static String patternStr = "[/.*<>:|'?\\[\\]~]";

    private DialogHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static void showRenameDialog(Context context, String name,
                                        IDialogCallback dialogCallback) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.hms_scr_layout_custom_dialog, null);
        final TextView txtNd = (TextView) dialogView.findViewById(R.id.limitOfText);
        txtNd.setText(String.format("%d/50", name.length()));
        final EditText dialogEditText = (EditText) dialogView.findViewById(R.id.dialogEditText);
        dialogEditText.setText(name);
        dialogEditText.setSelectAllOnFocus(true);
        dialogEditText.requestFocus();
        dialogEditText.setTextColor(Color.WHITE);
        dialogEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        dialogEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                txtNd.setText(String.format("%d/50", s.toString().length()));
            }
        });
        showKeyboard(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);

        builder.setTitle(context.getString(R.string.hms_scr_str_rename));
        builder.setView(dialogView);
        builder.setCancelable(false);

        builder.setPositiveButton(context.getString(R.string.hms_scr_str_ok), (dialog, which) -> {

            dialogCallback.okButton(dialogEditText.getText().toString());
            hideKeyboard(dialogEditText, context);
            if (isSpecialCharacter(dialogEditText.getText().toString())) {
                Toast.makeText(context, "Special characters /.*<>:|'? are not supported", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton(context.getString(R.string.hms_scr_str_cancel), (dialog, which) ->
                hideKeyboard(dialogEditText, context));
        builder.show();
    }

    private static boolean isSpecialCharacter(String text) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public static void showDeleteDialog(Context context, IDialogCallback dialogCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setTitle(context.getString(R.string.hms_scr_str_delete));
        builder.setPositiveButton(context.getString(R.string.hms_scr_str_ok), (dialog, which) ->
                dialogCallback.okButton(""));
        builder.setNegativeButton(context.getString(R.string.hms_scr_str_cancel), (dialog, which) -> {
        });
        builder.show();
    }

    private static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private static void hideKeyboard(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}