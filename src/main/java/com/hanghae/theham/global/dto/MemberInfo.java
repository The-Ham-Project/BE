package com.hanghae.theham.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberInfo {

    private final String type;
    private final String email;
    private final String role;
}
