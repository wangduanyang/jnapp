package com.dc.jnapp_v1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	static private SQLiteDatabase db2;
	static public boolean fdrop=false;
	public DbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		if(fdrop==true){
			db2 = this.getWritableDatabase();
			// TODO Auto-generated constructor stub
			//清空本地数据库，再放入访问远程数据库得到的数据
			db2.execSQL("drop table if exists "+name);
			//db2.close();
			Log.i("dbhelper() trop table ",name);
			fdrop=false;
		}
		String sql="create table if not exists "+name+
				 " (replyname varchar(20) ,username varchar(20) , msg text)";
		db2=this.getWritableDatabase();
		db2.execSQL(sql);
		db2.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql="create table if not exists "+ShowMap.username
				+ "(replyname varchar(20),username varchar(20), msg text)";
		db.execSQL(sql);
		//db2=db;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists "+ShowMap.username);
		Log.d("DbHelper","onUpgraded");
		onCreate(db);
	}
	
}
