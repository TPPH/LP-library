-- PostgreSQL-compatible table creation
CREATE TABLE favorites (
                           id SERIAL PRIMARY KEY,
                           user_id VARCHAR(255) NOT NULL,  -- Auth0 user ID
                           song_id BIGINT NOT NULL         -- Reference to SongService
);

-- Sample inserts with fake Auth0 IDs
INSERT INTO favorites (user_id, song_id) VALUES ('google-oauth2|102767745188238104433', 1);
INSERT INTO favorites (user_id, song_id) VALUES ('google-oauth2|102767745188238104433', 2);

-- Optional: view inserted data
SELECT * FROM favorites;
