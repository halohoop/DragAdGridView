package com.halohoop.draggableadgridview.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.halohoop.draggableadgridview.utils.DensityUtil;
import com.halohoop.draggableadgridview.utils.ScreenUtils;


public class AdBarFrameLayout extends FrameLayout {
    private DraggableAdGridView mDraggableAdGridView;
    private Context context;

    private AdBarFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    private AdBarFrameLayout(Context context) {
        this(context, null);
    }

    private AdBarFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context
     * @param draggableAdGridView
     * @param obj 用于区分两个参数的构造器，使用的时候传一个null进来就好了
     */
    public AdBarFrameLayout(Context context, DraggableAdGridView draggableAdGridView, Object obj) {
        this(context);
        this.mDraggableAdGridView = draggableAdGridView;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = ScreenUtils.getScreenSize(context).x;
        int expandSpecWidth = MeasureSpec.makeMeasureSpec(width,
                MeasureSpec.EXACTLY);
        int expandSpecHeight = MeasureSpec.makeMeasureSpec(
                DensityUtil.dip2px(getContext(), mDraggableAdGridView.getAdbarHeight()), MeasureSpec.EXACTLY);
        super.onMeasure(expandSpecWidth, expandSpecHeight);
    }
}
