package com.ahnu.server.controller;

import com.ahnu.server.dao.ChatMsgMapper;
import com.ahnu.server.dao.ChatMsgMapperCustom;
import com.ahnu.server.dao.ChatSessionMapper;
import com.ahnu.server.dao.GoodsMapper;
import com.ahnu.server.model.ChatSession;
import com.ahnu.server.model.ResJsonBody;
import com.ahnu.server.model.SessionListWrap;
import com.ahnu.server.serivce.impl.ChatServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/chat")
public class ChatController {

    ChatServiceImpl chatService;
    ChatMsgMapperCustom chatMsgMapperCustom;
    ChatSessionMapper chatSessionMapper;
    GoodsMapper goodsMapper;

    @Autowired
    public ChatController(ChatServiceImpl chatService, ChatMsgMapperCustom chatMsgMapperCustom, ChatSessionMapper chatSessionMapper, GoodsMapper goodsMapper) {
        this.chatService = chatService;
        this.chatMsgMapperCustom = chatMsgMapperCustom;
        this.chatSessionMapper = chatSessionMapper;
        this.goodsMapper = goodsMapper;
    }

    @GetMapping("/list")
    public ResJsonBody getUnReadMsgList() throws Exception{
        ResJsonBody jsonBody = new ResJsonBody();
        int uid = (int) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        List<SessionListWrap> wrap = chatService.getSessionList(uid);
        jsonBody.setContent(wrap);
        return jsonBody;
    }

    @PostMapping("/{gid}")
    public ResJsonBody createSession(@PathVariable("gid")int gid){
        ResJsonBody jsonBody = new ResJsonBody();
        int uid = (int) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        ChatSession session = new ChatSession();
        session.setGid(gid);
        session.setToUid(goodsMapper.selectByPrimaryKey(gid).getUid());
        session.setFromUid(uid);
        chatSessionMapper.insert(session);
        jsonBody.setContent(session);
        return jsonBody;
    }
    @GetMapping("/{sid}")
    public ResJsonBody getMsgs(@PathVariable("sid")int sid) throws Exception{
        ResJsonBody jsonBody = new ResJsonBody();
        jsonBody.setContent(chatMsgMapperCustom.getMsgs(sid));
        return jsonBody;
    }

}
