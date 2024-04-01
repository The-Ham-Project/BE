package com.hanghae.theham.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.NaverUserInfoDto;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.entity.type.RoleType;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.global.jwt.TokenProvider;
import com.hanghae.theham.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Service
public class NaverService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final TokenProvider tokenProvider;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.redirect-uri}")
    private String redirectUri;

    public NaverService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, RestTemplate restTemplate, TokenProvider tokenProvider) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.restTemplate = restTemplate;
        this.tokenProvider = tokenProvider;
    }

    public NaverUserInfoDto naverLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String token = getToken(code);

        // 2. 토큰으로 네이버 API 호출 : "액세스 토큰"으로 "네이버 사용자 정보" 가져오기
        NaverUserInfoDto naverUserInfoDto = getNaverUserInfo(token);

        // 3. 필요시에 회원가입
        Member naverUser = registerNaverUserIfNeeded(naverUserInfoDto);

        // 4. 로그인 성공
        String accessToken = tokenProvider.createAccessToken(naverUser.getEmail(), naverUser.getRole().name());
        String refreshToken = tokenProvider.createRefreshToken(naverUser.getEmail(), naverUser.getRole().name());

        response.addHeader(TokenProvider.AUTHORIZATION_HEADER, accessToken);
        tokenProvider.addRefreshTokenToCookie(refreshToken, response);

        forceLogin(naverUser);
        return naverUserInfoDto;
    }

    private String getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://nid.naver.com/oauth2.0")
                .path("/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    private NaverUserInfoDto getNaverUserInfo(String token) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/nid/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String id = jsonNode.get("response")
                .get("id")
                .asText();
        String nickname = UUID.randomUUID().toString();
        String profileUrl = jsonNode.get("response")
                .get("profile_image")
                .asText();
        String email = jsonNode.get("response")
                .get("email")
                .asText();

        return new NaverUserInfoDto(id, nickname, email, profileUrl);
    }

    private Member registerNaverUserIfNeeded(NaverUserInfoDto naverUserInfoDto) {
        // DB 에 중복된 Naver Id 가 있는지 확인
        String naverId = naverUserInfoDto.getId();
        Member naverUser = memberRepository.findByNaverId(naverId).orElse(null);

        if (naverUser == null) {
            // 네이버 사용자 email 동일한 email 가진 회원이 있는지 확인
            String naverEmail = naverUserInfoDto.getEmail();
            Member sameEmailUser = memberRepository.findByEmail(naverEmail).orElse(null);

            if (sameEmailUser != null) {
                naverUser = sameEmailUser;
                // 기존 회원정보에 네이버 Id 추가
                naverUser = naverUser.naverIdUpdate(naverId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: naver email
                String email = naverUserInfoDto.getEmail();

                naverUser = Member.builder()
                        .email(email)
                        .nickname(naverUserInfoDto.getNickname())
                        .password(encodedPassword)
                        .profileUrl(naverUserInfoDto.getProfileUrl())
                        .role(RoleType.ROLE_USER)
                        .naverId(naverId)
                        .build();
            }
            memberRepository.save(naverUser);
        }
        return naverUser;
    }

    private void forceLogin(Member naverUser) {
        UserDetails userDetails = new UserDetailsImpl(naverUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
