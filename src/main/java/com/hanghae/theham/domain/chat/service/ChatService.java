package com.hanghae.theham.domain.chat.service;

import com.hanghae.theham.domain.chat.dto.ChatRequestDto.ChatSendMessageRequestDto;
import com.hanghae.theham.domain.chat.dto.ChatResponseDto.ChatReadResponseDto;
import com.hanghae.theham.domain.chat.entity.Chat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.chat.repository.ChatRepository;
import com.hanghae.theham.domain.chat.repository.ChatRoomRepository;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import com.hanghae.theham.global.websocket.ChatRoomParticipantManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomParticipantManager chatRoomParticipantManager;

    public ChatService(ChatRepository chatRepository, ChatRoomRepository chatRoomRepository, MemberRepository memberRepository, ChatRoomParticipantManager chatRoomParticipantManager) {
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.memberRepository = memberRepository;
        this.chatRoomParticipantManager = chatRoomParticipantManager;
    }

    @Transactional
    public ChatReadResponseDto saveMessage(ChatSendMessageRequestDto requestDto, String email, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOT_FOUND_CHAT_ROOM.getMessage())
                );
        Member sender = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage())
                );
        boolean isSender = chatRoom.getSender().equals(sender);

        if (chatRoom.getSenderIsDeleted() || chatRoom.getReceiverIsDeleted()) {
            chatRoom.rejoinChatRoom();
        }

        int currentMemberCount = chatRoomParticipantManager.getMemberCountInRoom(roomId);

        // 메세지 발송 처리
        Chat chat = chatRepository.save(requestDto.toEntity(chatRoom, sender, currentMemberCount));

        System.out.println(chat.getCreatedAt());

        // 채팅방 업데이트
        chatRoom.updateChatRoom(isSender, chat.getMessage(), chat.getCreatedAt(), currentMemberCount);

        return new ChatReadResponseDto(chat);
    }
}
