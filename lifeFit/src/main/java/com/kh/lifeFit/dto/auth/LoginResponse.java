package com.kh.lifeFit.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private Long userId;
    private String email;
    private String accessToken;
    private String message;
}

