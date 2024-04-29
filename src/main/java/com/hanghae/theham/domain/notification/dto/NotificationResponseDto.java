package com.hanghae.theham.domain.notification.dto;

import lombok.Getter;

public class NotificationResponseDto {
    @Getter
    public static class NotificationCreateResponseDto {
        private final int totalUnreadCount;

        public NotificationCreateResponseDto(int totalUnreadCount) {
            this.totalUnreadCount = totalUnreadCount;
        }
    }
}