package com.fitness;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenAdminHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin123");
        System.out.println(hash);
        System.out.println("Verify: " + encoder.matches("admin123", hash));
    }
}
