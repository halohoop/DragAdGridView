package net.halohoop.draggableadgridview.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import net.halohoop.draggableadgridview.R;
import net.halohoop.draggableadgridview.adapters.BaseDraggableAdAdapter;
import net.halohoop.draggableadgridview.utils.LogUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 有广告的draggableGridView
 * Created by Pooholah on 2016/4/2.
 */
public class DraggableAdGridView extends GridView {

    /**
     * 上下文环境
     */
    private Context mContext;

    /**
     * 拖动的view创建出来的bitmap
     */
    private Bitmap mDrawingCacheBitmap;
    /**
     * 屏幕最上面的sstatusbar的高度，在构造函数中赋值
     */
    private int mStatusHeight;
    /**
     * 被拖动的imageview控件，使用WindowManager添加到屏幕中;
     */
    private ImageView mDragImageView;
    /**
     * down事件长按后获取到的item控件；
     */
    private View mDragDownTouchView;
    /**
     * 用于添加拖动控件
     */
    private WindowManager mWindowManager;

    /**
     * 可能有margin等一些宽度
     */
    private int mDragViewOffsetX = 0;
    /**
     * 可能title以及有其他的控件的高度
     */
    private int mDragViewOffsetY = 0;
    private static LayoutInflater mInflater;
    /**
     * item间的分割线的颜色，默认值是448e8e8e
     */
    private int crossLineColor = 0;
    private static final int DEFAULT_DRAGVIEW_BGCOLOR = android.graphics.Color.parseColor("#55000000");
    private Paint mPaint;
    private WindowManager.LayoutParams mLayoutParams;
    private int mTopViewOffset;
    private int mLeftViewOffset;
    private int mDownPosition;
    private boolean mAllowAnimation = false;
    private Handler mHandler = new Handler();
    /**
     * 长按时候等待的时间
     */
    private long mLongPressMills = 1000;
    /**
     * 长按任务
     */
    private CreateAndHideDragView mLongPressPostRun;
    private Vibrator mVibrator;
    /**
     * 长按开始拖动时候震动提醒用户的时间，默认50毫秒
     */
    private long mVibratorMills = 50;
    /**
     * 默认广告条高度是 100
     * by default the height of ad bar is 100
     */
    private float mAdbarHeight = 100;
    private float mTmpAdbarHeight = 100;
    private int mAdBarRawPostion = 3;
    private int mAdBarItemPostion = 0;
    private int mAdBarLastEmptyItemPostion = 0;
    private int mNumColumns;
    /**
     * 拖动时候GV上下滚动的速度
     */
    private final int speed = 20;
    private int moveY;

    private int mUpScrollBorder;
    private int mDownScrollBorder;
    /**
     */
    private Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            int scrollY;
//            if (getFirstVisiblePosition() == 0
//                    || getLastVisiblePosition() == getCount() - 1) {
//                mHandler.removeCallbacks(mScrollRunnable);
//                return;
//            }

