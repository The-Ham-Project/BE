package com.hanghae.theham.domain.chat.service;

import com.hanghae.theham.domain.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomCreateResponseDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomReadResponseDto;
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

import java.util.ArrayList;
import java.util.List;

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
        if (buyer == seller || rental.getMember() == buyer) {
            throw new BadRequestException(ErrorCode.CANNOT_CHAT_WITH_SELF.getMessage());
        }

        ChatRoom existingChatRoom = chatRoomRepository.findChatRoomByBuyerAndRental(buyer, rental);
        if (existingChatRoom != null) {
            throw new BadRequestException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS.getMessage());
        }

        ChatRoom newRoom = ChatRoom.builder()
                .buyer(buyer)
                .seller(seller)
                .rental(rental)
                .build();
        return new ChatRoomCreateResponseDto(chatRoomRepository.save(newRoom));
    }

    public List<ChatRoomReadResponseDto> getChatRoomList(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });

        // 내가 판매자 or 구매자로 참가하고 있는 채팅방 목록 조회
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomByBuyerOrSeller(member);
        List<ChatRoomReadResponseDto> chatRoomList = new ArrayList<>();

        chatRooms.stream().forEach(chatRoom -> {
            // member를 채팅 상대 정보로 변경
            Member toMember = resolveToMember(chatRoom, member.getEmail());
            chatRoomList.add(new ChatRoomReadResponseDto(
                    chatRoom.getId(),
                    toMember.getId(),
                    toMember.getNickname(),
                    toMember.getProfileUrl(),
                    chatRoom.getRental().getTitle()
            ));
        });

        return chatRoomList;
    }

    /***
     * 
     * @param chatRoom  현재 채팅방 정보
     * @param fromEmail 조회하는 유저 이메일
     * @return
     *  채팅방 seller 정보와 현재 email 정보가 일치?
     *  true : member = Buyer
     *  false : member = seller
     */
    private Member resolveToMember(ChatRoom chatRoom, String fromEmail) {
        return chatRoom.getSeller().getEmail().equals(fromEmail) ?
                chatRoom.getBuyer() :
                chatRoom.getSeller();
    }
}
