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
	//���ӷ�������IP�Ͷ˿ڣ���ַӳ��ʱ�޸�����
	//private String IP = "1.207.142.24";
	//private int PORT = 5222;
	private String IP = "xmppnat.nat123.net";
	private int PORT = 45217;
	
	//XMPP����
	private XMPPConnection connection = null;
	
	//������ĵ���
	private static XMPPUtils xmppUtils = new XMPPUtils();
	
	//��ǰ�û��������û������촰�ڼ���
	private Map<String, Chat> chatList = new HashMap<String, Chat>();
	//���ÿһ���Ự������һ�������б������ʷ��Ϣ
	public static Map<String, Object> username2listItems = new HashMap<String, Object>();
	public static Map<String, Bitmap> username2bitmap = new HashMap<String, Bitmap>();
	public static Map<String, String> username2nickname = new HashMap<String, String>();
	
	//���������
	private MyChatManagerListener myChatManagerListener = new MyChatManagerListener();
	//����������
	private ReConnectionListener reConnectionListener = new ReConnectionListener();
	
	/*
	 * �����൥��
	 */
	synchronized public static XMPPUtils getInstance() {
		return xmppUtils;
	}
	
	/*
	 * ��ȡ�ͷ�����������
	 */
	public XMPPConnection getConnection() {
		if (connection == null) {
			openConnection();
		}
		return connection;
	}
	
	/*
	 * �򿪺ͷ�����������
	 */
	public boolean openConnection() {
		try {
			if (connection == null) {
				//��������
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
	 * �رպͷ�����������
	 */
	public void closeConnection() {
		if (connection != null) { 
			//�Ƴ�����ļ���
			getConnection().getChatManager().removeChatListener(myChatManagerListener);
			//�Ƴ������ļ���
			getConnection().removeConnectionListener(reConnectionListener);
			if (connection.isConnected())
				connection.disconnect();
			connection = null;
		}
	}
	
	/*
	 * ע��
	 */
	public boolean register(String username, String password) {
		if (getConnection() == null)
			return false;
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(getConnection().getServiceName());
		//�û���������
		reg.setUsername(username);
		reg.setPassword(password);	
		reg.addAttribute("android", "geolo_createUser_android");
		
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = getConnection().createPacketCollector(filter);
		getConnection().sendPacket(reg);
		
		IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		//ֹͣ����result
		collector.cancel();
		
		if (result == null)
			return false;
		if (result.getType() == IQ.Type.RESULT)
			return true;
		else
			return false;
	}
	
	/*
	 * ��¼
	 */
	public boolean login(String username, String password) {
		try {
			if (getConnection() == null) 
				return false;
			getConnection().login(username, password);
			//��������ļ���
			getConnection().getChatManager().addChatListener(myChatManagerListener);
			//������������
			getConnection().addConnectionListener(reConnectionListener);
			//��ȡ������Ϣ
			getOfflineMessages();
			
			return true;
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * �����û�id��ȡ��ǰϵͳ�û��͸��û������촰��
	 */
	public Chat getFriendChat(String username, MessageListener listener) {
		if (getConnection() == null) 
			return null;
		//�ж��Ƿ����
		//for (String key : chatList.keySet()) {
			//if (key.equals(username))
				//return chatList.get(key);
		//}
		//�������촰��
		Chat chat = getConnection().getChatManager().createChat(
				username + "@" + getConnection().getServiceName(), listener);
		//chatList.put(username, chat);]]
		
		return chat;
	}
	
	/*
	 * �����û�id��ǰϵͳ�û���������Ϣ���͸����û�
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
	 * ��ȡ������Ϣ
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
				//�û���������
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
 * ��������ļ�����
 */
class MyChatManagerListener implements ChatManagerListener {
	@Override
	public void chatCreated(Chat chat, boolean arg1) {
		chat.addMessageListener(new MessageListener() {	
			@Override
			public void processMessage(Chat arg0, Message msg) {
				//������Ϣ���û�
				final String username = msg.getFrom().substring(0, msg.getFrom().indexOf("@"));
				//��Ϣ����
				final String content = msg.getBody();
				
				//�鿴�Ƿ���������¼��û�����½����ݼ��ϣ����ڴ洢
    			int flag = 0;
    			for (String str : XMPPUtils.username2listItems.keySet()) {
    				if (username.equals(str)) {
    					flag = 1;
    					break;
    				}
    			}
    			//û��ͨ�Ź�
    			if (flag == 0) {
    				LinkedList<Map<String, Object>> temp = new LinkedList<Map<String,Object>>();
    				XMPPUtils.username2listItems.put(username, temp);
    				
    				//���û��ͨ�Ź�������Ҫ��ȡ�û���Ϣ
    				new Thread(new Runnable() {
						@Override
						public void run() {
							FriendsInfo info = null;
							try {
								//����Socket�����ӵ�������
								Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
								ObjectInputStream in = new ObjectInputStream(client.getInputStream()); 
								ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
								
								//�������д����
								out.writeObject("GETUSERINFO|" + username);
								out.flush();
								
								//�ӷ�������������Ϣ
								info = (FriendsInfo) in.readObject();
								
								//�ر����������
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
								//����������Ϣ�����������������ݼ�����
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
    			//ͨ�Ź�
    			else {
    				//����������Ϣ�����������������ݼ�����
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

