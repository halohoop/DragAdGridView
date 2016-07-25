package net.halohoop.draggableadgridview.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 单例吐司
 * Created by halohoop on 2016/4/2.
 */
public class ToastSingle {
    private static Toast toast;

    public static void showToast(Context context, String string) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(string);
        toast.show();
    }
}
