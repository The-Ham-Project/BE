package com.hanghae.theham.domain.chat.service;

import com.hanghae.theham.domain.chat.dto.ChatResponseDto.ChatReadResponseDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomDetailResponseDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomReadResponseDto;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.chat.repository.ChatRepository;
import com.hanghae.theham.domain.chat.repository.ChatRoomRepository;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.repository.RentalRepository;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final RentalRepository rentalRepository;
    private final ChatRepository chatRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, MemberRepository memberRepository, RentalRepository rentalRepository, ChatRepository chatRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.memberRepository = memberRepository;
        this.rentalRepository = rentalRepository;
        this.chatRepository = chatRepository;
    }

    @Transactional
    public Long handleChatRoom(String email, ChatRoomCreateRequestDto requestDto) {
        // 렌탈 작성글이 존재하는지 확인
        Rental rental = findRentalById(requestDto.getRentalId());

        // 채팅 요청한 member
        Member buyer = findMemberByEmail(email);

        // 채팅 요청 받은 member
        Member seller = memberRepository.findByNickname(requestDto.getSellerNickname()).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. nickname: {}", requestDto.getSellerNickname());
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });

        if (buyer == seller || rental.getMember() == buyer) {
            throw new BadRequestException(ErrorCode.CANNOT_CHAT_WITH_SELF.getMessage());
        }

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findChatRoomByBuyerAndRental(buyer, rental);

        return optionalChatRoom.orElseGet(()
                -> createChatRoom(buyer, seller, rental)).getId();
    }

    @Transactional
    public ChatRoom createChatRoom(Member buyer, Member seller, Rental rental) {
        ChatRoom newRoom = ChatRoom.builder()
                .buyer(buyer)
                .seller(seller)
                .rental(rental)
                .build();
        return chatRoomRepository.save(newRoom);
    }

    // 채팅방 전체 목록 조회
    public List<ChatRoomReadResponseDto> getChatRoomList(String email, int page, int size) {
        Member member = findMemberByEmail(email);

        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.Direction.DESC, "modifiedAt");
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findChatRoomByMember(member, pageRequest);

        List<ChatRoom> chatRooms = chatRoomPage.getContent();
        List<ChatRoomReadResponseDto> chatRoomList = new ArrayList<>();

        chatRooms.stream().forEach(chatRoom -> {
            // member를 채팅 상대 정보로 변경
            Member toMember = resolveToMember(chatRoom, member.getEmail());
            chatRoomList.add(new ChatRoomReadResponseDto(
                    chatRoom.getId(),
                    toMember.getId(),
                    toMember.getNickname(),
                    toMember.getProfileUrl(),
                    chatRoom.getLastChat(),
                    chatRoom.getModifiedAt()
            ));
        });
        return chatRoomList;
    }

    // 채팅방 상세 조회
    public ChatRoomDetailResponseDto getChatRoom(String email, Long chatRoomId, int page, int size) {
        Member member = findMemberByEmail(email);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> {
            log.error("채팅방 정보를 찾을 수 없습니다. 채팅방 ID: {}", chatRoomId);
            return new BadRequestException(ErrorCode.NOT_FOUND_CHAT_ROOM.getMessage());
        });
        String senderProfileImage = member.getProfileUrl();
        Member toMember = resolveToMember(chatRoom, member.getEmail());

        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.Direction.DESC, "createdAt");

        List<ChatReadResponseDto> chatResponseList = chatRepository.findByChatRoom(chatRoom, pageRequest)
                .stream()
                .map(ChatReadResponseDto::new)
                .toList();

        return new ChatRoomDetailResponseDto(
                toMember.getNickname(),
                toMember.getProfileUrl(),
                senderProfileImage,
                chatResponseList);
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

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });
    }

    private Rental findRentalById(Long id) {
        return rentalRepository.findById(id).orElseThrow(() -> {
            log.error("함께쓰기 게시글 정보를 찾을 수 없습니다. 함께쓰기 정보: {}", id);
            return new BadRequestException(ErrorCode.NOT_FOUND_RENTAL.getMessage());
        });
    }
}
