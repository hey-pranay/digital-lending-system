
package com.sprint.lending.service;

import com.sprint.lending.dto.request.RegisterRequest;
import com.sprint.lending.dto.request.LoginRequest;
import com.sprint.lending.dto.response.AuthResponse;

import com.sprint.lending.entity.Role;
import com.sprint.lending.entity.User;

import com.sprint.lending.repository.UserRepository;
import com.sprint.lending.security.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Constructor injection
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // User Registration
    public AuthResponse register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }


        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User();

        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(Role.BORROWER);

        userRepository.save(user);

        return new AuthResponse(
                "Registration successful",
                user.getEmail(),
                user.getRole().name()
        );
    }

    // User Login
// User Login
    public AuthResponse login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        // Verify password using BCrypt
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        // Return authentication response
        return new AuthResponse(
                "Login successful",
                user.getEmail(),
                user.getRole().name(),
                token
        );
    }
}
