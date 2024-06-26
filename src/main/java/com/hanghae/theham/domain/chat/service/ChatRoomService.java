package com.hanghae.theham.domain.chat.service;

import com.hanghae.theham.domain.chat.dto.ChatResponseDto.ChatReadResponseDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomDetailResponseDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomInfoResponseDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomReadResponseDto;
import com.hanghae.theham.domain.chat.entity.Chat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.chat.entity.type.VisibleType;
import com.hanghae.theham.domain.chat.repository.ChatRepository;
import com.hanghae.theham.domain.chat.repository.ChatRoomRepository;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.notification.service.NotificationService;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import com.hanghae.theham.domain.rental.repository.RentalImageRepository;
import com.hanghae.theham.domain.rental.repository.RentalImageThumbnailRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Transactional(readOnly = true)
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final RentalRepository rentalRepository;
    private final ChatRepository chatRepository;
    private final RentalImageRepository rentalImageRepository;
    private final RentalImageThumbnailRepository rentalImageThumbnailRepository;
    private final NotificationService notificationService;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, MemberRepository memberRepository, RentalRepository rentalRepository, ChatRepository chatRepository, RentalImageRepository rentalImageRepository, RentalImageThumbnailRepository rentalImageThumbnailRepository, NotificationService notificationService) {
        this.chatRoomRepository = chatRoomRepository;
        this.memberRepository = memberRepository;
        this.rentalRepository = rentalRepository;
        this.chatRepository = chatRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.rentalImageThumbnailRepository = rentalImageThumbnailRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Long handleChatRoom(String email, ChatRoomCreateRequestDto requestDto) {
        // 렌탈 작성글이 존재하는지 확인
        Rental rental = validateRental(requestDto.getRentalId());

        // 채팅 요청한 member
        Member sender = validateMember(email);

        // 채팅 요청 받은 member
        Member receiver = validateMember(rental.getMember().getEmail());

        if (sender.equals(receiver)) {
            throw new BadRequestException(ErrorCode.CANNOT_CHAT_WITH_SELF.getMessage());
        }

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findChatRoomBySenderAndRental(sender, rental);

        ChatRoom chatRoom = optionalChatRoom.orElseGet(() -> createChatRoom(sender, receiver, rental));

        if (chatRoom.getSenderIsDeleted() || chatRoom.getReceiverIsDeleted()) {
            chatRoom.rejoinChatRoom();
            chatRoomRepository.save(chatRoom);
        }
        return chatRoom.getId();
    }

    @Transactional
    public ChatRoom createChatRoom(Member sender, Member receiver, Rental rental) {
        ChatRoom newRoom = ChatRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .rental(rental)
                .build();
        return chatRoomRepository.save(newRoom);
    }

    // 채팅방 전체 목록 조회
    public ChatRoomReadResponseDto getChatRoomList(String email, int page, int size) {
        Member member = validateMember(email);

        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.Direction.DESC, "lastChatTime");
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findChatRoomByMember(member, pageRequest);

        List<ChatRoom> chatRooms = chatRoomPage.getContent();
        List<ChatRoomInfoResponseDto> chatRoomList = chatRooms.stream()
                .map(chatRoom -> {
                    Member toMember = chatRoom.getSender().equals(member) ? chatRoom.getReceiver() : chatRoom.getSender();
                    int unreadCount = chatRoom.getSender().equals(member) ? chatRoom.getSenderUnreadCount() : chatRoom.getReceiverUnreadCount();
                    return new ChatRoomInfoResponseDto(chatRoom, toMember, unreadCount);
                })
                .toList();
        return new ChatRoomReadResponseDto(chatRoomPage, chatRoomList);
    }

    // 채팅방 상세 조회
    @Transactional
    public ChatRoomDetailResponseDto getChatRoom(String email, Long chatRoomId, int page, int size) {

        ChatRoom chatRoom = validateChatRoom(chatRoomId);
        Rental rental = validateRental(chatRoom.getRental().getId());
        Member member = validateMember(email); // 현재 접속한 멤버

        validateChatRoomParticipant(chatRoom, member); // 채팅방 참여자인지 확인

        Member sender = chatRoom.getSender(); // 채팅방 최초 발신자
        Member receiver = chatRoom.getReceiver(); // 채팅방 최초 수신자

        // 발신자가 최초 발신자와 동일한지 확인
        boolean isSender = sender.equals(member);


        List<Chat> chatsToMarkRead = readPreviousMessages(chatRoom, isSender, sender, receiver);
//        .forEach(Chat::updateIsRead);

        if(!chatsToMarkRead.isEmpty()){
            // 이전 메시지 읽음 처리
            chatsToMarkRead.forEach(Chat::updateIsRead);

            // 알림 발송
            notificationService.sendNotification(member);
        }


        // 채팅방 업데이트
        chatRoom.updateChatRoom(isSender);

        // 채팅 메시지 가져오기
        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.Direction.DESC, "createdAt");

        Page<Chat> chatPage = chatRepository.findByChatRoomAndVisibleIn(
                chatRoom,
                VisibleType.BOTH,
                isSender ? VisibleType.ONLY_SENDER : VisibleType.ONLY_RECEIVER,
                pageRequest
        );

        List<ChatReadResponseDto> chatResponseList = new ArrayList<>(chatPage.map(ChatReadResponseDto::new).toList());
        Collections.reverse(chatResponseList);

        // rental thumnail 이미지
        AtomicReference<String> rentalThumbnailUrl = new AtomicReference<>(rentalImageRepository.findFirstByRental(chatRoom.getRental())
                .map(RentalImage::getImageUrl)
                .orElse(null));

        if (rentalThumbnailUrl.get() != null) {
            rentalImageThumbnailRepository.findByImagePath(rentalThumbnailUrl.get()).ifPresent(
                    rentalImageThumbnail -> rentalThumbnailUrl.set(rentalImageThumbnail.getThumbnailPath())
            );
        }

        return new ChatRoomDetailResponseDto(
                chatPage,
                rental,
                rentalThumbnailUrl.get(),
                isSender ? receiver : sender,
                member,
                chatResponseList);
    }

    // 채팅방 나가기 기능
    @Transactional
    public void leaveChatRoom(String email, Long chatRoomId) {
        ChatRoom chatRoom = validateChatRoom(chatRoomId); // 채팅방 존재여부
        Member member = validateMember(email); // 존재하는 회원여부
        boolean isSender = chatRoom.getSender().equals(member);

        validateChatRoomParticipant(chatRoom, member);

        // 1. 한명 이미 나간상태 -> 채팅방, 해당 메세지 완전 삭제 진행
        if (chatRoom.getSenderIsDeleted() || chatRoom.getReceiverIsDeleted()) {
            chatRoomRepository.delete(chatRoom);
            return;
        }

        // 2. 두명 다 채팅방에 있는 상태
        List<Chat> deleteChatList = chatRepository.findByChatRoomAndVisible(chatRoom, isSender ? VisibleType.ONLY_SENDER : VisibleType.ONLY_RECEIVER);
        chatRepository.deleteAll(deleteChatList);
        List<Chat> chatList = chatRepository.findByChatRoomAndVisible(chatRoom, VisibleType.BOTH);
        chatList.forEach(chat -> {
            chat.updateChatVisible(isSender ? VisibleType.ONLY_RECEIVER : VisibleType.ONLY_SENDER);
        });
        chatRoom.disableChatRoom(isSender);
        chatRoomRepository.save(chatRoom);
    }

    // 채팅 방 상세조회 이전에 안읽은 메세지 가져오기
    private List<Chat> readPreviousMessages(ChatRoom chatRoom, boolean isSender, Member sender, Member receiver) {
        if (isSender) {
            // 현재 사용자가 발신자인 경우, 수신자(receiver)가 보낸 읽지 않은 메시지를 가져온다.
            return chatRepository.findByChatRoomAndSenderAndIsRead(chatRoom, receiver, false);
        }
        // 현재 사용자가 수신자인 경우, 발신자(sender)가 보낸 읽지 않은 메시지를 가져온다.
        return chatRepository.findByChatRoomAndSenderAndIsRead(chatRoom, sender, false);
    }

    private Member validateMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });
    }

    private Rental validateRental(Long id) {
        return rentalRepository.findById(id).orElseThrow(() -> {
            log.error("함께쓰기 게시글 정보를 찾을 수 없습니다. 함께쓰기 정보: {}", id);
            return new BadRequestException(ErrorCode.NOT_FOUND_RENTAL.getMessage());
        });
    }

    private ChatRoom validateChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(() -> {
            log.error("함께쓰기 게시글 정보를 찾을 수 없습니다. 함께쓰기 정보: {}", chatRoomId);
            return new BadRequestException(ErrorCode.NOT_FOUND_CHAT_ROOM.getMessage());
        });
    }

    private void validateChatRoomParticipant(ChatRoom chatRoom, Member member) {
        boolean isSender = chatRoom.getSender().equals(member);
        boolean isReceiver = chatRoom.getReceiver().equals(member);

        if (!(isSender && !chatRoom.getSenderIsDeleted()) && !(isReceiver && !chatRoom.getReceiverIsDeleted())) {
            log.error("채팅방 참여자가 아닙니다. 이메일: {}, 채팅방 ID: {}", member.getEmail(), chatRoom.getId());
            throw new BadRequestException(ErrorCode.INVALID_CHAT_ROOM_PARTICIPANT.getMessage());
        }
    }
}