package cn.itcast.zhxa09;

import com.umeng.analytics.MobclickAgent;

import cn.itcast.zhxa09.utils.PrefUtils;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		RelativeLayout rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);

		// 给rlRoot增加动画效果
		// 1、旋转动画
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setFillAfter(true);// 保持动画最终的状态
		// 2、缩放动画
		ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(1000);
		scaleAnimation.setFillAfter(true);
		// 3、渐变动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(2000);
		alphaAnimation.setFillAfter(true);

		// 将三个动画添加到动画集合中
		AnimationSet animationSet = new AnimationSet(true);// 共享动画插入器
		animationSet.addAnimation(rotateAnimation);
		animationSet.addAnimation(scaleAnimation);
		animationSet.addAnimation(alphaAnimation);

		rlRoot.startAnimation(animationSet);

		animationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 跳转界面
				// 判断是否是第一次运行
				// 从sp中取值
				// SharedPreferences sp = getSharedPreferences("config",
				// MODE_PRIVATE);
				boolean isFirstIn = PrefUtils.getBoolean(
						getApplicationContext(), "isFirstIn", true);// sp.getBoolean("isFirstIn",
																	// true);
				if (isFirstIn) {
					// 跳转新手引导页面
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(),
							GuideActivity.class);
					startActivity(intent);
				} else {
					// 跳转主界面
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), MainActivity.class);
					startActivity(intent);
				}

				finish();

			}
		});

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
