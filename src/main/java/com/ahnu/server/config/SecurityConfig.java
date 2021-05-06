package com.ahnu.server.config;

import com.ahnu.server.config.JWT.JwtAuthenticationFilter;
import com.ahnu.server.model.ResJsonBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Component
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/chat/**").permitAll()
                .antMatchers(HttpMethod.POST,"/user/login").permitAll()
                .antMatchers(HttpMethod.GET,"/comment/**").permitAll()
                .antMatchers(HttpMethod.POST,"/user/reg").permitAll()
                .antMatchers(HttpMethod.GET,"/goods*","/goods/**").permitAll()
                .antMatchers(HttpMethod.POST,"/goods/search/**").permitAll()
                .anyRequest().authenticated()
                .and()
                // 禁用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                // 授权错误信息处理
                .exceptionHandling()
                //用户访问资源没有携带正确的token
                .authenticationEntryPoint((request, response, e) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ResJsonBody jsonBody = new ResJsonBody();
                    jsonBody.setContent("用户访问资源没有携带正确的token");
                    jsonBody.setCode(204);
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().println(objectMapper.writeValueAsString(jsonBody));
                })
                //用户访问没有授权资源
                .accessDeniedHandler((request, response, e) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ResJsonBody jsonBody = new ResJsonBody();
                    jsonBody.setContent("没有访问权限");
                    jsonBody.setCode(205);
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().println(objectMapper.writeValueAsString(jsonBody));
                });
    }


}