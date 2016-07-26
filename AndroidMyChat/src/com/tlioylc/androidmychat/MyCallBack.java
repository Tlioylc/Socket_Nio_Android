package com.tlioylc.androidmychat;

import java.io.PrintWriter;
import java.net.Socket;

public abstract class MyCallBack {
	public abstract  void OnFail();
	public abstract  void OnSuccess(Socket Client ,PrintWriter os);
}
