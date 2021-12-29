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

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.util.ArrayList;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RMCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.view.decoration.SpacesItemDecoration;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.GraffitiColorAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.GraffitiInfo;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.GraffitiShapeAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.GraffitiStokeAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.OnGraffitiChanged;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.MySeekBar;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GraffitiFragment extends BaseFragment implements OnGraffitiChanged {
    private static final String TAG = "GraffitiFragment";

    private ImageView iv_certain;

    private RecyclerView shapeSelected;

    protected EditPreviewViewModel mEditPreviewViewModel;

    private TextView tv_title;

    private RecyclerView mColorRecyclerView;

    private RecyclerView stokeRecycler;

    private MySeekBar seekBar;

    private ImageView ivClose;

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_graffiti;
    }

    public static GraffitiFragment newInstance(boolean isForCover) {
        Bundle args = new Bundle();
        args.putBoolean("forCover", isForCover);
        GraffitiFragment fragment = new GraffitiFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        GraffitiInfo info = new GraffitiInfo();
        info.type = GraffitiInfo.TYPE.DEFAULT;
        info.visible = View.VISIBLE;

        mEditPreviewViewModel.setGraffitiInfo(info);

        Log.i(TAG, "initView: ");
        iv_certain = view.findViewById(R.id.iv_certain);
        tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.tuya_name);
        ivClose = view.findViewById(R.id.iv_close);
        ivClose.setVisibility(View.VISIBLE);
        ivClose.setOnClickListener(v -> {
            mEditPreviewViewModel.setClearGraffitView(true);
            mActivity.onBackPressed();
        });
        seekBar = view.findViewById(R.id.seekbar);
        if (ScreenUtil.isRTL()) {
            seekBar.setScaleX(RTL_UI);
        } else {
            seekBar.setScaleX(LTR_UI);
        }
        seekBar.setOnProgressChangedListener(progress -> {
            GraffitiInfo info1 = new GraffitiInfo();
            info1.type = GraffitiInfo.TYPE.ALPHA;
            info1.stokeAlpha = (int) (((float) progress / 100f) * 255f);
            mEditPreviewViewModel.setGraffitiInfo(info1);
            mEditPreviewViewModel.setToastTime((int) seekBar.getProgress() + "");
        });
        seekBar.setcTouchListener(
            isTouch -> mEditPreviewViewModel.setToastTime(isTouch ? seekBar.getProgress() + "" : ""));
        shapeSelected = view.findViewById(R.id.shape_select);
        shapeSelected.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        shapeSelected.addItemDecoration(new SpacesItemDecoration(ScreenUtil.dp2px(8)));

        GraffitiShapeAdapter adapter = new GraffitiShapeAdapter();
        adapter.setGraffitiChangedListener(this);
        shapeSelected.setAdapter(adapter);
        initColorData(view);

        stokeRecycler = view.findViewById(R.id.stoke_select);
        stokeRecycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        stokeRecycler.addItemDecoration(new SpacesItemDecoration(ScreenUtil.dp2px(5)));
        GraffitiStokeAdapter adapter1 = new GraffitiStokeAdapter();
        adapter1.setGraffitiChangedListener(this);
        stokeRecycler.setAdapter(adapter1);
    }

    private void initColorData(View view) {
        ArrayList<Integer> colourList = new ArrayList<>();
        colourList.add(0xFFFFFFFF);
        colourList.add(0xFFB8B8B8);
        colourList.add(0xFF808080);
        colourList.add(0xFF333333);
        colourList.add(0xFF000000);
        colourList.add(0xFFFBACAC);
        colourList.add(0xFFE26464);
        colourList.add(0xFFD14040);
        colourList.add(0xFFC41B1B);
        colourList.add(0xFF950202);
        colourList.add(0xFFFBF1CA);
        colourList.add(0xFFE8D471);
        colourList.add(0xFFD2B72D);
        colourList.add(0xFFBC9A12);
        colourList.add(0xFFDFFBBF);
        colourList.add(0xFFA9E36E);
        colourList.add(0xFF5FC235);
        colourList.add(0xFF3EA214);
        colourList.add(0xFF056A01);
        colourList.add(0xFFCCF6D1);
        colourList.add(0xFF8CEC97);
        colourList.add(0xFF48DB59);
        colourList.add(0xFF169B25);
        colourList.add(0xFF03680E);
        colourList.add(0xFFC3F5F6);
        colourList.add(0xFF71E7E9);
        colourList.add(0xFF32C7C9);
        colourList.add(0xFF10A5A7);
        colourList.add(0xFF005E60);
        colourList.add(0xFFDEE8FE);
        colourList.add(0xFF8FAEF2);
        colourList.add(0xFF3163D1);
        colourList.add(0xFF1243AE);
        colourList.add(0xFF022268);
        colourList.add(0xFFC1BFFB);
        colourList.add(0xFF8683F2);
        colourList.add(0xFF4642E4);
        colourList.add(0xFF2521CE);
        colourList.add(0xFF0A0798);
        colourList.add(0xFFF3CCF6);
        colourList.add(0xFFF3CCF6);
        colourList.add(0xFFE48DEB);
        colourList.add(0xFFD354DE);
        colourList.add(0xFFA612B4);
        colourList.add(0xFF770881);
        colourList.add(0xFFF6C3D9);
        colourList.add(0xFFEC89B4);
        colourList.add(0xFFD9548E);
        colourList.add(0xFFB71E61);
        colourList.add(0xFF750A39);
        colourList.add(0xFF85CEFF);
        colourList.add(0xFF5F98BE);
        colourList.add(0xFF46789A);
        colourList.add(0xFF2A4557);
        colourList.add(0xFF12202A);
        colourList.add(0xFFD6CC96);
        colourList.add(0xFFA49C70);
        colourList.add(0xFF6B6647);
        colourList.add(0xFF27251B);
        colourList.add(0xFFF1C7BD);
        colourList.add(0xFFC8836F);
        colourList.add(0xFFAB604D);
        colourList.add(0xFF823E2C);
        colourList.add(0xFF3E180E);
        mColorRecyclerView = view.findViewById(R.id.color_select);

        View cancelHeader = LayoutInflater.from(context).inflate(R.layout.adapter_edittext_label_header, null, false);
        View viewBg = cancelHeader.findViewById(R.id.bg_view_head);
        viewBg.setVisibility(View.GONE);
        RelativeLayout.LayoutParams layoutParams =
            new RelativeLayout.LayoutParams(SizeUtils.dp2Px(context, 40), SizeUtils.dp2Px(context, 40));
        layoutParams.setMarginEnd(SizeUtils.dp2Px(context, 8));
        cancelHeader.setLayoutParams(layoutParams);

        mColorRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
        GraffitiColorAdapter canvasColorAdapter =
            new GraffitiColorAdapter(mActivity, colourList, R.layout.item_color_view, mEditPreviewViewModel);
        mColorRecyclerView.setAdapter(canvasColorAdapter);
        canvasColorAdapter.addHeaderView(cancelHeader);
        cancelHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewBg.setVisibility(View.VISIBLE);
                mEditPreviewViewModel.setHeadClick(true);
                GraffitiInfo info = new GraffitiInfo();
                info.visible = View.VISIBLE;
                mEditPreviewViewModel.setGraffitiInfo(info);
            }
        });
        canvasColorAdapter.setOnItemClickListener(new RMCommandAdapter.OnItemClickListener() {
            @Override
            public void onItemTouch(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                mEditPreviewViewModel.setHeadClick(false);
                viewBg.setVisibility(View.GONE);
                GraffitiInfo info = new GraffitiInfo();
                info.type = GraffitiInfo.TYPE.COLOR;
                info.stokeColor = colourList.get(position - 1);
                mEditPreviewViewModel.setGraffitiInfo(info);
            }

            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                return false;
            }
        });
    }

    @Override
    protected void initObject() {
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        iv_certain.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mEditPreviewViewModel != null) {
                GraffitiInfo info = new GraffitiInfo();
                info.type = GraffitiInfo.TYPE.SAVE;
                mEditPreviewViewModel.setGraffitiInfo(info);
            }
            mActivity.onBackPressed();
        }));

    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    @Override
    public void onGraffitiChanged(GraffitiInfo info) {
        if (mEditPreviewViewModel != null) {
            mEditPreviewViewModel.setGraffitiInfo(info);
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GraffitiInfo info = new GraffitiInfo();
        info.visible = View.GONE;
        mEditPreviewViewModel.setGraffitiInfo(info);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDynamicViewLayoutChange();
    }
}
