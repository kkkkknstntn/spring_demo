CREATE TABLE games (
    id SERIAL PRIMARY KEY,
    word VARCHAR(255) NOT NULL,
    user_id BIGINT NULL,
    game_status VARCHAR(50) NOT NULL,
    current_try INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);