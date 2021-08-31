
package com.huawei.videoeditorkit.videoeditdemo.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.huawei.videoeditorkit.videoeditdemo.R;
import com.huawei.videoeditorkit.videoeditdemo.util.SizeUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class RoundImage extends AppCompatImageView {
    /**
     * Fillet size. The default value is 10.
     */
    private int mBorderRadius = 0;

    private Paint mPaint;

    /**
     * 3x3 matrix, mainly used for zoom-in and zoom-in
     */
    private Matrix mMatrix;

    /**
     * rendering images, using images to shade drawn graphics
     */
    private BitmapShader mBitmapShader;

    public RoundImage(Context context) {
        this(context, null);
    }

    public RoundImage(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundImage);
        mBorderRadius = (int) ta.getDimension(R.styleable.RoundImage_radius, SizeUtils.dp2Px(context, 0));
        ta.recycle();
        mMatrix = new Matrix();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        Bitmap bitmap = drawableToBitamp(getDrawable());
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if (!(bitmap.getWidth() == getWidth() && bitmap.getHeight() == getHeight())) {
            // If the width or height of the image does not match the width or height of the view,
            // calculate the scaling ratio. The width and height of the zoomed image must be greater
            // than the width and height of the view. So we take the larger value here;
            scale = Math.max(getWidth() * 1.0f / bitmap.getWidth(), getHeight() * 1.0f / bitmap.getHeight());
        }
        // Shader transformation matrix, which is used to zoom in or out.
        mMatrix.setScale(scale, scale);
        // Set the transformation matrix
        mBitmapShader.setLocalMatrix(mMatrix);
        // Setting the shader
        mPaint.setShader(mBitmapShader);
        canvas.drawRoundRect(new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(),
            getHeight() - getPaddingBottom()), mBorderRadius, mBorderRadius, mPaint);
    }

    /**
     * Set Fillet
     */
    public void setBorderRadius(int borderRadius) {
        this.mBorderRadius = borderRadius;
        invalidate();
    }

    private Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        // When the value is not a picture but a color, the width and height of the drawable control are incorrect.
        // When the value is a color, the width and height of the drawable control are incorrectly obtained.
        int w = drawable.getIntrinsicWidth() <= 0 ? getWidth() : drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight() <= 0 ? getHeight() : drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(getPaddingLeft(), getPaddingTop(), w + getPaddingLeft(), h + getPaddingTop());
        drawable.draw(canvas);
        return bitmap;
    }
}
