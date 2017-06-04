package cn.itcast.zhxa09;

import java.util.ArrayList;

import com.umeng.analytics.MobclickAgent;

import cn.itcast.zhxa09.utils.PrefUtils;
import cn.itcast.zhxa09.utils.UiUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GuideActivity extends Activity {

	private int[] mResIds = new int[] { R.drawable.guide_1, R.drawable.guide_2,
			R.drawable.guide_3 };

	private ArrayList<ImageView> displayImageViews = new ArrayList<ImageView>();

	private LinearLayout llContainer;

	private ImageView ivRed;

	private int distance;

	private Button btnStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 通过代码的方式去掉标题栏 这行代码必须在setContentView之前调用
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);

		ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

		llContainer = (LinearLayout) findViewById(R.id.llContainer);

		ivRed = (ImageView) findViewById(R.id.ivRed);

		btnStart = (Button) findViewById(R.id.btnStart);

		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, MainActivity.class);
				startActivity(intent);

				// 保存isFirstIn的值
				PrefUtils.setBoolean(getApplicationContext(), "isFirstIn",
						false);

				finish();
			}
		});

		initView();

		viewPager.setAdapter(new MyAdapter());

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (position == mResIds.length - 1) {
					btnStart.setVisibility(View.VISIBLE);
				} else {
					btnStart.setVisibility(View.GONE);
				}
			}

			// position：当前的界面
			// positionOffset:当前界面移动的偏移比例
			// positionOffsetPixels:当前界面移动的像素值
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

				System.out.println("onPageScrolled.position=" + position
						+ ",positionOffset=" + positionOffset
						+ ",positionOffsetPixels=" + positionOffsetPixels);

				// 改变ivRed的位置信息
				RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) ivRed
						.getLayoutParams();
				layoutParams.leftMargin = (int) (distance * (positionOffset + position));
				ivRed.setLayoutParams(layoutParams);

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		// view的绘制流程 measure--layout--draw
		// 千万不要在onCreate方法中获取一个View的位置和大小信息
		/*
		 * int distance = llContainer.getChildAt(1).getLeft() -
		 * llContainer.getChildAt(0).getLeft(); System.out.println("distance=" +
		 * distance);
		 */

		// 视图树的监听
		llContainer.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					// 视图树中所有的View都执行了layout方法之后，会进行的回调
					@Override
					public void onGlobalLayout() {
						distance = llContainer.getChildAt(1).getLeft()
								- llContainer.getChildAt(0).getLeft();
						System.out.println("distance=" + distance);

						// 一般情况下，我们会在获取值之后，将监听取消
						llContainer.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						// llContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);

					}
				});

	}

	private void initView() {
		for (int i = 0; i < mResIds.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setBackgroundResource(mResIds[i]);
			displayImageViews.add(iv);

			ImageView dotImageView = new ImageView(this);
			dotImageView.setImageResource(R.drawable.shape_dot_grey);

			llContainer.addView(dotImageView);

			// 布局参数对象代表的是在布局文件中写的以layout_xxx具体的属性
			// 布局参数对象究竟是哪一种类型，得取决于父控件
			if (i > 0) {
				// LayoutPa
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				params.leftMargin = UiUtils.dp2px(10, getApplicationContext());
				dotImageView.setLayoutParams(params);
			}

		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mResIds.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 1、创建需要显示的View对象
			// ImageView iv = new ImageView(getApplicationContext());
			// iv.setBackgroundResource(mResIds[position]);
			ImageView iv = displayImageViews.get(position);
			// 2、将view对象添加到container
			container.addView(iv);
			// 3、将view对象返回
			return iv;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}
}
