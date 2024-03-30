package com.hanghae.theham.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MemberDTO {
    private String id;
    private String email;
    private String nickname;
}
