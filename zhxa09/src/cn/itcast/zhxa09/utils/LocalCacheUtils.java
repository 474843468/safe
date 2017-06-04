package cn.itcast.zhxa09.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * 本地缓存
 * 
 * @author zhengping
 * 
 */
public class LocalCacheUtils {
	
	private MemoryCacheUtils memoryCacheUtils;

	public LocalCacheUtils(MemoryCacheUtils memoryCacheUtils) {
		this.memoryCacheUtils = memoryCacheUtils;
	}

	// 1、写缓存
	public void saveBitmap2Local(Bitmap bitmap, String url) {
		try {
			// 存在哪里呢? /sdcard/ /data/data/包名
			File sdcardRoot = Environment.getExternalStorageDirectory();
			// /sdcard/baidu/ /sdcard/tencent/
			File dir = new File(sdcardRoot, "zhxa09");
			if (!dir.exists() || dir.isFile()) {
				dir.mkdirs();// 创建文件夹
			}
			// /sdcard/zhxa09/xxxxx.jpg
			String fileName = MD5Encoder.encode(url);

			File savedFile = new File(dir, fileName);

			bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(
					savedFile));// 将Bitmap对象存储到某一个文件中
			//bitmap图片压缩算法

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 2、读缓存
	public Bitmap getBitmapFromLocal(String url) {

		try {
			// 存在哪里呢? /sdcard/ /data/data/包名
			File sdcardRoot = Environment.getExternalStorageDirectory();
			// /sdcard/baidu/ /sdcard/tencent/
			File dir = new File(sdcardRoot, "zhxa09");
			if (!dir.exists() || dir.isFile()) {
				dir.mkdirs();// 创建文件夹
			}
			// /sdcard/zhxa09/xxxxx.jpg
			String fileName = MD5Encoder.encode(url);

			File savedFile = new File(dir, fileName);

			Bitmap bitmap = BitmapFactory.decodeFile(savedFile
					.getAbsolutePath());
			
			//当加载本地缓存成功之后，需要将Bitmap缓存在内存中
			memoryCacheUtils.saveBitmap2memory(url, bitmap);
			return bitmap;

		} catch (Exception e) {

		}
		return null;

	}
}
