package com.hanghae.theham.domain.rental.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.rental.dto.RentalRequestDto.RentalCreateRequestDto;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.RentalCreateResponseDto;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import com.hanghae.theham.domain.rental.repository.RentalImageRepository;
import com.hanghae.theham.domain.rental.repository.RentalRepository;
import com.hanghae.theham.global.config.S3Config;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Transactional(readOnly = true)
@Service
public class RentalService {

    private static final int MAX_IMAGE_UPLOAD_COUNT = 3;
    private static final int MAX_IMAGE_UPLOAD_SIZE = 2 * 1024 * 1024; // 2MB
    private static final List<String> LIMIT_IMAGE_TYPE_LIST = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );
    private static final String S3_UPLOAD_FOLDER = "rentals/";

    private final RentalRepository rentalRepository;
    private final RentalImageRepository rentalImageRepository;
    private final MemberRepository memberRepository;
    private final S3Config s3Config;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public RentalService(RentalRepository rentalRepository, RentalImageRepository rentalImageRepository, MemberRepository memberRepository, S3Config s3Config) {
        this.rentalRepository = rentalRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.memberRepository = memberRepository;
        this.s3Config = s3Config;
    }

    @Transactional
    public RentalCreateResponseDto createRental(RentalCreateRequestDto requestDto, List<MultipartFile> multipartFileList) {
        // 회원 정보 검증
        Member member = memberRepository.findByEmail(requestDto.getEmail()).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", requestDto.getEmail());
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });

        // 함께쓰기 정보 저장
        Rental rental = rentalRepository.save(requestDto.toEntity(member));

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
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    private void saveRentalImage(Rental rental, String imageUrl) {
        RentalImage rentalImage = RentalImage.builder()
                .rental(rental)
                .imageUrl(imageUrl)
                .build();
        rentalImageRepository.save(rentalImage);
    }
}
