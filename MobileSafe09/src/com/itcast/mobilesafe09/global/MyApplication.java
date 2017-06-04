package com.itcast.mobilesafe09.global;

import java.io.File;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.os.Environment;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		//捕获全局异常
		//设置默认未捕获异常处理器
		Thread.setDefaultUncaughtExceptionHandler(new MyHandler());

	}

	class MyHandler implements UncaughtExceptionHandler {

		//一旦出现未捕获异常,就会走到此方法中
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			System.out.println("发现一个未处理的异常,但是被哥捕获了...");
			ex.printStackTrace();

			//收集错误日志, 自动将错误日志文件上传到服务器
			PrintWriter writer;
			try {
				writer = new PrintWriter(new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/err09.log"));
				ex.printStackTrace(writer);

				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			//结束当前进程, 闪退
			//System.exit(0);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
}
