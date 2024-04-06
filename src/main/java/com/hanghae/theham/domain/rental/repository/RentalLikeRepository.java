package com.hanghae.theham.domain.rental.repository;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentalLikeRepository extends JpaRepository<RentalLike, Long> {
    boolean existsByMemberAndRental(Member member, Rental rental);

    Optional<RentalLike> findByMemberAndRental(Member member, Rental rental);
}
