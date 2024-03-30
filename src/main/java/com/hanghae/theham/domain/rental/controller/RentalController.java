package com.hanghae.theham.domain.rental.controller;

import com.hanghae.theham.domain.rental.controller.docs.RentalControllerDocs;
import com.hanghae.theham.domain.rental.dto.RentalRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.CreateRentalResponseDto;
import com.hanghae.theham.domain.rental.service.RentalService;
import com.hanghae.theham.global.dto.ResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class RentalController implements RentalControllerDocs {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping(value = "/rentals", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<CreateRentalResponseDto> createRental(
            @RequestPart @Valid RentalRequestDto.RentalCreateRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> multipartFileList
    ) {
        CreateRentalResponseDto responseDto = rentalService.createRental(requestDto, multipartFileList);
        return ResponseDto.success("함께쓰기 게시글 작성 기능", responseDto);
    }
}
