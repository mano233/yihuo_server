package com.ahnu.server.serivce.impl;

import com.ahnu.server.dao.ChatMsgMapper;
import com.ahnu.server.dao.ChatMsgMapperCustom;
import com.ahnu.server.dao.ChatSessionMapperCustom;
import com.ahnu.server.model.ChatMsg;
import com.ahnu.server.model.SessionListWrap;
import com.ahnu.server.serivce.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {
    ChatSessionMapperCustom sessionMapperCustom;

    @Autowired
    public ChatServiceImpl(ChatSessionMapperCustom sessionMapperCustom) {
        this.sessionMapperCustom = sessionMapperCustom;
    }

    @Override
    public List<SessionListWrap> getSessionList(int uid) throws Exception {
        return sessionMapperCustom.getSessionList(uid);
    }
}
