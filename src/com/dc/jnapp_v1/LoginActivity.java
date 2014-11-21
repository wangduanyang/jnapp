package com.dc.jnapp_v1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;

public class LoginActivity extends Activity implements OnClickListener{
	private RennClient rennClient;
	private Button loginBtn;
	private static final String APP_ID = "168802";

	private static final String API_KEY = "e884884ac90c4182a426444db12915bf";

	private static final String SECRET_KEY = "094de55dc157411e8a5435c6a7c134c5";
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		rennClient = RennClient.getInstance(this);
		rennClient.init(APP_ID, API_KEY, SECRET_KEY);
		rennClient
				.setScope("read_user_blog read_user_photo read_user_status read_user_album "
						+ "read_user_comment read_user_share publish_blog publish_share "
						+ "send_notification photo_upload status_update create_album "
						+ "publish_comment publish_feed");
		setContentView(R.layout.welcome);
		loginBtn = (Button) findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(this);
		//initView();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.login_btn){
			rennClient.setLoginListener(new LoginListener(){

				@Override
				public void onLoginCanceled() {
					// TODO Auto-generated method stub
					loginBtn.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoginSuccess() {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "µÇÂ¼³É¹¦", Toast.LENGTH_SHORT).show();
					loginBtn.setVisibility(View.GONE);
					Intent intent = new Intent(LoginActivity.this, ShowMap.class);
	                startActivity(intent);
	                //WelcomeActivity.this.finish();
				}
			});
			rennClient.login(this);	
			
		}
	}

}
