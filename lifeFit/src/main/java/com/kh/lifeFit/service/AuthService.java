package com.kh.lifeFit.service;

import com.kh.lifeFit.domain.user.User;
import com.kh.lifeFit.dto.auth.LoginRequest;
import com.kh.lifeFit.dto.auth.LoginResponse;
import com.kh.lifeFit.jwt.JwtTokenProvider;
import com.kh.lifeFit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest loginRequest) {


        System.out.println("⭐ 입력된 이메일 = " + loginRequest.getEmail());
        System.out.println("⭐ 입력된 비밀번호 = [" + loginRequest.getPassword() + "]");


        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        System.out.println("⭐ matches 검사 = "
                + new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A)
                .matches("password", user.getPassword()));
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());

        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .accessToken(accessToken)
                .message("로그인성공")
                .build();
    }
}
