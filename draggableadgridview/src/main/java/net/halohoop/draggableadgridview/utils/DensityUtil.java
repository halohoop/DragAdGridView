package net.halohoop.draggableadgridview.utils;

import android.content.Context;

/**
 * Created by halohoop on 2016/5/28.
 */
public class DensityUtil {
    /**
     * 汉字
     */
    public static final int CHINESE = 0x000001;

    /**
     * 数字或字符
     */
    public static final int NUMBER_OR_CHARACTER = 0x000002;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转成px
     *
     * @param spValue
     * @param type
     * @return
     */
    public static float sp2px(Context context, float spValue, int type) {
        final float scale = context.getResources().getDisplayMetrics().density;
        switch (type) {
            case CHINESE:
                return spValue * scale;
            case NUMBER_OR_CHARACTER:
                return spValue * scale * 10.0f / 18.0f;
            default:
                return spValue * scale;
        }
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param context
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
