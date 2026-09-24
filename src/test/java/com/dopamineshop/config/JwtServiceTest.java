package com.dopamineshop.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService service = new JwtService(
            "test-secret-at-least-thirty-two-characters-long", 15, 30);
    private final User user = (User) User.withUsername("buyer@example.com")
            .password("unused").roles("USER").build();

    @Test
    void accessAndRefreshTokensAreNotInterchangeable() {
        String accessToken = service.generateAccessToken(user);
        String refreshToken = service.generateRefreshToken(user);

        assertThat(service.isAccessTokenValid(accessToken, user)).isTrue();
        assertThat(service.isRefreshTokenValid(refreshToken, user)).isTrue();
        assertThat(service.isAccessTokenValid(refreshToken, user)).isFalse();
        assertThat(service.isRefreshTokenValid(accessToken, user)).isFalse();
    }
}
