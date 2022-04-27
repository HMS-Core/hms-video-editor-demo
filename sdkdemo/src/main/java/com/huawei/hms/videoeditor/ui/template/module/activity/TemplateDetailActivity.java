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

package com.huawei.hms.videoeditor.ui.template.module.activity;

import static com.huawei.hms.videoeditor.ui.template.module.VideoModulePagerFragment.TEMPLATES_CATEGORY_ID;
import static com.huawei.hms.videoeditor.ui.template.module.VideoModulePagerFragment.TEMPLATES_COLOUNM;
import static com.huawei.hms.videoeditor.ui.template.module.VideoModulePagerFragment.TEMPLATES_LIST;
import static com.huawei.hms.videoeditor.ui.template.module.VideoModulePagerFragment.TEMPLATES_PAGING;
import static com.huawei.hms.videoeditor.ui.template.module.VideoModulePagerFragment.TEMPLATES_POSITION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.icu.text.CompactDecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huawei.hms.videoeditor.common.network.http.ability.util.network.NetworkStartup;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.template.HVETemplateInfo;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SPManager;
import com.huawei.hms.videoeditor.ui.common.utils.TimeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.FilterLinearLayoutManager;
import com.huawei.hms.videoeditor.ui.template.adapter.RViewHolder;
import com.huawei.hms.videoeditor.ui.template.adapter.TemplateDetailAdapter;
import com.huawei.hms.videoeditor.ui.template.bean.Constant;
import com.huawei.hms.videoeditor.ui.template.view.TemplateProgressButton;
import com.huawei.hms.videoeditor.ui.template.view.exoplayer.PageListPlayDetector;
import com.huawei.hms.videoeditor.ui.template.view.exoplayer.PageListPlayManager;
import com.huawei.hms.videoeditor.ui.template.viewmodel.HVETemplateCategoryModel;
import com.huawei.hms.videoeditor.ui.template.viewmodel.HVETemplateDetailModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TemplateDetailActivity extends BaseActivity implements NetworkStartup.INetworkChangeListener {
    public static final String TAG = "TemplateDetailActivity";

    public static final String TEMPLATE_TYPE = "TemplateType";

    public static final String TEXTURE_VIEW_WIDTH = "TextureViewWidth";

    public static final String TEXTURE_VIEW_HEIGHT = "TextureViewHeight";

    public static final String TEMPLATE_ID = "templateId";

    public static final String NAME = "name";

    public static final String DESCRIPTION = "description";

    private static final int GOTO_TEMPLATE_EDIT_REQUEST_CODE = 1002;

    public static final int MIN_PROGRESS = 30;

    private static final int MAX_PROGRESS = 100;

    private ImageView mCloseIv;

    private RecyclerView mRecyclerView;

    private TextView mTitleTv;

    private TemplateProgressButton progressButton;

    private List<HVETemplateInfo> templateInfos;

    private int mPosition = 0;

    private int mPagingPosition = 0;

    private String categoryId;

    private HVETemplateCategoryModel mHVETemplateModel;

    private HVETemplateDetailModel mHVETemplateDetailModel;

    private FilterLinearLayoutManager mLayoutManager;

    private PagerSnapHelper mPagerSnapHelper;

    private String mJsonValue;

    private String mTextureViewWidth;

    private String mTextureViewHeight;

    private PageListPlayDetector playDetector;

    private HashMap<String, Integer> templateDownloadProgress;

    private static final String FILE_NAME = "TemplateDetailNetWork";

    private static final SPManager SP = SPManager.get(FILE_NAME);

    private String mColumnName;

    private String mDuration;

    private String mTempNum;

    private TemplateDetailAdapter mTemplateDetailAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.black;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_detail_t);
        initActivity();
    }

    private void initActivity() {
        initView();
        initObject();

        initData();
        initEvent();
    }

    private void initView() {
        mCloseIv = findViewById(R.id.iv_back);
        mRecyclerView = findViewById(R.id.recycler_view);
        mTitleTv = findViewById(R.id.tv_title);
        progressButton = findViewById(R.id.tv_use_module);
    }

    private void initObject() {
        mHVETemplateModel = new ViewModelProvider(this, mFactory).get(HVETemplateCategoryModel.class);
        mHVETemplateDetailModel = new ViewModelProvider(this, mFactory).get(HVETemplateDetailModel.class);
        templateInfos = new ArrayList<>();
        SafeIntent safeIntent = new SafeIntent(getIntent());
        mPosition = safeIntent.getIntExtra(TEMPLATES_POSITION, 0);
        mPagingPosition = safeIntent.getIntExtra(TEMPLATES_PAGING, 0);
        categoryId = safeIntent.getStringExtra(TEMPLATES_CATEGORY_ID);
        mColumnName = safeIntent.getStringExtra(TEMPLATES_COLOUNM);

        if (TextUtils.isEmpty(mColumnName)) {
            mColumnName = "local";
        }

        ArrayList<String> list = safeIntent.getStringArrayListExtra(TEMPLATES_LIST);

        for (int i = 0; i < list.size(); i++) {
            String templateJson = list.get(i);
            HVETemplateInfo templateInfo = new Gson().fromJson(templateJson, HVETemplateInfo.class);
            if (templateInfo != null) {
                templateInfos.add(templateInfo);
            }
        }

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = mRecyclerView.getWidth();
                int height = mRecyclerView.getHeight();
                initRecyclerView(width, height);
            }
        });
    }

    private void initRecyclerView(int width, int height) {
        if (mColumnName == null) {
            return;
        }
        mTemplateDetailAdapter = new TemplateDetailAdapter(this, width, height, categoryId, templateInfos,
            R.layout.adapter_template_detail_item_t, mColumnName) {
            @Override
            public void onViewAttachedToWindow(@NonNull RViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                if (playDetector != null) {
                    playDetector.addTarget(holder.getView(R.id.list_player_view));
                }
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull RViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
                if (playDetector != null) {
                    playDetector.removeTarget(holder.getView(R.id.list_player_view));
                }
            }
        };
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new FilterLinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mTemplateDetailAdapter);
        if (mPagerSnapHelper == null) {
            mPagerSnapHelper = new PagerSnapHelper();
        }
        mPagerSnapHelper.attachToRecyclerView(mRecyclerView);
        playDetector = new PageListPlayDetector(this, mRecyclerView);

        if (mPosition != 0) {
            mRecyclerView.scrollToPosition(mPosition);
        }
    }

    private void initData() {
        resetData();

        mHVETemplateDetailModel.getTemplateResources().observe(this, templateDetail -> {
            if (mPosition >= templateInfos.size()) {
                return;
            }

            if (String.valueOf(templateDetail.getTemplateId()).equals(templateInfos.get(mPosition).getId())) {
                progressButton.setEnabled(true);
                mJsonValue = new Gson().toJson(templateDetail);
            }
        });
        mHVETemplateDetailModel.getTemplateResourcesProgress().observe(this, progress -> {
            if (mPosition >= templateInfos.size()) {
                return;
            }

            progressButton.setStop(false);
            int pro;
            if (progress.get(templateInfos.get(mPosition).getId()) != null) {
                pro = (int) progress.get(templateInfos.get(mPosition).getId());
            } else {
                pro = MIN_PROGRESS;
            }
            SmartLog.e(TAG, pro + ": " + mPosition + ": " + templateInfos.get(mPosition).getId());
            int finalPro = pro;
            runOnUiThread(() -> {
                progressButton.setProgress(finalPro);
            });

            if (finalPro == MAX_PROGRESS && playDetector != null) {
                playDetector.postAutoPlay();
            }
        });
    }

    private void resetData() {
        if (mPosition >= templateInfos.size() || mPosition < 0) {
            return;
        }

        HVETemplateInfo templateCutContent = templateInfos.get(mPosition);
        if (templateDownloadProgress == null) {
            templateDownloadProgress = new HashMap<>();
        }
        templateDownloadProgress.put(templateCutContent.getId(), 0);
        mTitleTv.setText(templateCutContent.getName());
        if (!TextUtils.isEmpty(templateCutContent.getDescription())) {
            mTitleTv.setText(templateCutContent.getName() + " | " + templateCutContent.getDescription());
        }
        SmartLog.d(TAG, "templateCutContent.getDuration() value is : " + templateCutContent.getDuration());
        mDuration = TimeUtils.makeTimeString(this, templateCutContent.getDuration() * 1000);
        long downloadNum = templateCutContent.getDownloadCount();
        SmartLog.d(TAG, "duration value is : " + mDuration);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTempNum = CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
                .format(downloadNum);
        }
        SmartLog.d(TAG, "tempNum value is : " + mTempNum);

        if (!TextUtils.isEmpty(templateCutContent.getAspectRatio())) {
            String[] split = templateCutContent.getAspectRatio().split("\\*");
            if (split.length != 2) {
                SmartLog.e(TAG, "aspectRatio value Illegal");
            } else {
                mTextureViewWidth = split[0];
                mTextureViewHeight = split[1];
            }
        }
        progressButton.setEnabled(false);
        progressButton.initState();
        mHVETemplateDetailModel.initTemplateResourceLiveData(templateCutContent);
    }

    private void initEvent() {
        progressButton.setOnStateListener(new TemplateProgressButton.OnStateListener() {
            @Override
            public void onFinish() {
                Log.i(TAG, "onFinish");
                progressButton.setText(getString(R.string.use_module));
            }

            @Override
            public void onStop() {
                Log.i(TAG, "stop");
            }

            @Override
            public void onContinue() {
                Log.i(TAG, "continue");
            }
        });

        mCloseIv.setOnClickListener(new OnClickRepeatedListener(v -> finish()));

        progressButton.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mPosition >= templateInfos.size()) {
                return;
            }
            HVETemplateInfo templateInfo = templateInfos.get(mPosition);
            Intent intent = new Intent(this, TemplateEditActivity.class);
            intent.putExtra(Constant.TEMPLATE_KEY_INFO, new Gson().toJson(templateInfo));
            intent.putExtra(Constant.TEMPLATE_KEY_DETAIL, mJsonValue);
            String source = new SafeIntent(getIntent()).getStringExtra("source");
            intent.putExtra("source", source);
            if (mTextureViewWidth != null) {
                intent.putExtra(TEXTURE_VIEW_WIDTH, Float.parseFloat(mTextureViewWidth));
            }
            if (mTextureViewHeight != null) {
                intent.putExtra(TEXTURE_VIEW_HEIGHT, Float.parseFloat(mTextureViewHeight));
            }
            startActivityForResult(intent, GOTO_TEMPLATE_EDIT_REQUEST_CODE);
        }));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (manager == null) {
                    return;
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View snapView = mPagerSnapHelper.findSnapView(manager);
                    int currentPageIndex = -1;
                    if (snapView != null) {
                        currentPageIndex = manager.getPosition(snapView);
                    }
                    if (mPosition != currentPageIndex) {
                        mPosition = currentPageIndex;
                        resetData();
                    } else if (mPosition == templateInfos.size() - 1) {
                        mPagingPosition++;
                        mHVETemplateModel.initTemplateListLiveData(mPagingPosition, categoryId, true);
                    }
                }
            }
        });

        mHVETemplateModel.getHVECloudTemplateList().observe(this, hVECloudTemplateList -> {
            if (playDetector != null) {
                playDetector.onPause();
            }

            mRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int width = mRecyclerView.getWidth();
                        int height = mRecyclerView.getHeight();
                        initRecyclerView(width, height);
                    }
                });
            mPosition++;
            mRecyclerView.scrollToPosition(mPosition);
            resetData();
        });

        mHVETemplateDetailModel.getErrorString().observe(this, errorString -> {
            ToastWrapper.makeText(this, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playDetector != null) {
            playDetector.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setDestory();
    }

    public void setDestory() {
        if (playDetector != null) {
            playDetector.onPause();
            playDetector = null;
            PageListPlayManager.release(categoryId);
            NetworkStartup.removeNetworkChangeListener(this);
            if (SP != null) {
                SP.clear();
            }
        }
    }

    @Override
    public void onNetworkChange() {
        boolean isNetWork = SP.getBoolean("NetWork", false);
        if (NetworkStartup.isOnlyMobileConn() && !isNetWork) {
            SmartLog.i(TAG, "MobileConn");
            SP.put("NetWork", true);
            ToastWrapper.makeText(this, R.string.no_wifi, Toast.LENGTH_SHORT).show();
        } else {
            SmartLog.i(TAG, "Not MobileConn");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOTO_TEMPLATE_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
