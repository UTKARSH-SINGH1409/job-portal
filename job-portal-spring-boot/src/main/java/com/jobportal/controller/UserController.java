package com.jobportal.controller;

import com.jobportal.entity.User;
import com.jobportal.security.JwtUtil;
import com.jobportal.security.UserDetailsImpl;
import com.jobportal.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User userRequest, HttpServletResponse response) {
        if (userService.findByEmail(userRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email is already registered!"));
        }

        User user = userService.registerUser(userRequest);
        
        String jwt = jwtUtil.generateTokenFromEmail(user.getEmail());
        setTokenCookie(response, jwt);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "User Registered!",
            "user", user
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest, HttpServletResponse response) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        String role = loginRequest.get("role");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findByEmail(email).get();

        if (!user.getRole().equals(role)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("success", false, "message", "User with this role not found!")
            );
        }

        setTokenCookie(response, jwt);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "User logged in successfully!",
            "user", user
        ));
    }

    @GetMapping("/getuser")
    public ResponseEntity<?> getUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "User not authenticated"));
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findById(userDetails.getId()).orElse(null);
        
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "User not found"));
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "user", user
        ));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("success", true, "message", "User logged out successfully!"));
    }
    
    private void setTokenCookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie("token", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 day
        response.addCookie(cookie);
    }
}
