package com.example.demo.controller;

import com.example.demo.dto.GuessRequestDTO;
import com.example.demo.dto.GuessResponseDTO;
import com.example.demo.entity.Game;
import com.example.demo.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @Operation(summary = "Получить все игры", description = "Получить список всех доступных игр.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Game> getAllGames() {
        return gameService.getAllGames();
    }

    @Operation(summary = "Получить игру по ID", description = "Получить игру по ее уникальному идентификатору.")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Game> getGameById(@PathVariable Long id) {
        return gameService.getGameById(id);
    }

    @Operation(summary = "Создать новую игру", description = "Создать новый экземпляр игры.")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GuessResponseDTO> createGame(Authentication authentication) {
        return gameService.createGame(authentication);
    }

    @Operation(summary = "Обновить угадывание пользователя", description = "Обновить угадывание пользователя для текущей игры.")
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GuessResponseDTO> updateUser(@RequestBody GuessRequestDTO guessDTO, Authentication authentication) {
        return gameService.update(guessDTO, authentication);
    }

    @Operation(summary = "Удалить игру", description = "Удалить игру по ее уникальному идентификатору.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return gameService.deleteGame(id);
    }
}
