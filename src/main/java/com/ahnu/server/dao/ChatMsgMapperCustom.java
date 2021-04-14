package com.ahnu.server.dao;

import com.ahnu.server.model.ChatMsg;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMsgMapperCustom {
    List<ChatMsg> getMsgs(int sid);
}
