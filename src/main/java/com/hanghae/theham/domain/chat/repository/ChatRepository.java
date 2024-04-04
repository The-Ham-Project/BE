package com.hanghae.theham.domain.chat.repository;

import com.hanghae.theham.domain.chat.dto.ChatResponseDto;
import com.hanghae.theham.domain.chat.entity.Chat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByChatRoomOrderByIdDesc(ChatRoom chatRoom);
}
