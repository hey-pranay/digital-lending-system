
package com.sprint.lending.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.sprint.lending.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import({
        SecurityConfig.class,
        SecurityConfigTest.TestControllerConfiguration.class
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
        reset(jwtUtil);
    }

    // Test-only configuration to register test controller
    @TestConfiguration
    static class TestControllerConfiguration {

        @Bean
        TestController testController() {
            return new TestController();
        }
    }

    // Test endpoints
    @RestController
    static class TestController {

        @GetMapping("/api/auth/test")
        public String publicEndpoint() {
            return "Public endpoint";
        }

        @GetMapping("/api/admin/test")
        public String adminEndpoint() {
            return "Admin endpoint";
        }

        @GetMapping("/api/borrower/test")
        public String borrowerEndpoint() {
            return "Borrower endpoint";
        }

        @GetMapping("/api/private/test")
        public String privateEndpoint() {
            return "Authenticated endpoint";
        }
    }

    // Helper method to mock a valid JWT
    private void mockValidToken(
            String token,
            String email,
            String role) {

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        Claim roleClaim = mock(Claim.class);

        when(jwtUtil.verifyToken(token))
                .thenReturn(decodedJWT);

        when(decodedJWT.getSubject())
                .thenReturn(email);

        when(decodedJWT.getClaim("role"))
                .thenReturn(roleClaim);

        when(roleClaim.asString())
                .thenReturn(role);
    }

    @Test
    void shouldAllowPublicAuthEndpointWithoutToken()
            throws Exception {

        mockMvc.perform(get("/api/auth/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Public endpoint"));
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing()
            throws Exception {

        mockMvc.perform(get("/api/admin/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowBorrowerToAccessBorrowerEndpoint()
            throws Exception {

        mockValidToken(
                "borrower-token",
                "borrower@example.com",
                "BORROWER"
        );

        mockMvc.perform(get("/api/borrower/test")
                        .header("Authorization", "Bearer borrower-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Borrower endpoint"));
    }

    @Test
    void shouldDenyBorrowerAccessToAdminEndpoint()
            throws Exception {

        mockValidToken(
                "borrower-token",
                "borrower@example.com",
                "BORROWER"
        );

        mockMvc.perform(get("/api/admin/test")
                        .header("Authorization", "Bearer borrower-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToAccessAdminEndpoint()
            throws Exception {

        mockValidToken(
                "admin-token",
                "admin@example.com",
                "ADMIN"
        );

        mockMvc.perform(get("/api/admin/test")
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin endpoint"));
    }

    @Test
    void shouldDenyAdminAccessToBorrowerEndpoint()
            throws Exception {

        mockValidToken(
                "admin-token",
                "admin@example.com",
                "ADMIN"
        );

        mockMvc.perform(get("/api/borrower/test")
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRequireAuthenticationForOtherEndpoints()
            throws Exception {

        mockMvc.perform(get("/api/private/test"))
                .andExpect(status().isUnauthorized());
    }
}
