CREATE TABLE IF NOT EXISTS blinds (
    id BIGSERIAL PRIMARY KEY,
    position INT NOT NULL CHECK (position >= 0 AND position <= 100),
    last_updated TIMESTAMP,
    room_id BIGINT NOT NULL UNIQUE REFERENCES rooms(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);
