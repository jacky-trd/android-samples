package com.jikexueyuan.jokes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 下拉刷新的列表
 */
public class PullRefreshView extends LinearLayout implements View.OnTouchListener {

    //列表状态
    private enum ViewStatus {
        STATUS_PULL_TO_REFRESH,
        STATUS_RELEASE_TO_REFRESH,
        STATUS_REFRESHING,
        STATUS_REFRESH_FINISHED
    }

    //当前状态和上一次的状态
    private ViewStatus currentStatus = ViewStatus.STATUS_REFRESH_FINISHED;
    private ViewStatus lastStatus = currentStatus;

    //时间常量（单位是毫秒）
    private final long ONE_MINUTE = 60 * 1000;
    private final long ONE_HOUR = 60 * 60 * 1000;
    private final long ONE_DAY = 24 * 60 * 60 * 1000;
    private final long ONE_MONTH = 30 * 24 * 60 * 60 * 1000;
    private final long ONE_YEAR = 12 * 30 * 24 * 60 * 60 * 1000;

    //列表回滚速度
    private final int SCROLL_SPEED = -20;
    //头部的高度（负数，从而可以隐藏）
    private int refreshAreaHeight;
    //sharedpreferences的tag
    private final String spTag = "update_time";
    //刷新列表
    private View refreshView;
    //笑话列表
    private ListView jokesList;
    //用于显示网络不可用
    private Toast networkToast;
    //环境上下文
    private Context context;
    //头部的刷新箭头
    private ImageView refreshArrow;
    //头部的刷新进度条
    private ProgressBar refreshProgressBar;
    //头部的刷新状态文字
    private TextView statusText;
    //头部的上一次刷新时间
    private TextView lastUpdateTimeText;
    //手指滑动的阈值（超过则空间开始滚动）
    private int touchSlop;
    //sharedpreferences
    private SharedPreferences preferences;
    //是否已经设置好布局的标志
    private boolean haveLayout;
    //layout参数对象（top margin）
    private MarginLayoutParams refreshLayoutParams;
    //是否允许下拉的标志
    private boolean canPull;
    //上一次更新的时间
    private long lastUpdateTime;
    //手指按下时的纵坐标
    private float downY;
    //刷新时的回调函数
    private RefreshListener refreshListener;

    /*下拉刷新的监听器，使用下拉刷新的地方应该注册此监听器来获取刷新回调。*/
    public interface RefreshListener {
        void onRefresh();
    }

    /*构造函数，用于初始化*/
    public PullRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    /*初始化*/
    private void init(Context context){
        this.context = context;
        refreshView = LayoutInflater.from(context).inflate(R.layout.refresh_view,null,true);
        refreshArrow = (ImageView) refreshView.findViewById(R.id.refresh_arrow);
        refreshProgressBar = (ProgressBar) refreshView.findViewById(R.id.progressbar);
        statusText = (TextView) refreshView.findViewById(R.id.list_status);
        lastUpdateTimeText = (TextView) refreshView.findViewById(R.id.last_update_time);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        showLastUpdateTime();
        setOrientation(VERTICAL);
        addView(refreshView, 0);
    }

    /*显示上一次刷新的时间*/
    private void showLastUpdateTime(){
        lastUpdateTime = preferences.getLong(spTag, -1);
        long currentTime = System.currentTimeMillis();
        long lastUpdateTimeDuration = currentTime - lastUpdateTime;

        String updateStatus;
        String timeFormat = getResources().getString(R.string.last_update_time);

        if(lastUpdateTime == -1){
            updateStatus = getContext().getString(R.string.never_update);
        } else if (lastUpdateTimeDuration < 0){
            updateStatus = getContext().getString(R.string.time_error);
        } else if (lastUpdateTimeDuration < ONE_MINUTE){
            updateStatus = getContext().getString(R.string.update_just_now);
        } else if(lastUpdateTimeDuration < ONE_HOUR){
            updateStatus = String.format(timeFormat, lastUpdateTimeDuration / ONE_MINUTE + "分钟");
        } else if (lastUpdateTimeDuration < ONE_DAY) {
            updateStatus = String.format(timeFormat, lastUpdateTimeDuration / ONE_HOUR + "小时");
        }else if (lastUpdateTimeDuration < ONE_MONTH) {
            updateStatus = String.format(timeFormat, lastUpdateTimeDuration / ONE_DAY + "天");
        } else if (lastUpdateTimeDuration < ONE_YEAR) {
            updateStatus = String.format(timeFormat, lastUpdateTimeDuration / ONE_MONTH + "月");
        } else {
            updateStatus = String.format(timeFormat, lastUpdateTimeDuration / ONE_YEAR + "年");
        }

        lastUpdateTimeText.setText(updateStatus);
    }

    /*绑定刷新回调函数*/
    public void setOnRefreshListener(RefreshListener listener) {
        refreshListener = listener;
    }

    /*刷新结束*/
    public void finishRefresh() {
        currentStatus = ViewStatus.STATUS_REFRESH_FINISHED;
        preferences.edit().putLong(spTag, System.currentTimeMillis()).commit();
        new HideRefreshTask().execute();
    }

    /*隐藏头部（显示的刷新的那部分）*/
    class HideRefreshTask extends AsyncTask<Void,Integer,Integer>{

