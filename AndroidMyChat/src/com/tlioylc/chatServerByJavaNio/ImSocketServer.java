package com.tlioylc.chatServerByJavaNio;


import java.io.IOException;  
import java.net.InetSocketAddress;  
import java.net.ServerSocket;  
import java.net.Socket;  
import java.nio.ByteBuffer;  
import java.nio.channels.SelectionKey;  
import java.nio.channels.Selector;  
import java.nio.channels.ServerSocketChannel;  
import java.nio.channels.SocketChannel;  
import java.nio.charset.Charset;  
import java.util.HashMap;  
import java.util.Iterator;
import java.util.Map;  
import java.util.Set;  
  
public class ImSocketServer {  
    private int port = 8888;  
    private Charset cs = Charset.forName("gbk");  
    private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);  
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);  
    private Map<String, SocketChannel> clientsMap = new HashMap<String, SocketChannel>();  
    private static Selector selector;  
    
    public static void main(String[] args) throws IOException {  
        ImSocketServer server = new ImSocketServer(7777);  
        server.listen();  
    }    
    
    public ImSocketServer(int port){  
        this.port = port;  
        try {  
            init();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    private void init() throws IOException{  

    	ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();  
        serverSocketChannel.configureBlocking(false);  
        ServerSocket serverSocket = serverSocketChannel.socket();  
        serverSocket.bind(new InetSocketAddress(port));  
        selector = Selector.open();  
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server start on port:"+port);  
    }  

    private void listen(){  
    	
        while (true) {  
            try {   
            	System.out.println("block....");
            	//block.......
            	selector.select();
//                Set<SelectionKey> selectionKeys = selector.selectedKeys();  
            	   Set<SelectionKey> keySet = selector.selectedKeys();  
                   for(final SelectionKey key : keySet){  
                	   try{
                      	 handle(key);
                          }
                          catch(Exception e){
                          	System.out.println("Error@Handler");
                          }
                   };  
                   keySet.clear();  
//            	 Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
//                while(keyIter.hasNext()){  
//                	SelectionKey key = keyIter.next();
//                    try{
//                    	 handle(key);
//                        }
//                        catch(Exception e){
//                        	System.out.println("Error@Handler");
//                        }
//                }  
//                keyIter.remove();
            } catch (Exception e) {  
                e.printStackTrace();
                
                break;  
            }  
              
        }  
    }  
    
    private boolean handle(SelectionKey selectionKey) throws IOException {  
        ServerSocketChannel server = null;  
        SocketChannel client = null;  
        String receiveText=null;  
        int count=0;  
        if (selectionKey.isAcceptable()) {  
            server = (ServerSocketChannel) selectionKey.channel();  
            client = server.accept();  
            client.configureBlocking(false);  
            client.register(selector, SelectionKey.OP_READ);  
        } else if (selectionKey.isReadable()) {  
            client = (SocketChannel) selectionKey.channel();  
            rBuffer.clear();  
            count = client.read(rBuffer);
            if (count > 0) {  
                rBuffer.flip();  
                receiveText = String.valueOf(cs.decode(rBuffer).array());  
                System.out.println(client.toString()+":"+receiveText);  
                dispatch(client, receiveText);  
                client = (SocketChannel) selectionKey.channel();  
                client.register(selector, SelectionKey.OP_READ);  
            } 
            if(count <= 0){//close and delete clientChannel from map
            	 client = (SocketChannel) selectionKey.channel();  
                 client.register(selector, SelectionKey.OP_READ);
                 Socket s = client.socket();  
                 clientsMap.remove("["+s.getInetAddress().toString().substring(1)+":"+Integer.toHexString(client.hashCode())+"]");
                 client.socket().close();
                 selectionKey.cancel();
                 return false;
            }
        }
		return true;
    }  

    private void dispatch(SocketChannel client,String info) throws IOException{  
        Socket s = client.socket();  
        String name = "["+s.getInetAddress().toString().substring(1)+":"+Integer.toHexString(client.hashCode())+"]";  
        
        if(!clientsMap.isEmpty()){  
            for(Map.Entry<String, SocketChannel> entry : clientsMap.entrySet()){  
                SocketChannel temp = entry.getValue();  
                if(!client.equals(temp)){  
                    sBuffer.clear();  
                    sBuffer.put((name+":"+info).getBytes());  
                    sBuffer.flip();  
                    temp.write(sBuffer);  
                }  
            }  
        }  
        
        clientsMap.put(name, client);  
    }  
   
} 