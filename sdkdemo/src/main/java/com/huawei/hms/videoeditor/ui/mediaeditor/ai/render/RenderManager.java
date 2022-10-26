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

package com.huawei.hms.videoeditor.ui.mediaeditor.ai.render;

import android.content.Context;
import android.opengl.GLES30;
import android.util.SparseArray;

import com.huawei.hms.videoeditor.ai.HVEAIBeautyOptions;
import com.huawei.hms.videoeditor.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.camera.CameraParam;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.filter.ImageBeautyFilter;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.filter.ImageFilter;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.filter.ImageInputFilter;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public final class RenderManager {

    public RenderManager() {
        mCameraParam = CameraParam.getInstance();
    }

    private SparseArray<ImageFilter> mFilterArrays = new SparseArray<ImageFilter>();

    private FloatBuffer mVertexBuffer;

    private FloatBuffer mTextureBuffer;

    private FloatBuffer mTextureBuffer2;

    private FloatBuffer mDisplayVertexBuffer;

    private FloatBuffer mDisplayTextureBuffer;

    private FloatBuffer mDisplayTextureBuffer2;

    private int mViewWidth, mViewHeight;

    private int mTextureWidth, mTextureHeight;

    private CameraParam mCameraParam;

    private ImageBeautyFilter imageBeautyFilter;

    private boolean isInit = false;

    private int frameWidth;

    private int frameHeight;

    public void init(Context context) {
        initBuffers();
        initFilters(context);
    }

    public void release() {
        imageBeautyFilter.release();
        releaseBuffers();
        releaseFilters();
    }

    private void releaseFilters() {
        for (int i = 0; i < RenderIndex.NumberIndex; i++) {
            if (mFilterArrays.get(i) != null) {
                mFilterArrays.get(i).release();
            }
        }
        mFilterArrays.clear();
    }

    private void releaseBuffers() {
        if (mVertexBuffer != null) {
            mVertexBuffer.clear();
            mVertexBuffer = null;
        }
        if (mTextureBuffer != null) {
            mTextureBuffer.clear();
            mTextureBuffer = null;
        }
        if (mDisplayVertexBuffer != null) {
            mDisplayVertexBuffer.clear();
            mDisplayVertexBuffer = null;
        }
        if (mDisplayTextureBuffer != null) {
            mDisplayTextureBuffer.clear();
            mDisplayTextureBuffer = null;
        }
    }

    private void initBuffers() {
        releaseBuffers();
        mDisplayVertexBuffer = OpenGLUtils.createFloatBuffer(RotationUtils.CubeVertices);
        mDisplayTextureBuffer = OpenGLUtils.createFloatBuffer(RotationUtils.TextureVertices);

        mDisplayTextureBuffer2 = OpenGLUtils.createFloatBuffer(RotationUtils.TextureVertices_180);
        mVertexBuffer = OpenGLUtils.createFloatBuffer(RotationUtils.CubeVertices);
        mTextureBuffer = OpenGLUtils.createFloatBuffer(RotationUtils.TextureVertices);

        mTextureBuffer2 = OpenGLUtils.createFloatBuffer(RotationUtils.TextureVertices_180);
    }

    private void initFilters(Context context) {
        releaseFilters();
        imageBeautyFilter = new ImageBeautyFilter();
        mFilterArrays.put(RenderIndex.CameraIndex, new ImageInputFilter(context));
        mFilterArrays.put(RenderIndex.DisplayIndex, new ImageFilter(context));
    }

    public void initBeauty(HVEAIInitialCallback callback) {
        if (imageBeautyFilter == null) {
            imageBeautyFilter = new ImageBeautyFilter();
        }
        imageBeautyFilter.initEngine(callback);
    }

    public void prepareBeauty() {
        if (imageBeautyFilter == null) {
            imageBeautyFilter = new ImageBeautyFilter();
        }

        imageBeautyFilter.prepare();
        isInit = true;
    }

    public int drawFrame(int inputTexture, float[] mMatrix) {
        int currentTexture = inputTexture;
        if (mFilterArrays.get(RenderIndex.CameraIndex) == null || mFilterArrays.get(RenderIndex.DisplayIndex) == null) {
            return currentTexture;
        }
        if (mFilterArrays.get(RenderIndex.CameraIndex) instanceof ImageInputFilter) {
            ((ImageInputFilter) mFilterArrays.get(RenderIndex.CameraIndex)).setTextureTransformMatrix(mMatrix);
        }
        currentTexture =
            mFilterArrays.get(RenderIndex.CameraIndex).drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer2);

        if (!mCameraParam.showCompare) {

            if (isInit && (mTextureWidth != frameWidth || mTextureHeight != frameHeight)) {
                frameWidth = mTextureWidth;
                frameHeight = mTextureHeight;
                imageBeautyFilter.resize(frameWidth, frameHeight);
            }
            if (isInit) {
                HVEAIBeautyOptions options = new HVEAIBeautyOptions.Builder().setBigEye(mCameraParam.beauty.bigEye)
                    .setBlurDegree(mCameraParam.beauty.blurDegree)
                    .setBrightEye(mCameraParam.beauty.brightEye)
                    .setThinFace(mCameraParam.beauty.thinFace)
                    .setWhiteDegree(mCameraParam.beauty.whiteDegree)
                    .build();
                imageBeautyFilter.updateOptions(options);
                GLES30.glDisable(GL10.GL_CULL_FACE);
                currentTexture = imageBeautyFilter.drawFrameBuffer(currentTexture);
            }
        }

        mFilterArrays.get(RenderIndex.DisplayIndex)
            .drawFrame(currentTexture, mDisplayVertexBuffer, mDisplayTextureBuffer2);
        return currentTexture;
    }

    public void setTextureSize(int width, int height) {
        mTextureWidth = width;
        mTextureHeight = height;
        if (mViewWidth != 0 && mViewHeight != 0) {
            adjustCoordinateSize();
            onFilterChanged();
        }
    }

    public void setDisplaySize(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
        if (mTextureWidth != 0 && mTextureHeight != 0) {
            adjustCoordinateSize();
            onFilterChanged();
        }
    }

    private void onFilterChanged() {
        for (int i = 0; i < RenderIndex.NumberIndex; i++) {
            if (mFilterArrays.get(i) != null) {
                mFilterArrays.get(i).onInputSizeChanged(mTextureWidth, mTextureHeight);
                if (i < RenderIndex.DisplayIndex) {
                    mFilterArrays.get(i).initFrameBuffer(mTextureWidth, mTextureHeight);
                }
                mFilterArrays.get(i).onDisplaySizeChanged(mViewWidth, mViewHeight);
            }
        }
    }

    private void adjustCoordinateSize() {
        float[] textureCoord = null;
        float[] vertexCoord = null;
        float[] textureVertices = RotationUtils.TextureVertices;
        float[] vertexVertices = RotationUtils.CubeVertices;
        float ratioMax = Math.max((float) mViewWidth / mTextureWidth, (float) mViewHeight / mTextureHeight);
        int imageWidth = Math.round(mTextureWidth * ratioMax);
        int imageHeight = Math.round(mTextureHeight * ratioMax);
        float ratioWidth = (float) imageWidth / (float) mViewWidth;
        float ratioHeight = (float) imageHeight / (float) mViewHeight;
        float distHorizontal = (1 - 1 / ratioWidth) / 2;
        float distVertical = (1 - 1 / ratioHeight) / 2;
        textureCoord =
            new float[] {addDistance(textureVertices[0], distHorizontal), addDistance(textureVertices[1], distVertical),
                addDistance(textureVertices[2], distHorizontal), addDistance(textureVertices[3], distVertical),
                addDistance(textureVertices[4], distHorizontal), addDistance(textureVertices[5], distVertical),
                addDistance(textureVertices[6], distHorizontal), addDistance(textureVertices[7], distVertical),};
        if (vertexCoord == null) {
            vertexCoord = vertexVertices;
        }
        if (textureCoord == null) {
            textureCoord = textureVertices;
        }
        if (mDisplayVertexBuffer == null || mDisplayTextureBuffer == null) {
            initBuffers();
        }
        mDisplayVertexBuffer.clear();
        mDisplayVertexBuffer.put(vertexCoord).position(0);
        mDisplayTextureBuffer.clear();
        mDisplayTextureBuffer.put(textureCoord).position(0);
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }
}
