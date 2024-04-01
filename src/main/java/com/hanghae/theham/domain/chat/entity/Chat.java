package com.hanghae.theham.domain.chat.entity;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @Column(name = "is_read", columnDefinition = "boolean default false")
    private boolean isRead;

    @Builder
    public Chat(String content, ChatRoom chatRoom, Member sender, Member receiver, boolean isRead) {
        this.content = content;
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.receiver = receiver;
        this.isRead = isRead;
    }

}
