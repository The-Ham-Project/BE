package com.hanghae.theham.domain.rental.dto;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

public class RentalRequestDto {

    @Getter
    public static class RentalCreateRequestDto {

        @Schema(description = "제목", example = "스프링부트 책 필요하신분~")
        @NotBlank(message = "제목을 입력해주세요")
        private String title;

        @Schema(description = "카테고리", example = "HOUSEHOLD, KITCHEN, CLOSET, ELECTRONIC, BOOK, PLACE, OTHER")
        private CategoryType category;

        @Schema(description = "내용", example = "필요하신분 무료로 나눔해드립니다.")
        @NotBlank(message = "내용을 입력해주세요.")
        private String content;

        @Schema(description = "대여비", example = "0")
        @PositiveOrZero(message = "금액 범위를 다시 한번 확인해주세요.")
        private Long rentalFee;

        @Schema(description = "보증금", example = "0")
        @PositiveOrZero(message = "금액 범위를 다시 한번 확인해주세요.")
        private Long deposit;

        public Rental toEntity(Member member, String district) {
            return Rental.builder()
                    .title(this.title)
                    .category(this.category)
                    .content(this.content)
                    .rentalFee(this.rentalFee)
                    .deposit(this.deposit)
                    .latitude(member.getLatitude())
                    .longitude(member.getLongitude())
                    .district(district)
                    .member(member)
                    .build();
        }
    }

    @Getter
    public static class RentalUpdateRequestDto {

        @Schema(description = "제목", example = "스프링부트 책 필요하신분~")
        @NotBlank(message = "제목을 입력해주세요")
        private String title;

        @Schema(description = "카테고리", example = "ELECTRONIC, HOUSEHOLD, KITCHEN, CLOSET, BOOK, COSMETICS, PLACE, OTHER")
        private CategoryType category;

        @Schema(description = "내용", example = "필요하신분 무료로 나눔해드립니다.")
        @NotBlank(message = "내용을 입력해주세요.")
        private String content;

        @Schema(description = "대여비", example = "0")
        @PositiveOrZero(message = "금액 범위를 다시 한번 확인해주세요.")
        private Long rentalFee;

        @Schema(description = "보증금", example = "0")
        @PositiveOrZero(message = "금액 범위를 다시 한번 확인해주세요.")
        private Long deposit;
    }
}
