package com.hanghae.theham.domain.chat.controller;

import com.hanghae.theham.domain.chat.controller.docs.ChatRoomControllerDocs;
import com.hanghae.theham.domain.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomDetailResponseDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomReadResponseDto;
import com.hanghae.theham.domain.chat.service.ChatRoomService;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "ChatController")
@RequestMapping("/api/v1")
@RestController
public class ChatRoomController implements ChatRoomControllerDocs {

    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/chat-rooms")
    public ResponseDto<Long> createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ChatRoomCreateRequestDto requestDto
    ) {
        Long roomId = chatRoomService.handleChatRoom(userDetails.getUsername(), requestDto);
        return ResponseDto.success("채팅방 생성 및 기존 채팅방 반환 기능", roomId);
    }

    @GetMapping("/chat-rooms")
    public ResponseDto<List<ChatRoomReadResponseDto>> getChatRoomList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        List<ChatRoomReadResponseDto> responseDtoList = chatRoomService.getChatRoomList(userDetails.getUsername(), page, size);
        return ResponseDto.success("회원 자신의 채팅방 목록 조회 기능", responseDtoList);
    }

    @GetMapping("/chat-rooms/{chatRoomId}")
    public ResponseDto<ChatRoomDetailResponseDto> getChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long chatRoomId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        ChatRoomDetailResponseDto responseDto = chatRoomService.getChatRoom(userDetails.getUsername(), chatRoomId, page, size);
        return ResponseDto.success("채팅방 상세 조회 기능", responseDto);
    }
}
