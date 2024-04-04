package com.hanghae.theham.domain.chat.entity;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.rental.entity.Rental;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Member seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private Member buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Rental rental;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<Chat> chatList = new ArrayList<>();

    @Builder
    public ChatRoom(Member seller, Member buyer, Rental rental) {
        this.seller = seller;
        this.buyer = buyer;
        this.rental = rental;
    }

}
