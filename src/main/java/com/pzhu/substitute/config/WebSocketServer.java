package com.pzhu.substitute.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pzhu.substitute.common.status.RoleStatus;
import com.pzhu.substitute.entity.Message;
import com.pzhu.substitute.mapper.MessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author dengyiqing
 * @description websocket 服务端
 * @date 2022/3/21
 */
@ServerEndpoint("/imserver/{userId}")
@Component
@Slf4j
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String userId = "";

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
            //加入set中
        } else {
            webSocketMap.put(userId, this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        log.info("用户连接:" + userId + ",当前在线人数为:" + getOnlineCount());

        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            log.error("用户:" + userId + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        log.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + userId + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis
        Gson gson = new Gson();
        if (StringUtils.isNotBlank(message)) {
            Message msgInfo;
            try {
                //解析发送的报文
                JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
                //追加发送人(防止串改)
                jsonObject.addProperty("from", userId);
                jsonObject.addProperty("createTime", new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
                String orderId = jsonObject.get("orderId").getAsString();
                String msg = jsonObject.get("msg").getAsString();
                String type = jsonObject.get("type").getAsString();
                if ("driverTakeOrder".equals(type)) {
                    String userId = jsonObject.get("userId").getAsString();
                    jsonObject.addProperty("driverId", userId.substring(1));
                    webSocketMap.get("U"+userId).sendMessage(gson.toJson(jsonObject));
                } else if ("Umsg".equals(type)) {
                    String driverId = "D" + jsonObject.get("driverId").getAsString();
                    jsonObject.addProperty("roleStatus", RoleStatus.USER.getValue());
                    msgInfo = new Message(null, Long.parseLong(orderId), RoleStatus.USER, msg, null);
                    if (StringUtils.isNotBlank(driverId) && webSocketMap.containsKey(driverId)) {
                        webSocketMap.get(driverId).sendMessage(gson.toJson(jsonObject));
                    }
                    MessageMapper bean = applicationContext.getBean(MessageMapper.class);
                    bean.insert(msgInfo);
                } else if ("Dmsg".equals(type)) {
                    String userId = "U" + jsonObject.get("userId").getAsString();
                    jsonObject.addProperty("roleStatus", RoleStatus.DRIVER.getValue());
                    msgInfo = new Message(null, Long.parseLong(orderId), RoleStatus.DRIVER, msg, null);
                    if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
                        webSocketMap.get(userId).sendMessage(gson.toJson(jsonObject));
                    }
                    MessageMapper bean = applicationContext.getBean(MessageMapper.class);
                    bean.insert(msgInfo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.userId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
//        this.session.getBasicRemote().sendText(message);
        this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMap(Map message) throws EncodeException, IOException {
        this.session.getBasicRemote().sendObject(message);
    }


    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        log.info("发送消息到:" + userId + "，报文:" + message);
        if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户" + userId + ",不在线！");
        }
    }

    public static void sendAllMessage(String message) {
        for (Map.Entry<String, WebSocketServer> entry : webSocketMap.entrySet()) {
            try {
                entry.getValue().sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("[消息推送]发送给 {} 的消息出现错误: {}", entry.getKey(), e);
            }
        }
    }

    public static void sendAllObject(Map map) {
        for (Map.Entry<String, WebSocketServer> entry : webSocketMap.entrySet()) {
            try {
                entry.getValue().sendMap(map);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("[消息推送]发送给 {} 的消息出现错误: {}", entry.getKey(), e);
            } catch (EncodeException e) {
                e.printStackTrace();
                log.error("[消息推送]发送给 {} 的消息出现编码异常: {}", entry.getKey(), e);
            }

        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}

