package com.halohoop.dragadgridview.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.halohoop.dragadgridview.beans.DataBean;
import com.halohoop.draggableadgridview.adapters.BaseDraggableAdAdapter;
import com.halohoop.draggableadgridview.utils.LogUtils;
import com.halohoop.draggableadgridview.utils.ToastSingle;
import com.halohoop.draggableadgridview.views.AdBarFrameLayout;
import com.halohoop.draggableadgridview.views.DraggableAdGridView;

import java.util.ArrayList;
import java.util.List;

public class MainAdActivity extends AppCompatActivity {

    private DraggableAdGridView dgv;
    private List<DataBean> mDataList;
    private MyDraggableAdapter myDraggableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.halohoop.dragadgridview.R.layout.activity_ad_main);
        dgv = (DraggableAdGridView) findViewById(com.halohoop.dragadgridview.R.id.dgv);
        dgv.setAllowSwapAnimation(true);//允许动画 allow movement animation
        mDataList = new ArrayList<>();
        new AsyncTask<Integer, Integer, String>() {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                ToastSingle.showToast(getBaseContext(), "结束5\"等待");
                myDraggableAdapter = new MyDraggableAdapter(mDataList);
                dgv.setAdapter(myDraggableAdapter);
//                animateGridIn();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(5000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dgv.setAdbarShowOrHide(false);
                            }
                        });
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(10000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dgv.setAdbarShowOrHide(true);
                            }
                        });
                    }
                }).start();
            }

            @Override
            protected String doInBackground(Integer... params) {
                for (int i = 0; i < 26; i++) {
                    String s = "aa" + (char) (97 + i);
                    int drawableId = MainAdActivity.this.getResources().getIdentifier(s,
                            "drawable", MainAdActivity.this.getPackageName());
                    DataBean dataBean = new DataBean();
                    dataBean.setName(s);
                    dataBean.setResourceId(drawableId);
                    mDataList.add(dataBean);
                }
                SystemClock.sleep(100);
                return null;
            }

            @Override
            protected void onPreExecute() {
                ToastSingle.showToast(getBaseContext(), "开始5\"等待");
                super.onPreExecute();
            }
        }.execute();
    }

    private void animateGridIn() {
        LayoutAnimationController lac = new LayoutAnimationController(
                AnimationUtils.loadAnimation(MainAdActivity.this, com.halohoop.dragadgridview.R.anim.main_item_anim));
        // 设置顺序
//                lac.setOrder(LayoutAnimationController.ORDER_RANDOM);
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        // 设置一个布局动画
        dgv.setLayoutAnimation(lac);
        // 开启动画
        dgv.startLayoutAnimation();
    }

    private class MyDraggableAdapter extends BaseDraggableAdAdapter<DataBean> {

        private ViewPager mVpAd;
        private View mAdBarContainer;

        public MyDraggableAdapter(List<DataBean> dataList) {
            super(MainAdActivity.this, dgv, dataList);
        }

        @Override
        protected View getView4AdVp() {
            //TODO add your own ad bar view here,maybe ViewPager
            if (mAdBarContainer == null) {
                mAdBarContainer = View.inflate(MainAdActivity.this, com.halohoop.dragadgridview.R.layout.ad_bar_item, null);
                mVpAd = (ViewPager) mAdBarContainer.findViewById(com.halohoop.dragadgridview.R.id.vp_ad);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<String> texts = new ArrayList<>();
                    texts.add("aaa1");
                    texts.add("aaa2");
                    texts.add("aaa3");
                    texts.add("aaa4");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mVpAd.setAdapter(new MyPagerAdapter(texts));
                        }
                    });
                }
            }).start();
            return mAdBarContainer;
        }

        @Override
        protected View getView4NormalIcon(int position) {
            //TODO add your own grid item view here
            View view = View.inflate(MainAdActivity.this, com.halohoop.dragadgridview.R.layout.item, null);
            TextView tv = (TextView) view.findViewById(com.halohoop.dragadgridview.R.id.tv_item);
            ImageView iv = (ImageView) view.findViewById(com.halohoop.dragadgridview.R.id.iv_item);
            DataBean dataBean = mDataList.get(position);
            if (dataBean == null) {
                LogUtils.d("null");
            }
            tv.setText(dataBean.getName());
            iv.setImageDrawable(MainAdActivity.this.getResources().getDrawable(dataBean.getResourceId()));
            if (mHidePosition == position) {
                view.setVisibility(View.INVISIBLE);
            }
            return view;
        }
    }

    private View createView(String text, String colorHex) {
        TextView textView = new TextView(MainAdActivity.this);
        textView.setText(text);
        textView.setBackgroundColor(android.graphics.Color.parseColor(colorHex));

        textView.setLayoutParams(new AdBarFrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    class MyPagerAdapter extends PagerAdapter {
        private List<String> texts = null;

        public MyPagerAdapter(List<String> texts) {
            this.texts = texts;
        }

        @Override

        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            if (position % 2 == 0) {
                view = createView(texts.get(position), "#3300ff00");
            } else {
                view = createView(texts.get(position), "#33ff0000");
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return texts.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
