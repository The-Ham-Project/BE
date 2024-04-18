package com.hanghae.theham.domain.rental.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.rental.dto.RentalImageResponseDto.RentalImageReadResponseDto;
import com.hanghae.theham.domain.rental.dto.RentalRequestDto.RentalCreateRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalRequestDto.RentalUpdateRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.*;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import com.hanghae.theham.domain.rental.repository.RentalImageRepository;
import com.hanghae.theham.domain.rental.repository.RentalRepository;
import com.hanghae.theham.global.config.S3Config;
import com.hanghae.theham.global.exception.AwsS3Exception;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Transactional(readOnly = true)
@Service
public class RentalService {

    private static final int MAX_IMAGE_UPLOAD_COUNT = 3;
    private static final int MAX_IMAGE_UPLOAD_SIZE = 5 * 1024 * 1024; // 2MB
    private static final List<String> LIMIT_IMAGE_TYPE_LIST = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );
    private static final String S3_UPLOAD_FOLDER = "rentals/";

    private final RentalRepository rentalRepository;
    private final RentalImageRepository rentalImageRepository;
    private final MemberRepository memberRepository;
    private final S3Config s3Config;
    private final RestTemplate restTemplate;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    public RentalService(RentalRepository rentalRepository, RentalImageRepository rentalImageRepository, MemberRepository memberRepository, S3Config s3Config, RestTemplate restTemplate) {
        this.rentalRepository = rentalRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.memberRepository = memberRepository;
        this.s3Config = s3Config;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public RentalCreateResponseDto createRental(
            String email,
            RentalCreateRequestDto requestDto,
            List<MultipartFile> multipartFileList
    ) {
        // 회원 정보 검증
        Member member = validateMember(email);

        // 회원 위치 좌표 검사
        if (member.getLatitude() == 0.0 || member.getLongitude() == 0.0) {
            throw new BadRequestException(ErrorCode.INVALID_MEMBER_POSITION.getMessage());
        }
        if (requestDto.getCategory() == CategoryType.ALL) {
            throw new BadRequestException(ErrorCode.INVALID_RENTAL_CATEGORY.getMessage());
        }

        // 카카오 지도 API로 지역구 불러오기
        String district = getDistrictFromKakaoAPI(member.getLatitude(), member.getLongitude());

        // 함께쓰기 정보 저장
        Rental rental = rentalRepository.save(requestDto.toEntity(member, district));

        if (multipartFileList != null && !multipartFileList.isEmpty()) {
            // 파일 검증 로직
            validateFiles(multipartFileList);

            // 파일 업로드 및 이미지 정보 저장
            multipartFileList.forEach(file -> {
                String imageUrl = uploadFileToS3(file);
                saveRentalImage(rental, imageUrl);
            });
        }
        return new RentalCreateResponseDto(rental);
    }

    public RentalReadResponseDto readRental(String email, Long rentalId) {
        Boolean isChatButton = Boolean.TRUE;

        Rental rental = validateRental(rentalId);
        if (email != null) {
            Member member = memberRepository.findByEmail(email).orElse(null);

            if (rental.getMember().equals(member)) {
                isChatButton = Boolean.FALSE;
            }
        }

        List<RentalImageReadResponseDto> rentalImageReadResponseDtoList = rentalImageRepository.findAllByRental(rental).stream()
                .map(RentalImageReadResponseDto::new)
                .toList();

        return new RentalReadResponseDto(rental, isChatButton, rentalImageReadResponseDtoList);
    }

    public List<RentalCategoryReadResponseDto> readRentalList(String email, CategoryType category, int page, int size) {
        List<Rental> rentalList;
        List<RentalCategoryReadResponseDto> responseDtoList = new ArrayList<>();

        if (email == null) {
            PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.Direction.DESC, "createdAt");

            if (category == CategoryType.ALL) {
                rentalList = rentalRepository.findAll(pageRequest).getContent();
            } else {
                rentalList = rentalRepository.findAllByCategory(category, pageRequest).getContent();
            }
        } else {
            Member member = memberRepository.findByEmail(email).orElseThrow(() ->
                    new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage())
            );

            double latitude = member.getLatitude();
            double longitude = member.getLongitude();

            PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size);

            if (category == CategoryType.ALL) {
                rentalList = rentalRepository.findAllByDistance(latitude, longitude, pageRequest.getPageSize(), (int) pageRequest.getOffset());
            } else {
                rentalList = rentalRepository.findAllByCategoryAndDistance(category.toString(), latitude, longitude, pageRequest.getPageSize(), (int) pageRequest.getOffset());
            }
        }

        for (Rental rental : rentalList) {
            String firstThumbnailUrl = rentalImageRepository.findAllByRental(rental).stream()
                    .findFirst()
                    .map(RentalImage::getImageUrl)
                    .orElse(null);

            responseDtoList.add(new RentalCategoryReadResponseDto(rental, firstThumbnailUrl));
        }
        return responseDtoList;
    }

    public List<RentalMyReadResponseDto> readRentalMyList(String email, int page, int size) {
        Member member = validateMember(email);

        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.Direction.DESC, "createdAt");
        Page<Rental> rentalPage = rentalRepository.findByMember(member, pageRequest);

        List<RentalMyReadResponseDto> responseDtoList = new ArrayList<>();
        for (Rental rental : rentalPage) {
            String firstThumbnailUrl = rentalImageRepository.findAllByRental(rental).stream()
                    .findFirst()
                    .map(RentalImage::getImageUrl)
                    .orElse(null);

            responseDtoList.add(new RentalMyReadResponseDto(rental, firstThumbnailUrl));
        }
        return responseDtoList;
    }

    @Transactional
    public RentalUpdateResponseDto updateRental(String email, Long rentalId, RentalUpdateRequestDto requestDto, List<MultipartFile> multipartFileList) {
        Member member = validateMember(email);
        Rental rental = validateRental(rentalId);

        if (!member.equals(rental.getMember())) {
            log.error("게시글 작성자 정보가 일치하지 않습니다. 회원: {}, 작성자: {}", member, rental.getMember());
            throw new BadRequestException(ErrorCode.UNMATCHED_RENTAL_MEMBER.getMessage());
        }

        // 기존 이미지 삭제
        List<RentalImage> existingImages = rentalImageRepository.findAllByRental(rental);
        existingImages.forEach(image -> {
            deleteFileFromS3(image.getImageUrl());
            rentalImageRepository.delete(image);
        });

        // 새 이미지 파일 검증 및 업로드
        if (multipartFileList != null && !multipartFileList.isEmpty()) {
            validateFiles(multipartFileList);

            multipartFileList.forEach(file -> {
                String imageUrl = uploadFileToS3(file);
                saveRentalImage(rental, imageUrl);
            });
        }

        rental.update(
                requestDto.getTitle(),
                requestDto.getCategory(),
                requestDto.getContent(),
                requestDto.getRentalFee(),
                requestDto.getDeposit(),
                rental.getLatitude(),
                rental.getLongitude(),
                rental.getDistrict()
        );
        return new RentalUpdateResponseDto(rental);
    }

    @Transactional
    public void deleteRental(String email, Long rentalId) {
        Member member = validateMember(email);
        Rental rental = validateRental(rentalId);

        if (!member.equals(rental.getMember())) {
            log.error("게시글 작성자 정보가 일치하지 않습니다. 회원: {}, 작성자: {}", member, rental.getMember());
            throw new BadRequestException(ErrorCode.UNMATCHED_RENTAL_MEMBER.getMessage());
        }

        rentalRepository.delete(rental);
    }

    private Member validateMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });
    }

    private Rental validateRental(Long rentalId) {
        return rentalRepository.findById(rentalId).orElseThrow(() -> {
            log.error("함께쓰기 게시글 정보를 찾을 수 없습니다. ID: {}", rentalId);
            return new BadRequestException(ErrorCode.NOT_FOUND_RENTAL.getMessage());
        });
    }

    private void validateFiles(List<MultipartFile> files) {
        if (files.size() > MAX_IMAGE_UPLOAD_COUNT) {
            throw new BadRequestException(ErrorCode.MAXIMUM_RENTAL_FILE_UPLOAD.getMessage());
        }
        for (MultipartFile file : files) {
            if (!LIMIT_IMAGE_TYPE_LIST.contains(file.getContentType())) {
                throw new BadRequestException(ErrorCode.UNSUPPORTED_FILE_TYPE.getMessage());
            }
            if (file.getSize() > MAX_IMAGE_UPLOAD_SIZE) {
                throw new BadRequestException(ErrorCode.FILE_SIZE_EXCEEDED.getMessage());
            }
        }
    }

    private String uploadFileToS3(MultipartFile file) {
        try {
            // 파일 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // 새로운 파일명 생성 (UUID 사용)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String newFilename = UUID.randomUUID() + extension;
            String key = S3_UPLOAD_FOLDER + newFilename;

            // S3 업로드 요청 생성
            PutObjectRequest request = new PutObjectRequest(bucket, key, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            // S3에 파일 업로드
            s3Config.amazonS3Client().putObject(request);

            // 업로드된 파일의 URL 생성 및 반환
            return s3Config.amazonS3Client().getUrl(bucket, key).toString();
        } catch (IOException e) {
            log.error("파일 업로드 중 오류가 발생했습니다.", e);
            throw new AwsS3Exception(ErrorCode.S3_UPLOAD_UNKNOWN_ERROR.getMessage());
        }
    }

    private void saveRentalImage(Rental rental, String imageUrl) {
        RentalImage rentalImage = RentalImage.builder()
                .rental(rental)
                .imageUrl(imageUrl)
                .build();

        rentalImageRepository.save(rentalImage);
    }

    private void deleteFileFromS3(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String key = url.getPath().substring(1); // URL에서 객체 키 추출
            s3Config.amazonS3Client().deleteObject(new DeleteObjectRequest(bucket, key));
        } catch (MalformedURLException e) {
            log.error("S3에서 파일을 삭제하는 도중 오류가 발생했습니다.", e);
            throw new AwsS3Exception(ErrorCode.S3_DELETE_UNKNOWN_ERROR.getMessage());
        }
    }

    private String getDistrictFromKakaoAPI(double latitude, double longitude) {
        String district;
        try {
            URI uri = UriComponentsBuilder
                    .fromUriString("https://dapi.kakao.com")
                    .path("/v2/local/geo/coord2address.json")
                    .queryParam("x", longitude)
                    .queryParam("y", latitude)
                    .queryParam("input_coord", "WGS84")
                    .encode()
                    .build()
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK " + kakaoClientId);

            RequestEntity<Void> requestEntity = RequestEntity
                    .get(uri)
                    .headers(headers)
                    .build();

            ResponseEntity<String> response = restTemplate.exchange(
                    requestEntity,
                    String.class
            );

            JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
            district = jsonNode.path("documents")
                    .path(0)
                    .path("address")
                    .path("region_2depth_name")
                    .asText();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return district;
    }
}