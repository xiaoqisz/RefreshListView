package org.itheima19.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.itheima19.library.RefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends Activity
        implements RefreshListView.OnRefreshListener
{
    private RefreshListView mListView;

    private List<String>   mDatas;
    private RefreshAdapter mAdapter;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        mListView = (RefreshListView) findViewById(R.id.listview);


        mDatas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mDatas.add("数据-" + i);
        }

        mAdapter = new RefreshAdapter();
        mListView.setAdapter(mAdapter);// -->list

        //设置下拉刷新的监听
        mListView.addOnRefreshListener(this);
    }

    @Override
    public void onRefreshing() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mDatas = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    mDatas.add(count + "次刷新后的数据-" + i);
                }
                mAdapter.notifyDataSetChanged();

                count++;
                //让RefreshlistView刷新状态改变
                mListView.refreshFinish();
            }
        }, 2000);
    }

    private class RefreshAdapter
            extends BaseAdapter
    {

        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.tv = (TextView) convertView.findViewById(R.id.item_tv);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String data = mDatas.get(position);

            holder.tv.setText(data);


            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv;
    }


}
