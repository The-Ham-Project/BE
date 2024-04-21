package com.hanghae.theham.domain.rental.service;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.RentalSearchResponseDto;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.RentalSearchResponseListDto;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import com.hanghae.theham.domain.rental.repository.RentalDistanceRepository;
import com.hanghae.theham.domain.rental.repository.RentalImageRepository;
import com.hanghae.theham.domain.rental.repository.RentalImageThumbnailRepository;
import com.hanghae.theham.domain.rental.repository.RentalRepository;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Transactional(readOnly = true)
@Service
public class RentalSearchService {

    private static final double DISTANCE_RANGE = 4000.0;

    private final RentalRepository rentalRepository;
    private final RentalDistanceRepository rentalDistanceRepository;
    private final RentalImageRepository rentalImageRepository;
    private final RentalImageThumbnailRepository rentalImageThumbnailRepository;
    private final MemberRepository memberRepository;

    public RentalSearchService(RentalRepository rentalRepository, RentalDistanceRepository rentalDistanceRepository, RentalImageRepository rentalImageRepository, RentalImageThumbnailRepository rentalImageThumbnailRepository, MemberRepository memberRepository) {
        this.rentalRepository = rentalRepository;
        this.rentalDistanceRepository = rentalDistanceRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.rentalImageThumbnailRepository = rentalImageThumbnailRepository;
        this.memberRepository = memberRepository;
    }

    public RentalSearchResponseListDto searchRentalList(String email, String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.Direction.DESC, "createdAt");
        List<Rental> rentalList;
        Long count;

        if (email == null) {
            rentalList = rentalRepository.findByTitleContainingOrContentContaining(keyword, pageRequest).getContent();
            count = rentalRepository.countByTitleContainingOrContentContaining(keyword);
        } else {
            Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
                log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
                return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
            });

            GeometryFactory geometryFactory = new GeometryFactory();
            Point memberLocation = geometryFactory.createPoint(new Coordinate(member.getLongitude(), member.getLatitude()));
            memberLocation.setSRID(4326);

            rentalList = rentalDistanceRepository.findRentalsByKeywordNearby(keyword, memberLocation, DISTANCE_RANGE, pageRequest).getContent();
            count = rentalDistanceRepository.countRentalsByKeywordNearby(keyword, memberLocation, DISTANCE_RANGE);
        }

        List<RentalSearchResponseDto> rentalSearchResponseDtoList = new ArrayList<>();
        for (Rental rental : rentalList) {
            AtomicReference<String> firstThumbnail = new AtomicReference<>(rentalImageRepository.findFirstByRental(rental)
                    .map(RentalImage::getImageUrl)
                    .orElse(null));

            if (firstThumbnail.get() != null) {
                rentalImageThumbnailRepository.findByImagePath(firstThumbnail.get()).ifPresent(
                        rentalImageThumbnail -> firstThumbnail.set(rentalImageThumbnail.getThumbnailPath())
                );
            }
            rentalSearchResponseDtoList.add(new RentalSearchResponseDto(rental, firstThumbnail.get()));
        }
        return new RentalSearchResponseListDto(count, rentalSearchResponseDtoList);
    }
}
