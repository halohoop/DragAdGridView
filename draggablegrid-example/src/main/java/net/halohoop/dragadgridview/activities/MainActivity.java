package net.halohoop.dragadgridview.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import net.halohoop.dragadgridview.R;
import net.halohoop.dragadgridview.beans.DataBean;
import net.halohoop.draggableadgridview.utils.ToastSingle;
import net.halohoop.draggableadgridview.views.DraggableGridView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DraggableGridView dgv;
    private List<DataBean> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dgv = (DraggableGridView) findViewById(R.id.dgv);
        dgv.setmAllowAnimation(true);//允许动画
        mDataList = new ArrayList<>();
        new AsyncTask<Integer, Integer, String>() {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                ToastSingle.showToast(getBaseContext(), "结束5\"等待");
                dgv.setAdapter(new MyDraggableAdapter(mDataList));
                LayoutAnimationController lac = new LayoutAnimationController(
                        AnimationUtils.loadAnimation(MainActivity.this, R.anim.main_item_anim));
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
                    int drawableId = MainActivity.this.getResources().getIdentifier(s,
                            "drawable", MainActivity.this.getPackageName());
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

    private class MyDraggableAdapter extends DraggableGridView.DraggableAdapter<DataBean> {

        public MyDraggableAdapter(List<DataBean> dataList) {
            super(dataList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView tv = (TextView) view.findViewById(R.id.tv_item);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_item);
            DataBean dataBean = mDataList.get(position);
            tv.setText(dataBean.getName());
            iv.setImageDrawable(MainActivity.this.getResources().getDrawable(dataBean.getResourceId()));
            if (mHidePosition == position) {
                view.setVisibility(View.INVISIBLE);
            }
            return view;
        }
    }

}
