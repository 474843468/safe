package cn.itcast.zhxa09.widget;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.itcast.zhxa09.R;
import cn.itcast.zhxa09.utils.PrefUtils;

public class RefreshListView extends ListView implements OnScrollListener {

	public static final int STATE_PULL_TO_REFRESH = 0;// 下拉刷新
	public static final int STATE_RELEASE_TO_REFRESH = 1;// 松开刷新
	public static final int STATE_REFRESHING = 2;// 正在刷新

	private int mCurrentState = STATE_PULL_TO_REFRESH;

	private int headerHeight;
	private float startY;
	private View headerView;
	private ImageView ivArrow;
	private ProgressBar pb;
	private TextView tvStatus;
	private TextView tvTime;
	private RotateAnimation upAnimation;
	private RotateAnimation downAnimation;

	// 构造方法的串联
	public RefreshListView(Context context) {
		this(context, null);
		// initHeaderView();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// initHeaderView();
	}

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeaderView();
		initFooterView();
		initAnimation();
	}

	private void initHeaderView() {
		headerView = View.inflate(getContext(), R.layout.layout_refresh_header,
				null);

		ivArrow = (ImageView) headerView.findViewById(R.id.ivArrow);
		pb = (ProgressBar) headerView.findViewById(R.id.pb);

		tvStatus = (TextView) headerView.findViewById(R.id.tvStatus);
		tvTime = (TextView) headerView.findViewById(R.id.tvTime);
		
		//设置上一次加载数据成功的时间
		String lastUpdateTime = PrefUtils.getString(getContext(), "last_update_time", "");
		tvTime.setText(lastUpdateTime);

		// 最先添加的头布局显示的位置最高
		this.addHeaderView(headerView);

		// padding值可以改变一个控件的大小
		// 正值--变大
		// 负值--变小

		// measure--layout--draw
		int height = headerView.getHeight();
		Log.i("RefreshListView", "height=" + height);

		// 监听视图树
		// 手动测量的操作
		headerView.measure(0, 0);// 把测量的工作计算逻辑交给系统来处理，系统不做任何限制操作

		headerHeight = headerView.getMeasuredHeight();

		headerView.setPadding(0, -headerHeight, 0, 0);

	}
	
	private void initFooterView() {
		footerView = View.inflate(getContext(), R.layout.layout_refresh_footer, null);
		
		footerView.measure(0, 0);
		footerHeight = footerView.getMeasuredHeight();
		footerView.setPadding(0, -footerHeight, 0, 0);
		this.addFooterView(footerView);
		
		this.setOnScrollListener(this);
	}

	private void initAnimation() {
		upAnimation = new RotateAnimation(0, -180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		upAnimation.setDuration(200);
		upAnimation.setFillAfter(true);
		
		
		downAnimation = new RotateAnimation(-180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		downAnimation.setDuration(200);
		downAnimation.setFillAfter(true);
	}

	// 一个View只要得到了事件，就一定会最先调用dispatchTouchEvent这个方法
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			startY = ev.getY();
		}
		return super.dispatchTouchEvent(ev);
	}

	// onTouchEvent的down事件会在子控件消费了down事件的时候，获取不到
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			startY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float moveY = ev.getY();
			float dy = moveY - startY;

			int firstVisiblePosition = getFirstVisiblePosition();
			if (firstVisiblePosition == 0 && dy > 0) {
				
				//如果当前处于正在刷新的状态，不需要改变头布局的高度
				if(mCurrentState == STATE_REFRESHING) {
					break;
				}
				
				int paddingTop = (int) (dy - headerHeight);
				// 不断的改变头布局的paddingTop的值，让头布局的高度慢慢变大
				headerView.setPadding(0, paddingTop, 0, 0);

				int oldState = mCurrentState;

				if (paddingTop < 0) {
					// 下拉刷新
					mCurrentState = STATE_PULL_TO_REFRESH;
				} else {
					// 松开刷新
					mCurrentState = STATE_RELEASE_TO_REFRESH;
				}

				// 代表状态发生了改变，才需要刷新UI
				if (oldState != mCurrentState) {
					refreshState();
				}

				return true;// 代表消费事件
			}

			break;
		case MotionEvent.ACTION_UP:

			if(mCurrentState == STATE_PULL_TO_REFRESH) {
				//把头布局隐藏
				headerView.setPadding(0, -headerHeight, 0, 0);
			} else if(mCurrentState == STATE_RELEASE_TO_REFRESH) {
				
				//1、更新状态
				mCurrentState = STATE_REFRESHING;
				refreshState();
				//2、让头布局完全显示出来
				headerView.setPadding(0, 0, 0, 0);
				
				//3、刷新网络数据
				notifyOnRefresh();
			}
			
			
			
			break;

		}
		return super.onTouchEvent(ev);
	}

	private void refreshState() {
		switch (mCurrentState) {
		case STATE_PULL_TO_REFRESH:
			// 不能设置为GONE ，设置GONE会影响整体的布局情况，设置INVISIBLE不会影响整体的布局
			// GONE/INVISIBLE 一个不占位一个占位
			pb.setVisibility(View.INVISIBLE);
			ivArrow.setVisibility(View.VISIBLE);
			ivArrow.startAnimation(downAnimation);
			tvStatus.setText("下拉刷新");
			break;
		case STATE_RELEASE_TO_REFRESH:
			pb.setVisibility(View.INVISIBLE);
			ivArrow.setVisibility(View.VISIBLE);
			ivArrow.setAnimation(upAnimation);
			tvStatus.setText("松开刷新");
			break;
		case STATE_REFRESHING:
			pb.setVisibility(View.VISIBLE);
			//需要把ivArrow的动画移除，才能把ivArrow隐藏起来
			ivArrow.clearAnimation();
			ivArrow.setVisibility(View.INVISIBLE);
			tvStatus.setText("正在刷新");
			break;

		}
	}
	
	//2、定义观察者
	public interface OnRefreshListener {
		public void onRefresh();
		public void onLoadMore();
	}
	
	//3、保存观察者
	private OnRefreshListener mListener;
	private View footerView;
	private int footerHeight;
	
	public void setOnRefreshListener(OnRefreshListener listener) {
		this.mListener = listener;
	}
	
	//4、通知观察者
	private void notifyOnRefresh() {
		if(mListener != null) {
			mListener.onRefresh();
		}
	}
	private void notifyLoadMore() {
		if(mListener != null) {
			mListener.onLoadMore();
		}
	}
	
	public void onRefreshComplete(boolean success) {
		
		//头布局隐藏起来
		headerView.setPadding(0, -headerHeight, 0, 0);
		//改变状态
		mCurrentState = STATE_PULL_TO_REFRESH;
		//refreshState();
		pb.setVisibility(View.INVISIBLE);
		ivArrow.setVisibility(View.VISIBLE);
		tvStatus.setText("下拉刷新");
		
		if(success) {
			setCurrentTime();
		}
		
		
		
	}
	
	private void setCurrentTime() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String currentTime = sdf.format(now);
		tvTime.setText(currentTime);
		
		PrefUtils.setString(getContext(), "last_update_time", currentTime);
	}

	private boolean isLoadingMore = false;
	
	//当滚动状态发生改变的时候
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == SCROLL_STATE_IDLE && !isLoadingMore) {
			int lastVisiblePosition = getLastVisiblePosition();
			//getAdapter().getCount()  -->获取数据集合的条目数量
			if(lastVisiblePosition == getCount()-1) {
				//getCount()-1   代表的是那一条线
				System.out.println("到底了...");
				footerView.setPadding(0, 0, 0, 0);//显示脚布局
				setSelection(getCount()-1);//将脚布局自己自动显示出来
				
				isLoadingMore = true;
				//加载下一页的数据
				notifyLoadMore();
				
			}
		}
	}

	//整个滚动的过程都会进行回调
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}
	
	public void onLoadMoreComplete() {
		isLoadingMore = false;
		//隐藏脚布局
		footerView.setPadding(0, -footerHeight, 0, 0);
	}
}
