package com.hanghae.theham.domain.chat.repository;

import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.rental.entity.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findChatRoomBySenderAndRental(Member sender, Rental rental);

    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.sender = :member OR cr.receiver = :member)")
    Page<ChatRoom> findChatRoomByMember(Member member, Pageable pageable);
}
