package com.dc.jnapp_v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyAdapter extends SimpleAdapter{


	private LayoutInflater mInflater;
	EditText edittext;
	TextView tv;
	static String inputText3;
	static Context cont;
	static String str_name,str_msg;
	static int pos;
	ListViewHolder holder;
	private SQLiteDatabase db;
	static public String atReplyname;
	public static boolean fReply = false;
	static public ReplyThread replyThread;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//ListViewHolder holder;
		pos=position;
		if(convertView==null){
			holder = new ListViewHolder();
			convertView = mInflater.inflate(R.layout.row,null);
			holder.mBtn = (Button)convertView.findViewById(R.id.id_btn_at);
			//holder.mBtn.setTag(position);
			//holder.mBtn.setFocusableInTouchMode(false);
			holder.mTv1 = (TextView)convertView.findViewById(R.id.tv1);  //replyname
			holder.mBtn.setTag(holder.mTv1);
			holder.mTv11 = (TextView)convertView.findViewById(R.id.tv2);   //atname
			holder.mTv2 = (TextView)convertView.findViewById(R.id.textView2);
			convertView.setTag(holder);
		}else{
			holder=(ListViewHolder)convertView.getTag();
		}
		
		holder.mBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("getView() mBtn.onClick()","yes");
				//final int index = (Integer)v.getTag();
				//Log.i("getView()-> onclick()-> v.getTag()", index+"");
				final TextView tview =(TextView)v.getTag(); 
				atReplyname = tview.getText().toString();
				Log.i("getView()->onclick()->v.getTag() ",atReplyname);
				//Log.i("getView() data2 ",data2.get(pos).toString());
			/*	Log.i("getView() ",str_name);
				Log.i("getView() ",str_msg);*/
				edittext = new EditText(cont);
				tv = new TextView(cont);
				new AlertDialog.Builder(cont).setTitle("回复内容")
				.setIcon(android.R.drawable.ic_dialog_info).setView(edittext)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						inputText3 = edittext.getText().toString();
						//运行ReplyThread 存数据到mysql
						if(fReply){
							ReplyThread.interrupted();
							replyThread=null;
							replyThread=new ReplyThread();
							replyThread.start();
						}else{
							fReply=true;
							replyThread = new ReplyThread();
							replyThread.start();
						}
						try {
							Thread.currentThread();
							Thread.sleep(700);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						/*GetReply ShowMap.getReply = new GetReply();
						ShowMap.getReply.start();*/
						//GetReply ShowMap.getReply = new GetReply();
						 if(ShowMap.fGetReply){
			                	ShowMap.getReply.interrupt();
			                	ShowMap.getReply=null;
			                	ShowMap.getReply=new GetReply();
			                	ShowMap.getReply.start();
			                }else{
				                ShowMap.fGetReply=true;
				            	ShowMap.getReply = new GetReply();
								ShowMap.getReply.start();
			                }
						//延迟
						try {
							Thread.currentThread();
							Thread.sleep(700);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//刷新listview
						updateFromSqlite2(atReplyname,ShowMap.mMarkerInfoLy);
					}
				}).setNegativeButton("取消", null).show();
				
			}
			
		});
		return super.getView(position, convertView, parent);
	}

	public MyAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		cont=context;
		mInflater= LayoutInflater.from(context);
		//str_name=from[0];
	}
	
	
	/**
	 * 刷新ListView
	 * @param username
	 * @param mMarkerInfoLy2
	 */
	public void updateFromSqlite2(String username,RelativeLayout mMarkerInfoLy2) {
		// TODO Auto-generated method stub
		DbHelper dbHelper = new DbHelper(cont,username,null,3);
		db = dbHelper.getReadableDatabase();
		ViewHolder viewHolder;
		try{
			//ViewHolder viewHolder = null;
			if(mMarkerInfoLy2.getTag()==null)
			{
				viewHolder =new ViewHolder();
				viewHolder.infoListV=(ListView)mMarkerInfoLy2.findViewById(R.id.listView_del);
				//viewHolder.infoListV.setDescendantFocusability(2);
				mMarkerInfoLy2.setTag(viewHolder);
			}
			viewHolder = (ViewHolder) mMarkerInfoLy2.getTag();
			ArrayList<Map<String,Object>> arr = new ArrayList<Map<String,Object>>();
			Map<String,Object> item;
			Cursor cursor = db.query(username, null, null, null, null, null, null);
			//startManagingCursor(cursor);
			String replyname,atname,msg;
			while(cursor.moveToNext()){
				replyname=cursor.getString(cursor.getColumnIndex("replyname"));
				atname=cursor.getString(cursor.getColumnIndex("username"));
				msg = cursor.getString(cursor.getColumnIndex("msg"));
				item = new HashMap<String,Object>();
				item.put("titlename",replyname);
				item.put("titlename2","@"+atname);
				item.put("message", msg);
				item.put("reply", "reply");
				Log.i("updateFromSqlite2()", replyname+atname+msg);
				arr.add(item);
			}
			db.close();
			MyAdapter adapter = new MyAdapter(cont,arr,R.layout.row,new String[]{"titlename","titlename2","message","reply"}
			,new int[]{R.id.tv1,R.id.tv2,R.id.textView2,R.id.id_btn_at});
			//adapter.notifyDataSetChanged();  //刷新listView
			viewHolder.infoListV.setAdapter(adapter);
			viewHolder.infoListV.invalidate();
	/*		viewHolder.infoListV.setOnItemClickListener(new OnItemClickListener(){
	
				@SuppressWarnings("unchecked")
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					HashMap<String,String> map = (HashMap<String,String>)viewHolder.infoListV.getItemAtPosition(arg2);
					String title =map.get("titlename");
					String content = map.get("message");
					Log.i("OnitemClick() ",title+content);
				}
			});*/
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
