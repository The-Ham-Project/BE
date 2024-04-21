package com.hanghae.theham.domain.rental.repository;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    Page<Rental> findAllByCategory(CategoryType category, Pageable pageable);

    Page<Rental> findByMember(Member member, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM rental_tbl WHERE id = :id", nativeQuery = true)
    void hardDeleteForRental(Long id);

    @Query(value = "SELECT * FROM rental_tbl WHERE is_deleted = true", nativeQuery = true)
    List<Rental> findSoftDeletedRentalList();

    @Query("SELECT r FROM Rental r WHERE r.title LIKE %:keyword% OR r.content LIKE %:keyword%")
    Page<Rental> findByTitleContainingOrContentContaining(String keyword, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Rental r WHERE r.title LIKE %:keyword% OR r.content LIKE %:keyword%")
    Long countByTitleContainingOrContentContaining(String keyword);
}
