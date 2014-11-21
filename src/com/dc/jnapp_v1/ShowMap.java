package com.dc.jnapp_v1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

public class ShowMap extends ActionBarActivity{
	public static MapView mMapView ;
	public static LocationClient mLocationClient ;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	public static String inputText ,inputText2;
	public static TextView tv;//��infowindow����ʾ
	//public TextView tv2;
	public static EditText edittext;
	public Marker marker;
	public static AlertDialog alertDia;
	final static int TIMEOUT_MILLISEC=8000;
	static PutMsgThread putMsgThread;
	static UpdateThread updateThread;
	static boolean fPutMsgRunning=false;
	static boolean fUpdateRunning=false;
	private static double static_lat,static_lng;
    public static List<Info> infos = new ArrayList<Info>();  
    Info myinfo;
	private int index;
	static RelativeLayout mMarkerInfoLy;
	ListView lv;
	ListView lv_del;
	private boolean flag=false;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	
	public static String reply_name;//������
	static GetReply getReply;
	static boolean fGetReply=false;
	public static String username;
	static public Context context;
	ViewHolder viewHolder = null;
	private BaiduMap mBaidumap;
	static RelativeLayout lv_del_ly;
	static String infoname;
	static public Info info2;
	static int zan2=0;
	public int msgsDbVersion=3;
	static String strmsg;
	static String strname;
	//private UpdateThread updateThread2;
/*    static  
    {  
        infos.add(new Info(31.242652, 120.971171, R.drawable.ic_launcher,"a", "Ӣ�׹���С�ù�",  
                "����209��", 1456));  
        infos.add(new Info(31.242952, 120.972171, R.drawable.ic_launcher, "b","ɳ������ϴԡ����",  
                "����897��", 456));  
        infos.add(new Info(34.242852, 108.973171, R.drawable.ic_launcher, "�廷��װ��",  
                "����249��", 1456));  
        infos.add(new Info(34.242152, 108.971971, R.drawable.ic_launcher, "���׼�����С��",  
                "����679��", 1456));  
    }  */
	// private String tempcoor="gcj02";
	// BaiduMap mBaidumap=mMapView.getMap();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		context=getApplicationContext();
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.map);
		// ��ȡ��ͼ�ؼ�����
		
		mMapView = (MapView) findViewById(R.id.bmapView);

		Button btn_show=(Button)findViewById(R.id.show);
		btn_show.setOnClickListener(btnShow_listener);
			
		Button btn_dia=(Button)findViewById(R.id.dialog);
		btn_dia.setOnClickListener(btnDia_listener);
		
		lv_del = (ListView)findViewById(R.id.listView_del);
		
		
		if(fUpdateRunning){
			updateThread.interrupt();
			updateThread=null;
			updateThread=new UpdateThread();
			updateThread.start();
		}else{
			fUpdateRunning=true;
			updateThread=new UpdateThread();
			updateThread.start();
		}
		
		mMarkerInfoLy=(RelativeLayout)findViewById(R.id.id_marker_info); 
		//lv_del_ly = (RelativeLayout)findViewById(R.id.id_del_lv);
		//lv=(ListView)findViewById(R.id.listView_del);
		initMap();
	}
	public void initMap(){
		mBaidumap=mMapView.getMap();
		//������λͼ��
		mBaidumap.setMyLocationEnabled(true);
		LatLng pt0=new LatLng(31.489074,120.279228);
		MapStatus mMapStatus = new MapStatus.Builder()
		.target(pt0)
		.zoom(17)
		.build();
		MapStatusUpdate mMapStatusUpdate=MapStatusUpdateFactory.newMapStatus(mMapStatus);
		mBaidumap.setMapStatus(mMapStatusUpdate);//���µ�ͼ״̬
		
		
		
		//addInfosOverlay(infos);
		//����marker����¼�
		 mBaidumap.setOnMarkerClickListener(new OnMarkerClickListener()  
	        {  
	            @Override  
	            public boolean onMarkerClick(final Marker marker)  
	            {  
	                //���marker�е�����  
	                Info info = (Info) marker.getExtraInfo().get("info");  
	                  
	         /*       InfoWindow mInfoWindow;  
	                //����һ��TextView�û��ڵ�ͼ����ʾInfoWindow  
	                TextView tv2 = new TextView(getApplication());
					tv2.setId(2);
					tv2.setBackgroundResource(R.drawable.popup);
					tv2.setWidth(100);
					tv2.setMaxLines(4);
					tv2.setMaxWidth(15);
					tv2.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
					tv2.setPadding(30, 20, 30, 50);
					tv2.setText(info.getName()+info.getMsg());
	                TextView location = new TextView(getApplicationContext());  
	                location.setBackgroundResource(R.drawable.popup);  
	                location.setPadding(30, 20, 30, 50);  
	                location.setText(info.getName());  
	                //��marker���ڵľ�γ�ȵ���Ϣת������Ļ�ϵ�����  
	                final LatLng ll = marker.getPosition();  
	                Point p = mBaidumap.getProjection().toScreenLocation(ll);  
	                //Log.e(TAG, "--!" + p.x + " , " + p.y);
	                p.y -= 47;  
	                LatLng llInfo = mBaidumap.getProjection().fromScreenLocation(p);  
	                //Ϊ������InfoWindow��ӵ���¼�  
	                mInfoWindow = new InfoWindow(tv2, llInfo,  
	                        new OnInfoWindowClickListener()  
	                        {  
	  
	                            @Override  
	                            public void onInfoWindowClick()  
	                            {  
	                                //����InfoWindow  
	                                mBaidumap.hideInfoWindow();  
	                            }  
	                        });  
	                //��ʾInfoWindow  
	                mBaidumap.showInfoWindow(mInfoWindow);  */
	                //������ϸ��Ϣ����Ϊ�ɼ�  
	                username = info.getName();
	                infoname=username;
	                mMarkerInfoLy.setVisibility(View.VISIBLE);  
	                //������ϢΪ��ϸ��Ϣ����������Ϣ  
	                popupInfo(mMarkerInfoLy, info);
	               
	                //put msg in mysql
	             /*   MyAdapter.inputText3 = info.getMsg();
	                ReplyThread replyThread = new ReplyThread();
	                replyThread.start();*/
	                
	                //put msg in sqlite
	                //putMsgSqlite(info);
	                if(fGetReply){
	                	getReply.interrupt();
	                	getReply=null;
	                	getReply=new GetReply();
	                	getReply.start();
	                }else{
		                fGetReply=true;
		            	getReply = new GetReply();
						getReply.start();
	                }
	                //���������ڸ���֮ǰ��ˢ��listview,ʹ����ʾΪ�յ����
					try {
						//Thread.currentThread();
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//updateMsgInfo(mMarkerInfoLy,GetReply.Msginfos);
					 //�ӱ������ݿ��ȡ���ݣ�����ListView
	                updateFromSqlite(info.getName(),mMarkerInfoLy);
	                flag=true; //omMapclick()��־λ
	                return true;  
	            }  
	        }); 
		 //��ӵ�ͼ�ĵ����¼������س��ֵ���ϸ��Ϣ���ֺ�InfoWindow 
		 mBaidumap.setOnMapClickListener(new OnMapClickListener()
		 {

			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				mMarkerInfoLy.setVisibility(View.GONE);
				lv_del.setVisibility(View.GONE);
				flag=false;
				mBaidumap.hideInfoWindow();
			}

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			 
		 });
		 
	/*	 mBaidumap.setOnMapDoubleClickListener(new OnMapDoubleClickListener(){

			@Override
			public void onMapDoubleClick(LatLng arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplication(), "map double clicked", Toast.LENGTH_SHORT).show();
				popupInfoDel(lv_del,infos);
			}
			 
		 });*/
		 //��ͼ�����¼�
		 mBaidumap.setOnMapLongClickListener(new OnMapLongClickListener(){

			@Override
			public void onMapLongClick(LatLng arg0) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplication(), "map long clicked", Toast.LENGTH_SHORT).show();
				//lv_del_ly.setVisibility(View.VISIBLE);
				flag=true;
				popupInfoDel(lv_del,infos);
			}
			 
		 });
	}
	/**
	 * ����ɾ��info����
	 * @param infos2
	 */
	protected void popupInfoDel(ListView lv_del2,List<Info> infos2) {
		// TODO Auto-generated method stub
		lv_del2.setVisibility(View.VISIBLE);
		//ArrayList<Map<String,Object>> arr_del=new ArrayList<Map<String,Object>>();
		//Map<String,Object> item;
	/*	for(Info info:infos){
			item=new HashMap<String,Object>();
			item.put("name", info.getName());
			arr_del.add(item);
		}*/
		MsgDbHelper msgDbHelper=new MsgDbHelper(context,"Msgs",null,msgsDbVersion);
		SQLiteDatabase db = msgDbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from Msgs where fshow=?", new String[]{"1"});
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter dellvAdapter = new SimpleCursorAdapter(this, R.layout.dellv_row,cursor,new String[]{"username","msg"},
				new int[]{R.id.tv2del,R.id.tvdelMsg});
	/*	while(cursor.moveToNext()){
			item=new HashMap<String,Object>();
			item.put("name", cursor).getString(cursor.getColumnIndex("username"));
		}
		SimpleAdapter dellvAdapter=new SimpleAdapter(this,arr_del,R.layout.dellv_row,new String[]{"name"},new int[]{R.id.tv2del});*/
		lv_del2.setAdapter(dellvAdapter);
		lv_del2.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//
			
				Cursor cursor = (Cursor)lv_del.getItemAtPosition(arg2);
				strname=cursor.getString(cursor.getColumnIndex("username"));
				//strmsg=cursor.getString(cursor.getColumnIndex("msg"));
			/*	final String msg=map.get("msg");
				final double lat=map.get("lat");
				final double lng=map.get("lng");*/
				new AlertDialog.Builder(ShowMap.this).setTitle("ȷ��Ҫɾ����")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						updateSqlite4DelMark(strname);
					}

					private void updateSqlite4DelMark(String name) {
						// TODO Auto-generated method stub
						//��sqlite��fshow��Ϊ0
						MsgDbHelper msgDbHelper=new MsgDbHelper(context,"Msgs",null,msgsDbVersion);
						SQLiteDatabase db= msgDbHelper.getWritableDatabase();
						//Cursor cursor=db.update("Msgs",values, new String[]{name});
						db.execSQL("update Msgs set fshow=0 where username='"+name+"'");
						db.close();
					}
				}).setNegativeButton("ȡ��", null).show();
				//Toast.makeText(getApplicationContext(), "onitemClicked", Toast.LENGTH_SHORT).show();
			}
			
		});
	}
	/**
	 * �ѷ�����Ϣ���뱾�����ݿ�
	 * @param info
	 */
	/*
	protected void putMsgSqlite(Info info) {
		// TODO Auto-generated method stub
		dbHelper = new DbHelper(this,username,null,3);
		//��������Ƿ��Ѵ���
		db= dbHelper.getReadableDatabase();
		Cursor cursor=db.rawQuery("select * from "+username+" where replyname= ? and username= ? and msg = ?", new String[]{
				info.getName(),info.getName(),info.getMsg()});
		Log.i("putMsgSqlite() ",cursor.getCount()+"");
		if(cursor.getCount()==0){
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("replyname", info.getName());
			values.put("username", info.getName());
			values.put("msg", info.getMsg());
			db.insertOrThrow(username, null, values);
		}else{
			//�����ظ�������ɾ�������
			db = dbHelper.getWritableDatabase();
			db.delete(username, "replyname=? and username=? and msg=?", new String[]{
					info.getName(),info.getName(),info.getMsg()});
			ContentValues values = new ContentValues();
			values.put("replyname", info.getName());
			values.put("username", info.getName());
			values.put("msg", info.getMsg());
			db.insertOrThrow(username, null, values);
		}
		db.close();
	}*/
	/**
	 * �ӱ������ݿ��ȡ���ݣ�����ListView
	 */
	public void updateFromSqlite(String username,RelativeLayout mMarkerInfoLy2) {
		// TODO Auto-generated method stub
		
		Log.i("updateFromSqlite()", ShowMap.username);
		//DbHelper.fdrop = true;
		dbHelper = new DbHelper(this,username,null,3);
		db = dbHelper.getReadableDatabase();
		try{
			//ViewHolder viewHolder = null;
			if(mMarkerInfoLy2.getTag()==null)
			{
				viewHolder =new ViewHolder();
				viewHolder.infoListV=(ListView)mMarkerInfoLy2.findViewById(R.id.info_listView);
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
				Log.i("updateFromSqlite()", replyname+atname+msg);
				arr.add(item);
			}
			db.close();
			MyAdapter adapter = new MyAdapter(this,arr,R.layout.row,new String[]{"titlename","titlename2","message","reply"}
			,new int[]{R.id.tv1,R.id.tv2,R.id.textView2,R.id.id_btn_at});
			//adapter.notifyDataSetChanged();  //ˢ��listView
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
	//���ؼ����
	@Override
	public void onBackPressed(){
		if(flag==true){
			mMarkerInfoLy.setVisibility(View.GONE);
			lv_del.setVisibility(View.GONE);
			flag=false;
		}else{
			super.onBackPressed();
		}
	}

	protected void popupInfo(RelativeLayout mMarkerInfoLy2, Info info) {
		// TODO Auto-generated method stub
		info2=info;
		if(mMarkerInfoLy2.getTag()==null)
		{
			viewHolder =new ViewHolder();
			viewHolder.infoZanImg=(ImageView) mMarkerInfoLy2.findViewById(R.id.info_zan_image);
			viewHolder.infoListV=(ListView) mMarkerInfoLy2.findViewById(R.id.info_listView);
			viewHolder.infoName=(TextView) mMarkerInfoLy2.findViewById(R.id.info_name);
			viewHolder.infoDistance=(TextView) mMarkerInfoLy2.findViewById(R.id.info_distance);
			viewHolder.infoZan = (TextView) mMarkerInfoLy2.findViewById(R.id.info_zan);
			viewHolder.mbtn_shuaxin = (Button)mMarkerInfoLy2.findViewById(R.id.id_shuaxin);
			mMarkerInfoLy2.setTag(viewHolder);
		}
		viewHolder = (ViewHolder) mMarkerInfoLy2.getTag();
	/*	ArrayList<Map<String,Object>> arr = new ArrayList<Map<String,Object>>();
		Map<String,Object> item;
		item = new HashMap<String,Object>();
		item.put("name",info.getName()+"@"+info.getName());
		item.put("message", info.getMsg());
		item.put("reply", "reply");
		arr.add(item);
		MyAdapter adapter = new MyAdapter(this,arr,R.layout.row,new String[]{"name","message","reply"}
		,new int[]{R.id.tv1,R.id.textView2,R.id.id_btn_at});
		adapter.notifyDataSetChanged();//���ݸı�ʱˢ��listView
		viewHolder.infoListV.setAdapter(adapter);*/
		//viewHolder.infoZanImg.setImageResource(info.getImgId());
		//viewHolder.infoZanImg.setImageResource(R.id.info_zan_image);
		viewHolder.infoZanImg.setTag(info.getZan());
		viewHolder.infoZanImg.setOnClickListener(zan);
		viewHolder.infoName.setText(info.getName());
		viewHolder.infoDistance.setText(info.getDistance());
		viewHolder.infoZan.setText(info.getZan()+"");
		viewHolder.mbtn_shuaxin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("shuaxin_button clicked ","yes" );
			      if(fGetReply){
	                	getReply.interrupt();
	                	getReply=null;
	                	getReply=new GetReply();
	                	getReply.start();
	                }else{
		                fGetReply=true;
		            	getReply = new GetReply();
						getReply.start();
	                }
			      try {
						//Thread.currentThread();
						Thread.sleep(600);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				updateFromSqlite(info2.getName(),mMarkerInfoLy);
			}
			
		});
		//username = info.getName();
		//put info to msgInfo
	/*	MsgInfo msgInfo = new MsgInfo();
		msgInfo.setName(info.getName());
		msgInfo.setReplyname(info.getName());
		msgInfo.setMsg(info.getMsg());
		GetReply.updateMsgInfo(info.getName(), info.getName(),info.getMsg(), msgInfo);*/
		
		//GetReply.Msginfos.add(msgInfo);
		
	}
	/**
	 * update msginfos
	 * ���»ظ���Ϣ�б�
	 * @param mMarkerInfoLy2
	 * @param info
	 */
