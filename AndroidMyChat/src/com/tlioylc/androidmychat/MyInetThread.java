package com.tlioylc.androidmychat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;

public class MyInetThread extends Thread{

	private Socket Client;
	private PrintWriter os;
	private MyCallBack mc;
	public MyInetThread(MyCallBack mc , Socket Client ,PrintWriter os){
		this.Client = Client;
		this.os = os;
		this.mc = mc;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Client = new Socket("192.168.0.104", 7777);
			os = new PrintWriter(Client.getOutputStream());
			 final Reader reader = new InputStreamReader(Client.getInputStream());  
			 new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
				     Reader reader = null;
					try {
						reader = new InputStreamReader(Client.getInputStream());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}  
					 char chars[] = new char[1024];  
				      int len;  
				      StringBuffer sb = new StringBuffer();  
				      while(true){
				    	  try {
//								while ((len=reader.read(chars)) != -1) {  
									len=reader.read(chars);
								     sb.append(new String(chars, 0, len));  
//								  }
								System.out.println("from server: " + sb);  
								sb.delete(0,sb.length());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}  
						      
						      
				      }
				      
				}
			}).start();
		     
			mc.OnSuccess(Client ,os);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.run();
	}
	
	
}
