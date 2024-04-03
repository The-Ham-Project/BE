package com.hanghae.theham.domain.rental.repository;

import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    Slice<Rental> findAllByCategoryOrderByCreatedAt(CategoryType categoryType, Pageable pageable);
    Slice<Rental> findSliceBy(Pageable pageable);
    List<Rental> findAllByCategoryOrderByCreatedAt(CategoryType categoryType);

    @Modifying
    @Query(value = "DELETE FROM rental_tbl WHERE id = :id", nativeQuery = true)
    void hardDeleteForRental(Long id);

    @Query(value = "SELECT * FROM rental_tbl WHERE is_deleted = true", nativeQuery = true)
    List<Rental> findSoftDeletedRentalList();
}
