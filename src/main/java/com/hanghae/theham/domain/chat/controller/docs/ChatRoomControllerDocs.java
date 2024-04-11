package com.hanghae.theham.domain.chat.controller.docs;

import com.hanghae.theham.domain.chat.dto.ChatRoomRequestDto.ChatRoomCreateRequestDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomDetailResponseDto;
import com.hanghae.theham.domain.chat.dto.ChatRoomResponseDto.ChatRoomReadResponseDto;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "chat-rooms", description = "채팅방 관련 API")
public interface ChatRoomControllerDocs {

    @Operation(summary = "채팅방 생성 기능", description = "채팅방을 생성할 수 있는 API, 존재하는 채팅방이라면 기존의 채팅방을")
    ResponseDto<Long> createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ChatRoomCreateRequestDto requestDto
    );

    @Operation(summary = "나의 채팅방 목록 조회 기능", description = "나의 채팅방을 목록을 조회 할 수 있는 API")
    ResponseDto<ChatRoomReadResponseDto> getChatRoomList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    );

    @Operation(summary = "선택한 채팅방 조회 기능", description = "선택한 채팅방을 조회 할 수 있는 API")
    ResponseDto<ChatRoomDetailResponseDto> getChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long chatRoomId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    );
}
