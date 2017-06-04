package cn.itcast.zhxa09;

import cn.itcast.zhxa09.fragment.ContentFragment;
import cn.itcast.zhxa09.fragment.LeftMenuFragment;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.Window;

/**
 * 项目库：提供一些别人已经写好的方法
 * 
 * jar包的差别：
 * 
 * 1、项目库中可以提供资源供别人调用 2、项目库和关联的项目需要在同一个盘符 3、避免中文路径 空格路径 4、重启eclipse 5、打包的流程不变
 * 
 * 
 * 将主界面一分为2，分为LeftMenuFragment和ContentFragment，具体的逻辑在对应的Fragment中进行操作，
 * Activity所需要做的事情就仅仅是把Fragment加载进来就可以。
 * 
 * @author zhengping
 * 
 */

public class MainActivity extends SlidingFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_content);

		setBehindContentView(R.layout.layout_left);

		// 获取SlidingMenu的对象
		SlidingMenu slidingMenu = getSlidingMenu();
		
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		
		
		slidingMenu.setBehindOffset((int) (screenWidth*0.625f + 0.5f));
		
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		initFragment();

	}
	
	private void initFragment() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();//开启事务
		//1、需要将布局文件中的哪一个部分进行替换
		//2、将这部分内容替换成什么Fragment
		transaction.replace(R.id.flContent, new ContentFragment(),"content");
		transaction.replace(R.id.flLeft, new LeftMenuFragment(),"left");
		
		transaction.commit();//提交事务
		
		
		
	}
	
	public LeftMenuFragment getLeftMenuFragment() {
		FragmentManager fm = getSupportFragmentManager();
		LeftMenuFragment leftMenuFragment = (LeftMenuFragment) fm.findFragmentByTag("left");
		return leftMenuFragment;
	}

	
	public ContentFragment getContentFragment() {
		FragmentManager fm = getSupportFragmentManager();
		ContentFragment contentFragment = (ContentFragment) fm.findFragmentByTag("content");
		return contentFragment;
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
