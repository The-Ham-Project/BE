package com.hanghae.theham.domain.chat.controller;

import com.hanghae.theham.domain.chat.controller.docs.ChatControllerDocs;
import com.hanghae.theham.domain.chat.dto.ChatRequestDto.ChatSendMessageRequestDto;
import com.hanghae.theham.domain.chat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController implements ChatControllerDocs {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    @MessageMapping("/chat/talk/{roomId}")
   public void sendMessage(@Valid @Payload ChatSendMessageRequestDto requestDto,
                           @DestinationVariable Long roomId){
        chatService.saveMessage(requestDto, roomId);
        messagingTemplate.convertAndSend("/sub/chat/chatRoom/"+roomId, requestDto);
   }
}
