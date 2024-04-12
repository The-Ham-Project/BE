package com.hanghae.theham.global.websocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatRoomParticipantManager { // 채팅방 참가자 수 관리
    // 채팅방 id, 접속자 수 매핑하여 저장
    private final Map<Long, Set<String>> roomMemberCount = new ConcurrentHashMap<>();

    // 채팅방에 사용자가 접속할 때 호출
    public void addMemberToRoom(Long roomId, String email) {// 0~2< 3++++ 유저 정보를 넣으면 되지 않을까?
        roomMemberCount.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(email);
    }

    // 채팅방에서 사용자가 나갈 때 호출
    public void removeMemberFromRoom(Long roomId, String email) {
        Set<String> Members = roomMemberCount.get(roomId);
        if (Members != null) {
            Members.remove(email);
            if (Members.isEmpty()) {
                roomMemberCount.remove(roomId);
            }
        }
    }

    // 특정 채팅방의 현재 동시 접속자 수 조회
    public int getMemberCountInRoom(Long roomId) {
        Set<String> users = roomMemberCount.get(roomId);
        return users != null ? users.size() : 0;
    }
}

