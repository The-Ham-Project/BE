package com.hanghae.theham.domain.chat.controller;

import com.hanghae.theham.domain.chat.controller.docs.ChatRoomControllerDocs;
import com.hanghae.theham.domain.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomCreateResponseDto;
import com.hanghae.theham.domain.chat.service.ChatRoomService;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "chatController")
@RequestMapping("/api/v1")
@RestController
public class ChatRoomController implements ChatRoomControllerDocs {

    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/chat-rooms")
    public ResponseDto<ChatRoomCreateResponseDto> createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ChatRoomCreateRequestDto requestDto
    ) {
        ChatRoomCreateResponseDto responseDto = chatRoomService.createChatRoom(userDetails.getUsername(), requestDto);

        return ResponseDto.success("채팅 채팅방 생성 기능", responseDto);
    }
}
