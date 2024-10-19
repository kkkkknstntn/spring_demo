package com.example.demo.service;

import com.example.demo.dto.GuessRequestDTO;
import com.example.demo.dto.GuessResponseDTO;
import com.example.demo.entity.Game;
import com.example.demo.entity.User;
import com.example.demo.enums.GameStatus;
import com.example.demo.enums.LetterStatus;
import com.example.demo.exception.ApiException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final Random random = new Random();
    private final List<String> words = loadWordsFromFile();

    @Override
    public Flux<Game> getAllGames() {
        return gameRepository.findAll()
                .flatMap(this::populateUser);
    }

    @Override
    public Mono<Game> getGameById(Long id) {
        return gameRepository.findById(id)
                .flatMap(this::populateUser);
    }

    @Override
    public Mono<GuessResponseDTO> createGame(Authentication authentication) {
        String guessedWord = getRandomWord();

        Game newGame = buildNewGame(guessedWord);

        return userService.getAuthenticatedUser(authentication)
                .flatMap(user -> saveGameWithUserId(newGame, user.getId()))
                .switchIfEmpty(gameRepository.save(newGame)) // Save game without userId if no user found
                .map(this::mapToGuessResponseDTO);
    }

    @Override
    public Mono<Void> deleteGame(Long id) {
        return gameRepository.deleteById(id);
    }

    @Override
    public Mono<GuessResponseDTO> update(GuessRequestDTO dto, Authentication authentication) {
        return getGameById(dto.getGameId())
                .flatMap(game -> processGuess(dto.getGuessedWord().toUpperCase(), game, authentication))
                .switchIfEmpty(Mono.error(new ApiException("Invalid gameId", "INVALID_GAME_ID")));
    }

    private List<String> loadWordsFromFile() {
        try (InputStream inputStream = new ClassPathResource("words.json").getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load words from file", e);
        }
    }

    private String getRandomWord() {
        return words.get(random.nextInt(words.size()));
    }

    private Game buildNewGame(String guessedWord) {
        return Game.builder()
                .currentTry(0)
                .gameStatus(GameStatus.IN_PROGRESS)
                .word(guessedWord)
                .build();
    }

    private Mono<Game> saveGameWithUserId(Game game, Long userId) {
        game.setUserId(userId);
        return gameRepository.save(game);
    }

    private GuessResponseDTO mapToGuessResponseDTO(Game game) {
        return GuessResponseDTO.builder()
                .gameId(game.getId())
                .guessedWord(game.getWord())
                .currentTry(game.getCurrentTry())
                .gameStatus(game.getGameStatus())
                .letterStatuses(List.of())
                .build();
    }

    private Mono<GuessResponseDTO> processGuess(String guessedWord, Game game, Authentication authentication) {
        validateGameStatus(game);
        validateGuessedWord(guessedWord);

        updateCurrentTry(game);

        List<LetterStatus> letterStatuses = evaluateGuess(game.getWord(), guessedWord);

        updateGameStatus(game, letterStatuses);

        return updateUserPointsIfAuthenticated(game, authentication)
                .then(gameRepository.save(game)
                        .switchIfEmpty(Mono.error(new ApiException("Invalid gameId", "INVALID_GAME_ID"))))
                .then(Mono.just(buildResponse(guessedWord, game, letterStatuses)));
    }

    private void validateGameStatus(Game game) {
        if (game.getGameStatus() != GameStatus.IN_PROGRESS) {
            throw new ApiException("Game has already ended", "MAXIMUM_ATTEMPTS");
        }
    }

    private void validateGuessedWord(String guessedWord) {
        if (!words.contains(guessedWord) || guessedWord.length() != 6) {
            throw new ApiException("This word is incorrect", "INVALID_WORD");
        }
    }

    private void updateCurrentTry(Game game) {
        if (game.getCurrentTry() >= 6) {
            throw new ApiException("Current try exceeds maximum allowed attempts.", "MAXIMUM_ATTEMPTS");
        }
        game.setCurrentTry(game.getCurrentTry() + 1);
    }

    private List<LetterStatus> evaluateGuess(String word, String guessedWord) {
        List<LetterStatus> letterStatuses = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            char character = word.charAt(i);
            char guessedCharacter = guessedWord.charAt(i);

            if (character == guessedCharacter) {
                letterStatuses.add(LetterStatus.CORRECT);
            } else if (word.indexOf(guessedCharacter) != -1) {
                letterStatuses.add(LetterStatus.MISPLACED);
            } else {
                letterStatuses.add(LetterStatus.NOT_PRESENT);
            }
        }

        return letterStatuses;
    }

    private void updateGameStatus(Game game, List<LetterStatus> letterStatuses) {
        if (letterStatuses.stream().allMatch(status -> status == LetterStatus.CORRECT)) {
            game.setGameStatus(GameStatus.WIN);
        } else if (game.getCurrentTry() >= 6) {
            game.setGameStatus(GameStatus.LOSE);
        } else {
            game.setGameStatus(GameStatus.IN_PROGRESS);
        }
    }

    private Mono<User> updateUserPointsIfAuthenticated(Game game, Authentication authentication) {
        return userService.getAuthenticatedUser(authentication)
                .flatMap(user -> updateUserWinsOrLoses(userMapper.responseMap(user), game))
                .onErrorResume(e -> Mono.empty());
    }

    private Mono<User> updateUserWinsOrLoses(User user, Game game) {
        if (game.getGameStatus() == GameStatus.WIN) {
            user.setWins(user.getWins() + 1);
        } else if (game.getGameStatus() == GameStatus.LOSE) {
            user.setLoses(user.getLoses() + 1);
        }

        return userRepository.save(user);
    }

    private GuessResponseDTO buildResponse(String guessedWord, Game game, List<LetterStatus> letterStatuses) {
        return GuessResponseDTO.builder()
                .guessedWord(guessedWord)
                .currentTry(game.getCurrentTry())
                .gameStatus(game.getGameStatus())
                .letterStatuses(letterStatuses)
                .build();
    }


    private Mono<Game> populateUser(Game game) {
        if (game.getUserId() == null) return Mono.just(game);

        return userService.getById(game.getUserId())
                .doOnNext(userDTO -> game.setUser(userMapper.responseMap(userDTO)))
                .thenReturn(game);
    }
}
