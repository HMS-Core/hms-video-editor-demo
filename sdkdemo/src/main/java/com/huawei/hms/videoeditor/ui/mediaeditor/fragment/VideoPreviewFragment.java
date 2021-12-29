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

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.secure.android.common.intent.SafeBundle;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class VideoPreviewFragment extends PreviewLazyFragment
    implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener {
    private static final String TAG = "VideoPreviewFragment";

    private TextureView mTextureView;

    private MediaPlayer mMediaPlayer;

    private String mMediaPath;

    private RelativeLayout mPlayLayout;

    private RelativeLayout mTranLayout;

    private ImageView mPreviewIv;

    private int mVideoWidth;

    private int mVideoHeight;

    private float mTextureViewWidth;

    private float mTextureViewHeight;

    public static VideoPreviewFragment newInstance(String path) {
        SafeBundle args = new SafeBundle();
        args.putString(Constant.EXTRA_SELECT_RESULT, path);
        VideoPreviewFragment fragment = new VideoPreviewFragment();
        fragment.setArguments(args.getBundle());
        return fragment;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_video_preview;
    }

    @Override
    protected void initView(View view) {
        initPlayer();
        SafeBundle safeBundle = new SafeBundle(getArguments());
        mMediaPath = safeBundle.getString(Constant.EXTRA_SELECT_RESULT);
        mTextureView = view.findViewById(R.id.page_video);
        mTranLayout = view.findViewById(R.id.rl_video_play_button_page);
        mTextureView.setSurfaceTextureListener(this);
        mPreviewIv = view.findViewById(R.id.iv_video_first_bitmap);
        mPlayLayout = view.findViewById(R.id.play_layout);
        mTranLayout.setVisibility(View.VISIBLE);
        mPreviewIv.setVisibility(View.VISIBLE);
        Glide.with(this).load(mMediaPath).into(mPreviewIv);

        mTextureView.setOnClickListener(v -> {
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
                mPlayLayout.setVisibility(View.VISIBLE);
            }
        });

        mPlayLayout.setOnClickListener(v -> {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
                mPlayLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void lazyLoad() {
        play(mMediaPath);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnInfoListener(null);
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnVideoSizeChangedListener(null);
            mMediaPlayer = null;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mTranLayout != null) {
            mTranLayout.setVisibility(View.VISIBLE);
        }

        if (mPreviewIv != null) {
            mPreviewIv.setVisibility(View.VISIBLE);
            Glide.with(this).load(mMediaPath).into(mPreviewIv);
        }
        if (mMediaPlayer != null) {
            mTextureViewWidth = width;
            mTextureViewHeight = height;
            mMediaPlayer.setSurface(new Surface(surface));
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        SmartLog.d(TAG, "width = " + width + " " + "height = " + height);
        mTextureViewWidth = width;
        mTextureViewHeight = height;
        updateTextureViewSizeCenter();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.start();
        mp.setLooping(true);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            if (mTranLayout != null) {
                mTranLayout.setVisibility(View.INVISIBLE);
            }
            if (mTextureView != null) {
                mTextureView.setVisibility(View.VISIBLE);
            }
            if (mPreviewIv != null) {
                mPreviewIv.setVisibility(View.INVISIBLE);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    private void initPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
        }
    }

    private void play(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }

        resetMediaPlayer(path);
    }

    private void resetMediaPlayer(String path) {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepareAsync();
            }
        } catch (RuntimeException e) {
            SmartLog.e(TAG, "prepare fail RuntimeException");
        } catch (Exception e) {
            SmartLog.e(TAG, "prepare fail Exception");
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        mVideoWidth = mp.getVideoWidth();
        mVideoHeight = mp.getVideoHeight();
        updateTextureViewSizeCenter();
    }

    private void updateTextureViewSizeCenter() {
        float sx = mTextureViewWidth / (float) mVideoWidth;
        float sy = mTextureViewHeight / (float) mVideoHeight;
        Matrix matrix = new Matrix();

        matrix.preTranslate((mTextureViewWidth - mVideoWidth) / 2, (mTextureViewHeight - mVideoHeight) / 2);

        matrix.preScale(mVideoWidth / mTextureViewWidth, mVideoHeight / mTextureViewHeight);

        if (sx >= sy) {
            matrix.postScale(sy, sy, mTextureViewWidth / 2, mTextureViewHeight / 2);
        } else {
            matrix.postScale(sx, sx, mTextureViewWidth / 2, mTextureViewHeight / 2);
        }

        if (mTextureView != null) {
            mTextureView.setTransform(matrix);
            mTextureView.postInvalidate();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
