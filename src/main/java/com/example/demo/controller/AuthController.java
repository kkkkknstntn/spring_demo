package com.example.demo.controller;

import com.example.demo.security.oauth.OAuthService;
import lombok.RequiredArgsConstructor;
import com.example.demo.dto.AuthRequestDTO;
import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.security.CustomPrincipal;
import com.example.demo.security.SecurityService;
import com.example.demo.service.UserServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final SecurityService securityService;
    private final UserServiceImpl userService;
    private final OAuthService oAuthService;

    @PostMapping("/login")
    public Mono<AuthResponseDTO> login(@RequestBody AuthRequestDTO dto) {
        return securityService.login(dto);
    }

    @PostMapping("/refresh")
    public Mono<AuthResponseDTO> refresh(@RequestBody String refreshToken) {
        return securityService.refresh(refreshToken);
    }

    @GetMapping("/oauth2/vk")
    public Mono<Void> oauth2(@RegisteredOAuth2AuthorizedClient("vk") OAuth2AuthorizedClient authorizedClient) {
        return null;
    }

    @GetMapping("/login/oauth2/code/vk")
    public Mono<AuthResponseDTO> handleRedirect(@RequestParam("code") String code) {
        return oAuthService.authenticate(code);
    }

    @GetMapping("/info")
    public Mono<UserResponseDTO> getUserInfo(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return userService.getById(customPrincipal.getId());
    }
}
