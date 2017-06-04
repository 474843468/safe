package cn.itcast.zhxa09.fragment.pager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import cn.itcast.zhxa09.MainActivity;
import cn.itcast.zhxa09.R;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

//加载共同的布局文件
//往空的帧布局中填充不同的东西
public class BasePager {
	
	public Activity mActivity;//MainActivity
	public ImageView ivMenu;
	public TextView tvTitle;
	public FrameLayout flContent;
	public View mRootView;//标题栏+空的帧布局
	public ImageView ivDisplay;
	
	//一旦创建了BasePager的对象就做了加载布局文件和初始化控件的两件事
	public BasePager(Activity activity) {
		mActivity = activity;
		mRootView = initView();
	}
	
	//加载布局文件
	//初始化控件
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_base, null);
		ivMenu = (ImageView) view.findViewById(R.id.ivMenu);
		
		ivMenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//对侧边栏SlidingMenu进行打开或者关闭的操作
				MainActivity mainActivity = (MainActivity) mActivity;
				SlidingMenu slidingMenu = mainActivity.getSlidingMenu();
				
				//如果SlidingMenu的状态是打开的状态，此时调用toggle的方法，就把它关闭。反之亦然
				slidingMenu.toggle();
				
				
			}
		});
		ivDisplay = (ImageView) view.findViewById(R.id.ivDisplay);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		flContent = (FrameLayout) view.findViewById(R.id.flContent);
		return view;
	}
	
	public void initData() {
		TextView tv = new TextView(mActivity);
		tv.setText("我是ViewPager的其中一个item");
		
		flContent.addView(tv);
		
	}

}
