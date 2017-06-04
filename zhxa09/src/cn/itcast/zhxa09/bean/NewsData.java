package cn.itcast.zhxa09.bean;

import java.util.ArrayList;

/**
 * {}--类 []--集合 ArrayList 什么符号都没有---基本数据类型
 * 
 * @author zhengping
 * 
 */
public class NewsData {

	public ArrayList<NewsMenuData> data;

	public ArrayList<Integer> extend;

	public int retcode;

	// 尽量的把javabean的作用范围定义的广一些
	public class NewsMenuData {

		public int id;
		public String title;
		public int type;
		public String url;
		public String url1;
		public String dayurl;
		public String excurl;
		public String weekurl;

		public ArrayList<NewsTabData> children;

		@Override
		public String toString() {
			return "NewsMenuData [title=" + title + ", children=" + children
					+ "]";
		}

	}

	public class NewsTabData {

		public int id;
		public String title;
		public int type;
		public String url;

		@Override
		public String toString() {
			return "NewsTabData [title=" + title + "]";
		}

	}

	@Override
	public String toString() {
		return "NewsData [data=" + data + ", retcode=" + retcode + "]";
	}

}
