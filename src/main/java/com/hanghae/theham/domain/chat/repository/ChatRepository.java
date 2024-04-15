package com.hanghae.theham.domain.chat.repository;

import com.hanghae.theham.domain.chat.entity.Chat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.chat.entity.type.VisibleType;
import com.hanghae.theham.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Page<Chat> findByChatRoom(ChatRoom chatRoom, PageRequest pageRequest);

    List<Chat> findByChatRoomAndSenderAndIsRead(ChatRoom chatRoom, Member sender, boolean isRead);

    List<Chat> findByChatRoomAndVisible(ChatRoom chatRoom, VisibleType visibleType);
}
