package com.kh.lifeFit.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender; // FEMALE, MALE

    private int age;

    @Enumerated(EnumType.STRING)
    private UserType type; // EMPLOYEE, HR_MANAGER, SYS_ADMIN

}
