package com.bsu.bakerstreet42;

import java.io.File;

import com.bsu.bakerstreet42.lrc.LrcView;
import com.bsu.bakerstreet42.tools.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class RadioActivity extends Activity {
	private VideoView vv;
	private MediaController mc;
	private TextView tv;
	private TextView tv_content;
	private LrcView lrc;
	private Button bt_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radio);
		
		//通过意图对象获得要显示的标题，歌词文件资源，ogg声音文件路径
		Intent intent = this.getIntent();
		String title = intent.getStringExtra("title");
		int lrcpath = intent.getIntExtra("lrcpath", 0);
		String oggpath = intent.getStringExtra("oggpath");
		Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
		
		vv = (VideoView) findViewById(R.id.vv);
		mc = new MediaController(this);
		mc.show(0);											//让控制条一直显示
		tv = (TextView) findViewById(R.id.tv_videotitle);
		tv.setText(title);

		bt_back = (Button) findViewById(R.id.bt_back);
		bt_back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
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

		vv.setOnPreparedListener(new OnPreparedListener(){
			@Override
			public void onPrepared(MediaPlayer arg0) {
				vv.start();
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 当歌曲还在播放时
						// 就一直调用changeCurrent方法
						// 虽然一直调用， 但界面不会一直刷新
						// 只有当唱到下一句时才刷新
						while(vv.isPlaying()) {
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

		
		vv.setMediaController(mc);
		vv.setVideoURI(Uri.parse(oggpath));
		vv.requestFocus();
//		vv.start();
	}
}
