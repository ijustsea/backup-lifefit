package com.kh.lifeFit.service;

import com.kh.lifeFit.domain.user.User;
import com.kh.lifeFit.dto.auth.LoginRequest;
import com.kh.lifeFit.dto.auth.LoginResponse;
import com.kh.lifeFit.jwt.JwtTokenProvider;
import com.kh.lifeFit.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        // ⭐ UserType → ROLE_ 변환
        String role = "ROLE_" + user.getType().name();

        // ⭐ JWT 생성 (role 포함!)
        String accessToken =
                jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), role);

        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(role)               // ★ 프론트가 바로 사용!
                .accessToken(accessToken)
                .message("로그인성공")
                .build();
    }
}
