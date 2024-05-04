package com.hanghae.theham.domain.chat.service;

import com.hanghae.theham.domain.chat.dto.ChatRequestDto.ChatSendMessageRequestDto;
import com.hanghae.theham.domain.chat.dto.ChatResponseDto.ChatReadResponseDto;
import com.hanghae.theham.domain.chat.entity.Chat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.chat.repository.ChatRepository;
import com.hanghae.theham.domain.chat.repository.ChatRoomRepository;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.notification.service.NotificationService;
import com.hanghae.theham.global.exception.ErrorCode;
import com.hanghae.theham.global.websocket.ChatRoomParticipantManager;
import com.hanghae.theham.global.websocket.exception.WebSocketException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class ChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final ChatRoomParticipantManager chatRoomParticipantManager;

    public ChatService(SimpMessagingTemplate messagingTemplate, ChatRepository chatRepository, ChatRoomRepository chatRoomRepository, MemberRepository memberRepository, NotificationService notificationService, ChatRoomParticipantManager chatRoomParticipantManager) {
        this.messagingTemplate = messagingTemplate;
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.memberRepository = memberRepository;
        this.notificationService = notificationService;
        this.chatRoomParticipantManager = chatRoomParticipantManager;
    }

    @Transactional
    public void saveMessage(ChatSendMessageRequestDto requestDto, String email, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new WebSocketException(ErrorCode.NOT_FOUND_CHAT_ROOM)
                );
        Member sender = memberRepository.findByEmail(email)
                .orElseThrow(() -> new WebSocketException(ErrorCode.NOT_FOUND_MEMBER)
                );
        boolean isSender = chatRoom.getSender().equals(sender);

        if (chatRoom.getSenderIsDeleted() || chatRoom.getReceiverIsDeleted()) {
            chatRoom.rejoinChatRoom();
        }

        int currentMemberCount = chatRoomParticipantManager.getMemberCountInRoom(roomId);

        // 메세지 발송 처리
        Chat chat = chatRepository.save(requestDto.toEntity(chatRoom, sender, currentMemberCount));

        // 채팅방 업데이트
        chatRoom.updateChatRoom(isSender, chat.getMessage(), chat.getCreatedAt(), currentMemberCount);

        // 구독자 들에게 메세지 전송
        messagingTemplate.convertAndSend("/sub/chat/chatRoom/" + roomId, new ChatReadResponseDto(chat));

        // 채팅방 알림
        notificationService.sendNotification(chatRoom, isSender, currentMemberCount);
    }
}
