package com.hanghae.theham.global.config;

import com.hanghae.theham.global.websocket.WebSocketInterceptor;
import com.hanghae.theham.global.websocket.exception.WebSocketErrorHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketInterceptor webSocketInterceptor;
    private final WebSocketErrorHandler webSocketErrorHandler;

    public WebSocketConfig(WebSocketInterceptor webSocketInterceptor, WebSocketErrorHandler webSocketErrorHandler) {
        this.webSocketInterceptor = webSocketInterceptor;
        this.webSocketErrorHandler = webSocketErrorHandler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry
                .setErrorHandler(webSocketErrorHandler)
                .addEndpoint("/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub"); // @MessageMapping 메서드로 라우팅
        registry.enableSimpleBroker("/sub", "/queue"); // 구독자에게 메세지 발생
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketInterceptor);
    }
}
