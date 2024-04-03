package com.hanghae.theham.domain.chat.service;

import com.hanghae.theham.domain.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomCreateResponseDto;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.chat.repository.ChatRoomRepository;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.repository.RentalRepository;
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
    private final RentalRepository rentalRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, MemberRepository memberRepository, RentalRepository rentalRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.memberRepository = memberRepository;
        this.rentalRepository = rentalRepository;
    }


    @Transactional
    public ChatRoomCreateResponseDto createChatRoom(String email, ChatRoomCreateRequestDto requestDto) {
        // 렌탈 작성글이 존재하는지 확인
        Rental rental = rentalRepository.findById(requestDto.getRentalId()).orElseThrow(() -> {
            log.error("함께쓰기 게시글 정보를 찾을 수 없습니다. 함께쓰기 정보: {}", requestDto.getRentalId());
            return new BadRequestException(ErrorCode.NOT_FOUND_RENTAL.getMessage());
        });

        // 채팅 요청한 member
        Member buyer = memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });

        // 채팅 요청 받은 member
        Member seller = memberRepository.findByNickname(requestDto.getSellerNickname()).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. nickname: {}", requestDto.getSellerNickname());
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });

        /***
         * buyer : 구매자 (채팅 요청자)
         * seller : 게시글 작성자
         */
        if (buyer == seller) {
            throw new BadRequestException(ErrorCode.CANNOT_CHAT_WITH_SELF.getMessage());
        }

        ChatRoom existingChatRoom = chatRoomRepository.findChatRoomByBuyerAndRental(buyer, rental);
        if (existingChatRoom != null) {
            throw new BadRequestException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS.getMessage());
        }

        ChatRoom newRoom = ChatRoom.builder()
                .buyer(buyer)
                .rental(rental)
                .build();
        return new ChatRoomCreateResponseDto(chatRoomRepository.save(newRoom));
    }
}
