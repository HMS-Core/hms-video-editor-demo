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

package com.huawei.hms.videoeditor.ui.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaCodec;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.huawei.hms.videoeditor.sdk.bean.HVECut;
import com.huawei.hms.videoeditor.sdk.bean.HVESize;
import com.huawei.hms.videoeditor.sdk.error.EditorRuntimeException;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageUtil {
    private static final String TAG = "ImageUtil";

    public static Bitmap adjustBitmapSize(Bitmap bitmap, int canvasWidth, int canvasHeight) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = (float) height / (float) width;

        HVESize size = null;
        if (canvasWidth * scale > canvasHeight) {
            size = new HVESize(canvasHeight / scale, canvasHeight);
        } else {
            size = new HVESize(canvasWidth, canvasWidth * scale);
        }

        float adjustScale = Math.max(canvasWidth / size.width, canvasHeight / size.height);

        int newBitmapWidth = (int) Math.ceil(size.width * adjustScale);
        int newBitmapHeight = (int) Math.ceil(size.height * adjustScale);

        SmartLog.i(TAG, "adjustBitmapSize newBitmapWidth: " + newBitmapWidth + "/" + newBitmapHeight);
        Bitmap scaleBitmap = scaleBitmap(bitmap, newBitmapWidth, newBitmapHeight);

        int x = (newBitmapWidth - canvasWidth) / 2;
        int y = (newBitmapHeight - canvasHeight) / 2;
        Bitmap rtn = null;
        try {
            rtn = Bitmap.createBitmap(scaleBitmap, x, y, canvasWidth, canvasHeight);
        } catch (IllegalArgumentException exception) {
            SmartLog.e(TAG, "adjustBitmapSize failed:" + exception.getMessage());
        }
        return rtn == null ? scaleBitmap : rtn;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
        float scaleWidth = ((float) width) / bitmap.getWidth();
        float scaleHeight = ((float) height) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap byteBufferToBitmap(MediaCodec mediaCodec, int outputBufferIndex, int quality) {
        return byteBufferToBitmap(mediaCodec, outputBufferIndex, quality, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap byteBufferToBitmap(MediaCodec mediaCodec, int outputBufferIndex, int quality, int rote) {
        Image image = mediaCodec.getOutputImage(outputBufferIndex);
        if (image == null) {
            return null;
        }
        byte[] tmp = yuv420888ToNv21(image);
        YuvImage yuvImage = new YuvImage(tmp, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = null;
        try {
            yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), quality, stream);
            bitmap = BitmapDecodeUtils.decodeByteArray(stream.toByteArray(), 0, stream.size());
        } catch (EditorRuntimeException e) {
            SmartLog.e(TAG, e.getMessage());
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                SmartLog.e(TAG, e.getMessage());
            }
        }
        image.close();
        return bitmap;
    }

    private static byte[] yuv420888ToNv21(Image image) {
        long time = System.currentTimeMillis();
        byte[] nv21Bytes;
        ByteBuffer yByteBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uByteBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vByteBuffer = image.getPlanes()[2].getBuffer();
        int sizeY = yByteBuffer.remaining();
        int sizeU = uByteBuffer.remaining();
        int sizeV = vByteBuffer.remaining();
        nv21Bytes = new byte[sizeY + sizeU + sizeV];
        // U and V are swapped
        yByteBuffer.get(nv21Bytes, 0, sizeY);
        vByteBuffer.get(nv21Bytes, sizeY, sizeV);
        uByteBuffer.get(nv21Bytes, sizeY + sizeV, sizeU);
        SmartLog.i(TAG, "Image To YUV Cost = " + (System.currentTimeMillis() - time));
        return nv21Bytes;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap byteBufferToBitmapWithScale(MediaCodec mediaCodec, int outputBufferIndex, int width,
        int height, int quality, int rotation) {
        Bitmap bitmap = scaleAndCutBitmap(byteBufferToBitmap(mediaCodec, outputBufferIndex, quality, rotation), width,
            height, rotation);
        return bitmap;
    }

    public static Bitmap scaleAndCutBitmap(Bitmap bitmap, int width, int height, int rotation) {
        if (bitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        double scale = getScale(bitmap.getWidth(), bitmap.getHeight(), width, height);

        matrix.postScale((float) scale, (float) scale);
        matrix.postRotate(rotation);
        Bitmap bitmap1;
        int space;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            space = (bitmap.getHeight() - bitmap.getWidth()) / 2;
            bitmap1 = Bitmap.createBitmap(bitmap, 0, space, bitmap.getWidth(), bitmap.getWidth(), matrix, true);
        } else {
            space = (bitmap.getWidth() - bitmap.getHeight()) / 2;
            bitmap1 = Bitmap.createBitmap(bitmap, space, 0, bitmap.getHeight(), bitmap.getHeight(), matrix, true);
        }
        return bitmap1;
    }

    private static double getScale(int oriWidth, int oriHeight, int targetWidth, int targetHeight) {
        double scale = 0;
        if (oriHeight <= oriWidth) {
            scale = 1.0 * targetHeight / oriHeight;
        } else {
            scale = 1.0 * targetWidth / oriWidth;
        }

        return scale > 1 ? 1 : scale;
    }

    public static float[] correctionWH(float limitWidth, float limitHeight, float dataWidth, float dataHeight) {
        return correctionWH(false, limitWidth, limitHeight, dataWidth, dataHeight);
    }

    public static float[] correctionWH(boolean isLandScapeMode, float limitWidth, float limitHeight, float dataWidth,
        float dataHeight) {
        if (limitWidth == 0 || limitHeight == 0 || dataWidth == 0 || dataHeight == 0) {
            SmartLog.w(TAG, "data is zero when call correctionWH");
            return new float[2];
        }

        if (isLandScapeMode) {
            float temp = dataWidth;
            dataWidth = dataHeight;
            dataHeight = temp;
        }

        float limitZoom = (float) limitWidth / (float) limitHeight;

        float sourceZoom = (float) dataWidth / (float) dataHeight;

        float rtnWidth;
        float rtnHeight;
        if (limitZoom > sourceZoom) {
            rtnHeight = limitHeight;
            rtnWidth = (rtnHeight * sourceZoom);
        } else {
            rtnWidth = limitWidth;
            rtnHeight = (rtnWidth / sourceZoom);
        }

        float[] rtn = new float[2];
        rtn[0] = rtnWidth;
        rtn[1] = rtnHeight;
        return rtn;
    }

    public static HVECut computeCurrentHVECut(float width, float height, float posX, float poxY, float picWidth,
        float picHeight) {
        float widthZoom = picWidth / width;
        float heightZoom = picHeight / height;
        if (widthZoom > heightZoom) {
            picWidth = height * (picWidth / picHeight);
            picHeight = height;
        } else {
            picHeight = width * (picHeight / picWidth);
            picWidth = width;
        }

        float leftBottomX = posX - picWidth / 2;
        float leftBottomY = poxY + picHeight / 2;

        float textureLeftBottomX = (posX - width / 2 - leftBottomX) / picWidth;
        float textureLeftBottomY = (leftBottomY - poxY - height / 2) / picHeight;

        float textureRightTopX = (posX + width / 2 - leftBottomX) / picWidth;
        float textureRightTopY = (leftBottomY - poxY + height / 2) / picHeight;

        if (textureLeftBottomX < 0) {
            textureLeftBottomX = 0;
        }
        if (textureLeftBottomY < 0) {
            textureLeftBottomY = 0;
        }
        if (textureRightTopX > 1) {
            textureRightTopX = 1;
        }
        if (textureRightTopY > 1) {
            textureRightTopY = 1;
        }
        return new HVECut(textureLeftBottomX, textureLeftBottomY, textureRightTopX, textureRightTopY, width / 2,
            height / 2);
    }

    public static int[] getImageWidthAndHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return new int[] {options.outWidth, options.outHeight};
    }

}