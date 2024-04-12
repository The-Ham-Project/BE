package com.hanghae.theham.global.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
@Slf4j
public class WebSocketEventListener {
    @EventListener
    public void sessionConnectedEvent(SessionConnectedEvent event) { // 클라이언트가 websocket에 열결 되고 세션 확립될 때 발생
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        log.info("received session connected event: session id={}", accessor.getSessionId());
        log.info("session destination: {}", accessor.getDestination());
    }

    @EventListener
    public void sessionSubscribeEvent(SessionSubscribeEvent event) {// 클라이언트가 특정 주제 구독할 때 발생
        log.info("구독 발생 event : {}", event);
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        log.info("received session subscribe event: session id={}", accessor.getSessionId());
        log.info("session destination: {}", accessor.getDestination());
        log.info("session subscription id: {}", accessor.getSubscriptionId());
    }

    @EventListener
    public void SessionUnsubscribeEvent(SessionUnsubscribeEvent event) { // 구독 해지할 때 발생
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        log.info("received session ubsubscribe event: session id={}", accessor.getSessionId());
        log.info("session destination: {}", accessor.getDestination());
        log.info("session subscription id: {}", accessor.getSubscriptionId());
    }

    @EventListener
    public void sessionDisconnectEvent(SessionDisconnectEvent event) { // 웹소켓에서 연결 해제할 때 발생
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        log.info("received session disconnect event: session id={}", accessor.getSessionId());
        log.info("session destination: {}", accessor.getDestination());
        log.info("session subscription id: {}", accessor.getSubscriptionId());
    }
}