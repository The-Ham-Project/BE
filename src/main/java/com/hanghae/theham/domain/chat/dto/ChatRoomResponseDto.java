package com.hanghae.theham.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.theham.domain.chat.dto.ChatResponseDto.ChatReadResponseDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomResponseDto {

    @Getter
    public static class ChatRoomDetailResponseDto {
        private final int totalPage;
        private final int currentPage;
        private final String toUserNickname; // 상대방 식별
        private final String toUserProfileImage; // 상대방 이미지
        private final String senderProfileImage; // sender 이미지
        private final List<ChatReadResponseDto> chatReadResponseDtoList;

        public ChatRoomDetailResponseDto(int totalPage,
                                         int currentPage,
                                         String toUserNickname,
                                         String toUserProfileImage,
                                         String senderProfileImage,
                                         List<ChatReadResponseDto> chatReadResponseDtoList) {
            this.totalPage = totalPage;
            this.currentPage = currentPage;
            this.toUserNickname = toUserNickname;
            this.toUserProfileImage = toUserProfileImage;
            this.senderProfileImage = senderProfileImage;
            this.chatReadResponseDtoList = chatReadResponseDtoList;
        }
    }

    @Getter
    public static class ChatRoomReadResponseDto {
        private final int totalPage;
        private final int currentPage;
        private final List<ChatRoomListResponseDto> ChatRoomListResponseDto;

        public ChatRoomReadResponseDto(int totalPage, int currentPage, List<ChatRoomListResponseDto> chatRoomListResponseDto) {
            this.totalPage = totalPage;
            this.currentPage = currentPage;
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

        public ChatRoomListResponseDto(Long chatRoomId,
                                       Long toMemberId,
                                       String toMemberNickName,
                                       String toMemberProfileUrl,
                                       String lastMessage,
                                       int unreadCount,
                                       LocalDateTime modifiedAt) {
            this.chatRoomId = chatRoomId;
            this.toMemberId = toMemberId;
            this.toMemberNickName = toMemberNickName;
            this.toMemberProfileUrl = toMemberProfileUrl;
            this.lastMessage = lastMessage;
            this.unreadCount = unreadCount;
            this.lastMessageTime = modifiedAt;
        }
    }
}
