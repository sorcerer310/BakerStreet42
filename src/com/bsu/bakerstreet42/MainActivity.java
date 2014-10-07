package com.bsu.bakerstreet42;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bsu.bakerstreet42.listener.OnNfcReadListener;
import com.bsu.bakerstreet42.tools.NfcActivityHelper;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {
	private NfcAdapter adapter;				//Nfc设备代理
	private PendingIntent pintent;			//意图对象
	private NfcActivityHelper nfchelper;	//帮助类
	
	//列表控件
	private ListView lv_message;
	private List<Map<String,Object>> listdata;
	private SimpleAdapter sa;
	
	//视频音频资源路径
	private String vpath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//初始化设备
		adapter = NfcAdapter.getDefaultAdapter(this);
		//截获Intent,使用当前的Activity
		pintent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		//初始化nfc数据帮助类
		nfchelper = new NfcActivityHelper(this,adapter,pintent);
		nfchelper.onCreate();
		//当读取到nfc数据时的操作
		nfchelper.setOnNFCReadListener(new OnNfcReadListener(){
			@Override
			public void read(String data) {
				System.out.println("=============data");
//				if(data.equals("bk42-lr002")){
					Map map = new HashMap<String,Object>();
					map.put("id", "bk42-lr001");
					map.put("title","序章");
					map.put("path", R.raw.v001);
					listdata.add(map);
//				}else if(data.equals("bk42-lr003")){
//					Map map = new HashMap<String,Object>();
//					map.put("id", data);
//					map.put("content", "视频3");
//					map.put("image", R.drawable.msg);
//					list.add(map);
//				}else if(data.equals("bk42-lr004")){
//					Map map = new HashMap<String,Object>();
//					map.put("id", data);
//					map.put("content", "视频4");
//					map.put("image", R.drawable.msg);
//					list.add(map);
//				}
			}});

		initMessage();
	}
	/**
	 * 初始化收件箱消息部分
	 */
	private void initMessage(){
		lv_message = (ListView) findViewById(R.id.lv_message);
		listdata = new ArrayList<Map<String,Object>>();
		//增加序章数据
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", "bk42-lr001");
		map.put("title","序章");
		map.put("cpath", R.raw.c001);
		map.put("vpath", R.raw.r001);
		listdata.add(map);
		
		sa = new SimpleAdapter(this,listdata,R.layout.listitem
				,new String[]{"title"}
				,new int[]{R.id.item_title});
		
		lv_message.setAdapter(sa);
		vpath = "android.resource://com.bsu.bakerstreet42/";
		
		lv_message.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,long id) {
				Intent intent = new Intent(MainActivity.this,RadioActivity.class);
				Map<String,Object> mapitem = listdata.get(position);
				intent.putExtra("title", mapitem.get("title").toString());			//传送标题到下一个界面
				intent.putExtra("cpath", (int)mapitem.get("cpath"));				//歌词路径 
				intent.putExtra("vpath", vpath+((int)mapitem.get("vpath")));		//传送播放路径到下一个界面
				MainActivity.this.startActivity(intent);
			}});
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		nfchelper.onNewIntent(intent);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		nfchelper.onPause();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		nfchelper.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//解惑back键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}

		if (KeyEvent.KEYCODE_HOME == keyCode)
			return true;
		return super.onKeyDown(keyCode, event);
	}
}
