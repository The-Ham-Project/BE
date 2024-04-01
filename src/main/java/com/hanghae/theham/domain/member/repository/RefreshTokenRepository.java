package com.hanghae.theham.domain.member.repository;

import com.hanghae.theham.domain.member.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
