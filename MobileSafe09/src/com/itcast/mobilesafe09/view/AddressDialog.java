package com.itcast.mobilesafe09.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.itcast.mobilesafe09.R;

/**
 * 归属地弹窗样式自定义Dialog
 * 
 * 开发流程:
 * 1. 写一个类继承Dialog
 * 2. 给Dialog设置布局, setContentView
 * 3. 去掉标题栏和背景
 * 
 * 		定义一个样式, super(context, R.style.AddressDialogStyle);
 * 
 * 		 <!-- 归属地自定义Dialog样式 -->
	    <style name="AddressDialogStyle" parent="android:style/Theme.Dialog">
	
	        <!-- 去掉标题栏 -->
	        <item name="android:windowNoTitle">true</item>
	        <!-- 去掉背景 -->
	        <item name="android:windowBackground">@color/white</item>
	    </style>
	    
	4.让Dialog显示在屏幕底端居中位置, 修改Window的位置
	5. 给ListView设置数据
	
	6. 给ListView条目设置点击事件
	
		6.1  AddressDialog增加设置事件方法setOnItemClickListener
		6.2    在SettingActivity中, 设置点击事件, 点击之后,在sp中记录当前被点击的位置
		6.3   在SettingActivity->getView方法中, 根据sp中记录的位置, 决定选中图标的显示和隐藏
		
	7. 在AddressToast中, 根据sp中记录的样式的位置, 更新TextView图片
 */
public class AddressDialog extends Dialog {

	private ListView lvList;

	public AddressDialog(Context context) {
		//此方法可以在创建弹窗时,定义弹窗的样式
		super(context, R.style.AddressDialogStyle);
		
		//设置布局样式
		setContentView(R.layout.dialog_address);

		lvList = (ListView) findViewById(R.id.lv_list);

		//获取当前Dialog所在的窗口对象
		Window window = getWindow();

		//获取当前窗口属性,布局参数
		LayoutParams attributes = window.getAttributes();
		//修改布局参数, 底端居中
		attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

		//重新给窗口设置布局参数
		window.setAttributes(attributes);
	}

	//设置数据适配器
	//由外界将适配器传递进来; 扩展性比较强
	public void setAdapter(BaseAdapter adapter) {
		lvList.setAdapter(adapter);
	}

	//设置ListView条目点击事件, 又外界来实现
	public void setOnItemClickListener(OnItemClickListener listener) {
		lvList.setOnItemClickListener(listener);
	}

}
