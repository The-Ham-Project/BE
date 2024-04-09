package com.hanghae.theham.domain.chat.service;

import com.hanghae.theham.domain.chat.dto.ChatRequestDto.ChatSendMessageRequestDto;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.chat.repository.ChatRepository;
import com.hanghae.theham.domain.chat.repository.ChatRoomRepository;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public ChatService(ChatRepository chatRepository, ChatRoomRepository chatRoomRepository, MemberRepository memberRepository) {
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void saveMessage(ChatSendMessageRequestDto requestDto, String email, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOT_FOUND_CHAT_ROOM.getMessage())
                );
        Member sender = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage())
                );
        chatRoom.updateLastChat(requestDto.getMessage());
        chatRepository.save(requestDto.toEntity(chatRoom, sender));
    }
}
