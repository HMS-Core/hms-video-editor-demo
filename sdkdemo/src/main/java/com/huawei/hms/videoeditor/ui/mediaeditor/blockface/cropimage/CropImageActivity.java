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

package com.huawei.hms.videoeditor.ui.mediaeditor.blockface.cropimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.ui.common.utils.BitmapDecodeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeIntent;

public class CropImageActivity extends BaseActivity implements View.OnClickListener {
    public final static String CROP_IMAGE_RESULT = "crop_image_result";

    private final static String IMAGE_PATH = "image_path";

    public final static int REQUEST_CODE_OFF_CROP = 0x1001;

    private String mImagePath = "";

    private ImageView iv_close;

    private TextView tv_title;

    private LinearLayout mViewpreview;

    private CropImageLayout mCropImageLayout;

    public static void startActivityForResult(Activity activity, String imagePath) {
        Intent intent = new Intent(activity, CropImageActivity.class);
        intent.putExtra(IMAGE_PATH, imagePath);
        activity.startActivityForResult(intent, REQUEST_CODE_OFF_CROP);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBarColor = R.color.black;
        setContentView(R.layout.activity_crop_image);
        SafeIntent safeIntent = new SafeIntent(getIntent());
        mImagePath = safeIntent.getStringExtra(IMAGE_PATH);
        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.iv_certain).setOnClickListener(this);
        iv_close = findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        iv_close.setVisibility(View.VISIBLE);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.action_11);
        mCropImageLayout = findViewById(R.id.crop_image);
        mViewpreview = findViewById(R.id.viewpreview);
        mViewpreview.requestLayout();
        mViewpreview.postInvalidate();
    }

    private void initData() {
        if (!TextUtils.isEmpty(mImagePath)) {
            Bitmap bitmap = getBitmap(mImagePath);
            if (bitmap == null) {
                return;
            }
            mCropImageLayout.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View v) {
        int resourceId = v.getId();
        if (resourceId == R.id.iv_close) {
            onBackPressed();
        } else if (resourceId == R.id.iv_certain) {
            String imgPath = mCropImageLayout.clip();
            Intent intent = new Intent();
            intent.putExtra(CROP_IMAGE_RESULT, imgPath);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * Return bitmap.
     *
     * @param filePath The path of file.
     * @return bitmap
     */
    public static Bitmap getBitmap(final String filePath) {
        if (StringUtil.isSpace(filePath)) {
            return null;
        }
        return BitmapDecodeUtils.decodeFile(filePath);
    }
}
