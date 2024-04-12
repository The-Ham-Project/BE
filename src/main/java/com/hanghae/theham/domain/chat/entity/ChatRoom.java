package com.hanghae.theham.domain.chat.entity;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "chat_room_tbl")
//@EntityListeners(AuditingEntityListener.class)
public class ChatRoom extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("채팅방 생성시 최초의 메시지 발신자, 채팅하기를 시작하는 사람")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member sender;

    @Column
    private int senderUnreadCount;

    @Column
    private Boolean senderIsDeleted = Boolean.FALSE;

    @Comment("채팅방 생성시 최초의 메시지 수신자, 함께쓰기 게시글 작성자")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member receiver;

    @Column
    private int receiverUnreadCount;

    @Column
    private Boolean receiverIsDeleted = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    private Rental rental;

    @Column(name = "last_chat")
    private String lastChat;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<Chat> chatList = new ArrayList<>();

    @Builder
    public ChatRoom(Member sender, Member receiver, Rental rental) {
        this.sender = sender;
        this.receiver = receiver;
        this.rental = rental;
    }

    public void updateChatRoom(Boolean isSender) {
        if (isSender) {
            this.senderUnreadCount = 0;
            return;
        }
        this.receiverUnreadCount = 0;
    }

    public void updateChatRoom(Boolean isSender, String lastChat, int currentMemberCount) {
        this.lastChat = lastChat;

        if (currentMemberCount == 2) {
            this.senderUnreadCount = 0;
            this.receiverUnreadCount = 0;
            return;
        }
        if (isSender) {
            this.senderUnreadCount = 0;
            this.receiverUnreadCount += 1;
            return;
        }
        this.receiverUnreadCount = 0;
        this.senderUnreadCount += 1;
    }
}
