package com.hanghae.theham.domain.member.repository;

import com.hanghae.theham.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
