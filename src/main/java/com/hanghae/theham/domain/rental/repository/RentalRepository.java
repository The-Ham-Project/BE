package com.hanghae.theham.domain.rental.repository;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    Slice<Rental> findAllByCategory(CategoryType category, Pageable pageable);

    Slice<Rental> findByMemberOrderByCreatedAt(Member member, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM rental_tbl WHERE id = :id", nativeQuery = true)
    void hardDeleteForRental(Long id);

    @Query(value = "SELECT * FROM rental_tbl WHERE is_deleted = true", nativeQuery = true)
    List<Rental> findSoftDeletedRentalList();

    // 두 좌표(경도, 위도)로 거리 계산(m), distance < 4 / 4km보다 작은 게시글만
    @Query(value =
            "SELECT  *, r.distance as distance FROM" +
                    "(SELECT *, ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(rental_tbl.longitude, rental_tbl.latitude)) / 1000 as distance FROM rental_tbl) as r " +
                    "WHERE distance < 4 " +
                    "ORDER BY r.created_at DESC " +
                    "LIMIT :limit OFFSET :page"
            , nativeQuery = true
    )
    Slice<Rental> findAllByDistance(int page, int limit, double userLatitude, double userLongitude);

    @Query(value =
            "SELECT  *, r.distance as distance FROM" +
                    "(SELECT *, ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(rental_tbl.longitude, rental_tbl.latitude)) / 1000 as distance FROM rental_tbl) as r " +
                    "WHERE distance < 4 AND category=:category " +
                    "ORDER BY r.created_at DESC " +
                    "LIMIT :limit OFFSET :page"
            , nativeQuery = true
    )
    Slice<Rental> findAllByCategoryAndDistance(String category, int page, int limit, double userLatitude, double userLongitude);

    // 검색 쿼리
    @Query(value = "SELECT * FROM rental_tbl " +
            "WHERE id in(" +
            "SELECT DISTINCT rental_id FROM rental_image_tbl " +
            "WHERE image_url LIKE CONCAT('%', :searchValue, '%')) " +
            "OR id LIKE CONCAT('%', :searchValue, '%') " +
            "OR title LIKE CONCAT('%', :searchValue, '%') " +
            "OR member_id LIKE CONCAT('%', :searchValue, '%') " +
            "ORDER BY created_at DESC " +
            "LIMIT :size OFFSET :page", nativeQuery = true)
    Slice<Rental> findAllWithSearch(String searchValue, int page, int size);
}
