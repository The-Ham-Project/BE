package com.hanghae.theham.domain.notification.controller.docs;

import com.hanghae.theham.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "notifications", description = "알림 관련 API")
public interface NotificationControllerDocs {

    @Operation(summary = "실시간 알림 수신을 위한 SSE 연결", description = "클라이언트와 서버의 SSE 통신으로 실시간으로 알림을 수신하는 API")
    SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                         @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId);
}