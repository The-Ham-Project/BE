package com.hanghae.theham.domain.member.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberInfoDto;
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
public class GoogleLoginStrategy implements SocialLoginStrategy {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final TokenProvider tokenProvider;

    private final String googleClientId;
    private final String googleClientSecret;
    private final String googleRedirectUrl;

    public GoogleLoginStrategy(
            PasswordEncoder passwordEncoder,
            MemberRepository memberRepository,
            RestTemplate restTemplate,
            TokenProvider tokenProvider,
            @Value("${google.client-id}") String googleClientId,
            @Value("${google.client-secret}") String googleClientSecret,
            @Value("${google.redirect-uri}") String googleRedirectUrl
    ) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.restTemplate = restTemplate;
        this.tokenProvider = tokenProvider;
        this.googleClientId = googleClientId;
        this.googleClientSecret = googleClientSecret;
        this.googleRedirectUrl = googleRedirectUrl;
    }

    @Override
    public MemberInfoDto socialLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String token = getToken(code);
        MemberInfoDto memberInfoDto = getMemberInfo(token);
        Member googleUser = registerMemberIfNeeded(memberInfoDto);

        String accessToken = tokenProvider.createAccessToken(googleUser.getEmail(), googleUser.getRole().name());
        String refreshToken = tokenProvider.createRefreshToken(googleUser.getEmail(), googleUser.getRole().name());

        response.addHeader(TokenProvider.AUTHORIZATION_HEADER, accessToken);
        tokenProvider.addRefreshTokenToCookie(refreshToken, response);

        forceLogin(googleUser);
        return memberInfoDto;
    }

    @Override
    public String getToken(String code) throws JsonProcessingException {
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
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("redirect_uri", googleRedirectUrl);
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

    @Override
    public MemberInfoDto getMemberInfo(String token) throws JsonProcessingException {
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
        String email = jsonNode.get("email").asText();
        String profileUrl = jsonNode.get("picture").asText();

        return new MemberInfoDto(id, email, profileUrl);
    }

    @Override
    public Member registerMemberIfNeeded(MemberInfoDto memberInfoDto) {
        String googleId = memberInfoDto.getId();
        Member googleUser = memberRepository.findByGoogleId(googleId).orElse(null);

        if (googleUser == null) {
            String googleEmail = memberInfoDto.getEmail();
            Member sameEmailUser = memberRepository.findByEmail(googleEmail).orElse(null);

            if (sameEmailUser != null) {
                googleUser = sameEmailUser;
                googleUser = googleUser.googleIdUpdate(googleId);
            } else {
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);
                String email = memberInfoDto.getEmail();

                // 닉네임
                Integer maxSequence = memberRepository.findMaxNicknameSequence();
                int nextSequenceNumber = maxSequence != null ? maxSequence + 1 : 1;
                String nickname = String.format("더함이%03d", nextSequenceNumber);

                googleUser = Member.builder()
                        .email(email)
                        .nickname(nickname)
                        .password(encodedPassword)
                        .profileUrl(memberInfoDto.getProfileUrl())
                        .role(RoleType.ROLE_USER)
                        .googleId(googleId)
                        .build();
            }
            memberRepository.save(googleUser);
        }
        return googleUser;
    }

    private void forceLogin(Member member) {
        UserDetails userDetails = new UserDetailsImpl(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
