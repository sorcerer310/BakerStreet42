package com.bsu.bakerstreet42.tools;

import com.bsu.bakerstreet42.listener.OnNdefReadListener;
import com.bsu.promevideo.tools.NFCDataUtils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
/**
 * 公共类，用来帮助一个Activity快速构建成一个支持Ndef的Activity
 * @author fengchong
 *
 */
public class NdefActivityHelper {
	private Activity activity;				//要操作的Activity
	private NfcAdapter adapter;				//Nfc设备代理
	private PendingIntent pintent;			//意图对象
	private OnNdefReadListener listener;	//读取操作的监听器

	public NdefActivityHelper(Activity a,NfcAdapter nfca,PendingIntent pi){
		activity = a;
		adapter = nfca;
		pintent = pi;
	}
	
	/**
	 * 用于Activity的onCreate执行
	 */
	public void onCreate(){
	}
	/**
	 * 用于在Activity中的onResume里执行
	 */
	public void onResume(){
		if(adapter != null)
			adapter.enableForegroundDispatch(activity, pintent, null, null);
	}
	/**
	 * 用于在Activity中的onPause里的执行
	 */
	public void onPause(){
		if(adapter != null)
			adapter.disableForegroundDispatch(activity);
	}
	/**
	 * 设置读取nfc数据的监听器
	 * @param l
	 */
	public void setOnNFCReadListener(OnNdefReadListener l){
		listener = l;
	}
	
	/**
	 * 用在Activity中的onNewIntent里的执行
	 * @param intent	传入的意图对象，其中包含标签的数据
	 */
	public void onNewIntent(Intent intent){
		//当读取到一个ACTION_TAG_DISCOVERED标签
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String tagtype = NFCDataUtils.witchMifareType(tag);
			if(tagtype.equals("MifareClassic")){
				if(listener!=null)
					listener.read(NFCDataUtils.readMifareClassicData(tag));
			}
			else if(tagtype.equals("MifareUltralight")){
				if(listener!=null)
					listener.read(NFCDataUtils.readMifareUltralightDataByPage(tag, 8));
//					listener.read(NFCDataUtils.readMifareUltralightData(tag));
			}

		//当读到一个ACTION_NDEF_DISCOVERED数据
		}else if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
			
		}
	}
}