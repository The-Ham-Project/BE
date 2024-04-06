package com.hanghae.theham.domain.like.service;

import com.hanghae.theham.domain.like.dto.LikeResponseDto.*;
import com.hanghae.theham.domain.like.entity.Like;
import com.hanghae.theham.domain.like.repository.LikeRepository;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.repository.RentalRepository;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final RentalRepository rentalRepository;

    public LikeService(LikeRepository likeRepository, MemberRepository memberRepository, RentalRepository rentalRepository) {
        this.likeRepository = likeRepository;
        this.memberRepository = memberRepository;
        this.rentalRepository = rentalRepository;
    }

    @Transactional
    public LikeCreateResponseDto createLike(String email, Long rentalId) {
        Member member = validateMember(email);
        Rental rental = findRentalById(rentalId);

        if (likeRepository.existsByMemberAndRental(member, rental)) {
            throw new BadRequestException(ErrorCode.ALREADY_EXIST_LIKE.getMessage());
        }

        Like like = likeRepository.save(Like.builder()
                .member(member)
                .rental(rental)
                .build());

        return new LikeCreateResponseDto(like);
    }

    @Transactional
    public void deleteLike(String email, Long rentalId) {
        Member member = validateMember(email);
        Rental rental = findRentalById(rentalId);

        Like like = likeRepository.findByMemberAndRental(member, rental).orElseThrow(() ->
                new BadRequestException(ErrorCode.NOT_FOUND_LIKE_ID.getMessage()));

        likeRepository.delete(like);
    }

    public Slice<LikeReadResponseDto> readLikeList(String email, int page, int size) {
        Member member = validateMember(email);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.Direction.DESC, "createdAt");
        Slice<Like> likeListPage = likeRepository.findAllByMemberOrderByCreatedAtDesc(member, pageable);

        List<LikeReadResponseDto> responseDtoList = likeListPage.getContent().stream()
                .map(like -> new LikeReadResponseDto(like))
                .collect(Collectors.toList());

        // 페이징 여부 확인
        boolean hasNestPage = likeListPage.hasNext();

        return new SliceImpl<>(responseDtoList, pageable, hasNestPage);
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
