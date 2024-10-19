package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByUsername(String username);
    Mono<User> findByVkId(Long vkId);
    @Query("SELECT * FROM users ORDER BY (wins * 1.0 / CASE WHEN loses = 0 THEN 1 ELSE loses END) DESC")
    Flux<User> findAllSortedByWinLossRatio();
    @Query("SELECT * FROM users ORDER BY (wins * 1.0 / CASE WHEN loses = 0 THEN 1 ELSE loses END) DESC LIMIT :size OFFSET :offset")
    Flux<User> findAllSortedByWinLossRatio(@Param("size") int size, @Param("offset") int offset);
    @Query("SELECT * FROM users ORDER BY wins DESC")
    Flux<User> findAllSortedByWins();
    @Query("SELECT * FROM users ORDER BY wins DESC LIMIT :size OFFSET :offset")
    Flux<User> findAllSortedByWins(@Param("size") int size, @Param("offset") int offset);
}
