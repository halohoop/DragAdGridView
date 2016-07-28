package com.halohoop.draggableadgridview.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 屏幕工具
 * Created by halohoop on 2016/4/18.
 */
public class ScreenUtils {

    @SuppressLint("NewApi")//getSize(方法)需要 api13才能使用
    /**
     * 通过返回值point拿宽高
     * point.x 屏幕的宽
     * point.y 屏幕的高
     */
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point out = new Point();
        //Build.VERSION_CODES.HONEYCOMB_MR2 → 13
        if (Build.VERSION.SDK_INT >= 13) {
            display.getSize(out);
        } else {
            int width = display.getWidth();
            int height = display.getHeight();
            out.set(width, height);
        }
        return out;
    }

    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
        float hh = 800f;//这里设置高度为800f  
        float ww = 480f;//这里设置宽度为480f  
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;//be=1表示不缩放  
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
    }

    private static Bitmap compressImage(Bitmap image) {
        if (image == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 20, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 20;//每次都减少20
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;
    }

    /**
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer
                        .parseInt(localClass.getField("status_bar_height")
                                .get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 根据屏幕宽度与密度计算GridView显示的列数， 最少为三列，并获取Item宽度
     */
    public static int getImageItemWidth(Activity activity) {
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int densityDpi = activity.getResources().getDisplayMetrics().densityDpi;
        int cols = screenWidth / densityDpi;
        cols = cols < 3 ? 3 : cols;
        int columnSpace = (int) (2 * activity.getResources().getDisplayMetrics().density);
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }

    /**
     * dp值转化成px值
     *
     * @param ctx
     * @param dpValue
     * @return
     */
    public static int dp2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue / scale + 0.5f);
    }

    public static int px2dp(Context ctx, float pxValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue * scale + 0.5f);
    }

    /**
     * 必须在setContentView之前调用
     *
     * @param act
     */
    public static void hideSoftNavigabar(Activity act) {
        Window window = act.getWindow();
        WindowManager.LayoutParams tmpParams = window.getAttributes();
        tmpParams.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
//        tmpParams.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;//会导致华为手机不响应第一次响应
        window.setAttributes(tmpParams);
    }

    /**
     * 判断底部是否有虚拟按钮
     *
     * @param context
     * 传入一个大小为1的数组存高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean hasSoftKeys(Context context, int[] softKeysHeight) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display d = windowManager.getDefaultDisplay();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        if (softKeysHeight != null) {
            if (softKeysHeight.length > 1) {
                throw new IllegalArgumentException("请传入一个大小为1的int数组或者null");
            }
            softKeysHeight[0] = realHeight - displayHeight;
        }
        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * 这个方法能拿到魅族的虚拟键盘高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
}
