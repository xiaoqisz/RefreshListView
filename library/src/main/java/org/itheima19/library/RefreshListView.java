package org.itheima19.library;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nineoldandroids.animation.ValueAnimator;

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


    private TextView    mTvState;
    private TextView    mTvDate;
    private ImageView   mIvArrow;
    private ProgressBar mPbLoading;

    private RotateAnimation mUp2DownAnim;
    private RotateAnimation mDown2UpAnim;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //初始化刷新的头
        initRfreshHeader();

        mUp2DownAnim = new RotateAnimation(-180,
                                           0,
                                           Animation.RELATIVE_TO_SELF,
                                           0.5f,
                                           Animation.RELATIVE_TO_SELF,
                                           0.5f);
        mUp2DownAnim.setDuration(400);
        mUp2DownAnim.setFillAfter(true);


        mDown2UpAnim = new RotateAnimation(0,
                                           180,
                                           Animation.RELATIVE_TO_SELF,
                                           0.5f,
                                           Animation.RELATIVE_TO_SELF,
                                           0.5f);
        mDown2UpAnim.setDuration(400);
        mDown2UpAnim.setFillAfter(true);
    }

    private void initRfreshHeader() {
        //添加头
        mRefreshHeader = View.inflate(getContext(), R.layout.refresh_header, null);
        this.addHeaderView(mRefreshHeader);

        //findView
        mTvDate = (TextView) mRefreshHeader.findViewById(R.id.refresh_header_tv_date);
        mTvState = (TextView) mRefreshHeader.findViewById(R.id.refresh_header_tv_state);
        mIvArrow = (ImageView) mRefreshHeader.findViewById(R.id.refresh_header_iv_arrow);
        mPbLoading = (ProgressBar) mRefreshHeader.findViewById(R.id.refresh_header_pb_loading);

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
                        Log.d(TAG, "显示为释放刷新");
                        mCurrentState = STATE_RELEASE_REFRESH;
                        // 根据状态改变 UI
                        refreshStateUI();

                    } else if (top < 0 && mCurrentState != STATE_PULL_DOWN) {
                        //当用户没有超过某个临界点时，显示为下拉刷新
                        Log.d(TAG, "显示为下拉刷新");
                        mCurrentState = STATE_PULL_DOWN;

                        // 根据状态改变 UI
                        refreshStateUI();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:

                // 松开时正在刷新

                //如果是释放刷新时松开的，变为正在刷新
                if (mCurrentState == STATE_RELEASE_REFRESH) {
                    //状态改变
                    mCurrentState = STATE_REFRESHING;
                    //UI改变
                    refreshStateUI();
                    //刷新头得刚好完全显示
                    //                    mRefreshHeader.setPadding(0, 0, 0, 0);//结果正确，过程不好

                    int start = mRefreshHeader.getPaddingTop();
                    int end = 0;
                    doHeaderAnimation(start, end);


                } else {
                    //如果是下拉刷新时松开的，
                    //完全隐藏刷新的头
                    // mRefreshHeader.setPadding(0, -mRefreshHeight, 0, 0);
                    int start = mRefreshHeader.getPaddingTop();
                    int end = -mRefreshHeight;
                    doHeaderAnimation(start, end);
                }

                break;
        }


        return super.onTouchEvent(ev);
    }

    private void doHeaderAnimation(int start, int end) {
        //模拟数据变化
        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        long duration = Math.abs(start - end) * 10;
        if (duration > 600) {
            duration = 600;
        }
        animator.setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mRefreshHeader.setPadding(0, value, 0, 0);
            }
        });

        animator.start();
    }

    private void refreshStateUI() {
        switch (mCurrentState) {
            case STATE_PULL_DOWN:
                //下拉刷新
                //文本改变
                mTvState.setText("下拉刷新");
                //箭头动画(由上往下转)
                mPbLoading.setVisibility(View.INVISIBLE);
                mIvArrow.startAnimation(mUp2DownAnim);
                break;
            case STATE_RELEASE_REFRESH:
                //文本改变
                mTvState.setText("释放刷新");
                //箭头动画(由下往上转)
                mPbLoading.setVisibility(View.INVISIBLE);
                mIvArrow.startAnimation(mDown2UpAnim);
                break;
            case STATE_REFRESHING:
                //文本改变
                mTvState.setText("正在刷新");
                //隐藏箭头，显示进度圈
                mIvArrow.clearAnimation();
                mIvArrow.setVisibility(View.INVISIBLE);
                mPbLoading.setVisibility(View.VISIBLE);

                break;
        }

    }
}
