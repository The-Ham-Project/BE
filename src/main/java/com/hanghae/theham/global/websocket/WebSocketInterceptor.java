package com.hanghae.theham.global.websocket;

import com.hanghae.theham.global.jwt.TokenProvider;
import com.hanghae.theham.global.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
public class WebSocketInterceptor implements ChannelInterceptor {
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public WebSocketInterceptor(TokenProvider tokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor.getCommand() == StompCommand.CONNECT) {

            String bearerToken = accessor.getFirstNativeHeader(TokenProvider.AUTHORIZATION_HEADER);

            if (bearerToken == null && bearerToken.isEmpty()) {
                throw new AuthException("Authentication failed!!");
            }

            if (!(StringUtils.hasText(bearerToken) && bearerToken.startsWith(TokenProvider.BEARER_PREFIX))) {
                throw new AuthException("Authentication failed!!");
            }

            String accessToken = bearerToken.substring(TokenProvider.BEARER_PREFIX.length());

            if (!tokenProvider.getTokenType(accessToken).equals("access") || tokenProvider.isExpired(accessToken)) {
                throw new AuthException("Authentication failed!!");
            }

            Claims claims = tokenProvider.getMemberInfoFromToken(accessToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(claims.get("email").toString());

            // UsernamePasswordAuthenticationToken 발급
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // accessor에 등록
            accessor.setUser(authentication);
        }
        return message;
    }
}
