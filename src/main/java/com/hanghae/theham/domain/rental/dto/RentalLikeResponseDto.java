package com.hanghae.theham.domain.rental.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hanghae.theham.domain.rental.entity.RentalLike;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RentalLikeResponseDto {

    @NoArgsConstructor
    @Getter
    public static class RentalLikeCreateResponseDto {
        private Long id;
        private Long rentalId;

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

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
