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

package com.huawei.hms.videoeditor.ui.mediaeditor.blockface;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.huawei.hms.videoeditor.ui.common.adapter.comment.RCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.common.view.image.RoundImage;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import com.bumptech.glide.Glide;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FaceStickerListAdapter extends RCommandAdapter<FaceBlockingInfo> {

    private Context mContext;

    private volatile int mSelectPosition = -1;

    private final Map<String, CloudMaterialBean> mDownloadingMap = new LinkedHashMap<>();

    public OnStickerSelectedListener stickerSelectedListener;

    public FaceStickerListAdapter(Context context, List<FaceBlockingInfo> list, int layoutId) {
        super(context, list, layoutId);
        mContext = context;
    }

    @Override
    protected void convert(RViewHolder holder, FaceBlockingInfo faceBlockingInfo, int dataPosition, int position) {
        CloudMaterialBean materialsCutContent = faceBlockingInfo.getMaterialsCutContent();
        View viewNoClick = holder.getView(R.id.view_no_click);
        ImageView ivDelete = holder.getView(R.id.iv_delete);
        RoundImage itemImageView = holder.getView(R.id.item_image_view);
        ImageView mDownloadIv = holder.getView(R.id.item_download_view);
        ProgressBar mDownloadPb = holder.getView(R.id.item_progress);
        itemImageView.setSelected(mSelectPosition == position);
        if (faceBlockingInfo.isShowGray()) {
            mDownloadIv.setEnabled(false);
            viewNoClick.setVisibility(View.VISIBLE);
        } else {
            mDownloadIv.setEnabled(true);
            viewNoClick.setVisibility(View.GONE);
        }
        if (materialsCutContent != null) {
            if (!StringUtil.isEmpty(materialsCutContent.getLocalPath())) {
                mDownloadIv.setVisibility(View.GONE);
                mDownloadPb.setVisibility(View.GONE);
            } else {
                mDownloadPb.setVisibility(mSelectPosition == position ? View.VISIBLE : View.GONE);
                mDownloadIv.setVisibility(View.VISIBLE);
            }
        } else {
            mDownloadIv.setVisibility(View.GONE);
            mDownloadPb.setVisibility(View.GONE);
        }

        if (faceBlockingInfo.getType().equals("0")) {
            Glide.with(mContext)
                .asBitmap()
                .load(faceBlockingInfo.getLocalSticker())
                .placeholder(R.drawable.sticker_normal_bg)
                .into(itemImageView);
        } else if (faceBlockingInfo.getType().equals("1")) {
            if (materialsCutContent != null) {
                Glide.with(mContext)
                    .asBitmap()
                    .load(materialsCutContent.getPreviewUrl())
                    .placeholder(R.drawable.sticker_normal_bg)
                    .into(itemImageView);
            }
        }
        if (faceBlockingInfo.getType().equals("0")) {
            ivDelete.setVisibility(View.VISIBLE);
        } else {
            ivDelete.setVisibility(View.INVISIBLE);
        }
        itemImageView.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stickerSelectedListener != null) {
                    if (materialsCutContent != null) {
                        if (!StringUtil.isEmpty(materialsCutContent.getLocalPath())) {
                            stickerSelectedListener.onStickerSelected(faceBlockingInfo, position, dataPosition);
                        } else {
                            if (!mDownloadingMap.containsKey(materialsCutContent.getId())) {
                                stickerSelectedListener.onStickerDownload(position, dataPosition);
                            }
                        }
                    } else {
                        stickerSelectedListener.onStickerSelected(faceBlockingInfo, position, dataPosition);
                    }
                }
            }
        }));

        mDownloadIv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (materialsCutContent == null) {
                return;
            }

            if (stickerSelectedListener != null && !mDownloadingMap.containsKey(materialsCutContent.getId())) {
                stickerSelectedListener.onStickerDownload(position, dataPosition);
            }
        }));
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stickerSelectedListener != null) {
                    stickerSelectedListener.onStickerDeleted(faceBlockingInfo, position);
                }
            }
        });

    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.mSelectPosition = selectPosition;
    }

    public void addDownloadMaterial(CloudMaterialBean item) {
        mDownloadingMap.put(item.getId(), item);
    }

    public void removeDownloadMaterial(String contentId) {
        mDownloadingMap.remove(contentId);
    }

    public void setStickerSelectedListener(OnStickerSelectedListener stickerSelectedListener) {
        this.stickerSelectedListener = stickerSelectedListener;
    }

    public interface OnStickerSelectedListener {

        void onStickerSelected(FaceBlockingInfo faceBlockingInfo, int position, int dataPosition);

        void onStickerDownload(int position, int dataPosition);

        void onStickerDeleted(FaceBlockingInfo faceBlockingInfo, int position);
    }
}
