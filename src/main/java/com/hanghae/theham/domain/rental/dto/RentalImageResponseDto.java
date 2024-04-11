package com.hanghae.theham.domain.rental.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RentalImageResponseDto {

    @NoArgsConstructor
    @Getter
    public static class RentalImageReadResponseDto {

        private String imageUrl;

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

        public RentalImageReadResponseDto(RentalImage rentalImage) {
            this.imageUrl = rentalImage.getImageUrl();
            this.createdAt = rentalImage.getCreatedAt();
        }
    }
}
