package com.example.demo.service;

import com.example.demo.dto.GuessRequestDTO;
import com.example.demo.dto.GuessResponseDTO;
import com.example.demo.entity.Game;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface GameService {
    Flux<Game> getAllGames();
    Mono<Game> getGameById(Long id);
    Mono<GuessResponseDTO> createGame(Authentication authentication);
    Mono<GuessResponseDTO> update(GuessRequestDTO dto, Authentication authentication);
    Mono<Void> deleteGame(Long id);

}
