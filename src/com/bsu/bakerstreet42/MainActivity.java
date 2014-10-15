package com.bsu.bakerstreet42;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.bsu.bakerstreet42.listener.OnNfcReadListener;
import com.bsu.bakerstreet42.tools.NfcActivityHelper;
import com.bsu.bakerstreet42.tools.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
//	private NfcHelperHandler nfchandler;
	
	//程序列表持久数据，防止玩家退出程序再进入获得的数据不对，如要重置需要在游戏重置功能操作
	private SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//初始化设备
		adapter = NfcAdapter.getDefaultAdapter(this);
		//截获Intent,使用当前的Activity
		pintent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		//初始化代理数据
		if(listdata==null)
			listdata = new ArrayList<Map<String,Object>>();
		
		initPreferences();
		initNfcHelper();
		initMessage();
		initResetGameDialog();
		
	}

	/**
	 * 初始化持久数据
	 */
	private void initPreferences(){
		settings = this.getSharedPreferences("ListDatas", MODE_PRIVATE);
		Editor editor = settings.edit();
		
		//先初始化第一条数据
		editor.putBoolean("bk42-lr001", true);
		editor.commit();
		
		listdata.clear();
		//初始化数据并将已扫描的数据加入到列表中
		makeListDataInitPreByID("bk42-lr001");
		makeListDataInitPreByID("bk42-lr002");
		makeListDataInitPreByID("bk42-lr003");
		makeListDataInitPreByID("bk42-lr004");
		makeListDataInitPreByID("bk42-lr005");
		makeListDataInitPreByID("bk42-lr006");
		makeListDataInitPreByID("bk42-lr007");
	}
	/**
	 * 如果id对应的数据在持久数据中存在，则增加到列表中
	 * 如果id对应的数据在持久数据中不存在，则在持久数据中初始化该数据
	 * 用于程序启动初始化持久数据
	 * @param id	对应的资源id
	 */
	private void makeListDataInitPreByID(String id){
		if(settings.contains(id) && settings.getBoolean(id, false))
			listdata.add(makeListDataByID(id));
		else{
			Editor editor = settings.edit();
			editor.putBoolean(id, false);
			editor.commit();
		}
	}
	
	/**
	 * 根据数据项的id生成一条列表数据
	 * @param id	列表的id
	 * @return		返回该id对应的资源数据
	 */
	private Map<String,Object> makeListDataByID(String id){
		if(id.equals("bk42-lr001")){
			return Utils.makeListItemData("bk42-lr001","001",R.raw.c001,R.raw.r001);
		}else if(id.equals("bk42-lr002")){
			return Utils.makeListItemData("bk42-lr002","002",R.raw.c001,R.raw.r001);
		}else if(id.equals("bk42-lr003")){
			return Utils.makeListItemData("bk42-lr003","003",R.raw.c001,R.raw.r001);
		}else if(id.equals("bk42-lr004")){
			return Utils.makeListItemData("bk42-lr004", "004", R.raw.c001, R.raw.r001);
		}else if(id.equals("bk42-lr005")){
			return Utils.makeListItemData("bk42-lr005", "005", R.raw.c001, R.raw.r001);
		}else if(id.equals("bk42-lr006")){
			return Utils.makeListItemData("bk42-lr006", "006", R.raw.c001, R.raw.r001);
		}else if(id.equals("bk42-lr007")){
			return Utils.makeListItemData("bk42-lr007", "007", R.raw.c001, R.raw.r001);
		}
		return null;
	}
	/**
	 * 将一个id增加到持久数据中
	 * @param id	id编号
	 */
	private void addListDataToPrefences(String id){
		Editor editor = settings.edit();
		editor.putBoolean(id, true);
		editor.commit();
	}
	
	/*
	 * 初始化NfcHelper
	 */
	private void initNfcHelper(){
//		nfchandler = new NfcHelperHandler(this);
		//初始化nfc数据帮助类
		nfchelper = new NfcActivityHelper(this,adapter,pintent);
		nfchelper.onCreate();
		//当读取到nfc数据时的操作
		nfchelper.setOnNFCReadListener(new OnNfcReadListener(){
			@Override
			public void read(String data) {
				//如果列表中包含当前的数据，则不增加该数据到列表中
				for(Map<String,Object> map:listdata){
					if(map.get("id").equals(data)){
						return;
					}
				}
				if(data.equals("bk42-lr002")){
					listdata.add(Utils.makeListItemData("bk42-lr002","002",R.raw.c001,R.raw.r001));
					addListDataToPrefences(data);
				}else if(data.equals("bk42-lr003")){
					listdata.add(Utils.makeListItemData("bk42-lr003","003",R.raw.c001,R.raw.r001));
					addListDataToPrefences(data);
				}else if(data.equals("bk42-lr004")){
					listdata.add(Utils.makeListItemData("bk42-lr004","004",R.raw.c001,R.raw.r001));
					addListDataToPrefences(data);
				}else if(data.equals("bk42-lr005")){
					listdata.add(Utils.makeListItemData("bk42-lr005","005",R.raw.c001,R.raw.r001));
					addListDataToPrefences(data);
				}else if(data.equals("bk42-lr006")){
					listdata.add(Utils.makeListItemData("bk42-lr006","006",R.raw.c001,R.raw.r001));
					addListDataToPrefences(data);
				}else if(data.equals("bk42-lr007")){
					listdata.add(Utils.makeListItemData("bk42-lr007","007",R.raw.c001,R.raw.r001));
					addListDataToPrefences(data);
				}
				sa.notifyDataSetChanged();						
				
				
//				Bundle bundle = new Bundle();
//				Message msg = new Message();
//				if(data.equals("bk42-lr002")){
//					bundle.putString("id", "bk42-lr002");
//					bundle.putString("title","002");
//					bundle.putInt("lrcpath", R.raw.c001);
//					bundle.putInt("oggpath", R.raw.r001);
//					msg.setData(bundle);
//					MainActivity.this.nfchandler.sendMessage(msg);
//				}else if(data.equals("bk42-lr003")){
//					bundle.putString("id", "bk42-lr003");
//					bundle.putString("title","003");
//					bundle.putInt("lrcpath", R.raw.c001);
//					bundle.putInt("oggpath", R.raw.r001);
//					msg.setData(bundle);
//					MainActivity.this.nfchandler.sendMessage(msg);
//				}else if(data.equals("bk42-lr004")){
//					bundle.putString("id", "bk42-lr004");
//					bundle.putString("title","004");
//					bundle.putInt("lrcpath", R.raw.c001);
//					bundle.putInt("oggpath", R.raw.r001);
//					msg.setData(bundle);
//					MainActivity.this.nfchandler.sendMessage(msg);
//				}
			}});	
	}
	

	

	/**
	 * 初始化收件箱消息部分
	 */
	private void initMessage(){
		lv_message = (ListView) findViewById(R.id.lv_message);
		
		//增加序章数据
//		listdata.add(Utils.makeListItemData("bk42-lr001", "序章", R.raw.l001, R.raw.r001));
		
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
//		}
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
//	public class NfcHelperHandler extends Handler{
//		private MainActivity me;
//		public NfcHelperHandler(MainActivity m){
//			me = m;
//		}
//		@Override
//		public void handleMessage(Message msg){
//			boolean additem = true;							//默认增加数据标示为true
//			
//			Bundle bundle = msg.getData();					//获得handle传送过来得消息
//			String id = bundle.getString("id");				//获得要增加的项目的id
//			//遍历所有的list项目如果该项目已存在，设置增加数据的标识为false
//			for(Map<String,Object> m:me.listdata){			
//				if(m.get("id").toString().equals(id)){
//					additem = false;
//					break;
//				}
//			}
//			//增加数据
//			if(additem){
//				me.listdata.add(Utils.makeListItemData(bundle.getString("id")
//						, bundle.getString("title")
//						, bundle.getInt("lrcpath")
//						, bundle.getInt("oggpath")));
//				me.sa.notifyDataSetChanged();
//			}
//		}
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);  
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		 int id = item.getItemId();
		if(id==R.id.action_settings){
			Toast.makeText(this, "action_setting", Toast.LENGTH_SHORT).show();
			dlg_rstgame.show();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private AlertDialog dlg_rstgame;
	/**
	 * 初始化输入密码对话框
	 */
	private void initResetGameDialog(){
		//获得密码编辑框的view
		LayoutInflater li = LayoutInflater.from(this);
		View gameResetDialogView = li.inflate(R.layout.game_reset_dialog, null);
		
		//获得密码编辑框
		final EditText et = (EditText) gameResetDialogView.findViewById(R.id.et_password);
		
		//定义输入密码的对话框
		dlg_rstgame = new AlertDialog.Builder(this)
			.setTitle("游戏重置密码")
			.setView(gameResetDialogView)
			.setPositiveButton("确定", new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					if(et.getText().toString().equals("12345")){
						Toast.makeText(MainActivity.this, "重置游戏成功", Toast.LENGTH_SHORT).show();
						//密码正确则清除数据
						Editor editor = MainActivity.this.settings.edit();
						editor.clear();
						editor.commit();
						MainActivity.this.initPreferences();
					}
					else
						Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
					dialog.dismiss();	
				}})
			.setNeutralButton("取消", null)
			.create();
	}
	

}
