package com.example.demo.dto;

import com.example.demo.enums.GameStatus;
import com.example.demo.enums.LetterStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GuessResponseDTO {
    private Long gameId;
    private String guessedWord;
    private Integer currentTry;
    private GameStatus gameStatus;
    private List<LetterStatus> letterStatuses;
}
