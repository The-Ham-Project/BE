package com.hanghae.theham.domain.like.repository;

import com.hanghae.theham.domain.like.entity.Like;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.rental.entity.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByMemberAndRental(Member member, Rental rental);

    Optional<Like> findByMemberAndRental(Member member, Rental rental);

    Page<Like> findAllByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);
}
