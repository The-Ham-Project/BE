package com.hanghae.theham.domain.rental.controller.docs;

import com.hanghae.theham.domain.rental.dto.RentalRequestDto.RentalCreateRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalRequestDto.RentalUpdateRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.*;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "rentals", description = "함께쓰기 관련 API")
public interface RentalControllerDocs {

    @Operation(summary = "함께쓰기 게시글 등록 기능", description = "함께쓰기 게시글을 등록할 수 있는 API")
    ResponseDto<RentalCreateResponseDto> createRental(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart @Valid RentalCreateRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> multipartFileList
    );

    @Operation(summary = "함께쓰기 게시글 조회 기능", description = "함께쓰기 게시글을 조회할 수 있는 API")
    ResponseDto<RentalReadResponseDto> readRental(
            @AuthenticationPrincipal @Nullable UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    );

    @Operation(summary = "함께쓰기 카테고리별 게시글 조회 기능", description = "함께쓰기 게시글을 조회할 수 있는 API")
    ResponseDto<List<RentalCategoryReadResponseDto>> readRentalList(
            @AuthenticationPrincipal @Nullable UserDetailsImpl userDetails,
            @RequestParam CategoryType category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6", required = false) int size
    );

    @Operation(summary = "함께쓰기 마이페이지 내가 쓴 게시글 조회 기능", description = "함께쓰기 마이페이지 내가 쓴 게시글 조회할 수 있는 API")
    ResponseDto<List<RentalMyReadResponseDto>> readRentalMyList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6", required = false) int size
    );

    @Operation(summary = "함께쓰기 게시글 수정 기능", description = "함께쓰기 게시글을 수정할 수 있는 API")
    ResponseDto<RentalUpdateResponseDto> updateRental(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId,
            @RequestPart @Valid RentalUpdateRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> multipartFileList
    );

    @Operation(summary = "함께쓰기 게시글 삭제 기능", description = "함께쓰기 게시글을 삭제할 수 있는 API")
    void deleteRental(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    );


    @Operation(summary = "함께쓰기 검색 기능", description = "함께쓰기 검색 기능")
    ResponseDto<RentalSearchResponseListDto> searchRental(
            @AuthenticationPrincipal @Nullable UserDetailsImpl userDetails,
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "6", required = false) int size
    );
}
