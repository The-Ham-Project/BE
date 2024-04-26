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
public class NaverLoginStrategy implements SocialLoginStrategy {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final TokenProvider tokenProvider;

    private final String naverClientId;
    private final String naverClientSecret;
    private final String naverRedirectUri;

    public NaverLoginStrategy(
            PasswordEncoder passwordEncoder,
            MemberRepository memberRepository,
            RestTemplate restTemplate,
            TokenProvider tokenProvider,
            @Value("${naver.client-id}") String naverClientId,
            @Value("${naver.client-secret}") String naverClientSecret,
            @Value("${naver.redirect-uri}") String naverRedirectUri
    ) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.restTemplate = restTemplate;
        this.tokenProvider = tokenProvider;
        this.naverClientId = naverClientId;
        this.naverClientSecret = naverClientSecret;
        this.naverRedirectUri = naverRedirectUri;
    }

    @Override
    public MemberInfoDto socialLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String token = getToken(code);
        MemberInfoDto memberInfoDto = getMemberInfo(token);
        Member naverUser = registerMemberIfNeeded(memberInfoDto);

        String accessToken = tokenProvider.createAccessToken(naverUser.getEmail(), naverUser.getRole().name());
        String refreshToken = tokenProvider.createRefreshToken(naverUser.getEmail(), naverUser.getRole().name());

        response.addHeader(TokenProvider.AUTHORIZATION_HEADER, accessToken);
        response.addHeader(TokenProvider.REFRESH_TOKEN_COOKIE, refreshToken);

        forceLogin(naverUser);
        return memberInfoDto;
    }

    @Override
    public String getToken(String code) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://nid.naver.com/oauth2.0")
                .path("/token")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", naverClientId);
        body.add("client_secret", naverClientSecret);
        body.add("redirect_uri", naverRedirectUri);
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
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/nid/me")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String id = jsonNode.get("response").get("id").asText();
        String profileUrl = jsonNode.get("response").get("profile_image").asText();
        String email = jsonNode.get("response").get("email").asText();

        return new MemberInfoDto(id, email, profileUrl);
    }

    @Override
    public Member registerMemberIfNeeded(MemberInfoDto memberInfoDto) {
        String naverId = memberInfoDto.getId();
        Member naverUser = memberRepository.findByNaverId(naverId).orElse(null);

        if (naverUser == null) {
            String naverEmail = memberInfoDto.getEmail();
            Member sameEmailUser = memberRepository.findByEmail(naverEmail).orElse(null);

            if (sameEmailUser != null) {
                naverUser = sameEmailUser;
                naverUser = naverUser.naverIdUpdate(naverId);
            } else {
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);
                String email = memberInfoDto.getEmail();

                // 닉네임
                Integer maxSequence = memberRepository.findMaxNicknameSequence();
                int nextSequenceNumber = maxSequence != null ? maxSequence + 1 : 1;
                String nickname = String.format("더함이%03d", nextSequenceNumber);

                naverUser = Member.builder()
                        .email(email)
                        .nickname(nickname)
                        .password(encodedPassword)
                        .profileUrl(memberInfoDto.getProfileUrl())
                        .role(RoleType.ROLE_USER)
                        .naverId(naverId)
                        .build();
            }
            memberRepository.save(naverUser);
        }
        return naverUser;
    }

    private void forceLogin(Member member) {
        UserDetails userDetails = new UserDetailsImpl(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
