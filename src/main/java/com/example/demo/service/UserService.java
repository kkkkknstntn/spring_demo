package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface UserService {
    Mono<UserDTO> getById(Long id);
    Mono<UserDTO> getByUsername(String username);
    Mono<UserDTO> getByVkId(Long vkId);
    Flux<UserDTO> getList();
    Mono<UserDTO> create(UserDTO userDTO);
    Mono<UserDTO> createVk(UserDTO userDTO);
    Mono<UserDTO> update(Long id, UserDTO userDTO);
    Mono<Void> delete(Long id);
}
