package com.hanghae.theham.domain.chat.entity;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "chat_tbl")
public class Chat extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    public Chat(String content, Member sender, Member receiver) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
    }
}
