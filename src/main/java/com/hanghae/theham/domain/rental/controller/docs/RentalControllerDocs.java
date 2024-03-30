package com.hanghae.theham.domain.rental.controller.docs;

import com.hanghae.theham.domain.rental.dto.RentalRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.CreateRentalResponseDto;
import com.hanghae.theham.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "rentals", description = "함께쓰기 관련 API")
public interface RentalControllerDocs {

    @Operation(summary = "함께쓰기 게시글 등록 기능", description = "함께쓰기 게시글을 등록할 수 있는 API")
    ResponseDto<CreateRentalResponseDto> createRental(
            @RequestPart @Valid RentalRequestDto.RentalCreateRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> multipartFileList
    );
}
