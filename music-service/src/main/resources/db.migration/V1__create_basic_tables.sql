-- =========================================
-- Initial schema for music service
-- Based on original table definitions
-- =========================================

-- 1. Albums
CREATE TABLE IF NOT EXISTS albums (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(255),
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6)
);

-- 2. Bands
CREATE TABLE IF NOT EXISTS bands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6)
);

-- 3. Genres
CREATE TABLE IF NOT EXISTS genres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6)
);

-- 4. Musicians
CREATE TABLE IF NOT EXISTS musicians (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    bio VARCHAR(255),
    birth_date DATE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6)
);

-- 5. Singles
CREATE TABLE IF NOT EXISTS singles (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(255),
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6)
);

-- 6. Tracks
CREATE TABLE IF NOT EXISTS tracks (
    id BIGSERIAL PRIMARY KEY,
    trackpath VARCHAR(255),
    duration INT,
    track_number SMALLINT,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6)
);

-- =========================================
-- Junction tables (many-to-many relationships)
-- =========================================

-- 7. Band ↔ Genre (many-to-many)
CREATE TABLE IF NOT EXISTS band_genre (
    band_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (band_id, genre_id),
    CONSTRAINT fk_band_genre_band FOREIGN KEY (band_id) REFERENCES bands(id) ON DELETE CASCADE,
    CONSTRAINT fk_band_genre_genre FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

-- 8. Track ↔ Album (many-to-many)
CREATE TABLE IF NOT EXISTS track_album (
    track_id BIGINT NOT NULL,
    album_id BIGINT NOT NULL,
    PRIMARY KEY (track_id, album_id),
    CONSTRAINT fk_track_album_track FOREIGN KEY (track_id) REFERENCES tracks(id) ON DELETE CASCADE,
    CONSTRAINT fk_track_album_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE
);

-- 9. Track ↔ Genre (many-to-many)
CREATE TABLE IF NOT EXISTS track_genre (
    track_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (track_id, genre_id),
    CONSTRAINT fk_track_genre_track FOREIGN KEY (track_id) REFERENCES tracks(id) ON DELETE CASCADE,
    CONSTRAINT fk_track_genre_genre FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

-- 10. Track ↔ Single (many-to-many)
CREATE TABLE IF NOT EXISTS track_single (
    track_id BIGINT NOT NULL,
    single_id BIGINT NOT NULL,
    PRIMARY KEY (track_id, single_id),
    CONSTRAINT fk_track_single_track FOREIGN KEY (track_id) REFERENCES tracks(id) ON DELETE CASCADE,
    CONSTRAINT fk_track_single_single FOREIGN KEY (single_id) REFERENCES singles(id) ON DELETE CASCADE
);

-- =========================================
-- Indexes for performance
-- =========================================
CREATE INDEX IF NOT EXISTS idx_album_title ON albums(title);
CREATE INDEX IF NOT EXISTS idx_band_name ON bands(name);
CREATE INDEX IF NOT EXISTS idx_genre_name ON genres(name);
CREATE INDEX IF NOT EXISTS idx_musician_name ON musicians(name);
CREATE INDEX IF NOT EXISTS idx_single_title ON singles(title);
CREATE INDEX IF NOT EXISTS idx_track_trackpath ON tracks(trackpath);
CREATE INDEX IF NOT EXISTS idx_track_duration ON tracks(duration);

-- Indexes on foreign key columns
CREATE INDEX IF NOT EXISTS idx_band_genre_band ON band_genre(band_id);
CREATE INDEX IF NOT EXISTS idx_band_genre_genre ON band_genre(genre_id);
CREATE INDEX IF NOT EXISTS idx_track_album_track ON track_album(track_id);
CREATE INDEX IF NOT EXISTS idx_track_album_album ON track_album(album_id);
CREATE INDEX IF NOT EXISTS idx_track_genre_track ON track_genre(track_id);
CREATE INDEX IF NOT EXISTS idx_track_genre_genre ON track_genre(genre_id);
CREATE INDEX IF NOT EXISTS idx_track_single_track ON track_single(track_id);
CREATE INDEX IF NOT EXISTS idx_track_single_single ON track_single(single_id);