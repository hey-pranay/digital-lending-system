
package com.sprint.lending.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.lending.dto.request.LoginRequest;
import com.sprint.lending.dto.request.RegisterRequest;
import com.sprint.lending.dto.response.AuthResponse;
import com.sprint.lending.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private AuthService authService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        authService = mock(AuthService.class);

        AuthController authController =
                new AuthController(authService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse(
                "Registration successful",
                "test@example.com",
                "BORROWER"
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Registration successful"))
                .andExpect(jsonPath("$.email")
                        .value("test@example.com"))
                .andExpect(jsonPath("$.role")
                        .value("BORROWER"));

        verify(authService, times(1))
                .register(any(RegisterRequest.class));
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse(
                "Login successful",
                "test@example.com",
                "BORROWER",
                "sample-jwt-token"
        );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Login successful"))
                .andExpect(jsonPath("$.email")
                        .value("test@example.com"))
                .andExpect(jsonPath("$.role")
                        .value("BORROWER"))
                .andExpect(jsonPath("$.token")
                        .value("sample-jwt-token"));

        verify(authService, times(1))
                .login(any(LoginRequest.class));
    }
}
