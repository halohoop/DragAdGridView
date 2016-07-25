package net.halohoop.dragadgridview.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import net.halohoop.dragadgridview.R;
import net.halohoop.dragadgridview.beans.DataBean;
import net.halohoop.draggableadgridview.adapters.BaseDraggableAdAdapter;
import net.halohoop.draggableadgridview.utils.LogUtils;
import net.halohoop.draggableadgridview.utils.ToastSingle;
import net.halohoop.draggableadgridview.views.AdBarFrameLayout;
import net.halohoop.draggableadgridview.views.DraggableAdGridView;

import java.util.ArrayList;
import java.util.List;

public class MainAdActivity extends AppCompatActivity {

    private DraggableAdGridView dgv;
    private List<DataBean> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_main);
        dgv = (DraggableAdGridView) findViewById(R.id.dgv);
        dgv.setAllowSwapAnimation(true);//允许动画
        mDataList = new ArrayList<>();
        new AsyncTask<Integer, Integer, String>() {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                ToastSingle.showToast(getBaseContext(), "结束5\"等待");
                dgv.setAdapter(new MyDraggableAdapter(mDataList));
                LayoutAnimationController lac = new LayoutAnimationController(
                        AnimationUtils.loadAnimation(MainAdActivity.this, R.anim.main_item_anim));
                // 设置顺序
//                lac.setOrder(LayoutAnimationController.ORDER_RANDOM);
                lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
                // 设置一个布局动画
                dgv.setLayoutAnimation(lac);
                // 开启动画
                dgv.startLayoutAnimation();
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

    private class MyDraggableAdapter extends BaseDraggableAdAdapter<DataBean> {

        public MyDraggableAdapter(List<DataBean> dataList) {
            super(MainAdActivity.this, dgv, dataList);
        }

        @Override
        protected View getView4AdVp() {
            TextView textView = new TextView(MainAdActivity.this);
            textView.setText("广告条");
            textView.setBackgroundColor(android.graphics.Color.parseColor("#8e8e8e"));

            textView.setLayoutParams(new AdBarFrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setGravity(Gravity.CENTER);
            return textView;
        }

        @Override
        protected View getView4NormalIcon(int position) {
            View view = View.inflate(MainAdActivity.this, R.layout.item, null);
            TextView tv = (TextView) view.findViewById(R.id.tv_item);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_item);
            DataBean dataBean = mDataList.get(position);
            if(dataBean==null){
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

}
