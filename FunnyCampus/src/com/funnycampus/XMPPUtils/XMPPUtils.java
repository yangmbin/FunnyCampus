package com.funnycampus.XMPPUtils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.OfflineMessageManager;

import com.funnycampus.socket.FriendsInfo;
import com.funnycampus.socket.IP_PORT;
import com.funnycampus.ui.MainPage;
import com.funnycampus.utils.Utils;
import com.yangmbin.funnycampus.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

public class XMPPUtils {
	//连接服务器的IP和端口，地址映射时修改这里
	//private String IP = "1.207.142.24";
	//private int PORT = 5222;
	private String IP = "xmppnat.nat123.net";
	private int PORT = 45217;
	
	//XMPP连接
	private XMPPConnection connection = null;
	
	//工具类的单例
	private static XMPPUtils xmppUtils = new XMPPUtils();
	
	//当前用户和其他用户的聊天窗口集合
	private Map<String, Chat> chatList = new HashMap<String, Chat>();
	//针对每一个会话，都有一个数据列表，存放历史消息
	public static Map<String, Object> username2listItems = new HashMap<String, Object>();
	public static Map<String, Bitmap> username2bitmap = new HashMap<String, Bitmap>();
	public static Map<String, String> username2nickname = new HashMap<String, String>();
	
	//聊天监听器
	private MyChatManagerListener myChatManagerListener = new MyChatManagerListener();
	//重连监听器
	private ReConnectionListener reConnectionListener = new ReConnectionListener();
	
	/*
	 * 工具类单例
	 */
	synchronized public static XMPPUtils getInstance() {
		return xmppUtils;
	}
	
	/*
	 * 获取和服务器的连接
	 */
	public XMPPConnection getConnection() {
		if (connection == null) {
			openConnection();
		}
		return connection;
	}
	
	/*
	 * 打开和服务器的连接
	 */
	public boolean openConnection() {
		try {
			if (connection == null) {
				//配置连接
				ConnectionConfiguration config = new ConnectionConfiguration(IP, PORT);
				connection = new XMPPConnection(config);
				connection.connect();
				
				return true;
			}
		} catch (XMPPException e) {
			e.printStackTrace();
			connection = null;
		}
		return false;
	}
	
	/*
	 * 关闭和服务器的连接
	 */
	public void closeConnection() {
		if (connection != null) { 
			//移除聊天的监听
			getConnection().getChatManager().removeChatListener(myChatManagerListener);
			//移除重连的监听
			getConnection().removeConnectionListener(reConnectionListener);
			if (connection.isConnected())
				connection.disconnect();
			connection = null;
		}
	}
	
	/*
	 * 注册
	 */
	public boolean register(String username, String password) {
		if (getConnection() == null)
			return false;
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(getConnection().getServiceName());
		//用户名和密码
		reg.setUsername(username);
		reg.setPassword(password);	
		reg.addAttribute("android", "geolo_createUser_android");
		
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = getConnection().createPacketCollector(filter);
		getConnection().sendPacket(reg);
		
		IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		//停止请求result
		collector.cancel();
		
		if (result == null)
			return false;
		if (result.getType() == IQ.Type.RESULT)
			return true;
		else
			return false;
	}
	
