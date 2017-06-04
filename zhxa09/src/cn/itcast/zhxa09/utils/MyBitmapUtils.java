package cn.itcast.zhxa09.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class MyBitmapUtils {

	private Activity mActivity;
	private NetCacheUtils netCacheUtils;
	private LocalCacheUtils localCacheUtils;
	private MemoryCacheUtils memoryCacheUtils;

	public MyBitmapUtils(Activity mActivity) {
		this.mActivity = mActivity;
		memoryCacheUtils = new MemoryCacheUtils();
		localCacheUtils = new LocalCacheUtils(memoryCacheUtils);
		netCacheUtils = new NetCacheUtils(localCacheUtils,memoryCacheUtils);
	}

	public void display(ImageView iv, String url) {

		// 1、最优先加载内存缓存
		Bitmap bitmap = memoryCacheUtils.getBitmapFromMemory(url);
		if(bitmap != null) {
			//代表内存有缓存
			System.out.println("加载内存缓存");
			iv.setImageBitmap(bitmap);
			return;
			
		}
		// 2、其次加载本地缓存
		bitmap = localCacheUtils.getBitmapFromLocal(url);
		if (bitmap != null) {
			// 代表有本地缓存
			System.out.println("加载本地缓存");
			iv.setImageBitmap(bitmap);
			return;
		}
		// 3、最后才去加载网络缓存
		netCacheUtils.getBitmapFromServer(iv, url);

	}

}
