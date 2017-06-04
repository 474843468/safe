package com.itcast.mobilesafe09.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {

	public static final String PREF_NAME = "config";

	public static void putBoolean(Context ctx, String key, boolean value) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context ctx, String key, boolean defValue) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, defValue);
	}

	public static void putString(Context ctx, String key, String value) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}

	public static String getString(Context ctx, String key, String defValue) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return sp.getString(key, defValue);
	}

	public static void putInt(Context ctx, String key, int value) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putInt(key, value).commit();
	}

	public static int getInt(Context ctx, String key, int defValue) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return sp.getInt(key, defValue);
	}

	//删除某个key
	public static void remove(Context ctx, String key) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		sp.edit().remove(key).commit();
	}
}
