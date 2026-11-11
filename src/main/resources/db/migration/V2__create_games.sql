CREATE TABLE games(
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,                    -- Game title
    description TEXT,                               -- Game Description
    price NUMERIC(12, 2) NOT NULL,                  -- Game Price
    currency VARCHAR(3) NOT NULL DEFAULT 'EUR',     -- Currency price 
    size_mb INTEGER NOT NULL,                       -- Game Size
    published BOOLEAN NOT NULL DEFAULT FALSE,       -- Game published state
    version BIGINT NOT NULL DEFAULT 0,              -- Gake Version


    CONSTRAINT chk_games_price -- We can have free games but not negative priced games
        CHECK (price >= 0),

    CONSTRAINT chk_games_size -- Size of a game can't be 0
        CHECK (size_mb > 0)
);