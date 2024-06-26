package com.hanghae.theham.domain.rental.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.theham.domain.chat.repository.ChatRoomRepository;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.rental.dto.RentalImageResponseDto.RentalImageReadResponseDto;
import com.hanghae.theham.domain.rental.dto.RentalRequestDto.RentalCreateRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalRequestDto.RentalUpdateRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.*;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import com.hanghae.theham.domain.rental.repository.*;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import com.hanghae.theham.global.service.S3Service;
import com.hanghae.theham.global.util.BadWordFilteringUtil;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Transactional(readOnly = true)
@Service
public class RentalService {

    private static final int MAX_IMAGE_UPLOAD_COUNT = 3;
    private static final int MAX_IMAGE_UPLOAD_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> LIMIT_IMAGE_TYPE_LIST = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    private static final String S3_UPLOAD_FOLDER = "rentals/";
    private static final double DISTANCE_RANGE = 4000.0;

    private final RentalRepository rentalRepository;
    private final RentalDistanceRepository rentalDistanceRepository;
    private final RentalImageRepository rentalImageRepository;
    private final RentalImageThumbnailRepository rentalImageThumbnailRepository;
    private final RentalLikeRepository rentalLikeRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RestTemplate restTemplate;
    private final RentalCachingService rentalCachingService;
    private final S3Service s3Service;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    public RentalService(RentalRepository rentalRepository, RentalDistanceRepository rentalDistanceRepository, RentalImageRepository rentalImageRepository, RentalImageThumbnailRepository rentalImageThumbnailRepository, RentalLikeRepository rentalLikeRepository, MemberRepository memberRepository, ChatRoomRepository chatRoomRepository, RestTemplate restTemplate, RentalCachingService rentalCachingService, S3Service s3Service) {
        this.rentalRepository = rentalRepository;
        this.rentalDistanceRepository = rentalDistanceRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.rentalImageThumbnailRepository = rentalImageThumbnailRepository;
        this.rentalLikeRepository = rentalLikeRepository;
        this.memberRepository = memberRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.restTemplate = restTemplate;
        this.rentalCachingService = rentalCachingService;
        this.s3Service = s3Service;
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
                String imageUrl = s3Service.uploadFileToS3(S3_UPLOAD_FOLDER, file);
                saveRentalImage(rental, imageUrl);
            });
        }
        return new RentalCreateResponseDto(rental);
    }

    @Cacheable(value = "Rentals", key = "{#rentalId, #email != null ? #email : 'noEmail'}", cacheManager = "redisCacheManager")
    public RentalReadResponseDto readRental(String email, Long rentalId) {
        Boolean isChatButton = Boolean.TRUE;
        Boolean isLike = Boolean.FALSE;

        Rental rental = validateRental(rentalId);
        if (email != null) {
            Member member = memberRepository.findByEmail(email).orElse(null);

            if (rental.getMember().equals(member)) {
                isChatButton = Boolean.FALSE;
            }
            if (rentalLikeRepository.existsByMemberAndRental(member, rental)) {
                isLike = Boolean.TRUE;
            }
        }

        List<RentalImageReadResponseDto> rentalImageReadResponseDtoList = rentalImageRepository.findAllByRental(rental).stream()
                .map(RentalImageReadResponseDto::new)
                .toList();

        return new RentalReadResponseDto(rental, isChatButton, isLike, rentalImageReadResponseDtoList);
    }

    public List<RentalCategoryReadResponseDto> readRentalList(String email, CategoryType category, int page, int size) {
        List<Rental> rentalList;
        List<RentalCategoryReadResponseDto> responseDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.Direction.DESC, "createdAt");

        Member member = null;
        if (email != null) {
            member = memberRepository.findByEmail(email).orElseThrow(() ->
                    new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage())
            );
        }

        if (member == null) {
            rentalList = category == CategoryType.ALL ?
                    rentalRepository.findAll(pageRequest).getContent() :
                    rentalRepository.findAllByCategory(category, pageRequest).getContent();
        } else {
            GeometryFactory geometryFactory = new GeometryFactory();
            Point memberLocation = geometryFactory.createPoint(new Coordinate(member.getLongitude(), member.getLatitude()));
            memberLocation.setSRID(4326);

            rentalList = category == CategoryType.ALL ?
                    rentalDistanceRepository.findRentalsNearby(memberLocation, DISTANCE_RANGE, pageRequest).getContent() :
                    rentalDistanceRepository.findRentalsByCategoryNearby(category, memberLocation, DISTANCE_RANGE, pageRequest).getContent();
        }

        for (Rental rental : rentalList) {
            AtomicReference<String> firstThumbnail = new AtomicReference<>(rentalImageRepository.findFirstByRental(rental)
                    .map(RentalImage::getImageUrl)
                    .orElse(null));

            if (firstThumbnail.get() != null) {
                rentalImageThumbnailRepository.findByImagePath(firstThumbnail.get()).ifPresent(
                        rentalImageThumbnail -> firstThumbnail.set(rentalImageThumbnail.getThumbnailPath())
                );
            }

            Boolean isLike = member != null && rentalLikeRepository.existsByMemberAndRental(member, rental);

            responseDtoList.add(new RentalCategoryReadResponseDto(rental, firstThumbnail.get(), isLike));
        }
        return responseDtoList;
    }

    public List<RentalMyReadResponseDto> readRentalMyList(String email, int page, int size) {
        Member member = validateMember(email);

        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.Direction.DESC, "createdAt");
        Page<Rental> rentalPage = rentalRepository.findByMember(member, pageRequest);

        List<RentalMyReadResponseDto> responseDtoList = new ArrayList<>();
        for (Rental rental : rentalPage) {
            AtomicReference<String> firstThumbnail = new AtomicReference<>(rentalImageRepository.findFirstByRental(rental)
                    .map(RentalImage::getImageUrl)
                    .orElse(null));

            if (firstThumbnail.get() != null) {
                rentalImageThumbnailRepository.findByImagePath(firstThumbnail.get()).ifPresent(
                        rentalImageThumbnail -> firstThumbnail.set(rentalImageThumbnail.getThumbnailPath())
                );
            }
            responseDtoList.add(new RentalMyReadResponseDto(rental, firstThumbnail.get()));
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

        // 기존 이미지 저장하기
        List<String> beforeImageList = new ArrayList<>();
        rentalImageRepository.findAllByRental(rental).forEach(rentalImage ->
                beforeImageList.add(rentalImage.getImageUrl())
        );

        // 삭제된 이미지 구분하기
        List<String> requestImageUrlList = requestDto.getBeforeImageUrlList();
        List<String> deletedImageList = beforeImageList.stream()
                .filter(imageUrl -> !requestImageUrlList.contains(imageUrl))
                .toList();

        // 삭제 처리 하기
        deletedImageList.forEach(deletedImageUrl -> {
            s3Service.deleteFileFromS3(deletedImageUrl);
            rentalImageRepository.deleteByImageUrl(deletedImageUrl);
        });

        // 새 이미지 파일 검증 및 업로드
        if (multipartFileList != null && !multipartFileList.isEmpty()) {
            validateFiles(multipartFileList);

            multipartFileList.forEach(file -> {
                String imageUrl = s3Service.uploadFileToS3(S3_UPLOAD_FOLDER, file);
                saveRentalImage(rental, imageUrl);
            });
        }

        String badWordFilteringTitle = BadWordFilteringUtil.change(requestDto.getTitle());
        String badWordFilteringContent = BadWordFilteringUtil.change(requestDto.getContent());

        rental.update(
                badWordFilteringTitle,
                requestDto.getCategory(),
                badWordFilteringContent,
                requestDto.getRentalFee(),
                requestDto.getDeposit(),
                rental.getLocation(),
                rental.getDistrict()
        );

        rentalCachingService.deleteKeys(rentalId);
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

        chatRoomRepository.deleteAllByRental(rental);
        rentalRepository.delete(rental);

        rentalCachingService.deleteKeys(rentalId);
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

    private void saveRentalImage(Rental rental, String imageUrl) {
        RentalImage rentalImage = RentalImage.builder()
                .rental(rental)
                .imageUrl(imageUrl)
                .build();

        rentalImageRepository.save(rentalImage);
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