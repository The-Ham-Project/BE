package com.hanghae.theham.domain.chat.service;

import com.hanghae.theham.domain.chat.dto.ChatRequestDto.ChatRoomCreateRequestDto;
import com.hanghae.theham.domain.chat.dto.ChatResponseDto.ChatRoomCreateResponseDto;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.chat.repository.ChatRoomRepository;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, MemberRepository memberRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ChatRoomCreateResponseDto createChatRoom(String email, ChatRoomCreateRequestDto requestDto) {
        // 채팅 요청한 member
        Member requester = memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });

        // 채팅 요청 받은 member
        Member requestedMember = memberRepository.findByNickname(requestDto.getNickname()).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. nickname: {}", requestDto.getNickname());
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });

        if(requester == requestedMember){
            throw new BadRequestException(ErrorCode.CANNOT_CHAT_WITH_SELF.getMessage());
        }

        ChatRoom existingRoom = chatRoomRepository.findByMembers(requester, requestedMember);

        if (existingRoom != null) {
            throw new BadRequestException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS.getMessage());
        }

        ChatRoom newRoom = ChatRoom.builder()
                .memberA(requester)
                .memberB(requestedMember)
                .build();

        return new ChatRoomCreateResponseDto(chatRoomRepository.save(newRoom));
    }
}
