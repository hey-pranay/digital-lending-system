package com.sprint.lending.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.sprint.lending.entity.Role;
import com.sprint.lending.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET =
            "test-secret-key-that-is-long-enough-for-hmac256";

    @BeforeEach
    void setUp() {

        jwtUtil = new JwtUtil();

        // Inject test values into @Value fields
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);

        ReflectionTestUtils.setField(
                jwtUtil,
                "expirationMs",
                86400000L
        );
    }

    // Test 1: Generate JWT successfully
    @Test
    void generateToken_ShouldGenerateValidJwt() {

        User user = new User();
        user.setEmail("borrower@gmail.com");
        user.setRole(Role.BORROWER);

        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());

        // A JWT has three parts separated by dots
        assertEquals(3, token.split("\\.").length);
    }

    // Test 2: Verify a valid JWT
    @Test
    void verifyToken_ShouldReturnCorrectClaims_WhenTokenIsValid() {

        User user = new User();
        user.setEmail("borrower@gmail.com");
        user.setRole(Role.BORROWER);

        String token = jwtUtil.generateToken(user);

        DecodedJWT decodedJWT = jwtUtil.verifyToken(token);

        assertEquals(
                "borrower@gmail.com",
                decodedJWT.getSubject()
        );

        assertEquals(
                "BORROWER",
                decodedJWT.getClaim("role").asString()
        );

        assertNotNull(decodedJWT.getIssuedAt());
        assertNotNull(decodedJWT.getExpiresAt());
    }

    // Test 3: Reject JWT signed with a different secret
    @Test
    void verifyToken_ShouldThrowException_WhenSignatureIsInvalid() {

        String invalidToken = JWT.create()
                .withSubject("borrower@gmail.com")
                .withClaim("role", "BORROWER")
                .sign(Algorithm.HMAC256("different-secret-key"));

        assertThrows(
                JWTVerificationException.class,
                () -> jwtUtil.verifyToken(invalidToken)
        );
    }
}
