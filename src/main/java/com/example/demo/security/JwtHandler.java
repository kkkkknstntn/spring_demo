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

    public Mono<VerificationResult> check(String token) {
        VerificationResult verificationResult = new VerificationResult(getClaimsFromToken(token), token);

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
        public final Claims claims;
        public final String token;

        public VerificationResult(Claims claims, String token) {
            this.claims = claims;
            this.token = token;
        }
    }
}
