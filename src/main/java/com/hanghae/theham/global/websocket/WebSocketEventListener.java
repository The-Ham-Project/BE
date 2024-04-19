package com.hanghae.theham.global.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Objects;

@Component
@Slf4j
public class WebSocketEventListener {
    private final ChatRoomParticipantManager chatRoomParticipantManager;

    public WebSocketEventListener(ChatRoomParticipantManager chatRoomParticipantManager) {
        this.chatRoomParticipantManager = chatRoomParticipantManager;
    }

    @EventListener
    public void sessionConnectedEvent(SessionConnectedEvent event) { // 클라이언트가 websocket에 열결 되고 세션 확립될 때 발생
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        log.info("received session connected event: session id={}", accessor.getSessionId());
    }

    @EventListener
    public void sessionSubscribeEvent(SessionSubscribeEvent event) {// 클라이언트가 특정 주제 구독할 때 발생

        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        log.info("session destination: {}", accessor.getDestination());
        log.info("received session connected event: session id={}", accessor.getSessionId());

        // 사용자 정보 추출
        Authentication authentication = (Authentication) event.getMessage().getHeaders().get("simpUser");
        assert authentication != null;
        String email = authentication.getName();
        Long chatRoomId = Long.valueOf(Objects.requireNonNull(accessor.getFirstNativeHeader("chatRoomId")));
        chatRoomParticipantManager.addMemberToRoom(chatRoomId, email);
        log.info("구독 발생 event :::::: chatRoomId : {}, Member : {} ", chatRoomId, email);
        log.info("구독 발생 구독 현황 구독 현황 :::::::::::: {}", chatRoomParticipantManager.getMemberCountInRoom(chatRoomId));
    }

    @EventListener
    public void sessionUnsubscribeEvent(SessionUnsubscribeEvent event) { // 구독 해지할 때 발생
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

        // 사용자 정보 추출
        Authentication authentication = (Authentication) event.getMessage().getHeaders().get("simpUser");
        assert authentication != null;
        String email = authentication.getName();
        Long chatRoomId = Long.valueOf(Objects.requireNonNull(accessor.getFirstNativeHeader("chatRoomId")));
        chatRoomParticipantManager.removeMemberFromRoom(chatRoomId, email);

        log.info("구독해지 event chatRoomId : {}, Member : {} ", chatRoomId, email);
        log.info("구독 해지 구독 현황 구독 현황 :::::::::::: {}", chatRoomParticipantManager.getMemberCountInRoom(chatRoomId));
    }

    @EventListener
    public void sessionDisconnectEvent(SessionDisconnectEvent event) { // 웹소켓에서 연결 해제할 때 발생
        log.info("구독취소 event : {}", event.getCloseStatus());
    }
}