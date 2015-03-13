> Android应用——趣味校园
-----------------------

### 一、项目简介
    这是一个贴近校园生活的移动社交应用，以分享和获取信息资讯为主要目的，并提供了聊天功能。
    暂时实现了以下功能：
    ①(主页-模块)：支持用户发布文本、图片信息在主页上，其他用户可通过主页查看，可以赞、踩、
                 评论以及点击用户头像私聊、添加好友等。
    ②(资讯-模块)：支持在手机上查看新闻资讯，暂且实现了“我的中大”近期新闻，其他待实现。
    ③(发现-模块)：支持获取好友列表、进入聊天纪录列表和查找周边的人；并且提供了额外两个功能：
                 天气查询和图书查询。
    ④(我  -模块)：暂时实现了更改昵称和用户头像功能。
    
### 二、如何运行
    1、服务端
    直接运行MyAppServer工程即可，需要导入MySQL驱动：mysql-connector-java-5.1.8-bin.jar，
    IP和端口设为本机地址和端口号。然后安装MySQL服务器，导入info.sql数据库文件并运行。除此
    之外，需要安装openfire聊天服务器并开启，提供聊天服务。
    设置IP和PORT的3个文件为：
    /FunnyCampus/src/com/funnycampus/XMPPUtils/XMPPUtils.java——本机IP和5222端口
    /FunnyCampus/src/com/funnycampus/socket/IP_PORT.java——本机IP和任意可用端口
    /MyAppServer/src/com/funnycampus/socket/MyServer.java——本机任意可用端口
    如果电脑不是公有IP地址，又需要任何网络（如移动网络）访问到服务器，则可以使用nat123软件
    进行IP和端口的映射，这样就可以把私有IP影射成共有的IP。
    
    2、客户端
    直接运行工程FunnyCampus工程，需要导入refresh_library下拉刷新库和Jsoup第三方Html解析
    库：jsoup-1.6.1.jar，以及xmpp android客户端sdk：asmack.jar，Socket通信需用与服务器的IP
    和端口相对应或端口映射后相对应的IP和端口，openfire服务器的访问端口为5222。
    
