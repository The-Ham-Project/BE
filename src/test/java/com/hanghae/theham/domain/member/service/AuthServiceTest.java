package com.hanghae.theham.domain.member.service;

import com.hanghae.theham.domain.member.entity.type.RoleType;
import com.hanghae.theham.domain.member.repository.RefreshTokenRepository;
import com.hanghae.theham.global.exception.ErrorCode;
import com.hanghae.theham.global.exception.TokenException;
import com.hanghae.theham.global.jwt.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @DisplayName("성공 - 리프레쉬 토큰을 이용해서 토큰 재발행")
    @Test
    void reissue_01() {
        // given
        String refreshToken = "refresh_token";
        String email = "test@test.com";
        String role = RoleType.ROLE_USER.name();
        String newAccessToken = "new_access_token";
        String newRefreshToken = "new_refresh_token";

        // when
        when(tokenProvider.getRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(tokenProvider.getTokenType(refreshToken)).thenReturn("refresh");
        when(refreshTokenRepository.existsById(refreshToken)).thenReturn(true);
        when(tokenProvider.getTokenEmail(refreshToken)).thenReturn(email);
        when(tokenProvider.getTokenRole(refreshToken)).thenReturn(role);
        when(tokenProvider.createAccessToken(email, role)).thenReturn(newAccessToken);
        when(tokenProvider.createRefreshToken(email, role)).thenReturn(newRefreshToken);

        authService.reissue(request, response);

        // then
        verify(response).addHeader(TokenProvider.AUTHORIZATION_HEADER, newAccessToken);
        verify(tokenProvider).addRefreshTokenToCookie(newRefreshToken, response);
        verify(refreshTokenRepository).deleteById(refreshToken);
    }

    @DisplayName("실패 - 리프레쉬 토큰 값이 null일 경우 재발행")
    @Test
    void reissue_02() {
        // when & then
        when(tokenProvider.getRefreshTokenFromCookie(request)).thenReturn(null);

        TokenException exception = Assertions.assertThrows(TokenException.class, () ->
                authService.reissue(request, response)
        );

        Assertions.assertEquals(ErrorCode.NOT_FOUND_REFRESH_TOKEN.getMessage(), exception.getMessage());
    }

    @DisplayName("실패 - 리프레쉬 토큰 타입이 맞지 않을 경우 재발행")
    @Test
    void reissue_03() {
        // given
        String refreshToken = "refresh_token";

        // when & then
        when(tokenProvider.getRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(tokenProvider.getTokenType(refreshToken)).thenReturn("access");

        TokenException exception = Assertions.assertThrows(TokenException.class, () ->
                authService.reissue(request, response)
        );

        Assertions.assertEquals(ErrorCode.INVALID_REFRESH_TOKEN.getMessage(), exception.getMessage());
    }

    @DisplayName("실패 - 리프레쉬 토큰을 DB에서 찾을 수 없는 경우 재발행")
    @Test
    void reissue_04() {
        // given
        String refreshToken = "refresh_token";

        // when & then
        when(tokenProvider.getRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(tokenProvider.getTokenType(refreshToken)).thenReturn("refresh");
        when(refreshTokenRepository.existsById(refreshToken)).thenReturn(false);

        TokenException exception = Assertions.assertThrows(TokenException.class, () ->
                authService.reissue(request, response)
        );

        Assertions.assertEquals(ErrorCode.NOT_FOUND_REFRESH_TOKEN.getMessage(), exception.getMessage());
    }

    @DisplayName("성공 - 리프레쉬 토큰으로 로그아웃")
    @Test
    void logout_01() {
        // given
        String refreshToken = "refresh_token";

        // when
        when(tokenProvider.getRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(tokenProvider.getTokenType(refreshToken)).thenReturn("refresh");
        when(refreshTokenRepository.existsById(refreshToken)).thenReturn(true);

        authService.logout(request, response);

        // then
        verify(refreshTokenRepository).deleteById(refreshToken);
        verify(response).addCookie(any(Cookie.class));
    }

    @DisplayName("실패 - 리프레쉬 토큰 값이 null일 경우 로그아웃")
    @Test
    void logout_02() {
        // when & then
        when(tokenProvider.getRefreshTokenFromCookie(request)).thenReturn(null);

        TokenException exception = Assertions.assertThrows(TokenException.class, () ->
                authService.logout(request, response)
        );

        Assertions.assertEquals(ErrorCode.NOT_FOUND_REFRESH_TOKEN.getMessage(), exception.getMessage());
    }

    @DisplayName("실패 - 리프레쉬 토큰 타입이 맞지 않을 경우 로그아웃")
    @Test
    void logout_03() {
        // given
        String refreshToken = "refresh_token";

        // when & then
        when(tokenProvider.getRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(tokenProvider.getTokenType(refreshToken)).thenReturn("access");

        TokenException exception = Assertions.assertThrows(TokenException.class, () ->
                authService.logout(request, response)
        );

        Assertions.assertEquals(ErrorCode.INVALID_REFRESH_TOKEN.getMessage(), exception.getMessage());
    }

    @DisplayName("실패 - 리프레쉬 토큰을 DB에서 찾을 수 없는 경우 로그아웃")
    @Test
    void logout_04() {
        // given
        String refreshToken = "refresh_token";

        // when & then
        when(tokenProvider.getRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(tokenProvider.getTokenType(refreshToken)).thenReturn("refresh");
        when(refreshTokenRepository.existsById(refreshToken)).thenReturn(false);

        TokenException exception = Assertions.assertThrows(TokenException.class, () ->
                authService.logout(request, response)
        );

        Assertions.assertEquals(ErrorCode.NOT_FOUND_REFRESH_TOKEN.getMessage(), exception.getMessage());
    }
}