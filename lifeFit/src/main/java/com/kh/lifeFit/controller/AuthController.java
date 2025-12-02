package com.kh.lifeFit.controller;

import com.kh.lifeFit.dto.auth.LoginRequest;
import com.kh.lifeFit.dto.auth.LoginResponse;
import com.kh.lifeFit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://192.168.20.60:5173"
})
public class AuthController {
    private final AuthService  authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {

            return ResponseEntity.ok(authService.login(loginRequest));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
