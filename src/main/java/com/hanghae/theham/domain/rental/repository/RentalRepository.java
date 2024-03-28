package com.hanghae.theham.domain.rental.repository;

import com.hanghae.theham.domain.rental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
