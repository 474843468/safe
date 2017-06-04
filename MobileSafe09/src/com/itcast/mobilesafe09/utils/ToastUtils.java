package com.itcast.mobilesafe09.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

	public static void showToast(Context ctx, String text) {
		Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
	}

	//可以在子线程中toast
	public static void showToastOnUIThread(final Activity activity,
			final String text) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
