package cn.itcast.zhxa09.fragment;

import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.itcast.zhxa09.MainActivity;
import cn.itcast.zhxa09.R;
import cn.itcast.zhxa09.fragment.pager.BasePager;
import cn.itcast.zhxa09.fragment.pager.impl.GovAffairsPager;
import cn.itcast.zhxa09.fragment.pager.impl.HomePager;
import cn.itcast.zhxa09.fragment.pager.impl.NewsCenterPager;
import cn.itcast.zhxa09.fragment.pager.impl.SettingPager;
import cn.itcast.zhxa09.fragment.pager.impl.SmartServicePager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ContentFragment extends BaseFragment {

	private ArrayList<BasePager> mPagers;
	@ViewInject(R.id.rg)
	private RadioGroup rg;
	@ViewInject(R.id.viewPager)
	private ViewPager viewPager;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_layout_content,
				null);

		// 参数1：控件声明的区域对象
		// 参数2：指的是需要进行findViewById的View对象
		ViewUtils.inject(this, view);
		// 如果当前对象是activity的话，只需要传递一个参数即可
		// ViewUtils.inject(activity);

		viewPager.setAdapter(new MyAdapter());
		
		Log.i("ContentFragment", "");

		return view;
	}

	@Override
	public void initData() {
		mPagers = new ArrayList<BasePager>();

		mPagers.add(new HomePager(mActivity));
		mPagers.add(new NewsCenterPager(mActivity));
		mPagers.add(new SmartServicePager(mActivity));
		mPagers.add(new GovAffairsPager(mActivity));
		mPagers.add(new SettingPager(mActivity));

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rbHome:
					viewPager.setCurrentItem(0, false);
					break;
				case R.id.rbNewsCenter:
					viewPager.setCurrentItem(1, false);
					break;
				case R.id.rbSmartService:
					viewPager.setCurrentItem(2, false);
					break;
				case R.id.rbGovAffairs:
					viewPager.setCurrentItem(3, false);
					break;
				case R.id.rbSetting:
					viewPager.setCurrentItem(4, false);
					break;

				}
			}
		});

		rg.check(R.id.rbHome);

		// 控制viewPager预加载界面的数量 LazyViewPager
		// viewPager.setOffscreenPageLimit(0);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mPagers.get(position).initData();

				if (position == 0 || position == mPagers.size() - 1) {
					// 禁用侧边栏
					setSlidingMenuEnable(false);
				} else {
					// 启用侧边栏
					setSlidingMenuEnable(true);
				}

			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub

			}
		});

		mPagers.get(0).initData();// 默认加载第0页的数据
		setSlidingMenuEnable(false);

	}
	
	public NewsCenterPager getNewsCenterPager() {
		return (NewsCenterPager) mPagers.get(1);
	}

	private void setSlidingMenuEnable(boolean enable) {

		MainActivity mainActivity = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainActivity.getSlidingMenu();

		if (enable) {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}

	class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mPagers.size();
			//return 5;//魔术数字
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		// 抽取：共同的东西---标题栏+空的帧布局
		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			// 创建每一个item的view对象
			// View view = View.inflate(mActivity, R.layout.pager_base, null);
			// FrameLayout flContent = (FrameLayout)
			// view.findViewById(R.id.flContent);
			BasePager basePager = mPagers.get(position);// new
			// basePager.initData();
			container.addView(basePager.mRootView);

			return basePager.mRootView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

}
