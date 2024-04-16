package com.hanghae.theham.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.theham.domain.chat.dto.ChatResponseDto.ChatReadResponseDto;
import com.hanghae.theham.domain.chat.entity.Chat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.rental.entity.Rental;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomResponseDto {

    @Getter
    public static class ChatRoomDetailResponseDto {
        private final int totalPage;
        private final int currentPage;
        private final Long rentalId;
        private final String rentalTitle;
        private final String rentalThumbnailUrl;
        private final long rentalFee;
        private final long deposit;
        private final String toUserNickname; // 상대방 식별
        private final String toUserProfileImage; // 상대방 이미지
        private final String senderProfileImage; // sender 이미지
        private final List<ChatReadResponseDto> chatReadResponseDtoList;

        public ChatRoomDetailResponseDto(Page<Chat> chatPage,
                                         Rental rental,
                                         String rentalThumbnailUrl,
                                         Member toMember,
                                         Member member,
                                         List<ChatReadResponseDto> chatReadResponseDtoList) {
            this.totalPage = chatPage.getTotalPages();
            this.currentPage = chatPage.getNumber() + 1;
            this.rentalId = rental.getId();
            this.rentalTitle = rental.getTitle();
            this.rentalThumbnailUrl = rentalThumbnailUrl;
            this.rentalFee = rental.getRentalFee();
            this.deposit = rental.getDeposit();
            this.toUserNickname = toMember.getNickname();
            this.toUserProfileImage = toMember.getProfileUrl();
            this.senderProfileImage = member.getProfileUrl();
            this.chatReadResponseDtoList = chatReadResponseDtoList;
        }
    }

    @Getter
    public static class ChatRoomReadResponseDto {
        private final int totalPage;
        private final int currentPage;
        private final List<ChatRoomListResponseDto> ChatRoomListResponseDto;

        public ChatRoomReadResponseDto(Page<ChatRoom> chatRoomPage, List<ChatRoomListResponseDto> chatRoomListResponseDto) {
            this.totalPage = chatRoomPage.getTotalPages();
            this.currentPage = chatRoomPage.getNumber() + 1;
            ChatRoomListResponseDto = chatRoomListResponseDto;
        }
    }

    @Getter
    public static class ChatRoomListResponseDto {
        private final Long chatRoomId;
        private final Long toMemberId;
        private final String toMemberNickName;
        private final String toMemberProfileUrl;
        private final String lastMessage;
        private final int unreadCount;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime lastMessageTime;

        public ChatRoomListResponseDto(ChatRoom chatRoom,
                                       Member toMember,
                                       int unreadCount) {
            this.chatRoomId = chatRoom.getId();
            this.toMemberId = toMember.getId();
            this.toMemberNickName = toMember.getNickname();
            this.toMemberProfileUrl = toMember.getProfileUrl();
            this.lastMessage = chatRoom.getLastChat();
            this.unreadCount = unreadCount;
            this.lastMessageTime = chatRoom.getLastChatTime();
        }
    }
}
