package com.hanghae.theham.domain.chat.controller.docs;

import com.hanghae.theham.domain.chat.dto.ChatRequestDto.ChatRoomCreateRequestDto;
import com.hanghae.theham.domain.chat.dto.ChatResponseDto.ChatRoomCreateResponseDto;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "chats", description = "채팅 관련 API")
public interface ChatControllerDocs {

    @Operation(summary = "채팅방 생성 기능", description = "채팅방을 생성할 수 있는 API")
    ResponseDto<ChatRoomCreateResponseDto> createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ChatRoomCreateRequestDto requestDto
    );


}
