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
对于每一个客户端连接，创建一个线程来处理，线程中将判断请求的类型
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
					client.getOutputStream()); //向客户端写数据
			ObjectInputStream in = new ObjectInputStream(
					client.getInputStream()); //读客户端数据
			Object obj = in.readObject();
			
			/*判断类对象的类型，进行不同的处理*/
			
			/**
			 * 注册消息
			 */
			if(obj instanceof RegisterMSG) {
				RegisterMSG registerMSG_OBJ = (RegisterMSG)obj;
				
				System.out.println("服务器接收的到的注册数据是：" + registerMSG_OBJ.getName() + " "
						+ registerMSG_OBJ.getPassword1() + " "
						+ registerMSG_OBJ.getPassword2());
				
				//客户端注册的用户名和密码
				String name, pass1, pass2;
				name = registerMSG_OBJ.getName();
				pass1 = registerMSG_OBJ.getPassword1();
				pass2 = registerMSG_OBJ.getPassword2();
				
				//返回到服务器的消息
				String backMSG;
				
				//判断客户端输入的用户名和密码的合法性（还没涉及数据库）
				if("".equals(name) || "".equals(pass1) || "".equals(pass2)) {
					backMSG = "EMPTY_ERROR";
				}
				else if(-1 != name.indexOf(' ') || -1 != pass1.indexOf(' ') || -1 != pass2.indexOf(' ')) {
					backMSG = "SPACE_ERROR";
				}
				else if(!pass1.equals(pass2)) {
					backMSG = "DIFFERENT_ERROR";
				}
				//数据库操作
				else {
					//连接本地数据库
					String driverClass = "com.mysql.jdbc.Driver";
					String url = "jdbc:mysql://localhost:3306/info";
					String user = "root";
					String password = "root";
					
					Connection conn;
					
					//把Mysql的JDBC驱动类加载到内存中
					Class.forName(driverClass).newInstance();
					//通过驱动管理器取得一个与Mysql数据库连接的对象
					conn=DriverManager.getConnection(url, user, password);
					//使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句
					Statement stmt = conn.createStatement();
					//插入语句需注意一点：语句必须符合MYSQL语句语法标准，否则会失败
					
					//查看数据库中是否有重名
					String _sql = "select * from user where username = '" + name + "'";
					ResultSet rs = stmt.executeQuery(_sql);
					
					//账号重复
					if(rs.next()) {
						backMSG = "REPEAT_ERROR";
					}
					else {
						String sql = "insert into user(username, password, nickname, headimg) values('" + name + "', '" + pass1 + "'" + ", \"NULL\" , \"NULL\")";
						System.out.println(sql);
						stmt.executeUpdate(sql);
						backMSG = "SUCCESS";
					}
					//释放资源，释放前需先判断statement和connection两个对象是否为空，
					//如果不为空则说明资源没有释放。就需要关闭占有数据库的对象statement和connection
					if(stmt != null)
						stmt.close();
					if(conn != null)
						conn.close();					
				}
				
				//向客户端写数据
				out.writeObject(backMSG);
				out.flush();
				
				//关闭输入输出流
				in.close();
				out.close();
				
				//关闭socket
				client.close();
			}
			/**
			 * 登录消息 
			 */
			else if(obj instanceof LoginMSG) {
				LoginMSG loginMSG_OBJ = (LoginMSG)obj;
				
				System.out.println("服务器接收到的登录数据是：" + loginMSG_OBJ.getName() + " "
						+ loginMSG_OBJ.getPassword());
				
				//登录的用户名和密码
				String name = loginMSG_OBJ.getName();
				String pass = loginMSG_OBJ.getPassword();
				
				//返回到服务器的消息
				String backMSG;
				
				//判断用户名和密码是否为空
				if("".equals(name)|| "".equals(pass)) {
					backMSG = "EMPTY_ERROR";
				}
				else {
					//连接本地数据库
					String driverClass = "com.mysql.jdbc.Driver";
					String url = "jdbc:mysql://localhost:3306/info";
					String user = "root";
					String password = "root";
					
					Connection conn;
					
					//把Mysql的JDBC驱动类加载到内存中
					Class.forName(driverClass).newInstance();
					//通过驱动管理器取得一个与Mysql数据库连接的对象
					conn=DriverManager.getConnection(url, user, password);
					//使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句
					Statement stmt = conn.createStatement();
					//插入语句需注意一点：语句必须符合MYSQL语句语法标准，否则会失败
					
					//查看数据库中用户名和密码是否匹配
					String _sql = "select * from user where username = '" + name + "' and password = '" + pass + "'";
					ResultSet rs = stmt.executeQuery(_sql);
					
					//登录成功
					if(rs.next()) {
						String nickname = rs.getString("nickname");
						String headimg = rs.getString("headimg");
						backMSG = "SUCCESS" + "$" + nickname + "$" + headimg;
					}
					//登录失败
					else {
						backMSG = "WRONG_ERROR";
					}
					//释放资源，释放前需先判断statement和connection两个对象是否为空，
					//如果不为空则说明资源没有释放。就需要关闭占有数据库的对象statement和connection
					if(stmt != null)
						stmt.close();
					if(conn != null)
						conn.close();
				}
				
				//向客户端写数据
				out.writeObject(backMSG);
				out.flush();
				
				//关闭输入输出流
				in.close();
				out.close();
				
				//关闭socket
				client.close();
			}
			/**
			 * 下拉刷新消息
			 */
			else if((obj instanceof String) && (((String)obj).substring(0, 12)).equals("HEAD_REFRESH")) {
				System.out.println("服务器接收到的刷新消息是：" + (String)obj);
				
				//需要刷新的消息的种类
				int type = Integer.parseInt(((String)obj).substring(12));
				
				SchoolInfoMSGList backMSG = new SchoolInfoMSGList(); //返回客户端的数据
				List<SchoolInfoMSG> list = new ArrayList<SchoolInfoMSG>();
				
				//连接本地数据库
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";
				
				Connection conn;
				
				//把Mysql的JDBC驱动类加载到内存中
				Class.forName(driverClass).newInstance();
				//通过驱动管理器取得一个与Mysql数据库连接的对象
				conn=DriverManager.getConnection(url, user, password);
				//使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句
				Statement stmt = conn.createStatement();
				//插入语句需注意一点：语句必须符合MYSQL语句语法标准，否则会失败
				ResultSet rs = stmt.executeQuery("SELECT * FROM information where type =" +  "'" + type + "'"); //执行SQL语句并返回结果.
				
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
					
					//评论
					Statement stmt1 = conn.createStatement();
					ResultSet rs1 = stmt1.executeQuery("select * from comment where id = " + "'" 
							+ rs.getInt("id") + "'");
					rs1.last();
					temp.setCommentNum(rs1.getRow());
					//赞
					Statement stmt2 = conn.createStatement();
					ResultSet rs2 = stmt2.executeQuery("select * from LikeAndDislike where id = " + "'"
							+ rs.getInt("id") + "'" + " and liked = 1");
					rs2.last();
					temp.setLikeNum(rs2.getRow());
					//踩
					Statement stmt3 = conn.createStatement();
					ResultSet rs3 = stmt3.executeQuery("select * from LikeAndDislike where id = " + "'"
							+ rs.getInt("id") + "'" + " and dislike = 1");
					rs3.last();
					temp.setDislikeNum(rs3.getRow());
					
					list.add(temp);
					
					/*关闭释放资源*/
					if(stmt1 != null)
						stmt1.close();
					if(stmt2 != null)
						stmt2.close();
					if(stmt3 != null)
						stmt3.close();
				}
				
				backMSG.setList(list);

				//向客户端写数据
				out.writeObject(backMSG);
				out.flush();
				
				//关闭输入输出流
				in.close();
				out.close();
				System.out.println("发送信息结束！");
				
				//关闭socket
				client.close();
				
				/*关闭释放资源*/
				if(stmt != null)
					stmt.close();
				if(conn != null) 
					conn.close();
			}
			/**
			 * 发送新鲜事
			 */
			else if(obj instanceof SchoolInfoMSG) {
				SchoolInfoMSG msg = (SchoolInfoMSG)obj;
				
				System.out.println("服务器接收到的分享新鲜事儿数据是：" + msg.getName() + " "
						+ msg.getContent());
				
				//获取接收到的信息
				String name = msg.getName();
				String content = msg.getContent();
				
				String nickname = msg.getNickname();
				String headimg = msg.getHeadIMG();
				String time = msg.getTime();
				
				List<String> photos = msg.getPhotos();
				int type = msg.getType();
				
				//返回到服务器的消息
				String backMSG;
				
				//连接本地数据库
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";
				
				Connection conn;
				
				//把Mysql的JDBC驱动类加载到内存中
				Class.forName(driverClass).newInstance();
				//通过驱动管理器取得一个与Mysql数据库连接的对象
				conn=DriverManager.getConnection(url, user, password);
				//使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句
				Statement stmt = conn.createStatement();
				//插入语句需注意一点：语句必须符合MYSQL语句语法标准，否则会失败
				
				ResultSet rs = stmt.executeQuery("SELECT* FROM information"); //执行SQL语句并返回结果.
				rs.last();//获取记录总数
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
				
				//释放资源，释放前需先判断statement和connection两个对象是否为空，
				//如果不为空则说明资源没有释放。就需要关闭占有数据库的对象statement和connection
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
				
				//向客户端写数据
				backMSG = "SUCCESS";
				out.writeObject(backMSG);
				out.flush();
				
				//关闭输入输出流
				in.close();
				out.close();
				
				//关闭socket
				client.close();
			}
			/**
			 * 更改头像
			 */
			else if(obj instanceof SetUserHeadImgMSG) {
				SetUserHeadImgMSG msg = (SetUserHeadImgMSG)obj;
				System.out.println("服务器接收到消息：更改头像！");
				
				String name = msg.getName();
				String headimg = msg.getHeadimg();
				
				//返回到服务器的消息
				String backMSG;
				
				//连接本地数据库
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";
				
				Connection conn;
				
				//把Mysql的JDBC驱动类加载到内存中
				Class.forName(driverClass).newInstance();
				//通过驱动管理器取得一个与Mysql数据库连接的对象
				conn=DriverManager.getConnection(url, user, password);
				//使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句
				Statement stmt = conn.createStatement();
				//插入语句需注意一点：语句必须符合MYSQL语句语法标准，否则会失败
				
				String sql = "update user set headimg='" + headimg + "'" + "where username='" + name + "'";
				stmt.executeUpdate(sql);
				
				sql = "update information set headimg='" + headimg + "'" + "where username='" + name +"'";
				stmt.executeUpdate(sql);
				
				//释放资源，释放前需先判断statement和connection两个对象是否为空，
				//如果不为空则说明资源没有释放。就需要关闭占有数据库的对象statement和connection
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
				
				//向客户端写数据
				backMSG = "SUCCESS";
				out.writeObject(backMSG);
				out.flush();
				
				//关闭输入输出流
				in.close();
				out.close();
				
				//关闭socket
				client.close();
			}
			/**
			 * 更改昵称
			 */
			else if(obj instanceof SetUserNicknameMSG) {
				SetUserNicknameMSG msg = (SetUserNicknameMSG)obj;
				System.out.println("服务器接收到消息：更改昵称！");
				
				String name = msg.getName();
				String nickname = msg.getNickname();
				
				//返回到服务器的消息
				String backMSG;
				
				//连接本地数据库
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";
				
				Connection conn;
				
				//把Mysql的JDBC驱动类加载到内存中
				Class.forName(driverClass).newInstance();
				//通过驱动管理器取得一个与Mysql数据库连接的对象
				conn=DriverManager.getConnection(url, user, password);
				//使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句
				Statement stmt = conn.createStatement();
				//插入语句需注意一点：语句必须符合MYSQL语句语法标准，否则会失败
				
				String sql = "update user set nickname='" + nickname + "'" + "where username='" + name + "'";
				stmt.executeUpdate(sql);
				
				sql = "update information set nickname='" + nickname + "'" + "where username='" + name +"'";
				stmt.executeUpdate(sql);
				
				//释放资源，释放前需先判断statement和connection两个对象是否为空，
				//如果不为空则说明资源没有释放。就需要关闭占有数据库的对象statement和connection
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
				
				//向客户端写数据
				backMSG = "SUCCESS";
				out.writeObject(backMSG);
				out.flush();
				
				//关闭输入输出流
				in.close();
				out.close();
				
				//关闭socket
				client.close();
			}
			/**
			 * 发送评论
			 */
			else if(obj instanceof CommentMSG) {
				System.out.println("服务器接收到消息：发送评论！");
				CommentMSG msg = (CommentMSG) obj;
				System.out.println(msg.getID() + "," + msg.getName() + "," + msg.getComment() + "," + msg.getTime());
				
				/*连接本地数据库*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*把Mysql的JDBC驱动类加载到内存中*/
				Class.forName(driverClass).newInstance();
				/*通过驱动管理器取得一个与Mysql数据库连接的对象*/
				Connection conn=DriverManager.getConnection(url, user, password);
				/*使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句*/
				Statement stmt = conn.createStatement();
				
				ResultSet rs = stmt.executeQuery("SELECT* FROM comment"); /*执行SQL语句并返回结果.*/
				rs.last();/*获取记录总数*/
				int totalItem=rs.getRow();
				totalItem += 1;
				totalItem *= -1;
				
				String sql = "insert into comment(id, iid, content, time, username) values(" +
				"'" + msg.getID() + "', " + "'" + totalItem + "', " + "'" + msg.getComment() + "', " 
						+ "'" + msg.getTime() + "', " + "'" + msg.getName() + "')";
				stmt.execute(sql);
				
				/*关闭释放资源*/
				if(stmt != null)
					stmt.close();
				if(conn != null) 
					conn.close();
				
				
				/*向客户端写数据*/
				String backMSG = "SUCCESS";
				out.writeObject(backMSG);
				out.flush();
				
				/*关闭流和套接字*/
				in.close();
				out.close();
				client.close();
			}
			/**
			 * 获取评论
			 */
			else if(obj instanceof String && ((String) obj).substring(0, 11).equals("GETCOMMENTS")) {
				int id = Integer.parseInt(((String) obj).substring(11));
				System.out.println("服务器接收到消息：GETCOMMENTS" + id);		
				
				/*连接本地数据库*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*把Mysql的JDBC驱动类加载到内存中*/
				Class.forName(driverClass).newInstance();
				/*通过驱动管理器取得一个与Mysql数据库连接的对象*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句*/
				Statement stmt = conn.createStatement();
				Statement stmt1 = conn.createStatement();
				
				String sql = "select * from comment where id = " + "'" + id + "'";
				ResultSet rs = stmt.executeQuery(sql);
				
				/*返回客户端的数据*/
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
				
				/*向客户端写数据*/
				out.writeObject(backList);
				out.flush();
				System.out.println("发送信息结束！");
				
				/*关闭流和套接字*/
				in.close();
				out.close();
				client.close();
				
				/*关闭释放资源*/
				if(stmt != null)
					stmt.close();
				if(stmt1 != null)
					stmt1.close();
				if(conn != null) 
					conn.close();
			}
			/**
			 * 点赞和踩的消息
			 */
			else if (obj instanceof LikeAndDislikeMSG) {
				System.out.println("服务器接收到消息：" + ((LikeAndDislikeMSG)obj).getLikeORdislike() + ":" + 
						((LikeAndDislikeMSG)obj).getUsername());
				int id = ((LikeAndDislikeMSG)obj).getFreshNewsID();
				String username = ((LikeAndDislikeMSG)obj).getUsername();
				String str = ((LikeAndDislikeMSG)obj).getLikeORdislike();
				
				/*连接本地数据库*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*把Mysql的JDBC驱动类加载到内存中*/
				Class.forName(driverClass).newInstance();
				/*通过驱动管理器取得一个与Mysql数据库连接的对象*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句*/
				Statement stmt = conn.createStatement();
				
				ResultSet rs = stmt.executeQuery("select * from LikeAndDislike where id = " + "'" +
						id + "'" + " and username = " + "'" + username + "'");
				rs.last();
				int count = rs.getRow();
				
				//返回给客户端的消息
				String backMSG = null;
				
				//没有记录则插入
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
					//释放资源
					if (stmt1 != null)
						stmt1.close();
					if (statementtemp != null)
						statementtemp.close();
					backMSG = "SUCCESS";
				}
				else {
					backMSG = "FAILED";
				}
				
				/*向客户端写数据*/
				out.writeObject(backMSG);
				out.flush();
				System.out.println("发送信息结束！");
				
				/*关闭流和套接字*/
				in.close();
				out.close();
				client.close();
				
				//释放资源
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			}
			/**
			 * 获取好友列表
			 */
			else if (obj instanceof String && ((String) obj).substring(0, 14).equals("GETFRIENDSLIST")) {
				System.out.println("服务器接收到消息：" + (String) obj);
				
				String username = ((String) obj).substring(15); //用户名
				List<FriendsInfo> friendlist = new ArrayList<FriendsInfo>(); //返回到客户端的数据集
				
				/*连接本地数据库*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*把Mysql的JDBC驱动类加载到内存中*/
				Class.forName(driverClass).newInstance();
				/*通过驱动管理器取得一个与Mysql数据库连接的对象*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句*/
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
				/*向客户端写数据*/
				out.writeObject(friendlist);
				out.flush();
				System.out.println("发送好友列表结束！");
				
				/*释放数据库资源*/
				if (stmt != null)
					stmt.close();
				if (stmt1 != null)
					stmt1.close();
				if (conn != null)
					conn.close();
				
				/*关闭流和套接字*/
				in.close();
				out.close();
				client.close();
				
			}
			/**
			 * 获取周围的人
			 */
			else if (obj instanceof String && ((String) obj).substring(0, 15).equals("GETPEOPLEAROUND")) {
				System.out.println("服务器接收到消息：" + (String) obj);
				
				String username = ((String) obj).substring(16); //用户名
				List<PeopleAroundInfo> peoplearoundlist = new ArrayList<PeopleAroundInfo>(); //返回到客户端的数据集
				
				/*连接本地数据库*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*把Mysql的JDBC驱动类加载到内存中*/
				Class.forName(driverClass).newInstance();
				/*通过驱动管理器取得一个与Mysql数据库连接的对象*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句*/
				Statement stmt1 = conn.createStatement();
				Statement stmt2 = conn.createStatement();
				Statement stmt3 = conn.createStatement();
				
				ResultSet rs1 = stmt1.executeQuery("select * from location where username = '" + 
						username + "'");
				rs1.last();
				int count = rs1.getRow();
				
				//如果数据库中没有该用户的位置信息，就返回错误信息
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
						//找到5000米以内的人
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
				
				//排序操作
				
				
				/*向客户端写数据*/
				out.writeObject(peoplearoundlist);
				out.flush();
				System.out.println("发送周围的人结束！");
				
				/*释放数据库资源*/
				if (stmt1 != null)
					stmt1.close();
				if (stmt2 != null)
					stmt2.close();
				if (stmt3 != null)
					stmt3.close();
				if (conn != null)
					conn.close();
				
				/*关闭流和套接字*/
				in.close();
				out.close();
				client.close();
			}
			/**
			 * 添加好友
			 */
			else if (obj instanceof String && ((String) obj).substring(0, 10).equals("ADDFRIENDS")) {
				String received = (String) obj;
				System.out.println("服务器接收到消息：" + (String) obj);
				
				//好友关系
				String username = received.substring(10, received.indexOf("|"));
				String friend = received.substring(received.indexOf("|") + 1);
				
				/*连接本地数据库*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*把Mysql的JDBC驱动类加载到内存中*/
				Class.forName(driverClass).newInstance();
				/*通过驱动管理器取得一个与Mysql数据库连接的对象*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句*/
				Statement stmt = conn.createStatement();
				Statement stmt1 = conn.createStatement();
				Statement stmt2 = conn.createStatement();
				
				ResultSet rs = stmt.executeQuery("select * from friendslist where username = '" + 
						username + "' and friend = '" + friend + "'");
				rs.last();
				int count = rs.getRow();
				//没有则插入
				if (count == 0) {
					ResultSet rs1 = stmt1.executeQuery("select * from friendslist");
					rs1.last();
					int total = rs1.getRow();
					int index = -1 * (total + 1);
					
					stmt2.execute("insert into friendslist(id, username, friend) values('" + 
							index + "', '" + username + "', '" + friend + "')");
				}
				/*向客户端写数据*/
				out.writeObject("SUCCESS");
				out.flush();
				System.out.println("好友添加完成！");
				
				/*释放数据库资源*/
				if (stmt != null)
					stmt.close();
				if (stmt1 != null)
					stmt1.close();
				if (stmt2 != null)
					stmt2.close();
				if (conn != null)
					conn.close();
				
				/*关闭流和套接字*/
				in.close();
				out.close();
				client.close();
				
			}
			/**
			 * 根据用户名获取用户信息
			 */
			else if (obj instanceof String && ((String) obj).substring(0, 11).equals("GETUSERINFO")) {
				System.out.println("服务器接收到消息：" + (String) obj);
				
				String username = ((String) obj).substring(12);
				/*连接本地数据库*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*把Mysql的JDBC驱动类加载到内存中*/
				Class.forName(driverClass).newInstance();
				/*通过驱动管理器取得一个与Mysql数据库连接的对象*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句*/
				Statement stmt = conn.createStatement();
				
				ResultSet rs = stmt.executeQuery("select * from user where username = '" + 
						username + "'");
				rs.first();
				
				FriendsInfo info = new FriendsInfo();
				info.setHeadimg(rs.getString("headimg"));
				info.setNickname(rs.getString("nickname"));
				info.setUsername(username);
				
				/*向客户端写数据*/
				out.writeObject(info);
				out.flush();
				System.out.println("获取用户信息完成！");
				
				/*释放数据库资源*/
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
				
				/*关闭流和套接字*/
				in.close();
				out.close();
				client.close();
			}
			/**
			 * 更新用户位置信息
			 */
			else if (obj instanceof LocationInfoMSG) {
				LocationInfoMSG msg = (LocationInfoMSG) obj;
				System.out.println("服务器收到消息：更新用户位置信息");
				System.out.println(msg.getUsername() + ":" + msg.getLatitude() + "," + msg.getLongitude());
				
				/*连接本地数据库*/
				String driverClass = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://localhost:3306/info";
				String user = "root";
				String password = "root";				
				
				/*把Mysql的JDBC驱动类加载到内存中*/
				Class.forName(driverClass).newInstance();
				/*通过驱动管理器取得一个与Mysql数据库连接的对象*/
				Connection conn = DriverManager.getConnection(url, user, password);
				/*使用数据库连接对象conn来创建一个statement对象，可以执行SQL语句*/
				Statement stmt1 = conn.createStatement();
				Statement stmt2 = conn.createStatement();
				
				ResultSet rs1 = stmt1.executeQuery("select * from location where username = '" + 
						msg.getUsername() + "'");
				rs1.last();
				int count = rs1.getRow();
				
				//插入记录
				if (count == 0) {
					stmt2.execute("insert into location(username, latitude, longitude) values('"
							+ msg.getUsername() + "', '" + msg.getLatitude() + "', '" + msg.getLongitude() + "')");
				}
				//更新记录
				else {
					stmt2.execute("update location set latitude = '" + msg.getLatitude() + "', longitude = '" +
							msg.getLongitude() + "' where username = '" + msg.getUsername() + "'");
				}
				
				/*向客户端写数据*/
				out.writeObject("SUCCESS");
				out.flush();
				System.out.println("用户位置信息更新完成！");
				
				/*释放数据库资源*/
				if (stmt1 != null)
					stmt1.close();
				if (stmt2 != null)
					stmt2.close();
				if (conn != null)
					conn.close();
				
				/*关闭流和套接字*/
				in.close();
				out.close();
				client.close();
			}
			/**
			 * 未知请求
			 */
			else {
				System.out.println("未知请求！");
				client.close();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			try {
				client.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("客户端连接出错了。。。");
		} finally {
			//关闭socket
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("客户端关闭出错了。。。");
			}
		}
	}
	
	/*
	 * 根据经纬度计算距离
	 */
	public static double getDistance(double lat1, double long1, double lat2, double long2) {
		  double PI = 3.14159265358979323; // 圆周率
		  double R = 6371229; // 地球的半径
		  double x, y, distance;
		  x = (long2 - long1) * PI * R
		    * Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
		  y = (lat2 - lat1) * PI * R / 180;
		  distance = Math.hypot(x, y);
		  
		  //保留一位小数，并转为km
		  DecimalFormat df = new DecimalFormat("#.0");
		  return Double.valueOf(df.format(distance / 1000.0));
	}
}

