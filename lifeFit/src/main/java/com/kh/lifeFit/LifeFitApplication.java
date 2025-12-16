package com.kh.lifeFit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@EnableRetry
@SpringBootApplication
public class LifeFitApplication {

	public static void main(String[] args) {

		SpringApplication.run(LifeFitApplication.class, args);
	}

}
