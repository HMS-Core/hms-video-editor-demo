
package com.huawei.videoeditorkit.videoeditdemo.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class SizeUtils {
    /**
     * DP to PX units conversion
     *
     * @param context Context Object
     * @param dp DP value
     * @return PX value
     */
    public static int dp2Px(Context context, float dp) {
        Resources resources = context.getResources();
        if (resources == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (displayMetrics == null) {
            return 0;
        }
        float scale = displayMetrics.density;
        return (int) (dp * scale + 0.5f);
    }
}
