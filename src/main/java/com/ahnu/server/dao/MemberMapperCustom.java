package com.ahnu.server.dao;

import com.ahnu.server.model.Member;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface MemberMapperCustom {
    Member selectByName(String name);
    List<Member> selectAll(int type, Integer status);
    void changeStatus(int uid,int status);
    List<Member> searchUserByName(String name);
    List<Member> selectByStatus(int status);
}
