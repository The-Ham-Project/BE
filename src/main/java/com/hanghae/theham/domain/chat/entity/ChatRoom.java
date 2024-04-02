package com.hanghae.theham.domain.chat.entity;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "chat_room_tbl")
public class ChatRoom extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_a_id")
    private Member memberA;

    @ManyToOne
    @JoinColumn(name = "member_b_id")
    private Member memberB;


    @Column(columnDefinition = "TEXT")
    private String lastChat;

    @Builder
    public ChatRoom(Member memberA, Member memberB,  String lastChat) {
        this.memberA = memberA;
        this.memberB = memberB;
        this.lastChat = lastChat;
    }

}