	/*
	 * 登录
	 */
	public boolean login(String username, String password) {
		try {
			if (getConnection() == null) 
				return false;
			getConnection().login(username, password);
			//设置聊天的监听
			getConnection().getChatManager().addChatListener(myChatManagerListener);
			//设置重连监听
			getConnection().addConnectionListener(reConnectionListener);
			//获取离线消息
			getOfflineMessages();
			
			return true;
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * 根据用户id获取当前系统用户和该用户的聊天窗口
	 */
	public Chat getFriendChat(String username, MessageListener listener) {
		if (getConnection() == null) 
			return null;
		//判断是否存在
		//for (String key : chatList.keySet()) {
			//if (key.equals(username))
				//return chatList.get(key);
		//}
		//创建聊天窗口
		Chat chat = getConnection().getChatManager().createChat(
				username + "@" + getConnection().getServiceName(), listener);
		//chatList.put(username, chat);]]
		
		return chat;
	}
	
	/*
	 * 根据用户id当前系统用户把聊天信息发送给该用户
	 */
	public boolean sendChatMessage(String username, MessageListener listener, String msg) {
		Chat chat = getFriendChat(username, listener);
		try {
			chat.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/*
	 * 获取离线消息
	 */
	public void getOfflineMessages() {
		if (getConnection() == null)
			return;
		try {
			OfflineMessageManager offlineMessageManager = new OfflineMessageManager(getConnection());
			Iterator<Message> it = offlineMessageManager.getMessages();
			
			int count = offlineMessageManager.getMessageCount();
			if (count <= 0)
				return;
			
			while(it.hasNext()) {
				Message msg = it.next();
				//用户名和内容
				String username = msg.getFrom().substring(0, msg.getFrom().indexOf("@"));
				String content = msg.getBody();
				
				android.os.Message message = new android.os.Message();
				message.what = 0;
				Map<String, String> map = new HashMap<String, String>();
				map.put("username", username);
				map.put("content", content);
				message.obj = map;
				MainPage.handler.sendMessage(message);
			}
			offlineMessageManager.deleteMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/*
 * 单人聊天的监听类
 */
class MyChatManagerListener implements ChatManagerListener {
	@Override
	public void chatCreated(Chat chat, boolean arg1) {
		chat.addMessageListener(new MessageListener() {	
			@Override
			public void processMessage(Chat arg0, Message msg) {
				//发送消息的用户
				final String username = msg.getFrom().substring(0, msg.getFrom().indexOf("@"));
				//消息内容
				final String content = msg.getBody();
				
				//查看是否存在聊天记录，没有则新建数据集合，用于存储
    			int flag = 0;
    			for (String str : XMPPUtils.username2listItems.keySet()) {
    				if (username.equals(str)) {
    					flag = 1;
    					break;
    				}
    			}
    			//没有通信过
    			if (flag == 0) {
    				LinkedList<Map<String, Object>> temp = new LinkedList<Map<String,Object>>();
    				XMPPUtils.username2listItems.put(username, temp);
    				
    				//如果没有通信过，就需要获取用户信息
    				new Thread(new Runnable() {
						@Override
						public void run() {
							FriendsInfo info = null;
							try {
								//创建Socket，连接到服务器
								Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
								ObjectInputStream in = new ObjectInputStream(client.getInputStream()); 
								ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
								
								//向服务器写数据
								out.writeObject("GETUSERINFO|" + username);
								out.flush();
								
								//从服务器读返回消息
								info = (FriendsInfo) in.readObject();
								
								//关闭输入输出流
								in.close();
								out.close();
								client.close();
							} catch (Exception e) {
								info = null;
							}
							if (info == null) {
								MainPage.handler.sendEmptyMessage(1);
							}
							else {
								XMPPUtils.username2bitmap.put(username, Utils.getInstance().stringToBitmap(info.getHeadimg()));
								XMPPUtils.username2nickname.put(username, info.getNickname());
								//构建聊天信息，放入气泡聊天数据集合中
			        			Map<String, Object> map = new HashMap<String, Object>();
			        			map.put("headimg", XMPPUtils.username2bitmap.get(username));
			        			map.put("content", content);
			        			map.put("type", "0");
			        			((LinkedList<Map<String, Object>>) XMPPUtils.username2listItems.get(username)).add(map);
			        			
			        			MainPage.handler.sendEmptyMessage(0);
							}
						}
    					
    				}).start();
    			}
    			//通信过
    			else {
    				//构建聊天信息，放入气泡聊天数据集合中
        			Map<String, Object> map = new HashMap<String, Object>();
        			map.put("headimg", XMPPUtils.username2bitmap.get(username));
        			map.put("content", content);
        			map.put("type", "0");
        			((LinkedList<Map<String, Object>>) XMPPUtils.username2listItems.get(username)).add(map);
        			
        			MainPage.handler.sendEmptyMessage(0);
    			}
			}
		});
	}
}

