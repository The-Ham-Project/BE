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

    // 두 좌표(경도, 위도)로 거리 계산(m), distance < 4 / 4km보다 작은 게시글만
    @Query(value =
            "SELECT *, r.distance " +
                    "FROM (SELECT *, ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(rental_tbl.longitude, rental_tbl.latitude)) / 1000 as distance " +
                    "FROM rental_tbl) as r " +
                    "WHERE r.distance < 4 " +
                    "ORDER BY r.created_at DESC " +
                    "LIMIT :limit OFFSET :offset"
            , nativeQuery = true
    )
    List<Rental> findAllByDistance(double userLatitude, double userLongitude, int limit, int offset);

    @Query(value =
            "SELECT  *, r.distance as distance FROM" +
                    "(SELECT *, ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(rental_tbl.longitude, rental_tbl.latitude)) / 1000 as distance FROM rental_tbl) as r " +
                    "WHERE distance < 4 AND category=:category " +
                    "ORDER BY r.created_at DESC " +
                    "LIMIT :limit OFFSET :offset"
            , nativeQuery = true
    )
    List<Rental> findAllByCategoryAndDistance(String category, double userLatitude, double userLongitude, int limit, int offset);

    // 검색 쿼리
    @Query(value =
            "SELECT * FROM rental_tbl " +
                    "WHERE id in(" +
                    "SELECT DISTINCT rental_id FROM rental_image_tbl " +
                    "WHERE title LIKE CONCAT('%', :keyword, '%')) " +
                    "OR content LIKE CONCAT('%', :keyword, '%') " +
                    "ORDER BY created_at DESC " +
                    "LIMIT :limit OFFSET :offset"
            , nativeQuery = true
    )
    List<Rental> findAllWithSearch(String keyword, int limit, int offset);

    @Query("SELECT COUNT(r) FROM Rental r WHERE r.title LIKE %:keyword% OR r.content LIKE %:keyword%")
    Long countByTitleContainingOrContentContaining(String keyword);

    @Query(value =
            "SELECT *, r.distance AS distance FROM " +
                    "(SELECT *, ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(rental_tbl.longitude, rental_tbl.latitude)) / 1000 AS distance " +
                    "FROM rental_tbl " +
                    "WHERE (id IN (SELECT DISTINCT rental_id FROM rental_image_tbl WHERE title LIKE CONCAT('%', :keyword, '%')) " +
                    "OR content LIKE CONCAT('%', :keyword, '%')) " +
                    "AND ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(rental_tbl.longitude, rental_tbl.latitude)) / 1000 < 4) AS r " +
                    "ORDER BY r.created_at DESC " +
                    "LIMIT :limit OFFSET :offset"
            , nativeQuery = true
    )
    List<Rental> findAllWithSearchDistance(String keyword, double userLatitude, double userLongitude, int limit, int offset);

    @Query(value =
            "SELECT count(*) AS distance FROM " +
                    "(SELECT *, ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(rental_tbl.longitude, rental_tbl.latitude)) / 1000 AS distance " +
                    "FROM rental_tbl " +
                    "WHERE (id IN (SELECT DISTINCT rental_id FROM rental_image_tbl WHERE title LIKE CONCAT('%', :keyword, '%')) " +
                    "OR content LIKE CONCAT('%', :keyword, '%')) " +
                    "AND ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(rental_tbl.longitude, rental_tbl.latitude)) / 1000 < 4) AS r"
            , nativeQuery = true
    )
    Long CountWithSearchDistance(String keyword, double userLatitude, double userLongitude);
}
