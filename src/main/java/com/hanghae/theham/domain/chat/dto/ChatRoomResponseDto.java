package com.hanghae.theham.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomResponseDto {

    @Getter
    public static class ChatRoomCreateResponseDto {
        private final Long id;
        private String seller;
        private String buyer;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createdAt;

        public ChatRoomCreateResponseDto(ChatRoom chatRoom) {
            this.id = chatRoom.getId();
            this.seller = chatRoom.getSeller().getNickname();
            this.buyer = chatRoom.getBuyer().getNickname();
            this.createdAt = chatRoom.getCreatedAt();
        }
    }

    @Getter
    public static class ChatRoomDetailResponseDto{
        private final String toUserNickname; // 상대방 식별
        private final String toUserProfileImage; // 상대방 이미지
        private final List<ChatResponseDto.ChatReadResponseDto> chatReadResponseDtoList;

        public ChatRoomDetailResponseDto(String toUserNickname,
                                         String toUserProfileImage,
                                         List<ChatResponseDto.ChatReadResponseDto> chatReadResponseDtoList) {
            this.toUserNickname = toUserNickname;
            this.toUserProfileImage = toUserProfileImage;
            this.chatReadResponseDtoList = chatReadResponseDtoList;
        }
    }

    @Getter
    public static class ChatRoomReadResponseDto{
        private final Long chatRoomId;
        private final Long toMemberId;
        private final String toMemberNickName;
        private final String toMemberProfileUrl;
        private final String lastMessage;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime lastMessageTime;


        public ChatRoomReadResponseDto(Long chatRoomId,
                                       Long toMemberId,
                                       String toMemberNickName,
                                       String toMemberProfileUrl,
                                       String lastMessage,
                                       LocalDateTime modifiedAt) {
            this.chatRoomId = chatRoomId;
            this.toMemberId = toMemberId;
            this.toMemberNickName = toMemberNickName;
            this.toMemberProfileUrl = toMemberProfileUrl;
            this.lastMessage = lastMessage;
            this.lastMessageTime = modifiedAt;
        }
    }
}
