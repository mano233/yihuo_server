package com.ahnu.server.controller;

import com.ahnu.server.dao.MemberMapper;
import com.ahnu.server.helper.JwtTokenUtils;
import com.ahnu.server.model.Member;
import com.ahnu.server.model.ResJsonBody;
import com.ahnu.server.serivce.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;

/**
 * @author mano233
 */
@Controller
@ResponseBody
@RequestMapping("/user")
public class UserController {
    UserService service;
    MemberMapper memberMapper;
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public UserController(UserService service, MemberMapper memberMapper) {
        this.service = service;
        this.memberMapper = memberMapper;
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/all")
    public ResJsonBody getAllUsers(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize,
                                   @RequestParam(value = "orderBy", required = false) String orderBy,
                                   @RequestParam(value = "status", required = false) Integer status,
                                   @RequestParam(value = "type", required = false) Integer type) throws Exception {
        ResJsonBody resJsonBody = new ResJsonBody();
        resJsonBody.setContent(service.getAllUsers(pageNum, pageSize, orderBy, status, type));
        return resJsonBody;
    }

    @PostMapping("/reg")
    public ResJsonBody reg(@RequestBody Member member) throws Exception {
        ResJsonBody resJsonBody = new ResJsonBody();
        long size = service.searchByName(1,1,member.getName()).getTotal();
        if(size>0){
            resJsonBody.setCode(5001);
            resJsonBody.setMsg("用户名已存在");
            return resJsonBody;
        };
        member.setCtime(new Date());
        member.setStatus((byte) 0);
        member.setType((byte) 0);
        member.setLevelValue(0);
        memberMapper.insert(member);
        resJsonBody.setCode(5002);
        resJsonBody.setContent(member.getId());
        resJsonBody.setMsg("注册成功，等待管理员审核通过后即可登陆。预计：1-2小时内");
        return resJsonBody;
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/delete/{uid}")
    public ResJsonBody deleteUserById(@PathVariable("uid") int uid) throws Exception {
        ResJsonBody resJsonBody = new ResJsonBody();
        service.deleteById(uid);
        resJsonBody.setMsg("ok");
        return resJsonBody;
    }

    @GetMapping("/search")
    public ResJsonBody searchByName(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, @RequestParam("name") String name) throws Exception {
        ResJsonBody resJsonBody = new ResJsonBody();
        resJsonBody.setContent(service.searchByName(pageNum, pageSize, name));
        return resJsonBody;
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/audit/pass/{uid}")
    public ResJsonBody auditPass(@PathVariable("uid") int uid) throws Exception {
        ResJsonBody resJsonBody = new ResJsonBody();
        service.changeUserStatus(uid, 1);
        resJsonBody.setMsg("ok");
        return resJsonBody;
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/audit/reject/{uid}")
    public ResJsonBody auditReject(@PathVariable("uid") int uid) throws Exception {
        ResJsonBody resJsonBody = new ResJsonBody();
        service.changeUserStatus(uid, 0);
        resJsonBody.setMsg("ok");
        return resJsonBody;
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/by_status")
    public ResJsonBody getUserByStatus(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, @RequestParam("status") int status) throws Exception {
        ResJsonBody resJsonBody = new ResJsonBody();
        resJsonBody.setContent(service.getUserByStatus(pageNum, pageSize, status));
        return resJsonBody;

    }

    @GetMapping("/{uid}")
    public ResJsonBody getUserDetail(@PathVariable int uid){
        ResJsonBody resJsonBody = new ResJsonBody();
        // int uid = (Integer) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        Member me = memberMapper.selectByPrimaryKey(uid);
        me.setPwd(null);
        resJsonBody.setContent(me);
        return resJsonBody;
    }
    @PostMapping("/login")
    public ResJsonBody login(@RequestBody String json) throws JsonProcessingException {
        ResJsonBody resJsonBody = new ResJsonBody();
        HashMap jsonMap = objectMapper.readValue(json, HashMap.class);
        try {
            Member loginUser = service.login((String) jsonMap.get("name"), (String) jsonMap.get("pwd"));
            int uid = loginUser.getId();
            HashMap<String, Object> con = new HashMap();
            con.put("uid", uid);
            String role;
            if (loginUser.getType() == 1) {
                role = "ROLE_admin";
                con.put("token", JwtTokenUtils.createToken(uid, (String) jsonMap.get("name"), role));
            } else {
                role = "ROLE_user";
                if(loginUser.getStatus()==0){
                    resJsonBody.setCode(5002);
                }else{
                    con.put("token", JwtTokenUtils.createToken(uid, (String) jsonMap.get("name"), role));
                }
            }
            resJsonBody.setContent(con);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resJsonBody;
    }
}
