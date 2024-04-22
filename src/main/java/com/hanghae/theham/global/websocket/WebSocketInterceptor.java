package com.hanghae.theham.global.websocket;

import com.hanghae.theham.global.exception.ErrorCode;
import com.hanghae.theham.global.jwt.TokenProvider;
import com.hanghae.theham.global.security.UserDetailsServiceImpl;
import com.hanghae.theham.global.websocket.exception.WebSocketException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j(topic = "WebSocketInterceptor")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketInterceptor implements ChannelInterceptor {
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public WebSocketInterceptor(TokenProvider tokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            setAuthenticate(accessor);
        }
        return message;
    }

    private void setAuthenticate(final StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader(TokenProvider.AUTHORIZATION_HEADER);

        if (bearerToken == null || !(StringUtils.hasText(bearerToken) && bearerToken.startsWith(TokenProvider.BEARER_PREFIX))) {
            throw new WebSocketException(ErrorCode.MEMBER_NOT_LOGIN.getMessage(), HttpStatus.FORBIDDEN);
        }

        String accessToken = bearerToken.substring(TokenProvider.BEARER_PREFIX.length());
        validateToken(accessToken);

        String memberEmail = tokenProvider.getTokenEmail(accessToken);
        log.info("소켓 CONNECT 시도, 유저 이메일 : {}", memberEmail);

        Authentication authentication = createAuthentication(memberEmail);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        accessor.setUser(authentication);
    }

    private Authentication createAuthentication(final String email) {
        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    private void validateToken(String accessToken) {
        try {
            if (!tokenProvider.getTokenType(accessToken).equals("access")) {
                throw new WebSocketException(ErrorCode.EXPIRED_ACCESS_TOKEN.getMessage(), HttpStatus.UNAUTHORIZED);
            }
            if (tokenProvider.isExpired(accessToken)) {
                throw new WebSocketException(ErrorCode.EXPIRED_ACCESS_TOKEN.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        } catch (ExpiredJwtException e) {
            throw new WebSocketException(ErrorCode.EXPIRED_ACCESS_TOKEN.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            throw new WebSocketException(ErrorCode.INVALID_ACCESS_TOKEN.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}