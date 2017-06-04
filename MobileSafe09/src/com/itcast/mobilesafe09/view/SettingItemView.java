package com.itcast.mobilesafe09.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;

/**
 * 自定义组合控件
 * 
 * 开发流程:
 * 0. 写一个类继承RelativeLayout, 或LinearLayout
 * 1. 初始化自定义组合控件布局 View
				.inflate(getContext(), R.layout.setting_item_view, this);核心代码
 * 2. 更新组合控件标题和背景
 * 
 * 		setTitle, setBackground
 * 
 * 3. 点击事件设置及开关更新
 * 4. 自动更新逻辑控制
 * 5. SharePreference封装, PrefUtils
 * 6. 自定义属性
 * 
 * 		6.1 创建values/attrs.xml, 配置相关的属性名称和类型
 * 		6.2 在布局文件中声明命名空间, 并给相关控件配置自定义属性
 * 		6.3 从自定义组合控件中取出自定义属性值,并更新控件
 */
public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.itcast.mobilesafe09";

	private int[] mBgs = new int[] { R.drawable.setting_first_selector,
			R.drawable.setting_middle_selector,
			R.drawable.setting_last_selector };

	private TextView tvTitle;
	private ImageView ivToggle;
	private RelativeLayout rlRoot;

	private boolean isToggleOn = true;//记录当前开关状态, 默认打开

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();

		//从属性集合中取出自定义属性的值
		String title = attrs.getAttributeValue(NAMESPACE, "title");
		//System.out.println("title:" + title);
		//修改标题
		setTitle(title);

		//取出背景信息
		int bgPos = attrs.getAttributeIntValue(NAMESPACE, "background", 0);//0,1,2
		//设置背景
		setBackground(mBgs[bgPos]);

		//开关的显示和隐藏
		boolean showToggle = attrs.getAttributeBooleanValue(NAMESPACE,
				"showToggle", true);
		//		if (showToggle) {
		//			//显示控件
		//			ivToggle.setVisibility(View.VISIBLE);
		//		} else {
		//			//隐藏控件
		//			ivToggle.setVisibility(View.GONE);
		//		}
		//三元表达式
		ivToggle.setVisibility(showToggle ? View.VISIBLE : View.GONE);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public SettingItemView(Context context) {
		this(context, null);
	}

	private void initView() {
		//自定义控件中如果想获取Context, 调用getContext();
		// A view group that will be the parent:
		//初始化布局的同时, 以当前的SettingItemView为布局的父控件
		//此时空的SettingItemView就有了子控件,也就有了内容
		//注意: 参数3: 传递当前SettingItemView对象
		View view = View
				.inflate(getContext(), R.layout.setting_item_view, this);

		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		ivToggle = (ImageView) view.findViewById(R.id.iv_toggle);
		rlRoot = (RelativeLayout) view.findViewById(R.id.rl_root);
	}

	//修改标题, 供外部修改标题
	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	//修改背景,供外部修改背景
	public void setBackground(int bgId) {
		rlRoot.setBackgroundResource(bgId);
	}

	//判断当前开关是否开启
	public boolean isToggleOn() {
		return isToggleOn;
	}

	//设置当前开关状态
	public void setToggleOn(boolean isToggleOn) {
		this.isToggleOn = isToggleOn;

		//更新图片
		if (isToggleOn) {
			ivToggle.setImageResource(R.drawable.on);
		} else {
			ivToggle.setImageResource(R.drawable.off);
		}
	}

	//如果打开,则关闭; 反之亦然
	public void toggle() {
		//		if (isToggleOn) {
		//			setToggleOn(false);
		//		} else {
		//			setToggleOn(true);
		//		}
		setToggleOn(!isToggleOn);
	}
}
