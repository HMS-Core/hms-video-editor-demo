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

package com.huawei.hms.videoeditor.ui.mediaeditor.ai.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.camera.CameraParam;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.ArrayList;
import java.util.List;

public class PreviewBeautyFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private View mContentView;

    private Button mBtnCompare;

    private LinearLayout mLayoutProgress;

    private TextView mTypeValueView;

    private SeekBar mValueSeekBar;

    private TabLayout mEffectTabLayout;

    private ViewPager mEffectViewPager;

    private List<View> mEffectViewLists = new ArrayList<>();

    private List<String> mEffectTitleLists = new ArrayList<>();

    private RecyclerView mBeautyRecyclerView;

    private LinearLayoutManager mBeautyLayoutManager;

    private PreviewBeautyAdapter mBeautyAdapter;

    private Button mBtnReset;

    private LayoutInflater mInflater;

    private Activity mActivity;

    private CameraParam mCameraParam;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            mActivity = getActivity();
        }
        mInflater = LayoutInflater.from(context);
        mCameraParam = CameraParam.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_filter_edit, container, false);
        initView(mContentView);
        return mContentView;
    }

    private void initView(View view) {
        initCompareButton(view);
        initBeautyProgress(view);
        initViewList(view);
    }

    private void initCompareButton(@NonNull View view) {
        mBtnCompare = view.findViewById(R.id.btn_compare);
        mBtnCompare.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mCompareEffectListener != null) {
                        mCompareEffectListener.onCompareEffect(true);
                    }
                    mBtnCompare.setBackgroundResource(R.drawable.ic_camera_compare_pressed);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mCompareEffectListener != null) {
                        mCompareEffectListener.onCompareEffect(false);
                    }
                    mBtnCompare.setBackgroundResource(R.drawable.ic_camera_compare_normal);
                    break;
            }
            return true;
        });
    }

    private void initBeautyProgress(@NonNull View view) {
        mLayoutProgress = view.findViewById(R.id.layout_progress);
        mTypeValueView = view.findViewById(R.id.tv_type_value);
        mValueSeekBar = view.findViewById(R.id.value_progress);
        mValueSeekBar.setMax(100);
        mValueSeekBar.setOnSeekBarChangeListener(this);
    }

    private void initViewList(@NonNull View view) {
        mEffectViewLists.clear();
        mEffectTitleLists.clear();

        addBeautyView();

        mEffectViewPager = view.findViewById(R.id.vp_effect);
        mEffectTabLayout = view.findViewById(R.id.tl_effect_type);
        mEffectViewPager.setAdapter(new ViewPagerAdapter(mEffectViewLists, mEffectTitleLists));
        mEffectTabLayout.setupWithViewPager(mEffectViewPager);
        mEffectTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mLayoutProgress.setVisibility(tab.getPosition() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void addBeautyView() {
        mLayoutProgress.setVisibility(View.VISIBLE);
        View beautyView = mInflater.inflate(R.layout.view_preview_beauty, null);
        mBeautyRecyclerView = beautyView.findViewById(R.id.preview_beauty_list);
        mBeautyLayoutManager = new LinearLayoutManager(mActivity);
        mBeautyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBeautyRecyclerView.setLayoutManager(mBeautyLayoutManager);
        mBeautyAdapter = new PreviewBeautyAdapter(mActivity);
        mBeautyRecyclerView.setAdapter(mBeautyAdapter);
        mBeautyAdapter.addOnBeautySelectedListener((position, beautyName) -> setSeekBarBeautyParam(position));
        mBtnReset = beautyView.findViewById(R.id.btn_beauty_reset);
        mBtnReset.setOnClickListener(v -> {
            mCameraParam.beauty.reset();
            setSeekBarBeautyParam(mBeautyAdapter.getSelectedPosition());
        });
        mBtnReset.setVisibility(View.INVISIBLE);
        setSeekBarBeautyParam(mBeautyAdapter.getSelectedPosition());
        mEffectViewLists.add(beautyView);
        mEffectTitleLists.add(getResources().getString(R.string.beauty));
    }

    @Override
    public void onDestroyView() {
        mContentView = null;
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (mEffectTabLayout.getSelectedTabPosition() == 0) { // 美颜
                processBeautyParam(mBeautyAdapter.getSelectedPosition(), progress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setSeekBarBeautyParam(int position) {
        if (position == 0) {
            mValueSeekBar.setProgress((int) (mCameraParam.beauty.blurDegree * 100));
        } else if (position == 1) {
            mValueSeekBar.setProgress((int) (mCameraParam.beauty.whiteDegree * 100));
        } else if (position == 2) {
            mValueSeekBar.setProgress((int) (mCameraParam.beauty.thinFace * 100));
        } else if (position == 3) {
            mValueSeekBar.setProgress((int) (mCameraParam.beauty.bigEye * 100));
        } else if (position == 4) {
            mValueSeekBar.setProgress((int) (mCameraParam.beauty.brightEye * 100));
        }
    }

    private void processBeautyParam(int position, int progress) {
        if (position == 0) {
            mCameraParam.beauty.blurDegree = progress / 100.0f;
        } else if (position == 1) {
            mCameraParam.beauty.whiteDegree = progress / 100.0f;
        } else if (position == 2) {
            mCameraParam.beauty.thinFace = progress / 100.0f;
        } else if (position == 3) {
            mCameraParam.beauty.bigEye = progress / 100.0f;
        } else if (position == 4) {
            mCameraParam.beauty.brightEye = progress / 100.0f;
        }
    }

    public interface OnCompareEffectListener {

        void onCompareEffect(boolean compare);
    }

    public void addOnCompareEffectListener(OnCompareEffectListener listener) {
        mCompareEffectListener = listener;
    }

    private OnCompareEffectListener mCompareEffectListener;
}
