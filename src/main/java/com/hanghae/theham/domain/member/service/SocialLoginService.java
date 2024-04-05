package com.hanghae.theham.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberInfoDto;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.member.strategy.GoogleLoginStrategy;
import com.hanghae.theham.domain.member.strategy.KakaoLoginStrategy;
import com.hanghae.theham.domain.member.strategy.NaverLoginStrategy;
import com.hanghae.theham.domain.member.strategy.SocialLoginStrategy;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import com.hanghae.theham.global.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SocialLoginService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final TokenProvider tokenProvider;

    private SocialLoginStrategy socialLoginStrategy;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUrl;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    @Value("${google.redirect-uri}")
    private String googleRedirectUrl;

    @Value("${naver.client-id}")
    private String naverClientId;

    @Value("${naver.client-secret}")
    private String naverClientSecret;

    @Value("${naver.redirect-uri}")
    private String naverRedirectUri;

    public SocialLoginService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, RestTemplate restTemplate, TokenProvider tokenProvider) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.restTemplate = restTemplate;
        this.tokenProvider = tokenProvider;
    }

    public MemberInfoDto login(String type, String code, HttpServletResponse response) throws JsonProcessingException {
        switch (type) {
            case "kakao" -> this.socialLoginStrategy =
                    new KakaoLoginStrategy(passwordEncoder, memberRepository, restTemplate, tokenProvider, kakaoClientId, kakaoRedirectUrl);
            case "google" -> this.socialLoginStrategy =
                    new GoogleLoginStrategy(passwordEncoder, memberRepository, restTemplate, tokenProvider, googleClientId, googleClientSecret, googleRedirectUrl);
            case "naver" -> this.socialLoginStrategy =
                    new NaverLoginStrategy(passwordEncoder, memberRepository, restTemplate, tokenProvider, naverClientId, naverClientSecret, naverRedirectUri);
            default -> throw new BadRequestException(ErrorCode.NOT_FOUND_SOCIAL_LOGIN.getMessage());
        }
        return socialLoginStrategy.socialLogin(code, response);
    }
}
