package com.kh.lifeFit.jwt;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;           // 🔥 PK
    private final String email;          // username
    private final String password;       // encoded password
    private final List<GrantedAuthority> authorities; // ROLE_?

    // 2025.12.24 심박수 데이터 필요한 필드 추가
    private final int age;
    private final Gender gender;

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();

        // 🔥 UserType 자동 ROLE 변환 (EMPLOYEE → ROLE_EMPLOYEE)
        this.authorities = List.of(() -> "ROLE_" + user.getType().name());

        // 2025.12.24 심박수 데이터
        this.age = user.getAge();
        this.gender = user.getGender();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
