package com.hanghae.theham.domain.rental.controller;

import com.hanghae.theham.domain.rental.controller.docs.RentalLikeControllerDocs;
import com.hanghae.theham.domain.rental.dto.RentalLikeResponseDto.RentalLikeCheckResponseDto;
import com.hanghae.theham.domain.rental.dto.RentalLikeResponseDto.RentalLikeCreateResponseDto;
import com.hanghae.theham.domain.rental.service.RentalLikeService;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/rentals")
@RestController
public class RentalRentalLikeController implements RentalLikeControllerDocs {

    private final RentalLikeService rentalLikeService;

    public RentalRentalLikeController(RentalLikeService rentalLikeService) {
        this.rentalLikeService = rentalLikeService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/likes/{rentalId}")
    public ResponseDto<RentalLikeCreateResponseDto> createRentalLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    ) {
        RentalLikeCreateResponseDto responseDto = rentalLikeService.createRentalLike(userDetails.getUsername(), rentalId);
        return ResponseDto.success("함께쓰기 찜하기 등록 기능", responseDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/likes/{rentalId}")
    public void deleteRentalLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    ) {
        rentalLikeService.deleteRentalLike(userDetails.getUsername(), rentalId);
    }

    @GetMapping("/likes/{rentalId}")
    public ResponseDto<RentalLikeCheckResponseDto> checkRentalLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long rentalId
    ) {
        RentalLikeCheckResponseDto responseDto = rentalLikeService.checkRentalLike(userDetails.getUsername(), rentalId);
        return ResponseDto.success("함께쓰기 찜하기 조회 기능", responseDto);
    }
}
