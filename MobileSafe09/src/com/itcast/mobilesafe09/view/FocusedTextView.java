package com.itcast.mobilesafe09.view;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 获取焦点的TextView, 跑马灯效果
 */
public class FocusedTextView extends TextView {

	//defStyle: 样式
	//当布局文件中配有样式时, 走此构造方法, 由系统底层来调用
	public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//System.out.println("构造方法3");
		initView();
	}

	//AttributeSet:属性集合
	//当在布局文件中配置TextView时, 有属性, 会走此构造方法, 由系统底层来调用
	public FocusedTextView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
		//System.out.println("构造方法2");
		//initView();
	}

	//当通过代码new一个TextView的时候,使用此构造方法
	public FocusedTextView(Context context) {
		this(context, null);//此方法会将构造方法引导到第二个构造方法中
		//System.out.println("构造方法1");
		//initView();
	}

	//初始化布局
	private void initView() {
		//System.out.println("初始化布局了....");

		//		 android:singleLine="true"
		//	                android:ellipsize="marquee"
		//	                android:focusable="true"
		//	                android:focusableInTouchMode="true"
		setSingleLine(true);
		setEllipsize(TruncateAt.MARQUEE);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	//强制返回true, 让系统认为当前TextView永远有焦点
	//解决问题: 当通知给两个TextView设置跑马灯效果时, 只有一个跑马灯起作用的bug
	@Override
	public boolean isFocused() {
		return true;
	}

}
