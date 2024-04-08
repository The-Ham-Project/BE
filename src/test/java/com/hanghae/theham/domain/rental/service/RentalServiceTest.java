package com.hanghae.theham.domain.rental.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.rental.dto.RentalRequestDto.RentalCreateRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalRequestDto.RentalUpdateRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.RentalCreateResponseDto;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.RentalUpdateResponseDto;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import com.hanghae.theham.domain.rental.repository.RentalImageRepository;
import com.hanghae.theham.domain.rental.repository.RentalRepository;
import com.hanghae.theham.global.config.S3Config;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @InjectMocks
    private RentalService rentalService;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private RentalImageRepository rentalImageRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3Config s3Config;

    @Mock
    private RestTemplate restTemplate;

    @DisplayName("성공 - 함께쓰기 게시글 등록")
    @Test
    void createRental_01() {
        // given
        Member member = Member.builder()
                .email("test@test.com")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        Rental rental = Rental.builder()
                .title("제목")
                .category(CategoryType.BOOK)
                .content("내용")
                .rentalFee(1000L)
                .deposit(2000L)
                .build();

        RentalCreateRequestDto requestDto =
                new RentalCreateRequestDto("제목", CategoryType.BOOK, "내용", 1000L, 2000L);

        List<MultipartFile> multipartFileList = new ArrayList<>();

        String kakaoApiResponse = "{\"documents\": [{\"address\": {\"region_2depth_name\": \"종로구\"}}]}";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(kakaoApiResponse, HttpStatus.OK);

        // when
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        when(restTemplate.exchange(
                ArgumentMatchers.<RequestEntity<Void>>any(),
                ArgumentMatchers.eq(String.class))
        ).thenReturn(mockResponse);

        when(rentalRepository.save(any())).thenReturn(rental);

        RentalCreateResponseDto responseDto = rentalService.createRental(member.getEmail(), requestDto, multipartFileList);

        // then

        assertEquals("제목", responseDto.getTitle());
        assertEquals("내용", responseDto.getContent());
    }

    @DisplayName("성공 - 함께쓰기 게시글 등록, 이미지 두장")
    @Test
    void createRental_02() throws Exception {
        // given
        Member member = Member.builder()
                .email("test@test.com")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        Rental rental = Rental.builder()
                .title("제목")
                .category(CategoryType.BOOK)
                .content("내용")
                .rentalFee(1000L)
                .deposit(2000L)
                .build();

        RentalCreateRequestDto requestDto =
                new RentalCreateRequestDto("제목", CategoryType.BOOK, "내용", 1000L, 2000L);

        MultipartFile mockFile1 = mock(MultipartFile.class);
        MultipartFile mockFile2 = mock(MultipartFile.class);
        List<MultipartFile> multipartFileList = Arrays.asList(mockFile1, mockFile2);

        String kakaoApiResponse = "{\"documents\": [{\"address\": {\"region_2depth_name\": \"종로구\"}}]}";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(kakaoApiResponse, HttpStatus.OK);

        // when
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        when(restTemplate.exchange(
                ArgumentMatchers.<RequestEntity<Void>>any(),
                ArgumentMatchers.eq(String.class))
        ).thenReturn(mockResponse);

        when(rentalRepository.save(any())).thenReturn(rental);
        when(mockFile1.getContentType()).thenReturn("image/jpeg");
        when(mockFile2.getContentType()).thenReturn("image/jpeg");

        AmazonS3Client mockS3Client = mock(AmazonS3Client.class);
        when(s3Config.amazonS3Client()).thenReturn(mockS3Client);
        when(mockS3Client.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());
        when(mockS3Client.getUrl(nullable(String.class), anyString())).thenReturn(new URI("http://example.com/image.jpg").toURL());

        RentalCreateResponseDto responseDto = rentalService.createRental(member.getEmail(), requestDto, multipartFileList);

        // then

        assertEquals("제목", responseDto.getTitle());
        assertEquals("내용", responseDto.getContent());
        verify(mockS3Client, times(2)).putObject(any(PutObjectRequest.class));
    }

    @DisplayName("실패 - 함께쓰기 게시글 등록, 회원 좌표 값이 올바르지 않음")
    @Test
    void createRental_03() {
        // given
        Member member = Member.builder()
                .email("test@test.com")
                .latitude(0)
                .longitude(0)
                .build();

        // when & then
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                rentalService.createRental(member.getEmail(), any(RentalCreateRequestDto.class), null)
        );

        assertEquals(ErrorCode.INVALID_MEMBER_POSITION.getMessage(), exception.getMessage());
    }

    @DisplayName("실패 - 함께쓰기 게시글 등록, 허용되지 않은 이미지 유형")
    @Test
    void createRental_04() {
        // given
        Member member = Member.builder()
                .email("test@test.com")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        Rental rental = Rental.builder()
                .title("제목")
                .category(CategoryType.BOOK)
                .content("내용")
                .rentalFee(1000L)
                .deposit(2000L)
                .build();

        RentalCreateRequestDto requestDto =
                new RentalCreateRequestDto("제목", CategoryType.BOOK, "내용", 1000L, 2000L);

        MultipartFile mockFile1 = mock(MultipartFile.class);
        MultipartFile mockFile2 = mock(MultipartFile.class);
        List<MultipartFile> multipartFileList = Arrays.asList(mockFile1, mockFile2);

        String kakaoApiResponse = "{\"documents\": [{\"address\": {\"region_2depth_name\": \"종로구\"}}]}";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(kakaoApiResponse, HttpStatus.OK);

        // when
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        when(restTemplate.exchange(
                ArgumentMatchers.<RequestEntity<Void>>any(),
                ArgumentMatchers.eq(String.class))
        ).thenReturn(mockResponse);

        when(rentalRepository.save(any())).thenReturn(rental);
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        // then
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                rentalService.createRental(member.getEmail(), requestDto, multipartFileList)
        );

        assertEquals(ErrorCode.UNSUPPORTED_FILE_TYPE.getMessage(), exception.getMessage());
    }

    @DisplayName("성공 - 함께쓰기 게시글 수정")
    @Test
    void updateRental_01() {
        // given
        Member member = Member.builder()
                .email("test@test.com")
                .build();

        Rental rental = Rental.builder()
                .title("제목")
                .category(CategoryType.BOOK)
                .content("내용")
                .rentalFee(1000L)
                .deposit(2000L)
                .member(member)
                .build();

        RentalUpdateRequestDto requestDto =
                new RentalUpdateRequestDto("제목2", CategoryType.CLOSET, "내용2", 10000L, 20000L);

        // when
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        when(rentalRepository.findById(any())).thenReturn(Optional.of(rental));

        RentalUpdateResponseDto responseDto = rentalService.updateRental(member.getEmail(), rental.getId(), requestDto, null);

        // then

        assertEquals("제목2", responseDto.getTitle());
        assertEquals(CategoryType.CLOSET.getValue(), responseDto.getCategory());
        assertEquals("내용2", responseDto.getContent());
        assertEquals(10000L, responseDto.getRentalFee());
        assertEquals(20000L, responseDto.getDeposit());
    }

    @DisplayName("실패 - 함께쓰기 게시글 수정, 작성자 정보가 일치하지 않음")
    @Test
    void updateRental_02() {
        // given
        Member member1 = Member.builder()
                .email("test@test.com")
                .build();

        Member member2 = Member.builder()
                .email("test2@test.com")
                .build();

        Rental rental = Rental.builder()
                .title("제목")
                .category(CategoryType.BOOK)
                .content("내용")
                .rentalFee(1000L)
                .deposit(2000L)
                .member(member2)
                .build();

        RentalUpdateRequestDto requestDto =
                new RentalUpdateRequestDto("제목2", CategoryType.CLOSET, "내용2", 10000L, 20000L);

        // when
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member1));
        when(rentalRepository.findById(any())).thenReturn(Optional.of(rental));

        // then
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                rentalService.updateRental(member1.getEmail(), rental.getId(), requestDto, null)
        );

        assertEquals(ErrorCode.UNMATCHED_RENTAL_MEMBER.getMessage(), exception.getMessage());
    }

    @DisplayName("성공 - 함께쓰기 게시글 삭제")
    @Test
    void deleteRental_01() {
        // given
        Member member = Member.builder()
                .email("test@test.com")
                .build();

        Rental rental = Rental.builder()
                .title("제목")
                .category(CategoryType.BOOK)
                .content("내용")
                .rentalFee(1000L)
                .deposit(2000L)
                .member(member)
                .build();

        // when
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        when(rentalRepository.findById(any())).thenReturn(Optional.of(rental));

        rentalService.deleteRental(member.getEmail(), rental.getId());

        // then
        verify(rentalRepository).delete(rental);
    }

    @DisplayName("실패 - 함께쓰기 게시글 삭제, 작성자 정보가 일치하지 않음")
    @Test
    void deleteRental_02() {
        // given
        Member member1 = Member.builder()
                .email("test@test.com")
                .build();

        Member member2 = Member.builder()
                .email("test2@test.com")
                .build();

        Rental rental = Rental.builder()
                .title("제목")
                .category(CategoryType.BOOK)
                .content("내용")
                .rentalFee(1000L)
                .deposit(2000L)
                .member(member2)
                .build();

        // when
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member1));
        when(rentalRepository.findById(any())).thenReturn(Optional.of(rental));

        // then
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                rentalService.deleteRental(member1.getEmail(), rental.getId())
        );

        assertEquals(ErrorCode.UNMATCHED_RENTAL_MEMBER.getMessage(), exception.getMessage());
    }
}