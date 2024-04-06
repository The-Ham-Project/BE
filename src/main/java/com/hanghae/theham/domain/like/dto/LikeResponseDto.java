package com.hanghae.theham.domain.like.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.theham.domain.like.entity.Like;
import lombok.Getter;

import java.time.LocalDateTime;

public class LikeResponseDto {

    @Getter
    public static class LikeCreateResponseDto {
        private final Long id;
        private final Long rentalId;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createAt;

        public LikeCreateResponseDto(Like like) {
            this.id = like.getId();
            this.rentalId = like.getRental().getId();
            this.createAt = like.getCreatedAt();
        }
    }

    @Getter
    public static class LikeReadResponseDto {
        private final Long id;
        private final Long rentalId;

        public LikeReadResponseDto(Like like) {
            this.id = like.getId();
            this.rentalId = like.getRental().getId();
        }
    }
}
