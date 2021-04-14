package com.ahnu.server.serivce;

import com.ahnu.server.model.ChatMsg;
import com.ahnu.server.model.SessionListWrap;

import java.util.List;
import java.util.Map;

public interface ChatService {
    List<SessionListWrap> getSessionList(int uid) throws Exception;
}
