
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

package com.huawei.hms.videoeditor.ui.mediaeditor.texts.fragment;

import static android.view.View.VISIBLE;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEWordAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEWordStyle;
import com.huawei.hms.videoeditor.sdk.engine.word.FontFileManager;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.adapter.SelectAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RMCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.FoldScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.LanguageUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SharedPreferencesUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.ui.common.view.decoration.GridItemDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopInfo;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopLayout;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.MySeekBar;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.adapter.EditTextFontAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.custom.CustomNestedScrollView;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel.TextEditFontViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel.TextEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel.TextPanelViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class EditTextStyleFragment extends Fragment {

    private static final String TAG = "EditTextStyleFragment";

    protected FragmentActivity mActivity;

    private TabTopLayout mTabTopLayout;

    private List<TabTopInfo<?>> mInfoList;

    private final int[] tabs =
        {R.string.edit_item2_1_4, R.string.edit_item2_1_5, R.string.edit_item2_1_6, R.string.edit_item2_1_7,
            R.string.edit_item2_1_8, R.string.edit_item2_1_9, R.string.edit_item2_1_10, R.string.edit_item2_1_11};

    private ViewPager2 viewpager;

    private RelativeLayout mErrorLayout;

    private TextView mErrorTv;

    private LoadingIndicatorView mLoadingIndicatorView;

    private Context fragmentContext;

    private TextEditViewModel textEditViewModel;

    private TextEditFontViewModel mFontViewModel;

    private EditPreviewViewModel mEditPreviewViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    private TextPanelViewModel textPanelViewModel;

    private RecyclerView mFontRecycleView;

    private List<CloudMaterialBean> mFontList;

    private EditTextFontAdapter mEditTextFontAdapter;

    private View defaultFontHeader;

    private View headerSelect;

    private View headerNormal;

    private boolean isDefaultFont = true;

    private int mCurrentPage = 0;

    private Boolean mHasNextPage = false;

    private boolean isScrolled = false;

    private int alignment = HVEWordStyle.ALIGNMENT_HORIZONTAL_LEFT;

    private static final float SEEKMAX = 100.0f;

    private static final int ALPHAMAX = 255;

    private static final String COLOR = "color";

    private static final String STROKE = "stroke";

    private static final String SHAWDOW = "shawdow";

    private static final String BACK = "back";

    private int mSelectValue = 0;

    private int mStrokeValue = 1;

    private boolean isBackground;

    private ViewPagerAdapter mViewPagerAdapter;

    private boolean isErrorVisible;

    private boolean isFirst;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        fragmentContext = getContext();
        if (fragmentContext instanceof ViewModelStoreOwner) {
            textEditViewModel =
                new ViewModelProvider((ViewModelStoreOwner) fragmentContext).get(TextEditViewModel.class);
            HVEWordStyle wordStyle = textEditViewModel.getLastWordStyle();
            if (wordStyle == null) {
                textEditViewModel.setDefWordStyle(0);
            }
            mFontViewModel =
                new ViewModelProvider((ViewModelStoreOwner) fragmentContext).get(TextEditFontViewModel.class);
            mEditPreviewViewModel =
                new ViewModelProvider((ViewModelStoreOwner) fragmentContext).get(EditPreviewViewModel.class);
            mMaterialEditViewModel =
                new ViewModelProvider((ViewModelStoreOwner) fragmentContext).get(MaterialEditViewModel.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_text_style, container, false);

        initView(rootView);
        initObject();
        initData();
        return rootView;
    }

    private void initView(View rootView) {
        mTabTopLayout = rootView.findViewById(R.id.tab_top_layout);
        if (ScreenUtil.isRTL()) {
            mTabTopLayout.setScaleX(RTL_UI);
        } else {
            mTabTopLayout.setScaleX(LTR_UI);
        }
        viewpager = rootView.findViewById(R.id.viewpager);
        mErrorLayout = rootView.findViewById(R.id.error_layout);
        mErrorTv = rootView.findViewById(R.id.error_text);
        mLoadingIndicatorView = rootView.findViewById(R.id.indicator);
    }

    @Override
    public void onResume() {
        super.onResume();
        isBackground = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isBackground = true;
    }

    private void initObject() {
        initTimeoutState();
        textPanelViewModel = new ViewModelProvider(mActivity).get(TextPanelViewModel.class);
        mInfoList = new ArrayList<>();
        initFontHead();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchListener);
        }
    }

    private void initFontHead() {
        int headerWidth = 0;
        int screenWidth = SizeUtils.screenWidth(mActivity);
        int marginWidth = SizeUtils.dp2Px(mActivity, Constant.IMAGE_WIDTH_MARGIN);
        if (FoldScreenUtil.isFoldable() && FoldScreenUtil.isFoldableScreenExpand(getContext())) {
            headerWidth = (screenWidth - marginWidth) / Constant.IMAGE_COUNT_8;
        } else {
            headerWidth = (screenWidth - marginWidth) / Constant.IMAGE_COUNT_4;
        }
        int headerHeight = headerWidth / Constant.TEXT_HALF;
        defaultFontHeader =
            LayoutInflater.from(fragmentContext).inflate(R.layout.adapter_add_text_font_header, null, false);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(headerWidth, headerHeight);
        params.bottomMargin = SizeUtils.dp2Px(mActivity, Constant.TEXT_FONT_MARGIN_BOTTOM);
        defaultFontHeader.setLayoutParams(params);
        headerSelect = defaultFontHeader.findViewById(R.id.item_select_view);
        headerNormal = defaultFontHeader.findViewById(R.id.item_normal_view);
        mViewPagerAdapter = new ViewPagerAdapter(fragmentContext);
        viewpager.setAdapter(mViewPagerAdapter);
        viewpager.setUserInputEnabled(false);
    }

    private void tabToTextFont(int index) {
        if (mViewPagerAdapter != null && mViewPagerAdapter.getItemCount() >= 0 && viewpager.getCurrentItem() != index) {
            viewpager.setCurrentItem(index, false);
            mEditPreviewViewModel.setIndexTitle(index);
        }
    }

    private void initData() {
        int defaultColor = ContextCompat.getColor(fragmentContext, R.color.white);
        int tintColor = ContextCompat.getColor(fragmentContext, R.color.tab_text_tint_color);
        int rightPadding = SizeUtils.dp2Px(fragmentContext, 24);
        for (int item : tabs) {
            TabTopInfo<?> info = new TabTopInfo<>(mActivity.getResources().getString(item), false, defaultColor,
                tintColor, 14, 14, 0, rightPadding);
            mInfoList.add(info);
        }
        mTabTopLayout.inflateInfo(mInfoList);
        mTabTopLayout.defaultSelected(mInfoList.get(0));
        mTabTopLayout.addTabSelectedChangeListener((index, prevInfo, nextInfo) -> {
            if (index == 1 && isErrorVisible) {
                mErrorLayout.setVisibility(View.VISIBLE);
            } else {
                mErrorLayout.setVisibility(View.GONE);
            }
            tabToTextFont(index);
        });

        mEditPreviewViewModel.getDefaultFontContent()
            .observe(getViewLifecycleOwner(), new Observer<CloudMaterialBean>() {
                @Override
                public void onChanged(CloudMaterialBean materialsCutContent) {
                    if (materialsCutContent != null && !StringUtil.isEmpty(materialsCutContent.getLocalPath())) {
                        FontFileManager.setDefaultFontFile(materialsCutContent.getLocalPath());
                        isDefaultFont = true;
                        headerSelect.setVisibility(View.VISIBLE);
                        headerNormal.setVisibility(View.INVISIBLE);
                        textEditViewModel.setFontPath(materialsCutContent.getLocalPath(), materialsCutContent.getId());
                    }
                }
            });

        mEditPreviewViewModel.getTimeout()
            .observe(getViewLifecycleOwner(), isTimeout -> {
                if (isTimeout && !isBackground) {
                    mActivity.onBackPressed();
                }
            });
    }

    private class ViewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mCtx;

        private ViewPagerAdapter(Context context) {
            this.mCtx = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = null;
            RecyclerView.ViewHolder viewHolder = null;
            switch (viewType) {
                case 0:
                    itemView = LayoutInflater.from(mCtx).inflate(R.layout.itemview_text_style, parent, false);
                    viewHolder = new TextStyleHolder(itemView);
                    break;
                case 1:
                    itemView = LayoutInflater.from(mCtx).inflate(R.layout.itemview_font_style, parent, false);
                    viewHolder = new FontStyleHolder(itemView);
                    break;
                case 2:
                    itemView = LayoutInflater.from(mCtx).inflate(R.layout.itemview_text_color, parent, false);
                    viewHolder = new ColorHolder(itemView);
                    break;
                case 3:
                    itemView = LayoutInflater.from(mCtx).inflate(R.layout.itemview_text_stroke_color, parent, false);
                    viewHolder = new StrokesColorHolder(itemView);
                    break;
                case 4:
                    itemView = LayoutInflater.from(mCtx).inflate(R.layout.itemview_text_shadow_color, parent, false);
                    viewHolder = new ShadowColorHolder(itemView);
                    break;
                case 5:
                    itemView = LayoutInflater.from(mCtx).inflate(R.layout.itemview_text_label, parent, false);
                    viewHolder = new LabelHolder(itemView);
                    break;
                case 6:
                    itemView = LayoutInflater.from(mCtx).inflate(R.layout.itemview_text_set_type, parent, false);
                    viewHolder = new SetTypeHolder(itemView);
                    break;
                case 7:
                    itemView = LayoutInflater.from(mCtx).inflate(R.layout.itemview_text_bold_underline, parent, false);
                    viewHolder = new BoldItalicsHolder(itemView);
                    break;
                default:
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return 8;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class TextStyleHolder extends RecyclerView.ViewHolder {

            private View mSeekBarLayout;

            private EditorTextView nameLayoutTransSeekBar;

            private MySeekBar mSeekBarTrans;

            private RecyclerView mRecyclerView;

            private int mInitProgress;

            TextStyleHolder(@NonNull View itemView) {
                super(itemView);
                nameLayoutTransSeekBar = itemView.findViewById(R.id.name_layout_trans_seekbar);
                mSeekBarLayout = itemView.findViewById(R.id.view_seekbar_style);
                int stylePosition = textEditViewModel.getStylePosition();
                if (stylePosition != 0) {
                    mSeekBarLayout.setVisibility(View.VISIBLE);
                } else {
                    mSeekBarLayout.setVisibility(View.GONE);
                }
                mSeekBarTrans = itemView.findViewById(R.id.seekbar);
                if (ScreenUtil.isRTL()) {
                    mSeekBarTrans.setScaleX(RTL_UI);
                } else {
                    mSeekBarTrans.setScaleX(LTR_UI);
                }
                ConstraintLayout.LayoutParams transParams;
                if (LanguageUtils.isZh()) {
                    transParams = new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 48.0f),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);

                } else {
                    transParams = new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 64.0f),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
                }
                transParams.setMargins(0, 0, 0, SizeUtils.dp2Px(mCtx, 3.0f));
                transParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                transParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                nameLayoutTransSeekBar.setLayoutParams(transParams);
                mInitProgress = (int) (textEditViewModel.getFontDefaultAlpha() * SEEKMAX / ALPHAMAX);
                mSeekBarTrans.setProgress(mInitProgress);
                SmartLog.d(TAG, "Initialize progress of font style  [mInitProgress] ==" + mInitProgress);
                mRecyclerView = itemView.findViewById(R.id.recycleview);
                Context context = getContext();
                if (context == null) {
                    return;
                }
                StyleAdapter adapter = new StyleAdapter(context, textEditViewModel.getNormalImageList());
                adapter.setPosition(stylePosition);
                int lineCount = 4;
                if (FoldScreenUtil.isFoldable() && FoldScreenUtil.isFoldableScreenExpand(context)) {
                    lineCount = 7;
                }
                mRecyclerView.setLayoutManager(new GridLayoutManager(mCtx, lineCount));
                if (mRecyclerView.getItemDecorationCount() == 0) {
                    mRecyclerView.addItemDecoration(new GridItemDividerDecoration(SizeUtils.dp2Px(mCtx, 8f),
                        SizeUtils.dp2Px(mCtx, 8f), ContextCompat.getColor(mCtx, R.color.transparent)));
                }
                mRecyclerView.setAdapter(adapter);

                mSeekBarTrans.setOnProgressChangedListener(new MySeekBar.OnProgressChangedListener() {
                    @Override
                    public void onProgressChanged(int progress) {
                        textEditViewModel.setFontDefaultAlpha((int) (progress * ALPHAMAX / SEEKMAX));
                        textEditViewModel.setTextTrans((int) (progress * ALPHAMAX / SEEKMAX));
                        mEditPreviewViewModel.setToastTime((int) mSeekBarTrans.getProgress() + "");
                    }
                });
                adapter.setOnItemClickListener(new SelectAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Object k, int position) {
                        textEditViewModel.setStylePosition(position);
                        if (position == 0) {
                            mSeekBarLayout.setVisibility(View.GONE);
                        } else {
                            mSeekBarLayout.setVisibility(View.VISIBLE);
                        }
                        textEditViewModel.setDefWordStyle(position);
                    }
                });
                mSeekBarTrans.setcTouchListener(isTouch -> mEditPreviewViewModel
                    .setToastTime(isTouch ? (int) mSeekBarTrans.getProgress() + "" : ""));
            }
        }

        public class FontStyleHolder extends RecyclerView.ViewHolder {

            FontStyleHolder(@NonNull View itemView) {
                super(itemView);
                mFontRecycleView = itemView.findViewById(R.id.recycleview);
                int lineCount = 4;
                Context context = getContext();
                if (context == null) {
                    return;
                }
                if (FoldScreenUtil.isFoldable() && FoldScreenUtil.isFoldableScreenExpand(context)) {
                    lineCount = 7;
                }
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(fragmentContext, lineCount);
                mFontList = new ArrayList<>();
                mEditTextFontAdapter =
                    new EditTextFontAdapter(fragmentContext, mFontList, R.layout.adapter_add_text_font_item);
                mEditTextFontAdapter.addHeaderView(defaultFontHeader);
                mFontRecycleView.setItemAnimator(null);
                mFontRecycleView.setAdapter(mEditTextFontAdapter);
                mFontRecycleView.setLayoutManager(layoutManager);

                int fontPosition = textEditViewModel.getTextFontPosition();
                if (fontPosition != -1) {
                    isDefaultFont = false;
                    headerSelect.setVisibility(View.INVISIBLE);
                    headerNormal.setVisibility(View.VISIBLE);
                    mEditTextFontAdapter.setSelectPosition(fontPosition);
                } else {
                    isDefaultFont = true;
                    headerSelect.setVisibility(View.VISIBLE);
                    headerNormal.setVisibility(View.INVISIBLE);
                }
                initTextFont();
            }
        }

        public class ColorHolder extends RecyclerView.ViewHolder {
            private MySeekBar seekbar;

            private RecyclerView recycleview;

            ColorHolder(@NonNull View itemView) {
                super(itemView);
                seekbar = itemView.findViewById(R.id.seekbar);
                int colorTransValue = textEditViewModel.getColorTransValue();
                seekbar.setProgress(colorTransValue);
                if (ScreenUtil.isRTL()) {
                    seekbar.setScaleX(RTL_UI);
                } else {
                    seekbar.setScaleX(LTR_UI);
                }
                EditorTextView textLayoutTransSeekBar = itemView.findViewById(R.id.text_layout_trans_seek_bar);
                ConstraintLayout.LayoutParams transParams;
                if (LanguageUtils.isZh()) {
                    transParams = new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 48.0f),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);

                } else {
                    transParams = new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 64.0f),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
                }
                transParams.setMargins(SizeUtils.dp2Px(mCtx, 10.0f), 0, 0, SizeUtils.dp2Px(mCtx, 3.0f));
                transParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                transParams.bottomToBottom = R.id.seekbar;
                transParams.endToStart = R.id.seekbar;
                textLayoutTransSeekBar.setLayoutParams(transParams);
                recycleview = itemView.findViewById(R.id.recycleview);
                mSelectValue = SharedPreferencesUtils.getInstance()
                    .getIntValue(mActivity, SharedPreferencesUtils.TEXT_COLOR_INDEX);
                LabelAdapter mLabelAdapterColor =
                    new LabelAdapter(fragmentContext, textEditViewModel.getColorList(), R.layout.item_color_view,
                        mEditPreviewViewModel, COLOR, mSelectValue, mStrokeValue, textEditViewModel);
                recycleview.setLayoutManager(new LinearLayoutManager(fragmentContext, RecyclerView.HORIZONTAL, false));
                recycleview.setAdapter(mLabelAdapterColor);
                int colorPositionValue = textEditViewModel.getColorPositionValue();
                mLabelAdapterColor.setSelectPosition(colorPositionValue);
                textEditViewModel.setTextColor(textEditViewModel.getStrokeDefaultAlpha(),
                    textEditViewModel.getColorList().get(colorPositionValue));
                textEditViewModel.setFontDefaultAlpha((int) (colorTransValue * ALPHAMAX / SEEKMAX));
                textEditViewModel.setColorTrans((int) (colorTransValue * ALPHAMAX / SEEKMAX));
                mLabelAdapterColor.setOnItemClickListener(new RMCommandAdapter.OnItemClickListener() {
                    @Override
                    public void onItemTouch(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                        textEditViewModel.setColorPositionValue(position);
                        List<Integer> colorList = textEditViewModel.getColorList();
                        if (dataPosition < colorList.size()) {
                            textEditViewModel.setTextColor(
                                (int) (textEditViewModel.getColorTransValue() * ALPHAMAX / SEEKMAX),
                                colorList.get(position));
                        }
                        mEditPreviewViewModel.setHeadClick(false);
                    }

                    @Override
                    public void onItemClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                    }

                    @Override
                    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int dataPosition,
                        int position) {
                        return false;
                    }
                });
                seekbar.setOnProgressChangedListener(progress -> {
                    textEditViewModel.setColorTransValue(progress);
                    textEditViewModel.setFontDefaultAlpha((int) (progress * ALPHAMAX / SEEKMAX));
                    textEditViewModel.setColorTrans((int) (progress * ALPHAMAX / SEEKMAX));
                    mEditPreviewViewModel.setToastTime((int) seekbar.getProgress() + "");
                });
                seekbar.setcTouchListener(
                    isTouch -> mEditPreviewViewModel.setToastTime(isTouch ? seekbar.getProgress() + "" : ""));

            }
        }

        public class StrokesColorHolder extends RecyclerView.ViewHolder {

            private RecyclerView mRecycleView;

            private MySeekBar mSeekBarThickness;

            private MySeekBar mSeekBarTrans;

            private CustomNestedScrollView customScrollView;

            private View mViewThickness;

            private View mViewTrans;

            StrokesColorHolder(@NonNull View itemView) {
                super(itemView);
                mRecycleView = itemView.findViewById(R.id.recycleview);
                mSeekBarThickness = itemView.findViewById(R.id.seekbar_thickness);
                mSeekBarTrans = itemView.findViewById(R.id.seekbar);
                if (ScreenUtil.isRTL()) {
                    mSeekBarThickness.setScaleX(RTL_UI);
                    mSeekBarTrans.setScaleX(RTL_UI);
                } else {
                    mSeekBarThickness.setScaleX(LTR_UI);
                    mSeekBarTrans.setScaleX(LTR_UI);
                }
                EditorTextView textLayoutSeekBarThickness = itemView.findViewById(R.id.text_layout_seek_bar_thickness);
                EditorTextView nameLayoutTransSeekBar = itemView.findViewById(R.id.name_layout_trans_seekbar);
                ConstraintLayout.LayoutParams transParams;
                if (LanguageUtils.isZh()) {
                    transParams = new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 36f),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);

                } else {
                    transParams = new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 64.0f),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
                }
                transParams.setMargins(0, 0, 0, SizeUtils.dp2Px(mCtx, 3.0f));
                transParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                transParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                textLayoutSeekBarThickness.setLayoutParams(transParams);
                nameLayoutTransSeekBar.setLayoutParams(transParams);

                mViewThickness = itemView.findViewById(R.id.view_thickness);
                mViewTrans = itemView.findViewById(R.id.view_trans);
                customScrollView = itemView.findViewById(R.id.custom_scrollview);
                int transValue = textEditViewModel.getStrokeTransValue();
                mSeekBarTrans.setProgress(transValue);
                int thickValue = textEditViewModel.getStrokeThicknessValue();
                mSeekBarThickness.setProgress(thickValue);
                mStrokeValue = SharedPreferencesUtils.getInstance()
                    .getIntValue2(mActivity, SharedPreferencesUtils.TEXT_STROKE_INDEX);

                View cancelHeader =
                    LayoutInflater.from(mCtx).inflate(R.layout.adapter_edittext_label_header, null, false);
                RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 40), SizeUtils.dp2Px(mCtx, 40));
                layoutParams.setMarginEnd(SizeUtils.dp2Px(mCtx, 8));
                View mViewBg = cancelHeader.findViewById(R.id.bg_view_head);
                ConstraintLayout mHeadLayout = cancelHeader.findViewById(R.id.layout_head);
                mEditPreviewViewModel.setHeadClick(true);
                cancelHeader.setLayoutParams(layoutParams);
                LabelAdapter labelAdapter =
                    new LabelAdapter(fragmentContext, textEditViewModel.getColorList(), R.layout.item_color_view,
                        mEditPreviewViewModel, STROKE, mSelectValue, mStrokeValue, mViewBg, textEditViewModel);
                mRecycleView.setLayoutManager(new LinearLayoutManager(fragmentContext, RecyclerView.HORIZONTAL, false));
                mRecycleView.setAdapter(labelAdapter);
                labelAdapter.addHeaderView(cancelHeader);

                int strokeColorPositionValue = textEditViewModel.getStrokeColorPositionValue();
                if (strokeColorPositionValue != -1) {
                    mEditPreviewViewModel.setHeadClick(false);
                    mViewBg.setVisibility(View.GONE);
                    labelAdapter.setSelectPosition(strokeColorPositionValue);
                    mViewThickness.setVisibility(View.VISIBLE);
                    mViewTrans.setVisibility(View.VISIBLE);
                } else {
                    mViewBg.setVisibility(VISIBLE);
                    textEditViewModel.setStrokeSize(40);
                    mEditPreviewViewModel.setHeadClick(true);
                    mViewThickness.setVisibility(View.INVISIBLE);
                    mViewTrans.setVisibility(View.INVISIBLE);
                }

                mHeadLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mViewThickness.setVisibility(View.INVISIBLE);
                        mViewTrans.setVisibility(View.INVISIBLE);
                        mViewBg.setVisibility(View.VISIBLE);
                        mEditPreviewViewModel.setHeadClick(true);
                        textEditViewModel.setStrokeColor(textEditViewModel.getStrokeDefaultAlpha(), Color.TRANSPARENT);
                        return false;
                    }
                });

                labelAdapter.setOnItemClickListener(new RMCommandAdapter.OnItemClickListener() {
                    @Override
                    public void onItemTouch(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                        textEditViewModel.setStrokeColorPositionValue(position);
                        mViewThickness.setVisibility(View.VISIBLE);
                        mViewTrans.setVisibility(View.VISIBLE);
                        mViewBg.setVisibility(View.GONE);
                        List<Integer> colorList = textEditViewModel.getColorList();
                        if (position < colorList.size() - 1) {
                            textEditViewModel.setStrokeColor(colorList.get(position - 1));
                        }
                        mEditPreviewViewModel.setHeadClick(false);
                        mViewPagerAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onItemClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                    }

                    @Override
                    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int dataPosition,
                        int position) {
                        return false;
                    }
                });
                mSeekBarThickness.setOnProgressChangedListener(new MySeekBar.OnProgressChangedListener() {
                    @Override
                    public void onProgressChanged(int progress) {
                        textEditViewModel.setStrokeThicknessValue(progress);
                        textEditViewModel.setStrokeSize(progress);
                        mEditPreviewViewModel.setToastTime((int) mSeekBarThickness.getProgress() + "");
                        customScrollView.setScrollEnabled(false);
                    }
                });
                mSeekBarTrans.setOnProgressChangedListener(new MySeekBar.OnProgressChangedListener() {
                    @Override
                    public void onProgressChanged(int progress) {
                        textEditViewModel.setStrokeTransValue(progress);
                        textEditViewModel.setStrokeDefaultAlpha((int) (progress * ALPHAMAX / SEEKMAX));
                        textEditViewModel.setStrokeTrans((int) (progress * ALPHAMAX / SEEKMAX));
                        mEditPreviewViewModel.setToastTime((int) mSeekBarTrans.getProgress() + "");
                        customScrollView.setScrollEnabled(false);
                    }
                });

                mSeekBarThickness.setcTouchListener(isTouch -> {
                    mEditPreviewViewModel.setToastTime(isTouch ? mSeekBarThickness.getProgress() + "" : "");
                    customScrollView.setScrollEnabled(true);
                });
                mSeekBarTrans.setcTouchListener(isTouch -> {
                    mEditPreviewViewModel.setToastTime(isTouch ? mSeekBarTrans.getProgress() + "" : "");
                    customScrollView.setScrollEnabled(true);
                });
            }
        }

        public class ShadowColorHolder extends RecyclerView.ViewHolder {
            private RecyclerView recycleview;

            private MySeekBar seekTrans;

            private MySeekBar seekBlur;

            private MySeekBar seekDis;

            private MySeekBar seekAngle;

            private View mViewBlur;

            private View mViewTrans;

            private View mViewDis;

            private View mViewAngle;

            private CustomNestedScrollView customScrollView;

            ShadowColorHolder(@NonNull View itemView) {
                super(itemView);
                recycleview = itemView.findViewById(R.id.recycleview);
                seekTrans = itemView.findViewById(R.id.seekbar);
                seekBlur = itemView.findViewById(R.id.seekbar_blur);
                seekAngle = itemView.findViewById(R.id.seekbar_angle);
                seekDis = itemView.findViewById(R.id.seekbar_dis);
                EditorTextView nameLayoutTransSeekBar = itemView.findViewById(R.id.name_layout_trans_seekbar);
                EditorTextView textLayoutSeekBarBlur = itemView.findViewById(R.id.text_layout_seek_bar_blur);
                EditorTextView textLayoutSeekBarDistance = itemView.findViewById(R.id.text_layout_seek_bar_distance);
                EditorTextView textLayoutSeekBarAngle = itemView.findViewById(R.id.text_layout_seek_bar_angle);

                ConstraintLayout.LayoutParams transParams;
                if (LanguageUtils.isZh()) {
                    transParams = new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 48.0f),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);

                } else {
                    transParams = new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 64.0f),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
                }
                transParams.setMargins(0, 0, 0, SizeUtils.dp2Px(mCtx, 3.0f));
                transParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                transParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                nameLayoutTransSeekBar.setLayoutParams(transParams);
                textLayoutSeekBarBlur.setLayoutParams(transParams);
                textLayoutSeekBarDistance.setLayoutParams(transParams);
                textLayoutSeekBarAngle.setLayoutParams(transParams);

                if (ScreenUtil.isRTL()) {
                    seekTrans.setScaleX(RTL_UI);
                    seekBlur.setScaleX(RTL_UI);
                    seekAngle.setScaleX(RTL_UI);
                    seekDis.setScaleX(RTL_UI);
                } else {
                    seekTrans.setScaleX(LTR_UI);
                    seekBlur.setScaleX(LTR_UI);
                    seekAngle.setScaleX(LTR_UI);
                    seekDis.setScaleX(LTR_UI);
                }
                customScrollView = itemView.findViewById(R.id.custom_scrollview);
                mViewBlur = itemView.findViewById(R.id.view_shadow_blur);
                mViewAngle = itemView.findViewById(R.id.view_shadow_angle);
                mViewDis = itemView.findViewById(R.id.view_shadow_dis);
                mViewTrans = itemView.findViewById(R.id.view_shadow_trans);
                mStrokeValue = SharedPreferencesUtils.getInstance()
                    .getIntValue2(mActivity, SharedPreferencesUtils.TEXT_SHAWDOW_INDEX);
                View cancelHeader =
                    LayoutInflater.from(mCtx).inflate(R.layout.adapter_edittext_label_header, null, false);
                RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 40), SizeUtils.dp2Px(mCtx, 40));
                layoutParams.setMarginEnd(SizeUtils.dp2Px(mCtx, 8));
                View mViewBg = cancelHeader.findViewById(R.id.bg_view_head);
                ConstraintLayout mHeadLayout = cancelHeader.findViewById(R.id.layout_head);
                mViewBg.setVisibility(VISIBLE);

                setSeekBarValue();

                mEditPreviewViewModel.setHeadClick(true);
                cancelHeader.setLayoutParams(layoutParams);
                LabelAdapter labelAdapter =
                    new LabelAdapter(fragmentContext, textEditViewModel.getColorList(), R.layout.item_color_view,
                        mEditPreviewViewModel, SHAWDOW, mSelectValue, mStrokeValue, mViewBg, textEditViewModel);
                recycleview.setLayoutManager(new LinearLayoutManager(fragmentContext, RecyclerView.HORIZONTAL, false));
                recycleview.setAdapter(labelAdapter);
                labelAdapter.addHeaderView(cancelHeader);

                int shadowColorPositionValue = textEditViewModel.getShadowColorPositionValue();
                if (shadowColorPositionValue != -1) {
                    mEditPreviewViewModel.setHeadClick(false);
                    mViewBg.setVisibility(View.GONE);
                    labelAdapter.setSelectPosition(shadowColorPositionValue);
                    mViewBlur.setVisibility(VISIBLE);
                    mViewTrans.setVisibility(VISIBLE);
                    mViewDis.setVisibility(VISIBLE);
                    mViewAngle.setVisibility(VISIBLE);
                } else {
                    mViewBg.setVisibility(VISIBLE);
                    mEditPreviewViewModel.setHeadClick(true);
                    mViewBlur.setVisibility(View.GONE);
                    mViewTrans.setVisibility(View.GONE);
                    mViewDis.setVisibility(View.GONE);
                    mViewAngle.setVisibility(View.GONE);
                }

                setTransfer(80);
                setBlur(40);
                setDis(0);
                setSeekAngle(0);
                mHeadLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mViewBlur.setVisibility(View.GONE);
                        mViewTrans.setVisibility(View.GONE);
                        mViewDis.setVisibility(View.GONE);
                        mViewAngle.setVisibility(View.GONE);
                        mViewBg.setVisibility(View.VISIBLE);
                        textEditViewModel.setShadowColorPositionValue(-1);
                        mEditPreviewViewModel.setHeadClick(true);
                        textEditViewModel.setShadowColor(textEditViewModel.getStrokeDefaultAlpha(), Color.TRANSPARENT);
                        setTransfer(100);
                        setBlur(0);
                        setDis(0);
                        setSeekAngle(0);
                        return false;
                    }
                });

                labelAdapter.setOnItemClickListener(new RMCommandAdapter.OnItemClickListener() {
                    @Override
                    public void onItemTouch(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                        textEditViewModel.setShadowColorPositionValue(position);
                        mViewBlur.setVisibility(View.VISIBLE);
                        mViewTrans.setVisibility(View.VISIBLE);
                        mViewDis.setVisibility(View.VISIBLE);
                        mViewAngle.setVisibility(View.VISIBLE);
                        mEditPreviewViewModel.setHeadClick(false);
                        mViewBg.setVisibility(View.GONE);
                        List<Integer> colorList = textEditViewModel.getColorList();
                        if (dataPosition < colorList.size()) {
                            textEditViewModel.setShadowColor(textEditViewModel.getStrokeDefaultAlpha(),
                                colorList.get(position - 1));
                        }
                    }

                    @Override
                    public void onItemClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                    }

                    @Override
                    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int dataPosition,
                        int position) {
                        return false;
                    }
                });
                seekAngle.setOnProgressChangedListener(new MySeekBar.OnProgressChangedListener() {
                    @Override
                    public void onProgressChanged(int progress) {
                        setSeekAngle(progress);
                        textEditViewModel.setShadowAngleValue(progress);
                        mEditPreviewViewModel.setToastTime((int) seekAngle.getProgress() + "");
                        customScrollView.setScrollEnabled(false);
                    }
                });
                seekTrans.setOnProgressChangedListener(new MySeekBar.OnProgressChangedListener() {
                    @Override
                    public void onProgressChanged(int progress) {
                        textEditViewModel.setShadowTransValue(progress);
                        setTransfer(progress);
                        mEditPreviewViewModel.setToastTime((int) seekTrans.getProgress() + "");
                        customScrollView.setScrollEnabled(false);
                    }
                });

                seekDis.setOnProgressChangedListener(new MySeekBar.OnProgressChangedListener() {
                    @Override
                    public void onProgressChanged(int progress) {
                        textEditViewModel.setShadowDisValue(progress);
                        setDis(progress);
                        mEditPreviewViewModel.setToastTime((int) seekDis.getProgress() + "");
                        customScrollView.setScrollEnabled(false);
                    }
                });
                seekBlur.setOnProgressChangedListener(new MySeekBar.OnProgressChangedListener() {
                    @Override
                    public void onProgressChanged(int progress) {
                        textEditViewModel.setShadowBlurValue(progress);
                        setBlur(progress);
                        mEditPreviewViewModel.setToastTime(seekBlur.getProgress() + "");
                        customScrollView.setScrollEnabled(false);
                    }
                });
                seekTrans.setcTouchListener(isTouch -> {
                    mEditPreviewViewModel.setToastTime(isTouch ? seekTrans.getProgress() + "" : "");
                    customScrollView.setScrollEnabled(true);
                });
                seekBlur.setcTouchListener(isTouch -> {
                    mEditPreviewViewModel.setToastTime(isTouch ? seekBlur.getProgress() + "" : "");
                    customScrollView.setScrollEnabled(true);
                });
                seekAngle.setcTouchListener(isTouch -> {
                    mEditPreviewViewModel.setToastTime(isTouch ? seekAngle.getProgress() + "" : "");
                    customScrollView.setScrollEnabled(true);
                });
                seekDis.setcTouchListener(isTouch -> {
                    mEditPreviewViewModel.setToastTime(isTouch ? seekDis.getProgress() + "" : "");
                    customScrollView.setScrollEnabled(true);
                });
            }

            private void setSeekBarValue() {
                int transValue = textEditViewModel.getShadowTransValue();
                int blueValue = textEditViewModel.getShadowBlurValue();
                int disValue = textEditViewModel.getShadowDisValue();
                int angleValue = textEditViewModel.getShadowAngleValue();
                seekTrans.setProgress(transValue);
                seekBlur.setProgress(blueValue);
                seekAngle.setProgress(angleValue);
                seekDis.setProgress(disValue);
            }

            private void setSeekAngle(double progress) {
                HVEAsset hveAsset = mEditPreviewViewModel.getSelectedAsset();
                if (hveAsset instanceof HVEWordAsset) {
                    HVEWordStyle style = ((HVEWordAsset) hveAsset).getWordStyle()
                        .setShadowAngle((float) BigDecimalUtils.mul2(progress, 3.6f, 1));
                    textEditViewModel.setWordStyle(style);

                    HuaweiVideoEditor editor = mEditPreviewViewModel.getEditor();
                    if (mEditPreviewViewModel.getEditor() == null) {
                        return;
                    }

                    editor.seekTimeLine(mEditPreviewViewModel.getSeekTime(), new HuaweiVideoEditor.SeekCallback() {
                        @Override
                        public void onSeekFinished() {

                        }
                    });
                }
            }

            private void setTransfer(double progress) {
                textEditViewModel.setShadowTrans((int) (progress * ALPHAMAX / SEEKMAX));
            }

            private void setBlur(double progress) {
                HVEAsset hveAsset = mEditPreviewViewModel.getSelectedAsset();
                if (hveAsset instanceof HVEWordAsset) {
                    HVEWordStyle style = ((HVEWordAsset) hveAsset).getWordStyle().setShadowBlur((float) progress / 100);
                    textEditViewModel.setWordStyle(style);

                    HuaweiVideoEditor editor = mEditPreviewViewModel.getEditor();
                    if (mEditPreviewViewModel.getEditor() == null) {
                        return;
                    }
                    editor.seekTimeLine(mEditPreviewViewModel.getSeekTime(), new HuaweiVideoEditor.SeekCallback() {
                        @Override
                        public void onSeekFinished() {

                        }
                    });
                }
            }

            private void setDis(double progress) {
                HVEAsset hveAsset = mEditPreviewViewModel.getSelectedAsset();
                if (hveAsset instanceof HVEWordAsset) {
                    HVEWordStyle style =
                        ((HVEWordAsset) hveAsset).getWordStyle().setShadowDistance((float) progress / 100);
                    textEditViewModel.setWordStyle(style);

                    HuaweiVideoEditor editor = mEditPreviewViewModel.getEditor();
                    if (mEditPreviewViewModel.getEditor() == null) {
                        return;
                    }
                    editor.seekTimeLine(mEditPreviewViewModel.getSeekTime(), new HuaweiVideoEditor.SeekCallback() {
                        @Override
                        public void onSeekFinished() {
                        }
                    });

                }
            }

        }

        public class LabelHolder extends RecyclerView.ViewHolder {
            private MySeekBar seekBarTrans;

            private EditorTextView tvTrans;

            private EditorTextView tvTransLabel;

            LabelHolder(@NonNull View itemView) {
                super(itemView);
                RecyclerView recyclerView = itemView.findViewById(R.id.recycleview);
                seekBarTrans = itemView.findViewById(R.id.seekbar);
                tvTrans = itemView.findViewById(R.id.tv_trans_label);
                int transValue = textEditViewModel.getBackTransValue();
                seekBarTrans.setProgress(transValue);
                if (ScreenUtil.isRTL()) {
                    seekBarTrans.setScaleX(RTL_UI);
                } else {
                    seekBarTrans.setScaleX(LTR_UI);
                }
                tvTransLabel = itemView.findViewById(R.id.tv_trans_label);
                ConstraintLayout.LayoutParams transParams;
                if (LanguageUtils.isZh()) {
                    transParams = new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 48.0f),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);

                } else {
                    transParams = new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 64.0f),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
                }
                transParams.setMargins(SizeUtils.dp2Px(mCtx, 10.0f), 0, 0, SizeUtils.dp2Px(mCtx, 3.0f));
                transParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                transParams.bottomToBottom = R.id.seekbar;
                transParams.endToStart = R.id.seekbar;
                tvTransLabel.setLayoutParams(transParams);

                mStrokeValue = SharedPreferencesUtils.getInstance()
                    .getIntValue2(mActivity, SharedPreferencesUtils.TEXT_BACK_INDEX);
                View cancelHeader =
                    LayoutInflater.from(mCtx).inflate(R.layout.adapter_edittext_label_header, null, false);
                RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 40), SizeUtils.dp2Px(mCtx, 40));
                layoutParams.setMarginEnd(SizeUtils.dp2Px(mCtx, 8));
                View mViewBg = cancelHeader.findViewById(R.id.bg_view_head);
                ConstraintLayout mHeadLayout = cancelHeader.findViewById(R.id.layout_head);
                mViewBg.setVisibility(VISIBLE);
                mEditPreviewViewModel.setHeadClick(true);
                cancelHeader.setLayoutParams(layoutParams);
                LabelAdapter labelAdapter =
                    new LabelAdapter(fragmentContext, textEditViewModel.getColorList(), R.layout.item_color_view,
                        mEditPreviewViewModel, BACK, mSelectValue, mStrokeValue, mViewBg, textEditViewModel);
                recyclerView.setLayoutManager(new LinearLayoutManager(fragmentContext, RecyclerView.HORIZONTAL, false));
                recyclerView.setAdapter(labelAdapter);
                labelAdapter.addHeaderView(cancelHeader);

                int backColorPositionValue = textEditViewModel.getBackColorPositionValue();
                if (backColorPositionValue != -1) {
                    mEditPreviewViewModel.setHeadClick(false);
                    mViewBg.setVisibility(View.GONE);
                    tvTrans.setVisibility(View.VISIBLE);
                    seekBarTrans.setVisibility(View.VISIBLE);
                    labelAdapter.setSelectPosition(backColorPositionValue);
                } else {
                    mViewBg.setVisibility(VISIBLE);
                    tvTrans.setVisibility(View.INVISIBLE);
                    seekBarTrans.setVisibility(View.INVISIBLE);
                    mEditPreviewViewModel.setHeadClick(true);
                }
                mHeadLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        tvTrans.setVisibility(View.INVISIBLE);
                        seekBarTrans.setVisibility(View.INVISIBLE);
                        mViewBg.setVisibility(View.VISIBLE);
                        textEditViewModel.setBackColorPositionValue(-1);
                        mEditPreviewViewModel.setHeadClick(true);
                        textEditViewModel.setWordLabel(Color.TRANSPARENT);
                        return false;
                    }
                });

                labelAdapter.setOnItemClickListener(new RMCommandAdapter.OnItemClickListener() {
                    @Override
                    public void onItemTouch(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                        textEditViewModel.setBackColorPositionValue(position);
                        mEditPreviewViewModel.setHeadClick(false);
                        mViewBg.setVisibility(View.GONE);
                        tvTrans.setVisibility(View.VISIBLE);
                        seekBarTrans.setVisibility(View.VISIBLE);
                        List<Integer> colorList = textEditViewModel.getColorList();
                        if (dataPosition < colorList.size()) {
                            textEditViewModel.setLabelColor(textEditViewModel.getStrokeDefaultAlpha(),
                                colorList.get(dataPosition));
                        }
                    }

                    @Override
                    public void onItemClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                    }

                    @Override
                    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int dataPosition,
                        int position) {
                        return false;
                    }
                });

                seekBarTrans.setOnProgressChangedListener(new MySeekBar.OnProgressChangedListener() {
                    @Override
                    public void onProgressChanged(int progress) {
                        textEditViewModel.setBackTransValue(progress);
                        textEditViewModel.setLabelDefaultAlpha((int) (progress * ALPHAMAX / SEEKMAX));
                        textEditViewModel.setLabelTrans((int) (progress * ALPHAMAX / SEEKMAX));
                        mEditPreviewViewModel.setToastTime((int) seekBarTrans.getProgress() + "");
                    }
                });
                seekBarTrans.setcTouchListener(
                    isTouch -> mEditPreviewViewModel.setToastTime(isTouch ? seekBarTrans.getProgress() + "" : ""));
            }

        }

        public class SetTypeHolder extends RecyclerView.ViewHolder {
            private RadioGroup radioGroup;

            private RadioButton leftBtn;

            private RadioButton horCenterBtn;

            private RadioButton rightBtn;

            private RadioButton topBtn;

            private RadioButton verCenterBtn;

            private RadioButton bottomBtn;

            private MySeekBar wordSpacingSeek;

            private MySeekBar wordLeadingSeek;

            private EditorTextView textLayoutSeekBarSpacing;

            private EditorTextView textLayoutSeekBarLeading;

            private final CustomNestedScrollView customScrollView;

            SetTypeHolder(@NonNull View itemView) {
                super(itemView);
                radioGroup = itemView.findViewById(R.id.rg_type);
                leftBtn = itemView.findViewById(R.id.set_type_left);
                horCenterBtn = itemView.findViewById(R.id.set_type_hor_center);
                rightBtn = itemView.findViewById(R.id.set_type_right);
                topBtn = itemView.findViewById(R.id.set_type_top);
                verCenterBtn = itemView.findViewById(R.id.set_type_ver_center);
                bottomBtn = itemView.findViewById(R.id.set_type_bottom);

                wordSpacingSeek = itemView.findViewById(R.id.seekbar_spacing);
                wordLeadingSeek = itemView.findViewById(R.id.seekbar_leading);
                if (ScreenUtil.isRTL()) {
                    wordSpacingSeek.setScaleX(RTL_UI);
                    wordLeadingSeek.setScaleX(RTL_UI);
                } else {
                    wordSpacingSeek.setScaleX(LTR_UI);
                    wordLeadingSeek.setScaleX(LTR_UI);
                }
                textLayoutSeekBarSpacing = itemView.findViewById(R.id.text_layout_seek_bar_spacing);
                textLayoutSeekBarLeading = itemView.findViewById(R.id.text_layout_seek_bar_leading);
                ConstraintLayout.LayoutParams transParams;
                if (LanguageUtils.isZh()) {
                    transParams = new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 48.0f),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);

                } else {
                    transParams = new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(mCtx, 64.0f),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
                }
                transParams.setMargins(0, 0, 0, SizeUtils.dp2Px(mCtx, 3.0f));
                transParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                transParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                textLayoutSeekBarSpacing.setLayoutParams(transParams);
                textLayoutSeekBarLeading.setLayoutParams(transParams);

                customScrollView = itemView.findViewById(R.id.custom_scrollview);

                HVEWordStyle wordStyle = textEditViewModel.getWordStyle();
                if (wordStyle != null) {
                    alignment = wordStyle.getAlignment();
                    if (alignment == HVEWordStyle.ALIGNMENT_HORIZONTAL_LEFT) {
                        leftBtn.setChecked(true);
                    } else if (alignment == HVEWordStyle.ALIGNMENT_HORIZONTAL_CENTER) {
                        horCenterBtn.setChecked(true);
                    } else if (alignment == HVEWordStyle.ALIGNMENT_HORIZONTAL_RIGHT) {
                        rightBtn.setChecked(true);
                    } else if (alignment == HVEWordStyle.ALIGNMENT_VERTICAL_TOP) {
                        topBtn.setChecked(true);
                    } else if (alignment == HVEWordStyle.ALIGNMENT_VERTICAL_CENTER) {
                        verCenterBtn.setChecked(true);
                    } else if (alignment == HVEWordStyle.ALIGNMENT_VERTICAL_BOTTOM) {
                        bottomBtn.setChecked(true);
                    }

                    wordSpacingSeek.setProgress(2);
                    wordLeadingSeek.setProgress(2);
                } else {
                    horCenterBtn.setChecked(true);
                }

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.set_type_left) {
                            alignment = HVEWordStyle.ALIGNMENT_HORIZONTAL_LEFT;
                        } else if (checkedId == R.id.set_type_hor_center) {
                            alignment = HVEWordStyle.ALIGNMENT_HORIZONTAL_CENTER;
                        } else if (checkedId == R.id.set_type_right) {
                            alignment = HVEWordStyle.ALIGNMENT_HORIZONTAL_RIGHT;
                        } else if (checkedId == R.id.set_type_top) {
                            alignment = HVEWordStyle.ALIGNMENT_VERTICAL_TOP;
                        } else if (checkedId == R.id.set_type_ver_center) {
                            alignment = HVEWordStyle.ALIGNMENT_VERTICAL_CENTER;
                        } else if (checkedId == R.id.set_type_bottom) {
                            alignment = HVEWordStyle.ALIGNMENT_VERTICAL_BOTTOM;
                        }
                        textEditViewModel.setWordAlignment(alignment);
                    }
                });

                wordSpacingSeek.setOnProgressChangedListener(new MySeekBar.OnProgressChangedListener() {
                    @Override
                    public void onProgressChanged(int progress) {
                        textEditViewModel.setWordSpace(progress);
                        customScrollView.setScrollEnabled(false);
                    }
                });

                wordLeadingSeek.setOnProgressChangedListener(new MySeekBar.OnProgressChangedListener() {
                    @Override
                    public void onProgressChanged(int progress) {
                        textEditViewModel.setRowSpace(progress);
                        customScrollView.setScrollEnabled(false);
                    }
                });

                wordSpacingSeek.setcTouchListener(isTouch -> {
                    customScrollView.setScrollEnabled(true);
                });

                wordLeadingSeek.setcTouchListener(isTouch -> {
                    customScrollView.setScrollEnabled(true);
                });

            }
        }

        public class BoldItalicsHolder extends RecyclerView.ViewHolder {
            private ImageView imgBold;

            private ImageView imgItalics;

            private ImageView imgUnderLine;

            BoldItalicsHolder(@NonNull View itemView) {
                super(itemView);
                imgBold = itemView.findViewById(R.id.img_bold);
                imgItalics = itemView.findViewById(R.id.img_italics);
                imgUnderLine = itemView.findViewById(R.id.img_underline);

                HVEWordStyle wordStyle = textEditViewModel.getWordStyle();
                if (wordStyle != null) {
                    imgBold.setSelected(wordStyle.isBold());
                    imgItalics.setSelected(wordStyle.isItalics());
                    imgUnderLine.setSelected(wordStyle.isUnderline());
                }

                imgBold.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (wordStyle != null) {
                            imgBold.setSelected(!wordStyle.isBold());
                            textEditViewModel.setBold(!wordStyle.isBold());
                        }
                    }
                });

                imgItalics.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (wordStyle != null) {
                            imgItalics.setSelected(!wordStyle.isItalics());
                            textEditViewModel.setItalics(!wordStyle.isItalics());
                        }
                    }
                });

                imgUnderLine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (wordStyle != null) {
                            imgUnderLine.setSelected(!wordStyle.isUnderline());
                            textEditViewModel.setUnderline(!wordStyle.isUnderline());
                        }
                    }
                });

            }
        }
    }

    private void setEditPanelFont(CloudMaterialBean cutContent) {
        textPanelViewModel.setFontContent(cutContent);
    }

    private void initTextFont() {
        defaultFontHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDefaultFont) {
                    textEditViewModel.setTextFontPosition(-1);
                    isDefaultFont = true;
                    headerSelect.setVisibility(View.VISIBLE);
                    headerNormal.setVisibility(View.INVISIBLE);
                    int mSelectPosition = mEditTextFontAdapter.getSelectPosition();
                    mEditTextFontAdapter.setSelectPosition(-1);
                    if (mSelectPosition != -1) {
                        mEditTextFontAdapter.notifyItemChanged(mSelectPosition);
                    }
                    setEditPanelFont(null);
                    CloudMaterialBean materialsCutContent = mEditPreviewViewModel.getDefaultFontContent().getValue();
                    if (materialsCutContent == null) {
                        textEditViewModel.setFontPath(Constant.DEFAULT_FONT_PATH, "");
                        return;
                    }
                    textEditViewModel.setFontPath(materialsCutContent.getLocalPath(), materialsCutContent.getId());
                }
            }
        });

        mEditTextFontAdapter.setOnItemClickListener(new EditTextFontAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int dataPosition) {
                int mSelectPosition = mEditTextFontAdapter.getSelectPosition();
                textEditViewModel.setTextFontPosition(position);
                if (mSelectPosition != position) {
                    isDefaultFont = false;
                    headerSelect.setVisibility(View.INVISIBLE);
                    headerNormal.setVisibility(View.VISIBLE);
                    mEditTextFontAdapter.setSelectPosition(position);
                    if (mSelectPosition != -1) {
                        mEditTextFontAdapter.notifyItemChanged(mSelectPosition);
                    }
                    mEditTextFontAdapter.notifyItemChanged(position);
                    textEditViewModel.setFontPath(mFontList.get(dataPosition).getLocalPath(),
                        mFontList.get(dataPosition).getId());
                    setEditPanelFont(mFontList.get(dataPosition));
                }
            }

            @Override
            public void onDownloadClick(int position, int aDataPosition) {
                int previousPosition = mEditTextFontAdapter.getSelectPosition();
                mEditTextFontAdapter.setSelectPosition(position);
                CloudMaterialBean content = mFontList.get(aDataPosition);
                mEditTextFontAdapter.addDownloadMaterial(content);
                mFontViewModel.downloadColumn(previousPosition, position, aDataPosition, content);
            }
        });

        mFontViewModel.getErrorString().observe(this, errorString -> {
            if (!TextUtils.isEmpty(errorString)) {
                mErrorTv.setText(errorString);
                mErrorLayout.setVisibility(View.VISIBLE);
                mLoadingIndicatorView.hide();
                isErrorVisible = true;
            }
        });

        mFontViewModel.getEmptyString().observe(this, errorString -> {
            ToastWrapper.makeText(fragmentContext, errorString, Toast.LENGTH_SHORT).show();
        });

        mLoadingIndicatorView.show();
        mFontViewModel.loadMaterials(mCurrentPage);
        mFontViewModel.getPageData().observe(getViewLifecycleOwner(), list -> {
            isErrorVisible = false;
            if (mCurrentPage == 0) {
                mLoadingIndicatorView.hide();
                mFontList.clear();
            }

            if (!mFontList.containsAll(list)) {
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                mFontList.addAll(list);
                mEditTextFontAdapter.notifyDataSetChanged();
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
            }
        });

        mFontViewModel.getDownloadSuccess().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "success:" + downloadInfo.getMaterialBean().getLocalPath());
            long endTimeDiff = System.currentTimeMillis();
            mEditTextFontAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            int downloadPosition = downloadInfo.getPosition();
            if (downloadPosition >= 0 && downloadPosition < mFontList.size()
                && downloadInfo.getContentId().equals(mFontList.get(downloadInfo.getDataPosition()).getId())) {
                isDefaultFont = false;
                headerSelect.setVisibility(View.INVISIBLE);
                headerNormal.setVisibility(View.VISIBLE);
                mFontList.set(downloadInfo.getDataPosition(), downloadInfo.getMaterialBean());
                mEditTextFontAdapter.notifyDataSetChanged();
                if (downloadPosition == mEditTextFontAdapter.getSelectPosition()) {
                    textEditViewModel.setFontPath(downloadInfo.getMaterialBean().getLocalPath(),
                        downloadInfo.getMaterialBean().getId());
                    setEditPanelFont(downloadInfo.getMaterialBean());
                }
            }
        });

        mFontViewModel.getFontColumn().observe(this, fontColumn -> {
            textPanelViewModel.setFontColumn(fontColumn);
        });

        mFontViewModel.getDownloadFail().observe(this, downloadInfo -> {
            mEditTextFontAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            int downloadPosition = downloadInfo.getPosition();
            int dataPosition = downloadInfo.getDataPosition();
            if (downloadPosition >= 0 && dataPosition < mFontList.size()
                && downloadInfo.getContentId().equals(mFontList.get(dataPosition).getId())) {
                mFontList.set(dataPosition, downloadInfo.getMaterialBean());
                mEditTextFontAdapter.notifyItemChanged(downloadPosition);
            }
            ToastWrapper.makeText(mActivity, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
        });

        mFontViewModel.getDownloadProgress().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "progress:" + downloadInfo.getProgress());
            updateProgress(downloadInfo);
        });

        mFontViewModel.getBoundaryPageData().observe(this, aBoolean -> {
            mHasNextPage = aBoolean;
        });

        if (mFontRecycleView != null) {
            mFontRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == SCROLL_STATE_IDLE
                        && mEditTextFontAdapter.getItemCount() >= mEditTextFontAdapter.getOriginalItemCount()) {
                        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                        if (!isScrolled && mHasNextPage && layoutManager != null) {
                            int lastCompletelyVisibleItemPosition =
                                layoutManager.findLastCompletelyVisibleItemPosition();
                            if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1) {
                                mCurrentPage++;
                                mFontViewModel.loadMaterials(mCurrentPage);
                                isScrolled = false;
                            }
                        }
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (mHasNextPage && layoutManager != null) {
                        int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                        if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1 && dy > 0) {
                            mCurrentPage++;
                            mFontViewModel.loadMaterials(mCurrentPage);
                            isScrolled = true;
                        }
                    }

                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int firstPosition = layoutManager.findFirstVisibleItemPosition();
                        if (firstPosition != -1 && visibleItemCount > 0 && !isFirst && mFontList.size() > 0) {
                            isFirst = true;
                            for (int i = 0; i < visibleItemCount - 1; i++) {
                                CloudMaterialBean cutContent = mFontList.get(i);
                                mEditTextFontAdapter.addFirstScreenMaterial(cutContent);
                            }
                        }
                    }
                }
            });
        }

        mErrorLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mCurrentPage == 0) {
                mErrorLayout.setVisibility(View.GONE);
                mLoadingIndicatorView.show();
            }
            mFontViewModel.loadMaterials(mCurrentPage);
        }));

    }

    private void updateProgress(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        if (mFontRecycleView != null && downloadPosition >= 0 && downloadInfo.getDataPosition() < mFontList.size()
            && downloadInfo.getContentId().equals(mFontList.get(downloadInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder =
                (RViewHolder) mFontRecycleView.findViewHolderForAdapterPosition(downloadInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mHwProgressBar = viewHolder.itemView.findViewById(R.id.item_progress);
                mHwProgressBar.setProgress(downloadInfo.getProgress());
            }
        }
    }

    public static class StyleAdapter extends SelectAdapter {
        private final int mImageViewWidth;

        StyleAdapter(Context context, List list) {
            super(context, list, R.layout.item_edit_text_style, VH.class);
            if (FoldScreenUtil.isFoldable() && FoldScreenUtil.isFoldableScreenExpand(context)) {
                mImageViewWidth = (SizeUtils.screenWidth(context) - (SizeUtils.dp2Px(context, 56))) / 8;
            } else {
                mImageViewWidth = (SizeUtils.screenWidth(context) - (SizeUtils.dp2Px(context, 56))) / 4;
            }
        }

        public class VH extends ThisViewHolder {
            public VH(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            protected void findView(View view) {

            }

            @Override
            protected void bindView(Object o) {
                itemView.setLayoutParams(new ViewGroup.LayoutParams(mImageViewWidth, mImageViewWidth));
                if ((Integer) o == R.drawable.icon_cancel_wu) {
                    ((ImageView) itemView).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                } else {
                    ((ImageView) itemView).setScaleType(ImageView.ScaleType.FIT_XY);
                }
                ((ImageView) itemView).setImageDrawable(mCtx.getResources().getDrawable((Integer) o));
            }

            @Override
            protected void onSelect(View view) {
                view.setSelected(true);
            }

            @Override
            protected void onUnSelect(View view) {
                view.setSelected(false);
            }
        }
    }

    public static class LabelAdapter extends RCommandAdapter<Integer> {
        private int mSelectPosition = -1;

        private EditPreviewViewModel editPreviewViewModel;

        private TextEditViewModel textEditViewModel;

        private Context context;

        View viewColor;

        private String type;

        public LabelAdapter(Context context, List<Integer> list, int layoutId,
            EditPreviewViewModel editPreviewViewModel, String type, int selectValue, int strokeValue,
            TextEditViewModel textEditViewModel) {
            super(context, list, layoutId);
            this.context = context;
            this.editPreviewViewModel = editPreviewViewModel;
            this.type = type;
            this.textEditViewModel = textEditViewModel;
        }

        public LabelAdapter(Context context, List<Integer> list, int layoutId,
            EditPreviewViewModel editPreviewViewModel, String type, int selectValue, int strokeValue, View headBg,
            TextEditViewModel textEditViewModel) {
            super(context, list, layoutId);
            this.context = context;
            this.editPreviewViewModel = editPreviewViewModel;
            this.type = type;
            this.textEditViewModel = textEditViewModel;
        }

        @Override
        protected void convert(RViewHolder holder, Integer colorBean, int dataPosition, int position) {
            viewColor = holder.getView(R.id.color_view_item_color);
            viewColor.setBackgroundColor(colorBean);
            View viewBgItem = holder.getView(R.id.bg_view_item_color);
            viewBgItem.setVisibility(mSelectPosition == position ? View.VISIBLE : View.GONE);

            viewColor.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mSelectPosition != position) {
                        if (mSelectPosition != -1) {
                            int lastp = mSelectPosition;
                            mSelectPosition = position;
                            notifyItemChanged(lastp);
                        } else {
                            mSelectPosition = position;
                        }
                        notifyItemChanged(mSelectPosition);
                    }
                    return false;
                }
            });

            editPreviewViewModel.getHeadClick().observe((LifecycleOwner) context, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    switch (type) {
                        case COLOR:
                            mSelectPosition = textEditViewModel.getColorPositionValue();
                            break;
                        case STROKE:
                            mSelectPosition = textEditViewModel.getStrokeColorPositionValue();
                            break;
                        case SHAWDOW:
                            mSelectPosition = textEditViewModel.getShadowColorPositionValue();
                            break;
                        case BACK:
                            mSelectPosition = textEditViewModel.getBackColorPositionValue();
                            break;
                        default:
                            SmartLog.i(TAG, "onChanged run in default case");
                    }

                    if (aBoolean) {
                        viewBgItem.setVisibility(View.GONE);
                    } else {
                        viewBgItem.setVisibility(mSelectPosition == position ? View.VISIBLE : View.GONE);
                    }
                }
            });
        }

        public void setSelectPosition(int selectPosition) {
            this.mSelectPosition = selectPosition;
        }

        public int getSelectPosition() {
            return this.mSelectPosition;
        }
    }

    private void initTimeoutState() {
        mEditPreviewViewModel.initTimeoutManager();
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchListener = new VideoClipsActivity.TimeOutOnTouchListener() {
        @Override
        public boolean onTouch(MotionEvent ev) {
            initTimeoutState();
            return false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ((mActivity instanceof VideoClipsActivity)) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        }
        mEditPreviewViewModel.destroyTimeoutManager();
    }
}
