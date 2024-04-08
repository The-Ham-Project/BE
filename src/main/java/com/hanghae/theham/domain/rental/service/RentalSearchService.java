package com.hanghae.theham.domain.rental.service;

import com.hanghae.theham.domain.rental.dto.RentalImageResponseDto;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import com.hanghae.theham.domain.rental.repository.RentalImageRepository;
import com.hanghae.theham.domain.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hanghae.theham.domain.rental.dto.RentalImageResponseDto.*;
import static com.hanghae.theham.domain.rental.dto.RentalResponseDto.*;

@Service
@RequiredArgsConstructor
public class RentalSearchService {
    private final RentalRepository rentalRepository;
    private final RentalImageRepository rentalImageRepository;

    public List<RentalReadResponseDto> searchRentalList(String searchValue, int page, int size) {
        Slice<Rental> rentalSlice = rentalRepository.findAllWithSearch(searchValue, page, size);
        List<RentalReadResponseDto> rentals = new ArrayList<>();

        for (Rental rental : rentalSlice) {
            // 이미지 찾아와서 RentalImageReadResponseDto 로 변환
            List<RentalImage> rentalImages = rentalImageRepository.findAllByRental(rental);
            List<RentalImageReadResponseDto> images = rentalImages.stream().map(RentalImageReadResponseDto::new).toList();

            rentals.add(new RentalReadResponseDto(rental, images));
        }
        return rentals;
    }
}
