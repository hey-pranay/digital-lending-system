
package com.sprint.lending.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTest {

    private JwtUtil jwtUtil;
    private JwtFilter jwtFilter;
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        jwtFilter = new JwtFilter(jwtUtil);
        filterChain = mock(FilterChain.class);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing()
            throws Exception {

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);

        assertEquals(200, response.getStatus());
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsNotBearer()
            throws Exception {

        request.addHeader("Authorization", "Basic abc123");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);

        assertEquals(200, response.getStatus());
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid()
            throws Exception {

        request.addHeader("Authorization", "Bearer valid-token");

        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(jwtUtil.verifyToken("valid-token"))
                .thenReturn(decodedJWT);

        when(decodedJWT.getSubject())
                .thenReturn("borrower@example.com");

        when(decodedJWT.getClaim("role"))
                .thenReturn(mock(com.auth0.jwt.interfaces.Claim.class));

        when(decodedJWT.getClaim("role").asString())
                .thenReturn("BORROWER");

        jwtFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals("borrower@example.com",
                authentication.getPrincipal());

        assertTrue(authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_BORROWER")));

        verify(filterChain).doFilter(request, response);
    }


    @Test
    void shouldReturnUnauthorizedWhenJwtClaimsAreMissing()
            throws Exception {

        request.addHeader("Authorization", "Bearer invalid-claims-token");

        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        com.auth0.jwt.interfaces.Claim roleClaim =
                mock(com.auth0.jwt.interfaces.Claim.class);

        when(jwtUtil.verifyToken("invalid-claims-token"))
                .thenReturn(decodedJWT);

        // Email is missing
        when(decodedJWT.getSubject()).thenReturn(null);

        // Role claim exists, but its value is missing
        when(decodedJWT.getClaim("role")).thenReturn(roleClaim);
        when(roleClaim.asString()).thenReturn("BORROWER");

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalid()
            throws Exception {

        request.addHeader("Authorization", "Bearer invalid-token");

        when(jwtUtil.verifyToken("invalid-token"))
                .thenThrow(new JWTVerificationException("Invalid token"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());

        assertNull(SecurityContextHolder.getContext()
                .getAuthentication());

        verify(filterChain, never()).doFilter(request, response);
    }
}
