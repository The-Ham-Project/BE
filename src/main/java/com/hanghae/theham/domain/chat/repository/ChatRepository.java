package com.hanghae.theham.domain.chat.repository;

import com.hanghae.theham.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
