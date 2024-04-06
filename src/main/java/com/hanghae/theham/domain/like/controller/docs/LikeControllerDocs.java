package com.hanghae.theham.domain.like.controller.docs;

import com.hanghae.theham.domain.like.dto.LikeResponseDto.*;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "likes", description = "좋아요 관련 API")
public interface LikeControllerDocs {

    @Operation(summary = "함께쓰기 좋아요 등록 기능", description = "함께쓰기 게시글에 좋아요를 등록할 수 있는 API")
    ResponseDto<LikeCreateResponseDto> createLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    );

    @Operation(summary = "함께쓰기 좋아요 취소 기능", description = "함께쓰기 게시글에 좋아요를 취소할 수 있는 API")
    void deleteLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    );

    @Operation(summary = "함께쓰기 좋아요 조회 기능", description = "함께쓰기 게시글에 좋아요를 조회할 수 있는 API")
    ResponseDto<Slice<LikeReadResponseDto>> readLikeList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    );
}
