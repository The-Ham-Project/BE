package com.hanghae.theham.domain.chat.controller.docs;

import com.hanghae.theham.domain.chat.dto.ChatRequestDto.ChatSendMessageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;

@Tag(name = "chats", description = "채팅 메세지 관련 API")
public interface ChatControllerDocs {

    @Operation(summary = "채팅 메세지 전송 기능", description = "채팅 메세지를 전송할 수 있는 API")
    void sendMessage(@Valid @Payload ChatSendMessageRequestDto requestDto,
                     @DestinationVariable Long roomId);
}
