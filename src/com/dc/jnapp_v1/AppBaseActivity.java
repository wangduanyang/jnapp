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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class AppBaseActivity extends ActionBarActivity {
	private CheckBox login_check;
	public EditText edt1;
	public EditText edt2;
	final int TIMEOUT_MILLISEC=8000;
	private static String username=""; 
	SendDataThread sendDataThread;
	GetDataThread getDataThread;
	boolean fRunning;
	boolean fRunning_login_check;
	boolean fLogin_check;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_base);
		edt1=(EditText)findViewById(R.id.etUserName);
		edt2=(EditText)findViewById(R.id.etPassWord);
		
		//记住密码
		//读取数据放入edi1,edit2
		login_check=(CheckBox)findViewById(R.id.checkBox1);
		SharedPreferences remdname=getPreferences(Activity.MODE_PRIVATE);
		String name_str=remdname.getString("name", "");
		String pass_str=remdname.getString("pass", "");
		edt1.setText(name_str);
		edt2.setText(pass_str);
		login_check.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					SharedPreferences remdname=getPreferences(Activity.MODE_PRIVATE);
					SharedPreferences.Editor edit=remdname.edit();
					edit.putString("name", edt1.getText().toString());
					edit.putString("pass",edt2.getText().toString());
					edit.commit();
				}else{
					SharedPreferences remdname=getPreferences(Activity.MODE_PRIVATE);
					SharedPreferences.Editor edit=remdname.edit();
					edit.putString("name", "");
					edit.putString("pass", "");
					edit.commit();
				}
			}
			
		});
		//renren btn
	    Button btn=(Button)findViewById(R.id.renn_btn);
        btn.setBackgroundResource(R.drawable.guide_btn_blue_selector);
        btn.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v){
        		Intent intent = new Intent(AppBaseActivity.this, LoginActivity.class);
                startActivity(intent);
                //AppBaseActivity.this.finish();
        	}
        });
        
        //注册
        Button btn_reg=(Button)findViewById(R.id.button2);
        btn_reg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*//sqlite实现
				String edt1val=edt1.getText().toString();
				String edt2val=edt2.getText().toString();
					
				DbHelper helper=new DbHelper(getApplicationContext(),"member.db",null,3);
				SQLiteDatabase db=helper.getWritableDatabase();
				Cursor result=db.rawQuery("select * from member where UserName='"+edt1val
						+"'",null);//检查是否已存在
				if(result.getCount()!=0){
					//Toast.makeText(getApplication(), "the username has already existed!", RESULT_OK).show();
				}else{
				String sql="insert into member(UserName,PassWord) values('"+edt1val+"','"+edt2val+"')";
				//Toast.makeText(getApplication(), edt1val+" reg succeed", RESULT_OK).show();
				db.execSQL(sql);
				}
				result.close();
				db.close();*/
				
				//构造json发送到service_send.php
				fRunning=true;
				sendDataThread=new SendDataThread();
				sendDataThread.start();
			}
		});
        
        //登录
        Button btn_log=(Button)findViewById(R.id.id_btn_log);
        btn_log.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				edt1=(EditText)findViewById(R.id.etUserName);
