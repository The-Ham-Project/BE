package com.hanghae.theham.domain.rental.entity.type;

import lombok.Getter;

@Getter
public enum CategoryType {

    ALL("전체"),
    HOUSEHOLD("생활용품"),
    KITCHEN("주방용품"),
    CLOSET("의류"),
    ELECTRONIC("전자제품"),
    BOOK("도서"),
    PLACE("장소"),
    OTHER("기타"),
    ;

    private final String value;

    CategoryType(String value) {
        this.value = value;
    }
}
