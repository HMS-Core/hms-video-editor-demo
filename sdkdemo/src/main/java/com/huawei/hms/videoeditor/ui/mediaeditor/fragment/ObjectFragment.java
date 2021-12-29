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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.lane.HVEEffectLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.adapter.ObjectAdapter;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ObjectFragment extends BaseFragment {
    private ImageView iv_certain;

    private RecyclerView rv;

    private HuaweiVideoEditor mEditor;

    private HVETimeLine mTimeLine;

    private ObjectAdapter mAdapter;

    private List<HVEVideoLane> allVideoLane;

    private List<HVEAsset> bitmapList;

    private CardView cardView;

    private View cardSelectView;

    private static final int SELECTALL = -1;

    private HVEEffect selectedEffect;

    private ObjectHandler objectHandler = new ObjectHandler(this);

    private static class ObjectHandler extends Handler {
        private WeakReference<ObjectFragment> weakReference;

        ObjectHandler(ObjectFragment objectFragment) {
            weakReference = new WeakReference<>(objectFragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (weakReference == null) {
                return;
            }
            ObjectFragment objectFragment = weakReference.get();
            if (objectFragment == null) {
                return;
            }
            if (objectFragment.mAdapter == null) {
                return;
            }
            int index = (int) msg.obj;
            objectFragment.mAdapter.notifyItemChanged(index);
        }
    }

    @Override
    protected void initViewModelObserve() {
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_object;
    }

    @Override
    protected void initView(View view) {
        TextView mTitleTv = view.findViewById(R.id.tv_title);
        mTitleTv.setText(R.string.object);
        CheckBox checkBox = view.findViewById(R.id.cb_apply);
        checkBox.setVisibility(View.GONE);
        iv_certain = view.findViewById(R.id.iv_certain);
        rv = view.findViewById(R.id.rv);
        cardView = view.findViewById(R.id.card_all);
        cardSelectView = view.findViewById(R.id.card_select_view);
        rv.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
        allVideoLane = viewModel.getEditor().getTimeLine().getAllVideoLane();
        selectedEffect = viewModel.getSelectedEffect();
    }

    @Override
    protected void initObject() {
        mEditor = viewModel.getEditor();
        if (mEditor == null) {
            return;
        }
        mTimeLine = mEditor.getTimeLine();
    }

    @Override
    protected void initData() {
        bitmapList = new ArrayList<>();
        mAdapter = new ObjectAdapter(bitmapList, mActivity);
        rv.setAdapter(mAdapter);
        for (int i = 0; i < allVideoLane.size(); i++) {
            HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
            if (editor == null) {
                continue;
            }
            HVETimeLine timeLine = editor.getTimeLine();
            if (timeLine == null) {
                continue;
            }
            long timeStamp = timeLine.getCurrentTime();

            if (allVideoLane.get(i).getAssets().size() <= 0) {
                continue;
            }

            if (allVideoLane.get(i).getAssets().size() > 0) {
                HVEAsset hveAsset = allVideoLane.get(i).getAssets().get(0);
                bitmapList.add(hveAsset);
            }
        }
        mAdapter = new ObjectAdapter(bitmapList, mActivity);
        rv.setAdapter(mAdapter);

        if (selectedEffect != null) {
            cardSelectView.setVisibility(selectedEffect.isGlobalAffect() ? View.VISIBLE : View.INVISIBLE);
            for (int i = 0; i < bitmapList.size(); i++) {
                if (bitmapList.get(i).getLaneIndex() == selectedEffect.getAffectIndex()) {
                    mAdapter.setIsSelect(i);
                    break;
                }
            }
        }
    }

    private boolean changeAffectLan(HVEEffect selectedEffect, int index) {
        if (mEditor == null || mTimeLine == null) {
            return false;
        }
        boolean isSuccess;
        if (index == SELECTALL) {
            HVEEffectLane effectLane = mTimeLine.getEffectLane(selectedEffect.getLaneIndex());
            isSuccess = effectLane.setAffectGlobal(selectedEffect.getIndex());
        } else {
            HVEEffectLane effectLane = mTimeLine.getEffectLane(selectedEffect.getLaneIndex());
            isSuccess = effectLane.setAffectLane(selectedEffect.getIndex(), index);
        }
        return isSuccess;
    }

    @Override
    protected void initEvent() {
        iv_certain.setOnClickListener(new OnClickRepeatedListener(v -> mActivity.onBackPressed()));

        mAdapter.setSelectedListener(position -> {
            if (selectedEffect != null) {
                int index = bitmapList.get(position).getLaneIndex();
                if (changeAffectLan(selectedEffect, index)) {
                    mAdapter.setIsSelect(position);
                    cardSelectView.setVisibility(View.INVISIBLE);
                }
            }
        });

        cardView.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (selectedEffect != null) {
                if (changeAffectLan(selectedEffect, SELECTALL)) {
                    mAdapter.setIsSelect(SELECTALL);
                    cardSelectView.setVisibility(View.VISIBLE);
                }
            }
        }));
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        objectHandler.removeCallbacksAndMessages(null);
        objectHandler = null;
    }
}
