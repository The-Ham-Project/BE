package com.hanghae.theham.domain.rental.entity.type;

import lombok.Getter;

@Getter
public enum CategoryType {

    HOUSEHOLD("생활용품"),
    ELECTRONIC("전자제품"),
    KITCHEN("주방용품"),
    CLOSET("옷장"),
    BOOK("도서"),
    PLACE("장소"),
    ;

    private final String value;

    CategoryType(String value) {
        this.value = value;
    }
}
