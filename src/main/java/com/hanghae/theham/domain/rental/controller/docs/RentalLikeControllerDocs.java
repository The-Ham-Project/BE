package com.hanghae.theham.domain.rental.controller.docs;

import com.hanghae.theham.domain.rental.dto.RentalLikeResponseDto.RentalLikeCheckResponseDto;
import com.hanghae.theham.domain.rental.dto.RentalLikeResponseDto.RentalLikeCreateResponseDto;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "rental likes", description = "함께쓰기 좋아요 관련 API")
public interface RentalLikeControllerDocs {

    @Operation(summary = "함께쓰기 좋아요 등록 기능", description = "함께쓰기 게시글에 좋아요를 등록할 수 있는 API")
    ResponseDto<RentalLikeCreateResponseDto> createRentalLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    );

    @Operation(summary = "함께쓰기 좋아요 취소 기능", description = "함께쓰기 게시글에 좋아요를 취소할 수 있는 API")
    void deleteRentalLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    );

    @Operation(summary = "함께쓰기 좋아요 조회 기능", description = "함께쓰기 게시글에 좋아요를 조회할 수 있는 API")
    ResponseDto<RentalLikeCheckResponseDto> checkRentalLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    );
}
