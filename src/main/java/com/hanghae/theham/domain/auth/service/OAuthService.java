package com.hanghae.theham.domain.auth.service;

import com.hanghae.theham.domain.auth.dto.MemberDTO;
import com.hanghae.theham.domain.auth.service.type.OAuthType;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.entity.type.RoleType;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Optional;

@Service
public class OAuthService {
    private final MemberRepository memberRepository;
    private final Environment env;

    private final String ENV_URI_PREFIX = "spring.uri.";
    private final String ENV_SECURITY_PREFIX = "spring.security.oauth2.client.registration.";

    public OAuthService(MemberRepository memberRepository, Environment env) {
        this.memberRepository = memberRepository;
        this.env = env;
        System.out.println(getLoginPage(OAuthType.KAKAO));
        System.out.println(getLoginPage(OAuthType.NAVER));
    }

    public String getLoginPage(OAuthType providerType) {
        String provider = providerType.getValue();
        String AUTH_URI = env.getProperty(ENV_URI_PREFIX + provider + ".auth-uri");
        String CLIENT_ID = env.getProperty(ENV_SECURITY_PREFIX + provider + ".client-id");
        String REDIRECT_URL = env.getProperty(ENV_SECURITY_PREFIX + provider + ".redirect-uri");

        //google 만 scope 필요
        String scope = providerType == OAuthType.GOOGLE ? "&scope=https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/userinfo.profile" : "";

        return AUTH_URI
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URL
                + "&response_type=code"
                + scope;
    }

    public HashMap<String, String> getUserToken(OAuthType providerType, String code) throws Exception {
        String accessToken = "";
        String refreshToken = "";

        try {
            HttpEntity<MultiValueMap<String, String>> httpEntity = makeTokenHttpEntity(providerType, code);
            ResponseEntity<String> response = getTokenExchangeResponse(providerType, httpEntity);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());
            accessToken = (String) jsonObj.get("access_token");
            refreshToken = (String) jsonObj.get("refresh_token");

        } catch (Exception e) {
            throw new Exception("API Call failed");
        }

        HashMap<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    public MemberDTO getUserInfo(OAuthType providerType, String accessToken) throws Exception {
        ResponseEntity<String> response = getUserInfoResponse(providerType, accessToken);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

        // getUserInfo
        String id;
        String email;
        String nickname;
        String profileUrl;

        if (providerType == OAuthType.NAVER) {
            JSONObject account = (JSONObject) jsonObj.get("response");
            id = String.valueOf(account.get("id"));
            email = String.valueOf(account.get("email"));
            nickname = String.valueOf(account.get("nickname"));
            profileUrl = String.valueOf(account.get("profile_image"));

        } else if (providerType == OAuthType.KAKAO) {
            JSONObject account = (JSONObject) jsonObj.get("kakao_account");
            JSONObject profile = (JSONObject) account.get("profile");

            id = String.valueOf(jsonObj.get("id"));
            email = String.valueOf(account.get("email"));
            nickname = String.valueOf(profile.get("nickname"));
            profileUrl = null;
        } else {
            id = String.valueOf(jsonObj.get("sub"));
            email = String.valueOf(jsonObj.get("email"));
            nickname = String.valueOf(jsonObj.get("name"));
            profileUrl = String.valueOf(jsonObj.get("picture"));
        }

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if(optionalMember.isEmpty()){
            Member member = new Member(email, nickname, profileUrl, RoleType.ROLE_USER);
            memberRepository.save(member);
        }
        return MemberDTO.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .profileUrl(profileUrl)
                .build();
    }


    private HttpEntity<MultiValueMap<String, String>> makeTokenHttpEntity(OAuthType providerType, String code) {
        String provider = providerType.getValue();
        String CLIENT_ID = env.getProperty(ENV_SECURITY_PREFIX + provider + ".client-id");
        String CLIENT_SECRET = env.getProperty(ENV_SECURITY_PREFIX + provider + ".client-secret");
        String REDIRECT_URL = env.getProperty(ENV_SECURITY_PREFIX + provider + ".redirect-uri");


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("code", code);
        params.add("redirect_uri", REDIRECT_URL);

        return new HttpEntity<>(params, headers);
    }

    private ResponseEntity<String> getTokenExchangeResponse(OAuthType providerType, HttpEntity<MultiValueMap<String, String>> httpEntity) {
        String TOKEN_URI = env.getProperty(ENV_URI_PREFIX + providerType.getValue() + ".token-uri");
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(
                TOKEN_URI,
                HttpMethod.POST,
                httpEntity,
                String.class
        );
    }

    private ResponseEntity<String> getUserInfoResponse(OAuthType provider, String accessToken) {
        String USERINFO_URI = env.getProperty(ENV_URI_PREFIX + provider.getValue() + ".userInfo-uri");

        //HttpHeader 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpHeader 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        return rt.exchange(
                USERINFO_URI,
                HttpMethod.POST,
                httpEntity,
                String.class
        );
    }

}
