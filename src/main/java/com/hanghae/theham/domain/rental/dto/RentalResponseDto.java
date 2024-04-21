package com.hanghae.theham.domain.rental.dto;

import com.hanghae.theham.domain.rental.dto.RentalImageResponseDto.RentalImageReadResponseDto;
import com.hanghae.theham.domain.rental.entity.Rental;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RentalResponseDto {

    @NoArgsConstructor
    @Getter
    public static class RentalCreateResponseDto {

        private Long id;
        private String title;
        private String content;

        public RentalCreateResponseDto(Rental rental) {
            this.id = rental.getId();
            this.title = rental.getTitle();
            this.content = rental.getContent();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class RentalReadResponseDto {

        private Long rentalId;
        private String nickname;
        private String profileUrl;
        private String category;
        private String title;
        private String content;
        private long rentalFee;
        private long deposit;
        private String district;
        private Boolean isChatButton;
        private List<RentalImageReadResponseDto> rentalImageList;

        public RentalReadResponseDto(Rental rental, Boolean isChatButton, List<RentalImageReadResponseDto> rentalImageList) {
            this.rentalId = rental.getId();
            this.nickname = rental.getMember().getNickname();
            this.profileUrl = rental.getMember().getProfileUrl();
            this.category = rental.getCategory().getValue();
            this.title = rental.getTitle();
            this.content = rental.getContent();
            this.rentalFee = rental.getRentalFee();
            this.deposit = rental.getDeposit();
            this.district = rental.getDistrict();
            this.isChatButton = isChatButton;
            this.rentalImageList = rentalImageList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class RentalCategoryReadResponseDto {

        private Long rentalId;
        private String nickname;
        private String profileUrl;
        private String title;
        private String content;
        private long rentalFee;
        private long deposit;
        private String firstThumbnailUrl;
        private String district;

        public RentalCategoryReadResponseDto(Rental rental, String firstThumbnailUrl) {
            this.rentalId = rental.getId();
            this.nickname = rental.getMember().getNickname();
            this.profileUrl = rental.getMember().getProfileUrl();
            this.title = rental.getTitle();
            this.content = rental.getContent();
            this.rentalFee = rental.getRentalFee();
            this.deposit = rental.getDeposit();
            this.firstThumbnailUrl = firstThumbnailUrl;
            this.district = rental.getDistrict();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class RentalMyReadResponseDto {

        private Long rentalId;
        private String profileUrl;
        private String title;
        private String content;
        private long rentalFee;
        private long deposit;
        private String firstThumbnailUrl;

        public RentalMyReadResponseDto(Rental rental, String firstThumbnailUrl) {
            this.rentalId = rental.getId();
            this.profileUrl = rental.getMember().getProfileUrl();
            this.title = rental.getTitle();
            this.content = rental.getContent();
            this.rentalFee = rental.getRentalFee();
            this.deposit = rental.getDeposit();
            this.firstThumbnailUrl = firstThumbnailUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class RentalUpdateResponseDto {

        private String category;
        private String title;
        private String content;
        private long rentalFee;
        private long deposit;

        public RentalUpdateResponseDto(Rental rental) {
            this.category = rental.getCategory().getValue();
            this.title = rental.getTitle();
            this.content = rental.getContent();
            this.rentalFee = rental.getRentalFee();
            this.deposit = rental.getDeposit();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class RentalSearchResponseListDto {
        private Long count;
        private List<RentalSearchResponseDto> searchResponseList;

        public RentalSearchResponseListDto(Long count, List<RentalSearchResponseDto> searchResponseList) {
            this.count = count;
            this.searchResponseList = searchResponseList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class RentalSearchResponseDto {

        private Long rentalId;
        private String nickname;
        private String profileUrl;
        private String category;
        private String title;
        private String content;
        private long rentalFee;
        private long deposit;
        private String district;
        private String firstThumbnailUrl;

        public RentalSearchResponseDto(Rental rental, String firstThumbnailUrl) {
            this.rentalId = rental.getId();
            this.nickname = rental.getMember().getNickname();
            this.profileUrl = rental.getMember().getProfileUrl();
            this.category = rental.getCategory().getValue();
            this.title = rental.getTitle();
            this.content = rental.getContent();
            this.rentalFee = rental.getRentalFee();
            this.deposit = rental.getDeposit();
            this.district = rental.getDistrict();
            this.firstThumbnailUrl = firstThumbnailUrl;
        }
    }
}