//				edt2=(EditText)findViewById(R.id.etPassWord);
				fRunning_login_check=true;
				getDataThread = new GetDataThread();
				getDataThread.start();
				
			/*	try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(fLogin_check){
					if(login_check.isChecked()){
					SharedPreferences remdname=getPreferences(Activity.MODE_PRIVATE);
					SharedPreferences.Editor edit=remdname.edit();
					edit.putString("name", edt1.getText().toString());
					edit.putString("pass", edt2.getText().toString());
					edit.commit();
					}
					Toast.makeText(getApplicationContext(), "login succeed", RESULT_OK).show();
					Intent intent=new Intent(AppBaseActivity.this, ShowMap.class);
					startActivity(intent);
					finish();
	
				}else{
					Toast.makeText(getApplicationContext(), "Username or Password is wrong!", Toast.LENGTH_LONG).show();
				}
				*/
			}
		});
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app_base, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if(id==R.id.Exit){
			//finish();
			System.exit(0);
		}
		return super.onOptionsItemSelected(item);
	}
	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		AppBaseActivity.username = username;
	}
	//get data from mysql and login
	public class GetDataThread extends Thread{
		public Handler mHandler;
		@Override
		public void run(){
			
			while(fRunning_login_check){
			Looper.prepare();mHandler = new Handler(){
				public void handleMessage(Message msg){
					//process incoming message here
				}
			};
			//Toast.makeText(getApplicationContext(), "getdatathread", Toast.LENGTH_LONG).show();
//			HttpParams httpParams = new BasicHttpParams();
//			HttpConnectionParams.setConnectionTimeout(httpParams,TIMEOUT_MILLISEC);
//			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
//			
//			HttpParams p=new BasicHttpParams();
//			p.setParameter("user","1");
			//p.setParameter("format", "json");
			//HttpClient httpclient = new DefaultHttpClient(p);
			//String url ="http://myjnapp.sinaapp.com/login_check.php";
			//HttpPost httppost = new HttpPost(url);
			try{
			/*	Log.i(getClass().getSimpleName(),"send task - start");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("user","1"));
				//nameValuePairs.add(new BasicNameValuePair("format","json"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,responseHandler);
				//解析json返回的JSONobject
				JSONObject json=new JSONObject(responseBody);
				JSONArray jArray= json.getJSONArray("posts");
				JSONObject e = jArray.getJSONObject(0);
				String s = e.getString("flag");
				ArrayList<HashMap<String,String>> mylist = new ArrayList<HashMap<String,String>>();
				for(int i=0;i<jArray.length();i++){
					HashMap<String,String> map = new HashMap<String,String>();
					JSONObject e = jArray.getJSONObject(i);
					String s = e.getString("post");
					JSONObject jObject=new JSONObject(s);
					
					map.put("id", ""+jObject.getInt("id"));
					map.put("Username", jObject.getString("name"));
					map.put("Password",jObject.getString("password"));
					map.put("Msg", jObject.getString("msg"));
					mylist.add(map);
				}*/

				JSONObject json3= new JSONObject();
				json3.put("Username", edt1.getText().toString());
				json3.put("Password", edt2.getText().toString());
				HttpParams httpParams2 = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams2,TIMEOUT_MILLISEC);
				HttpConnectionParams.setSoTimeout(httpParams2, TIMEOUT_MILLISEC);
				HttpClient client = new DefaultHttpClient(httpParams2);
				String url2 ="http://myjnapp.sinaapp.com/login_check.php";
				HttpPost request = new HttpPost(url2);
				request.setEntity(new ByteArrayEntity(json3.toString().getBytes("UTF8")));
				request.setHeader("json",json3.toString());
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = client.execute(request,responseHandler);
				//解析json2返回的JSONobject
				JSONObject json2=new JSONObject(responseBody);
				JSONArray jArray= json2.getJSONArray("posts");
				JSONObject e = jArray.getJSONObject(0);
				String s = e.getString("flag");
				if(Integer.parseInt(s)==1){
					fLogin_check=true;
					setUsername(edt1.getText().toString());
					if(login_check.isChecked()){
						SharedPreferences remdname=getPreferences(Activity.MODE_PRIVATE);
						SharedPreferences.Editor edit=remdname.edit();
						edit.putString("name", edt1.getText().toString());
						edit.putString("pass", edt2.getText().toString());
						edit.commit();
						}
						Toast.makeText(getApplicationContext(), "login succeed", RESULT_OK).show();
						Intent intent=new Intent(AppBaseActivity.this, ShowMap.class);
						startActivity(intent);
						finish();
				}else{
					fLogin_check=false;
					Toast.makeText(getApplicationContext(), "Username or Password is wrong!",
							Toast.LENGTH_LONG).show();	
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			fRunning_login_check=false;
			this.interrupt();
			Looper.loop();
			}
		}

	}
	//send data to mysql and register
	public class SendDataThread extends Thread{
		public Handler mHandler;
		@Override
		public void run(){
			
			while(fRunning){
			Looper.prepare();
			mHandler = new Handler(){
				public void handleMessage(Message msg){
					//process incoming message here
					
				}
			};
			
			//构造json发送到service_send.php
			
			try {
				JSONObject json= new JSONObject();
				json.put("Username", edt1.getText().toString());
				json.put("Password", edt2.getText().toString());
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams,TIMEOUT_MILLISEC);
				HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
				HttpClient client = new DefaultHttpClient(httpParams);
				String url ="http://myjnapp.sinaapp.com/reg_check.php";
				HttpPost request = new HttpPost(url);
				request.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
				request.setHeader("json",json.toString());
//				HttpResponse response = client.execute(request);
//				HttpEntity entity=response.getEntity();
//				if(entity!=null){
//					InputStream instream = entity.getContent();
//					String res=RestClient.convertStreamToString(instream);
//					Log.i("Read form server",res);
//					//Toast.makeText(getApplication(), res, Toast.LENGTH_LONG).show();
//				}
				
				//================
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = client.execute(request,responseHandler);
				//解析json返回的JSONobject
				JSONObject json2=new JSONObject(responseBody);
				JSONArray jArray= json2.getJSONArray("posts");
				//ArrayList<HashMap<String,String>> mylist = new ArrayList<HashMap<String,String>>();
				//for(int i=0;i<jArray.length();i++){
				//HashMap<String,String> map = new HashMap<String,String>();
				JSONObject e = jArray.getJSONObject(0);
				String s = e.getString("flag");
				//map.put("flag",s);
					
				//	mylist.add(map);
				//}
				//HashMap<String,String> map2 = new HashMap<String,String>();
				//map2=mylist.get(0);
//				Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
				if(Integer.parseInt(s)==1){
					Toast.makeText(getApplicationContext(), "Username has existed!Try again!", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(), "register success!", Toast.LENGTH_LONG).show();
				}
				//Toast.makeText(getApplicationContext(), map2.get("flag").toString(), Toast.LENGTH_LONG).show();
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fRunning=false;
			this.interrupt();
			Looper.loop();
			}
		}

	}
	

}
