package net.halohoop.draggableadgridview.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import net.halohoop.draggableadgridview.utils.DensityUtil;
import net.halohoop.draggableadgridview.utils.ScreenUtils;
import net.halohoop.draggableadgridview.views.AdBarFrameLayout;
import net.halohoop.draggableadgridview.views.DraggableAdGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pooholah on 2016/5/28.
 */
public abstract class BaseDraggableAdAdapter<T> extends BaseAdapter {
    private List<T> mDataList = new ArrayList<>();
    private List<T> mUserDataList;
    protected int mHidePosition = -1;
    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_ADBAR = 1;
    private static final int VIEW_TYPE_EMPTY = 2;
    private View emptyView;
    private Context mContext;
    private DraggableAdGridView mDraggableAdGridView;
    private AdBarFrameLayout mAdBarFrameLayout;
    private int mScreenWidth;

    public BaseDraggableAdAdapter(Context context, DraggableAdGridView draggableAdGridView, List<T> dataList) {
        this.mDraggableAdGridView = draggableAdGridView;
        addAllAndFixNullItemInDataList(dataList);
        this.mContext = context;
        mScreenWidth = ScreenUtils.getScreenSize(mContext).x;
    }

    public void addAllAndFixNullItemInDataList(List<T> dataList) {
        mUserDataList = dataList;
        this.mDataList.clear();
        mDataList.addAll(dataList);
//        this.mDataList = dataList;
//        插入空，空出一行给广告条
//        empty a raw for ad bar
        int adBarItemPostion = mDraggableAdGridView.getAdBarItemPostion();
        int numColumns = mDraggableAdGridView.getmNumColumns();
        for (int i = 0; i < numColumns; i++) {
            mDataList.add(adBarItemPostion, null);
        }
    }

    public void swapItems(int oldPosition, int newPosition) {
        boolean forwardOrNot = oldPosition < newPosition;
        //是否夸广告条
        boolean isCrossRegiion = false;//标识是否跨区域,也就是是否夸广告条
        if (forwardOrNot) {//上移到下 或者 左移到右
            if (oldPosition < mDraggableAdGridView.getAdBarItemPostion()
                    && newPosition > mDraggableAdGridView.getAdBarLastEmptyItemPostion()) {
                isCrossRegiion = true;
            } else {
                isCrossRegiion = false;
            }
        } else {//下移到上 或者 右移到左
            if (newPosition < mDraggableAdGridView.getAdBarItemPostion()
                    && oldPosition > mDraggableAdGridView.getAdBarLastEmptyItemPostion()) {
                isCrossRegiion = true;
            } else {
                isCrossRegiion = false;
            }
        }
        if (!isCrossRegiion) {
            if (forwardOrNot) {//oldPosition < newPosition;
                for (int i = oldPosition; i < newPosition; i++) {
                    int leftIndex = i;
                    int rightIndex = i + 1;
                    swapDataList(leftIndex, rightIndex, forwardOrNot);
                    andSwapUserListToo(leftIndex, rightIndex);
                }
            } else {
                for (int i = oldPosition; i > newPosition; i--) {
                    Collections.swap(mDataList, i, i - 1);
                    andSwapUserListToo(i, i - 1);
                }
            }
        } else {//跨区域 跨广告条
//            LogUtils.i("跨区域");
            //拿到广告条item前一个item index
            int beforeAdBarIndex = mDraggableAdGridView.getAdBarItemPostion() - 1;
            //拿到广告条哪一行最后一个item的后一个item index
            int afterAdBarIndex = mDraggableAdGridView.getAdBarLastEmptyItemPostion() + 1;
            if (forwardOrNot) {//oldPosition < newPosition;
                if (oldPosition < beforeAdBarIndex) {
                    //先交换广告条以前的
                    for (int i = oldPosition; i < beforeAdBarIndex; i++) {
                        int leftIndex = i;
                        int rightIndex = i + 1;
                        swapDataList(leftIndex, rightIndex, forwardOrNot);
                        andSwapUserListToo(leftIndex, rightIndex);
                    }
                    //交换一次跨区域相邻的
                    swapDataList(beforeAdBarIndex, afterAdBarIndex, forwardOrNot);
                    andSwapUserListToo(beforeAdBarIndex, afterAdBarIndex);
                    //交换广告条以后的
                    for (int i = afterAdBarIndex; i < newPosition; i++) {
                        int leftIndex = i;
                        int rightIndex = i + 1;
                        swapDataList(leftIndex, rightIndex, forwardOrNot);
                        andSwapUserListToo(leftIndex, rightIndex);
                    }
                } else {//当前拖动的就是广告条前一个item
                    //先换一次
                    swapDataList(oldPosition, afterAdBarIndex, forwardOrNot);
                    andSwapUserListToo(oldPosition, afterAdBarIndex);
                    //然后在换广告条以后的
                    for (int i = afterAdBarIndex; i < newPosition; i++) {
                        int leftIndex = i;
                        int rightIndex = i + 1;
                        swapDataList(leftIndex, rightIndex, forwardOrNot);
                        andSwapUserListToo(leftIndex, rightIndex);
                    }
                }
            } else {//下移到上 或者 右移到左
                //TODO
                //先移动广告条以后的
                for (int i = oldPosition; i > afterAdBarIndex; i--) {
                    int rightIndex = i;
                    int leftIndex = i - 1;
                    Collections.swap(mDataList, leftIndex, rightIndex);
                    andSwapUserListToo(leftIndex, rightIndex);
                }
                //交换移动相邻广告条的两个
                Collections.swap(mDataList, beforeAdBarIndex, afterAdBarIndex);
                andSwapUserListToo(beforeAdBarIndex, afterAdBarIndex);
                //再移动广告条以前的
                for (int i = beforeAdBarIndex; i > newPosition; i--) {
                    int rightIndex = i;
                    int leftIndex = i - 1;
                    Collections.swap(mDataList, leftIndex, rightIndex);
                    andSwapUserListToo(leftIndex, rightIndex);
                }
            }
        }
    }

