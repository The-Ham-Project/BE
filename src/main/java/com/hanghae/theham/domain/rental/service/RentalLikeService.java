package com.hanghae.theham.domain.rental.service;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.rental.dto.RentalLikeResponseDto.RentalLikeCheckResponseDto;
import com.hanghae.theham.domain.rental.dto.RentalLikeResponseDto.RentalLikeCreateResponseDto;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalLike;
import com.hanghae.theham.domain.rental.repository.RentalLikeRepository;
import com.hanghae.theham.domain.rental.repository.RentalRepository;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@Service
public class RentalLikeService {

    private final RentalLikeRepository rentalLikeRepository;
    private final MemberRepository memberRepository;
    private final RentalRepository rentalRepository;
    private final RentalCachingService rentalCachingService;

    public RentalLikeService(RentalLikeRepository rentalLikeRepository, MemberRepository memberRepository, RentalRepository rentalRepository, RentalCachingService rentalCachingService) {
        this.rentalLikeRepository = rentalLikeRepository;
        this.memberRepository = memberRepository;
        this.rentalRepository = rentalRepository;
        this.rentalCachingService = rentalCachingService;
    }

    @Transactional
    public RentalLikeCreateResponseDto createRentalLike(String email, Long rentalId) {
        Member member = validateMember(email);
        Rental rental = findRentalById(rentalId);

        if (rentalLikeRepository.existsByMemberAndRental(member, rental)) {
            throw new BadRequestException(ErrorCode.ALREADY_EXIST_RENTAL_LIKE.getMessage());
        }

        RentalLike rentalLike = rentalLikeRepository.save(RentalLike.builder()
                .member(member)
                .rental(rental)
                .build());

        rentalCachingService.deleteKeys(rentalId);
        return new RentalLikeCreateResponseDto(rentalLike);
    }

    @Transactional
    public void deleteRentalLike(String email, Long rentalId) {
        Member member = validateMember(email);
        Rental rental = findRentalById(rentalId);

        RentalLike rentalLike = rentalLikeRepository.findByMemberAndRental(member, rental).orElseThrow(() ->
                new BadRequestException(ErrorCode.NOT_FOUND_LIKE_ID.getMessage()));

        rentalCachingService.deleteKeys(rentalId);
        rentalLikeRepository.delete(rentalLike);
    }

    public RentalLikeCheckResponseDto checkRentalLike(String email, Long rentalId) {
        Member member = validateMember(email);
        Rental rental = findRentalById(rentalId);

        boolean isChecked = rentalLikeRepository.existsByMemberAndRental(member, rental);

        return new RentalLikeCheckResponseDto(isChecked);
    }

    private Member validateMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });
    }

    private Rental findRentalById(Long rentalId) {
        return rentalRepository.findById(rentalId).orElseThrow(() -> {
            log.error("해당 게시글을 찾을 수 없습니다. 게시글: {}", rentalId);
            return new BadRequestException(ErrorCode.NOT_FOUND_RENTAL.getMessage());
        });
    }
}