            if (moveY > mDownScrollBorder) {//上滚,看下面部分
                LogUtils.i("上滚,看下面部分 -speed:" + speed);
                if (getLastVisiblePosition() == getCount() - 1
                        && getChildAt(getChildCount() - 1).getBottom() >= 0) {
                    View lastChild = getChildAt(getChildCount() - 1);
                    int bottom = lastChild.getBottom();
                    int height = getHeight();
                    int distance = bottom - height;
                    LogUtils.i("distance:" + distance);
//                    int lastChildHeight = lastChild.getHeight();
                    if (distance <= 1) {//最后一个已完全出来
                        mHandler.removeCallbacks(mScrollRunnable);
                        return;
                    }
                }
                scrollY = speed;
                mHandler.postDelayed(mScrollRunnable, 100);
                // mHandler.post(mScrollRunnable);
            } else if (moveY < mUpScrollBorder) {//下滚,看上面部分
                LogUtils.i("下滚,看上面部分 speed:" + speed);
                if (getFirstVisiblePosition() == 0) {
                    int top = getChildAt(0).getTop();
                    if (top >= 0) {
                        mHandler.removeCallbacks(mScrollRunnable);
                        return;
                    }
                }
                scrollY = -speed;
                mHandler.postDelayed(mScrollRunnable, 100);
                // mHandler.post(mScrollRunnable);
            } else {
                mHandler.removeCallbacks(mScrollRunnable);
                return;
            }
            if (0 != scrollY)
                smoothScrollBy(scrollY, 10);
        }
    };
    private int mDragViewBgColor;
    private float mDragViewBgAlpha = 1.0f;

    public boolean ismAllowAnimation() {
        return mAllowAnimation;
    }

    public long getmLongPressMills() {
        return mLongPressMills;
    }

    public void setmLongPressMills(long mLongPressMills) {
        this.mLongPressMills = mLongPressMills;
    }

    public long getmVibratorMills() {
        return mVibratorMills;
    }

    public void setmVibratorMills(long mVibratorMills) {
        this.mVibratorMills = mVibratorMills;
    }

    public void setAllowSwapAnimation(boolean allowAnimation) {
        this.mAllowAnimation = allowAnimation;
    }

    public void setCrossLineColor(int resId) {
//        crossLineColor = mContext.getResources().getColor(android.R.color.darker_gray);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            crossLineColor = mContext.getResources().getColor(resId, null);
        } else {
            crossLineColor = mContext.getResources().getColor(resId);
        }
    }

    public DraggableAdGridView(Context context) {
        this(context, null);
    }

    public DraggableAdGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraggableAdGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //解析attrs自定义属性
        //read custom attrs set
        TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.DraggableAdGridViewAdBar);
        mAdbarHeight = attributes.getDimension(R.styleable.DraggableAdGridViewAdBar_adbar_height, 100);
        mTmpAdbarHeight = mAdbarHeight;
        mAdBarRawPostion = attributes.getInteger(R.styleable.DraggableAdGridViewAdBar_adbar_rawposition, 3);
        mNumColumns = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "numColumns", -1);
        mAdBarItemPostion = mAdBarRawPostion * mNumColumns;
        mAdBarLastEmptyItemPostion = mAdBarItemPostion + mNumColumns - 1;
        crossLineColor = attributes.getColor(R.styleable.DraggableAdGridViewAdBar_crossline_color,
                context.getResources().getColor(android.R.color.darker_gray));
        mDragViewBgColor = attributes.getColor(R.styleable.DraggableAdGridViewAdBar_drag_view_bg_color,
                DEFAULT_DRAGVIEW_BGCOLOR);
        mDragViewBgAlpha = attributes.getFloat(R.styleable.DraggableAdGridViewAdBar_drag_view_bg_alpha, 1.0f);
        //初始化设置
        //initialize
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mVibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        mStatusHeight = getStatusHeight(context);
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mDragImageView = new ImageView(getContext());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(crossLineColor);
        mDragImageView.setBackgroundColor(mDragViewBgColor);
        attributes.recycle();
    }

    /**
     * 表示是否正处在拖动状态
     * 这个变量需要在move事件被使用，防止一些意外的发生
     */
    private boolean isDragging = false;

    public int getAdBarItemPostion() {
        return mAdBarItemPostion;
    }

    public int getAdBarLastEmptyItemPostion() {
        return mAdBarLastEmptyItemPostion;
    }

    private void setAdbarHeight(float mAdbarHeight) {
        this.mAdbarHeight = mAdbarHeight;
    }

    /**
     * please call this method in UIThread;
     *
     * @param true4ShowFalse4Hide
     */
    public void setAdbarShowOrHide(boolean true4ShowFalse4Hide) {
        if (true4ShowFalse4Hide) {
            this.mAdbarHeight = this.mTmpAdbarHeight;
        } else {
            this.mAdbarHeight = 0;
        }

        BaseAdapter adapter = (BaseAdapter) getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public float getAdbarHeight() {
        return mAdbarHeight;
    }

    public int getmNumColumns() {
        return mNumColumns;
    }

    /**
     * 长按添加复制view，并且将item隐藏invisible
     */
    private class CreateAndHideDragView implements Runnable {

        private int left;
        private int top;

        public CreateAndHideDragView(int left, int top) {
            this.left = left;
            this.top = top;
        }

        @Override
        public void run() {
            createCopyOfDownTouchView(mDragDownTouchView,
                    left, top);
            mDragDownTouchView.setVisibility(View.INVISIBLE);
            mVibrator.vibrate(mVibratorMills);
            isDragging = true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        ToastSingle.showToast(getContext(), "halohoop" + ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int downX = (int) ev.getX();
                int downY = (int) ev.getY();
                int downRawX = (int) ev.getRawX();
                int downRawY = (int) ev.getRawY();
                mDragViewOffsetX = downRawX - downX;
                mDragViewOffsetY = downRawY - downY;//其中包含了状态栏的高度，所以需要减去状态栏的高度
                mDragViewOffsetY = mDragViewOffsetY - mStatusHeight;
//                ToastSingle.showToast(getContext(),""+downX+" "+downY);
                mDownPosition = pointToPosition(downX, downY);
                if (!isBetweenAdBarPosition(mDownPosition)) {
                    mDragDownTouchView = getChildAt(mDownPosition - getFirstVisiblePosition());
                    if (mDragDownTouchView == null) {
                        return super.dispatchTouchEvent(ev);
                    }
                    mDragDownTouchView.setBackgroundColor(android.graphics.Color.parseColor("#338e8e8e"));
                    int top = mDragDownTouchView.getTop();
                    int left = mDragDownTouchView.getLeft();
                    mTopViewOffset = downY - top;
                    mLeftViewOffset = downX - left;
//                ToastSingle.showToast(getContext(), "" + downTouchView);
                    mLongPressPostRun = new CreateAndHideDragView(left, top);
                    mHandler.postDelayed(mLongPressPostRun, mLongPressMills);
                }
                mDownScrollBorder = getHeight() * 4 / 5;
                mUpScrollBorder = getHeight() / 5;
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();
                this.moveY = moveY;
                final int currentMovePosition = pointToPosition(moveX, moveY);
                //如果在移出了区域就取消长按任务
                if (currentMovePosition != mDownPosition) {
                    mHandler.removeCallbacks(mLongPressPostRun);
                }
                if (isDragging) {
                    if (currentMovePosition != mDownPosition && currentMovePosition != AbsListView.INVALID_POSITION
                            && mIsAnimating && !isBetweenAdBarPosition(currentMovePosition)) {
                        BaseDraggableAdAdapter adapter = (BaseDraggableAdAdapter) getAdapter();
                        adapter.swapItems(mDownPosition, currentMovePosition);
                        if (mAllowAnimation) {
                            //                        ToastSingle.showToast(getContext(), "注册Observer");
                            final ViewTreeObserver viewTreeObserver = getViewTreeObserver();
                            viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    //首先删除上一次注册的监听器s
                                    viewTreeObserver.removeOnPreDrawListener(this);
                                    animateItemGroup(mDownPosition, currentMovePosition);
                                    mDownPosition = currentMovePosition;//更新当前的拖拽位置
                                    return true;// 返回true很关键，动画过程保证全部显示出来;
                                }
                            });
                        } else {
                            mDownPosition = currentMovePosition;//更新当前的拖拽位置
                        }
                        adapter.setHidePositionAndNotify(currentMovePosition);
                    }
                    mLayoutParams.x = moveX - mLeftViewOffset;
                    mLayoutParams.y = moveY - mTopViewOffset + mStatusHeight;
                    mWindowManager.updateViewLayout(mDragImageView, mLayoutParams);
                    mHandler.post(mScrollRunnable);
                    return false;
                } else {//当不是拖动状态的时候就处理父类本来该处理的事件，比如滑动
                    return super.dispatchTouchEvent(ev);
                }
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mLongPressPostRun);
                mHandler.removeCallbacks(mScrollRunnable);
                mLongPressPostRun = null;//堆内存释放回收
                if (mDragDownTouchView == null) {
                    return super.dispatchTouchEvent(ev);
                }
