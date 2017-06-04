package com.itcast.mobilesafe09.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.itcast.mobilesafe09.utils.ToastUtils;

/**
 * 设置向导页父类
 * 
 * BaseSetupActivity: 没有布局, 不展示, 所以可以不在清单文件注册
 */
public abstract class BaseSetupActivity extends Activity {

	private GestureDetector mDetector;//手势识别器

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDetector = new GestureDetector(this, new SimpleOnGestureListener() {

			//监听飞,抛的动作
			//e1:起点坐标
			//e2:终点坐标
			//velocityX: 水平方向滑动速度
			//velocityY: 竖直方向滑动速度
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//System.out.println("onFling.....");

				//竖直方向不允许滑动太大
				//过滤掉斜划
				if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
					ToastUtils.showToast(getApplicationContext(), "不能这么划哦!");
					return true;
				}

				//速度不能太慢
				if (Math.abs(velocityX) < 150) {
					ToastUtils.showToast(getApplicationContext(), "划得太慢啦!");
					return true;
				}

				//是否左右滑动
				if (e2.getRawX() - e1.getRawX() > 100) {
					System.out.println("向右滑动");
					//上一页
					go2Pre();
				}

				if (e1.getRawX() - e2.getRawX() > 100) {
					System.out.println("向左滑动");
					//下一页
					go2Next();
				}

				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	//监听当前页面触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//System.out.println("触摸了....");
		mDetector.onTouchEvent(event);//将事件数据传递给手势识别器,有识别器来过滤出我们想要的手势
		return super.onTouchEvent(event);
	}

	//点击下一步按钮,跳到下一个页面
	public void nextPage(View view) {
		go2Next();
	}

	//点击上一步按钮,跳到上一个页面
	public void prePage(View view) {
		go2Pre();
	}

	//下一页逻辑, 子类必须实现
	public abstract void go2Next();

	//上一页逻辑, 子类必须实现
	public abstract void go2Pre();
}
