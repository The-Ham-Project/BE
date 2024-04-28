package com.hanghae.theham.domain.notification.service;

import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomInfoResponseDto;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.chat.repository.ChatRoomRepository;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.notification.dto.NotificationResponseDto.NotificationCreateResponseDto;
import com.hanghae.theham.domain.notification.dto.type.NotificationType;
import com.hanghae.theham.domain.notification.repository.EmitterRepository;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j(topic = "NotificationService")
@Service
public class NotificationService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 연결시간 1시간 설정
    private final EmitterRepository emitterRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    public NotificationService(EmitterRepository emitterRepository, MemberRepository memberRepository, ChatRoomRepository chatRoomRepository) {
        this.emitterRepository = emitterRepository;
        this.memberRepository = memberRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public SseEmitter subscribe(String email) {
        Member member = validateMember(email);
        Long memberId = member.getId();

        String emitterId = createTimeIncludeId(memberId);

        SseEmitter emitter = createEmitter(emitterId);

        NotificationCreateResponseDto responseDto = new NotificationCreateResponseDto(getTotalUnreadMessagesCount(member));

        sendToClient(emitter, emitterId, ResponseDto.success(NotificationType.CONNECTED.getMessage(), responseDto));

        return emitter;
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            log.error("Unable to emit");
            emitterRepository.deleteById(emitterId);
            emitter.completeWithError(exception);
        }
    }

    public void sendNotification(ChatRoom chatRoom, boolean isSender, int currentMemberCount) {
        // 채팅방 업데이트가 될 대상 : 채팅방 참여자
        Member sender = chatRoom.getSender();
        sendChatRoomInformation(sender.getId(), ResponseDto.success(NotificationType.UPDATE_CHATROOM.getMessage(), getChatRoomInfoResponseDto(chatRoom, isSender)));

        Member receiver = chatRoom.getReceiver();
        sendChatRoomInformation(receiver.getId(), ResponseDto.success(NotificationType.UPDATE_CHATROOM.getMessage(), getChatRoomInfoResponseDto(chatRoom, isSender)));

        if (currentMemberCount == 1) {
            int totalUnreadCount = getTotalUnreadMessagesCount(isSender ? receiver : sender);
            NotificationCreateResponseDto responseDto = new NotificationCreateResponseDto(totalUnreadCount);
            sendTotalUnreadCount(isSender ? receiver.getId() : sender.getId(), ResponseDto.success(NotificationType.CHAT.getMessage(), responseDto));
        }
    }

    private void sendChatRoomInformation(Long memberId, ResponseDto<ChatRoomInfoResponseDto> responseDto) {
        // 항상 채팅방 참여자 모두에게 채팅방 업데이트 정보 넘겨줘야 한다.
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllByMemberId(String.valueOf(memberId));

        sseEmitters.forEach(
                (key, emitter) -> sendToClient(emitter, key, responseDto)
        );
    }

    private void sendTotalUnreadCount(Long memberId, ResponseDto<NotificationCreateResponseDto> responseDto) {
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllByMemberId(String.valueOf(memberId));

        sseEmitters.forEach(
                (key, emitter) -> sendToClient(emitter, key, responseDto)
        );
    }

    private ChatRoomInfoResponseDto getChatRoomInfoResponseDto(ChatRoom chatRoom, boolean isSender) {
        Member toMember = isSender ? chatRoom.getReceiver() : chatRoom.getSender();
        int unreadCount = isSender ? chatRoom.getSenderUnreadCount() : chatRoom.getReceiverUnreadCount();
        return new ChatRoomInfoResponseDto(chatRoom, toMember, unreadCount);
    }

    private int getTotalUnreadMessagesCount(Member member) {
        return chatRoomRepository.countUnreadMessagesReceivedByUser(member);
    }

    private String createTimeIncludeId(Long memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }

    private SseEmitter createEmitter(String emitterId) {
        SseEmitter sseEmitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        sseEmitter.onTimeout(sseEmitter::complete);
        sseEmitter.onCompletion(() -> emitterRepository.deleteById(emitterId));

        sseEmitter.onError(throwable -> {
            log.error("[SSE] ::::::::::::::::::::::: [ onError ]");
            log.error("", throwable);
            sseEmitter.complete();
        });
        return sseEmitter;
    }

    private Member validateMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });
    }

    @Scheduled(fixedRate = 30000) // 30초마다 heartbeat 메세지 전달.
    public void sendHeartbeat() {
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitters();
        sseEmitters.forEach((key, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .id(key)
                        .name("heartbeat")
                        .data(""));
                log.info("하트비트 메세지 전송");
            } catch (IOException e) {
                emitterRepository.deleteById(key);
                log.error("하트비트 전송 실패: {}", e.getMessage());
            }
        });
    }
}