package com.dc.jnapp_v1;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MyListView extends ListView implements OnScrollListener  {
	public LayoutInflater inflater;
	public LinearLayout headerView;
	public MyListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	private void init(Context context) {
		// TODO Auto-generated method stub
		setCacheColorHint(context.getResources().getColor(R.color.abc_search_url_text_holo));
		inflater = LayoutInflater.from(context);
		headerView=(LinearLayout) inflater.inflate(R.layout.lv_header,null);
	}
	TextView mTv1;
	TextView mTv2;
	Button mBtn;
	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
}
