package com.funnycampus.socket;
import java.net.ServerSocket;
import java.net.Socket;


public class MyServer {
	public static void main(String[] ars) throws Exception {
		ServerSocket server = new ServerSocket(8099); //服务端
		Socket client = null; //连接的客户端
		
		while(true) {
			System.out.println("服务器已开启，等待连接……");
			client = server.accept();
			new Thread(new MyThread(client)).start();
			System.out.println("\n");
		}
	}
}
