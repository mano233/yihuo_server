package com.ahnu.server.serivce;

import com.ahnu.server.model.Member;
import com.github.pagehelper.PageInfo;
import org.apache.tomcat.jni.User;

import java.util.List;

public interface UserService {
    Member login(String name, String pwd) throws Exception;
    void deleteById(int id)throws Exception;
    void changeUserStatus(int uid,int status) throws Exception;
    PageInfo<Member> searchByName(int pageNum, int pageSize,String name)throws Exception;
    PageInfo<Member> getAllUsers(int pageNum, int pageSize, String orderBy,Integer status,Integer type) throws Exception;
    PageInfo<Member> getUserByStatus(int pageNum, int pageSize,int status) throws Exception;
}