/*	protected void updateMsgInfo(RelativeLayout mMarkerInfoLy2, List<MsgInfo> msgInfos) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(mMarkerInfoLy2.getTag()==null)
		{
			viewHolder =new ViewHolder();
			viewHolder.infoListV=(ListView)mMarkerInfoLy2.findViewById(R.id.listView1);
			//viewHolder.infoListV.setDescendantFocusability(2);
			mMarkerInfoLy2.setTag(viewHolder);
		}
		viewHolder = (ViewHolder) mMarkerInfoLy2.getTag();
		//Log.i("updateMsgInfo()", msgInfos.toString());
		ArrayList<Map<String,Object>> arr = new ArrayList<Map<String,Object>>();
		Map<String,Object> item;
		for(int i=0;i<msgInfos.size();i++){
			MsgInfo msgInfo = msgInfos.get(i);
			item = new HashMap<String,Object>();
			item.put("titlename",msgInfo.getReplyname()+"@"+msgInfo.getName());
			item.put("message", msgInfo.getMsg());
			item.put("reply", "reply");
			arr.add(item);
		}
		MyAdapter adapter = new MyAdapter(this,arr,R.layout.row,new String[]{"titlename","message","reply"}
		,new int[]{R.id.tv1,R.id.textView2,R.id.id_btn_at});
		adapter.notifyDataSetChanged();  //ˢ��listView
		viewHolder.infoListV.setAdapter(adapter);
		
	}*/
	
	public void connectMysql(double lat, double lng, String context) {
		// TODO Auto-generated method stub
		static_lat=lat;
		static_lng=lng;
		//start PutMsgThread and then it will put data into mysql
		//put data into mysql 
		if(fPutMsgRunning){
			putMsgThread.interrupt();
			putMsgThread=null;
			putMsgThread=new PutMsgThread();
			putMsgThread.start();
		}else{
			putMsgThread=null;
			putMsgThread=new PutMsgThread();
			putMsgThread.start();
			fPutMsgRunning=true;
		}
		
/*		Info info2=new Info(static_lat,static_lng,R.drawable.ic_launcher,AppBaseActivity.getUsername(),
				"˵:"+context,"juli300",1456);
		updateInfo(AppBaseActivity.getUsername(),info2);*/
		
		//get data from mysql.table_user
		if(fUpdateRunning){
			updateThread.interrupt();
			updateThread=null;
			updateThread=new UpdateThread();
			updateThread.start();
		}else{
			fUpdateRunning=true;
			updateThread=new UpdateThread();
			updateThread.start();
		}
	
	}
	


