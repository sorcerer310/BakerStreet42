package com.bsu.bakerstreet42;

import com.bsu.bakerstreet42.tools.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class RadioActivity extends Activity {
	private VideoView vv;
	private MediaController mc;
	private TextView tv;
	private TextView tv_content;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radio);
		
		Intent intent = this.getIntent();
		String title = intent.getStringExtra("title");
		int cpath = intent.getIntExtra("cpath", 0);
		String vpath = intent.getStringExtra("vpath");
		Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
		
		vv = (VideoView) findViewById(R.id.vv);
		mc = new MediaController(this);
		mc.show(0);											//让控制条一直显示
		tv = (TextView) findViewById(R.id.tv_videotitle);
		tv.setText(title);
		
		tv_content = (TextView)findViewById(R.id.tv_content);
		tv_content.setText(Utils.getString(this.getResources().openRawResource(cpath)));
		
		vv.setMediaController(mc);
		vv.setVideoURI(Uri.parse(vpath));
		vv.requestFocus();
		vv.start();
	}
}
