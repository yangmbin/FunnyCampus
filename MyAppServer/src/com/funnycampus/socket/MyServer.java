package com.funnycampus.socket;
import java.net.ServerSocket;
import java.net.Socket;


public class MyServer {
	public static void main(String[] ars) throws Exception {
		ServerSocket server = new ServerSocket(8099); //�����
		Socket client = null; //���ӵĿͻ���
		
		while(true) {
			System.out.println("�������ѿ������ȴ����ӡ���");
			client = server.accept();
			new Thread(new MyThread(client)).start();
			System.out.println("\n");
		}
	}
}
