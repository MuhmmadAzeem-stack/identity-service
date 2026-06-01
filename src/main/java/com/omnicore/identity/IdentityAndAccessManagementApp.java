package com.omnicore.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IdentityAndAccessManagementApp {

  public static void main(String[] args) {
    SpringApplication.run(IdentityAndAccessManagementApp.class, args);
    //		System.out.println(new BCryptPasswordEncoder().encode("admin123"));
  }
}
