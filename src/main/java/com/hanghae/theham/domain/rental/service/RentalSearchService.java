package com.hanghae.theham.domain.rental.service;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import com.hanghae.theham.domain.rental.repository.RentalImageRepository;
import com.hanghae.theham.domain.rental.repository.RentalRepository;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.hanghae.theham.domain.rental.dto.RentalImageResponseDto.RentalImageReadResponseDto;
import static com.hanghae.theham.domain.rental.dto.RentalResponseDto.RentalReadResponseDto;

@Service
@Slf4j
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

    public List<RentalReadResponseDto> searchUserRentalList(String searchValue, int page, int size, @Nullable String email) {
        Slice<Rental> rentalSlice;

        // 비회원 - 전체 게시글
        if(email ==null){
            rentalSlice = rentalRepository.findAllWithSearch(searchValue, page, size);
        }
        // 회원 - 거리 이내 전체 게시글
        else{
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
                        return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
                    });

            double longitude = member.getLongitude();
            double latitude = member.getLatitude();

            rentalSlice = rentalRepository.findAllWithSearchDistance(searchValue, page, size, latitude, longitude);

        }
        return convertSliceToListDto(rentalSlice);
    }

    private List<RentalReadResponseDto> convertSliceToListDto(Slice<Rental> rentalSlice) {
        List<RentalReadResponseDto> rentals = new ArrayList<>();

        for (Rental rental : rentalSlice) {
            List<RentalImage> rentalImages = rentalImageRepository.findAllByRental(rental);
            List<RentalImageReadResponseDto> images = rentalImages.stream().map(RentalImageReadResponseDto::new).toList();

            rentals.add(new RentalReadResponseDto(rental, images));
        }
        return rentals;
    }
}
