package com.ahnu.server.config.JWT;

import com.ahnu.server.helper.JwtTokenUtils;
import com.auth0.jwt.interfaces.Claim;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String tokenHeader = request.getHeader(JwtTokenUtils.TOKEN_HEADER);
        //如果请求头中没有Authorization信息则直接放行了
        if(tokenHeader == null){
            chain.doFilter(request, response);
            return;
        }
        //如果请求头中有token,则进行解析，并且设置认证信息
        UsernamePasswordAuthenticationToken authentication = null;
        try {
            authentication = getAuthentication(tokenHeader);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.doFilterInternal(request, response, chain);
    }

    //获取用户信息
    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader)throws Exception{
        String token = tokenHeader.replace(JwtTokenUtils.TOKEN_PREFIX, "");
        // 获得权限 添加到权限上去
        Map<String, Claim> claimMap =  JwtTokenUtils.verifyToken(token);
        String username =  claimMap.get("name").asString();
        int uid = claimMap.get("uid").asInt();
        String role = claimMap.get("role").asString();
        List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add((GrantedAuthority) () -> role);
        if(username != null){
            return new UsernamePasswordAuthenticationToken(username, uid,roles);
        }
        return null;
    }

}