    private void swapDataList(int oldPosition, int newPosition, boolean forwardOrNot) {
        if (forwardOrNot) {//oldPosition < newPosition
            if (newPosition < mDraggableAdGridView.getAdBarItemPostion()) {
                //都在在广告条item以前的
                Collections.swap(mDataList, oldPosition, newPosition);
            }
//            else if (oldPosition < mDraggableAdGridView.getAdBarItemPostion()
//                    && newPosition > mDraggableAdGridView.getAdBarLastEmptyItemPostion()) {
//                //夸广告条item
//                Collections.swap(mDataList, oldPosition, newPosition);
//            }
            else if (oldPosition > mDraggableAdGridView.getAdBarLastEmptyItemPostion()) {
                //都在在广告条item以后的
                Collections.swap(mDataList, oldPosition, newPosition);
            }
        } else {//oldPosition >= newPosition
            if (oldPosition < mDraggableAdGridView.getAdBarItemPostion()) {
                //都在在广告条item以前的
                Collections.swap(mDataList, newPosition, oldPosition);
            }
//            else if (newPosition < mDraggableAdGridView.getAdBarItemPostion()
//                    && oldPosition > mDraggableAdGridView.getAdBarLastEmptyItemPostion()) {
//                //夸广告条item
//                Collections.swap(mDataList, newPosition, oldPosition);
//            }
            else if (newPosition > mDraggableAdGridView.getAdBarLastEmptyItemPostion()) {
                //都在在广告条item以后的
                Collections.swap(mDataList, newPosition, oldPosition);
            }
        }
    }

