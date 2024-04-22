package com.hanghae.theham.domain.rental.repository;

import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RentalImageRepository extends JpaRepository<RentalImage, Long> {

    List<RentalImage> findAllByRental(Rental rental);

    @Modifying
    @Query(value = "DELETE FROM rental_image_tbl WHERE rental_id = :rental_id", nativeQuery = true)
    void hardDeleteForRentalImage(Long rental_id);

    Optional<RentalImage> findFirstByRental(Rental rental);

    void deleteByImageUrl(String imageUrl);
}
