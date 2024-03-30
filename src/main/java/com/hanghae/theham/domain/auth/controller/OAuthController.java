package com.hanghae.theham.domain.auth.controller;

import com.hanghae.theham.domain.auth.dto.MemberDTO;
import com.hanghae.theham.domain.auth.service.OAuthService;
import com.hanghae.theham.domain.auth.service.type.OAuthType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("login")
public class OAuthController {

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping("/kakao-login")
    public String kakaoLogin() {
        return String.format("redirect:%s", oAuthService.getLoginPage(OAuthType.KAKAO));
    }

    @GetMapping("/kakao-callback")
    @ResponseBody
    public MemberDTO kakaoCallback(HttpServletRequest request) throws Exception {
        String accessToken = oAuthService.getUserToken(OAuthType.KAKAO, request.getParameter("code")).get("accessToken");
        return oAuthService.getUserInfo(OAuthType.KAKAO, accessToken);
    }

    @GetMapping("/naver-login")
    public String naverLogin() {
        return String.format("redirect:%s", oAuthService.getLoginPage(OAuthType.NAVER));

    }

    @GetMapping("/naver-callback")
    @ResponseBody
    public MemberDTO naverCallback(HttpServletRequest request) throws Exception {
        String accessToken = oAuthService.getUserToken(OAuthType.NAVER, request.getParameter("code")).get("accessToken");
        return oAuthService.getUserInfo(OAuthType.NAVER, accessToken);
    }

    @GetMapping("/google-login")
    public String googleLogin() {
        return String.format("redirect:%s", oAuthService.getLoginPage(OAuthType.GOOGLE));
    }

    @GetMapping("/google-callback")
    @ResponseBody
    public MemberDTO googleCallback(HttpServletRequest request) throws Exception {
        String accessToken = oAuthService.getUserToken(OAuthType.GOOGLE, request.getParameter("code")).get("accessToken");
        return oAuthService.getUserInfo(OAuthType.GOOGLE, accessToken);
    }


}
