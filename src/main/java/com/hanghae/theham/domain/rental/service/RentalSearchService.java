package com.hanghae.theham.domain.rental.service;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.RentalSearchResponseDto;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.RentalSearchResponseListDto;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import com.hanghae.theham.domain.rental.repository.RentalImageRepository;
import com.hanghae.theham.domain.rental.repository.RentalRepository;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.hanghae.theham.domain.rental.dto.RentalImageResponseDto.RentalImageReadResponseDto;

@Slf4j
@Transactional(readOnly = true)
@Service
public class RentalSearchService {
    private final RentalRepository rentalRepository;
    private final RentalImageRepository rentalImageRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public RentalSearchService(RentalRepository rentalRepository, RentalImageRepository rentalImageRepository, MemberRepository memberRepository) {
        this.rentalRepository = rentalRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.memberRepository = memberRepository;
    }

    public RentalSearchResponseListDto searchRentalList(String email, String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size);
        List<Rental> rentalList;
        Long count;

        if (email == null) {
            rentalList = rentalRepository.findAllWithSearch(keyword, pageRequest.getPageSize(), (int) pageRequest.getOffset());
            count = rentalRepository.countByTitleContainingOrContentContaining(keyword);
        } else {
            Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
                log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
                return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
            });

            double longitude = member.getLongitude();
            double latitude = member.getLatitude();

            rentalList = rentalRepository.findAllWithSearchDistance(keyword, latitude, longitude, pageRequest.getPageSize(), (int) pageRequest.getOffset());
            count = rentalRepository.CountWithSearchDistance(keyword, latitude, longitude);
        }

        List<RentalSearchResponseDto> rentalSearchResponseDtoList = new ArrayList<>();
        for (Rental rental : rentalList) {
            List<RentalImage> rentalImageList = rentalImageRepository.findAllByRental(rental);
            List<RentalImageReadResponseDto> images = rentalImageList.stream()
                    .map(RentalImageReadResponseDto::new)
                    .toList();

            rentalSearchResponseDtoList.add(new RentalSearchResponseDto(rental, images));
        }
        return new RentalSearchResponseListDto(count, rentalSearchResponseDtoList);
    }
}
