CREATE TABLE IF NOT EXISTS batteries (
                                         id BIGSERIAL PRIMARY KEY,
                                         temperature INT NOT NULL DEFAULT 20,
                                         last_updated TIMESTAMP DEFAULT NOW(),
    room_id BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
    );