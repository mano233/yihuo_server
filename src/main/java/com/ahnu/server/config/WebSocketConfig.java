package com.ahnu.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * ServerEndpointExporter 是由Spring官方提供的标准实现，
 * 用于扫描ServerEndpointConfig配置类和@ServerEndpoint注解实例。
 * 使用规则也很简单：1.如果使用默认的嵌入式容器 比如Tomcat 则必须手工在上下文提供ServerEndpointExporter。
 * 2. 如果使用外部容器部署war包，则不要提供提供ServerEndpointExporter，因为此时SpringBoot默认将扫描服务端的行为交给外部容器处理。
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
