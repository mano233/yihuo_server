package com.ahnu.server.dao;

import com.ahnu.server.model.ChatSession;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatSessionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ChatSession record);

    int insertSelective(ChatSession record);

    ChatSession selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ChatSession record);

    int updateByPrimaryKey(ChatSession record);
}