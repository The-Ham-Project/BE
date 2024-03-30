package com.hanghae.theham.domain.auth.controller;

import com.hanghae.theham.domain.auth.dto.MemberDTO;
import com.hanghae.theham.domain.auth.service.GoogleService;
import com.hanghae.theham.domain.auth.service.KakaoService;
import com.hanghae.theham.domain.auth.service.NaverService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("login")
public class OAuthController {
    private final KakaoService kakaoService;
    private final NaverService naverService;
    private final GoogleService googleService;

    public OAuthController(KakaoService kakaoService, NaverService naverService, GoogleService googleService) {
        this.kakaoService = kakaoService;
        this.naverService = naverService;
        this.googleService = googleService;
    }

    @GetMapping("/kakao-login")
    public String kakaoLogin() {
        return String.format("redirect:%s", kakaoService.getKakaoLogin());
    }

    @GetMapping("/kakao-callback")
    @ResponseBody
    public MemberDTO kakaoCallback(HttpServletRequest request) throws Exception {
        return kakaoService.getKakaoInfo(request.getParameter("code"));
    }

    @GetMapping("/naver-login")
    public String naverLogin() {
        return String.format("redirect:%s", naverService.getNaverLogin());
    }

    @GetMapping("/naver-callback")
    @ResponseBody
    public MemberDTO naverCallback(HttpServletRequest request) throws Exception {
        return naverService.getNaverInfo(request.getParameter("code"));
    }

    @GetMapping("/google-login")
    public String googleLogin() {
        return String.format("redirect:%s", googleService.getGoogleLogin());
    }

    @GetMapping("/google-callback")
    @ResponseBody
    public MemberDTO googleCallback(HttpServletRequest request) throws Exception {

        return googleService.getGoogleInfo(request.getParameter("code"));
    }


}
