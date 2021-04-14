package com.ahnu.server.serivce.impl;

import com.ahnu.server.dao.MemberMapper;
import com.ahnu.server.dao.MemberMapperCustom;
import com.ahnu.server.model.Member;
import com.ahnu.server.serivce.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    MemberMapperCustom memberCustomMapper;
    MemberMapper memberMapper;

    @Autowired
    public UserServiceImpl(MemberMapperCustom memberCustomMapper, MemberMapper memberMapper) {
        this.memberCustomMapper = memberCustomMapper;
        this.memberMapper = memberMapper;
    }

    @Override
    public Member login(String name, String pwd) throws Exception {
        Member user = memberCustomMapper.selectByName(name);
        if (user==null || !user.getPwd().equals(pwd)) {
            throw new Exception("密码不正确");
        }
        return user;
    }

    @Override
    public void deleteById(int id) throws Exception {
        memberMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void changeUserStatus(int uid,int status) throws Exception{
        memberCustomMapper.changeStatus(uid,status);
    }

    @Override
    public PageInfo<Member> searchByName(int pageNum, int pageSize,String name) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(memberCustomMapper.searchUserByName(name));
    }

    @Override
    public PageInfo<Member> getAllUsers(int pageNum, int pageSize, String orderBy,Integer status,Integer type) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        if (orderBy == null) {
            orderBy = "ctime desc";
        }
        PageHelper.orderBy(orderBy);
        return new PageInfo<>(memberCustomMapper.selectAll(type,status));
    }

    @Override
    public PageInfo<Member> getUserByStatus(int pageNum, int pageSize, int status) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("ctime desc");
        return new PageInfo<>(memberCustomMapper.selectByStatus(status));
    }
}