/*	@SuppressWarnings("unused")
	private void showInfoWindow() {
		// TODO Auto-generated method stub
		final BaiduMap mBaidumap = mMapView.getMap();
		// LatLng pt = new LatLng(39.21345,116.24255);
		mBaidumap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				// ��Ӵ���
				
				 * final Button button=new Button(getApplicationContext());
				 * button.setBackgroundResource(R.drawable.popup);
				 
				View popup = View.inflate(getApplicationContext(),
						R.layout.pop, null);
				popup.setBackgroundResource(R.drawable.popup);
				TextView content = (TextView) popup
						.findViewById(R.id.tv_content);
				content.setText("Hello world");

				final LatLng ll = marker.getPosition();
				Point p = mBaidumap.getProjection().toScreenLocation(ll);
				p.y -= 47;
				LatLng llInfo = mBaidumap.getProjection().fromScreenLocation(p);
				OnInfoWindowClickListener listener = null;
				// button.setText("����λ��");
				listener = new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						LatLng llNew = new LatLng(ll.latitude + 0.005,
								ll.longitude + 0.005);
						marker.setPosition(llNew);
						mBaidumap.hideInfoWindow();
					}
				};

				// InfoWindow mInfoWindow = new
				// InfoWindow(button,llInfo,listener);
				InfoWindow mInfoWindow = new InfoWindow(popup, llInfo, listener);
				mBaidumap.showInfoWindow(mInfoWindow);
				return true;
			}
		});
	}*/

	void addMarker(double d, double e) {
		// TODO Auto-generated method stub
		BaiduMap mBaidumap = mMapView.getMap();
		LatLng point = new LatLng(d, e);
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_marka);
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		marker=(Marker)mBaidumap.addOverlay(option);
		//===============
		/*Bundle bundle = new Bundle();  
	    bundle.putSerializable("info", info);  
	    marker.setExtraInfo(bundle);*/
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}

	OnClickListener btnDia_listener = new Button.OnClickListener(){
		public void onClick(View v){
			edittext = new EditText(ShowMap.this);
			tv=new TextView(ShowMap.this);
			new AlertDialog.Builder(ShowMap.this).setTitle("������")
			.setIcon(android.R.drawable.ic_dialog_info).setView(edittext)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					inputText = edittext.getText().toString();
					// Toast.makeText(ShowMap.this, inputText,
					// Toast.LENGTH_SHORT).show();
					mLocationClient = ((EveryoneApplication) getApplication()).mLocationClient;
					InitLocation();
					if(mLocationClient.isStarted()){
						mLocationClient.requestLocation();
					}else{
						mLocationClient.start();
					}
					
				}
			}).setNegativeButton("ȡ��", null).show();
			
		}
	};
	
