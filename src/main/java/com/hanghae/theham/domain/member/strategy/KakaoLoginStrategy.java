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
public class KakaoLoginStrategy implements SocialLoginStrategy {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final TokenProvider tokenProvider;

    private final String kakaoClientId;
    private final String kakaoRedirectUrl;

    public KakaoLoginStrategy(
            PasswordEncoder passwordEncoder,
            MemberRepository memberRepository,
            RestTemplate restTemplate,
            TokenProvider tokenProvider,
            @Value("${kakao.client-id}") String kakaoClientId,
            @Value("${kakao.redirect-uri}") String kakaoRedirectUrl
    ) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.restTemplate = restTemplate;
        this.tokenProvider = tokenProvider;
        this.kakaoClientId = kakaoClientId;
        this.kakaoRedirectUrl = kakaoRedirectUrl;
    }

    @Override
    public MemberInfoDto socialLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String token = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        MemberInfoDto memberInfoDto = getMemberInfo(token);

        // 3. 필요시에 회원가입
        Member member = registerMemberIfNeeded(memberInfoDto);

        // 4. 로그인 성공
        String accessToken = tokenProvider.createAccessToken(member.getEmail(), member.getRole().name());
        String refreshToken = tokenProvider.createRefreshToken(member.getEmail(), member.getRole().name());

        response.addHeader(TokenProvider.AUTHORIZATION_HEADER, accessToken);
        response.addHeader(TokenProvider.REFRESH_TOKEN_HEADER, refreshToken);

        forceLogin(member);
        return memberInfoDto;
    }

    @Override
    public String getToken(String code) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", kakaoRedirectUrl);
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    @Override
    public MemberInfoDto getMemberInfo(String token) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
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

        String id = jsonNode.get("id").asText();
        String profileUrl = jsonNode.get("properties").get("profile_image").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();

        return new MemberInfoDto(id, email, profileUrl);
    }

    @Override
    public Member registerMemberIfNeeded(MemberInfoDto memberInfoDto) {
        String kakaoId = memberInfoDto.getId();
        Member kakaoUser = memberRepository.findByKakaoId(kakaoId).orElse(null);

        if (kakaoUser == null) {
            String kakaoEmail = memberInfoDto.getEmail();
            Member sameEmailUser = memberRepository.findByEmail(kakaoEmail).orElse(null);

            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
            } else {
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);
                String email = memberInfoDto.getEmail();

                Integer maxSequence = memberRepository.findMaxNicknameSequence();
                int nextSequenceNumber = maxSequence != null ? maxSequence + 1 : 1;
                String nickname = String.format("더함이%03d", nextSequenceNumber);

                kakaoUser = Member.builder()
                        .email(email)
                        .nickname(nickname)
                        .password(encodedPassword)
                        .profileUrl(memberInfoDto.getProfileUrl())
                        .role(RoleType.ROLE_USER)
                        .kakaoId(kakaoId)
                        .build();
            }
            memberRepository.save(kakaoUser);
        }
        return kakaoUser;
    }

    private void forceLogin(Member member) {
        UserDetails userDetails = new UserDetailsImpl(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
