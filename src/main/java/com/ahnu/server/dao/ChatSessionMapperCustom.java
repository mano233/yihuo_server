package com.ahnu.server.dao;

import com.ahnu.server.model.ChatMsg;
import com.ahnu.server.model.SessionListWrap;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatSessionMapperCustom {
    List<SessionListWrap> getSessionList(int uid) throws Exception;
    Integer getSessionIdByUid(int uid,int gid);
}
