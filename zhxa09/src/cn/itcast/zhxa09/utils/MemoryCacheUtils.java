package cn.itcast.zhxa09.utils;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MemoryCacheUtils {

	public MemoryCacheUtils() {
		// LruCache是一个容器，用来存储bitmap对象，这个容器的最大容量是多少，maxSize
		long maxMemory = Runtime.getRuntime().maxMemory();//应用程序在手机当中能够占用的最大内存
		
		lruCache = new LruCache<String, Bitmap>(
				(int) (maxMemory / 8)){
			
			//为LRUCache容器中的每一个item定义规则，定义计算大小的规则
			@Override
			protected int sizeOf(String key, Bitmap value) {
				int byteCount = value.getRowBytes() * value.getHeight();
				return byteCount;
			}
			
		};
	}

	// private HashMap<String,Bitmap> savedBitmap = new
	// HashMap<String,Bitmap>();
	private HashMap<String, SoftReference<Bitmap>> savedBitmapRef = new HashMap<String, SoftReference<Bitmap>>();
	private LruCache<String, Bitmap> lruCache;

	public void saveBitmap2memory(String url, Bitmap bitmap) {

		// Reference<T>
		// SoftReference<T> 软引用 在内存不足的情况下，垃圾回收器会考虑回收软引用当中的对象
		// WeakReference<T> 弱引用 在内存不足的情况下，垃圾回收器更会考虑回收弱引用当中的对象
		// PhantomReference<T> 虚引用 在内存不足的情况下，垃圾回收器最会考虑回收虚引用当中的对象

		// savedBitmap.put(url, bitmap);
		//SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bitmap);// 将bitmap的对象变成了软引用的类型

		// LruCache :最近最少使用算法 A B C D A B

		//savedBitmapRef.put(url, soft);
		lruCache.put(url, bitmap);
	}

	public Bitmap getBitmapFromMemory(String url) {
		// Bitmap bitmap = savedBitmap.get(url);
//		SoftReference<Bitmap> soft = savedBitmapRef.get(url);
//		if (soft != null) {
//			Bitmap bitmap = soft.get();
//			return bitmap;
//		}
		Bitmap bitmap = lruCache.get(url);
		return bitmap;
	}

}
