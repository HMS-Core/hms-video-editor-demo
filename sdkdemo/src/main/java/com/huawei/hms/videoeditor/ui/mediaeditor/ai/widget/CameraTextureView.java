
package com.huawei.hms.videoeditor.ui.mediaeditor.ai.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class CameraTextureView extends TextureView {

    public CameraTextureView(Context context) {
        this(context, null);
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setClickable(true);
    }
}
