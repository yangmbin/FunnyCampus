package com.funnycampus.socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.events.Comment;


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
				
				//�ر�socket
				client.close();
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
				
				//�ر�socket
				client.close();
			}
			/**
			 * ����ˢ����Ϣ
			 */
			else if((obj instanceof String) && (((String)obj).substring(0, 12)).equals("HEAD_REFRESH")) {
				System.out.println("���������յ���ˢ����Ϣ�ǣ�" + (String)obj);
				
				//��Ҫˢ�µ���Ϣ������
				int type = Integer.parseInt(((String)obj).substring(12));
				
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
				ResultSet rs = stmt.executeQuery("SELECT * FROM information where type =" +  "'" + type + "'"); //ִ��SQL��䲢���ؽ��.
				
				while(rs.next()) {
					SchoolInfoMSG temp = new SchoolInfoMSG();
					temp.setName(rs.getString("username"));
					temp.setContent(rs.getString("content"));
					
					temp.setNickname(rs.getString("nickname"));
					temp.setHeadIMG(rs.getString("headimg"));
					temp.setTime(rs.getString("date"));
					
					List<String> photos = new ArrayList();
					photos.add(rs.getString("sharedimg1"));
					photos.add(rs.getString("sharedimg2"));
					photos.add(rs.getString("sharedimg3"));
					temp.setPhotos(photos);
					
					temp.setID(rs.getInt("id"));
					
					//����
					Statement stmt1 = conn.createStatement();
					ResultSet rs1 = stmt1.executeQuery("select * from comment where id = " + "'" 
							+ rs.getInt("id") + "'");
					rs1.last();
					temp.setCommentNum(rs1.getRow());
					//��
					Statement stmt2 = conn.createStatement();
					ResultSet rs2 = stmt2.executeQuery("select * from LikeAndDislike where id = " + "'"
							+ rs.getInt("id") + "'" + " and liked = 1");
					rs2.last();
					temp.setLikeNum(rs2.getRow());
					//��
					Statement stmt3 = conn.createStatement();
					ResultSet rs3 = stmt3.executeQuery("select * from LikeAndDislike where id = " + "'"
							+ rs.getInt("id") + "'" + " and dislike = 1");
					rs3.last();
					temp.setDislikeNum(rs3.getRow());
					
					list.add(temp);
					
					/*�ر��ͷ���Դ*/
					if(stmt1 != null)
						stmt1.close();
					if(stmt2 != null)
						stmt2.close();
					if(stmt3 != null)
						stmt3.close();
				}
				
				backMSG.setList(list);

				//��ͻ���д����
				out.writeObject(backMSG);
				out.flush();
				
				//�ر����������
				in.close();
				out.close();
				System.out.println("������Ϣ������");
				
				//�ر�socket
				client.close();
				
				/*�ر��ͷ���Դ*/
				if(stmt != null)
					stmt.close();
				if(conn != null) 
					conn.close();
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
				
				List<String> photos = msg.getPhotos();
				int type = msg.getType();
				
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
				
				String sql = "INSERT INTO information(id, title, content, username, date, headimg, nickname, type, sharedimg1, sharedimg2, sharedimg3) VALUES('" + 
						totalItem + "', " + "null" + ", '" + content + "', '" + name + "', '" + time + "'" + ", '" + headimg + "', '" + nickname + "', " +
						"'" + type + "', " + "'" + photos.get(0) + "'," + "'" + photos.get(1) + "', " + "'" + photos.get(2) + "')"; 
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
				
				//�ر�socket
				client.close();
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
				
				//�ر�socket
				client.close();
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
				
				//�ر�socket
				client.close();
			}
			/**
			 * ��������
			 */
			else if(obj instanceof CommentMSG) {
				System.out.println("���������յ���Ϣ���������ۣ�");
				CommentMSG msg = (CommentMSG) obj;
				System.out.println(msg.getID() + "," + msg.getName() + "," + msg.getComment() + "," + msg.getTime());
				
				/*���ӱ������ݿ�*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*��Mysql��JDBC��������ص��ڴ���*/
				Class.forName(driverClass).newInstance();
				/*ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���*/
				Connection conn=DriverManager.getConnection(url, user, password);
				/*ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���*/
				Statement stmt = conn.createStatement();
				
				ResultSet rs = stmt.executeQuery("SELECT* FROM comment"); /*ִ��SQL��䲢���ؽ��.*/
				rs.last();/*��ȡ��¼����*/
				int totalItem=rs.getRow();
				totalItem += 1;
				totalItem *= -1;
				
				String sql = "insert into comment(id, iid, content, time, username) values(" +
				"'" + msg.getID() + "', " + "'" + totalItem + "', " + "'" + msg.getComment() + "', " 
						+ "'" + msg.getTime() + "', " + "'" + msg.getName() + "')";
				stmt.execute(sql);
				
				/*�ر��ͷ���Դ*/
				if(stmt != null)
					stmt.close();
				if(conn != null) 
					conn.close();
				
				
				/*��ͻ���д����*/
				String backMSG = "SUCCESS";
				out.writeObject(backMSG);
				out.flush();
				
				/*�ر������׽���*/
				in.close();
				out.close();
				client.close();
			}
			/**
			 * ��ȡ����
			 */
			else if(obj instanceof String && ((String) obj).substring(0, 11).equals("GETCOMMENTS")) {
				int id = Integer.parseInt(((String) obj).substring(11));
				System.out.println("���������յ���Ϣ��GETCOMMENTS" + id);		
				
				/*���ӱ������ݿ�*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*��Mysql��JDBC��������ص��ڴ���*/
				Class.forName(driverClass).newInstance();
				/*ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���*/
				Statement stmt = conn.createStatement();
				Statement stmt1 = conn.createStatement();
				
				String sql = "select * from comment where id = " + "'" + id + "'";
				ResultSet rs = stmt.executeQuery(sql);
				
				/*���ؿͻ��˵�����*/
				BackCommentMSGList backList = new BackCommentMSGList();
				List<BackCommentMSG> list = new ArrayList<BackCommentMSG>();
				while(rs.next()) {
					sql = "select * from user where username = " + "'" + rs.getString("username") + "'";
					ResultSet rs1 = stmt1.executeQuery(sql);
					rs1.first();
					
					BackCommentMSG backCommentMSG = new BackCommentMSG();			
					backCommentMSG.setComment(rs.getString("content"));
					backCommentMSG.setTime(rs.getString("time"));
					backCommentMSG.setHeadIMG(rs1.getString("headimg"));
					backCommentMSG.setNickname(rs1.getString("nickname"));
					
					list.add(backCommentMSG);
				}
				backList.setList(list);
				
				/*��ͻ���д����*/
				out.writeObject(backList);
				out.flush();
				System.out.println("������Ϣ������");
				
				/*�ر������׽���*/
				in.close();
				out.close();
				client.close();
				
				/*�ر��ͷ���Դ*/
				if(stmt != null)
					stmt.close();
				if(stmt1 != null)
					stmt1.close();
				if(conn != null) 
					conn.close();
			}
			/**
			 * ���޺Ͳȵ���Ϣ
			 */
			else if (obj instanceof LikeAndDislikeMSG) {
				System.out.println("���������յ���Ϣ��" + ((LikeAndDislikeMSG)obj).getLikeORdislike() + ":" + 
						((LikeAndDislikeMSG)obj).getUsername());
				int id = ((LikeAndDislikeMSG)obj).getFreshNewsID();
				String username = ((LikeAndDislikeMSG)obj).getUsername();
				String str = ((LikeAndDislikeMSG)obj).getLikeORdislike();
				
				/*���ӱ������ݿ�*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*��Mysql��JDBC��������ص��ڴ���*/
				Class.forName(driverClass).newInstance();
				/*ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���*/
				Statement stmt = conn.createStatement();
				
				ResultSet rs = stmt.executeQuery("select * from LikeAndDislike where id = " + "'" +
						id + "'" + " and username = " + "'" + username + "'");
				rs.last();
				int count = rs.getRow();
				
				//���ظ��ͻ��˵���Ϣ
				String backMSG = null;
				
				//û�м�¼�����
				if (count == 0) {
					Statement statementtemp = conn.createStatement();
					ResultSet rstemp = statementtemp.executeQuery("select * from LikeAndDislike");
					rstemp.last();
					int iid = -1 * (rstemp.getRow() + 1);
					
					Statement stmt1 = conn.createStatement();
					if (str.equals("like")) {
						stmt1.execute("insert into LikeAndDislike(id, iid, username, liked, dislike) values(" +
								"'" + id + "', " + "'" + iid + "', "  + "'" + username + "', " + "1, 0)");				
					}
					else {
						stmt1.execute("insert into LikeAndDislike(id, iid, username, liked, dislike) values(" +
								"'" + id + "', " + "'" + iid + "', " + "'" + username + "', " + "0, 1)");				
					}
					//�ͷ���Դ
					if (stmt1 != null)
						stmt1.close();
					if (statementtemp != null)
						statementtemp.close();
					backMSG = "SUCCESS";
				}
				else {
					backMSG = "FAILED";
				}
				
				/*��ͻ���д����*/
				out.writeObject(backMSG);
				out.flush();
				System.out.println("������Ϣ������");
				
				/*�ر������׽���*/
				in.close();
				out.close();
				client.close();
				
				//�ͷ���Դ
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			}
			/**
			 * ��ȡ�����б�
			 */
			else if (obj instanceof String && ((String) obj).substring(0, 14).equals("GETFRIENDSLIST")) {
				System.out.println("���������յ���Ϣ��" + (String) obj);
				
				String username = ((String) obj).substring(15); //�û���
				List<FriendsInfo> friendlist = new ArrayList<FriendsInfo>(); //���ص��ͻ��˵����ݼ�
				
				/*���ӱ������ݿ�*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*��Mysql��JDBC��������ص��ڴ���*/
				Class.forName(driverClass).newInstance();
				/*ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���*/
				Statement stmt = conn.createStatement();
				Statement stmt1 = conn.createStatement();
				
				ResultSet rs = stmt.executeQuery("select * from friendslist where username = " + 
						"'" + username + "'");
				while(rs.next()) {
					ResultSet rs1 = stmt1.executeQuery("select * from user where username = " + 
							"'" + rs.getString("friend") + "'");
					rs1.first();
					FriendsInfo info = new FriendsInfo();
					info.setHeadimg(rs1.getString("headimg"));
					info.setUsername(rs1.getString("username"));
					info.setNickname(rs1.getString("nickname"));
					
					friendlist.add(info);
				}
				/*��ͻ���д����*/
				out.writeObject(friendlist);
				out.flush();
				System.out.println("���ͺ����б������");
				
				/*�ͷ����ݿ���Դ*/
				if (stmt != null)
					stmt.close();
				if (stmt1 != null)
					stmt1.close();
				if (conn != null)
					conn.close();
				
				/*�ر������׽���*/
				in.close();
				out.close();
				client.close();
				
			}
			/**
			 * ��ȡ��Χ����
			 */
			else if (obj instanceof String && ((String) obj).substring(0, 15).equals("GETPEOPLEAROUND")) {
				System.out.println("���������յ���Ϣ��" + (String) obj);
				
				String username = ((String) obj).substring(16); //�û���
				List<PeopleAroundInfo> peoplearoundlist = new ArrayList<PeopleAroundInfo>(); //���ص��ͻ��˵����ݼ�
				
				/*���ӱ������ݿ�*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*��Mysql��JDBC��������ص��ڴ���*/
				Class.forName(driverClass).newInstance();
				/*ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���*/
				Statement stmt1 = conn.createStatement();
				Statement stmt2 = conn.createStatement();
				Statement stmt3 = conn.createStatement();
				
				ResultSet rs1 = stmt1.executeQuery("select * from location where username = '" + 
						username + "'");
				rs1.last();
				int count = rs1.getRow();
				
				//������ݿ���û�и��û���λ����Ϣ���ͷ��ش�����Ϣ
				if (count == 0) {
					peoplearoundlist = null;
				}
				else {
					rs1.first();
					ResultSet rs2 = stmt2.executeQuery("select * from location where username != '" + 
						username + "'");
					while (rs2.next()) {
						PeopleAroundInfo info = new PeopleAroundInfo();
						double distance = getDistance(rs1.getDouble("latitude"), rs1.getDouble("longitude"),
								rs2.getDouble("latitude"), rs2.getDouble("longitude"));
						//�ҵ�5000�����ڵ���
						if (distance < 5000.0) {
							info.setDistance("" + distance);
							info.setUsername(rs2.getString("username"));	
							
							ResultSet rs3 = stmt3.executeQuery("select * from user where username = '" + 
									rs2.getString("username") + "'");
							rs3.first();
							info.setHeadimg(rs3.getString("headimg"));
							info.setNickname(rs3.getString("nickname"));
							
							peoplearoundlist.add(info);
						}
					}
				}
				
				//�������
				
				
				/*��ͻ���д����*/
				out.writeObject(peoplearoundlist);
				out.flush();
				System.out.println("������Χ���˽�����");
				
				/*�ͷ����ݿ���Դ*/
				if (stmt1 != null)
					stmt1.close();
				if (stmt2 != null)
					stmt2.close();
				if (stmt3 != null)
					stmt3.close();
				if (conn != null)
					conn.close();
				
				/*�ر������׽���*/
				in.close();
				out.close();
				client.close();
			}
			/**
			 * ��Ӻ���
			 */
			else if (obj instanceof String && ((String) obj).substring(0, 10).equals("ADDFRIENDS")) {
				String received = (String) obj;
				System.out.println("���������յ���Ϣ��" + (String) obj);
				
				//���ѹ�ϵ
				String username = received.substring(10, received.indexOf("|"));
				String friend = received.substring(received.indexOf("|") + 1);
				
				/*���ӱ������ݿ�*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*��Mysql��JDBC��������ص��ڴ���*/
				Class.forName(driverClass).newInstance();
				/*ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���*/
				Statement stmt = conn.createStatement();
				Statement stmt1 = conn.createStatement();
				Statement stmt2 = conn.createStatement();
				
				ResultSet rs = stmt.executeQuery("select * from friendslist where username = '" + 
						username + "' and friend = '" + friend + "'");
				rs.last();
				int count = rs.getRow();
				//û�������
				if (count == 0) {
					ResultSet rs1 = stmt1.executeQuery("select * from friendslist");
					rs1.last();
					int total = rs1.getRow();
					int index = -1 * (total + 1);
					
					stmt2.execute("insert into friendslist(id, username, friend) values('" + 
							index + "', '" + username + "', '" + friend + "')");
				}
				/*��ͻ���д����*/
				out.writeObject("SUCCESS");
				out.flush();
				System.out.println("���������ɣ�");
				
				/*�ͷ����ݿ���Դ*/
				if (stmt != null)
					stmt.close();
				if (stmt1 != null)
					stmt1.close();
				if (stmt2 != null)
					stmt2.close();
				if (conn != null)
					conn.close();
				
				/*�ر������׽���*/
				in.close();
				out.close();
				client.close();
				
			}
			/**
			 * �����û�����ȡ�û���Ϣ
			 */
			else if (obj instanceof String && ((String) obj).substring(0, 11).equals("GETUSERINFO")) {
				System.out.println("���������յ���Ϣ��" + (String) obj);
				
				String username = ((String) obj).substring(12);
				/*���ӱ������ݿ�*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*��Mysql��JDBC��������ص��ڴ���*/
				Class.forName(driverClass).newInstance();
				/*ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���*/
				Statement stmt = conn.createStatement();
				
				ResultSet rs = stmt.executeQuery("select * from user where username = '" + 
						username + "'");
				rs.first();
				
				FriendsInfo info = new FriendsInfo();
				info.setHeadimg(rs.getString("headimg"));
				info.setNickname(rs.getString("nickname"));
				info.setUsername(username);
				
				/*��ͻ���д����*/
				out.writeObject(info);
				out.flush();
				System.out.println("��ȡ�û���Ϣ��ɣ�");
				
				/*�ͷ����ݿ���Դ*/
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
				
				/*�ر������׽���*/
				in.close();
				out.close();
				client.close();
			}
			/**
			 * �����û�λ����Ϣ
			 */
			else if (obj instanceof LocationInfoMSG) {
				LocationInfoMSG msg = (LocationInfoMSG) obj;
				System.out.println("�������յ���Ϣ�������û�λ����Ϣ");
				System.out.println(msg.getUsername() + ":" + msg.getLatitude() + "," + msg.getLongitude());
				
				/*���ӱ������ݿ�*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*��Mysql��JDBC��������ص��ڴ���*/
				Class.forName(driverClass).newInstance();
				/*ͨ������������ȡ��һ����Mysql���ݿ����ӵĶ���*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*ʹ�����ݿ����Ӷ���conn������һ��statement���󣬿���ִ��SQL���*/
				Statement stmt1 = conn.createStatement();
				Statement stmt2 = conn.createStatement();
				
				ResultSet rs1 = stmt1.executeQuery("select * from location where username = '" + 
						msg.getUsername() + "'");
				rs1.last();
				int count = rs1.getRow();
				
				//�����¼
				if (count == 0) {
					stmt2.execute("insert into location(username, latitude, longitude) values('"
							+ msg.getUsername() + "', '" + msg.getLatitude() + "', '" + msg.getLongitude() + "')");
				}
				//���¼�¼
				else {
					stmt2.execute("update location set latitude = '" + msg.getLatitude() + "', longitude = '" +
							msg.getLongitude() + "' where username = '" + msg.getUsername() + "'");
				}
				
				/*��ͻ���д����*/
				out.writeObject("SUCCESS");
				out.flush();
				System.out.println("�û�λ����Ϣ������ɣ�");
				
				/*�ͷ����ݿ���Դ*/
				if (stmt1 != null)
					stmt1.close();
				if (stmt2 != null)
					stmt2.close();
				if (conn != null)
					conn.close();
				
				/*�ر������׽���*/
				in.close();
				out.close();
				client.close();
			}
			/**
			 * δ֪����
			 */
			else {
				System.out.println("δ֪����");
				client.close();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			try {
				client.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("�ͻ������ӳ����ˡ�����");
		} finally {
			//�ر�socket
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("�ͻ��˹رճ����ˡ�����");
			}
		}
	}
	
	/*
	 * ���ݾ�γ�ȼ������
	 */
	public static double getDistance(double lat1, double long1, double lat2, double long2) {
		  double PI = 3.14159265358979323; // Բ����
		  double R = 6371229; // ����İ뾶
		  double x, y, distance;
		  x = (long2 - long1) * PI * R
		    * Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
		  y = (lat2 - lat1) * PI * R / 180;
		  distance = Math.hypot(x, y);
		  
		  //����һλС������תΪkm
		  DecimalFormat df = new DecimalFormat("#.0");
		  return Double.valueOf(df.format(distance / 1000.0));
	}
}

