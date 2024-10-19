package com.example.demo.repository;

import com.example.demo.entity.Game;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface GameRepository extends R2dbcRepository<Game, Long> {
}
