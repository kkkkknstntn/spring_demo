package com.example.demo.controller;

import com.example.demo.dto.RefreshDTO;
import com.example.demo.security.oauth.OAuthService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import com.example.demo.dto.AuthRequestDTO;
import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final SecurityService securityService;
    private final OAuthService oAuthService;

    @Operation(summary = "Вход пользователя", description = "Авторизация пользователя с использованием учетных данных.")
    @PostMapping("/login")
    public Mono<AuthResponseDTO> login(@RequestBody AuthRequestDTO dto) {
        return securityService.login(dto);
    }

    @Operation(summary = "Обновление токена", description = "Обновить токен доступа с использованием refresh-токена.")
    @PostMapping("/refresh")
    public Mono<AuthResponseDTO> refresh(@RequestBody RefreshDTO dto) {
        log.info(dto.getRefreshToken());
        return securityService.refresh(dto);
    }

    @Operation(summary = "OAuth2 авторизация через VK", description = "Обработка OAuth2 авторизации через ВКонтакте.")
    @GetMapping("/oauth2/vk")
    public Mono<Void> oauth2(@RegisteredOAuth2AuthorizedClient("vk") OAuth2AuthorizedClient authorizedClient) {
        return null;
    }


    @Hidden
    @GetMapping("/login/oauth2/code/vk")
    public Mono<AuthResponseDTO> handleRedirect(@RequestParam("code") String code) {
        return oAuthService.authenticate(code);
    }
}
