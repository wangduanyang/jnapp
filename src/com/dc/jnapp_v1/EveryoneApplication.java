package com.dc.jnapp_v1;


import android.app.Application;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;


public class EveryoneApplication extends Application {
	public LocationClient mLocationClient;
	
	public MyLocationListener mMyLocationListener;
	private ShowMap sm;
	public TextView mLocationResult,logMsg;
	public TextView trigger,exit;


	
	
	@Override
	public void onCreate() {
		super.onCreate(); 
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		sm=new ShowMap();
	}

	
	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location 
			
			sm.connectMysql(location.getLatitude(),location.getLongitude(),ShowMap.inputText);
			//addMarker(location.getLatitude(),location.getLongitude());
			//mLocationClient.stop();
			
		/*	StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
			logMsg(sb.toString());
			Log.i("BaiduLocationApiDem", sb.toString());*/
		}

		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}


	}
	
	
	/**
	 * 显示请求字符串
	 * @param str
	 */
/*	public void logMsg(String str) {
		try {
			if (mLocationResult != null)
				mLocationResult.setText(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	

}
	