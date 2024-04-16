package com.hanghae.theham.domain.rental.controller;

import com.hanghae.theham.domain.rental.controller.docs.RentalControllerDocs;
import com.hanghae.theham.domain.rental.dto.RentalRequestDto.RentalCreateRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalRequestDto.RentalUpdateRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.*;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import com.hanghae.theham.domain.rental.service.RentalSearchService;
import com.hanghae.theham.domain.rental.service.RentalService;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class RentalController implements RentalControllerDocs {

    private final RentalService rentalService;
    private final RentalSearchService rentalSearchService;

    public RentalController(RentalService rentalService, RentalSearchService rentalSearchService) {
        this.rentalService = rentalService;
        this.rentalSearchService = rentalSearchService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/rentals", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<RentalCreateResponseDto> createRental(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart @Valid RentalCreateRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> multipartFileList
    ) {
        RentalCreateResponseDto responseDto = rentalService.createRental(userDetails.getUsername(), requestDto, multipartFileList);
        return ResponseDto.success("함께쓰기 게시글 작성 기능", responseDto);
    }

    @GetMapping("/rentals/{rentalId}")
    public ResponseDto<RentalReadResponseDto> readRental(
            @AuthenticationPrincipal @Nullable UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        RentalReadResponseDto responseDto = rentalService.readRental(email, rentalId);
        return ResponseDto.success("함께쓰기 게시글 조회 기능", responseDto);
    }

    @GetMapping("/rentals")
    public ResponseDto<List<RentalCategoryReadResponseDto>> readRentalList(
            @AuthenticationPrincipal @Nullable UserDetailsImpl userDetails,
            @RequestParam CategoryType category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6", required = false) int size
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        List<RentalCategoryReadResponseDto> responseDtoList = rentalService.readRentalList(email, category, page, size);
        return ResponseDto.success("함께쓰기 카테고리별 게시글 조회 기능", responseDtoList);
    }

    @GetMapping("/rentals/my/posts")
    public ResponseDto<List<RentalMyReadResponseDto>> readRentalMyList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6", required = false) int size
    ) {
        List<RentalMyReadResponseDto> responseDtoList = rentalService.readRentalMyList(userDetails.getUsername(), page, size);
        return ResponseDto.success("함께쓰기 마이페이지 내가 쓴 게시글 조회 기능", responseDtoList);
    }

    @PutMapping(value = "/rentals/{rentalId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<RentalUpdateResponseDto> updateRental(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId,
            @RequestPart @Valid RentalUpdateRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> multipartFileList
    ) {
        RentalUpdateResponseDto responseDto = rentalService.updateRental(userDetails.getUsername(), rentalId, requestDto, multipartFileList);
        return ResponseDto.success("함께쓰기 게시글 수정 기능", responseDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/rentals/{rentalId}")
    public void deleteRental(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    ) {
        rentalService.deleteRental(userDetails.getUsername(), rentalId);
    }

    @GetMapping("/rentals/search")
    public ResponseDto<RentalSearchResponseListDto> searchRental(
            @AuthenticationPrincipal @Nullable UserDetailsImpl userDetails,
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "6", required = false) int size
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        RentalSearchResponseListDto responseListDto = rentalSearchService.searchRentalList(email, keyword, page, size);

        return ResponseDto.success("함께쓰기 검색 기능", responseListDto);
    }
}