        @Override
        protected Integer doInBackground(Void... voids) {
            int topMargin = refreshLayoutParams.topMargin;
            while (true){
                topMargin += SCROLL_SPEED;
                if(topMargin<=refreshAreaHeight){
                    topMargin = refreshAreaHeight;
                    break;
                }

                publishProgress(topMargin);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            refreshLayoutParams.topMargin = values[0];
            refreshView.setLayoutParams(refreshLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            refreshLayoutParams.topMargin = integer;
            refreshView.setLayoutParams(refreshLayoutParams);
            currentStatus = ViewStatus.STATUS_REFRESH_FINISHED;
        }
    }

    /*用户手指点击响应*/
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        determinePullRefreshEnabledOrNot(motionEvent);
        if(canPull){
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downY = motionEvent.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveY = motionEvent.getRawY();
                    int moveDistance = (int) (moveY - downY);
                    //如果用户滑动距离太小或者向上拉动，不响应
                    if ((moveDistance <= 0 && refreshLayoutParams.topMargin <= refreshAreaHeight) || moveDistance < touchSlop) {
                        return false;
                    }

                    if (NetworkUtils.isWifi(context) || NetworkUtils.isNetworkConnected(context)) {
                        //设置刷新状态
                        if (currentStatus != ViewStatus.STATUS_REFRESHING) {
                            if (refreshLayoutParams.topMargin > 0) {
                                currentStatus = ViewStatus.STATUS_RELEASE_TO_REFRESH;
                            } else {
                                currentStatus = ViewStatus.STATUS_PULL_TO_REFRESH;
                            }

                            refreshLayoutParams.topMargin = moveDistance / 2 + refreshAreaHeight;
                            refreshView.setLayoutParams(refreshLayoutParams);
                        }
                    }else {
                        //没有网络时，防止重复显示Toast
                        if (networkToast == null) {
                            networkToast = Toast.makeText(context, R.string.network_unavailable, Toast.LENGTH_SHORT);
                        }
                        networkToast.show();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (currentStatus == ViewStatus.STATUS_RELEASE_TO_REFRESH) {
                        //开始刷新
                        new RefreshTask().execute();
                    } else if (currentStatus == ViewStatus.STATUS_PULL_TO_REFRESH) {
                        //用户下拉太小，不刷新，开始隐藏头部
                        new HideRefreshTask().execute();
                    }
                    break;
            }

            //更新刷新列表
            if (currentStatus == ViewStatus.STATUS_PULL_TO_REFRESH || currentStatus == ViewStatus.STATUS_RELEASE_TO_REFRESH) {
                updateRefreshView();
                jokesList.setPressed(false);
                jokesList.setFocusable(false);
                jokesList.setFocusableInTouchMode(false);
                lastStatus = currentStatus;
                return true;
            }
        }
        return false;
    }

    /*刷新时，头部的动态显示状态*/
    class RefreshTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int topMargin = refreshLayoutParams.topMargin;
            while (true) {
                //以SCROLL_SPEED为速度回滚
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= 0) {
                    break;
                }
                publishProgress(topMargin);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            currentStatus = ViewStatus.STATUS_REFRESHING;
            publishProgress(0);
            if (refreshListener != null) {
                refreshListener.onRefresh();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            updateRefreshView();
            refreshLayoutParams.topMargin = values[0];
            refreshView.setLayoutParams(refreshLayoutParams);
        }
    }

    /*判断是否允许下拉*/
    private void determinePullRefreshEnabledOrNot(MotionEvent motionEvent){
        View firstView = jokesList.getChildAt(0);
        if(firstView != null){
            int firstVisiblePosition = jokesList.getFirstVisiblePosition();
            if(firstVisiblePosition == 0 && firstView.getTop() == 0){
                if(!canPull){
                    downY = motionEvent.getRawY();
                }
                canPull = true;
            } else {
                if (refreshLayoutParams.topMargin != refreshAreaHeight) {
                    refreshLayoutParams.topMargin = refreshAreaHeight;
                    refreshView.setLayoutParams(refreshLayoutParams);
                }
                canPull = false;
            }
        } else {
            canPull = true;
        }
    }

    /*更新头部状态*/
    private void updateRefreshView() {
        if (lastStatus != currentStatus) {
            if (currentStatus ==ViewStatus.STATUS_PULL_TO_REFRESH) {
                statusText.setText(R.string.pull_to_refresh);
                refreshArrow.setVisibility(View.VISIBLE);
                refreshProgressBar.setVisibility(View.GONE);
                rotateArrow();
            } else if (currentStatus == ViewStatus.STATUS_RELEASE_TO_REFRESH) {
                statusText.setText(getContext().getString(R.string.release_to_refresh));
                refreshArrow.setVisibility(View.VISIBLE);
                refreshProgressBar.setVisibility(View.GONE);
                rotateArrow();
            } else if (currentStatus == ViewStatus.STATUS_REFRESHING) {
                statusText.setText(getContext().getString(R.string.refreshing_now));
                refreshArrow.clearAnimation();
                refreshArrow.setVisibility(View.GONE);
                refreshProgressBar.setVisibility(View.VISIBLE);
            }
            showLastUpdateTime();
        }
    }

    /*旋转箭头的动画*/
    private void rotateArrow() {
        float pivotX = refreshArrow.getWidth() / 2f;
        float pivotY = refreshArrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentStatus == ViewStatus.STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentStatus == ViewStatus.STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }

        RotateAnimation ra = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        ra.setDuration(100);
        ra.setFillAfter(true);
        refreshArrow.setAnimation(ra);
    }

    /*布局响应函数*/
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!haveLayout && changed) {
            refreshAreaHeight = -refreshView.getHeight();
            refreshLayoutParams = (MarginLayoutParams) refreshView.getLayoutParams();
            refreshLayoutParams.topMargin = refreshAreaHeight;
            jokesList = (ListView) getChildAt(1);
            jokesList.setOnTouchListener(this);
            haveLayout = true;
        }
    }
}
