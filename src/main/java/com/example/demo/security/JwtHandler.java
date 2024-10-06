package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import com.example.demo.exception.UnauthorizedException;
import reactor.core.publisher.Mono;

import java.util.Base64;

public class JwtHandler {

    private final String secret;

    public JwtHandler(String secret) {
        this.secret = secret;
    }

    public Mono<VerificationResult> check(String accessToken) {
        VerificationResult verificationResult = new VerificationResult(getClaimsFromToken(accessToken), accessToken);

        return Mono.just(verificationResult)
                .onErrorResume(e -> Mono.error(new UnauthorizedException(e.getMessage())));
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
                .parseClaimsJws(token)
                .getBody();
    }

    public static class VerificationResult {
        public Claims claims;
        public String token;

        public VerificationResult(Claims claims, String token) {
            this.claims = claims;
            this.token = token;
        }
    }
}
