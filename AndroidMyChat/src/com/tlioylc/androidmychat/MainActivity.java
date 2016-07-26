package com.tlioylc.androidmychat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.tlioylc.service.ConnectService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends Activity {
	private EditText et;
	Socket Client;
	PrintWriter os;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        init();
       
    }

    private void init() {
		// TODO Auto-generated method stub
    	 et = (EditText)findViewById(R.id.et);
    	 Intent intent = new Intent();
    	 intent.setClass(getApplicationContext(), ConnectService.class);
    	 this.getApplicationContext().bindService(intent , conn, Context.BIND_AUTO_CREATE);
	}
   
    
	public void onClickConnection(View v){
//		Thread t = new MyInetThread(new MyCallBack(){
//
//			@Override
//			public void OnFail() {
//				// TODO Auto-generated method stub
//				System.out.println("Fail");
//			}
//
//			@Override
//			public void OnSuccess(Socket Client1, PrintWriter os1) {
//				// TODO Auto-generated method stub
//				Client  = Client1;
//				os  = os1;
//			}
//			
//		}, Client, os);
//		
//		t.start();
//    
    }
    
    public void onClickSendMessage(View v){
//	    			os.write(et.getText().toString());
//	    	    	os.flush();
    }
 
    public void onClickDisConnection(View v){
//    	 new Thread(new Runnable() {
//	    		public void run() {
//	    			  try {
//	    					Client.close();
//	    				} catch (IOException e) {
//	    					// TODO Auto-generated catch block
//	    					e.printStackTrace();
//	    				}
//	    		}
//	    	}).start();
 	 
    }
}
