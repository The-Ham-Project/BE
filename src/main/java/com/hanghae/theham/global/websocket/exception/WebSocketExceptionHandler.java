package com.hanghae.theham.global.websocket.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.theham.global.exception.ErrorCode;
import com.hanghae.theham.global.websocket.dto.MessageErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@Slf4j
public class WebSocketExceptionHandler extends StompSubProtocolErrorHandler {
    private static final byte[] EMPTY_PAYLOAD = new byte[0];

    public WebSocketExceptionHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        log.info("handleClientMessageProcessingError: {}", ex.getMessage());

        final Throwable exception = converterTrowException(ex);

        if (exception != null) {
            final ErrorCode errorCode = ((WebSocketException) exception).getCode();
            return handleWebSocketException(clientMessage, errorCode);
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Throwable converterTrowException(final Throwable exception) {
        if (exception instanceof MessageDeliveryException) {
            return exception.getCause();
        }
        return exception;
    }

    private Message<byte[]> handleWebSocketException(Message<byte[]> clientMessage, ErrorCode errorCode) {
        return prepareErrorMessage(clientMessage, errorCode);
    }

    private Message<byte[]> prepareErrorMessage(Message<byte[]> clientMessage, ErrorCode errorCode) {
        // SET response Dto
        MessageErrorResponse messageErrorResponse = MessageErrorResponse.response(errorCode);

        // SET Header
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setLeaveMutable(true);
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);

        setReceiptIdForClient(clientMessage, accessor);

        // BINDING toJSONString
        ObjectMapper objectMapper = new ObjectMapper();
        String messageErrorResponseJSON = null;
        try {
            messageErrorResponseJSON = objectMapper.writeValueAsString(messageErrorResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return MessageBuilder.createMessage(
                messageErrorResponseJSON != null ? messageErrorResponseJSON.getBytes(StandardCharsets.UTF_8) : EMPTY_PAYLOAD,
                accessor.getMessageHeaders());
    }

    private void setReceiptIdForClient(final Message<byte[]> clientMessage, final StompHeaderAccessor accessor) {

        if (Objects.isNull(clientMessage)) {
            return;
        }

        final StompHeaderAccessor clientHeaderAccessor = MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);

        final String receiptId =
                Objects.isNull(clientHeaderAccessor) ? null : clientHeaderAccessor.getReceipt();

        if (receiptId != null) {
            accessor.setReceiptId(receiptId);
        }
    }

    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor,
                                             byte[] errorPayload,
                                             Throwable cause,
                                             StompHeaderAccessor clientHeaderAccessor) {

        return MessageBuilder.createMessage(errorPayload, errorHeaderAccessor.getMessageHeaders());
    }
}