package cn.itcast.zhxa09.bean;

import java.util.ArrayList;

public class TabInfo {

	public int retcode;

	public TabData data;

	public class TabData {
		public String countcommenturl;

		public String more;

		public ArrayList<NewsData> news;
		
		public String title;
		
		public ArrayList<TopicData> topic;
		
		
		public ArrayList<TopNewsData> topnews;
	}

	public class NewsData {

		public boolean comment;
		public String commentlist;
		public String commenturl;
		public int id;
		public String listimage;
		public String pubdate;
		public String title;
		public String type;
		public String url;

	}
	
	public class TopicData {
		public String description;
		public int id;
		public String listimage;
		public int sort;
		public String title;
		public String url;
	}
	
	public class TopNewsData {
		public boolean comment;
		public String commentlist;
		public String commenturl;
		public int id;
		public String pubdate;
		public String topimage;
		public String type;
		public String url;
		public String title;
	}

}
