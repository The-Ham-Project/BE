package com.hanghae.theham.domain.rental.repository;

import com.hanghae.theham.domain.rental.entity.RentalImageThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentalImageThumbnailRepository extends JpaRepository<RentalImageThumbnail, Long> {

    Optional<RentalImageThumbnail> findByImagePath(String imagePath);
}
