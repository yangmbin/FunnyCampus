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
			}
			/**
			 * 下拉刷新消息
			 */
			else if((obj instanceof String) && ((String)obj).equals("HEAD_REFRESH")) {
				System.out.println("服务器接收到的刷新消息是：" + (String)obj);
				
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
				ResultSet rs = stmt.executeQuery("SELECT * FROM information"); //执行SQL语句并返回结果.
				
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

				//向客户端写数据
				out.writeObject(backMSG);
				out.flush();
				
				//关闭输入输出流
				in.close();
				out.close();
				System.out.println("发送信息结束！");
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
				
				String sql = "INSERT INTO information(id, title, content, username, date, headimg, nickname) VALUES('" + 
						totalItem + "', " + "null" + ", '" + content + "', '" + name + "', '" + time + "'" + ", '" + headimg + "', '" + nickname + "')";	
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
			}
			else {
				System.out.println("未知请求！");
			}
			
		} catch(Exception e) {
			//e.printStackTrace();
		} finally {
			
		}
	}
}
