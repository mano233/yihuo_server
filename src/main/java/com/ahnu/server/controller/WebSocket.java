package com.ahnu.server.controller;

import com.ahnu.server.dao.*;
import com.ahnu.server.helper.JwtTokenUtils;
import com.ahnu.server.model.ChatMsg;
import com.ahnu.server.model.ChatSession;
import com.ahnu.server.model.Goods;
import com.ahnu.server.serivce.impl.ChatServiceImpl;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.ContextLoaderListener;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.xml.crypto.Data;
import java.beans.Transient;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@ServerEndpoint("/chat/{token}")//标记此类为服务端
public class WebSocket {
    static final ConcurrentHashMap<Integer, Session> ONLINE_SESSIONS = new ConcurrentHashMap<>();
    static final ObjectMapper MAPPER = new ObjectMapper();
    static ChatServiceImpl chatService;
    static GoodsMapper goodsMapper;
    static ChatSessionMapper chatSessionMapper;
    static ChatSessionMapperCustom chatSessionMapperCustom;
    static ChatMsgMapper chatMsgMapper;

    @Autowired
    public void setChatSessionMapperCustom(ChatSessionMapperCustom chatSessionMapperCustom) {
        WebSocket.chatSessionMapperCustom = chatSessionMapperCustom;
    }

    @Autowired
    public void setChatMsgMapper(ChatMsgMapper chatMsgMapper) {
        WebSocket.chatMsgMapper = chatMsgMapper;
    }


    @Autowired
    public void setChatSessionMapper(ChatSessionMapper chatSessionMapper) {
        WebSocket.chatSessionMapper = chatSessionMapper;
    }

    @Autowired
    public void setChatService(ChatServiceImpl chatService) {
        WebSocket.chatService = chatService;
    }

    @Autowired
    public void setGoodsMapper(GoodsMapper goodsMapper) {
        WebSocket.goodsMapper = goodsMapper;
    }


    @OnOpen
    public void onOpen(@PathParam("token") String token, Session session) throws IOException {
        ChatMsg msg = new ChatMsg();
        try {
            Map<String, Claim> claimMap = JwtTokenUtils.verifyToken(token);
            int uid = claimMap.get("uid").asInt();
            var userSession = ONLINE_SESSIONS.get(uid);

            if (userSession == null) {
                ONLINE_SESSIONS.put(uid, session);
            } else {
                ONLINE_SESSIONS.replace(uid, session);
                userSession.close();
                session.close();
            }
        } catch (Exception e) {
            session.close();
        }
    }

    static class JsonMessage {

    }

    @OnMessage
    public void onMessage(@PathParam("token") String token, Session session, String jsonStr) {
        try {
            Map<String, Claim> claimMap = JwtTokenUtils.verifyToken(token);
            int uid = claimMap.get("uid").asInt();
            ChatMsg msg = MAPPER.readValue(jsonStr, ChatMsg.class);
            msg.setCtime(new Date());
            msg.setUid(uid);
            // 如果sid为null，说明这是个新的会话
            if (msg.getSid() == null) {
                ChatSession newSession = new ChatSession();
                newSession.setFromUid(uid);
                int gid = msg.getGid();
                newSession.setGid(gid);
                Goods goods = goodsMapper.selectByPrimaryKey(gid);
                newSession.setToUid(goods.getUid());
                // 新建会话
                chatSessionMapper.insert(newSession);
                msg.setSid(newSession.getId());
            }
            // 如果不为空说明是在已有的会话上继续聊天
            ChatSession chatSession = chatSessionMapper.selectByPrimaryKey(msg.getSid());
            Integer toUid = null;
            if(chatSession.getToUid()==uid){
                toUid = chatSession.getFromUid();
            }else{
                toUid = chatSession.getToUid();
            }
            var toUserSession = ONLINE_SESSIONS.get(toUid);
            chatMsgMapper.insert(msg);
            // 不为空说明接受者在线
            if (toUserSession != null) {
                // 如果发送者和接收者是同一个，则不发送给自己
                if(toUserSession!=session){
                    toUserSession.getBasicRemote().sendText(MAPPER.writeValueAsString(msg));
                }
            }
            session.getBasicRemote().sendText(MAPPER.writeValueAsString(msg));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClose
    public void onClose(@PathParam("token") String token, Session session) throws Exception {
        Map<String, Claim> claimMap = JwtTokenUtils.verifyToken(token);
        int uid = claimMap.get("uid").asInt();
        ONLINE_SESSIONS.remove(uid);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
