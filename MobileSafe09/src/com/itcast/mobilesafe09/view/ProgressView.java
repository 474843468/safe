package com.itcast.mobilesafe09.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;

/**
 * 自定义进度条组合控件
 */
public class ProgressView extends LinearLayout {

	private TextView tvTitle;
	private TextView tvLeft;
	private TextView tvRight;
	private ProgressBar pbProgress;

	public ProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public ProgressView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public ProgressView(Context context) {
		this(context, null);
	}

	//初始化布局
	private void initView() {
		View view = View.inflate(getContext(), R.layout.layout_progress, this);

		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		tvLeft = (TextView) view.findViewById(R.id.tv_left);
		tvRight = (TextView) view.findViewById(R.id.tv_right);
		pbProgress = (ProgressBar) view.findViewById(R.id.pb_progress);
	}

	//设置标题
	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	//设置左侧文字
	public void setLeftText(String text) {
		tvLeft.setText(text);
	}

	//设置右侧文字
	public void setRightText(String text) {
		tvRight.setText(text);
	}

	//设置进度
	public void setProgress(int progress) {
		pbProgress.setProgress(progress);
	}

}
