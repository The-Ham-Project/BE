package com.hanghae.theham.domain.rental.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.theham.domain.rental.dto.RentalImageResponseDto.RentalImageReadResponseDto;
import com.hanghae.theham.domain.rental.entity.Rental;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class RentalResponseDto {

    @Getter
    public static class RentalCreateResponseDto {

        private final Long id;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createdAt;

        public RentalCreateResponseDto(Rental rental) {
            this.id = rental.getId();
            this.createdAt = rental.getCreatedAt();
        }
    }

    @Getter
    public static class RentalReadResponseDto {

        private final String nickname;
        private final String profileUrl;
        private final String category;
        private final String title;
        private final String content;
        private final long rentalFee;
        private final long deposit;
        private final List<RentalImageReadResponseDto> rentalImageList;

        public RentalReadResponseDto(Rental rental, List<RentalImageReadResponseDto> rentalImageList) {
            this.nickname = rental.getMember().getNickname();
            this.profileUrl = rental.getMember().getProfileUrl();
            this.category = rental.getCategory().getValue();
            this.title = rental.getTitle();
            this.content = rental.getContent();
            this.rentalFee = rental.getRentalFee();
            this.deposit = rental.getDeposit();
            this.rentalImageList = rentalImageList;
        }
    }
}
