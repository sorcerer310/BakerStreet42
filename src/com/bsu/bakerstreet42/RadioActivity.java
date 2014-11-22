package com.bsu.bakerstreet42;

import java.io.File;
import java.util.Map;

import com.bsu.bakerstreet42.listener.OnNfcReadListener;
import com.bsu.bakerstreet42.tools.NfcActivityHelper;
import com.bsu.bakerstreet42.widget.LrcView;
import com.bsu.bakerstreet42.widget.MediaControllerNoHide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class RadioActivity extends Activity {
	private VideoView vv;
	private MediaControllerNoHide mc;
//	private MediaController mc;
	private TextView tv;
	private TextView tv_content;
	private LrcView lrc;
	private Button bt_back;
	private RelativeLayout rl_root;
	private boolean flag_lrc = false;
	
	//nfc部分
	private NfcActivityHelper nfchelper;
	private SharedPreferences settings;
	//视频音频资源路径
	private String vpath = "android.resource://com.bsu.bakerstreet42/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radio);
		this.setTitle("贝克街42号－威廉古堡之狼人");

		//初始化nfc
		initNfcHelper();
		settings = this.getSharedPreferences("ListDatas", MODE_PRIVATE);
		
		//通过意图对象获得要显示的标题，歌词文件资源，ogg声音文件路径
		Intent intent = this.getIntent();
		String title = intent.getStringExtra("title");
		int lrcpath = intent.getIntExtra("lrcpath", 0);
		String oggpath = intent.getStringExtra("oggpath"); 
		int imgpath = intent.getIntExtra("imgpath", 0);
		
		Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
		
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
		Resources res = this.getResources();
		rl_root.setBackground(res.getDrawable(imgpath));
		rl_root.setAlpha(0.9f);
		
		vv = (VideoView) findViewById(R.id.vv);
//		mc = new MediaController(this);
		mc = new MediaControllerNoHide(this);
		tv = (TextView) findViewById(R.id.tv_videotitle);
		tv.setTypeface(Typeface.createFromAsset(getAssets(), "fzzy.ttf"));
		tv.setText(title);

		bt_back = (Button) findViewById(R.id.bt_back);
		bt_back.setAlpha(1.0f);
		bt_back.setEnabled(true);
		bt_back.setText("返回");
		bt_back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//设置按钮不可用
				Button bt = (Button) arg0;
				bt.setEnabled(false);
				bt.setText("正在返回...");
				//先停止线程
				flag_lrc = false;
				//转到主界面
				Intent intent = new Intent(RadioActivity.this,MainActivity.class);
				RadioActivity.this.startActivity(intent);
			}
		});
		
//		tv_content = (TextView)findViewById(R.id.tv_content);
//		tv_content.setText(Utils.getString(this.getResources().openRawResource(cpath)));

		try {
			//测试滚动歌词控件
			lrc = (LrcView) findViewById(R.id.lrc);
			lrc.setLrcPath(this.getResources().openRawResource(lrcpath));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		flag_lrc = true;
		vv.setOnPreparedListener(new OnPreparedListener(){
			@Override
			public void onPrepared(MediaPlayer arg0) {
//				vv.start();
				if(!mc.isShown())
					mc.show(0);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 当歌曲还在播放时
						// 就一直调用changeCurrent方法
						// 虽然一直调用， 但界面不会一直刷新
						// 只有当唱到下一句时才刷新
//						while(vv.isPlaying()) {
						while(flag_lrc){
							// 调用changeCurrent方法， 参数是当前播放的位置
							// LrcView会自动判断需不需要下一行
							RadioActivity.this.lrc.changeCurrent(vv.getCurrentPosition());
							
							// 当然这里还是要睡一会的啦
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}).start();
			}});

		
		vv.setVideoURI(Uri.parse(oggpath));
		vv.requestFocus();
		vv.setMediaController(mc);
		mc.show(0);
		vv.start();
		
	}
	//初始化nfc帮助类
	private void initNfcHelper(){
		nfchelper = new NfcActivityHelper(this);
		nfchelper.onCreate();
		nfchelper.setOnNFCReadListener(new OnNfcReadListener(){
			@Override
			public void read(String data) {
//				if(data.equals("preferencesClear"))
//					return;
//				
				//如果列表中包含当前的数据，则不增加该数据到列表中
//				for(Map<String,Object> map:listdata){
//					if(map.get("id").equals(data)){
//						startRadioActivity(map.get("title").toString(),(Integer)map.get("lrcpath"),
//								vpath+((Integer)map.get("oggpath")),(Integer)map.get("imgpath"));
//						return;
//					}
//				}
				
				System.out.println("========RadioActivity read:"+data);
				//根据数据id增加数据
				if(data.equals("bk42-lr001")){
					startRadioActivityAndAddPreferencesData(data,"凯瑟琳公主",R.raw.l001,R.raw.r001,R.drawable.i001);
				}else if(data.equals("bk42-lr002")){
					startRadioActivityAndAddPreferencesData(data,"米洛陶洛斯",R.raw.l002,R.raw.r002,R.drawable.i002);
				}else if(data.equals("bk42-lr003")){
					startRadioActivityAndAddPreferencesData(data,"法拉奥",R.raw.l003,R.raw.r003,R.drawable.i003);
				}else if(data.equals("bk42-lr004")){
					startRadioActivityAndAddPreferencesData(data,"阿喀琉斯",R.raw.l004,R.raw.r004,R.drawable.i004);
				}else if(data.equals("bk42-lr005")){
					startRadioActivityAndAddPreferencesData(data,"塔纳托斯",R.raw.l005,R.raw.r005,R.drawable.i005);
				}else if(data.equals("bk42-lr006")){
					startRadioActivityAndAddPreferencesData(data,"美杜莎",R.raw.l006,R.raw.r006,R.drawable.i006);
				}else if(data.equals("bk42-lr007")){
					startRadioActivityAndAddPreferencesData(data,"奥丁",R.raw.l007,R.raw.r007,R.drawable.i007);
				}
			}});
	}
	/**
	 * 
	 * @param id
	 * @param title
	 * @param lid
	 * @param rid
	 * @param iid
	 */
	private void startRadioActivityAndAddPreferencesData(String id,String title,int lid,int rid,int iid){
		addListDataToPrefences(id);
		
		//根据当前得数据跳转到下一界面自动播放
		Intent intent = new Intent(RadioActivity.this,RadioActivity.class);
		intent.putExtra("title", title);					//传送标题到下一个界面
		intent.putExtra("lrcpath", lid);					//歌词路径 
		intent.putExtra("oggpath", vpath+rid);				//传送播放路径到下一个界面
		intent.putExtra("imgpath", iid);					//传送背景图片到下一个界面
		RadioActivity.this.startActivity(intent);		
	}
	/**
	 * 增加数据到持久数据中
	 * @param id
	 */
	private void addListDataToPrefences(String id){
		Editor editor = settings.edit();
		editor.putBoolean(id, true);
		editor.commit();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		flag_lrc = false;
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		flag_lrc = false;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		nfchelper.onResume();
		flag_lrc = true;
	}
	

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
//		nfchelper.onNewIntent(intent);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		nfchelper.onPause();
	}
}
