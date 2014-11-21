package com.dc.jnapp_v1;

import java.io.IOException;

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

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

public class GetReply extends Thread {
	public Handler mHandler;
	static boolean  runed=false;
	private int TIMEOUT_MILLISEC=8000;
	//static public List<MsgInfo> Msginfos = new ArrayList<MsgInfo>();  
	static int I=0;
	//sqlite本地数据库
	static final int DELAY = 60000;//等待时间1分钟
	//DbHelper dbHelper;
	SQLiteDatabase db;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	while(ShowMap.fGetReply){
		try {
			JSONObject json= new JSONObject();
			json.put("username", ShowMap.username);
			//Log.i("in GetReply Thread",ShowMap.reply_name);
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpClient client = new DefaultHttpClient(httpParams);
			String url ="http://myjnapp.sinaapp.com/getReply.php";
			HttpPost request = new HttpPost(url);
			request.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
			request.setHeader("json",json.toString());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = client.execute(request,responseHandler);
			//解析json返回的JSONobject
			JSONObject json2=new JSONObject(responseBody);
			JSONArray jArray= json2.getJSONArray("posts");
			//ArrayList<HashMap<String,String>> mylist = new ArrayList<HashMap<String,String>>();
			DbHelper.fdrop=true;//drop 之前的数据表
			DbHelper dbHelper = new DbHelper(ShowMap.context,ShowMap.username,null,3);
			db=dbHelper.getWritableDatabase();
			for(int i=0;i<jArray.length();i++){
			//	HashMap<String,String> map = new HashMap<String,String>();
				JSONObject e = jArray.getJSONObject(i);
				String s = e.getString("post");
				JSONObject jObject=new JSONObject(s);
				
			/*	//map.put("id", ""+jObject.getInt("id"));
				map.put("Name", jObject.getString("Name"));
				map.put("Replyname", jObject.getString("Replyname"));
				map.put("Msg", jObject.getString("Msg"));
				mylist.add(map);*/
				//==========
				/*MsgInfo msgInfo = new MsgInfo(jObject.getString("Replyname"),jObject.getString("Name"),
						jObject.getString("Msg"));
				updateMsgInfo(jObject.getString("Replyname"),jObject.getString("Name"),
						jObject.getString("Msg"),msgInfo);*/
				
				//===========put data in SQlite
				//dbHelper = new DbHelper(ShowMap.context,ShowMap.username,null,3);
				//直接插入
				ContentValues values = new ContentValues();
				values.clear();
				values.put("replyname",jObject.getString("replyname"));  
				values.put("username", jObject.getString("username"));  
				values.put("msg", jObject.getString("msg"));
				db.insertOrThrow(ShowMap.username, null, values);
			}
			db.close();
//			Log.i("in GetReply Thread Msginfos.toString()",Msginfos.toString());
		/*	try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		runed=true;
		ShowMap.fGetReply=false;
		
		Log.i("GetReply Thread runed ",""+(I++));
	}
		super.run();
}
	
	
	//update msginfos
/*	public static void updateMsgInfo(String replyname,String name,String msg,MsgInfo msginfo){
		//update infos
		int index = 0;
	
		for(int i=0;i<Msginfos.size();i++){
			MsgInfo msg_info=Msginfos.get(i);
			String replyname2 = msg_info.getReplyname();
			String name2=msg_info.getName();
			String msg2 = msg_info.getMsg();
//			Log.i("info.getname()",username);
//			Log.i("AppBaseActivity.getusername()",AppBaseActivity.getUsername());
			if (replyname.equals(replyname2) && name.equals(name2) && msg.equals(msg2)) {
				//index = i;
				//Log.i("uername==name","true");
				//Msginfos.set(i, msginfo);
				break;
			}
			index = i+1;
		}
		if(index==Msginfos.size())
		{
			Msginfos.add(msginfo);
		}
	}*/

	}