/*	public Thread startLocationThread = new Thread(new Runnable(){
		public void run(){
			inputText = edittext.getText().toString();
			// Toast.makeText(ShowMap.this, inputText,
			// Toast.LENGTH_SHORT).show();
			mLocationClient = ((EveryoneApplication) getApplication()).mLocationClient;
			InitLocation();
			if(mLocationClient.isStarted()){
				mLocationClient.requestLocation();
			}else{
				mLocationClient.start();
			}
		}
	});
	*/
	public void btnDialogOnClick(View v) {
		// Toast.makeText(getApplicationContext(), "fuck",
		// Toast.LENGTH_SHORT).show();
		//tv=new TextView(ShowMap.this);
		alertDia = new AlertDialog.Builder(this).setTitle("������")
				.setIcon(android.R.drawable.ic_dialog_info).setView(edittext)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						inputText = edittext.getText().toString();
						// Toast.makeText(ShowMap.this, inputText,
						// Toast.LENGTH_SHORT).show();
						mLocationClient = ((EveryoneApplication) getApplication()).mLocationClient;
						InitLocation();
						if(mLocationClient.isStarted()){
							mLocationClient.requestLocation();
						}else{
							mLocationClient.start();
						}
						
					}
				}).setNegativeButton("ȡ��", null).show();
		
	}

	OnClickListener btnShow_listener = new Button.OnClickListener(){
		
		public void onClick(View v){
		/*	tv=new TextView(ShowMap.this);
			Toast.makeText(getApplicationContext(), "hello baidumap",Toast.LENGTH_SHORT).show();
			mLocationClient = ((EveryoneApplication) getApplication()).mLocationClient;
			InitLocation();
			if(mLocationClient.isStarted()){
				mLocationClient.requestLocation();
			}else{
				mLocationClient.start();
			}*/
			
			//========================��ȡ���ݿ����ݽ��и���
		/*	fUpdateRunning=true;
			//updateThread= new UpdateThread();
			updateThread.start();*/
			//�����ͼ
		/*	mMapView.removeAllViews();
			SDKInitializer.initialize(getApplicationContext());
			setContentView(R.layout.map);
			mMapView = (MapView) findViewById(R.id.bmapView);
			initMap();*/
			
			if(fUpdateRunning){
				updateThread.interrupt();
				updateThread=null;
				updateThread=new UpdateThread();
				updateThread.start();
			}else{
				fUpdateRunning=true;
				updateThread=new UpdateThread();
				updateThread.start();
			}
			
			
		}
	};
	
	
	
	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// ���ö�λģʽ
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		/*
		 * int span=1000; try { span =
		 * Integer.valueOf(frequence.getText().toString()); } catch (Exception
		 * e) { // TODO: handle exception }
		 * option.setScanSpan(span);//���÷���λ����ļ��ʱ��Ϊ5000ms
		 */// option.setIsNeedAddress(checkGeoLocation.isChecked());
		mLocationClient.setLocOption(option);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_map, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.Logout) {
			startActivity(new Intent(ShowMap.this,AppBaseActivity.class));
			finish();
		}
		if(id==R.id.Exit){
			//finish();
			System.exit(0);
		}
		return super.onOptionsItemSelected(item);
	}

	public class PutMsgThread extends Thread {
		public Handler mHandler;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(fPutMsgRunning){
			Looper.prepare();mHandler=new Handler(){

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
				}
				
			};
			try {
				JSONObject json= new JSONObject();
				json.put("Username", AppBaseActivity.getUsername());
				json.put("Msg", inputText);
				json.put("Lat",static_lat);
				json.put("Lng",static_lng);
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams,TIMEOUT_MILLISEC);
				HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
				HttpClient client = new DefaultHttpClient(httpParams);
				String url ="http://myjnapp.sinaapp.com/put_msg.php";
				HttpPost request = new HttpPost(url);
				request.setEntity(new ByteArrayEntity(json.toString().getBytes("utf8")));
				request.setHeader("json",json.toString());
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = client.execute(request,responseHandler);
				//����json���ص�JSONobject
				JSONObject json2=new JSONObject(responseBody);
				JSONArray jArray= json2.getJSONArray("posts");
			
				JSONObject e = jArray.getJSONObject(0);
				String s = e.getString("flag");
				if(Integer.parseInt(s)==1){
					//Toast.makeText(getApplication(), "send msg success", Toast.LENGTH_LONG).show();
					Log.i("PutMsgThread", "success");
				}else{
					Log.i("PutMsgThread","failed");
					//Toast.makeText(getApplication(), "send msg failed", Toast.LENGTH_LONG).show();
				}
				//Toast.makeText(getApplicationContext(), map2.get("flag").toString(), Toast.LENGTH_LONG).show();
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fPutMsgRunning=false;
			Looper.loop();
			this.interrupt();
			super.run();
		}
		}
	}

	public class UpdateThread extends Thread {
		public Handler mHandler;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(fUpdateRunning){
			Looper.prepare();mHandler=new Handler(){

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					//mMapView.invalidate();
					super.handleMessage(msg);
				}
				
			};
			try {
				JSONObject json= new JSONObject();
				json.put("Username", AppBaseActivity.getUsername());
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams,TIMEOUT_MILLISEC);
				HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
				HttpClient client = new DefaultHttpClient(httpParams);
				String url ="http://myjnapp.sinaapp.com/update.php";
				HttpPost request = new HttpPost(url);
				request.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
				request.setHeader("json",json.toString());
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = client.execute(request,responseHandler);
				//����json���ص�JSONobject
				JSONObject json2=new JSONObject(responseBody);
				JSONArray jArray= json2.getJSONArray("posts");
				
				ArrayList<HashMap<String,String>> mylist = new ArrayList<HashMap<String,String>>();
				MsgDbHelper msgDbHelper = new MsgDbHelper(context,"Msgs",null,msgsDbVersion);
				SQLiteDatabase db=msgDbHelper.getWritableDatabase();
				Cursor cursor;
				for(int i=0;i<jArray.length();i++){
					HashMap<String,String> map = new HashMap<String,String>();
					JSONObject e = jArray.getJSONObject(i);
					String s = e.getString("post");
					JSONObject jObject=new JSONObject(s);
					
					//map.put("id", ""+jObject.getInt("id"));
					map.put("Username", jObject.getString("name"));
					map.put("Msg", jObject.getString("msg"));
					map.put("Lat", jObject.getString("Lat"));
					map.put("Lng", jObject.getString("Lng"));
					mylist.add(map);
					double lat=Double.parseDouble(jObject.getString("Lat"));
					double lng=Double.parseDouble(jObject.getString("Lng"));
					String uname=jObject.getString("name");
					String msg="˵:"+jObject.getString("msg");
					//check data
					cursor=db.rawQuery("select * from Msgs where username=?",new String[]{uname});
					if(cursor.getCount()==0){
					//add to sqlite
						ContentValues values=new ContentValues();
						values.clear();
						values.put("username", uname);
						values.put("Lat", lat);
						values.put("Lng", lng);
						values.put("Msg", msg);
						values.put("fshow", 1);
						db.insertOrThrow("Msgs", null, values);
					}else{
						cursor=db.rawQuery("select * from Msgs where username=? and msg=?",new String[]{uname,msg});
						if(cursor.getCount()==0){    //��username������msg��ͬ
							ContentValues values=new ContentValues();
							values.clear();
							values.put("username", uname);
							values.put("Lat", lat);
							values.put("Lng", lng);
							values.put("Msg", msg);
							values.put("fshow", 1);
							db.update("Msgs", values, "username=?", new String[]{uname});
						}else{  //username,msg��ȫ��ͬ,���ظ���
							Log.i("put msg into sqlite","the username and msg already exists!");
						}
					}
			/*		//�����Ϣ��infos
					Info info = new Info(Double.parseDouble(jObject.getString("Lat")),Double.parseDouble(
							jObject.getString("Lng")),R.drawable.ic_launcher,
							jObject.getString("name"),"˵:"+jObject.getString("msg"),"����100",1456);
					//Log.i("jObject.getString('msg')",jObject.getString("msg"));
					updateInfo(jObject.getString("name"),"˵:"+jObject.getString("msg"),info);*/
					
				}
				db.close();
				//addInfosOverlay(infos);
				addOverlayFromSqlite();
				//Log.i("infos.size()",""+infos.size());
				//Log.i("updateThread",mylist.toString());
				} catch (JSONException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//			fUpdateRunning=false;
//			this.interrupt();
			try {
				UpdateThread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Looper.loop();
			super.run();
			}
		}
	}
	
	public void addInfosOverlay(List<Info> infos)  
    {  
		BaiduMap mBaiduMap = mMapView.getMap();
        mBaiduMap.clear();  
        LatLng latLng = null;  
        OverlayOptions overlayOptions = null;  
        Marker marker = null;  
        for (int i=0;i<infos.size();i++)
        {  
        	Info info=infos.get(i);
        	if(info.fshow==true){
        		//λ��
        		latLng = new LatLng(info.getLatitude(), info.getLongitude());  
	            // ͼ��  
	            BitmapDescriptor bitmap = BitmapDescriptorFactory
	    				.fromResource(R.drawable.icon_marka);
	            overlayOptions = new MarkerOptions().position(latLng)
	                    .icon(bitmap).zIndex(5);
	            marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));  
	            Bundle bundle = new Bundle();  
	            bundle.putSerializable("info", info);  
	            marker.setExtraInfo(bundle); 
	    
	            //�������
	            OverlayOptions textOption=new TextOptions()
	            .bgColor(0xAAFFFF00)
	            .fontSize(24)
	            .fontColor(0xAAFF00FF)
	           // .align(10, 10)
	            .text(info.getName()+info.getMsg())
	            .rotate(0)
	            .position(latLng);
	            mBaiduMap.addOverlay(textOption);
        	}
        }  
     /*   // ����ͼ�Ƶ������һ����γ��λ��  
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(static_lat,static_lng));  
        mBaiduMap.setMapStatus(u);*/
    }
	/**
	 * �����ݿ��ȡ���ݣ���ӵ�ͼ������
	 * @param 
	 */
	public void addOverlayFromSqlite() {
		// TODO Auto-generated method stub
		BaiduMap mBaiduMap = mMapView.getMap();
        mBaiduMap.clear();  
        LatLng latLng = null;  
        OverlayOptions overlayOptions = null;  
        Marker marker = null; 
        MsgDbHelper msgDbHelper=new MsgDbHelper(context,"Msgs",null,msgsDbVersion);
        SQLiteDatabase db2=msgDbHelper.getReadableDatabase();
        Cursor cursor=db2.rawQuery("select * from Msgs", null);
        double lat,lng;
        String uname,msg;
        while (cursor.moveToNext())
        {  
        	if(cursor.getInt(cursor.getColumnIndex("fshow"))==1){
        		lat=cursor.getDouble(cursor.getColumnIndex("lat"));
        		lng=cursor.getDouble(cursor.getColumnIndex("lng"));
        		uname=cursor.getString(cursor.getColumnIndex("username"));
        		msg=cursor.getString(cursor.getColumnIndex("msg"));
        		Info info=new Info();
        		info.setLatitude(lat);
        		info.setLongitude(lng);
        		info.setName(uname);
        		info.setMsg(msg);
        		//λ��
        		latLng = new LatLng(lat, lng);  
	            // ͼ��  
	            BitmapDescriptor bitmap = BitmapDescriptorFactory
	    				.fromResource(R.drawable.icon_marka);
	            overlayOptions = new MarkerOptions().position(latLng)
	                    .icon(bitmap).zIndex(5);
	            marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));  
	            Bundle bundle = new Bundle();  
	            bundle.putSerializable("info", info);  
	            marker.setExtraInfo(bundle); 
	    
	            //�������
	            OverlayOptions textOption=new TextOptions()
	            .bgColor(0xAAFFFF00)
	            .fontSize(24)
	            .fontColor(0xAAFF00FF)
	           // .align(10, 10)
	            .text(info.getName()+info.getMsg())
	            .rotate(0)
	            .position(latLng);
	            mBaiduMap.addOverlay(textOption);
        	}
        }  
	}
	//update infos
	public void updateInfo(String username,String msg,Info infoin){
		//update infos
		
		index = 0;
		for(int i=0;i<infos.size();i++){
			Info info=infos.get(i);
			String name = info.getName();
//			Log.i("info.getname()",username);
//			Log.i("AppBaseActivity.getusername()",AppBaseActivity.getUsername());
			if (username.equals(name)) {
				//index = i;
				//Log.i("uername==name","true");
				//��msgû���򲻻����
				//Log.i("msg", msg);
				//Log.i("info.getMsg()",info.getMsg());
				if(!msg.equals(info.getMsg())){
					infos.set(i, infoin);
					//Log.i("!msg.equals(info.getMsg())",!msg.equals(info.getMsg())+"");
				}
				break;
			}
			index = i+1;
		}
		if(index==infos.size())
		{
			infos.add(infoin);
		}
	}
	
	//�ظ���Ϣ
	/*public class ReplyThread extends Thread {
		public Handler mHandler;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Looper.prepare();mHandler=new Handler(){

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					//mMapView.invalidate();
					super.handleMessage(msg);
				}
				
			};
			try {
				JSONObject json= new JSONObject();
				json.put("Username", AppBaseActivity.getUsername());
				json.put("Replyname", reply_name);
				json.put("Msg", inputText2);
				
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams,TIMEOUT_MILLISEC);
				HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
				HttpClient client = new DefaultHttpClient(httpParams);
				String url ="http://myjnapp.sinaapp.com/reply.php";
				HttpPost request = new HttpPost(url);
				request.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
				request.setHeader("json",json.toString());
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = client.execute(request,responseHandler);
				//����json���ص�JSONobject
				JSONObject json2=new JSONObject(responseBody);
				JSONArray jArray= json2.getJSONArray("posts");
			
				JSONObject e = jArray.getJSONObject(0);
				String s = e.getString("flag");
				if(Integer.parseInt(s)==1){
					//Toast.makeText(getApplication(), "send msg success", Toast.LENGTH_LONG).show();
					Log.i("ReplyThread", "success");
				}else{
					Log.i("ReplyThread","failed");
					//Toast.makeText(getApplication(), "send msg failed", Toast.LENGTH_LONG).show();
				}
				//Toast.makeText(getApplicationContext(), map2.get("flag").toString(), Toast.LENGTH_LONG).show();
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			fUpdateRunning=false;
//			this.interrupt();
			Looper.loop();
			super.run();
			}
		}*/
	OnClickListener zan =new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			zan2 = (int) viewHolder.infoZanImg.getTag();
			zan2++;
			viewHolder.infoZan.setText(zan2+"");
			Log.i("zan ",zan2+"");
		}
		
	};
	
}