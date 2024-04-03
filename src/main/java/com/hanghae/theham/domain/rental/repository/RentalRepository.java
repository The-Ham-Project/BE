package com.hanghae.theham.domain.rental.repository;

import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    Slice<Rental> findAllByCategoryOrderByCreatedAt(CategoryType categoryType, Pageable pageable);
    Slice<Rental> findSliceBy(Pageable pageable);
}
