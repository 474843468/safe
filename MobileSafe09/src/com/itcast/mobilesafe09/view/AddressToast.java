package com.itcast.mobilesafe09.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.global.GlobalConstants;
import com.itcast.mobilesafe09.utils.PrefUtils;

/**
 * 归属地显示弹窗封装
 * 
 * 窗口布局触摸条件:
 * 1. 去掉FLAG_NOT_TOUCHABLE标记
 * 2. 定义布局显示级别,调整级别为TYPE_PHONE, 保证可以触摸
 * 3. 配权限: android.permission.SYSTEM_ALERT_WINDOW
 * 4. 设置触摸监听:setOnTouchListener
 */
public class AddressToast {

	private WindowManager mWM;
	private View mView;
	private WindowManager.LayoutParams mParams;
	private TextView tvAddress;

	private int startX;
	private int startY;

	private Context mContext;

	public AddressToast(Context ctx) {
		mContext = ctx;

		mWM = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);

		//初始化布局参数(能看懂, 直接抄)
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;//布局高度
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;//布局宽度
		//标记, 定义布局的特点, 没有焦点, 不能触摸, 保持屏幕常亮
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		//| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE(去掉此代码,保证可以触摸)
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		//显示格式,显示效果,此属性不重要,可以忽略
		mParams.format = PixelFormat.TRANSLUCENT;
		//吐司动画
		//params.windowAnimations = com.android.internal.R.style.Animation_Toast;
		//窗口布局类型, 吐司类型, 定义布局显示级别,调整级别为TYPE_PHONE, 保证可以触摸
		mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		//窗口布局标题
		//params.setTitle("Toast");

		//布局的位置设定
		//		mParams.x : x坐标
		//		mParams.y : y坐标

		//初始化布局
		//		mView = new TextView(ctx);
		//		mView.setTextColor(Color.RED);
		//		mView.setTextSize(30);
		mView = View.inflate(ctx, R.layout.toast_address, null);
		tvAddress = (TextView) mView.findViewById(R.id.tv_address);

		//设置布局的触摸监听
		//		1. 按下之后,获取起始坐标点
		//		2. 移动之后,获取移动后的坐标点
		//		3. 计算移动偏移量
		//		4. 根据偏移量,更新控件位置
		//		5. 重新初始化起点坐标
		mView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//System.out.println("按下");

					//1. 按下之后,获取起始坐标点

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					//					System.out.println("rawX=" + rawX);
					//					System.out.println("rawY=" + rawY);

					//int x = (int) event.getX();//以当前控件左上角为原点, 获取相对的坐标点
					//int y = (int) event.getY();

					//					System.out.println("x=" + x);
					//					System.out.println("y=" + y);

					break;
				case MotionEvent.ACTION_MOVE:
					//					System.out.println("移动");
					//2. 移动之后,获取移动后的坐标点
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();

					//3. 计算移动偏移量
					int dx = moveX - startX;
					int dy = moveY - startY;

					//4. 根据偏移量,更新控件位置
					mParams.x += dx;
					mParams.y += dy;

					//按照最新的布局参数,更新窗口布局
					mWM.updateViewLayout(mView, mParams);

					//5. 重新初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					//System.out.println("抬起");
					break;

				default:
					break;
				}

				return true;//消费掉此事件
			}
		});
	}

	//样式图片id集合
	private int[] mStyleIds = new int[] { R.drawable.shape_address_normal,
			R.drawable.shape_address_orange, R.drawable.shape_address_blue,
			R.drawable.shape_address_gray, R.drawable.shape_address_green };

	//显示窗口布局
	public void show(String text) {
		tvAddress.setText(text);//更新文本

		//根据sp中记录的样式的位置, 更新TextView图片
		int stylePos = PrefUtils.getInt(mContext,
				GlobalConstants.PREF_ADDRESS_STYLE, 0);
		tvAddress.setBackgroundResource(mStyleIds[stylePos]);

		//给窗口添加布局, 参1:布局对象; 参2:布局参数
		mWM.addView(mView, mParams);
	}

	//隐藏窗口布局
	public void hide() {
		//打开服务时, 会走此方法, 但此时没有给窗口添加布局, 直接移除布局会出异常
		//确保布局已经添加给窗口, 才能够移除; 如果布局父控件不为空,就认为已经添加给窗口了
		if (mWM != null && mView != null && mView.getParent() != null) {
			mWM.removeView(mView);
		}
	}
}
