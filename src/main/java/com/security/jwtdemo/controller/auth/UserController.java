package com.security.jwtdemo.controller.auth;

import com.security.jwtdemo.entity.mysql.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<String> getMyProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized access");
        }

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok("Hello " + currentUser.getFirstname() + " " + currentUser.getLastname()
                + ", Your email is: " + currentUser.getEmail()
                + " and Role is: " + currentUser.getRole());
    }

    @GetMapping("/admin-only")
    public ResponseEntity<String> adminOnlyData() {
        return ResponseEntity.ok("Welcome Admin! This is secret admin data.");
    }
}