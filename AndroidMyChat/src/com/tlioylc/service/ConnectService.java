package com.tlioylc.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class ConnectService extends Service {
	private static final long HEART_BEAT_RATE = 3 * 1000;
	public static final String HOST = "192.168.0.104";// "192.168.1.21";//
	public static final int PORT = 7777;
	private WeakReference<Socket> mSocket;
	private Handler mHandler = new Handler();
	private long sendTime = 0L;
	
	private ReadThread mReadThread;
	private Runnable heartBeatRunnable = new Runnable() {

		@Override
		public void run() {
			if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
				boolean isSuccess = sendMsg("");
				if (!isSuccess) {
					mHandler.removeCallbacks(heartBeatRunnable);
					mReadThread.release();
					releaseLastSocket(mSocket);
					new InitSocketThread().start();
				}
			}
			mHandler.postDelayed(this, HEART_BEAT_RATE);
		}
	};
	public boolean sendMsg(String msg) {
		if (null == mSocket || null == mSocket.get()) {
			return false;
		}
		Socket soc = mSocket.get();
		try {
			if (!soc.isClosed() && !soc.isOutputShutdown()) {
				OutputStream os = soc.getOutputStream();
				String message = msg + "\r\n";
				os.write(message.getBytes());
				os.flush();
				sendTime = System.currentTimeMillis();
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		new InitSocketThread().start();
	}
	class InitSocketThread extends Thread {
		@Override
		public void run() {
			super.run();
			initSocket();
		}
	}
	
	private void initSocket() {
		try {
			Socket so = new Socket(HOST, PORT);
			mSocket = new WeakReference<Socket>(so);
			mReadThread = new ReadThread(so);
			mReadThread.start();
			mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void releaseLastSocket(WeakReference<Socket> mSocket) {
		try {
			if (null != mSocket) {
				Socket sk = mSocket.get();
				if (!sk.isClosed()) {
					sk.close();
				}
				sk = null;
				mSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ReadThread extends Thread {
		private WeakReference<Socket> mWeakSocket;
		private boolean isStart = true;

		public ReadThread(Socket socket) {
			mWeakSocket = new WeakReference<Socket>(socket);
		}

		public void release() {
			isStart = false;
			releaseLastSocket(mWeakSocket);
		}

		@Override
		public void run() {
			super.run();
			Socket socket = mWeakSocket.get();
			if (null != socket) {
				try {
					InputStream is = socket.getInputStream();
					byte[] buffer = new byte[1024 * 4];
					int length = 0;
					while (!socket.isClosed() && !socket.isInputShutdown()
							&& isStart && ((length = is.read(buffer)) != -1)) {
						if (length > 0) {
							String message = new String(Arrays.copyOf(buffer,
									length)).trim();

//							if(message.equals("ok")){
//								//Heart beat action
//								Intent intent=new Intent(HEART_BEAT_ACTION);
//								mLocalBroadcastManager.sendBroadcast(intent);
//							}else{
//								//Message action
//								Intent intent=new Intent(MESSAGE_ACTION);
//								intent.putExtra("message", message);
//								mLocalBroadcastManager.sendBroadcast(intent);
//							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
//	private Socket Client;
//	private PrintWriter os;
//	private Handler mHandler = new Handler();
//	
//	private final long  HEART_BEAT_RATE = 1000;
//	private long sendTime = 0L;
//	private Runnable mHeartRunnable = new Runnable(){
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
//				boolean isSuccess = sendMsg("");
//				System.out.println("mHeartRunnableSend");
//			}
//			System.out.println("mHeartRunnable");
//			mHandler.postDelayed(this, HEART_BEAT_RATE);
//		}
//
//		
//	};
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		onClickConnection.start();
//	}
//
//	
//	private boolean sendMsg(String string) {
//		// TODO Auto-generated method stub
//		if(!Client.isClosed() && !Client.isOutputShutdown()) {
//			try {
//				os = new PrintWriter(Client.getOutputStream());
//				os.write(string);
//				os.flush();
//				System.out.println("sendMsg"+os);
//				sendTime = System.currentTimeMillis();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			return true;
//		}
//		return false;
//	}
//	@Override
//	public IBinder onBind(Intent intent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	public Thread onClickConnection = new Thread(new Runnable() {
//		
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			try {
//				Client = new Socket("192.168.0.104", 7777);
//				mHandler.postDelayed(mHeartRunnable, HEART_BEAT_RATE);
//				Reader.start();
//			} catch (UnknownHostException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	});
//	
//	private Thread Reader = new Thread(new Runnable() {
//		
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			 new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//				     Reader reader = null;
//					try {
//						reader = new InputStreamReader(Client.getInputStream());
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}  
//					 char chars[] = new char[1024];  
//				      int len;  
//				      StringBuffer sb = new StringBuffer();  
//				      while(true){
//				    	  try {
////								while ((len=reader.read(chars)) != -1) {  
//									len=reader.read(chars);
//								     sb.append(new String(chars, 0, len));  
////								  }
//								System.out.println("from server: " + sb);  
//								sb.delete(0,sb.length());
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}  
//						      
//						      
//				      }
//				      
//				}
//			}).start();
//		}
//	});
//	
//	
////	 public void onClickDisConnection(View v){
////    	 new Thread(new Runnable() {
////	    		public void run() {
////	    			  try {
////	    					Client.close();
////	    				} catch (IOException e) {
////	    					// TODO Auto-generated catch block
////	    					e.printStackTrace();
////	    				}
////	    		}
////	    	}).start();
//// 	 
////    }

}
