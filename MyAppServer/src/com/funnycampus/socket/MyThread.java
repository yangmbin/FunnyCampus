package com.funnycampus.socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/*
����ÿһ���ͻ������ӣ�����һ���߳��������߳��н��ж����������
*/
public class MyThread implements Runnable {
	private Socket client = null;
	public MyThread(Socket client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					client.getOutputStream()); //��ͻ���д����
			ObjectInputStream in = new ObjectInputStream(
					client.getInputStream()); //���ͻ�������
			Object obj = in.readObject();
			
			/*�ж����������ͣ����в�ͬ�Ĵ���*/
			
			/**
			 * ע����Ϣ
			 */
			if(obj instanceof RegisterMSG) {
				RegisterMSG registerMSG_OBJ = (RegisterMSG)obj;
				
				System.out.println("���������յĵ���ע�������ǣ�" + registerMSG_OBJ.getName() + " "
						+ registerMSG_OBJ.getPassword1() + " "
						+ registerMSG_OBJ.getPassword2());
				
				//�ͻ���ע����û���������
				String name, pass1, pass2;
				name = registerMSG_OBJ.getName();
				pass1 = registerMSG_OBJ.getPassword1();
				pass2 = registerMSG_OBJ.getPassword2();
				
				//���ص�����������Ϣ
				String backMSG;
				
				//�жϿͻ���������û���������ĺϷ��ԣ���û�漰���ݿ⣩
				if("".equals(name) || "".equals(pass1) || "".equals(pass2)) {
					backMSG = "EMPTY_ERROR";
				}
				else if(-1 != name.indexOf(' ') || -1 != pass1.indexOf(' ') || -1 != pass2.indexOf(' ')) {
					backMSG = "SPACE_ERROR";
				}
				else if(!pass1.equals(pass2)) {
					backMSG = "DIFFERENT_ERROR";
				}
				//���ݿ����
				else {
					//���ӱ������ݿ�
					String driverClass = "com.mysql.jdbc.Driver";
					String url = "jdbc:mysql://localhost:3306/info";
					String user = "root";
					String password = "root";
					
					Connection conn;
					
					//��Mysql��JDBC��������ص��ڴ���
					Class.forName(driverClass).newInstance();
					//ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���
					conn=DriverManager.getConnection(url, user, password);
					//ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���
					Statement stmt = conn.createStatement();
					//���������ע��һ�㣺���������MYSQL����﷨��׼�������ʧ��
					
					//�鿴���ݿ����Ƿ�������
					String _sql = "select * from user where username = '" + name + "'";
					ResultSet rs = stmt.executeQuery(_sql);
					
					//�˺��ظ�
					if(rs.next()) {
						backMSG = "REPEAT_ERROR";
					}
					else {
						String sql = "insert into user(username, password, nickname, headimg) values('" + name + "', '" + pass1 + "'" + ", \"NULL\" , \"NULL\")";
						System.out.println(sql);
						stmt.executeUpdate(sql);
						backMSG = "SUCCESS";
					}
					//�ͷ���Դ���ͷ�ǰ�����ж�statement��connection���������Ƿ�Ϊ�գ�
					//�����Ϊ����˵����Դû���ͷš�����Ҫ�ر�ռ�����ݿ�Ķ���statement��connection
					if(stmt != null)
						stmt.close();
					if(conn != null)
						conn.close();
				}
				
				//��ͻ���д����
				out.writeObject(backMSG);
				out.flush();
				
				//�ر����������
				in.close();
				out.close();
			}
			/**
			 * ��¼��Ϣ 
			 */
			else if(obj instanceof LoginMSG) {
				LoginMSG loginMSG_OBJ = (LoginMSG)obj;
				
				System.out.println("���������յ��ĵ�¼�����ǣ�" + loginMSG_OBJ.getName() + " "
						+ loginMSG_OBJ.getPassword());
				
				//��¼���û���������
				String name = loginMSG_OBJ.getName();
				String pass = loginMSG_OBJ.getPassword();
				
				//���ص�����������Ϣ
				String backMSG;
				
				//�ж��û����������Ƿ�Ϊ��
				if("".equals(name)|| "".equals(pass)) {
					backMSG = "EMPTY_ERROR";
				}
				else {
					//���ӱ������ݿ�
					String driverClass = "com.mysql.jdbc.Driver";
					String url = "jdbc:mysql://localhost:3306/info";
					String user = "root";
					String password = "root";
					
					Connection conn;
					
					//��Mysql��JDBC��������ص��ڴ���
					Class.forName(driverClass).newInstance();
					//ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���
					conn=DriverManager.getConnection(url, user, password);
					//ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���
					Statement stmt = conn.createStatement();
					//���������ע��һ�㣺���������MYSQL����﷨��׼�������ʧ��
					
					//�鿴���ݿ����û����������Ƿ�ƥ��
					String _sql = "select * from user where username = '" + name + "' and password = '" + pass + "'";
					ResultSet rs = stmt.executeQuery(_sql);
					
					//��¼�ɹ�
					if(rs.next()) {
						String nickname = rs.getString("nickname");
						String headimg = rs.getString("headimg");
						backMSG = "SUCCESS" + "$" + nickname + "$" + headimg;
					}
					//��¼ʧ��
					else {
						backMSG = "WRONG_ERROR";
					}
					//�ͷ���Դ���ͷ�ǰ�����ж�statement��connection���������Ƿ�Ϊ�գ�
					//�����Ϊ����˵����Դû���ͷš�����Ҫ�ر�ռ�����ݿ�Ķ���statement��connection
					if(stmt != null)
						stmt.close();
					if(conn != null)
						conn.close();
				}
				
				//��ͻ���д����
				out.writeObject(backMSG);
				out.flush();
				
				//�ر����������
				in.close();
				out.close();
			}
			/**
			 * ����ˢ����Ϣ
			 */
			else if((obj instanceof String) && ((String)obj).equals("HEAD_REFRESH")) {
				System.out.println("���������յ���ˢ����Ϣ�ǣ�" + (String)obj);
				
				/*
				SchoolInfoMSG temp1 = new SchoolInfoMSG();
				temp1.setName("111");
				temp1.setContent("aaa");
				SchoolInfoMSG temp2 = new SchoolInfoMSG();
				temp2.setName("222");
				temp2.setContent("bbb");
				
				List<SchoolInfoMSG> list = new ArrayList<SchoolInfoMSG>();
				list.add(temp1);
				list.add(temp2);
				
				SchoolInfoMSGList schoolInfoMSGList = new SchoolInfoMSGList();
				schoolInfoMSGList.setList(list);
				*/
				
				SchoolInfoMSGList backMSG = new SchoolInfoMSGList(); //���ؿͻ��˵�����
				List<SchoolInfoMSG> list = new ArrayList<SchoolInfoMSG>();
				
				//���ӱ������ݿ�
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";
				
				Connection conn;
				
				//��Mysql��JDBC��������ص��ڴ���
				Class.forName(driverClass).newInstance();
				//ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���
				conn=DriverManager.getConnection(url, user, password);
				//ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���
				Statement stmt = conn.createStatement();
				//���������ע��һ�㣺���������MYSQL����﷨��׼�������ʧ��
				ResultSet rs = stmt.executeQuery("SELECT * FROM information"); //ִ��SQL��䲢���ؽ��.
				
				while(rs.next()) {
					SchoolInfoMSG temp = new SchoolInfoMSG();
					temp.setName(rs.getString("username"));
					temp.setContent(rs.getString("content"));
					
					temp.setNickname(rs.getString("nickname"));
					temp.setHeadIMG(rs.getString("headimg"));
					temp.setTime(rs.getString("date"));
					
					list.add(temp);
				}
				
				backMSG.setList(list);

				//��ͻ���д����
				out.writeObject(backMSG);
				out.flush();
				
				//�ر����������
				in.close();
				out.close();
				System.out.println("������Ϣ������");
			}
			/**
			 * ����������
			 */
			else if(obj instanceof SchoolInfoMSG) {
				SchoolInfoMSG msg = (SchoolInfoMSG)obj;
				
				System.out.println("���������յ��ķ��������¶������ǣ�" + msg.getName() + " "
						+ msg.getContent());
				
				//��ȡ���յ�����Ϣ
				String name = msg.getName();
				String content = msg.getContent();
				
				String nickname = msg.getNickname();
				String headimg = msg.getHeadIMG();
				String time = msg.getTime();
				
				//���ص�����������Ϣ
				String backMSG;
				
				//���ӱ������ݿ�
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";
				
				Connection conn;
				
				//��Mysql��JDBC��������ص��ڴ���
				Class.forName(driverClass).newInstance();
				//ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���
				conn=DriverManager.getConnection(url, user, password);
				//ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���
				Statement stmt = conn.createStatement();
				//���������ע��һ�㣺���������MYSQL����﷨��׼�������ʧ��
				
				ResultSet rs = stmt.executeQuery("SELECT* FROM information"); //ִ��SQL��䲢���ؽ��.
				rs.last();//��ȡ��¼����
				int totalItem=rs.getRow();
				totalItem += 1;
				totalItem *= -1;
				
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				//Date date=new Date();  
				//String str = sdf.format(date); 
				
				String sql = "INSERT INTO information(id, title, content, username, date, headimg, nickname) VALUES('" + 
						totalItem + "', " + "null" + ", '" + content + "', '" + name + "', '" + time + "'" + ", '" + headimg + "', '" + nickname + "')";	
				stmt.executeUpdate(sql);
				
				//�ͷ���Դ���ͷ�ǰ�����ж�statement��connection���������Ƿ�Ϊ�գ�
				//�����Ϊ����˵����Դû���ͷš�����Ҫ�ر�ռ�����ݿ�Ķ���statement��connection
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
				
				//��ͻ���д����
				backMSG = "SUCCESS";
				out.writeObject(backMSG);
				out.flush();
				
				//�ر����������
				in.close();
				out.close();
			}
			/**
			 * ����ͷ��
			 */
			else if(obj instanceof SetUserHeadImgMSG) {
				SetUserHeadImgMSG msg = (SetUserHeadImgMSG)obj;
				System.out.println("���������յ���Ϣ������ͷ��");
				
				String name = msg.getName();
				String headimg = msg.getHeadimg();
				
				//���ص�����������Ϣ
				String backMSG;
				
				//���ӱ������ݿ�
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";
				
				Connection conn;
				
				//��Mysql��JDBC��������ص��ڴ���
				Class.forName(driverClass).newInstance();
				//ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���
				conn=DriverManager.getConnection(url, user, password);
				//ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���
				Statement stmt = conn.createStatement();
				//���������ע��һ�㣺���������MYSQL����﷨��׼�������ʧ��
				
				String sql = "update user set headimg='" + headimg + "'" + "where username='" + name + "'";
				stmt.executeUpdate(sql);
				
				sql = "update information set headimg='" + headimg + "'" + "where username='" + name +"'";
				stmt.executeUpdate(sql);
				
				//�ͷ���Դ���ͷ�ǰ�����ж�statement��connection���������Ƿ�Ϊ�գ�
				//�����Ϊ����˵����Դû���ͷš�����Ҫ�ر�ռ�����ݿ�Ķ���statement��connection
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
				
				//��ͻ���д����
				backMSG = "SUCCESS";
				out.writeObject(backMSG);
				out.flush();
				
				//�ر����������
				in.close();
				out.close();
			}
			/**
			 * �����ǳ�
			 */
			else if(obj instanceof SetUserNicknameMSG) {
				SetUserNicknameMSG msg = (SetUserNicknameMSG)obj;
				System.out.println("���������յ���Ϣ�������ǳƣ�");
				
				String name = msg.getName();
				String nickname = msg.getNickname();
				
				//���ص�����������Ϣ
				String backMSG;
				
				//���ӱ������ݿ�
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";
				
				Connection conn;
				
				//��Mysql��JDBC��������ص��ڴ���
				Class.forName(driverClass).newInstance();
				//ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���
				conn=DriverManager.getConnection(url, user, password);
				//ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���
				Statement stmt = conn.createStatement();
				//���������ע��һ�㣺���������MYSQL����﷨��׼�������ʧ��
				
				String sql = "update user set nickname='" + nickname + "'" + "where username='" + name + "'";
				stmt.executeUpdate(sql);
				
				sql = "update information set nickname='" + nickname + "'" + "where username='" + name +"'";
				stmt.executeUpdate(sql);
				
				//�ͷ���Դ���ͷ�ǰ�����ж�statement��connection���������Ƿ�Ϊ�գ�
				//�����Ϊ����˵����Դû���ͷš�����Ҫ�ر�ռ�����ݿ�Ķ���statement��connection
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
				
				//��ͻ���д����
				backMSG = "SUCCESS";
				out.writeObject(backMSG);
				out.flush();
				
				//�ر����������
				in.close();
				out.close();
			}
			else {
				System.out.println("δ֪����");
			}
			
		} catch(Exception e) {
			//e.printStackTrace();
		} finally {
			
		}
	}
}
