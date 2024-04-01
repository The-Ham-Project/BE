package com.hanghae.theham.domain.member.service;

import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.member.repository.RefreshTokenRepository;
import com.hanghae.theham.global.exception.ErrorCode;
import com.hanghae.theham.global.exception.TokenException;
import com.hanghae.theham.global.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    public AuthService(MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, TokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = tokenProvider.getRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            log.error("리프레쉬 토큰을 찾을 수 없습니다.");
            throw new TokenException(ErrorCode.NOT_FOUND_REFRESH_TOKEN.getMessage());
        }

        String type = tokenProvider.getTokenType(refreshToken);
        if (!type.equals("refresh")) {
            log.error("사용할 수 없는 리프레쉬 토큰입니다.");
            throw new TokenException(ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
        }

        if (!refreshTokenRepository.existsById(refreshToken)) {
            log.error("DB에서 리프레쉬 토큰을 찾을 수 없습니다.");
            throw new TokenException(ErrorCode.NOT_FOUND_REFRESH_TOKEN.getMessage());
        }

        String email = tokenProvider.getTokenEmail(refreshToken);
        String role = tokenProvider.getTokenRole(refreshToken);
        refreshTokenRepository.deleteById(refreshToken);

        String newAccessToken = tokenProvider.createAccessToken(email, role);
        String newRefreshToken = tokenProvider.createRefreshToken(email, role);

        response.addHeader(TokenProvider.AUTHORIZATION_HEADER, newAccessToken);
        tokenProvider.addRefreshTokenToCookie(newRefreshToken, response);
    }
}
