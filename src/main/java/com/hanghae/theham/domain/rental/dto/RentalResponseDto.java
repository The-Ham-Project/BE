package com.hanghae.theham.domain.rental.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.theham.domain.rental.entity.Rental;
import lombok.Getter;

import java.time.LocalDateTime;

public class RentalResponseDto {

    @Getter
    public static class CreateRentalResponseDto {

        private final Long id;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createdAt;

        public CreateRentalResponseDto(Rental rental) {
            this.id = rental.getId();
            this.createdAt = rental.getCreatedAt();
        }
    }
}
