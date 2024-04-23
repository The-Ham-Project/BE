package com.hanghae.theham.domain.rental.dto;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import com.hanghae.theham.global.util.BadWordFilteringUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.List;

public class RentalRequestDto {

    @AllArgsConstructor
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
        @Max(value = 999999, message = "금액은 1백만원 미만이어야 합니다.")
        private Long rentalFee;

        @Schema(description = "보증금", example = "0")
        @PositiveOrZero(message = "금액 범위를 다시 한번 확인해주세요.")
        @Max(value = 999999, message = "금액은 1백만원 미만이어야 합니다.")
        private Long deposit;

        public Rental toEntity(Member member, String district) {
            // 비속어
            String badWordFilterTitle = BadWordFilteringUtil.change(this.title);
            String badWordFilterContent = BadWordFilteringUtil.change(this.content);

            // 위치
            GeometryFactory geometryFactory = new GeometryFactory();
            Point location = geometryFactory.createPoint(new Coordinate(member.getLongitude(), member.getLatitude()));
            location.setSRID(4326);

            return Rental.builder()
                    .title(badWordFilterTitle)
                    .category(this.category)
                    .content(badWordFilterContent)
                    .rentalFee(this.rentalFee)
                    .deposit(this.deposit)
                    .location(location)
                    .district(district)
                    .member(member)
                    .build();
        }
    }

    @AllArgsConstructor
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
        @Max(value = 999999, message = "금액은 1백만원 미만이어야 합니다.")
        private Long rentalFee;

        @Schema(description = "보증금", example = "0")
        @PositiveOrZero(message = "금액 범위를 다시 한번 확인해주세요.")
        @Max(value = 999999, message = "금액은 1백만원 미만이어야 합니다.")
        private Long deposit;

        @Schema(description = "이전 이미지 URL")
        private List<String> beforeImageUrlList;
    }
}
