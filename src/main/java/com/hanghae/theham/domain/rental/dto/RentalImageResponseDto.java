package com.hanghae.theham.domain.rental.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import lombok.Getter;

import java.time.LocalDateTime;

public class RentalImageResponseDto {

    @Getter
    public static class RentalImageReadResponseDto {

        private final String imageUrl;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createdAt;

        public RentalImageReadResponseDto(RentalImage rentalImage) {
            this.imageUrl = rentalImage.getImageUrl();
            this.createdAt = rentalImage.getCreatedAt();
        }
    }
}
