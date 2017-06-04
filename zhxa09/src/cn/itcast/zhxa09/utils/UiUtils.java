package cn.itcast.zhxa09.utils;

import android.content.Context;

public class UiUtils {
	
	public static int dp2px(int dp,Context ctx) {
		
		float density = ctx.getResources().getDisplayMetrics().density;
		return (int) (dp*density + 0.5f);//四舍五入
		//3.1  3.6
		//3.6  4.1
		
	}
	
	public static int px2dp(int px,Context ctx) {
		float density = ctx.getResources().getDisplayMetrics().density;
		return (int) (px/density + 0.5f);
	}

}
