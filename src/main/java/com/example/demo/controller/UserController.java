package com.example.demo.controller;

import com.example.demo.dto.UserRequestDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<UserResponseDTO> getAllUsers() {
        return userService.getList();
    }

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя с указанным ID.")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @Operation(summary = "Получить пользователей, отсортированных по победам",
            description = "Возвращает список пользователей, отсортированных по количеству побед.")
    @GetMapping(value = "/sortedByWins", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<UserResponseDTO> getUsersSortedByWins(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return userService.getUsersSortedByWins(page, size);
    }

    @Operation(summary = "Получить пользователей, отсортированных по соотношению побед и поражений",
            description = "Возвращает список пользователей, отсортированных по соотношению побед и поражений.")
    @GetMapping(value = "/sortedByWinLossRatio", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<UserResponseDTO> getUsersSortedByWinLossRatio(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return userService.getUsersSortedByWinLossRatio(page, size);
    }

    @Operation(summary = "Найти позицию пользователя по количеству побед",
            description = "Возвращает позицию аутентифицированного пользователя по количеству побед.")
    @GetMapping(value = "/getByWins", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponseDTO> findUserPositionByWins(Authentication authentication) {
        return userService.findUserPositionByWins(authentication);
    }

    @Operation(summary = "Найти позицию пользователя по соотношению побед и поражений",
            description = "Возвращает позицию аутентифицированного пользователя по соотношению побед и поражений.")
    @GetMapping(value = "/getByWinLossRatio", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponseDTO> geByWinLossRatio(Authentication authentication) {
        return userService.findUserPositionByWinLossRatio(authentication);
    }

    @Operation(summary = "Создать нового пользователя", description = "Создает нового пользователя на основе переданных данных.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDTO> createUser(@RequestBody UserRequestDTO userDTO) {
        return userService.create(userDTO);
    }

    @Operation(summary = "Обновить данные пользователя", description = "Обновляет данные пользователя с указанным ID.")
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userDTO) {
        return userService.update(id, userDTO);
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя с указанным ID.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.delete(id);
    }

    @Operation(summary = "Получить информацию о текущем пользователе",
            description = "Возвращает информацию о аутентифицированном пользователе.")
    @GetMapping("/info")
    public Mono<UserResponseDTO> getUserInfo(Authentication authentication) {
        return userService.getAuthenticatedUser(authentication);
    }
}
