package com.hanghae.theham.domain.rental.entity;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import com.hanghae.theham.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "rental_tbl")
@DynamicUpdate
@SQLDelete(sql = "UPDATE rental_tbl SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Rental extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType category;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column
    private long rentalFee;

    @Column
    private long deposit;

    @Column
    private boolean isDeleted = Boolean.FALSE;

    @Column
    private double latitude; // 위도

    @Column
    private double longitude; // 경도

    @Column
    private String district; // 지역구

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Rental(CategoryType category, String title, String content, long rentalFee, long deposit, boolean isDeleted, double latitude, double longitude, String district, Member member) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.rentalFee = rentalFee;
        this.deposit = deposit;
        this.isDeleted = isDeleted;
        this.latitude = latitude;
        this.longitude = longitude;
        this.district = district;
        this.member = member;
    }

    public void update(String title, CategoryType category, String content, Long rentalFee, Long deposit, double latitude, double longitude, String district) {
        this.title = title;
        this.category = category;
        this.content = content;
        this.rentalFee = rentalFee;
        this.deposit = deposit;
        this.latitude = latitude;
        this.longitude = longitude;
        this.district = district;
    }
}
