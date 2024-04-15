package com.hanghae.theham.domain.rental.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hanghae.theham.domain.rental.dto.RentalImageResponseDto.RentalImageReadResponseDto;
import com.hanghae.theham.domain.rental.entity.Rental;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class RentalResponseDto {

    @NoArgsConstructor
    @Getter
    public static class RentalCreateResponseDto {

        private Long id;
        private String title;
        private String content;

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

        public RentalCreateResponseDto(Rental rental) {
            this.id = rental.getId();
            this.title = rental.getTitle();
            this.content = rental.getContent();
            this.createdAt = rental.getCreatedAt();
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
        private double latitude;
        private double longitude;
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
            this.latitude = rental.getMember().getLatitude();
            this.longitude = rental.getMember().getLongitude();
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
        private double latitude;
        private double longitude;
        private String firstThumbnailUrl;
        private String district;

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

        public RentalCategoryReadResponseDto(Rental rental, String firstThumbnailUrl) {
            this.rentalId = rental.getId();
            this.nickname = rental.getMember().getNickname();
            this.profileUrl = rental.getMember().getProfileUrl();
            this.title = rental.getTitle();
            this.content = rental.getContent();
            this.rentalFee = rental.getRentalFee();
            this.deposit = rental.getDeposit();
            this.latitude = rental.getLatitude();
            this.longitude = rental.getLongitude();
            this.firstThumbnailUrl = firstThumbnailUrl;
            this.district = rental.getDistrict();
            this.createdAt = rental.getCreatedAt();
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
    public static class RentalSearchResponseDto {

        private Long rentalId;
        private String nickname;
        private String profileUrl;
        private String category;
        private String title;
        private String content;
        private long rentalFee;
        private long deposit;
        private double latitude;
        private double longitude;
        private String district;
        private List<RentalImageReadResponseDto> rentalImageList;

        public RentalSearchResponseDto(Rental rental, List<RentalImageReadResponseDto> rentalImageList) {
            this.rentalId = rental.getId();
            this.nickname = rental.getMember().getNickname();
            this.profileUrl = rental.getMember().getProfileUrl();
            this.category = rental.getCategory().getValue();
            this.title = rental.getTitle();
            this.content = rental.getContent();
            this.rentalFee = rental.getRentalFee();
            this.deposit = rental.getDeposit();
            this.latitude = rental.getMember().getLatitude();
            this.longitude = rental.getMember().getLongitude();
            this.district = rental.getDistrict();
            this.rentalImageList = rentalImageList;
        }
    }
}
