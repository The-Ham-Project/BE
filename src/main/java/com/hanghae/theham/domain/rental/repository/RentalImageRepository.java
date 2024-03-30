package com.hanghae.theham.domain.rental.repository;

import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalImageRepository extends JpaRepository<RentalImage, Long> {

    List<RentalImage> findAllByRental(Rental rental);
}
