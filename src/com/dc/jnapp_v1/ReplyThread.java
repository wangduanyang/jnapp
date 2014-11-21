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

import android.os.Handler;
import android.util.Log;

public class ReplyThread extends Thread {
	public Handler mHandler;
	private int TIMEOUT_MILLISEC=8000;
	@Override
	public void run() {
		// TODO Auto-generated method stub
	while(MyAdapter.fReply){
		try {
			JSONObject json= new JSONObject();
			json.put("Username",MyAdapter.atReplyname);
			json.put("Replyname", AppBaseActivity.getUsername());
			json.put("Infoname", ShowMap.infoname);  //一个marker对应一个infoname
			Log.i("replyThread() ShowMap.infoname ",ShowMap.infoname);
			json.put("Msg", MyAdapter.inputText3);
			
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
			//解析json返回的JSONobject
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
		MyAdapter.fReply=false;
		//this.interrupt();
	}
		super.run();
		}
	}