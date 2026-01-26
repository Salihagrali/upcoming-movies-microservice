CREATE TABLE IF NOT EXISTS favorites (
    -- Surrogate primary key for the favorites entry itself
                                         id BIGSERIAL PRIMARY KEY,
    -- User identifier (from Keycloak/JWT subject)
                                         user_id VARCHAR(255) NOT NULL,
    -- Movie data mapped from my Java Record
    movie_id INTEGER NOT NULL,
    title VARCHAR(255),
    original_title VARCHAR(255),
    overview TEXT,                        -- TEXT type allows for long descriptions
    release_date DATE,
    poster_path VARCHAR(255),
    backdrop_path VARCHAR(255),
    popularity DOUBLE PRECISION,          -- Maps to Java 'double'
    vote_average DOUBLE PRECISION,        -- Maps to Java 'double'
    vote_count INTEGER,
    genre_ids INTEGER[],                  -- Maps to Java 'List<Integer>' using Postgres Arrays
    original_language VARCHAR(50),
    adult BOOLEAN,
    video BOOLEAN,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- A user cannot favorite the same movie_id twice
    CONSTRAINT uk_user_movie UNIQUE (user_id, movie_id)
    );