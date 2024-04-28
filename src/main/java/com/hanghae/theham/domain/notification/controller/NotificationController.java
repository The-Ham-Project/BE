package com.hanghae.theham.domain.notification.controller;

import com.hanghae.theham.domain.notification.controller.docs.NotificationControllerDocs;
import com.hanghae.theham.domain.notification.service.NotificationService;
import com.hanghae.theham.global.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j(topic = "알림 컨트롤러 동작")
@RestController
@RequestMapping("/api/v1")
public class NotificationController implements NotificationControllerDocs {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(value = "/sse", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        log.info("연결시도 연결시도>>>>>>>>>>>>>>>>>> userDetails : {}", userDetails.getUsername());
        return notificationService.subscribe(userDetails.getUsername());
    }
}