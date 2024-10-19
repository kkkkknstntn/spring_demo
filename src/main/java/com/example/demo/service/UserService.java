package com.example.demo.service;

import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface UserService {
    Mono<UserResponseDTO> getById(Long id);
    Mono<UserResponseDTO> getByUsername(String username);
    Flux<UserResponseDTO> getList();
    Mono<UserResponseDTO> create(UserRequestDTO userDTO);
    Mono<UserResponseDTO> createVk(UserRequestDTO userDTO, Long vkId);
    Mono<UserResponseDTO> update(Long id, UserRequestDTO userDTO);
    Mono<Void> delete(Long id);
    Flux<UserResponseDTO> getUsersSortedByWins(int page, int size);
    Mono<UserResponseDTO> findUserPositionByWinLossRatio(Authentication authentication);
    Mono<UserResponseDTO> findUserPositionByWins(Authentication authentication);
    Flux<UserResponseDTO> getUsersSortedByWinLossRatio(int page, int size);
    Mono<UserResponseDTO> getAuthenticatedUser(Authentication authentication);
}
