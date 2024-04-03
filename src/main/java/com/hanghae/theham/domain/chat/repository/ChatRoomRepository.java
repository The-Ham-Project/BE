package com.hanghae.theham.domain.chat.repository;

import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {

    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.memberA = :requester AND cr.memberB = :requestedMember) OR (cr.memberA = :requestedMember AND cr.memberB = :requester)")
    ChatRoom findByMembers(Member requester, Member requestedMember);
}
