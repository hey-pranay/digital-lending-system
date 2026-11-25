package com.sprint.lending.service;

import com.sprint.lending.dto.request.LoginRequest;
import com.sprint.lending.dto.request.RegisterRequest;
import com.sprint.lending.dto.response.AuthResponse;
import com.sprint.lending.entity.Role;
import com.sprint.lending.entity.User;
import com.sprint.lending.repository.UserRepository;
import com.sprint.lending.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {

        registerRequest = new RegisterRequest();
        registerRequest.setName("Test Borrower");
        registerRequest.setEmail("borrower@gmail.com");
        registerRequest.setPassword("Password@123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("borrower@gmail.com");
        loginRequest.setPassword("Password@123");

        user = new User();
        user.setName("Test Borrower");
        user.setEmail("borrower@gmail.com");
        user.setPassword("encoded-password");
        user.setRole(Role.BORROWER);
    }

    // Test 1: Successful registration
    @Test
    void register_ShouldRegisterBorrowerSuccessfully() {

        when(userRepository.existsByEmail("borrower@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password@123"))
                .thenReturn("encoded-password");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("Test Borrower", savedUser.getName());
        assertEquals("borrower@gmail.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals(Role.BORROWER, savedUser.getRole());

        verify(passwordEncoder).encode("Password@123");
    }

    // Test 2: Duplicate email registration
    @Test
    void register_ShouldThrow409_WhenEmailAlreadyExists() {

        when(userRepository.existsByEmail("borrower@gmail.com"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    // Test 3: Successful login
    @Test
    void login_ShouldReturnJwtToken_WhenCredentialsAreValid() {

        when(userRepository.findByEmail("borrower@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "Password@123",
                "encoded-password"
        )).thenReturn(true);

        when(jwtUtil.generateToken(user))
                .thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);

        verify(passwordEncoder).matches(
                "Password@123",
                "encoded-password"
        );

        verify(jwtUtil).generateToken(user);
    }

    // Test 4: Incorrect password
    @Test
    void login_ShouldThrow401_WhenPasswordIsIncorrect() {

        when(userRepository.findByEmail("borrower@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "Password@123",
                "encoded-password"
        )).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatusCode()
        );

        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    // Test 5: Email does not exist
    @Test
    void login_ShouldThrow401_WhenEmailDoesNotExist() {

        when(userRepository.findByEmail("borrower@gmail.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatusCode()
        );

        verify(passwordEncoder, never()).matches(
                anyString(),
                anyString()
        );

        verify(jwtUtil, never()).generateToken(any(User.class));
    }
}
