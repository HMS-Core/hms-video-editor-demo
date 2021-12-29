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

package com.huawei.hms.videoeditor.ui.mediaeditor.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.bean.HVERational;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.adapter.ProportionAdapter;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class VideoProportionFragment extends BaseFragment {

    private ImageView iv_certain;

    private HVERational oldRational;

    private HVERational rational;

    private HVETimeLine mTimeLine;

    private RecyclerView recyclerView;

    private ProportionAdapter proportionAdapter;

    private List<HVERational> properList;

    private int selectedPosition = 0;

    private int screenWidth;

    private int screenHeight;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_video_proportion;
    }

    @Override
    protected void initView(View view) {
        TextView mTitleTv = view.findViewById(R.id.tv_title);
        mTitleTv.setText(R.string.cut_second_menu_ratio);
        iv_certain = view.findViewById(R.id.iv_certain);
        recyclerView = view.findViewById(R.id.recycler);
        screenWidth = SizeUtils.screenWidth(mActivity);
        screenHeight = SizeUtils.screenHeight(mActivity);
    }

    @Override
    protected void initObject() {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor != null) {
            mTimeLine = editor.getTimeLine();
            oldRational = editor.getCanvasRational();
            properList = new ArrayList<>();
        }
    }

    @Override
    protected void initData() {
        List<Integer> imageList = new ArrayList<Integer>();
        imageList.add(R.drawable.crop_free);
        imageList.add(R.drawable.crop_full);
        imageList.add(R.drawable.crop_9_16);
        imageList.add(R.drawable.crop_16_9);
        imageList.add(R.drawable.crop_1_1);
        imageList.add(R.drawable.crop_4_3);
        imageList.add(R.drawable.crop_3_4);
        imageList.add(R.drawable.crop_2_35_1);
        imageList.add(R.drawable.crop_9_21);
        imageList.add(R.drawable.crop_21_9);

        if (mTimeLine == null || oldRational == null || properList == null) {
            return;
        }

        properList.add(new HVERational(0, 0));
        properList.add(new HVERational(screenWidth, screenHeight));
        properList.add(new HVERational(9, 16));
        properList.add(new HVERational(16, 9));
        properList.add(new HVERational(1, 1));
        properList.add(new HVERational(4, 3));
        properList.add(new HVERational(3, 4));
        properList.add(new HVERational(235, 100));
        properList.add(new HVERational(9, 21));
        properList.add(new HVERational(21, 9));
        properList.add(new HVERational(screenHeight, screenWidth));
        proportionAdapter = new ProportionAdapter(properList, imageList, context);
        recyclerView.setAdapter(proportionAdapter);

        for (int i = 0; i < properList.size(); i++) {
            if (properList.get(i).num == oldRational.num && properList.get(i).dem == oldRational.dem) {
                selectedPosition = i;
                proportionAdapter.setSelectPosition(selectedPosition);
                proportionAdapter.notifyItemChanged(selectedPosition);
                new Handler().postDelayed(() -> recyclerView.scrollToPosition(selectedPosition), 30);
                break;
            }
        }
    }

    @Override
    protected void initEvent() {
        if (proportionAdapter == null) {
            return;
        }

        proportionAdapter.setOnItemClickListener(position -> {
            HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
            if (editor == null || mTimeLine == null || properList == null || properList.size() <= 0) {
                return;
            }

            selectedPosition = proportionAdapter.getSelectPosition();
            if (selectedPosition != position) {
                proportionAdapter.setSelectPosition(position);
                if (selectedPosition != -1) {
                    proportionAdapter.notifyItemChanged(selectedPosition);
                }
                proportionAdapter.notifyItemChanged(position);

                rational = properList.get(position);
                editor.setCanvasRational(rational);
                editor.seekTimeLine(mTimeLine.getCurrentTime());
            }
            selectedPosition = position;
        });

        iv_certain.setOnClickListener(new OnClickRepeatedListener(v -> {
            mActivity.onBackPressed();
        }));
    }

    @Override
    protected int setViewLayoutEvent() {
        return FIXED_HEIGHT_210;
    }
}
