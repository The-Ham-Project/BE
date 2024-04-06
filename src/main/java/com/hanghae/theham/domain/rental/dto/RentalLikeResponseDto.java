package com.hanghae.theham.domain.rental.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.theham.domain.rental.entity.RentalLike;
import lombok.Getter;

import java.time.LocalDateTime;

public class RentalLikeResponseDto {

    @Getter
    public static class RentalLikeCreateResponseDto {
        private final Long id;
        private final Long rentalId;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createdAt;

        public RentalLikeCreateResponseDto(RentalLike rentalLike) {
            this.id = rentalLike.getId();
            this.rentalId = rentalLike.getRental().getId();
            this.createdAt = rentalLike.getCreatedAt();
        }
    }

    @Getter
    public static class RentalLikeCheckResponseDto {

        private final Boolean isChecked;

        public RentalLikeCheckResponseDto(Boolean isChecked) {
            this.isChecked = isChecked;
        }
    }
}
