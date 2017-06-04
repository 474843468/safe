package cn.itcast.zhxa09.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 关于sp的工具类
 * 
 * @author zhengping
 * 
 */
public class PrefUtils {

	public static boolean getBoolean(Context ctx, String key, boolean defValue) {
		SharedPreferences sp = ctx.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		boolean retBoolean = sp.getBoolean(key, defValue);
		return retBoolean;
	}

	public static void setBoolean(Context ctx, String key, boolean value) {
		SharedPreferences sp = ctx.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();

	}

	public static String getString(Context ctx, String key, String defValue) {
		SharedPreferences sp = ctx.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String retString = sp.getString(key, defValue);
		return retString;
	}

	public static void setString(Context ctx, String key, String value) {
		SharedPreferences sp = ctx.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();

	}

}
