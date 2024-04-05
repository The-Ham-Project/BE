package com.hanghae.theham.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.GoogleUserInfoDto;
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
public class GoogleService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final TokenProvider tokenProvider;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    public GoogleService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, RestTemplate restTemplate, TokenProvider tokenProvider) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.restTemplate = restTemplate;
        this.tokenProvider = tokenProvider;
    }

    public GoogleUserInfoDto googleLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String token = getToken(code);

        // 2. 토큰으로 구글 API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기
        GoogleUserInfoDto googleUserInfoDto = getGoogleUserInfo(token);

        // 3. 필요시에 회원가입
        Member googleUser = registerGoogleUserIfNeeded(googleUserInfoDto);

        // 4. 로그인 성공
        String accessToken = tokenProvider.createAccessToken(googleUser.getEmail(), googleUser.getRole().name());
        String refreshToken = tokenProvider.createRefreshToken(googleUser.getEmail(), googleUser.getRole().name());

        response.addHeader(TokenProvider.AUTHORIZATION_HEADER, accessToken);
        tokenProvider.addRefreshTokenToCookie(refreshToken, response);

        forceLogin(googleUser);
        return googleUserInfoDto;
    }

    private String getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com/token")
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

    private GoogleUserInfoDto getGoogleUserInfo(String token) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com/oauth2/v2/userinfo")
                .queryParam("access_token", token)
                .encode()
                .build()
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String id = jsonNode.get("id").asText();
        String nickname = UUID.randomUUID().toString();
        String email = jsonNode.get("email").asText();
        String profileUrl = jsonNode.get("picture").asText();

        return new GoogleUserInfoDto(id, nickname, email, profileUrl);
    }

    private Member registerGoogleUserIfNeeded(GoogleUserInfoDto googleUserInfoDto) {
        // DB 에 중복된 Google Id 가 있는지 확인
        String googleId = googleUserInfoDto.getId();
        Member googleUser = memberRepository.findByGoogleId(googleId).orElse(null);

        if (googleUser == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String googleEmail = googleUserInfoDto.getEmail();
            Member sameEmailUser = memberRepository.findByEmail(googleEmail).orElse(null);

            if (sameEmailUser != null) {
                googleUser = sameEmailUser;
                // 기존 회원정보에 구글 Id 추가
                googleUser = googleUser.googleIdUpdate(googleId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: google email
                String email = googleUserInfoDto.getEmail();

                // 닉네임
                Integer maxSequence = memberRepository.findMaxNicknameSequence();
                int nextSequenceNumber = maxSequence != null ? maxSequence + 1 : 1;
                String nickname = String.format("더함이%03d", nextSequenceNumber);

                googleUser = Member.builder()
                        .email(email)
                        .nickname(nickname)
                        .password(encodedPassword)
                        .profileUrl(googleUserInfoDto.getProfileUrl())
                        .role(RoleType.ROLE_USER)
                        .googleId(googleId)
                        .build();
            }
            memberRepository.save(googleUser);
        }
        return googleUser;
    }

    private void forceLogin(Member googleUser) {
        UserDetails userDetails = new UserDetailsImpl(googleUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
