package com.bsu.bakerstreet42;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bsu.bakerstreet42.listener.OnNfcReadListener;
import com.bsu.bakerstreet42.tools.NfcActivityHelper;
import com.bsu.bakerstreet42.tools.Utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	
	//nfc帮助类Handler
	private NfcHelperHandler nfchandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//初始化设备
		adapter = NfcAdapter.getDefaultAdapter(this);
		//截获Intent,使用当前的Activity
		pintent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		
		initNfcHelper();
		initMessage();
	}
	/*
	 * 初始化NfcHelper
	 */
	private void initNfcHelper(){
		nfchandler = new NfcHelperHandler(this);
		//初始化nfc数据帮助类
		nfchelper = new NfcActivityHelper(this,adapter,pintent);
		nfchelper.onCreate();
		//当读取到nfc数据时的操作
		nfchelper.setOnNFCReadListener(new OnNfcReadListener(){
			@Override
			public void read(String data) {
				Bundle bundle = new Bundle();
				Message msg = new Message();
				if(data.equals("bk42-lr002")){
					bundle.putString("id", "bk42-lr002");
					bundle.putString("title","002");
					bundle.putInt("lrcpath", R.raw.c001);
					bundle.putInt("oggpath", R.raw.r001);
					msg.setData(bundle);
					MainActivity.this.nfchandler.sendMessage(msg);
				}else if(data.equals("bk42-lr003")){
					bundle.putString("id", "bk42-lr003");
					bundle.putString("title","003");
					bundle.putInt("lrcpath", R.raw.c001);
					bundle.putInt("oggpath", R.raw.r001);
					msg.setData(bundle);
					MainActivity.this.nfchandler.sendMessage(msg);
				}else if(data.equals("bk42-lr004")){
					bundle.putString("id", "bk42-lr004");
					bundle.putString("title","004");
					bundle.putInt("lrcpath", R.raw.c001);
					bundle.putInt("oggpath", R.raw.r001);
					msg.setData(bundle);
					MainActivity.this.nfchandler.sendMessage(msg);
				}
			}});	
	}
	
	/**
	 * 初始化收件箱消息部分
	 */
	private void initMessage(){
		lv_message = (ListView) findViewById(R.id.lv_message);
		if(listdata==null){
			listdata = new ArrayList<Map<String,Object>>();
		//增加序章数据
		listdata.add(Utils.makeListItemData("bk42-lr001", "序章", R.raw.l001, R.raw.r001));
		
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
				intent.putExtra("title", mapitem.get("title").toString());				//传送标题到下一个界面
				intent.putExtra("lrcpath", (int)mapitem.get("lrcpath"));				//歌词路径 
				intent.putExtra("oggpath", vpath+((int)mapitem.get("oggpath")));		//传送播放路径到下一个界面
				MainActivity.this.startActivity(intent);
			}});
		}
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
	/**
	 * 操作nfchelper
	 * @author fengchong
	 *
	 */
	public class NfcHelperHandler extends Handler{
		private MainActivity me;
		public NfcHelperHandler(MainActivity m){
			me = m;
		}
		@Override
		public void handleMessage(Message msg){
			boolean additem = true;							//默认增加数据标示为true
			
			Bundle bundle = msg.getData();					//获得handle传送过来得消息
			String id = bundle.getString("id");				//获得要增加的项目的id
			//遍历所有的list项目如果该项目已存在，设置增加数据的标识为false
			for(Map<String,Object> m:me.listdata){			
				if(m.get("id").toString().equals(id)){
					additem = false;
					break;
				}
			}
			//增加数据
			if(additem){
				me.listdata.add(Utils.makeListItemData(bundle.getString("id")
						, bundle.getString("title")
						, bundle.getInt("lrcpath")
						, bundle.getInt("oggpath")));
				me.sa.notifyDataSetChanged();
			}
		}
	}
}