//                mDragDownTouchView.setBackgroundColor(android.graphics.Color.parseColor("#ffffffff"));
                mDragDownTouchView.setBackgroundColor(getDrawingCacheBackgroundColor());
                if (mDragImageView != null && mDragImageView.getParent() != null) {
                    mWindowManager.removeView(mDragImageView);
                }
                if (isDragging) {
                    BaseDraggableAdAdapter adapter = (BaseDraggableAdAdapter) getAdapter();
                    adapter.setHidePositionAndNotify(-1);
                    isDragging = false;//重置拖动状态
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isBetweenAdBarPosition(int currentMovePosition) {
        if (currentMovePosition >= mAdBarItemPostion && currentMovePosition <= mAdBarLastEmptyItemPostion) {
            return true;
        }
        return false;
    }

    private StateListDrawable addStateDrawable(Context context, int idNormal, int idPressed, int idFocused) {
        StateListDrawable sd = new StateListDrawable();
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
        Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
        Drawable focus = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
        //注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
        //所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
        sd.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focus);
        sd.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        sd.addState(new int[]{android.R.attr.state_focused}, focus);
        sd.addState(new int[]{android.R.attr.state_pressed}, pressed);
        sd.addState(new int[]{android.R.attr.state_enabled}, normal);
        sd.addState(new int[]{}, normal);
        return sd;
    }

    private void animateItemGroup(int oldPosition, int newPosition) {
        int firstVisiblePosition = getFirstVisiblePosition();
        boolean forward = oldPosition < newPosition;
        List<Animator> animList = new ArrayList<Animator>();
//        ToastSingle.showToast(getContext(), "oldPosition:" + oldPosition + " newPosition:" + newPosition);
        if (forward) {//上往下拖
            for (int i = oldPosition; i < newPosition; i++) {
                if (isBetweenAdBarPosition(i)) {
                    continue;
                }
                View childAt = getChildAt(i - firstVisiblePosition);
                if (childAt != null) {
                    Animator singleItemAnimator = null;
                    if ((i + 1) % getNumColumns() == 0) {//当是每一行的最后一个的时候
                        singleItemAnimator = createSingleItemAnimator(childAt,
                                -childAt.getWidth() * (getNumColumns() - 1), 0, childAt.getHeight(), 0);
                    } else {
                        singleItemAnimator = createSingleItemAnimator(childAt,
                                childAt.getWidth(), 0, 0, 0);
                    }
                    animList.add(singleItemAnimator);
                }
            }
        } else {//从下往上拖
            for (int i = oldPosition; i > newPosition; i--) {
                if (isBetweenAdBarPosition(i)) {
                    continue;
                }
                View childAt = getChildAt(i - firstVisiblePosition);
                if (childAt != null) {
                    Animator singleItemAnimator = null;
                    if ((i % getNumColumns()) == 0) {//当是每一行最后一个的时候
                        singleItemAnimator = createSingleItemAnimator(childAt,
                                childAt.getWidth() * (getNumColumns() - 1), 0, -childAt.getHeight(), 0);
                    } else {
                        singleItemAnimator = createSingleItemAnimator(childAt,
                                -childAt.getWidth(), 0, 0, 0);
                    }
                    animList.add(singleItemAnimator);
                }
            }
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animList);
        animatorSet.setDuration(250);
        animatorSet.setInterpolator(new OvershootInterpolator(0.8f));
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
//                ToastSingle.showToast(getContext(), "anistart");
                mIsAnimating = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                ToastSingle.showToast(getContext(), "aniend");
                mIsAnimating = true;
            }
        });
        animatorSet.start();
    }

    private boolean mIsAnimating = true;

    private Animator createSingleItemAnimator(View view, int startX, int endX, int startY, int endY) {
        ObjectAnimator objAnimX = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
        ObjectAnimator objAnimY = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objAnimX, objAnimY);
        return animatorSet;
    }

    private void createCopyOfDownTouchView(View view, int x, int y) {
        if (mDragImageView.getParent() != null) {
            mWindowManager.removeView(mDragImageView);
        }
        view.setDrawingCacheEnabled(true);
        mDrawingCacheBitmap = view.getDrawingCache();
        if (!mDrawingCacheBitmap.isRecycled()) {//容错处理
            mDragImageView.setImageBitmap(mDrawingCacheBitmap);

            mLayoutParams = new WindowManager.LayoutParams();
            mLayoutParams.format = PixelFormat.TRANSLUCENT;
            mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mLayoutParams.x = view.getLeft() + mDragViewOffsetX;
            mLayoutParams.y = view.getTop() + mDragViewOffsetY;
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            mLayoutParams.alpha = mDragViewBgAlpha;
            mWindowManager.addView(mDragImageView, mLayoutParams);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public int getNumColumns() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            return super.getNumColumns();

        try {
            Field numColumns = getClass().getSuperclass().getDeclaredField("mNumColumns");
            numColumns.setAccessible(true);
            return numColumns.getInt(this);
        } catch (Exception e) {
        }

        int columns = AUTO_FIT;
        if (getChildCount() > 0) {
            int width = getChildAt(0).getMeasuredWidth();
            if (width > 0) columns = getWidth() / width;
        }
        return columns;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int itemWidth = getMeasuredWidth() / getNumColumns();
        int measuredHeight = getMeasuredHeight();
        //画(getNumColumns-1)条竖线
        for (int i = 0; i < (getNumColumns() - 1); i++) {
            float xPosition = itemWidth * (i + 1);
            canvas.drawLine(xPosition, 0, xPosition, measuredHeight, mPaint);
        }
        //画横线
        //算出需要画多少横线
//        ToastSingle.showToast(getContext(), "" + getChildAt(0).getTop());
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            int top = childAt.getTop();
            if (top > 0 && i % getmNumColumns() == 0) {
                Object tag = childAt.getTag();
                if (tag != null) {
                    if (tag instanceof String) {
                        if (TextUtils.equals("HalohoopEmptyMark", (String) tag)) {
                            LogUtils.i("HalohoopEmptyMark");
                        } else if (TextUtils.equals("HalohoopAdContainerMark", (String) tag)) {
                            LogUtils.i("HalohoopAdContainerMark");
                            canvas.drawLine(0, top, getMeasuredWidth(), top, mPaint);
                        } else {
                            canvas.drawLine(0, top, getMeasuredWidth(), top, mPaint);
                        }
                    } else {
                        canvas.drawLine(0, top, getMeasuredWidth(), top, mPaint);
                    }
                } else {
                    canvas.drawLine(0, top, getMeasuredWidth(), top, mPaint);
                }
            }
        }
    }

    private static int getStatusHeight(Context context) {
        int statusHeight;
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

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (!(adapter instanceof BaseDraggableAdAdapter)) {
            throw new IllegalArgumentException("please use a adapter which is a implementation of BaseDraggableAdAdapter");
        }
        super.setAdapter(adapter);
    }
}
