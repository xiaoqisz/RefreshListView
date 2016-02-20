package org.itheima19.library;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
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
    private static final String TAG                   = "RefreshListView";
    private static final int    STATE_PULL_DOWN       = 0;
    private static final int    STATE_RELEASE_REFRESH = 1;
    private static final int    STATE_REFRESHING      = 2;
    private float mDownY;
    private int   mRefreshHeight;
    private View  mRefreshHeader;

    private int mCurrentState = STATE_PULL_DOWN;//记录刷新的状态

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
        mRefreshHeader = View.inflate(getContext(), R.layout.refresh_header, null);
        this.addHeaderView(mRefreshHeader);

        //measure(width,height) ---> layout ---> draw

        //希望看不到刷新的头,如果用户手势拖动时才可以看到
        //隐藏头
        mRefreshHeader.measure(0, 0);
        mRefreshHeight = mRefreshHeader.getMeasuredHeight();
        Log.d(TAG, "refreshHeight : " + mRefreshHeight);
        int top = -mRefreshHeight;
        mRefreshHeader.setPadding(0, top, 0, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                // 拖动时

                float moveY = ev.getY();

                float diffY = moveY - mDownY;


                //当第0个可见时，用户是由上往下拉动时(diffY > 0)，需要刷新头可见
                int firstVisiblePosition = this.getFirstVisiblePosition();
                if (diffY > 0 && firstVisiblePosition == 0) {
                    // 需要刷新头可见
                    int top = (int) (diffY - mRefreshHeight + 0.5f);
                    mRefreshHeader.setPadding(0, top, 0, 0);

                    //                    Log.d(TAG, "设置paddingTop的值 : " + top);

                    //当用户拉到到某个临界点时，显示为释放刷新
                    if (top >= 0 && mCurrentState != STATE_RELEASE_REFRESH) {
                        //如果不是释放刷新的状态时才可以变为释放刷新
                        // 显示为释放刷新
                        Log.d(TAG,"显示为释放刷新");
                        mCurrentState = STATE_RELEASE_REFRESH;
                        // 根据状态改变 UI TODO:

                    } else if(top < 0 && mCurrentState != STATE_PULL_DOWN) {
                        //当用户没有超过某个临界点时，显示为下拉刷新
                        Log.d(TAG,"显示为下拉刷新");
                        mCurrentState = STATE_PULL_DOWN;

                        // 根据状态改变 UI TODO:
                    }


                }

                break;
            case MotionEvent.ACTION_UP:

                // TODO: 松开时正在刷新
                break;
        }


        return super.onTouchEvent(ev);
    }
}
