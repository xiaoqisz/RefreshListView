package org.itheima19.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.itheima19.library.RefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends Activity
        implements RefreshListView.OnRefreshListener
{
    private RefreshListView mListView;
    private ViewPager       mPager;

    private int[] pics = new int[]{R.mipmap.pic_1,
                                   R.mipmap.pic_2,
                                   R.mipmap.pic_3,
                                   R.mipmap.pic_4};


    private List<String>   mDatas;
    private RefreshAdapter mAdapter;
    private int mRefreshCount  = 0;
    private int mLoadMoreCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        mListView = (RefreshListView) findViewById(R.id.listview);

        View header = View.inflate(this, R.layout.header, null);
        mPager = (ViewPager) header.findViewById(R.id.viewpager);

        //用listView去添加自定义的头
        mListView.addHeaderView(header);


        //给viewpager 的数据
        mPager.setAdapter(new HeaderAdapter());


        //listView的数据
        mDatas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mDatas.add("数据-" + i);
        }

        mAdapter = new RefreshAdapter();
        mListView.setAdapter(mAdapter);// -->list

        //设置下拉刷新的监听
        mListView.addOnRefreshListener(this);


        //设置listView的点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "点击了条目", Toast.LENGTH_SHORT)
                     .show();
            }
        });

    }

    @Override
    public void onRefreshing() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mDatas = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    mDatas.add(mRefreshCount + "次刷新后的数据-" + i);
                }
                mAdapter.notifyDataSetChanged();

                mRefreshCount++;
                //让RefreshlistView刷新状态改变
                mListView.refreshFinish();


                mLoadMoreCount = 0;
            }
        }, 2000);
    }

    @Override
    public void onLoadingMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //网络翻页的数据---》list
                if (mLoadMoreCount >= 3) {
                    Toast.makeText(MainActivity.this, "没有更多了", Toast.LENGTH_SHORT)
                         .show();

                    mListView.refreshFinish(false);
                } else {
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < 10; i++) {
                        list.add("更多-" + (mDatas.size() + i));
                    }

                    mLoadMoreCount++;

                    mDatas.addAll(list);
                    //ui更新
                    mAdapter.notifyDataSetChanged();

                    //通知加载更多完成 ??为下一次加载更多做准备 TODO:
                    mListView.refreshFinish();
                }

            }
        }, 1500);

    }

    private class HeaderAdapter
            extends PagerAdapter
    {

        @Override
        public int getCount() {
            return pics.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = new ImageView(MainActivity.this);

            iv.setImageResource(pics[position]);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);

            container.addView(iv);

            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
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
