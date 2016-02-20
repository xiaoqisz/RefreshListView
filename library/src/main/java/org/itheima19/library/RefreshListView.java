package org.itheima19.library;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/*
 *  @项目名：  RefreshListView 
 *  @包名：    org.itheima19.library
 *  @文件名:   RefreshListView
 *  @创建者:   Administrator
 *  @创建时间:  2016/2/20 14:35
 *  @描述：    TODO
 */
public class RefreshListView
        extends ListView
{
    private static final String TAG = "RefreshListView";

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //初始化刷新的头
        initRfreshHeader();
    }

    private void initRfreshHeader() {
        //添加头
        View refreshHeader = View.inflate(getContext(), R.layout.refresh_header, null);
        this.addHeaderView(refreshHeader);


        //measure(width,height) ---> layout ---> draw

        //希望看不到刷新的头,如果用户手势拖动时才可以看到
        //隐藏头
        refreshHeader.measure(0, 0);
        int refreshHeight = refreshHeader.getMeasuredHeight();
        Log.d(TAG, "refreshHeight : " + refreshHeight);
        int top = -refreshHeight;
        refreshHeader.setPadding(0, top, 0, 0);
    }


}
