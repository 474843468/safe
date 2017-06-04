package cn.itcast.zhxa09.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class NetCacheUtils {
	
	private LocalCacheUtils localCacheUtils;
	private MemoryCacheUtils memoryCacheUtils;

	public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
		this.localCacheUtils = localCacheUtils;
		this.memoryCacheUtils = memoryCacheUtils;
	}

	public void getBitmapFromServer(ImageView iv, String url) {
		// 下载图片
		// 1、Thread+Handler
		// 2、AsyncTask：异步工具加载类，处理异步任务 Thread+Handler
		MyAsyncTask task = new MyAsyncTask();
		task.execute(iv, url);// 启动一个异步任务
	}

	// 泛型1：需要和doInBackground方法中的参数类型保持一致，还需要和execute方法中参数类型保持一致
	// 泛型2：需要和onProgressUpdate方法中参数类型保持一致，代表具体更新的进度
	// 泛型3：需要和doInBackground的返回值类型保持一致，还需要和onPostExecute方法参数的类型保持一致
	class MyAsyncTask extends AsyncTask<Object, Integer, Bitmap> {

		private ImageView iv;
		private String url;

		// 预加载 在后台任务执行之前会进行的回调
		// 此方法运行在主线程中
		// 使用场景：弹出一个加载框
		@Override
		protected void onPreExecute() {
			System.out.println("预加载");
			super.onPreExecute();
		}

		// 在后台默默运行，运行在子线中
		// 可以处理耗时的操作，但是不能进行UI操作
		@Override
		protected Bitmap doInBackground(Object... params) {
			System.out.println("正在下载");
			iv = (ImageView) params[0];
			url = (String) params[1];
			
			
			Bitmap bitmap = downloadBitmap(url);
			//iv.setImageBitmap(bitmap); 千万别再子线程修改UI
			
			//publishProgress(1);此方法会促发onProgressUpdate方法的调用
			
			return bitmap;
		}

		// 当下载进度更新的时候，会进行的回调
		// 此方法运行在主线程中
		// 使用场景：修改进度条
		@Override
		protected void onProgressUpdate(Integer... values) {
			System.out.println("进度更新");
			super.onProgressUpdate(values);
		}

		// 在后台任务执行结束之后，会进行的回调
		// 这个方法运行在主线程中
		// 使用场景：取消之前弹出的加载框
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			System.out.println("下载成功");
			String savedUrl = (String) iv.getTag();
			//需要判断一下在ImageView中存储的tag是否和任务中的url一致
			if(result != null && savedUrl.equals(url)) {
				iv.setImageBitmap(result);
				//将Bitmap存储在本地
				localCacheUtils.saveBitmap2Local(result, url);
				//将Bitmap存储在内存
				memoryCacheUtils.saveBitmap2memory(url, result);
				
			}
			super.onPostExecute(result);
		}
	}

	public Bitmap downloadBitmap(String url) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			conn.setRequestMethod("GET");//设置请求方式
			conn.setConnectTimeout(5000);// 设置连接超时
			conn.setReadTimeout(5000);// 设置读取超时：此时已经连接上了服务器，但是服务器迟迟不吐数据
			conn.connect();// 连接服务器

			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				// 连接成功
				InputStream inputStream = conn.getInputStream();
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);// 根据输入流来加载bitmap对象
				return bitmap;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
