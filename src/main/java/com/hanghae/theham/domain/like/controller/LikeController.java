package com.hanghae.theham.domain.like.controller;

import com.hanghae.theham.domain.like.controller.docs.LikeControllerDocs;
import com.hanghae.theham.domain.like.dto.LikeResponseDto.LikeCreateResponseDto;
import com.hanghae.theham.domain.like.dto.LikeResponseDto.LikeReadResponseDto;
import com.hanghae.theham.domain.like.service.LikeService;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/likes")
@RestController
public class LikeController implements LikeControllerDocs {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{rentalId}")
    public ResponseDto<LikeCreateResponseDto> createLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    ) {
        LikeCreateResponseDto responseDto = likeService.createLike(userDetails.getUsername(), rentalId);
        return ResponseDto.success("함께쓰기 찜하기 등록 기능", responseDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{rentalId}")
    public void deleteLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    ) {
        likeService.deleteLike(userDetails.getUsername(), rentalId);
    }

    @GetMapping
    public ResponseDto<Slice<LikeReadResponseDto>> readLikeList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Slice<LikeReadResponseDto> readResponseDtoList =
                likeService.readLikeList(userDetails.getUsername(), page - 1, size);
        return ResponseDto.success("함께쓰기 찜하기 조회 기능", readResponseDtoList);
    }
}
