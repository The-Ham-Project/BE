package com.hanghae.theham.domain.chat.repository;

import com.hanghae.theham.domain.chat.entity.Chat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Page<Chat> findByChatRoom(ChatRoom chatRoom, PageRequest pageRequest);
}
