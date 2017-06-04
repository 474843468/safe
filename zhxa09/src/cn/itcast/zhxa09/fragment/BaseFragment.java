package cn.itcast.zhxa09.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment可以认为是一个View的包装，并且Fragment还带有生命周期
 * 
 * @author zhengping
 * 
 */
public abstract class BaseFragment extends Fragment {

	public Activity mActivity;//可以给子类当作Context进行使用    -->MainActivity的实例对象

	// BaseFragment的实例对象被创建的时候进行的回调
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();//获取这个Fragment所依附的Activity的对象
	}

	// 返回Fragment所包装的View对象
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return initView();
	}

	// 所依附的Activity完全创建成功之后会进行的回调
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//加载数据
		initData();
	}
	
	//由于BaseFragment自己无法确定每一个子类究竟应该长什么样子，所以应该把这项工作交给子类完成
	public abstract View initView();
	
	public void initData() {
		
	}

}
