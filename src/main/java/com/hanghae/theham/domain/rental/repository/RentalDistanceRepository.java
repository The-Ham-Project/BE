package com.hanghae.theham.domain.rental.repository;

import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RentalDistanceRepository extends JpaRepository<Rental, Long> {

    @Query("SELECT r FROM Rental r WHERE distance(r.location, :location) <= :distance")
    Page<Rental> findRentalsNearby(Point location, double distance, Pageable pageable);

    @Query("SELECT r FROM Rental r WHERE r.category = :category AND distance(r.location, :location) <= :distance")
    Page<Rental> findRentalsByCategoryNearby(CategoryType category, Point location, double distance, Pageable pageable);

    @Query("SELECT r FROM Rental r WHERE (r.title LIKE %:keyword% OR r.content LIKE %:keyword%) AND distance(r.location, :location) <= :distance")
    Page<Rental> findRentalsByKeywordNearby(String keyword, Point location, double distance, Pageable pageable);

    @Query("SELECT count(r) FROM Rental r WHERE (r.title LIKE %:keyword% OR r.content LIKE %:keyword%) AND distance(r.location, :location) <= :distance")
    Long countRentalsByKeywordNearby(String keyword, Point location, double distance);
}
