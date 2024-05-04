package com.hanghae.theham.global.websocket.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.theham.global.exception.ErrorCode;
import com.hanghae.theham.global.websocket.dto.MessageErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CustomMessageExceptionHandler {
    private final SimpMessageSendingOperations messageTemplate;

    public CustomMessageExceptionHandler(SimpMessageSendingOperations messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @MessageExceptionHandler(WebSocketException.class)
    public void CustomExceptionHandler(WebSocketException e, Principal principal)
            throws JsonProcessingException {
        //JSON으로 설정
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);

        // 응답 메세지 커스텀
        MessageErrorResponse messageErrorResponse = MessageErrorResponse.response(e.getCode());

        // JSON 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String messageErrorResponseJSON = objectMapper.writeValueAsString(messageErrorResponse);
        log.error("error response: " + messageErrorResponseJSON);

        // 전송
        messageTemplate.convertAndSendToUser(principal.getName(), "/queue/error", messageErrorResponseJSON);
    }

    /**
     * MethodArgumentNotValidException
     * 요청 dto가 유효성 검사에서 틀렸을 때 발생
     */
    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, Principal principal) throws JsonProcessingException {
        Map<String, String> errorMap = new HashMap<>();
        BindingResult result = e.getBindingResult();

        for (FieldError error : result.getFieldErrors()) {
            log.error("name:{}, message:{}", error.getField(), error.getDefaultMessage());
            errorMap.put(error.getField(), error.getDefaultMessage());
        }

        MessageErrorResponse response = MessageErrorResponse.response(ErrorCode.INVALID_INPUT_VALUE, errorMap);

        ObjectMapper objectMapper = new ObjectMapper();
        String messageErrorResponseJSON = objectMapper.writeValueAsString(response);
        log.error("error response: " + messageErrorResponseJSON);

        messageTemplate.convertAndSendToUser(principal.getName(), "/queue/error", messageErrorResponseJSON);
    }
}