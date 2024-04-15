package com.hanghae.theham.domain.chat.repository;

import com.hanghae.theham.domain.chat.entity.Chat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.chat.entity.type.VisibleType;
import com.hanghae.theham.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.chatRoom = :chatRoom AND (c.visible = :visibleBoth OR c.visible = :visibleMember)")
    Page<Chat> findByChatRoomAndVisibleIn(ChatRoom chatRoom, VisibleType visibleBoth, VisibleType visibleMember, PageRequest pageRequest);

    List<Chat> findByChatRoomAndSenderAndIsRead(ChatRoom chatRoom, Member sender, boolean isRead);

    List<Chat> findByChatRoomAndVisible(ChatRoom chatRoom, VisibleType visibleType);
}
