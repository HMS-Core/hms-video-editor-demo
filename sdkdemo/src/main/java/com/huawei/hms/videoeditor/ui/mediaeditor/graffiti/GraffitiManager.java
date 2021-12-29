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

package com.huawei.hms.videoeditor.ui.mediaeditor.graffiti;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.view.GraffitiView;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class GraffitiManager {
    private static final String TAG = "GraffitiManager";

    GraffitiView mGraffitiView;

    LinearLayout controlMenuLayout;

    EditPreviewViewModel mEditPreviewViewModel;

    FragmentActivity activity;

    ImageView redo;

    ImageView undo;

    ImageView refresh;

    ImageView earser;

    View earserBg;

    public String path;

    private MenuViewModel mMenuViewModel;

    public GraffitiManager(GraffitiView view, LinearLayout layout, Context context, HuaweiVideoEditor editor) {
        mGraffitiView = view;
        controlMenuLayout = layout;
        activity = (FragmentActivity) context;
        mGraffitiView.setEditor(editor);
    }

    public void start() {
        mEditPreviewViewModel = new ViewModelProvider(activity).get(EditPreviewViewModel.class);
        mMenuViewModel = new ViewModelProvider(activity).get(MenuViewModel.class);
        redo = controlMenuLayout.findViewById(R.id.giffiti_redo);
        redo.setImageAlpha(25);
        undo = controlMenuLayout.findViewById(R.id.giffiti_undo);
        undo.setImageAlpha(25);
        refresh = controlMenuLayout.findViewById(R.id.giffiti_fresh);
        earser = controlMenuLayout.findViewById(R.id.giffiti_eraser);
        earserBg = controlMenuLayout.findViewById(R.id.item_select_view);
        mGraffitiView.setDrawingListChangedListener((drawListSize, saveListSize) -> {
            if (drawListSize > 0) {
                undo.setImageAlpha(255);
            } else {
                undo.setImageAlpha(25);
            }
            if (saveListSize > 0) {
                redo.setImageAlpha(255);
            } else {
                redo.setImageAlpha(25);
            }

        });
        mGraffitiView.setEarserChangedListener(() -> earserBg.setVisibility(View.GONE));
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo.setImageAlpha(25);
                redo.setImageAlpha(25);
                mGraffitiView.clear();
            }
        });
        earser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                earserBg.setVisibility(View.VISIBLE);
                mGraffitiView.eraser();
            }
        });
        mEditPreviewViewModel.getGraffitiInfo().observe(activity, new Observer<GraffitiInfo>() {
            @Override
            public void onChanged(GraffitiInfo graffitiInfo) {
                if (graffitiInfo != null) {
                    switch (graffitiInfo.type) {
                        case DEFAULT:
                            Log.i(TAG, "onChanged: Visichanged" + graffitiInfo.type);
                            ((ViewGroup) mGraffitiView.getParent()).setVisibility(graffitiInfo.visible);
                            break;
                        case ALPHA:
                            mGraffitiView.resetAlpha(graffitiInfo.stokeAlpha);
                            break;
                        case COLOR:
                            mGraffitiView.resetPaintColor(graffitiInfo.stokeColor);
                            break;
                        case WIDTH:
                            mGraffitiView.resetPaintWidth(graffitiInfo.stokeWidth);
                            break;
                        case SAVE:
                            if (mGraffitiView.getDrawSize() > 0) {
                                path = mGraffitiView.saveBitmap();
                                mMenuViewModel.addGraffiti(path, "");
                            }
                            mGraffitiView.clear();
                            ((ViewGroup) mGraffitiView.getParent()).setVisibility(View.GONE);
                            break;
                        case SHAPE:
                            mGraffitiView.resetPaintShape(graffitiInfo.shape);
                            break;
                    }
                }
            }
        });
    }

    public void clearGraffit() {
        mGraffitiView.clear();
    }
}
