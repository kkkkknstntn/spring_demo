package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.enums.Provider;
import com.example.demo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public Flux<UserDTO> getList() {
        return userRepository.findAll()
                .map(userMapper::map);
    }

    @Override
    public Mono<UserDTO> create(UserDTO userDTO) {
        return userRepository.save(
                build(userDTO, Provider.PASSWORD )
        ).doOnSuccess(u -> log.info("IN create - user: {} created", u)).map(userMapper::map);
    }

    public Mono<UserDTO> createVk(UserDTO userDTO) {
        return userRepository.save(
                build(userDTO, Provider.VK )
        ).doOnSuccess(u -> log.info("IN createVk - user: {} created", u)).map(userMapper::map);
    }

    private User build (UserDTO userDTO, Provider provider){
        User user = userMapper.map(userDTO);
        return  user.toBuilder()
                .password(provider == Provider.PASSWORD ? passwordEncoder.encode(user.getPassword()) : null)
                .role(UserRole.USER)
                .enabled(true)
                .provider(provider)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public Mono<UserDTO> update(Long id, UserDTO userDTO) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    User updatedUser = existingUser.toBuilder()
                            .username(userDTO.getUsername() != null ? userDTO.getUsername() : existingUser.getUsername())
                            .password(userDTO.getPassword() != null ? userDTO.getPassword() : existingUser.getPassword())
                            .firstName(userDTO.getFirstName() != null ? userDTO.getFirstName() : existingUser.getFirstName())
                            .lastName(userDTO.getLastName() != null ? userDTO.getLastName() : existingUser.getLastName())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return userRepository.save(updatedUser);
                })
                .map(userMapper::map);
    }

    @Override
    public Mono<UserDTO> getById(Long id) {
        return userRepository
                .findById(id)
                .map(userMapper::map);
    }

    @Override
    public Mono<UserDTO> getByVkId(Long vkId){
        return userRepository
                .findByVkId(vkId)
                .map(userMapper::map);
    }


    @Override
    public Mono<UserDTO> getByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .map(userMapper::map);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return userRepository.deleteById(id);
    }

}
