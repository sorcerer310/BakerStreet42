package com.bsu.bakerstreet42;

import java.io.File;
import java.util.Map;

import com.bsu.bakerstreet42.widget.LrcView;
import com.bsu.bakerstreet42.widget.MediaControllerNoHide;

import android.app.Activity;
import android.content.Intent;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radio);
		this.setTitle("贝克街42号－威廉古堡之狼人");
		
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
		bt_back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//设置按钮不可用
				arg0.setEnabled(false);
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
		flag_lrc = true;
	}
}
