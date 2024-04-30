package com.hanghae.theham.domain.member.service;

import com.hanghae.theham.domain.member.dto.MemberRequestDto.MemberUpdatePositionRequestDto;
import com.hanghae.theham.domain.member.dto.MemberRequestDto.MemberUpdateRequestDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberCheckNicknameResponseDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberCheckPositionResponseDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberReadResponseDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberUpdatePositionResponseDto;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import com.hanghae.theham.global.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@Service
public class MemberService {

    private static final int MAX_IMAGE_UPLOAD_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> LIMIT_IMAGE_TYPE_LIST = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );
    private static final String S3_UPLOAD_FOLDER = "profiles/";

    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    public MemberService(MemberRepository memberRepository, S3Service s3Service) {
        this.memberRepository = memberRepository;
        this.s3Service = s3Service;
    }

    @Cacheable(value = "Members", key = "#email", cacheManager = "redisCacheManager")
    public MemberReadResponseDto getMember(String email) {
        Member member = validateMember(email);
        return new MemberReadResponseDto(member);
    }

    @Transactional
    public MemberUpdatePositionResponseDto updatePosition(String email, MemberUpdatePositionRequestDto requestDto) {
        Member member = validateMember(email);

        member.updatePosition(requestDto.getLatitude(), requestDto.getLongitude());
        return new MemberUpdatePositionResponseDto(member);
    }

    @CacheEvict(value = "Members", key = "#email", cacheManager = "redisCacheManager")
    @Transactional
    public void updateProfile(String email, MemberUpdateRequestDto requestDto, MultipartFile profileImage) {
        Member member = validateMember(email);

        if (memberRepository.existsByNickname(requestDto.getNickname())) {
            throw new BadRequestException(ErrorCode.ALREADY_EXIST_NICKNAME.getMessage());
        }

        // 프로필 이미지 업로드 및 저장
        if (profileImage != null && !profileImage.isEmpty()) {
            validateFile(profileImage);
            String profileUrl = s3Service.uploadFileToS3(S3_UPLOAD_FOLDER, profileImage);
            member.updateProfile(profileUrl);
        }

        member.updateNickname(requestDto.getNickname());
    }

    public MemberCheckPositionResponseDto checkPosition(String email) {
        Member member = validateMember(email);

        if (member.getLongitude() == 0.0 || member.getLatitude() == 0.0) {
            return new MemberCheckPositionResponseDto(false);
        }
        return new MemberCheckPositionResponseDto(true);
    }

    public MemberCheckNicknameResponseDto checkNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            return new MemberCheckNicknameResponseDto(true);
        }
        return new MemberCheckNicknameResponseDto(false);
    }

    private Member validateMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });
    }

    private void validateFile(MultipartFile file) {
        if (!LIMIT_IMAGE_TYPE_LIST.contains(file.getContentType())) {
            throw new BadRequestException(ErrorCode.UNSUPPORTED_FILE_TYPE.getMessage());
        }
        if (file.getSize() > MAX_IMAGE_UPLOAD_SIZE) {
            throw new BadRequestException(ErrorCode.FILE_SIZE_EXCEEDED.getMessage());
        }
    }
}
