package com.bsu.bakerstreet42.listener;
/**
 * 用来监听读取ndef数据操作
 * @author fengchong
 *
 */
public interface OnNfcReadListener {
	public void read(String data);
}
