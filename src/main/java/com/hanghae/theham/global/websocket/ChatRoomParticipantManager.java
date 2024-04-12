package com.hanghae.theham.global.websocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatRoomParticipantManager { // 채팅방 참가자 수 관리
    // 채팅방 id, 접속자 수 매핑하여 저장
    private final Map<Long, Integer> roomUserCnt = new ConcurrentHashMap<>();

    // 채팅방에 사용자가 접속할 때 호출
    public void increaseMemberCount(Long chatRoomId) {
        roomUserCnt.compute(chatRoomId, (key, val) -> (val == null) ? 1 : val + 1);
    }

    // 채팅방에서 사용자가 나갈 때 호출
    public void decreaseMemberCount(Long chatRoomId) {
        roomUserCnt.computeIfPresent(chatRoomId, (id, count) -> (count - 1 <= 0) ? null : count - 1);
    }

    // 특정 채팅방의 현재 동시 접속자 수 조회
    public int getCurrentMemberCount(Long chatRoomId) {
        return roomUserCnt.getOrDefault(chatRoomId, 0);
    }
}