    /**
     * 此方法仅做 集合中 两个 对象的替换
     *
     * @param oldPosition
     * @param newPosition
     */
    private void andSwapUserListToo(int oldPosition, int newPosition) {
        if (newPosition < mDraggableAdGridView.getAdBarItemPostion()) {
            //都在在广告条item以前的
            Collections.swap(mUserDataList, oldPosition, newPosition);
        } else if (oldPosition > mDraggableAdGridView.getAdBarLastEmptyItemPostion()) {
            //都在在广告条item以后的
            Collections.swap(mUserDataList, oldPosition - mDraggableAdGridView.getmNumColumns(),
                    newPosition - mDraggableAdGridView.getmNumColumns());
        } else if (oldPosition < mDraggableAdGridView.getAdBarItemPostion()
                && newPosition > mDraggableAdGridView.getAdBarLastEmptyItemPostion()) {//跨广告条
            Collections.swap(mUserDataList, oldPosition,
                    newPosition - mDraggableAdGridView.getmNumColumns());
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;//有三种布局，
    }

    @Override
    public int getItemViewType(int position) {
        //广告条item布局的位置
        //ad bar position
        int adBarItemPostion = mDraggableAdGridView.getAdBarItemPostion();
        int adBarLastEmptyItemPostion = mDraggableAdGridView.getAdBarLastEmptyItemPostion();
        if (position == adBarItemPostion) {
            return VIEW_TYPE_ADBAR;
        } else if (position > adBarItemPostion && position <= adBarLastEmptyItemPostion) {
            //广告条 哪一行 其他 空view 的位置
            // item position right of ad bar
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        if (mDataList != null)
            return mDataList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        switch (getItemViewType(position)) {
            case VIEW_TYPE_NORMAL:
                if (position >= mDraggableAdGridView.getAdBarItemPostion()) {
                    view = getView4NormalIcon(position - mDraggableAdGridView.getNumColumns());
                } else {//广告条item以前的position是没有往后移动的
                    view = getView4NormalIcon(position);
                }
                break;
            case VIEW_TYPE_ADBAR:
                view = getView4AdVpFirst();
                break;
            case VIEW_TYPE_EMPTY:
                view = getView4InvisibleView(parent);
                break;
            default:
                view = getView4InvisibleView(parent);
                break;
        }
        return view;
    }

    /**
     * 子类定义广告布局
     * son to implement for ad bar layout view
     *
     * @return
     */
    protected View getView4AdVpFirst() {
        if (mAdBarFrameLayout == null) {
            mAdBarFrameLayout = new AdBarFrameLayout(mContext, mDraggableAdGridView, null);
            mAdBarFrameLayout.setTag("HalohoopAdContainerMark");
            mAdBarFrameLayout.addView(getView4AdVp());
        }
        return mAdBarFrameLayout;
    }

    /**
     * 子类定义广告布局
     * son to implement for ad bar layout view
     *
     * @return
     */
    protected abstract View getView4AdVp();

    /**
     * 广告条意外的每一个普通item布局
     * son to implement for normal gridview item view
     */
    protected abstract View getView4NormalIcon(int position);

    private View getView4InvisibleView(ViewGroup parent) {
        float adbarHeight = mDraggableAdGridView.getAdbarHeight();
        int adbarHeightDip2px = DensityUtil.dip2px(mContext, adbarHeight);
        if (emptyView == null) {
            emptyView = new View(parent.getContext());
            emptyView.setTag("HalohoopEmptyMark");//mark this view as a empty view
            emptyView.setBackgroundColor(
                    android.graphics.Color.parseColor("#00000000"));// transparent
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                    mScreenWidth / mDraggableAdGridView.getmNumColumns(),
                    adbarHeightDip2px);
            emptyView.setLayoutParams(params);
            emptyView.setVisibility(View.INVISIBLE);
        } else {
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                    mScreenWidth / mDraggableAdGridView.getmNumColumns(),
                    adbarHeightDip2px);
            emptyView.setLayoutParams(params);
        }
        return emptyView;
    }

    public void setHidePositionAndNotify(int hidePosition) {
        if (hidePosition < mDraggableAdGridView.getAdBarItemPostion())
            this.mHidePosition = hidePosition;
        else
            this.mHidePosition = hidePosition - mDraggableAdGridView.getmNumColumns();
        this.notifyDataSetInvalidated();
    }

    @Override
    public void notifyDataSetChanged() {
        mAdBarFrameLayout.requestLayout();
        super.notifyDataSetChanged();
    }
}
