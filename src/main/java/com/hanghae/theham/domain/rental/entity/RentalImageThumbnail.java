package com.hanghae.theham.domain.rental.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "rental_image_thumbnail_tbl")
public class RentalImageThumbnail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String imagePath;

    @Column
    private String thumbnailPath;

    @Builder
    public RentalImageThumbnail(String imagePath, String thumbnailPath) {
        this.imagePath = imagePath;
        this.thumbnailPath = thumbnailPath;
    }
}
