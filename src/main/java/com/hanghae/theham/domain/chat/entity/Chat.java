package com.hanghae.theham.domain.chat.entity;

import com.hanghae.theham.domain.chat.entity.type.VisibleType;
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
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Column(name = "is_read")
    private boolean isRead = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @Column
    private VisibleType visible = VisibleType.BOTH;

    @Builder
    public Chat(String message, ChatRoom chatRoom, Member sender, boolean isRead) {
        this.message = message;
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.isRead = isRead;
    }

    public void updateIsRead() {
        this.isRead = true;
    }

    public void updateChatVisible(VisibleType visible) {
        this.visible = visible;
    }
}
